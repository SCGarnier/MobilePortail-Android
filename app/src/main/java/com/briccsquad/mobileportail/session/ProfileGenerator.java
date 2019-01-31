package com.briccsquad.mobileportail.session;

import android.content.Context;
import android.support.annotation.NonNull;

import com.briccsquad.mobileportail.Utils;
import com.noveogroup.android.log.Logger;
import com.noveogroup.android.log.LoggerManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;

public class ProfileGenerator {
    private static final Logger logger = LoggerManager.getLogger(ProfileGenerator.class);
    private final StreamProvider streamProvider;

    public ProfileGenerator(@NonNull StreamProvider sp) {
        streamProvider = sp;
    }

    public boolean generateProfile(ProgressUpdater cb) {
        HashMap<PortalPage, String> pageData = new HashMap<>();
        if(cb != null) cb.setMax(PortalPage.values().length - 1);
        int progress = 0;
        for (PortalPage p : PortalPage.values()) {
            try (InputStream inputStream = streamProvider.fetchStream(p.getServerPath())) {
                byte[] txtBuf = Utils.readStream(inputStream);
                String htmlContent = new String(txtBuf, StandardCharsets.UTF_8);
                pageData.put(p, htmlContent);
                if(cb != null) cb.update(++progress);
            } catch (IOException e) {
                logger.e("Couldn't fully read HTML from provider", e);
                return false;
            }
        }

        PageParser pageParser = new PageParser(pageData);
        PortalUser.setCurrent(pageParser.createProfile());

        Context ctx = streamProvider.getContext();
        List<String> arr = PortalUser.getCurrent().getSummaryLinks();
        for (int i = 0, l = arr.size(); i < l; i++) {
            String link = arr.get(i);
            String summaryFilename = streamProvider.getLoginCredentials().toString() +
                    "_" + i + ".pdf";

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                inputStream = streamProvider.fetchStream(link);
                outputStream = ctx.openFileOutput(summaryFilename, Context.MODE_PRIVATE);
                Utils.pipeStream(inputStream, outputStream);
            } catch (IOException e) {
                logger.e("Couldn't transfer summary file to internal filesystem", e);
                return false;
            } finally {
                // Why are there so many exceptions in Java??
                try {
                    if (inputStream != null) inputStream.close();
                    if (outputStream != null) outputStream.close();
                } catch (IOException e) {
                    logger.e(e);
                }
            }

            PortalUser.getCurrent().setSummaryFile(i, summaryFilename);
        }

        return true;
    }

    public interface ProgressUpdater {
        void update(int p);
        void setMax(int m);
    }
}

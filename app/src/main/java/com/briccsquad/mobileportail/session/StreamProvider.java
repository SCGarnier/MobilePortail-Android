package com.briccsquad.mobileportail.session;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.briccsquad.mobileportail.Utils;
import com.koushikdutta.ion.Ion;
import com.noveogroup.android.log.Logger;
import com.noveogroup.android.log.LoggerManager;

import org.jsoup.Jsoup;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class StreamProvider {

    private static final Logger logger = LoggerManager.getLogger(StreamProvider.class);

    /**
     * Stores the map of POST parameters to send to the portail server
     * to fetch pages about a specific user.
     */
    private final Map<String, List<String>> paramDict = new HashMap<>();

    private final LoginCredentials loginCredentials;
    private final Context providerContext;

    public StreamProvider(@NonNull Context context, @NonNull LoginCredentials lc) {
        loginCredentials = lc;
        providerContext = context;

        // Fill the map with constant parameters the server checks for
        paramDict.put("__VIEWSTATE",
                Collections.singletonList("/wEPDwULLTE1OTEzODk5NDRkZHeWIDblnhXfWVgudGFcvrqUrsa8oUjUBNqAwiyC5bQZ"));
        paramDict.put("__VIEWSTATEGENERATOR",
                Collections.singletonList("3738FB10"));
        paramDict.put("__EVENTVALIDATION",
                Collections.singletonList("/wEdAAS/EmKn67wLWprMxhcvNZYzNV4T48EJ76cvY4cUAzjjnR0O4L6f0p" +
                        "zi6oGvFfSn1SztaUkzhlzahAalhIckp3krG4fQXm17dVV5HUDhFMY" +
                        "g3hg06HAD0C/01YYOsiBthV8="));
        paramDict.put("Blogin", Collections.singletonList("Entrer"));

        // Add credentials to the mix
        paramDict.put("Tlogin", Collections.singletonList(lc.getUsername()));
        paramDict.put("Tpassword", Collections.singletonList(lc.getPassword()));
    }

    boolean hasValidCrendentials() {
        try (InputStream inputStream = fetchStream(PortalPage.GRADES_TABLE.getServerPath())) {
            String res = new String(Utils.readStream(inputStream), StandardCharsets.UTF_8);
            return !Jsoup.parse(res).title().contains("portail");
        } catch (IOException e) {
            logger.e(e);
            return false;
        }
    }

    InputStream fetchStream(@NonNull String reqPath) {

        // Request with page as GET parameter
        String fullUrl = "https://apps.cscmonavenir.ca/PortailEleves/index.aspx?ReturnUrl=" +
                Uri.encode(reqPath);

        try {
            return Ion.with(providerContext)
                    .load(fullUrl)
                    .setBodyParameters(paramDict)
                    .asInputStream()
                    .get();
        } catch (ExecutionException | InterruptedException e) {
            logger.e("Failed to fetch path: " + reqPath, e);
            return null;
        }
    }

    LoginCredentials getLoginCredentials() {
        return loginCredentials;
    }

    Context getContext() {
        return providerContext;
    }
}

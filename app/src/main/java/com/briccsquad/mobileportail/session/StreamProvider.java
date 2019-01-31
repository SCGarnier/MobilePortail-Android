package com.briccsquad.mobileportail.session;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.briccsquad.mobileportail.Utils;
import com.noveogroup.android.log.Logger;
import com.noveogroup.android.log.LoggerManager;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class StreamProvider {

    private static final Logger logger = LoggerManager.getLogger(StreamProvider.class);

    /**
     * Stores the map of POST parameters to send to the portail server
     * to fetch pages about a specific user.
     */
    private final Map<String, String> paramDict = new HashMap<>();

    private final LoginCredentials loginCredentials;
    private final HttpClient httpClient = new DefaultHttpClient();
    private final Context appActivity;

    public StreamProvider(@NonNull Context activity, @NonNull LoginCredentials lc) {
        loginCredentials = lc;
        appActivity = activity;

        // Fill the map with constant parameters the server checks for
        paramDict.put("__VIEWSTATE", "/wEPDwULLTE1OTEzODk5NDRkZHeWIDblnhXfWVgudGFcvrqUrsa8oUjUBNqAwiyC5bQZ");
        paramDict.put("__VIEWSTATEGENERATOR", "3738FB10");
        paramDict.put("__EVENTVALIDATION",
                "/wEdAAS/EmKn67wLWprMxhcvNZYzNV4T48EJ76cvY4cUAzjjnR0O4L6f0pzi6oGvFfSn1SztaUkzhlzahAalhIckp3krG4fQXm17dVV5HUDhFMYg3hg06HAD0C/01YYOsiBthV8=");
        paramDict.put("Blogin", "Entrer");

        // Add credentials to the mix
        paramDict.put("Tlogin", lc.getUsername());
        paramDict.put("Tpassword", lc.getPassword());
    }

    public boolean hasValidCrendentials() {
        try (InputStream inputStream = fetchStream(PortalPage.GRADES_TABLE.getServerPath())) {
            String res = new String(Utils.readStream(inputStream), StandardCharsets.UTF_8);
            return !Jsoup.parse(res).title().contains("portail");
        } catch (IOException e) {
            logger.e(e);
            return false;
        }
    }

    public InputStream fetchStream(@NonNull String reqPath) {
        String processedPath = Uri.encode(reqPath);
        String fullUrl = "https://apps.cscmonavenir.ca/PortailEleves/index.aspx?ReturnUrl="
                + processedPath;

        HttpPost httpPost = new HttpPost(fullUrl);

        ArrayList<NameValuePair> modParams = new ArrayList<>();
        for (Map.Entry<String, String> entry : paramDict.entrySet()) {
            modParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }

        try {
            httpPost.setEntity(new UrlEncodedFormEntity(modParams));
            HttpResponse response = httpClient.execute(httpPost);
            return response.getEntity().getContent();
        } catch (IOException e) {
            logger.e("Failed to fetch path: " + reqPath, e);
            return null;
        }
    }

    public LoginCredentials getLoginCredentials() {
        return loginCredentials;
    }

    public Context getContext() {
        return appActivity;
    }
}

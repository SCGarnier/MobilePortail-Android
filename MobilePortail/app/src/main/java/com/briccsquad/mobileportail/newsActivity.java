package com.briccsquad.mobileportail;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;
import android.view.View;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class NewsActivity extends AppCompatActivity {

    public static void testBus() {
        // Get bus availability
        Document siteGarnier = null;
        try {
            siteGarnier = Jsoup.connect(toString(R.string.urlSCG_site))
                    .get();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Elements elemList = siteGarnier.select("li.list-group-item");

        String ecoleOuverte = elemList.get(0).text();
        String transportG = elemList.get(1).text();

        int schoolColour;
        int busColour;

        if (ecoleOuverte == "École ouverte") {
            schoolColour = R.color.good;
        } else {
            schoolColour = R.color.bad;
        }
        if (ecoleOuverte == "Transport normal") {
            busColour = R.color.good;
        } else {
            if (ecoleOuverte == "Transport annulé") {
                busColour = R.color.bad;
            } else {
                busColour = R.color.bad1;
            }
        }
        ((TextView) findViewById(R.id.school_stat)).setText(ecoleOuverte).setBackground(schoolColour);
        ((TextView) findViewById(R.id.bus_stat)).setText(transportG).setBackground(busColour);

}
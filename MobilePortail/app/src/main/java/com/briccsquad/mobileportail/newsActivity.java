package com.briccsquad.mobileportail;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity {
    private static final String STR_OPEN_SCHOOL = "École ouverte";
    private static final String STR_NORM_TRANSPORT = "Transport normal";
    private static final String STR_BAD_TRANSPORT = "Transport annulé";
    private static final String CSC_WEBSITE = "https://esscg.cscmonavenir.ca/";

    private static class InfoFetcher extends AsyncTask<String, Void, Document[]> {

        private final AppCompatActivity app;

        InfoFetcher(AppCompatActivity a){
            this.app = a;
        }

        @Override
        protected Document[] doInBackground(String... strings) {
            List<Document> arr = new ArrayList<>();

            for(String s: strings){
                try {
                    Document doc = Jsoup.connect(s)
                            .timeout(3000)
                            .get();
                    arr.add(doc);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return arr.toArray(new Document[arr.size()]);
        }

        @Override
        protected void onPostExecute(Document[] list) {
            Document siteGarnier = list[0];
            Elements elemList = siteGarnier.select("li.list-group-item");

            String ecoleOuverte = elemList.get(0).text();
            String transportG = elemList.get(1).text();

            int schoolColour;
            int busColour;

            if (ecoleOuverte.equals(STR_OPEN_SCHOOL)) {
                schoolColour = R.color.good;
            } else {
                schoolColour = R.color.bad;
            }

            if (ecoleOuverte.equals(STR_NORM_TRANSPORT)) {
                busColour = R.color.good;
            } else {
                if (ecoleOuverte.equals(STR_BAD_TRANSPORT)) {
                    busColour = R.color.bad;
                } else {
                    busColour = R.color.bad1;
                }
            }

            // Setup school
            TextView tv = app.findViewById(R.id.school_stat);
            tv.setText(ecoleOuverte);
            tv.setBackgroundColor(ContextCompat.getColor(app.getApplicationContext(), schoolColour));

            // Setup bus
            tv = app.findViewById(R.id.bus_stat);
            tv.setText(transportG);
            tv.setBackgroundColor(ContextCompat.getColor(app.getApplicationContext(), busColour));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        // Get bus availability
        new InfoFetcher(this).execute(CSC_WEBSITE);

        findViewById(R.id.gotoSchButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}
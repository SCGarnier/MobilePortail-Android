package com.briccsquad.mobileportail;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.briccsquad.mobileportail.session.PortalUser;
import com.briccsquad.mobileportail.session.School;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity {

    // Strings found on district websites that indicate status
    private static final String STR_OPEN_SCHOOL = "École ouverte";
    private static final String STR_NORM_TRANSPORT = "Transport normal";
    private static final String STR_BAD_TRANSPORT = "Transport annulé";

    private static School currentSchool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        currentSchool = PortalUser.getCurrent().getSchool();

        // Get bus & school availability, then display it
        new InfoFetcher(this).execute(currentSchool.website);
    }

    private static class InfoFetcher extends AsyncTask<String, Void, Document[]> {

        private final WeakReference<AppCompatActivity> activityReference;

        InfoFetcher(AppCompatActivity appCompatActivity) {
            activityReference = new WeakReference<>(appCompatActivity);
        }

        @Override
        protected Document[] doInBackground(String... strings) {
            List<Document> arr = new ArrayList<>();

            for (String s : strings) {
                try {
                    Document doc = Jsoup.connect(s)
                            .timeout(3000)
                            .get();
                    arr.add(doc);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return arr.toArray(new Document[0]);
        }

        @Override
        protected void onPostExecute(Document[] list) {
            Document homePage = list[0];
            Elements elemList = homePage.select("li.list-group-item");

            String schoolStatus = elemList.get(0).text();
            String busStatus = elemList.get(1).text();

            int schoolColour = schoolStatus.equals(STR_OPEN_SCHOOL) ? R.color.good : R.color.bad;
            int busColour;

            if (busStatus.equals(STR_NORM_TRANSPORT)) {
                busColour = R.color.good;
            } else {
                busColour = busStatus.equals(STR_BAD_TRANSPORT) ? R.color.bad : R.color.bad1;
            }

            AppCompatActivity activity = activityReference.get();
            if (activity == null) {
                return;
            }

            LinearLayout newsPageHolder = activity.findViewById(R.id.news_page);

            // Edit style for school status
            TextView tv = activity.findViewById(R.id.school_stat);
            tv.setText(schoolStatus);
            tv.setBackgroundColor(
                    ContextCompat.getColor(activity.getApplicationContext(), schoolColour));

            // Edit style for bus status
            tv = activity.findViewById(R.id.bus_stat);
            tv.setText(busStatus);
            tv.setBackgroundColor(
                    ContextCompat.getColor(activity.getApplicationContext(), busColour));

            if (currentSchool == School.SCG) {
                Elements posts = homePage.select("#content .media-body");
                for (Element post : posts) {
                    View v = Utils.fromTemplate(activity, R.layout.template_news_container);

                    String newsStr = post.selectFirst("p").text();
                    ((TextView) v.findViewById(R.id.newsText)).setText(newsStr);

                    Element postTitleElem = post.selectFirst("a");

                    ((TextView) v.findViewById(R.id.newsArticleName)).setText(postTitleElem.text());
                    final Uri hrefLink = Uri.parse(postTitleElem.attr("href"));

                    // Let the user access the website for more information
                    v.findViewById(R.id.userMoreButton).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setData(hrefLink);
                            activityReference.get().startActivity(intent);
                        }
                    });

                    newsPageHolder.addView(v);
                }
            }

            // Stop the progress screen and show the results of our work
            activity.findViewById(R.id.progressBarNews).setVisibility(View.GONE);
            activity.findViewById(R.id.news_loaded_display).setVisibility(View.VISIBLE);
        }
    }
}
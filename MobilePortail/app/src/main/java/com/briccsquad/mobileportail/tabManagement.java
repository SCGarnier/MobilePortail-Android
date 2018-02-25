package com.briccsquad.mobileportail;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/*public class SchoolCheckGarnier {
    public static void checkGarnier

        boolean fromGarnier;
        Document infoEcole=Jsoup.connect(urlInfoEcole)
                .data(paramDict)
                .timeout(6000)
                .post();
        Element nomEcole=infoEcole.select("#LNomEcole");
            // Maybe add .text() to previous line
        if(nomEcole=="ÉSC Saint-Charles-Garnier - Whitby"){
            fromGarnier = true
        }
}

private class listActivityNav extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

            setContentView(R.layout.main);

        ArrayAdapter<String> listUtilities = new ArrayAdapter<String>(this,
                android.R.layout.activity_list_nav,
                new String[] {"Horaire", "Notes"})

        if(fromGarnier = true){
            listUtilities.add("Nouvelles")
        }
            ListView mlistView = (ListView) findViewById(R.id.idListView);
            mlistView.setAdapter(listUtilities);

            mlistView.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    // TODO : When clicked, opens the corresponding activity
                    String selectActivity = ((TextView) view).getText().toString();
                    if(selectActivity == "Horaire"){
                        startActivity("SummaryViewActivity")
                    } else {
                        if (selectActivity == "Notes") {
                            startActivity("ClassNotesActivity")
                        } else {
                            startActivity("newsActivity")
                        }
                    }
                }
            });


    }
}
*/
package com.briccsquad.mobileportail;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.barteksc.pdfviewer.PDFView;
import com.noveogroup.android.log.Logger;
import com.noveogroup.android.log.LoggerManager;

import java.io.FileNotFoundException;

public class SummaryViewActivity extends AppCompatActivity {

    public static final String OPT_FNAME = "fname-pdf";

    private static final Logger logger = LoggerManager.getLogger(SummaryViewActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary_view);
        PDFView pdfView = findViewById(R.id.pdfView);
        String fname = getIntent().getStringExtra(OPT_FNAME);
        try {
            pdfView.fromStream(openFileInput(fname))
                    .load();
        } catch (FileNotFoundException e) {
            logger.e("Couldn't find the desired PDF file?!", e);
        }
    }

}

package com.briccsquad.mobileportail;

import android.os.Bundle;
import android.renderscript.ScriptGroup;
import android.support.v7.app.AppCompatActivity;

import com.github.barteksc.pdfviewer.PDFView;
import com.noveogroup.android.log.Logger;
import com.noveogroup.android.log.LoggerManager;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SummaryViewActivity extends AppCompatActivity {

    public static final String OPT_FNAME = "fname-pdf";

    private static final Logger logger = LoggerManager.getLogger(SummaryViewActivity.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary_view);
        PDFView pdfView = findViewById(R.id.pdfView);
        String fname = getIntent().getStringExtra(OPT_FNAME);

        try (InputStream inputStream = openFileInput(fname)){
            byte[] arr = readBytes(inputStream);
            pdfView.fromBytes(arr).load();
        } catch (IOException e) {
            logger.e(e);
        }
    }

    private static byte[] readBytes(InputStream inputStream) throws IOException {
        byte[] b = new byte[1024];
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        int c;
        while ((c = inputStream.read(b)) != -1) {
            os.write(b, 0, c);
        }
        return os.toByteArray();
    }

}

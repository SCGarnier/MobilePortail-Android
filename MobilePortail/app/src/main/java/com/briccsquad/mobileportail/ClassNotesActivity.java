package com.briccsquad.mobileportail;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import net.sf.andpdf.pdfviewer.PdfViewerActivity;

import java.util.ArrayList;

// TODO: Add tabbed activity for notes and schedule
public class ClassNotesActivity extends AppCompatActivity {
    private TableLayout tl;

    private void generateTableView(String cinfo, ArrayList<Session.Domain> classDomains, String bgColor){
        // Create table row for displaying text on activity
        TableRow row = new TableRow(this);
        row.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT));

        row.setPadding(0,
                (int) getResources().getDimension(R.dimen.note_special_margin),
                0,
                (int) getResources().getDimension(R.dimen.note_special_margin));

        row.setBackgroundColor(Color.parseColor(bgColor));

        // Create text view for class name
        TextView dispClassName = new TextView(this);
        dispClassName.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 3));
        dispClassName.setText(cinfo);
        dispClassName.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        // Create linear layout for all domains
        LinearLayout linLayout = new LinearLayout(this);
        linLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                2));

        for(final Session.Domain dom: classDomains){
            String avg = dom.grade;

            TextView dispClassAvg = new TextView(this);
            dispClassAvg.setText((avg != null) ? avg : "N/A");
            dispClassAvg.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

            if(avg != null){
                row.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent itt = new Intent(getApplicationContext(), SummaryViewActivity.class);
                        itt.putExtra(PdfViewerActivity.EXTRA_PDFFILENAME, dom.summaryFileName);
                        startActivity(itt);
                    }
                });
            } else {
                dispClassAvg.setTypeface(null, Typeface.BOLD);
            }
        }

        // Add text to row
        row.addView(dispClassName);
        row.addView(linLayout);

        // Add table row to layout
        tl.addView(row);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_notes);

        tl = findViewById(R.id.table_layout_notes);
        setup();
    }

    private void setup(){
        int j = 0;

        int len = Session.domainList.size();

        for(int i = 0; i < len;){
            ArrayList<Session.Domain> arr = new ArrayList<>();
            Session.Domain dom = Session.domainList.get(i);
            int lastRep = Session.domainList.get(i).reportTo;

            while(true) {
                Session.Domain adom;

                try {
                    adom = Session.domainList.get(i);
                } catch(Exception e) {
                    break;
                }

                if(lastRep != adom.reportTo) break;
                arr.add(adom);
                i++;
            }

            String info = Session.notesInfoList.get(dom.reportTo);
            generateTableView(info, arr, ((j++) % 2 == 0) ? "#DDDDDD" : "#EEEEEE");
        }
    }
}

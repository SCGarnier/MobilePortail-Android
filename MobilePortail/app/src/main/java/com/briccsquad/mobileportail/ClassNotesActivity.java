package com.briccsquad.mobileportail;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import net.sf.andpdf.pdfviewer.PdfViewerActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// TODO: Add tabbed activity for notes and schedule
public class ClassNotesActivity extends AppCompatActivity {
    private TableLayout tl;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_class_notes, menu);

        // Add click listener
        menu.findItem(R.id.action_logout).setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                // Just quickly quit
                finish();
                return false;
            }
        });

        return true;
    }

    private void generateTableView(String tname, ArrayList<String> avg, final String fname, boolean inc, String bgColor){
        // Create table row for displaying text on activity
        TableRow tabr = new TableRow(this);
        tabr.setLayoutParams(new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.MATCH_PARENT,
                2));

        tabr.setPadding((int) getResources().getDimension(R.dimen.activity_horizontal_margin),
                (int) getResources().getDimension(R.dimen.note_special_margin),
                (int) getResources().getDimension(R.dimen.activity_horizontal_margin),
                (int) getResources().getDimension(R.dimen.note_special_margin));

        tabr.setBackgroundColor(Color.parseColor(bgColor));

        // Create text view for class name
        TextView dispClassName = new TextView(this);
        dispClassName.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 3));
        dispClassName.setText(tname);

        // Create text view for class average
        TextView dispClassAvg = new TextView(this);
        dispClassAvg.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2));
        dispClassAvg.setText((avg != null) ? avg.toString() : "N/A");
        dispClassAvg.setGravity(Gravity.CENTER);
        if(avg == null) dispClassAvg.setTypeface(null, Typeface.BOLD);

        if(inc){
            tabr.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent itt = new Intent(getApplicationContext(), SummaryViewActivity.class);
                    itt.putExtra(PdfViewerActivity.EXTRA_PDFFILENAME, fname);
                    startActivity(itt);
                }
            });
        }

        // Add text to row
        tabr.addView(dispClassName);
        tabr.addView(dispClassAvg);

        // Add table row to layout
        tl.addView(tabr);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_notes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        tl = findViewById(R.id.table_layout_notes);

        // Gte list of summary PDFs
        final String[] pdfFileList = getIntent().getStringArrayExtra("sumdocs");

        // Extract HTML from string result
        Document doc = Jsoup.parse(getIntent().getStringExtra("reqval"));

        // Get relevant structure
        final Element notesTable = doc.selectFirst("#Table1");
        final Elements elemList = notesTable.select("tr");

        // Loop through each row in the table, one pass only
        // for best efficiency possible
        // TODO: Optimize to reduce amount of node traversals
        for(int i = 1, j = 0, l = elemList.size(); i < l; i++){
            Element classData = elemList.get(i);

            // Now, things get complicated here. Some portail profiles will have
            // specific domains for each criteria, and the table is not properly
            // formatted to hold details like that. What's worse is that there's an
            // average in each criteria, not one single string we can display
            // for the class. We must handle this separately.

            // Select element containing teacher and class info
            Element nextClassElem, classElem = classData.selectFirst("div.text");

            // Is the student still in the class?
            final boolean inClass = (classData.selectFirst("font") == null);

            try {
                nextClassElem = elemList.get(i + 1);
            } catch(IndexOutOfBoundsException e){
                nextClassElem = null;
            }

            // String for class average; pass to table generator
            final ArrayList<String> classAvgList = new ArrayList<>();

            // Get full class ID string
            String className = classElem.text();

            // Add a question mark if there was no teacher specified
            if (className.endsWith(":")) {
                className += " ?";
            }

            // Is there a sub-domain right after?
            if (nextClassElem != null && nextClassElem.selectFirst("div.text") == null) {
                Element domainData = classData.select("td").get(1);
                Element domainNoteElem = classData;
                classAvgList.clear();

                for(++i; i < l; i++){

                    // Check if it's a domain delimiter
                    if(!domainData.hasText()){
                        break;
                    }

                    Element nextAvgElem = domainNoteElem.selectFirst("a");

                    // Get next average for the sum
                    String nextAvg = (nextAvgElem == null) ? "n" : nextAvgElem.text();

                    classAvgList.add(nextAvg);

                    // Select next element for the next iteration
                    domainNoteElem = elemList.get(i);
                    domainData = elemList.get(i).selectFirst("td");
                }
            } else {
                // Get average note
                Element elemAvg = classData.selectFirst("a");

                if (elemAvg != null) {
                    classAvgList.add(elemAvg.text());
                }
            }

            generateTableView(className, classAvgList, pdfFileList[j], inClass,((j++) % 2 == 0) ? "#DDDDDD" : "#EEEEEE");
        }
    }
}

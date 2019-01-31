package com.briccsquad.mobileportail;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.briccsquad.mobileportail.session.PortalUser;
import com.briccsquad.mobileportail.session.PortalUserGrade;

import java.util.List;

public class ClassNotesActivity extends AppCompatActivity {
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_notes);
        linearLayout = findViewById(R.id.linearGradesLayout);
        setup();
    }

    private void setup() {
        PortalUser user = PortalUser.getCurrent();
        for(int i = 0, l = user.getClassListSize(); i < l; i++){
            List<PortalUserGrade> gradeList = user.getGradesForClass(i);

            if(gradeList.isEmpty()) continue;

            // TODO support domains in layout
            PortalUserGrade grade = gradeList.get(0);
            View v = Utils.fromTemplate(this, R.layout.template_class_note);
            String rawGrade = grade.getRawGradeAverage();
            ((TextView) v.findViewById(R.id.gradeAverageClass)).setText(rawGrade);
            int assocClass = grade.getPortalClassIndex();
            String[] info = user.getGradeClassInfo(assocClass).split("\n");
            ((TextView) v.findViewById(R.id.classGradeName)).setText(info[0]);
            ((TextView) v.findViewById(R.id.teacherGradeName)).setText(info[1]);

            final String summaryFile = grade.getSummaryFile();
            if(summaryFile != null){
                v.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent itt = new Intent(getApplicationContext(), SummaryViewActivity.class);
                        itt.putExtra(SummaryViewActivity.OPT_FNAME, summaryFile);
                        startActivity(itt);
                    }
                });
            }

            linearLayout.addView(v);
        }
    }
}

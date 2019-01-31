package com.briccsquad.mobileportail;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.briccsquad.mobileportail.session.PortalUser;

public class ScheduleActivity extends AppCompatActivity {
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        linearLayout = findViewById(R.id.linearScheduleLayout);
        setup();
    }

    private void setup() {
        PortalUser user = PortalUser.getCurrent();

        for (int i = 0, l = user.getNumPeriods(); i < l; i++) {
            View v = Utils.fromTemplate(this, R.layout.template_sch_item);
            ((TextView) v.findViewById(R.id.periodNum)).setText(Integer.toString(i + 1));
            ((TextView) v.findViewById(R.id.classDayName)).setText(user.getPeriodClass(i));
            ((TextView) v.findViewById(R.id.teacherDayName)).setText(user.getPeriodTeacher(i));
            linearLayout.addView(v);
        }

        findViewById(R.id.resultsButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ClassNotesActivity.class));
            }
        });

        findViewById(R.id.newsButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), NewsActivity.class));
            }
        });
    }
}

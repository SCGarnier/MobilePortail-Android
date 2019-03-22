package com.briccsquad.mobileportail;

import android.annotation.SuppressLint;
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

    @SuppressLint("SetTextI18n")
    private void setup() {
        PortalUser user = PortalUser.getCurrent();

        // For each period, populate a small summary with the information generated
        // during the parsing phase
        for (int i = 0, l = user.getNumPeriods(); i < l; i++) {
            View v = Utils.fromTemplate(this, R.layout.template_sch_item);
            ((TextView) v.findViewById(R.id.periodNum)).setText(Integer.toString(i + 1));
            ((TextView) v.findViewById(R.id.classDayName)).setText(user.getPeriodClass(i));
            ((TextView) v.findViewById(R.id.teacherDayName)).setText(user.getPeriodTeacher(i));
            linearLayout.addView(v);
        }

        // Should there be no classes to attend, inform the user
        // about it instead of staying completely blank
        if (user.getNumPeriods() == 0) {
            findViewById(R.id.noClassMsg).setVisibility(View.VISIBLE);
        }

        // At the bottom of the activity, show whose profile we are looking at
        ((TextView) findViewById(R.id.usageInfo)).setText(getString(R.string.connected_as) +
                " " + user.getLoginName());

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

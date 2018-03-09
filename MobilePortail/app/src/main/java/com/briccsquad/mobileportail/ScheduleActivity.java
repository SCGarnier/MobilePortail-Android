package com.briccsquad.mobileportail;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class ScheduleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);
        setup();
    }

    // TODO: Remove hardcoded setups
    private void setup(){
        ((TextView) findViewById(R.id.classDayName)).setText(Session.classDayList.get(0));
        ((TextView) findViewById(R.id.classDayName2)).setText(Session.classDayList.get(1));
        ((TextView) findViewById(R.id.classDayName3)).setText(Session.classDayList.get(2));
        ((TextView) findViewById(R.id.classDayName4)).setText(Session.classDayList.get(3));

        ((TextView) findViewById(R.id.teacherDayName)).setText(Session.teacherDayList.get(0));
        ((TextView) findViewById(R.id.teacherDayName2)).setText(Session.teacherDayList.get(1));
        ((TextView) findViewById(R.id.teacherDayName3)).setText(Session.teacherDayList.get(2));
        ((TextView) findViewById(R.id.teacherDayName4)).setText(Session.teacherDayList.get(3));

        findViewById(R.id.resultsButton).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), ClassNotesActivity.class));
            }
        });

        findViewById(R.id.gotoLoginButton).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            }
        });
    }
}

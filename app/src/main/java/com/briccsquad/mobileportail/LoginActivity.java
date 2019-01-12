package com.briccsquad.mobileportail;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.noveogroup.android.log.Logger;
import com.noveogroup.android.log.LoggerManager;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
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
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A login screen that offers login via username and password.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

    private static Map<String, String> paramDict = new HashMap<>();
    private static final Logger logger = LoggerManager.getLogger(LoginActivity.class);

    // Fill the dictionary with the constant parameters
    static {
        paramDict.put("__VIEWSTATE", "/wEPDwULLTE1OTEzODk5NDRkZHeWIDblnhXfWVgudGFcvrqUrsa8oUjUBNqAwiyC5bQZ");
        paramDict.put("__VIEWSTATEGENERATOR", "3738FB10");
        paramDict.put("__EVENTVALIDATION", "/wEdAAS/EmKn67wLWprMxhcvNZYzNV4T48EJ76cvY4cUAzjjnR0O4L6f0pzi6oGvFfSn1SztaUkzhlzahAalhIckp3krG4fQXm17dVV5HUDhFMYg3hg06HAD0C/01YYOsiBthV8=");
        paramDict.put("Blogin", "Entrer");
    }

    // UI references.
    private EditText mUnameView;
    private EditText mPasswordView;
    private View mLoginFormView;

    /**
     * A RegExp used to extract the necessary URL for PDFs from the HTML link source.
     */
    private static final Pattern pdfUrlPattern = Pattern.compile("open\\('(.*)'\\);");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the login form.
        mUnameView = findViewById(R.id.uname);

        mPasswordView = findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == EditorInfo.IME_ACTION_DONE || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }

                return false;
            }
        });

        Button mUnameSignInButton = findViewById(R.id.uname_sign_in_button);
        mUnameSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid username, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mUnameView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String uname = mUnameView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid username.
        if (TextUtils.isEmpty(uname)) {
            mUnameView.setError(getString(R.string.error_field_required));
            focusView = mUnameView;
            cancel = true;
        }

        // Check for a valid password.
        if (TextUtils.isEmpty(password)) {
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            View focView = getCurrentFocus();
            if (focView != null) { // Remove keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(focView.getWindowToken(), 0);
            }

            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(uname, password);
            mAuthTask.execute((Void) null);
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_mediumAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    @SuppressLint("StaticFieldLeak")
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUname;
        private final String mPassword;
        private String mPortailSchedule;
        private String mPortailGrades;
        private final ArrayList<String> mPortailSummaries = new ArrayList<>();

        UserLoginTask(String uname, String password) {
            mUname = uname;
            mPassword = password;
        }

        private final String API_URL_PREFIX = "https://apps.cscmonavenir.ca/PortailEleves/index.aspx?ReturnUrl=";

        private Document loadPage(final String extra){
            try {
                Document doc = Jsoup.connect(API_URL_PREFIX + extra)
                        .data(paramDict)
                        .timeout(4000)
                        .followRedirects(true)
                        .post();

                // Check if credentials were wrong
                if(doc.title().contains("portail")){
                    logger.w("got title page instead of accessing '%s'", extra);
                    return null;
                }

                return doc;
            } catch(IOException e) {
                logger.e("error processing request", e);
                return null;
            }
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            // Add username and password to dictionary
            paramDict.put("Tlogin", mUname);
            paramDict.put("Tpassword", mPassword);

            // Attempt authentication using the portal server.
            try {
                Document pageSchedule = loadPage("%2fPortailEleves%2fEmploiDuTemps.aspx");
                Document pageGrades = loadPage("%2fPortailEleves%2fAccueilEleve.aspx");

                Elements linkList = pageGrades.select("#Table1 a");
                DefaultHttpClient httpClient = new DefaultHttpClient();

                for(int i = 0; i < linkList.size(); i++){
                    Element el = linkList.get(i);

                    final Matcher m = pdfUrlPattern.matcher(el.attr("onclick"));
                    if(m.find()){
                        String fname = i + ".pdf";
                        String encodedMatch = m.group(1);

                        byte[] inp;

                        // Get PDF from the server
                        try {
                            HttpPost httpPost = new HttpPost("https://apps.cscmonavenir.ca/PortailEleves/index.aspx?ReturnUrl=" +
                                    Uri.encode(encodedMatch));

                            ArrayList<NameValuePair> modParams = new ArrayList<>();
                            for(Map.Entry<String, String> entry: paramDict.entrySet()){
                                modParams.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                            }

                            httpPost.setEntity(new UrlEncodedFormEntity(modParams));
                            HttpResponse resp = httpClient.execute(httpPost);
                            InputStream inputStream = resp.getEntity().getContent();
                            inp = new byte[(int) resp.getEntity().getContentLength()];
                            inputStream.read(inp);
                            inputStream.close();
                        } catch(IOException e){
                            logger.e(e);
                            return false;
                        }

                        // Write the response to a file
                        try {
                            FileOutputStream fos = openFileOutput(fname, Context.MODE_PRIVATE);
                            fos.write(inp);
                            fos.close();
                        } catch (IOException e){
                            e.printStackTrace();
                            return false;
                        }

                        // Add to the tracked files
                        mPortailSummaries.add(fname);
                    } else {
                        logger.e("could not find document URL in attribute");
                        return false;
                    }
                }

                mPortailSchedule = pageSchedule.html();
                mPortailGrades = pageGrades.html();
            } catch(NullPointerException e){
                logger.e("NPE occurred, probably caused by invalid credentials", e);
                return false;
            }

            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);

            // TODO: Figure out localized text for 'OK'
            if (!success) {
                new AlertDialog.Builder(LoginActivity.this)
                        .setTitle(getString(R.string.app_name))
                        .setMessage(getString(R.string.err_try_again))
                        .setPositiveButton(getString(android.R.string.ok), null).show();
            } else {

                // Go display class notes and other stuff
                Session.portailGradesContent = mPortailGrades;
                Session.portailScheduleContent = mPortailSchedule;
                Session.portailSumPages = mPortailSummaries;

                Session.computeGradesData();
                Session.computeScheduleData();

                startActivity(new Intent(getApplicationContext(), ScheduleActivity.class));
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}


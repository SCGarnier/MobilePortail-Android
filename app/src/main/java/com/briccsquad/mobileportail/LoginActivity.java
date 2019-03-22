package com.briccsquad.mobileportail;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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
import android.widget.ProgressBar;
import android.widget.TextView;

import com.briccsquad.mobileportail.session.LoginCredentials;
import com.briccsquad.mobileportail.session.ProfileGenerator;
import com.briccsquad.mobileportail.session.StreamProvider;
import com.noveogroup.android.log.Logger;
import com.noveogroup.android.log.LoggerManager;

/**
 * A login screen that offers login via username and password.
 */
public class LoginActivity extends AppCompatActivity {

    private static final Logger logger = LoggerManager.getLogger(LoginActivity.class);

    // TODO look into potentially allowing user to cancel task
    private UserLoginTask mAuthTask = null;

    // UI references
    private EditText mUnameView;
    private EditText mPasswordView;
    private View mLoginFormView;
    private ProgressBar mProgressBarLogin;
    private View mProgressTextLogin;

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
        mProgressBarLogin = findViewById(R.id.progressBarLogin);
        mProgressTextLogin = findViewById(R.id.progressInfoText);
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
            View userFocusTarget = getCurrentFocus();

            // Remove keyboard
            if (userFocusTarget != null) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(userFocusTarget.getWindowToken(), 0);
                }
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
    private void showProgress(final boolean show) {
        int shortAnimTime = getResources().getInteger(android.R.integer.config_mediumAnimTime);

        mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        });

        mProgressBarLogin.setVisibility(show ? View.VISIBLE : View.GONE);
        mProgressTextLogin.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    @SuppressLint("StaticFieldLeak")
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mUname;
        private final String mPassword;

        UserLoginTask(String uname, String password) {
            mUname = uname;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            LoginCredentials lc = new LoginCredentials(mUname, mPassword);
            StreamProvider sp = new StreamProvider(getApplicationContext(), lc);
            ProfileGenerator profileGenerator = new ProfileGenerator(sp);

            return profileGenerator.generateProfile(new ProfileGenerator.ProgressUpdater() {

                @Override
                public void update(final int p) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mProgressBarLogin.setProgress(p);
                        }
                    });
                }

                @Override
                public void setMax(final int m) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mProgressBarLogin.setMax(m);
                        }
                    });
                }
            });
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;

            // Clear the progress bar when done in case somebody else logs in afterwards
            mProgressBarLogin.setProgress(0);

            // Clear the password for next user to log in without any security issues
            mPasswordView.setText("");

            if (!success) {
                showProgress(false);

                // Tell the user that something went wrong
                // TODO would be nice to be a bit more informative
                new AlertDialog.Builder(LoginActivity.this)
                        .setTitle(getString(R.string.app_name))
                        .setMessage(getString(R.string.err_try_again))
                        .setPositiveButton(getString(android.R.string.ok), null)
                        .show();
            } else {

                // Clear the name field here because chances are the next person will want to enter
                // something other than what is inside said field
                mUnameView.setText("");

                startActivity(new Intent(getApplicationContext(), ScheduleActivity.class));

                // Avoid a UI flicker by disabling progress afterwards
                showProgress(false);
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }
}


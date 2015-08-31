package tk.easthigh.witsmobile;


import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

import java.net.MalformedURLException;
import java.net.URL;

import tk.easthigh.witsmobile.tools.CircularImageView;
import tk.easthigh.witsmobile.tools.DataManager;
import tk.easthigh.witsmobile.tools.SecurePreferences;

public class LoginFragment extends Fragment {


    public LoginFragment() {
        // Required empty public constructor
    }

    private EditText usernameView;
    private EditText passwordView;
    private View mProgressView;
    private View mLoginFormView;
    private View view;
    private UserLoginTask mAuthTask = null;
    private boolean isChecked;
    private Context context;
    private View focusView = null;
    private SecurePreferences sPrefs;
    private Activity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        super.onCreate(savedInstanceState);
        activity = getActivity();
        context = activity.getApplicationContext();
        view = inflater.inflate(R.layout.fragment_login, container, false);
        sPrefs = new DataManager().getSecurePrefs(context);

        usernameView = (EditText) view.findViewById(R.id.username);
        usernameView.setText(sPrefs.getString("userID"));
        passwordView = (EditText) view.findViewById(R.id.password);

        if (sPrefs.getString("userID") != null){
            usernameView.setText(sPrefs.getString("userID"));
            passwordView.requestFocus();
        }

        passwordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button usernameSignInButton = (Button) view.findViewById(R.id.email_sign_in_button);
        usernameSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = view.findViewById(R.id.login_form);
        mProgressView = view.findViewById(R.id.login_progress);

        addListenerOnChkIos();

        return view;
    }

    public void addListenerOnChkIos() {

        CheckBox chkbox;
        chkbox = (CheckBox) view.findViewById(R.id.chkbox);

        chkbox.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                isChecked = ((CheckBox) v).isChecked();
            }

        });

    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid username, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    public void attemptLogin() {


        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        usernameView.setError(null);
        passwordView.setError(null);

        boolean isUsernameAlpha = true;

        for (int i = 0; i < usernameView.getText().toString().length(); i++)
            if (!Character.isAlphabetic(usernameView.getText().toString().charAt(i)))
                isUsernameAlpha = false;


        // Store values at the time of the login attempt.
        String user = usernameView.getText().toString();
        String password = passwordView.getText().toString();

        boolean cancel = false;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            passwordView.setError(getString(R.string.error_field_required));
            focusView = passwordView;
            cancel = true;
        } else if (TextUtils.isEmpty(user)) {
            usernameView.setError(getString(R.string.error_field_required));
            focusView = usernameView;
            cancel = true;
        } else if (!isUsernameAlpha){
            usernameView.setError(getString(R.string.error_invalid_username));
            focusView = usernameView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            showProgress(true);
            if (!new DataManager().isOnline()) {
                Snackbar.make(activity.findViewById(R.id.nav_contentframe), "No internet connection!", Snackbar.LENGTH_SHORT);
                showProgress(false);
            } else {
                // Show a progress spinner, and kick off a background task to
                // perform the user login attempt.
                mAuthTask = new UserLoginTask(user, password);

                ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(view.getWindowToken(), 0);
                mAuthTask.execute(context);
            }
        }
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {

                    // TODO: Bug: When logging in and switching to another fragment, a illegal state exception is thrown, since the fragment isn't attached to the activity
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Context, Void, Boolean> {

        private final String username;
        private final String password;
        String response;
        private DataManager dataManager;

        UserLoginTask(String email, String password) {
            username = email;
            this.password = password;
        }

        @Override
        protected Boolean doInBackground(Context... params) {
            response = new DataManager(username, password).getResource("getName");
            dataManager = new DataManager(params[0]);
            dataManager.setProfilePic(username, password);

            return response != null;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            showProgress(false);
            URL hostSite = null;

            try {
                hostSite = new URL("http://easthigh.tk/");
            } catch (MalformedURLException e) {
                Log.wtf("Login Fragment", "\"http://easthigh.tk/\" was parsed as invalid!");
            }

            if (success) {
                sPrefs.put("isDataDone", "false");
                finish();
            } if (!dataManager.isOnline(hostSite))
                Snackbar.make(activity.findViewById(R.id.nav_contentframe), "Data Parser Servers are down! Please contact Support!", Snackbar.LENGTH_LONG).show();
            else {
                passwordView.setError(getString(R.string.error_incorrect_password));
                passwordView.setSelected(true);
                passwordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }


        void finish() {
            sPrefs.put("userID", username);
            sPrefs.put("userPass", password);
            sPrefs.put("saveUserData", Boolean.toString(isChecked));

            sPrefs.put("name", response);


            final DataManager dataManager = new DataManager(username, password);
            dataManager.collectData();

            view.getHandler()
                    .post(new Runnable() {
                              @Override
                              public void run() {
                                  sPrefs.put("userLoggedIn", "true");
                                  TextView drawerName = ((TextView) activity.getWindow().findViewById(android.R.id.content).findViewById(R.id.drawerName));
                                  drawerName.setText(response);
                                  ((NavigationView) activity.getWindow().findViewById(R.id.nav_view)).getMenu().getItem(7).setTitle("Logout");
                                  Snackbar.make(activity.getWindow().findViewById(R.id.nav_contentframe), "Welcome, " + sPrefs.getString("name"), Snackbar.LENGTH_SHORT).show();

                                  CircularImageView circle = (CircularImageView) activity.findViewById(R.id.profile_img);
                                  Boolean isGetPhoto = false;
                                  while (!isGetPhoto) {
                                      try {
                                          circle.setImageURI(Uri.parse(context.getFileStreamPath("profile.jpg").getAbsolutePath()));
                                          isGetPhoto = true;
                                      } catch (Exception e) {
                                          e.printStackTrace();
                                          try {
                                              Thread.sleep(500);
                                          } catch (Exception ef) {
                                              ef.printStackTrace();
                                          }
                                      }
                                  }
                              }
                          }

                    );

            activity.getFragmentManager().
                    beginTransaction()
                    .replace(R.id.nav_contentframe, new RssFragment())
                    .commit();
        }

    }
}
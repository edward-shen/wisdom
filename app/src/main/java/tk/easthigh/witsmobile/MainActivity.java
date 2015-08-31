package tk.easthigh.witsmobile;

//https://github.com/Suleiman19/Android-Material-Design-for-pre-Lollipop

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import tk.easthigh.witsmobile.tools.CircularImageView;
import tk.easthigh.witsmobile.tools.DataManager;
import tk.easthigh.witsmobile.tools.SecurePreferences;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private DrawerLayout mDrawerLayout;
    NavigationView mNavigationView;
    FrameLayout mContentFrame;

    private static final String PREFERENCES_FILE = "preferences";
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";

    public static SecurePreferences preferences; // TODO: Static or non-static

    private boolean mUserLearnedDrawer;
    boolean mFromSavedInstanceState;
    private int mCurrentSelectedPosition;

    private MenuItem mPreviousMenuItem;

    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DataManager dataManager = new DataManager();
        preferences  = dataManager.getSecurePrefs(getApplicationContext());

        // Theme setting
        if (preferences.getString("theme") != null)
            switch(preferences.getString("theme")) {
                case "east":
                    setTheme(R.style.Theme_East);
                    Log.i("Main", "Theme of EAST selected!");
                    break;
                case "north":
                    setTheme(R.style.Theme_North);
                    Log.i("Main", "Theme of NORTH selected!");
                    break;
                case "south":
                    setTheme(R.style.Theme_South);
                    Log.i("Main", "Theme of SOUTH selected!");
                    break;
            }
        else {
            setTheme(R.style.Theme_East);
            Log.i("Main", "No theme selected, defaulting to EAST!");
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpToolbar();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.nav_drawer);
        mContentFrame = (FrameLayout) findViewById(R.id.nav_contentframe);
        mNavigationView = (NavigationView) findViewById(R.id.nav_view);

        // Clean up any data we stored in the last session if the user did not wish to save any data

        mUserLearnedDrawer = Boolean.valueOf(readSharedSetting(this, PREF_USER_LEARNED_DRAWER, "false"));

        if (savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            mFromSavedInstanceState = true;
        }

        setUpNavDrawer();

        recallUserdata();

        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                menuItem.setCheckable(true);
                menuItem.setChecked(true);
                if (mPreviousMenuItem != null && mPreviousMenuItem != menuItem)
                    mPreviousMenuItem.setChecked(false);
                mPreviousMenuItem = menuItem;

                switch (menuItem.getItemId()) {
                    case R.id.navigation_item_0:
                        if (preferences.getString("userID") != null && preferences.getString("userID").equalsIgnoreCase("edshen")) {
                            mCurrentSelectedPosition = 0;
                            onNavigationDrawerItemSelected(mCurrentSelectedPosition);
                        } else
                            Snackbar.make(mContentFrame, "Sorry, this feature is not yet implemented!", Snackbar.LENGTH_SHORT).show();
                        return true;
                    case R.id.navigation_item_1:
                        mCurrentSelectedPosition = 1;
                        onNavigationDrawerItemSelected(mCurrentSelectedPosition);
                        return true;
                    case R.id.navigation_item_2:
                        mCurrentSelectedPosition = 2;
                        onNavigationDrawerItemSelected(mCurrentSelectedPosition);
                        return true;
                    case R.id.navigation_item_3:
                        if (preferences.getString("userID") != null && preferences.getString("userID").equalsIgnoreCase("edshen")) {
                            mCurrentSelectedPosition = 3;
                            onNavigationDrawerItemSelected(mCurrentSelectedPosition);
                        } else
                            Snackbar.make(mContentFrame, "Sorry, this feature is not yet functional!", Snackbar.LENGTH_SHORT).show();
                        return true;
                    case R.id.navigation_item_4:
                        mCurrentSelectedPosition = 4;
                        onNavigationDrawerItemSelected(mCurrentSelectedPosition);
                        return true;
                    case R.id.navigation_item_5:
                        if (preferences.getString("userID") != null && preferences.getString("userID").equalsIgnoreCase("edshen")) {
                            mCurrentSelectedPosition = 5;
                            onNavigationDrawerItemSelected(mCurrentSelectedPosition);
                        } else
                            Snackbar.make(mContentFrame, "Sorry, this feature is not yet implemented!", Snackbar.LENGTH_SHORT).show();
                        return true;
                    case R.id.navigation_item_6:
                        mCurrentSelectedPosition = 6;
                        onNavigationDrawerItemSelected(mCurrentSelectedPosition);
                        return true;
                    case R.id.navigation_item_7:
                        // for Login/out, don't update the position
                        mCurrentSelectedPosition = 7;
                        onNavigationDrawerItemSelected(mCurrentSelectedPosition);
                        return true;
                    case R.id.navigation_item_8:
                        mCurrentSelectedPosition = 8;
                        onNavigationDrawerItemSelected(mCurrentSelectedPosition);
                        return true;
                    case R.id.navigation_item_9:
                        mCurrentSelectedPosition = 9;
                        onNavigationDrawerItemSelected(mCurrentSelectedPosition);
                        return true;
                    default:
                        return true;
                }

            }
        });


        // Starts the app with the Homepage open
        if(preferences.getString("userID") != null && preferences.getString("userID").equalsIgnoreCase("edshen"))
            getFragmentManager().beginTransaction()
                    .replace(R.id.nav_contentframe, new HomeFragment())
                    .commit();
        else
            getFragmentManager().beginTransaction()
                    .replace(R.id.nav_contentframe, new RssFragment())
                    .commit();

    }

    @Override
    public boolean onKeyDown(int keycode, KeyEvent e) {
        switch(keycode) {
            case KeyEvent.KEYCODE_MENU:
                Log.i("MainActivity","Menu button pressed!");
                ((InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                if (!mDrawerLayout.isDrawerOpen(Gravity.LEFT))
                    mDrawerLayout.openDrawer(Gravity.LEFT);
                else
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                return true;
        }

        return super.onKeyDown(keycode, e);
    }

    @Override
    public void onBackPressed() {
        if (preferences.getString("parentFragment") == null) {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Snackbar.make(mContentFrame, "Press Back again to exit.", Snackbar.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        } else {
            FragmentTransaction ft = getFragmentManager().beginTransaction()
                    .setCustomAnimations(R.animator.slide_in_right, R.animator.slide_out_left);

            switch (preferences.getString("parentFragment")){
                case "ClassesFragment":
                    ft.replace(R.id.nav_contentframe, new ClassesFragment(), null);
                    break;
                case "MailFragment":
                    ft.replace(R.id.nav_contentframe, new MailFragment(), null);
                    break;
            }

            ft.commit();

            preferences.removeValue("parentFragment");

        }
    }

    public void onNavigationDrawerItemSelected(int position) {
        Fragment myFragment = null;
        switch(position) {
            case 0:
                mToolbar.setTitle("Home");
                myFragment = new HomeFragment();
                mDrawerLayout.closeDrawers();
                break;
            case 1:
                mToolbar.setTitle("East Side News");
                myFragment = new RssFragment();
                mDrawerLayout.closeDrawers();
                break;
            case 2:
                if (preferences.getString("name") == null)
                    Snackbar.make(mContentFrame, "Please log in to use this feature.", Snackbar.LENGTH_LONG).show();
                else if (preferences.getString("isDataDone") != null && preferences.getString("isDataDone").equals("false"))
                    Snackbar.make(mContentFrame, "Please wait, data is being loaded.", Snackbar.LENGTH_LONG).show();
                else {
                    mToolbar.setTitle("Classes");
                    myFragment = new ClassesFragment();
                    mDrawerLayout.closeDrawers();
                }
                break;
            case 3:
                if (preferences.getString("name") == null)
                    Snackbar.make(mContentFrame, "Please log in to use this feature.", Snackbar.LENGTH_LONG).show();
                else if (preferences.getString("isDataDone") != null && preferences.getString("isDataDone").equals("false"))
                    Snackbar.make(mContentFrame, "Please wait, data is being loaded.", Snackbar.LENGTH_LONG).show();
                else {
                    mToolbar.setTitle("Recent Grades");
                    myFragment = new RecentGradesFragment();
                    mDrawerLayout.closeDrawers();
                }
                break;
            case 4:
                if (preferences.getString("name") == null)
                    Snackbar.make(mContentFrame, "Please log in to use this feature.", Snackbar.LENGTH_LONG).show();
                else if (preferences.getString("isDataDone") == null
                        || preferences.getString("isMailDone") == null
                        || !preferences.getString("isMailDone").equals("true")) {
                    Snackbar.make(mContentFrame, "Please wait, data is being loaded.", Snackbar.LENGTH_LONG).show();
                    new DataManager(preferences).setMailData();
                } else {
                    mToolbar.setTitle("Mail");
                    myFragment = new MailFragment();
                    mDrawerLayout.closeDrawers();
                }
                break;
            case 5:
                if (preferences.getString("name") == null)
                    Snackbar.make(mContentFrame, "Please log in to use this feature.", Snackbar.LENGTH_LONG).show();
                else if (preferences.getString("isDataDone") != null && preferences.getString("isDataDone").equals("false"))
                    Snackbar.make(mContentFrame, "Please wait, data is being loaded.", Snackbar.LENGTH_LONG).show();
                else {
                    mToolbar.setTitle("Profile");
                    myFragment = new ProfileFragment();
                    mDrawerLayout.closeDrawers();
                }
                break;
            case 6:
                mToolbar.setTitle("Settings");
                myFragment = new SettingsFragment();
                mDrawerLayout.closeDrawers();
                break;
            case 7:
                if (preferences.getString("name") == null) {
                    mToolbar.setTitle("Login");
                    myFragment = new LoginFragment();
                    mDrawerLayout.closeDrawers();
                }
                else
                    logout();
                break;
            case 8:
                mToolbar.setTitle("Frequently Asked Questions");
                myFragment = new FaqFragment();
                mDrawerLayout.closeDrawers();
                break;
            case 9:
                mToolbar.setTitle("About Us");
                myFragment = new AboutUsFragment();
                mDrawerLayout.closeDrawers();
                break;

        }

        if(myFragment!=null) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.nav_contentframe, myFragment)
                    .commit();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION, 0);
        Menu menu = mNavigationView.getMenu();
        menu.getItem(mCurrentSelectedPosition).setChecked(true);
    }

    // TODO: Are the next 2 methods nesscessary?
    // Edit: They probably are useful as reference text
    /*
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
   */

    private void setUpToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setNavigationIcon(R.drawable.ic_menu);
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
        }
    }

    private void setUpNavDrawer() {

        if (mToolbar != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                }
            });
        }

        if (!mUserLearnedDrawer) {
            mDrawerLayout.openDrawer(GravityCompat.START);
            mUserLearnedDrawer = true;
            saveSharedSetting(this, PREF_USER_LEARNED_DRAWER, "true");
        }
    }

    private void recallUserdata(){

        if (preferences.getString("name") != null)
            runOnUiThread(new Runnable() {
                public void run() {
                    // Clean up any data we stored in the last session if the user did not wish to save any data
                    Log.v("MainActivity",
                            (preferences.getString("themeSwitch") != null) ?
                                    "themeSwitch="  + preferences.getString("themeSwitch") : "No themeswitch");

                    if (preferences.getString("themeSwitch") == null || preferences.getString("themeSwitch").equals("false")) {
                        if (preferences.getString("saveUserData") != null && preferences.getString("saveUserData").equals("false")) {
                            new DataManager().removeData();
                        }
                    } else {
                        preferences.put("themeSwitch", "false");
                        if (preferences.getString("name") != null) {
                            Snackbar.make(mContentFrame, "Welcome back, " + preferences.getString("name"), Snackbar.LENGTH_SHORT).show();
                            TextView drawerName = ((TextView) findViewById(android.R.id.content).findViewById(R.id.drawerName));
                            drawerName.setText(preferences.getString("name"));
                            ((NavigationView) getWindow().findViewById(R.id.nav_view)).getMenu().getItem(7).setTitle("Logout");
                            CircularImageView circle = (CircularImageView) findViewById(R.id.profile_img);
                            circle.setImageURI(Uri.parse(getFileStreamPath("profile.jpg").getAbsolutePath()));
                        }
                    }
                }
            });
        else
            runOnUiThread(new Runnable() {
                public void run() {
                    ((NavigationView) getWindow().findViewById(R.id.nav_view)).getMenu().getItem(7).setTitle("Login");
                }
            });
    }

    public static void saveSharedSetting(Context ctx, String settingName, String settingValue) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(settingName, settingValue);
        editor.apply();
    }

    public static String readSharedSetting(Context ctx, String settingName, String defaultValue) {
        SharedPreferences sharedPref = ctx.getSharedPreferences(PREFERENCES_FILE, Context.MODE_PRIVATE);
        return sharedPref.getString(settingName, defaultValue);
    }

    private void logout(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Confirm");
        builder.setMessage("Are you sure?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                removeCurrentUserData();
                dialog.dismiss();
            }

        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    private void removeCurrentUserData(){

        // Commented, so we can log back in easily
        // preferences.removeValue("userID");
        preferences.removeValue("userPass");
        preferences.removeValue("name");
        preferences.put("saveUserData", "false");

        mCurrentSelectedPosition = 0;
        onNavigationDrawerItemSelected(mCurrentSelectedPosition);

        findViewById(android.R.id.content).post(new Runnable() {
            @Override
            public void run() {
                TextView drawerName = ((TextView) getWindow().findViewById(android.R.id.content).findViewById(R.id.drawerName));
                drawerName.setText("Welcome Guest");

                CircularImageView circle = (CircularImageView) findViewById(R.id.profile_img);
                circle.setImageResource(R.drawable.ic_default_profile);
                // TODO: Make this dynamic
                ((NavigationView) getWindow().findViewById(R.id.nav_view)).getMenu().getItem(7).setTitle("Login");
            }
        });
    }
}

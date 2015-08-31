package tk.easthigh.witsmobile.tools;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RunnableFuture;

import tk.easthigh.witsmobile.MainActivity;

/**
 * Created by Edward on 6/28/2015.
 */
public class DataManager {
    private String username;
    private String password;
    private SecurePreferences sPrefs;
    private Context context;
    private String home = "http://easthigh.tk/wisdom/wits.php";

    private final String LOG_TAG = "Data";

    /**
     * This constructor should only be used in two cases:
     *  1) By the actual DataManager class itself
     *  2) By the login form.
     * @param username  WITS username
     * @param password  WITS password
     */
    public DataManager(String username, String password){
        this.username = username;
        this.password = password;
    }

    /**
     * For post-login data retrieving only. Examples include the mail list and each mail entry.
     * @param sPrefs Used to retrieve username and password
     * @throws NullPointerException when the username and password have not been set.
     */
    public DataManager(SecurePreferences sPrefs) throws NullPointerException{
        this.sPrefs = sPrefs;
        if(sPrefs.getString("userID") != null && !sPrefs.getString("userID").equals("")) {
            username = sPrefs.getString("userID");
            Log.i(LOG_TAG, "Successfully retrieved username from stored data:" + username);
        } else throw new NullPointerException("userID = null!");

        if(sPrefs.getString("userPass") != null && !sPrefs.getString("userPass").equals("")){
            password = sPrefs.getString("userPass");
            Log.i(LOG_TAG, "Successfully retrieved password from stored data:" + password);
        } else throw new NullPointerException("userPass = null!");
    }

    public DataManager() { } // For low sec level requests, i.e. remove data

    /**
     * For data which requires system resources to process, i.e. photos
     * @param context Application context
     */
    public DataManager(Context context) {
        this.context = context;
    }

    public String getResource(String value) {
        URL url;
        HttpURLConnection conn = null;

        String response = "";

        try {
            String urlParameters = "username=" + username + "&password=" + password + "&data=" + value;
            byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
            int postDataLength = postData.length;
            url = new URL(home);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("charset", "utf-8");
            conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
            conn.setUseCaches(false);
            try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
                wr.write(postData);
            }

            InputStream in = new BufferedInputStream(conn.getInputStream());
            response = convertStreamToString(in).trim();
            Log.v(LOG_TAG, "resource requested:" + value + "\treturned value: " + response);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }

        if (response.equals(""))
            return null;

        return response;

    }

    public void collectData() { new CacheData(username, password).execute(); }

    public JSONArray getArray(String[] keyParams, String[] valueParams) {
        // TODO: attempt authentication against a network service.

        URL url;
        HttpURLConnection conn = null;
        JSONArray response = null;

        try {
            String urlParameters = "username=" + username + "&password=" + password;
            for (int i = 0; i < keyParams.length; i++) {
                urlParameters = urlParameters + "&" + keyParams[i] + "=" + valueParams[i];
            }
            Log.i(LOG_TAG, "Getting array with POST value \"" + urlParameters + "\"");
            byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
            int postDataLength = postData.length;
            url = new URL(home);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("charset", "utf-8");
            conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
            conn.setUseCaches(false);
            try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
                wr.write(postData);
            }

            InputStream in = new BufferedInputStream(conn.getInputStream());
            String rawData = convertStreamToString(in);
            if (rawData.contains("<br />"))
                Log.wtf(LOG_TAG, "PHP RETURNED ERROR");
            Log.v(LOG_TAG, "Raw Array Data: " + rawData);
            response = new JSONArray(rawData.trim());
            if (response.length() != 0)
                Log.i(LOG_TAG, "Array with POST value \"" + urlParameters + "\" successfully received!");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }

        return response;

    }

    public JSONArray getArray(String value) {
        // TODO: attempt authentication against a network service.

        URL url;
        HttpURLConnection conn = null;

        JSONArray response = null;

        Log.i(LOG_TAG, "Getting array with POST value \"" + value + "\"");

        try {
            String urlParameters = "username=" + username + "&password=" + password + "&data=" + value;
            byte[] postData = urlParameters.getBytes(StandardCharsets.UTF_8);
            int postDataLength = postData.length;
            url = new URL(home);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("charset", "utf-8");
            conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
            conn.setUseCaches(false);
            try (DataOutputStream wr = new DataOutputStream(conn.getOutputStream())) {
                wr.write(postData);
            }

            InputStream in = new BufferedInputStream(conn.getInputStream());
            String rawData = convertStreamToString(in);
            if (rawData.contains("<br />"))
                Log.wtf(LOG_TAG, "PHP RETURNED ERROR");
            Log.v(LOG_TAG, "Raw Array Data: " + rawData);
            response = new JSONArray(rawData.trim());
            if (response.length() != 0)
                Log.i(LOG_TAG, "Array with POST value \"" + value + "\" successfully received!");

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            conn.disconnect();
        }

        return response;

    }

    // The only difference between wipeData and clearData is that wipeData clears EVERYTHING.
    public void removeData(){

        String userTemp = MainActivity.preferences.getString("userID");
        String themeTemp = MainActivity.preferences.getString("theme");

        MainActivity.preferences.clear();
        MainActivity.preferences.put("saveUserData", "false");
        MainActivity.preferences.put("userLoggedIn", "false");

        MainActivity.preferences.put("userID", userTemp);
        MainActivity.preferences.put("theme", themeTemp);
    }

    public void wipeData(){
        MainActivity.preferences.clear();
        MainActivity.preferences.put("saveUserData", "false");
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }


    public boolean isOnline() {
        RunnableFuture runnable = new FutureTask<>(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                Runtime runtime = Runtime.getRuntime();

                Log.i(LOG_TAG, "runtime recieved");
                try {
                    Log.i(LOG_TAG, "Checking internet status...");
                    Process ipProcess = runtime.exec("/system/bin/ping -c 1 8.8.8.8");
                    int exitValue = ipProcess.waitFor();

                    if (exitValue == 0)
                        Log.i(LOG_TAG, "Internet connection found!");
                    else
                        Log.i(LOG_TAG, "No Internet connection found!");

                    return (exitValue == 0);

                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }

                return false;
            }

        });

        new Thread(runnable).start();

        try {
            return (Boolean) runnable.get();
        } catch (InterruptedException | ExecutionException e){ Log.e(LOG_TAG, "Internet code broke, pls fix"); }

        return false;
    }

    public boolean isOnline(final URL siteURL){

        RunnableFuture runnable = new FutureTask<>(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                Runtime runtime = Runtime.getRuntime();

                Log.v(LOG_TAG, "runtime recieved");
                try {
                    Log.i(LOG_TAG, "Checking connectivity to " + siteURL.toString());
                    if (siteURL.toString().equals("http://easthigh.tk")) return true; // TODO: easthigh.tk rejects pings. Which means we can't check if it's up or not.
                    Process ipProcess = runtime.exec("/system/bin/ping -c 1 " + siteURL.toString());
                    int exitValue = ipProcess.waitFor();

                    if (exitValue == 0)
                        Log.i(LOG_TAG, "Connected to " + siteURL.toString() + " succesfully!");
                    else
                        Log.i(LOG_TAG, "No connection to " + siteURL.toString() + " found!");

                    return (exitValue == 0);

                } catch (IOException | InterruptedException e) { e.printStackTrace(); }

                return false;
            }

        });

        new Thread(runnable).start();

        try {
            return (Boolean) runnable.get();
        } catch (InterruptedException | ExecutionException e){ Log.e(LOG_TAG, "Internet code broke, pls fix"); }

        return false;
    }

    public SecurePreferences getSecurePrefs(Context context){
        final String key = "=2fssS<4k>ESWL)4].np~N!nv,9%_}(:";
        return new SecurePreferences(context, "preferences", key, true);
    }

    // Vestigial code
    public void setMailData(){
        setMailData(username, password);
    }

    private void setMailData(final String username, final String password) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {

                Log.i(LOG_TAG, "Getting mail...");
                SecurePreferences sPrefs = MainActivity.preferences;
                DataManager dataManager = new DataManager(username, password);

                Log.v(LOG_TAG, "username=" + username + ",password=" + password);
                JSONArray mailArray = dataManager.getArray("getMail");

                int size = Integer.valueOf(dataManager.getResource("getMailSize"));

                try {
                    if (size == 0)
                        Log.e(LOG_TAG, "JSON Array size = 0!");
                    for (int i = 0; i < size; i++) {
                        sPrefs.put("mail.size", Integer.toString(size));
                        JSONObject emailEntry = (JSONObject) mailArray.get(i);

                        if (emailEntry.getString("title").length() > 78) {
                            Log.i(LOG_TAG, "Title:" + emailEntry.get("title") + ", has a length of " + emailEntry.getString("title").length());
                            sPrefs.put("mail" + Integer.toString(i) + ".title", emailEntry.getString("title").substring(0, 78) + "…");
                        } else
                            sPrefs.put("mail" + Integer.toString(i) + ".title", emailEntry.getString("title"));

                        sPrefs.put("mail" + Integer.toString(i) + ".sender", emailEntry.getString("sender"));
                        sPrefs.put("mail" + Integer.toString(i) + ".date", emailEntry.getString("date"));
                        sPrefs.put("mail" + Integer.toString(i) + ".link", emailEntry.getString("link"));
                    }
                    sPrefs.put("isMailDone", "true");
                } catch (JSONException e) {
                    Log.e(LOG_TAG, "Malformed Mail JSON!");
                }
            }
        };

        new Thread(runnable).start();

    }

    public void setMailEntry(int pos){
        if (isOnline()){
            if (sPrefs.getString("mailentry" + Integer.toString(pos) + ".body") == null){

                final SecurePreferences sPrefs = this.sPrefs;
                final String link = sPrefs.getString("mail" + Integer.toString(pos) + ".link");
                RunnableFuture runnable = new FutureTask<>(new Callable<Boolean>() {
                    @Override
                    public Boolean call() {
                        DataManager dataManager = new DataManager(sPrefs);
                        String keyValues[] = {"data", "link"};
                        String valueValues[] = {"getMailEntry", link};
                        JSONArray entry = dataManager.getArray(keyValues, valueValues);
                        try {
                            Log.v(LOG_TAG, (String) entry.getJSONArray(0).get(0));
                        } catch (JSONException e){
                            e.printStackTrace();
                        }
                        return true;
                    }
                });

                new Thread(runnable).start();
            }
        } else { Log.e("DataManager", "Cannot get mail entry due to being offline!"); }
    }

    public Boolean setProfilePic(final String username, final String password) {
        RunnableFuture runnable = new FutureTask<>(new Callable<Boolean>() {
            @Override
            public Boolean call() {
                BufferedOutputStream fos = null;
                try {
                    DataManager dataManager = new DataManager(username, password);
                    fos = new BufferedOutputStream(context.openFileOutput("profile.jpg", Context.MODE_PRIVATE));
                    byte[] decodedString = Base64.decode(dataManager.getResource("getPhoto").getBytes(), Base64.DEFAULT);
                    fos.write(decodedString);
                    Log.v(LOG_TAG, context.getFileStreamPath("profile.jpg").toString());
                    fos.flush();
                    fos.close();
                    return true;


                } catch (Exception e) {

                    Log.e(LOG_TAG, e.getMessage());
                } finally {
                    if (fos != null) {
                        fos = null;
                    }
                }
                return false;
            }

        });

        new Thread(runnable).start();

        try {
            return (Boolean) runnable.get();
        } catch (InterruptedException | ExecutionException e) { e.printStackTrace(); }

        return false; // Execution should never reach this point!
    }

    /**
     * This method should be called before applying the current string array params to cache.
     * Checks if the string values are equal.
     * @param sharedPrefTag Identifier for logging purposes.
     * @param refSize Size to be compared with, Usually the size of one of the params which is reliably accurate (i.e. titles)
     * @param isFirstOfStringArrayTitles Used for logging purposes.
     * @param params Series of String arrays for comparison
     * @return true if the params have equal size, false if they don't.
     */
    public boolean verifyRssCache(String sharedPrefTag, int refSize, boolean isFirstOfStringArrayTitles, String[]... params){
        int numParams = params.length;
        boolean isValid = true;
        int[] paramSizes = new int[numParams]; // Used for logging purposes.
        int numInvalid = 0;

        // Get all the sizes of our string params...
        for (int i = (isFirstOfStringArrayTitles) ? 1 : 0; i < numParams; i++) {
            paramSizes[i] = params[i].length;
            if (paramSizes[i] != refSize) {
                isValid = false;
                numInvalid++;
            }
        }

        if (isValid){
            Log.i(LOG_TAG, "Rss Cache (" + sharedPrefTag + ") was successfully verified.");
            for (int i = (isFirstOfStringArrayTitles) ? 1 : 0; i < numParams; i++)
                Log.v(LOG_TAG, "Size of " + params[0][i - 1] + ": " + paramSizes[i]);

            return true;
        } else {
            Log.e(LOG_TAG, "RSS Array sizes are not equal!");
            Log.e(LOG_TAG, "Reference size: " + refSize);
            if (isFirstOfStringArrayTitles){
                for (int i = 0; i < numParams; i++)
                    if (paramSizes[i] != refSize) {
                        Log.e(LOG_TAG, params[0][i - 1] + " has a size of " + paramSizes[i] + "!");
                        for (int x = 0; x < paramSizes[i]; x++)
                            Log.e(LOG_TAG, params[0][i - 1] + " element " + x + ": " + params[i][x]);
                    }
            } else {
                Log.e(LOG_TAG, "Title array not provided, there are " + numInvalid + " incorrect RSS sizes!");
            }
            return false;
        }

    }

    private class CacheData extends AsyncTask<Void, Void, Boolean> {
        private String username;
        private String password;

        public CacheData(String username, String password){
            this.username = username;
            this.password = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            // TODO: Objectify this (No static)
            // Ironically enough, the only thing i can't objectify is the thing that was objectified...
            SecurePreferences p  = MainActivity.preferences;
            DataManager dataManager = new DataManager(username, password);
            p.put("numClasses", dataManager.getResource("getNumClasses"));
            JSONArray scheduleArray = dataManager.getArray("getSchedule"); // Retrieves data from the Schedule page
            JSONArray classDataArray = dataManager.getArray("getUserData"); // Retrieves data from each teacher's page

            /*
            The format of the name is as follows:
            (Period Number).(Data Name)(Interval).(Data Name).Interval... and so forth.
            For example,if you were to request the first period's teacher, since the data is
            across all four quarters, and is directly a property of the class itself, you would
            want to request as such, excluding quotes:
                "1.teacher"
            If you wished for an individual grade, you need to know the class period, quarter, and
            index of the grade. For example, if you wish to know the 15th assignment's name of the
            3rd quarter in the 8th period, you would request it as such, again, excluding quotes:
                "8.quarter3.entry15.title"
             */

            for (int i = 0; i < Integer.valueOf(p.getString("numClasses")); i++) {
                String numClass = Integer.toString(i);
                try {
                    JSONObject tempJSONArray = ((JSONObject) scheduleArray.get(i));
                    p.put(numClass + ".class", tempJSONArray.getString("class"));
                    Log.i(LOG_TAG, "Current Class being processed: " + tempJSONArray.getString("class"));
                    p.put(numClass + ".period", Integer.toString(((JSONObject) scheduleArray.get(i)).getInt("period")));
                    p.put(numClass + ".day", tempJSONArray.getString("day"));
                    p.put(numClass + ".currentAvg", tempJSONArray.getString("currentAvg"));
                    for (int x = 0; x < 4; x++)
                        p.put(numClass + ".quarter" + Integer.toString(x), String.valueOf(((JSONObject) scheduleArray.get(i)).getJSONArray("quarter").getBoolean(x)));
                    p.put(numClass + ".teacher", tempJSONArray.getString("teacher"));
                    p.put(numClass + ".room", tempJSONArray.getString("room"));
                    for (int x = 0; x < 4; x++)
                        p.put(numClass + ".pastAvgs" + Integer.toString(x), String.valueOf(((JSONObject) scheduleArray.get(i)).getJSONArray("pastAvgs").getDouble(x)));
                    p.put(numClass + ".teacherID", tempJSONArray.getString("teacherID"));
                    p.put(numClass + ".classID", tempJSONArray.getString("classID"));

                    int numQuarters = 0;

                    for (int x = 0; x < 4; x++) {
                        String quarter = Integer.toString(x);
                        p.put(numClass + ".quarter" + quarter + ".numEntries", Integer.toString(((JSONObject) (((JSONArray) classDataArray.get(i)).get(x))).getInt("numEntries")));

                        if (((JSONObject) (((JSONArray) classDataArray.get(i)).get(x))).getInt("numEntries") != 0)
                            numQuarters++;

                        for (int y = 0; y < ((JSONObject) (((JSONArray) classDataArray.get(i)).get(x))).getInt("numEntries"); y++) {
                            String numEntry = Integer.toString(y);

                            JSONObject tempJSONObject = ((JSONObject) ((JSONArray) classDataArray.get(i)).get(x)).getJSONObject(numEntry);

                            p.put(numClass + ".quarter" + quarter + ".entry" + numEntry + ".date",
                                    tempJSONObject.getString("date"));

                            p.put(numClass + ".quarter" + quarter + ".entry" + numEntry + ".title",
                                    tempJSONObject.getString("title"));

                            p.put(numClass + ".quarter" + quarter + ".entry" + numEntry + ".type",
                                    tempJSONObject.getString("type"));

                            p.put(numClass + ".quarter" + quarter + ".entry" + numEntry + ".ptsGot",
                                    tempJSONObject.getString("ptsGot"));

                            p.put(numClass + ".quarter" + quarter + ".entry" + numEntry + ".ptsTotal",
                                    tempJSONObject.getString("ptsTotal"));

                            p.put(numClass + ".quarter" + quarter + ".entry" + numEntry + ".scale",
                                    tempJSONObject.getString("scale"));

                        }
                        p.put(numClass + ".quarter" + quarter + ".quarterGrade",
                                ((JSONObject) ((JSONArray) classDataArray.get(i)).get(x)).getString("quarterGrade"));

                    }

                    Log.d(LOG_TAG, "Number of Entries/Quarter: "
                            + "\t"
                            + p.getString(numClass + ".quarter0" + ".numEntries")
                            + "\t"
                            + p.getString(numClass + ".quarter1" + ".numEntries")
                            + "\t"
                            + p.getString(numClass + ".quarter2" + ".numEntries")
                            + "\t"
                            + p.getString(numClass + ".quarter3" + ".numEntries"));

                    p.put(numClass + ".numQuarters", Integer.toString(numQuarters));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            // Sets the data as done, so that user requests won't return a "Not done" message.
            p.put("isDataDone", "true");
            return true;
        }
    }

}


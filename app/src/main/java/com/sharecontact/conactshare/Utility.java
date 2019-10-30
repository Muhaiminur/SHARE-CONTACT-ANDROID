package com.sharecontact.conactshare;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.TransitionDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.TrafficStats;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.StatFs;
import android.os.StrictMode;
import android.telephony.SmsMessage;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import static android.content.Context.ACTIVITY_SERVICE;

/**
 * Created by fojlesaikat on 8/24/17.
 */

public class Utility {

    Context context;
    ProgressDialog mProgressDialog;

    public native String getAuthorizationKey();

    public native String getBaseUrl();


    public Utility(Context context) {
        this.context = context;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        freeMemory();
    }

    public void writeLanguage(String language) {
        SharedPreferences sharedPref = context.getSharedPreferences("LANGUAGE", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("language", language);
        editor.commit();
    }

    public String getLangauge() {
        SharedPreferences sharedPref = context.getSharedPreferences("LANGUAGE", Context.MODE_PRIVATE);
        return sharedPref.getString("language", "en");
    }

    private SmsMessage getIncomingMessage(Object aObject, Bundle bundle) {
        SmsMessage currentSMS;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String format = bundle.getString("format");
            currentSMS = SmsMessage.createFromPdu((byte[]) aObject, format);
        } else {
            currentSMS = SmsMessage.createFromPdu((byte[]) aObject);
        }
        return currentSMS;
    }


    /*
   ================ Show Progress Dialog ===============
   */
    public void showProgress(boolean isCancelable, String message) {
        mProgressDialog = new ProgressDialog(context, R.style.AppCompatAlertDialogStyle);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(isCancelable);
        mProgressDialog.setMessage(message);
        mProgressDialog.show();
    }

    /*
    ================ Hide Progress Dialog ===============
    */
    public void hideProgress() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }





    /*
    ================ Show Toast Message ===============
    */
    public void showToast(String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();
    }

    /*
    =============== Set Window FullScreen ===============
    */
    public void setFullScreen() {
        Activity activity = ((Activity) context);
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }


    /*
    ================ Log function ===============
     */
    public void logger(String message) {
        Log.d(context.getString(R.string.app_name), message);
        long currentTime = System.currentTimeMillis();
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
        String date = sdf.format(new Date());
        //writeToFile(date+" -> "+message);
    }


    public void clearText(View[] view) {
        for (View v : view) {
            if (v instanceof EditText) {
                ((EditText) v).setText("");
            } else if (v instanceof Button) {
                ((Button) v).setText("");
            } else if (v instanceof TextView) {
                ((TextView) v).setText("");
            }
        }
    }

    /*
    ================ Hide Keyboard from Screen ===============
    */
    public void hideKeyboard(View view) {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }


    /*
    ================ Show Keyboard to Screen ===============
    */
    public void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    /*
    ================ Hide & Show Views ===============
    */
    public void hideAndShowView(View[] views, View view) {
        for (int i = 0; i < views.length; i++) {
            views[i].setVisibility(View.GONE);
        }
        view.setVisibility(View.VISIBLE);
    }

    public void hideViews(View[] views) {
        for (int i = 0; i < views.length; i++) {
            views[i].setVisibility(View.GONE);
        }
    }

//    private long getNextSchedule() {
//        try {
//            String[] schedules = context.getResources().getStringArray(R.array.schedules);
//            long timeDistance = 0;
//            Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
//            long currentTimeInMillis = calendar.getTimeInMillis();
//            for (int i = 0; i < schedules.length; i++) {
//                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
//                sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
//                String date = sdf.format(new Date()) + " " + schedules[i];
//                SimpleDateFormat sdf2 = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss", Locale.ENGLISH);
//                Date formattedDate = sdf2.parse(date);
//                long timeInMillis = formattedDate.getTime();
//                long distance = timeInMillis - currentTimeInMillis;
//                if (distance >= 0) {
//                    if (timeDistance != 0) {
//                        if (distance < timeDistance) {
//                            timeDistance = distance;
//                        }
//                    } else {
//                        timeDistance = distance;
//                    }
//                }
//            }
//            return timeDistance;
//        } catch (Exception ex) {
//            logger(ex.toString());
//            return 0;
//        }
//    }

    /*
    ================ Write to file ============
    */
//    public void writeToFile(String message) {
///*        try {
//            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss");
//            String date = sdf.format(new Date());
//            String value = date + " -> "+ message+"\n";
//            if (isExternalStorageWritable() && isExternalStorageReadable()) {
//                File directory = new File(Environment.getExternalStorageDirectory().getPath() + "/RadioG");
//                if (!directory.exists()) {
//                    if(directory.mkdirs()){
//                        logger("Directory Created");
//                    }
//                    else{
//                        logger("Directory Not Created");
//                    }
//                }
//                File file = new File(directory, "radiog.txt");
//                FileOutputStream fileOutputStream = new FileOutputStream(file, true);
//                fileOutputStream.write(value.getBytes());
//                fileOutputStream.close();
//            } else {
//                logger("External/Internal Storage is not available");
//            }
//        }
//        catch (Exception ex){
//            logger(ex.toString());
//        }*/
//    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state);
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
    }

    /*public File getAlbumStorageDir(String albumName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), albumName);
        if (!file.mkdirs()) {
            logger("Directory not created");
        }
        return file;
    }*/

//    public void setAlarm(int hour, int minute, int requestId) {
//        Intent intent = new Intent(context, AlarmReceiver.class);
//        boolean alarmUp = (PendingIntent.getBroadcast(context, requestId, intent, PendingIntent.FLAG_NO_CREATE) != null);
//        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestId, intent, 0);
//        if (!alarmUp) {
//            Calendar calendar = Calendar.getInstance();
//            calendar.setTimeInMillis(System.currentTimeMillis());
//            calendar.set(Calendar.HOUR_OF_DAY, hour);
//            calendar.set(Calendar.MINUTE, minute);
//            if (calendar.before(Calendar.getInstance())) {
//                calendar.add(Calendar.DATE, 1);
//            }
//            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
//            logger("Next " + requestId + " Trigger After " + 2 * 60 + " Sec(s)");
//            writeToFile("Setting Next Alarm on " + hour + ":" + minute);
//        } else {
//            writeToFile("Active Alarm " + hour + ":" + minute);
//        }
//    }

    public boolean isExternalMemoryAvailable() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    public long getAvailableInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = stat.getBlockSizeLong();
        }
        long availableBlocks = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            availableBlocks = stat.getAvailableBlocksLong();
        }
        return (availableBlocks * blockSize);
    }

    public long getTotalInternalMemorySize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = stat.getBlockSizeLong();
        } else {
            blockSize = stat.getBlockSize();
        }
        long totalBlocks = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            totalBlocks = stat.getBlockCountLong();
        } else {
            totalBlocks = stat.getBlockCount();
        }
        return (totalBlocks * blockSize);
    }

    public String getAvailableExternalMemorySize() {
        if (isExternalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                blockSize = stat.getBlockSizeLong();
            } else {
                blockSize = stat.getBlockSize();
            }
            long availableBlocks = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                availableBlocks = stat.getAvailableBlocksLong();
            } else {
                availableBlocks = stat.getAvailableBlocks();
            }
            return formatSize(availableBlocks * blockSize);
        } else {
            return "";
        }
    }

    public String getTotalExternalMemorySize() {
        if (isExternalMemoryAvailable()) {
            File path = Environment.getExternalStorageDirectory();
            StatFs stat = new StatFs(path.getPath());
            long blockSize = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                blockSize = stat.getBlockSizeLong();
            } else {
                blockSize = stat.getBlockSize();
            }
            long totalBlocks = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
                totalBlocks = stat.getBlockCountLong();
            } else {
                totalBlocks = stat.getBlockCount();
            }
            return formatSize(totalBlocks * blockSize);
        } else {
            return "";
        }
    }

    public static String formatSize(long size) {
        String suffix = null;
        if (size >= 1024) {
            suffix = "KB";
            size /= 1024;
            if (size >= 1024) {
                suffix = "MB";
                size /= 1024;
            }
        }
        StringBuilder resultBuffer = new StringBuilder(Long.toString(size));
        int commaOffset = resultBuffer.length() - 3;
        while (commaOffset > 0) {
            resultBuffer.insert(commaOffset, ',');
            commaOffset -= 3;
        }
        if (suffix != null) resultBuffer.append(suffix);
        return resultBuffer.toString();
    }

    /*
   ================ Check File Size ===============
   */
    public long checkFileSize(String fileUrl) {
        long file_size = 0;
        try {
            URL url = new URL(fileUrl);
            URLConnection urlConnection = url.openConnection();
            urlConnection.connect();
            file_size = urlConnection.getContentLength();
        } catch (Exception ex) {
            logger(ex.toString());
        }
        return file_size;
    }


    /*
   ================ Check File Exists ===============
   */
//    public boolean checkIfSongExists(SongModel songModel) {
//        long file_size = 0;
//        try {
//            File directory = new File(context.getFilesDir(), "RadioG");
//            if (!directory.exists()) {
//                if (directory.mkdir()) {
//                    logger("Directory Created");
//                } else {
//                    logger("Directory Not Created");
//                }
//            }
//            File file = new File(directory.getAbsolutePath() + "/" + songModel.getId() + ".mp3");
//            if (file.exists()) {
////                long fileSize = checkFileSize(context.getString(R.string.image_url)+songModel.getLink());
////                if(file.length()<file_size){
////                    file.delete();
////                }
//                return true;
//            } else {
//                return false;
//            }
//        } catch (Exception ex) {
//            logger(ex.toString());
//            return false;
//        }
//    }

    public Uri ussdToCallableUri(String ussd) {

        String uriString = "";

        if (!ussd.startsWith("tel:"))
            uriString += "tel:";

        for (char c : ussd.toCharArray()) {

            if (c == '#')
                uriString += Uri.encode("#");
            else
                uriString += c;
        }

        return Uri.parse(uriString);
    }

    public String convertSecondsToHour(long seconds) {
        String second = String.valueOf(seconds % 60);
        String minute = String.valueOf((seconds / 60) % 60);
        String hour = String.valueOf((seconds / 60 / 60) % 60);
        if (second.length() < 2) {
            second = "0" + second;
        }
        if (minute.length() < 2) {
            minute = "0" + minute;
        }
        if (hour.length() < 2) {
            hour = "0" + hour;
        }
        return hour + ":" + minute + ":" + second;
    }


    public HashMap<String, Long> getNetworkInfo() {
        HashMap<String, Long> map = new HashMap<>();
        ActivityManager manager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = manager.getRunningAppProcesses();
        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(
                PackageManager.GET_META_DATA);
        for (ApplicationInfo packageInfo : packages) {
            //get the UID for the selected app
            if (packageInfo.packageName.equals(context.getPackageName())) {
                int uid = packageInfo.uid;
                long received = TrafficStats.getUidRxBytes(uid);
                long send = TrafficStats.getUidTxBytes(uid);
                map.put("send", send /*+ getDataUsage("totalSend")*/);
                map.put("received", received /*+ getDataUsage("totalReceived")*/);
//                writeDataUsage(send, received);
                Log.v("" + uid, "Send :" + send + ", Received :" + received);
                return map;
            }
        }
        map.put("Send", Long.parseLong("0"));
        map.put("Received", Long.parseLong("0"));
        return map;
    }

//    public void writeSubscriptionStatus(int trackId, Master master) {
//        Gson gson = new GsonBuilder().create();
//        SharedPreferences sharedPref = context.getSharedPreferences("SUBS_INFO", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPref.edit();
//        editor.putString(String.valueOf(trackId), gson.toJson(master));
//        editor.commit();
//    }

    /*
  =============== Check Version ===============
  */
    public boolean checkVersion(int versionCode) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            int currentCode = packageInfo.versionCode;
            return currentCode < versionCode;
        } catch (Exception ex) {
            return false;
        }
    }

    public boolean isSubscribed(int trackId) {
        try {
            SharedPreferences sharedPref = context.getSharedPreferences("SUBS_INFO", Context.MODE_PRIVATE);
            String value = sharedPref.getString(String.valueOf(trackId), "{}");
            JSONObject jsonObject = new JSONObject(value);
            if (getMdn().equals("17")) {
                return (System.currentTimeMillis() <= Long.parseLong(jsonObject.optString("expiry")));
            } else {
                Map<String, ?> allEntries = sharedPref.getAll();
                for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
                    value = sharedPref.getString(entry.getKey(), "{}");
                    break;
                }
                jsonObject = new JSONObject(value);
                return (System.currentTimeMillis() <= Long.parseLong(jsonObject.optString("expiry")));
            }
        } catch (Exception ex) {
            logger(ex.toString());
            return false;
        }
    }

    public void clearSubscription() {
        SharedPreferences sharedPref = context.getSharedPreferences("SUBS_INFO", Context.MODE_PRIVATE);
        Map<String, ?> allEntries = sharedPref.getAll();
        SharedPreferences.Editor editor = sharedPref.edit();
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            editor.putString(entry.getKey(), "{}");
        }
        editor.commit();
    }

//    public String getPrice(int trackId) {
//        try {
//            SharedPreferences sharedPref = context.getSharedPreferences("SUBS_INFO", Context.MODE_PRIVATE);
//            String value = sharedPref.getString(String.valueOf(trackId), "{}");
//            JSONObject jsonObject = new JSONObject(value);
//            String price = jsonObject.optString("price");
//            return getLangauge().equals("bn")?convertToBangle(price):price;
//        }
//        catch (Exception ex){
//            logger(ex.toString());
//            return "0.00";
//        }
//    }


//    public void writeMsisdn(Msisdn msisdn) {
//        SharedPreferences sharedPref = context.getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPref.edit();
//        editor.putInt("id", msisdn.getId());
//        editor.putString("msisdn", String.valueOf(msisdn.getMsisdn()));
//        editor.putString("comment", msisdn.getComment());
//        editor.commit();
//        clearSubscription();
//    }

    public String getMsisdn() {
        SharedPreferences sharedPref = context.getSharedPreferences("USER_INFO", Context.MODE_PRIVATE);
        return sharedPref.getString("msisdn", "8800000000000");
    }

    public void freeMemory() {
        System.runFinalization();
        Runtime.getRuntime().gc();
        System.gc();
    }

    public String getCountryName() {
        SharedPreferences sharedPref = context.getSharedPreferences("COUNTRY", Context.MODE_PRIVATE);
        return sharedPref.getString("name", "");
    }

    public String getCountryCode() {
        SharedPreferences sharedPref = context.getSharedPreferences("COUNTRY", Context.MODE_PRIVATE);
        return sharedPref.getString("code", "");
    }

    public void setCountryName(String name, String code) {
        SharedPreferences sharedPref = context.getSharedPreferences("COUNTRY", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("name", name);
        editor.putString("code", code);
        editor.commit();
    }

    public HashMap<String, String> getDeviceInfo() {
        HashMap<String, String> map = new HashMap<>();
        map.put("Serial", Build.SERIAL);
        map.put("Model", Build.MODEL);
        //map.put("Id", Build.ID);
        map.put("Id", Build.SERIAL);
        map.put("Manufacture", Build.MANUFACTURER);
        map.put("Type", Build.TYPE);
        map.put("User", Build.USER);
        map.put("Base", String.valueOf(Build.VERSION_CODES.BASE));
        map.put("Incremental", Build.VERSION.INCREMENTAL);
        map.put("Board", Build.BOARD);
        map.put("Brand", Build.BRAND);
        map.put("Host", Build.HOST);
        map.put("Version Code", Build.VERSION.RELEASE);
        return map;
    }

    public String getMdn() {
        String msisdnValue = getMsisdn();
        return msisdnValue.substring(3, 5);
    }

    /*public boolean operatorExisted(){
        String msisdnValue = getMsisdn();
        String mdn = msisdnValue.substring(3,5);
        String[] operator = context.getResources().getStringArray(R.array.operator);
        for(int i=0; i<operator.length; i++){
            if(mdn.equals(operator[i])){
                return true;
            }
        }
        return false;
    }*/

    public boolean validateMsisdn(String msisdn) {
        if (msisdn.length() != 11) {
            return false;
        }
        return !msisdn.substring(0, 3).equals("010") && !msisdn.substring(0, 3).equals("011") && !msisdn.substring(0, 3).equals("012");
    }

    public void setFirebaseToken(String token) {
        SharedPreferences sharedPref = context.getSharedPreferences("FCM", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("token", token);
        editor.commit();
    }

    public String getFirebaseToken() {
        SharedPreferences sharedPref = context.getSharedPreferences("FCM", Context.MODE_PRIVATE);
        return sharedPref.getString("token", "");
    }



/*    private void showConfirmation(){
        Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        HashMap<String, Integer> screen = getScreenRes();
        int width = screen.get(KeyWord.SCREEN_WIDTH);
        int height = screen.get(KeyWord.SCREEN_HEIGHT);
        int mywidth = (width / 10) * 7;
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.dialog_layout);
        TextView tv = (TextView) dialog.findViewById(R.id.permission_message);
        Button yes = (Button) dialog.findViewById(R.id.dialog_yes);
        Button no = (Button) dialog.findViewById(R.id.dialog_no);
        switch (getMdn()){
            case "16":
                tv.setText(getLangauge().equals("bn") ? context.getString(R.string.robi_confirmation_msg_bn) : context.getString(R.string.robi_confirmation_msg_en));
                break;
            case "17":
                tv.setText(getLangauge().equals("bn") ? context.getString(R.string.gp_confirmation_msg_bn) : context.getString(R.string.gp_confirmation_msg_en));
                break;
            case "18":
                tv.setText(getLangauge().equals("bn") ? context.getString(R.string.robi_confirmation_msg_bn) : context.getString(R.string.robi_confirmation_msg_en));
                break;
            case "19":
                tv.setText(getLangauge().equals("bn") ? context.getString(R.string.banglalink_confirmation_msg_bn) : context.getString(R.string.banglalink_confirmation_msg_en));
                break;
        }
        //tv.setText(getLangauge().equals("bn") ? context.getString(R.string.meesage_bn) : context.getString(R.string.meesage_en));
        LinearLayout ll = (LinearLayout) dialog.findViewById(R.id.dialog_layout_size);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) ll.getLayoutParams();
        params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        params.width = mywidth;
        ll.setLayoutParams(params);
        yes.setText(getLangauge().equals("bn") ? context.getString(R.string.ok_bn) : context.getString(R.string.ok_en));
        no.setText(getLangauge().equals("bn") ? context.getString(R.string.no) : "No");
        no.setVisibility(View.GONE);
        ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams)yes.getLayoutParams();
        param.setMargins(10, 5, 10, 5);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                checkMasterSubscription(0);
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    private void showDeactivationConfirmation(){
        Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        HashMap<String, Integer> screen = getScreenRes();
        int width = screen.get(KeyWord.SCREEN_WIDTH);
        int height = screen.get(KeyWord.SCREEN_HEIGHT);
        int mywidth = (width / 10) * 7;
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.dialog_layout);
        TextView tv = (TextView) dialog.findViewById(R.id.permission_message);
        Button yes = (Button) dialog.findViewById(R.id.dialog_yes);
        Button no = (Button) dialog.findViewById(R.id.dialog_no);
        tv.setText(getLangauge().equals("bn") ? context.getString(R.string.meesage_bn) : context.getString(R.string.meesage_en));
        LinearLayout ll = (LinearLayout) dialog.findViewById(R.id.dialog_layout_size);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) ll.getLayoutParams();
        params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        params.width = mywidth;
        ll.setLayoutParams(params);
        yes.setText(getLangauge().equals("bn") ? context.getString(R.string.ok_bn) : context.getString(R.string.ok_en));
        no.setText(getLangauge().equals("bn") ? context.getString(R.string.no) : "No");
        no.setVisibility(View.GONE);
        ViewGroup.MarginLayoutParams param = (ViewGroup.MarginLayoutParams)yes.getLayoutParams();
        param.setMargins(10, 5, 10, 5);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                checkMasterSubscription(0);
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    public void showPremiumDialog(){
        Display display = ((Activity)context).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        HashMap<String, Integer> screen = getScreenRes();
        int width = screen.get(KeyWord.SCREEN_WIDTH);
        int height = screen.get(KeyWord.SCREEN_HEIGHT);
        int mywidth = (width / 10) * 7;
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.dialog_layout);
        TextView tv = (TextView) dialog.findViewById(R.id.permission_message);
        Button yes = (Button) dialog.findViewById(R.id.dialog_yes);
        Button no = (Button) dialog.findViewById(R.id.dialog_no);
        switch (getMdn()){
            case "18":
                tv.setText(getLangauge().equals("bn") ? context.getString(R.string.robi_premium_message_bn) : context.getString(R.string.robi_premium_message_en));
                break;
            case "16":
                tv.setText(getLangauge().equals("bn") ? context.getString(R.string.robi_premium_message_bn) : context.getString(R.string.robi_premium_message_en));
                break;
            case "17":
                tv.setText(getLangauge().equals("bn") ? context.getString(R.string.gp_premium_message_bn) : context.getString(R.string.gp_premium_message_en));
                break;
            case "19":
                tv.setText(getLangauge().equals("bn") ? context.getString(R.string.bangalink_premium_message_bn) : context.getString(R.string.banglalink_premium_message_en));
                break;
        }
        LinearLayout ll = (LinearLayout) dialog.findViewById(R.id.dialog_layout_size);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) ll.getLayoutParams();
        params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        params.width = mywidth;
        ll.setLayoutParams(params);
        yes.setText(getLangauge().equals("bn") ? context.getString(R.string.yes) : "Yes");
        no.setText(getLangauge().equals("bn") ? context.getString(R.string.no) : "No");
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                activateSubscription("0");
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }

    public void makeSubscriptionDialog(boolean showInterface) {
        try {
            final Dialog dialog = new Dialog(context);
            HashMap<String, Integer> screenRes = getScreenRes();
            int width = screenRes.get(KeyWord.SCREEN_WIDTH);
            int height = screenRes.get(KeyWord.SCREEN_HEIGHT);
            int mywidth = (width / 10) * 8;
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.number_layout);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            LinearLayout numberLayout = (LinearLayout) dialog.findViewById(R.id.number_layout);
            TextView title = (TextView) dialog.findViewById(R.id.subscription_title);
            EditText phoneNumber = (EditText) dialog.findViewById(R.id.phone_number);
            Button cancelBtn = (Button) dialog.findViewById(R.id.rating_btn_cancel);
            Button submitBtn = (Button) dialog.findViewById(R.id.rating_btn_submit);
            ViewGroup.LayoutParams params = numberLayout.getLayoutParams();
            params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            params.width = mywidth;
            numberLayout.setLayoutParams(params);
            setFonts(new View[]{title, cancelBtn, submitBtn});
            title.setText(getLangauge().equals("bn") ? context.getString(R.string.number_msg_bn) : context.getString(R.string.number_msg_en));
            cancelBtn.setText(getLangauge().equals("bn") ? context.getString(R.string.number_cancel_btn_bn) : context.getString(R.string.number_cancel_btn_en));
            submitBtn.setText(getLangauge().equals("bn") ? context.getString(R.string.number_submit_btn_bn) : context.getString(R.string.number_submit_btn_en));
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            submitBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    String msisdn = "8801"+phoneNumber.getText().toString();
                    String message = validateMsisdn(msisdn);
                    if(message.equals("OK")){
                        Msisdn mdn = new Msisdn();
                        mdn.setId(1);
                        mdn.setMsisdn(Long.parseLong(msisdn));
                        mdn.setComment("User Prompt");
                        writeMsisdn(mdn);
                        //showToast("Number Set");
                        if(showInterface) {
                            subscriptionInterfacce.numberSet();
                        }
                    }
                    else{
                        showToast(message);
                    }
                }
            });
            dialog.setCancelable(false);
            dialog.show();
        }
        catch (Exception ex){
            showToast(ex.toString());
        }
    }

    private void validatePinDialog() {
        try {
            final Dialog dialog = new Dialog(context);
            HashMap<String, Integer> screenRes = getScreenRes();
            int width = screenRes.get(KeyWord.SCREEN_WIDTH);
            int height = screenRes.get(KeyWord.SCREEN_HEIGHT);
            int mywidth = (width / 10) * 8;
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.number_layout);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            LinearLayout numberLayout = (LinearLayout) dialog.findViewById(R.id.number_layout);
            TextView title = (TextView) dialog.findViewById(R.id.subscription_title);
            EditText phoneNumber = (EditText) dialog.findViewById(R.id.phone_number);
            Button cancelBtn = (Button) dialog.findViewById(R.id.rating_btn_cancel);
            Button submitBtn = (Button) dialog.findViewById(R.id.rating_btn_submit);
            TextView phoneCode = (TextView) dialog.findViewById(R.id.phone_code);
            phoneCode.setVisibility(View.GONE);
            ViewGroup.LayoutParams params = numberLayout.getLayoutParams();
            params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            params.width = mywidth;
            numberLayout.setLayoutParams(params);
            setFonts(new View[]{title, cancelBtn, submitBtn});
            title.setText(getLangauge().equals("bn") ? context.getString(R.string.number_pin_bn) : context.getString(R.string.number_pin_en));
            phoneNumber.setHint("PIN e.g. XXXX");
            cancelBtn.setText(getLangauge().equals("bn") ? context.getString(R.string.number_cancel_btn_bn) : context.getString(R.string.number_cancel_btn_en));
            submitBtn.setText(getLangauge().equals("bn") ? context.getString(R.string.number_submit_btn_bn) : context.getString(R.string.number_submit_btn_en));
            cancelBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
            submitBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String pin = phoneNumber.getText().toString();
                    if(pin.length()>0) {
                        dialog.dismiss();
                        activateSubscription(phoneNumber.getText().toString());
                    }
                    else{
                        showToast("Pin Required");
                    }
                }
            });
            dialog.setCancelable(false);
            dialog.show();
        }
        catch (Exception ex){
            showToast(ex.toString());
        }
    }


    private void activateSubscription(String pin){
        showProgress(false);
        try{
            Call<List<Master>> call = masterApiInterface.activation(context.getString(R.string.authorization_key), getMsisdn(), getFirebaseToken(), "0", pin);
            call.enqueue(new Callback<List<Master>>() {
                @Override
                public void onResponse(Call<List<Master>> call, Response<List<Master>> response) {
                    hideProgress();
                    if(response.isSuccessful()&&response.code()==200){
                        List<Master> masters = response.body();
                        Master master = masters.get(0);
                        if(getMdn().equals("18")){
                            showConfirmation();
                        }
                        else {
                            if (master.getComment().equals("PIN Request Success")) {
                                if (getMdn().equals("19")||getMdn().equals("17")) {
                                    validatePinDialog();
                                } else {
                                    showConfirmation();
                                }
                            } else if (master.getComment().equals("Charge Request Success")) {
                                showConfirmation();
                            } else {
                                showToast("PIN Process Failed");
                            }
                        }
                    }
                    else{
                        logger("Response is not successfull");
                    }
                }

                @Override
                public void onFailure(Call<List<Master>> call, Throwable t) {
                    hideProgress();
                    logger(t.toString());
                }
            });
        }
        catch (Exception ex){
            hideProgress();
        }
    }

    public void deactivateSubscription(){
        showProgress(false);
        try{
            Call<List<Master>> call = masterApiInterface.deactivation(context.getString(R.string.authorization_key), getMsisdn(), getFirebaseToken());
            call.enqueue(new Callback<List<Master>>() {
                @Override
                public void onResponse(Call<List<Master>> call, Response<List<Master>> response) {
                    hideProgress();
                    if(response.isSuccessful()&&response.code()==200){
                        List<Master> masters = response.body();
                        Master master = masters.get(0);
                        writeSubscriptionStatus(0,master);
                        if(master.getExpiry().equals("0")) {
                            showDeactivationConfirmation();
                        }
                        else{
                            showToast("Deactivation Failed");
                        }
                    }
                    else{
                        logger("Response is not successfull");
                    }
                }

                @Override
                public void onFailure(Call<List<Master>> call, Throwable t) {
                    hideProgress();
                    logger(t.toString());
                }
            });
        }
        catch (Exception ex){
            hideProgress();
        }
    }

    public void checkMasterSubscription(int trackId){
        showProgress(false);
        try{
            Call<List<Master>> call = masterApiInterface.viewstatus(context.getString(R.string.authorization_key), getMsisdn(), getFirebaseToken(), String.valueOf(trackId));
            call.enqueue(new Callback<List<Master>>() {
                @Override
                public void onResponse(Call<List<Master>> call, Response<List<Master>> response) {
                    hideProgress();
                    if(response.isSuccessful()&&response.code()==200){
                        List<Master> masters = response.body();
                        Master master = masters.get(0);
                        writeSubscriptionStatus(trackId, master);
                        subscriptionInterfacce.viewStatus();
                    }
                    else{
                        logger("Response is not successfull");
                    }
                }

                @Override
                public void onFailure(Call<List<Master>> call, Throwable t) {
                    hideProgress();
                    logger(t.toString());
                }
            });
        }
        catch (Exception ex){
            hideProgress();
            logger(ex.toString());
        }
    }*/

    /*public List<SongModel> filterSong(List<SongModel> songModels){
        List<SongModel> models = new ArrayList<>();
        for(int i=0; i<songModels.size(); i++){
            SongModel songModel = songModels.get(i);
            if(songModel.getId()!=0){
                models.add(songModel);
            }
        }
        return models;
    }

    public int getSongPosition(List<SongModel> songModels, int songId){
        for(int i=0; i<songModels.size(); i++){
            SongModel songModel = songModels.get(i);
            if(songModel.getId()==songId){
                return i;
            }
        }
        return -1;
    }

    public void shareTrack(String name, int trackId){
        Intent intent = new Intent(android.content.Intent.ACTION_SEND);
        intent.setType("text/plain");
        String shareBodyText = context.getString(R.string.share_url)+trackId;
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("label", shareBodyText);
        clipboardManager.setPrimaryClip(clipData);
        intent.putExtra(android.content.Intent.EXTRA_SUBJECT, name);
        intent.putExtra(android.content.Intent.EXTRA_TEXT, shareBodyText);
        context.startActivity(Intent.createChooser(intent, "Share with"));
    }*/

    public boolean isEmailValid(String email) {
        String regExpn =
                "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                        + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                        + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                        + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                        + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$";

        CharSequence inputStr = email;

        Pattern pattern = Pattern.compile(regExpn, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);

        return matcher.matches();
    }

    public boolean isAdult(int year, int month, int day) {
        Calendar userAge = new GregorianCalendar(year, month, day);
        Calendar minAdultAge = new GregorianCalendar();
        minAdultAge.add(Calendar.YEAR, -18);
        return !minAdultAge.before(userAge);
    }

    /*public void setLoggedInUser(User user) {
        SharedPreferences sharedPref = context.getSharedPreferences("USER", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("id", user.getId());
        editor.putString("firstName", user.getFirstName());
        editor.putString("lastName", user.getLastName());
        editor.putString("phone", user.getPhone());
        editor.putString("email", user.getEmail());
        editor.putString("sid", user.getSid());
        editor.putString("url", user.getUrl());
        editor.putString("dob", user.getDob());
        editor.putString("gender", user.getGender());
        editor.putString("profileimg", user.getProfileImg());
        editor.commit();
    }*/

    public void clearUser() {
        SharedPreferences sharedPref = context.getSharedPreferences("USER", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("id", "");
        editor.putString("firstName", "");
        editor.putString("lastName", "");
        editor.putString("phone", "");
        editor.putString("email", "");
        editor.putString("sid", "");
        editor.putString("url", "");
        editor.putString("dob", "");
        editor.putString("gender", "");
        editor.putString("profileimg", "");
        editor.commit();
    }

    /*public User getLoggedInUser() {
        User user = new User();
        SharedPreferences sharedPref = context.getSharedPreferences("USER", Context.MODE_PRIVATE);
        user.setId(sharedPref.getString("id", ""));
        user.setFirstName(sharedPref.getString("firstName", ""));
        user.setLastName(sharedPref.getString("lastName", ""));
        user.setPhone(sharedPref.getString("phone", ""));
        user.setEmail(sharedPref.getString("email", ""));
        user.setSid(sharedPref.getString("sid", ""));
        user.setUrl(sharedPref.getString("url", ""));
        user.setDob(sharedPref.getString("dob", ""));
        user.setGender(sharedPref.getString("gender", ""));
        user.setProfileImg(sharedPref.getString("profileimg", ""));
        return user;
    }*/

    public boolean isTokenExpired() {
        SharedPreferences sharedPref = context.getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE);
        long expireTime = sharedPref.getLong("expireIn", 0);
        return System.currentTimeMillis() >= expireTime;
    }

    public HashMap<String, Integer> getDateFromBday(String date) {
        HashMap<String, Integer> map = new HashMap<>();
        String month = date.substring(0, date.indexOf('/'));
        String day = date.substring(date.indexOf('/') + 1, date.lastIndexOf('/'));
        String year = date.substring(date.lastIndexOf('/') + 1);
        int d = day.charAt(0) == '0' ? Integer.parseInt(day.substring(1)) : Integer.parseInt(day);
        int m = month.charAt(0) == '0' ? Integer.parseInt(month.substring(1)) : Integer.parseInt(month);
        int y = Integer.parseInt(year);
        map.put("day", d);
        map.put("month", m - 1);
        map.put("year", y);
        return map;
    }

    public void preventScreenShot() {
        ((Activity) context).getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
    }

    public boolean isDeviceRooted() {
        return checkRootMethod1() || checkRootMethod2() || checkRootMethod3();
    }

    private boolean checkRootMethod1() {
        String buildTags = Build.TAGS;
        return buildTags != null && buildTags.contains("test-keys");
    }

    private boolean checkRootMethod2() {
        String[] paths = {"/system/app/Superuser.apk", "/sbin/su", "/system/bin/su", "/system/xbin/su", "/data/local/xbin/su", "/data/local/bin/su", "/system/sd/xbin/su",
                "/system/bin/failsafe/su", "/data/local/su", "/su/bin/su"};
        for (String path : paths) {
            if (new File(path).exists()) return true;
        }
        return false;
    }

    private static boolean checkRootMethod3() {
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(new String[]{"/system/xbin/which", "su"});
            BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
            return in.readLine() != null;
        } catch (Throwable t) {
            return false;
        } finally {
            if (process != null) process.destroy();
        }
    }

    public String makeFirstLetterUpperCase(String value) {
        if (value.length() == 0) {
            return "";
        }
        return value.substring(0, 1).toUpperCase() + value.substring(1);
    }

    public void setMargins(View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }

    /*public void showDialog(String message) {
        HashMap<String, Integer> screen = getScreenRes();
        int width = screen.get(KeyWord.SCREEN_WIDTH);
        int height = screen.get(KeyWord.SCREEN_HEIGHT);
        int mywidth = (width / 10) * 7;
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setContentView(R.layout.dialog_toast);
        TextView tvMessage = dialog.findViewById(R.id.tv_message);
        Button btnOk = dialog.findViewById(R.id.btn_ok);
        setFonts(new View[]{tvMessage, btnOk});
        tvMessage.setText(message);
        LinearLayout ll = dialog.findViewById(R.id.dialog_layout_size);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) ll.getLayoutParams();
        params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        params.width = mywidth;
        ll.setLayoutParams(params);
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.setCancelable(false);
        dialog.show();
    }*/

    public String getFormattedDate(int year, int month, int day) {
        month = month + 1;
        String y = String.valueOf(year);
        String m = String.valueOf(month);
        String d = String.valueOf(day);
        if (m.length() == 1) m = "0" + m;
        if (d.length() == 1) d = "0" + d;
        return y + "-" + m + "-" + d;
    }
}

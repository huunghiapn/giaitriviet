package com.nghianh.giaitriviet.util;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.view.View;
import android.view.ViewAnimationUtils;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.security.ProviderInstaller;
import com.nghianh.giaitriviet.R;
import com.nghianh.giaitriviet.SettingsFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Helper {

    private static boolean DISPLAY_DEBUG = true;
    private static final char PARAMETER_DELIMITER = '&';
    private static final char PARAMETER_EQUALS_CHAR = '=';

    public static void noConnection(final Context context, String message) {

        AlertDialog.Builder ab = new AlertDialog.Builder(context);

        if (isOnline(context, false)) {
            String messageText = "";
            if (message != null && DISPLAY_DEBUG) {
                messageText = "\n\n" + message;
            }

            ab.setMessage(context.getResources().getString(R.string.dialog_connection_description) + messageText);
            ab.setPositiveButton(context.getResources().getString(R.string.ok), null);
            ab.setTitle(context.getResources().getString(R.string.dialog_connection_title));
        } else {
            ab.setMessage(context.getResources().getString(R.string.dialog_internet_description));
            ab.setPositiveButton(context.getResources().getString(R.string.ok), null);
            ab.setTitle(context.getResources().getString(R.string.dialog_internet_title));
        }

        ab.show();
    }

    public static void noConnection(final Context context) {
        noConnection(context, null);
    }

    public static boolean isOnline(Context c, boolean showDialog) {
        ConnectivityManager cm = (ConnectivityManager)
                c.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();

        if (ni != null && ni.isConnected())
            return true;
        else if (showDialog) {
            noConnection(c);
        }
        return false;
    }

    public static void admobLoader(Context c, Resources resources, View AdmobView) {
        String adId = resources.getString(R.string.ad_id);
        if (adId != null && !adId.equals("") && !SettingsFragment.getIsPurchased(c)) {
            AdView adView = (AdView) AdmobView;
            adView.setVisibility(View.VISIBLE);

            // Look up the AdView as a resource and load a request.
            //Builder adRequestBuilder = new AdRequest.Builder();
            AdRequest adRequest = new AdRequest.Builder()
                    //.addTestDevice("74C7855A0B5CA4319744B61C668E9BF4")
                    .build();
            //adRequestBuilder.addTestDevice("74C7855A0B5CA4319744B61C668E9BF4");
            adView.loadAd(adRequest);
        }
    }

    @SuppressLint("NewApi")
    public static void revealView(View toBeRevealed, View frame) {
        //Make sure that the view is still attached (e.g. we haven't switched to another screen in the meantime)
        if (ViewCompat.isAttachedToWindow(toBeRevealed)) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                // get the center for the clipping circle
                int cx = (frame.getLeft() + frame.getRight()) / 2;
                int cy = (frame.getTop() + frame.getBottom()) / 2;

                // get the final radius for the clipping circle
                int finalRadius = Math.max(frame.getWidth(), frame.getHeight());

                // create the animator for this view (the start radius is zero)
                Animator anim = ViewAnimationUtils.createCircularReveal(
                        toBeRevealed, cx, cy, 0, finalRadius);

                // make the view visible and start the animation
                toBeRevealed.setVisibility(View.VISIBLE);
                anim.start();
            } else {
                toBeRevealed.setVisibility(View.VISIBLE);
            }
        }
    }

    @SuppressLint("NewApi")
    public static void setStatusBarColor(Activity mActivity, int color) {
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                mActivity.getWindow().setStatusBarColor(color);
            }
        } catch (Exception e) {
            Log.printStackTrace(e);
        }
    }


    //Makes high numbers readable (e.g. 5000 -> 5K)
    public static String formatValue(double value) {
        if (value > 0) {
            int power;
            String suffix = " kmbt";
            String formattedNumber = "";

            NumberFormat formatter = new DecimalFormat("#,###.#");
            power = (int) StrictMath.log10(value);
            value = value / (Math.pow(10, (power / 3) * 3));
            formattedNumber = formatter.format(value);
            formattedNumber = formattedNumber + suffix.charAt(power / 3);
            return formattedNumber.length() > 4 ? formattedNumber.replaceAll("\\.[0-9]+", "") : formattedNumber;
        } else {
            return "0";
        }
    }

    //Get response from an URL request (GET)
    public static String getDataFromUrl(String url) {
        // Making HTTP request
        Log.v("INFO", "Requesting: " + url);

        StringBuffer chaine = new StringBuffer("");
        try {
            URL urlCon = new URL(url);

            HttpURLConnection connection = (HttpURLConnection) urlCon
                    .openConnection();
            connection.setRequestProperty("User-Agent", "Universal/2.0 (Android)");
            connection.setRequestMethod("GET");
            connection.setDoInput(true);
            connection.connect();

            InputStream inputStream = connection.getInputStream();

            BufferedReader rd = new BufferedReader(new InputStreamReader(
                    inputStream));
            String line = "";
            while ((line = rd.readLine()) != null) {
                chaine.append(line);
            }

        } catch (IOException e) {
            // writing exception to log
            Log.printStackTrace(e);
        }

        return chaine.toString();
    }

    //Get JSON from an url and parse it to a JSON Object.
    public static JSONObject getJSONObjectFromUrl(String url) {
        String data = getDataFromUrl(url);

        try {
            return new JSONObject(data);
        } catch (Exception e) {
            Log.e("INFO", "Error parsing JSON. Printing stacktrace now");
            Log.printStackTrace(e);
        }

        return null;
    }

    //Get JSON from an url and parse it to a JSON Array.
    public static JSONArray getJSONArrayFromUrl(String url) {
        String data = getDataFromUrl(url);

        try {
            return new JSONArray(data);
        } catch (Exception e) {
            Log.e("INFO", "Error parsing JSON. Printing stacktrace now");
            Log.printStackTrace(e);
        }

        return null;
    }

    //Install certificates to reach HTTPS sites with specific certificates on older devices
    public static void updateAndroidSecurityProvider(Activity callingActivity) {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            try {
                ProviderInstaller.installIfNeeded(callingActivity);
            } catch (GooglePlayServicesRepairableException e) {
                // Thrown when Google Play Services is not installed, up-to-date, or enabled
                // Show dialog to allow users to install, update, or otherwise enable Google Play services.
                GooglePlayServicesUtil.getErrorDialog(e.getConnectionStatusCode(), callingActivity, 0);
            } catch (GooglePlayServicesNotAvailableException e) {
                Log.e("SecurityException", "Google Play Services not available.");
            }
        }
    }


    public static List<Map<String, String>> getUrlSetting(String url, Context context) {

        //Get the text file
        File file = new File(context.getFilesDir().getPath().toString()
                + "/List_TV.txt");
        BufferedReader in = null;
        List<Map<String, String>> ListTV = new ArrayList<>();
        try {
            if (!file.exists()) {
                in = new BufferedReader(
                        new InputStreamReader(context.getAssets().open("List_TV.txt")));
            } else {
                in = new BufferedReader(new FileReader(file));

            }
            String inputLine;
            Map<String, String> VN_VTV = new HashMap<>();
            Map<String, String> VN_HTV = new HashMap<>();
            Map<String, String> VN_VTC = new HashMap<>();
            Map<String, String> VN_mobiTV = new HashMap<>();
            Map<String, String> VN_DIA_PHUONG = new HashMap<>();
            Map<String, String> HAI_NGOAI = new HashMap<>();
            Map<String, String> CHINA = new HashMap<>();
            Map<String, String> QUOC_TE_TH = new HashMap<>();
            Map<String, String> PHIM_TH = new HashMap<>();
            Map<String, String> SPORTS = new HashMap<>();
            Map<String, String> _18PLUS = new HashMap<>();

            while ((inputLine = in.readLine()) != null)
                if (inputLine.contains("EXTINF")) {
                    if (inputLine.contains("VN-VTV")) {
                        VN_VTV.put(inputLine.split(",")[1], in.readLine().replace("|User-Agent=Mozilla/5.0", ""));
                    } else if (inputLine.contains("VN-HTV")) {
                        VN_HTV.put(inputLine.split(",")[1], in.readLine().replace("|User-Agent=Mozilla/5.0", ""));
                    } else if (inputLine.contains("VN-VTC")) {
                        VN_VTC.put(inputLine.split(",")[1], in.readLine().replace("|User-Agent=Mozilla/5.0", ""));
                    } else if (inputLine.contains("VN-mobiTV")) {
                        VN_mobiTV.put(inputLine.split(",")[1], in.readLine().replace("|User-Agent=Mozilla/5.0", ""));
                    } else if (inputLine.contains("VN-DIA PHUONG")) {
                        VN_DIA_PHUONG.put(inputLine.split(",")[1], in.readLine().replace("|User-Agent=Mozilla/5.0", ""));
                    } else if (inputLine.contains("HAI NGOAI")) {
                        HAI_NGOAI.put(inputLine.split(",")[1], in.readLine().replace("|User-Agent=Mozilla/5.0", ""));
                    } else if (inputLine.contains("CHINA")) {
                        CHINA.put(inputLine.split(",")[1], in.readLine().replace("|User-Agent=Mozilla/5.0", ""));
                    } else if (inputLine.contains("QUOC TE TH")) {
                        QUOC_TE_TH.put(inputLine.split(",")[1], in.readLine().replace("|User-Agent=Mozilla/5.0", ""));
                    } else if (inputLine.contains("PHIM TH")) {
                        PHIM_TH.put(inputLine.split(",")[1], in.readLine().replace("|User-Agent=Mozilla/5.0", ""));
                    } else if (inputLine.contains("SPORTS")) {
                        SPORTS.put(inputLine.split(",")[1], in.readLine().replace("|User-Agent=Mozilla/5.0", ""));
                    } else if (inputLine.contains("18+")) {
                        _18PLUS.put(inputLine.split(",")[1], in.readLine().replace("|User-Agent=Mozilla/5.0", ""));
                    }
                }
            in.close();
            ListTV.add(VN_VTV);
            ListTV.add(VN_HTV);
            ListTV.add(VN_VTC);
            ListTV.add(VN_mobiTV);
            ListTV.add(VN_DIA_PHUONG);
            ListTV.add(HAI_NGOAI);
            ListTV.add(QUOC_TE_TH);
            ListTV.add(CHINA);
            ListTV.add(PHIM_TH);
            ListTV.add(SPORTS);
            ListTV.add(_18PLUS);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ListTV;
    }

    public static List<Map<String, String>> getYTSetting(Context context) {

        //Get the text file
        File file = new File(context.getFilesDir().getPath().toString()
                + "/List_YT.txt");
        BufferedReader in = null;
        List<Map<String, String>> ListTV = new ArrayList<>();
        try {
            if (!file.exists()) {
                in = new BufferedReader(
                        new InputStreamReader(context.getAssets().open("List_YT.txt")));
            } else {
                in = new BufferedReader(new FileReader(file));

            }
            String inputLine;
            Map<String, String> STREAM = new HashMap<>();
            Map<String, String> PLAYLIST = new HashMap<>();
            //Map<String, String> VN_VTC = new HashMap<>();

            while ((inputLine = in.readLine()) != null)
                if (inputLine.contains("STREAM")) {
                    String[] line = inputLine.split(":");
                    STREAM.put(line[1], line[2]);
                } else if (inputLine.contains("PLAYLIST")) {
                    String[] line = inputLine.split(":");
                    PLAYLIST.put(line[1], line[2]);
                }
            in.close();
            ListTV.add(STREAM);
            ListTV.add(PLAYLIST);
            //System.out.println(ListTV.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ListTV;
    }

    public static String requestUrl(String url, String postParameters) throws Exception {
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) new URL(url).openConnection();
            urlConnection.setConnectTimeout(20000);
            urlConnection.setReadTimeout(20000);
            //urlConnection.setRequestProperty("Authorization", "Basic " + Base64.encodeToString("vipcard:vip123".getBytes(), 1));
            if (postParameters != null) {
                urlConnection.setDoOutput(true);
                urlConnection.setRequestMethod("POST");
                urlConnection.setFixedLengthStreamingMode(postParameters.getBytes().length);
                urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                Log.d("param", postParameters);
                PrintWriter out = new PrintWriter(urlConnection.getOutputStream());
                out.print(postParameters);
                out.close();
            }
            if (urlConnection.getResponseCode() != 200) {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                return null;
            }
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            return null;
        } catch (MalformedURLException e) {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        } catch (SocketTimeoutException e2) {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        } catch (IOException e3) {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        } catch (Throwable th) {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return null;
    }

    public static String createQueryStringForParameters(Map<String, String> parameters) {
        StringBuilder parametersAsQueryString = new StringBuilder();
        if (parameters != null) {
            boolean firstParameter = true;
            for (String parameterName : parameters.keySet()) {
                if (!firstParameter) {
                    parametersAsQueryString.append(PARAMETER_DELIMITER);
                }
                parametersAsQueryString.append(parameterName).append(PARAMETER_EQUALS_CHAR).append(URLEncoder.encode(parameters.get(parameterName)));
                firstParameter = false;
            }
        }
        return parametersAsQueryString.toString();
    }

}

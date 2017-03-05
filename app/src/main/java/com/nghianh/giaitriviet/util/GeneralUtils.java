package com.nghianh.giaitriviet.util;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.PowerManager;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.EditText;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * Created by bado on 11/07/2015.
 */
public class GeneralUtils {

    public static final String DATE_HH_MM_A = "hh:mm a";
    public static final String FULL_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    private static final String TAG = "GeneralUtils";

    public static boolean isEmptyEditText(EditText et) {
        return TextUtils.isEmpty(et.getText().toString());
    }

    public static String convertDateFormat(String input, String oldFormat, String newFormat) {
        String newDateString;

        SimpleDateFormat sdf = new SimpleDateFormat(oldFormat);
        Date d = null;
        try {
            d = sdf.parse(input);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        sdf.applyPattern(newFormat);
        newDateString = sdf.format(d);

        return newDateString;
    }


    public static String convert24To12Format(int hour, int minutes) {
        SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
        Date dateObj = null;
        String intput = hour + ":" + minutes;
        try {
            dateObj = sdf.parse(intput);
        } catch (final ParseException e) {
            e.printStackTrace();
        }

        String value = new SimpleDateFormat("KK:mm").format(dateObj);
//        if(hour >= 12){
//            value = value +" PM";
//        }else{
//            value = value +" AM";
//        }

        return value;
    }


    public static String getWorkingTimeFormat(String input, String format) {
        String formattedDate = "";
        try {

            SimpleDateFormat sdf = new SimpleDateFormat(FULL_DATE_FORMAT);
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));   // This line converts the given date into UTC time zone
            Date dateObj = sdf.parse(input);
            formattedDate = new SimpleDateFormat(format).format(dateObj);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return formattedDate;
    }

    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality) {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    public static Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public static String getAndroidVersion() {
        String release = Build.VERSION.RELEASE;
//        int sdkVersion = Build.VERSION.SDK_INT;
        return "Android " + release;
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return capitalize(model);
        } else {
            return capitalize(manufacturer) + " " + model;
        }
    }


    private static String capitalize(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        char first = s.charAt(0);
        if (Character.isUpperCase(first)) {
            return s;
        } else {
            return Character.toUpperCase(first) + s.substring(1);
        }
    }

    public static String getDateFormat(String input) {
//        Date date = null;
        String formattedDate = "";
        try {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));   // This line converts the given date into UTC time zone
            Date dateObj = sdf.parse(input);
            formattedDate = new SimpleDateFormat("HH:mm:ss, dd/MM/yyyy").format(dateObj);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return formattedDate;
    }

    public static float calculateDistance(Location startLocation, Location endLocation) {
        float distance = 0.0f;
        if (startLocation != null) {
            Location location = new Location("");
            location.setLongitude(endLocation.getLongitude());
            location.setLatitude(endLocation.getLatitude());
            distance = startLocation.distanceTo(location);
        }

        return distance;
    }

    public static int dpToPx(int dp) {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static int pxToDp(int px) {
        return (int) (px / Resources.getSystem().getDisplayMetrics().density);
    }

    public static String getCurrentDate(String format) {
        String value = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.US);
        value = dateFormat.format(new Date());

        Log.e(TAG, "DATA " + " " + dateFormat.format(new Date()));

        return value;
    }

    public static String getCurrentDateFullFormat() {
        String value = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat(FULL_DATE_FORMAT, Locale.US);
        value = dateFormat.format(new Date());

        Log.e(TAG, "DATA " + " " + dateFormat.format(new Date()));

        return value;
    }

    public static String getCurrentTime(String format) {
        try {
            SimpleDateFormat timeFormat = new SimpleDateFormat(format);
            String currentTimeStamp = timeFormat.format(new Date());

            return currentTimeStamp;
        } catch (Exception e) {
            return "";
        }
    }

    public static boolean isPackageInstalled(String packagename, Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(packagename, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static boolean isApplicationBroughtToBackground(Context context) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        if (!pm.isScreenOn()) {
            return true;
        }

        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

    public static String GetCountryPhoneCode(Context context) {
        String CountryID = "";

        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        CountryID = manager.getSimCountryIso().toUpperCase();

        Map<String, String> maps = GeneralUtils.getCountryNumberInfo();
        for (Map.Entry<String, String> entry : maps.entrySet()) {
            if (entry.getKey().trim().toLowerCase().equals(CountryID.trim().toLowerCase())) {
                return entry.getValue();
            }
        }
        return CountryID;
    }

    public static String getCountryAbbr(String countryCode) {
        String result = "";
        Map<String, String> maps = GeneralUtils.getCountryNumberInfo();
        for (Map.Entry<String, String> entry : maps.entrySet()) {
            if (entry.getValue().trim().toLowerCase().equals(countryCode.trim().toLowerCase())) {
                return entry.getKey();
            }
        }
        return "";
    }

    public static Map<String, String> getCountryNumberInfo() {
        Map<String, String> country_to_indicative = new HashMap<String, String>();
        country_to_indicative.put("AF", "+93");
        country_to_indicative.put("AL", "+355");
        country_to_indicative.put("DZ", "+213");
        country_to_indicative.put("AS", "+1684");
        country_to_indicative.put("AD", "+376");
        country_to_indicative.put("AO", "+244");
        country_to_indicative.put("AI", "+1264");
        country_to_indicative.put("AG", "+1268");
        country_to_indicative.put("AR", "+54");
        country_to_indicative.put("AM", "+374");
        country_to_indicative.put("AU", "+61");
        country_to_indicative.put("AW", "+297");
        country_to_indicative.put("AT", "+43");
        country_to_indicative.put("AZ", "+994");
        country_to_indicative.put("BS", "+1242");
        country_to_indicative.put("BH", "+973");
        country_to_indicative.put("BD", "+880");
        country_to_indicative.put("BB", "+1246");
        country_to_indicative.put("BY", "+375");
        country_to_indicative.put("BE", "+32");
        country_to_indicative.put("BZ", "+501");
        country_to_indicative.put("BJ", "+229");
        country_to_indicative.put("BM", "+1441");
        country_to_indicative.put("BT", "+975");
        country_to_indicative.put("BO", "+591");
        country_to_indicative.put("BA", "+387");
        country_to_indicative.put("BW", "+267");
        country_to_indicative.put("BR", "+55");
        country_to_indicative.put("BN", "+673");
        country_to_indicative.put("BG", "+359");
        country_to_indicative.put("BF", "+226");
        country_to_indicative.put("BI", "+257");
        country_to_indicative.put("KH", "+855");
        country_to_indicative.put("CM", "+237");
        country_to_indicative.put("CA", "+1");
        country_to_indicative.put("CV", "+238");
        country_to_indicative.put("CF", "+236");
        country_to_indicative.put("TD", "+235");
        country_to_indicative.put("CL", "+56");
        country_to_indicative.put("CN", "+86");
        country_to_indicative.put("CO", "+57");
        country_to_indicative.put("KM", "+269");
        country_to_indicative.put("CD", "+243");
        country_to_indicative.put("CG", "+242");
        country_to_indicative.put("CR", "+506");
        country_to_indicative.put("CI", "+225");
        country_to_indicative.put("HR", "+385");
        country_to_indicative.put("CU", "+53");
        country_to_indicative.put("CY", "+357");
        country_to_indicative.put("CZ", "+420");
        country_to_indicative.put("DK", "+45");
        country_to_indicative.put("DJ", "+253");
        country_to_indicative.put("DM", "+1767");
        country_to_indicative.put("DO", "+1829");
        country_to_indicative.put("EC", "+593");
        country_to_indicative.put("EG", "+20");
        country_to_indicative.put("SV", "+503");
        country_to_indicative.put("GQ", "+240");
        country_to_indicative.put("ER", "+291");
        country_to_indicative.put("EE", "+372");
        country_to_indicative.put("ET", "+251");
        country_to_indicative.put("FJ", "+679");
        country_to_indicative.put("FI", "+358");
        country_to_indicative.put("FR", "+33");
        country_to_indicative.put("GA", "+241");
        country_to_indicative.put("GM", "+220");
        country_to_indicative.put("GE", "+995");
        country_to_indicative.put("DE", "+49");
        country_to_indicative.put("GH", "+233");
        country_to_indicative.put("GR", "+30");
        country_to_indicative.put("GD", "+1473");
        country_to_indicative.put("GT", "+502");
        country_to_indicative.put("GN", "+224");
        country_to_indicative.put("GW", "+245");
        country_to_indicative.put("GY", "+592");
        country_to_indicative.put("HT", "+509");
        country_to_indicative.put("HN", "+504");
        country_to_indicative.put("HU", "+36");
        country_to_indicative.put("IS", "+354");
        country_to_indicative.put("IN", "+91");
        country_to_indicative.put("ID", "+62");
        country_to_indicative.put("IR", "+98");
        country_to_indicative.put("IQ", "+964");
        country_to_indicative.put("IE", "+353");
        country_to_indicative.put("IL", "+972");
        country_to_indicative.put("IT", "+39");
        country_to_indicative.put("JM", "+1876");
        country_to_indicative.put("JP", "+81");
        country_to_indicative.put("JO", "+962");
        country_to_indicative.put("KZ", "+7");
        country_to_indicative.put("KE", "+254");
        country_to_indicative.put("KI", "+686");
        country_to_indicative.put("KP", "+850");
        country_to_indicative.put("KR", "+82");
        country_to_indicative.put("KW", "+965");
        country_to_indicative.put("KG", "+996");
        country_to_indicative.put("LA", "+856");
        country_to_indicative.put("LV", "+371");
        country_to_indicative.put("LB", "+961");
        country_to_indicative.put("LS", "+266");
        country_to_indicative.put("LR", "+231");
        country_to_indicative.put("LY", "+218");
        country_to_indicative.put("LI", "+423");
        country_to_indicative.put("LT", "+370");
        country_to_indicative.put("LU", "+352");
        country_to_indicative.put("MK", "+389");
        country_to_indicative.put("MG", "+261");
        country_to_indicative.put("MW", "+265");
        country_to_indicative.put("MY", "+60");
        country_to_indicative.put("MV", "+960");
        country_to_indicative.put("ML", "+223");
        country_to_indicative.put("MT", "+356");
        country_to_indicative.put("MH", "+692");
        country_to_indicative.put("MR", "+222");
        country_to_indicative.put("MU", "+230");
        country_to_indicative.put("MX", "+52");
        country_to_indicative.put("FM", "+691");
        country_to_indicative.put("MD", "+373");
        country_to_indicative.put("MC", "+377");
        country_to_indicative.put("MN", "+976");
        country_to_indicative.put("ME", "+382");
        country_to_indicative.put("MA", "+212");
        country_to_indicative.put("MZ", "+258");
        country_to_indicative.put("MM", "+95");
        country_to_indicative.put("NA", "+264");
        country_to_indicative.put("NR", "+674");
        country_to_indicative.put("NP", "+977");
        country_to_indicative.put("NL", "+31");
        country_to_indicative.put("NZ", "+64");
        country_to_indicative.put("NI", "+505");
        country_to_indicative.put("NE", "+227");
        country_to_indicative.put("NG", "+234");
        country_to_indicative.put("NO", "+47");
        country_to_indicative.put("OM", "+968");
        country_to_indicative.put("PK", "+92");
        country_to_indicative.put("PW", "+680");
        country_to_indicative.put("PA", "+507");
        country_to_indicative.put("PG", "+675");
        country_to_indicative.put("PY", "+595");
        country_to_indicative.put("PE", "+51");
        country_to_indicative.put("PH", "+63");
        country_to_indicative.put("PL", "+48");
        country_to_indicative.put("PT", "+351");
        country_to_indicative.put("QA", "+974");
        country_to_indicative.put("RO", "+40");
        country_to_indicative.put("RU", "+7");
        country_to_indicative.put("RW", "+250");
        country_to_indicative.put("KN", "+1869");
        country_to_indicative.put("LC", "+1758");
        country_to_indicative.put("VC", "+1784");
        country_to_indicative.put("WS", "+685");
        country_to_indicative.put("SM", "+378");
        country_to_indicative.put("ST", "+239");
        country_to_indicative.put("SA", "+966");
        country_to_indicative.put("SN", "+221");
        country_to_indicative.put("RS", "+381");
        country_to_indicative.put("SC", "+248");
        country_to_indicative.put("SL", "+232");
        country_to_indicative.put("SG", "+65");
        country_to_indicative.put("SK", "+421");
        country_to_indicative.put("SI", "+386");
        country_to_indicative.put("SB", "+677");
        country_to_indicative.put("SO", "+252");
        country_to_indicative.put("ZA", "+27");
        country_to_indicative.put("ES", "+34");
        country_to_indicative.put("LK", "+94");
        country_to_indicative.put("SD", "+249");
        country_to_indicative.put("SR", "+597");
        country_to_indicative.put("SZ", "+268");
        country_to_indicative.put("SE", "+46");
        country_to_indicative.put("CH", "+41");
        country_to_indicative.put("SY", "+963");
        country_to_indicative.put("TJ", "+992");
        country_to_indicative.put("TZ", "+255");
        country_to_indicative.put("TH", "+66");
        country_to_indicative.put("TL", "+670");
        country_to_indicative.put("TG", "+228");
        country_to_indicative.put("TO", "+676");
        country_to_indicative.put("TT", "+1868");
        country_to_indicative.put("TN", "+216");
        country_to_indicative.put("TR", "+90");
        country_to_indicative.put("TM", "+993");
        country_to_indicative.put("TV", "+688");
        country_to_indicative.put("UG", "+256");
        country_to_indicative.put("UA", "+380");
        country_to_indicative.put("AE", "+971");
        country_to_indicative.put("GB", "+44");
        country_to_indicative.put("US", "+1");
        country_to_indicative.put("UY", "+598");
        country_to_indicative.put("UZ", "+998");
        country_to_indicative.put("VU", "+678");
        country_to_indicative.put("VA", "+39");
        country_to_indicative.put("VE", "+58");
        country_to_indicative.put("VN", "+84");
        country_to_indicative.put("YE", "+967");
        country_to_indicative.put("ZM", "+260");
        country_to_indicative.put("ZW", "+263");
        country_to_indicative.put("GE", "+995");
        country_to_indicative.put("TW", "+886");
        country_to_indicative.put("AZ", "+994");
        country_to_indicative.put("MD", "+373");
        country_to_indicative.put("SO", "+252");
        country_to_indicative.put("GE", "+995");
        country_to_indicative.put("AU", "+61");
        country_to_indicative.put("CX", "+61");
        country_to_indicative.put("CC", "+61");
        country_to_indicative.put("NF", "+672");
        country_to_indicative.put("NC", "+687");
        country_to_indicative.put("PF", "+689");
        country_to_indicative.put("YT", "+262");
        country_to_indicative.put("GP", "+590");
        country_to_indicative.put("GP", "+590");
        country_to_indicative.put("PM", "+508");
        country_to_indicative.put("WF", "+681");
        country_to_indicative.put("PF", "+689");
        country_to_indicative.put("CK", "+682");
        country_to_indicative.put("NU", "+683");
        country_to_indicative.put("TK", "+690");
        country_to_indicative.put("GG", "+44");
        country_to_indicative.put("IM", "+44");
        country_to_indicative.put("JE", "+44");
        country_to_indicative.put("AI", "+1264");
        country_to_indicative.put("BM", "+1441");
        country_to_indicative.put("IO", "+246");
        country_to_indicative.put("VG", "+1284");
        country_to_indicative.put("KY", "+1345");
        country_to_indicative.put("FK", "+500");
        country_to_indicative.put("GI", "+350");
        country_to_indicative.put("MS", "+1664");
        country_to_indicative.put("PN", "+870");
        country_to_indicative.put("SH", "+290");
        country_to_indicative.put("TC", "+1649");
        country_to_indicative.put("MP", "+1670");
        country_to_indicative.put("PR", "+1");
        country_to_indicative.put("AS", "+1684");
        country_to_indicative.put("GU", "+1671");
        country_to_indicative.put("VI", "+1340");
        country_to_indicative.put("HK", "+852");
        country_to_indicative.put("MO", "+853");
        country_to_indicative.put("FO", "+298");
        country_to_indicative.put("GL", "+299");
        country_to_indicative.put("GF", "+594");
        country_to_indicative.put("GP", "+590");
        country_to_indicative.put("MQ", "+596");
        country_to_indicative.put("RE", "+262");
        country_to_indicative.put("AX", "+35818");
        country_to_indicative.put("AW", "+297");
        country_to_indicative.put("AN", "+599");
        country_to_indicative.put("SJ", "+47");
        country_to_indicative.put("AC", "+247");
        country_to_indicative.put("TA", "+290");
        country_to_indicative.put("AQ", "+6721");
        country_to_indicative.put("CS", "+381");
        country_to_indicative.put("PS", "+970");
        country_to_indicative.put("EH", "+212");

        return country_to_indicative;
    }

//    public boolean checkPower(Context context){
//        PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
//        if(pm.isScreenOn())
//        {
//            //GeneralUtils.logErrorMessage("P: Haz Front",false);
//            //Toast.makeText(packet.getActivity(),"P: Haz Front",0).show();
//            packet.putExtra("preserve-activity", true);
//            this.activityPreserved = true;
//            this.showTimerExpiredDialog(packet);
//        }
//    }

    public static String getCountryNameFromCountryCode(String countryCode) {
        Locale loc = new Locale("", countryCode);
        return loc.getDisplayCountry();
    }

    public static Bitmap decodeFile(String path) {
        int orientation;
        try {
            if (path == null) {
                return null;
            }
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, o);
            // Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE = 70;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 0;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE
                        || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale++;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            Bitmap bm = BitmapFactory.decodeFile(path, o2);
            Bitmap bitmap = bm;

            ExifInterface exif = new ExifInterface(path);

            orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);

            Matrix m = new Matrix();

            if ((orientation == ExifInterface.ORIENTATION_ROTATE_180)) {
                m.postRotate(180);
                Log.e("QueANh in orientation", "" + orientation);
                bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);
                return bitmap;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                m.postRotate(90);
                Log.e("QueANh in orientation", "" + orientation);
                bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);
                return bitmap;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                m.postRotate(270);
                Log.e("QueANh in orientation", "" + orientation);
                bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), m, true);
                return bitmap;
            }

            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean isNetworkOnline(Context context) {
        boolean status = false;
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo netInfo = cm.getNetworkInfo(0);
            if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED) {
                status = true;
            } else {
                netInfo = cm.getNetworkInfo(1);
                if (netInfo != null && netInfo.getState() == NetworkInfo.State.CONNECTED)
                    status = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return status;

    }

    public static String removeEndlineBreakline(String value) {
        String meetingAddress = value.trim();
        meetingAddress = meetingAddress.replace("\n", ", ").replace("\r", ", ");

        return meetingAddress;
    }

    public static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public String date2String(Date date, String format) {
        SimpleDateFormat timeFormat = new SimpleDateFormat(format);
        String timeString = timeFormat.format(date);

        return timeString;
    }

    public String date2Time(Date date, String format) {

        if (date == null) {
            return "";
        }

        SimpleDateFormat timeFormat = new SimpleDateFormat(format);
        String timeStamp = timeFormat.format(date);

        return timeStamp;
    }

    public boolean isTwoSeperateDate(Date date1, Date date2) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");

        try {
            Date firstDate = formatter.parse(formatter.format(date1));
            Date secondDate = formatter.parse(formatter.format(date2));

            int result = firstDate.compareTo(secondDate);

            return result != 0;
        } catch (ParseException e) {
            return false;
        }
    }
}

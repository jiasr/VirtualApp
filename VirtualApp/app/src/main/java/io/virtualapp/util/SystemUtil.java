package io.virtualapp.util;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;

import java.util.Locale;

import com.alibaba.fastjson.JSONObject;


public class SystemUtil {


    public static String getIMEI(Context ctx) {
        TelephonyManager tm = (TelephonyManager) ctx.getSystemService(Activity.TELEPHONY_SERVICE);
        if (tm != null) {
            if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return tm.getDeviceId();
            }

        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static JSONObject getInfo(Context ctx) {

        JSONObject js = new JSONObject();
        js.put("serialNum", Build.SERIAL);
        js.put("model", Build.MODEL);
        js.put("brand", Build.BRAND);
        js.put("device", Build.DEVICE);
        js.put("CPU_API", Build.CPU_ABI);
        js.put("CPU_ABI2", Build.CPU_ABI2);

        js.put("current_locale",Locale.getDefault().getLanguage());
        js.put("RELEASE",Build.VERSION.RELEASE);
        try {
            js.put("serial",getIMEI(ctx));
        } catch (Exception e) {
            js.put("serial",Build.UNKNOWN);
        }

        //js.put("locale_list",Locale.getAvailableLocales());

        return js;

    }
}

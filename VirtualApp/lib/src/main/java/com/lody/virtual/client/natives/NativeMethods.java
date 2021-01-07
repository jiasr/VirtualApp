package com.lody.virtual.client.natives;

import android.hardware.Camera;
import android.media.AudioRecord;
import android.os.Build;

import java.lang.reflect.Method;

import dalvik.system.DexFile;

import android.util.Log;

/**
 * @author Lody
 */
public class NativeMethods {

    private static final String TAG = NativeMethods.class.getSimpleName();

    public static int gCameraMethodType;
    public static Method gCameraNativeSetup;

    public static Method gOpenDexFileNative;

    public static Method gAudioRecordNativeCheckPermission;

    public static void init() {
        String methodName =
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT ? "openDexFileNative" : "openDexFile";
        for (Method method : DexFile.class.getDeclaredMethods()) {
            if (method.getName().equals(methodName)) {
                gOpenDexFileNative = method;
                break;
            }
        }
        if (gOpenDexFileNative == null) {
            throw new RuntimeException("Unable to find method : " + methodName);
        }
        gOpenDexFileNative.setAccessible(true);

        gCameraMethodType = -1;
        try {
            gCameraNativeSetup = Camera.class.getDeclaredMethod("native_setup", Object.class, int.class, String.class);
            gCameraMethodType = 1;
        } catch (NoSuchMethodException e) {
            // ignore
        }
        if (gCameraNativeSetup == null) {
            try {
                gCameraNativeSetup = Camera.class.getDeclaredMethod("native_setup", Object.class, int.class, int.class, String.class);
                gCameraMethodType = 2;
            } catch (NoSuchMethodException e) {
                // ignore
            }
        }
        // HuaWei common
        if (gCameraNativeSetup == null) {
            try {
                gCameraNativeSetup = Camera.class.getDeclaredMethod("native_setup", Object.class, int.class, int.class, String.class, boolean.class);
                gCameraMethodType = 3;
            } catch (NoSuchMethodException e) {
                // ignore
            }
        }
        // HUAWEI MediaPad X1 7.0
        if (gCameraNativeSetup == null) {
            try {
                gCameraNativeSetup = Camera.class.getDeclaredMethod("native_setup", Object.class, int.class, String.class, boolean.class);
                gCameraMethodType = 4;
            } catch (NoSuchMethodException e) {
                // ignore
            }
        }
        if (gCameraNativeSetup != null) {
            gCameraNativeSetup.setAccessible(true);
        }

        for (Method mth : AudioRecord.class.getDeclaredMethods()) {
            Log.i(TAG, mth.getName());
            if (mth.getName().equals("native_check_permission")
                    && mth.getParameterTypes().length == 1
                    && mth.getParameterTypes()[0] == String.class) {
                gAudioRecordNativeCheckPermission = mth;
                mth.setAccessible(true);
                break;
            }
        }
    }

}

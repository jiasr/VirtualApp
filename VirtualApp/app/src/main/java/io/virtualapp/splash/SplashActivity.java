package io.virtualapp.splash;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.WindowManager;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.lody.virtual.client.core.VirtualCore;
import com.lody.virtual.client.env.VirtualRuntime;

import io.virtualapp.R;
import io.virtualapp.VCommends;
import io.virtualapp.abs.ui.VActivity;
import io.virtualapp.abs.ui.VUiKit;
import io.virtualapp.home.FlurryROMCollector;
import io.virtualapp.home.HomeActivity;
import io.virtualapp.util.SystemUtil;
import io.virtualapp.widgets.TwoGearsView;
import jonathanfinerty.once.Once;
import mirror.android.providers.Settings;

public class SplashActivity extends VActivity {

    private TextView phone_DeviceBrand;
    private TextView phone_SystemModel;
    private TextView phone_SystemLanguage;
    private TextView phone_SystemVersion;
    private TextView phone_IMEI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        @SuppressWarnings("unused")
        boolean enterGuide = !Once.beenDone(Once.THIS_APP_INSTALL, VCommends.TAG_NEW_VERSION);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        try {
            showSystemParameter();
        } catch (Exception e) {
            e.printStackTrace();
        }
        VUiKit.defer().when(() -> {
            if (!Once.beenDone("collect_flurry")) {
                FlurryROMCollector.startCollect();
                Once.markDone("collect_flurry");
            }
            long time = System.currentTimeMillis();
            doActionInThread();
            time = System.currentTimeMillis() - time;
            long delta = 30000L - time;
            if (delta > 0) {
                VUiKit.sleep(delta);
            }
        }).done((res) -> {
            HomeActivity.goHome(this);
            finish();
        });
    }


    private void bindViews() {

        phone_DeviceBrand = (TextView) findViewById(R.id.phone_DeviceBrand);
        phone_SystemModel = (TextView) findViewById(R.id.phone_SystemModel);
        phone_SystemLanguage = (TextView) findViewById(R.id.phone_SystemLanguage);
        phone_SystemVersion = (TextView) findViewById(R.id.phone_SystemVersion);
        phone_IMEI = (TextView) findViewById(R.id.phone_IMEI);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showSystemParameter() {
        String TAG = "系统参数：";
        bindViews();
        JSONObject js = SystemUtil.getInfo(this);
        Log.i(TAG, "Android系统版本号：" + js);
    }
    private void doActionInThread() {
        if (!VirtualCore.get().isEngineLaunched()) {
            VirtualCore.get().waitForEngine();
        }
    }
}

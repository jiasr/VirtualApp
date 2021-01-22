package io.virtualapp.splash;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.WindowManager;
import android.widget.LinearLayout;
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

    private LinearLayout splash_list;


    @RequiresApi(api = Build.VERSION_CODES.O)
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
            long delta = 2000L - time;
            if (delta > 0) {
                VUiKit.sleep(delta);
            }
        }).done((res) -> {
            HomeActivity.goHome(this);
            finish();
        });
    }


    private void bindViews(JSONObject js) {
        splash_list = (LinearLayout) findViewById(R.id.splash_list);
        String TAG = "系统参数：";
        for (String value:js.keySet()){
            Log.i(TAG, "Android系统版本号：" + value);
            TextView child = new TextView(this);
            child.setTextSize(20);
            child.setTextColor(getResources().getColor(R.color.colorAccent));
            child.setText(value+":"+js.get(value));
            // 调用一个参数的addView方法
            splash_list.addView(child);
        }


    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showSystemParameter() {
        String TAG = "系统参数：";
        JSONObject js = SystemUtil.getInfo(this);
        Log.i(TAG, "Android系统版本号：" + js);
        bindViews(js);
    }
    private void doActionInThread() {
        if (!VirtualCore.get().isEngineLaunched()) {
            VirtualCore.get().waitForEngine();
        }
    }
}

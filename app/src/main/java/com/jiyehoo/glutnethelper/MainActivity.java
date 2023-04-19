package com.jiyehoo.glutnethelper;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.progressindicator.CircularProgressIndicator;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private final String TAG = "###Main";
    private EditText mEtNumber, mEtTimeGap;
    private TextView mTvState;
    private String orgName = "xgqg";
    private CircularProgressIndicator mCpiLoading;
    private Button mBtnIsOnline, mBtnStartService, mBtnStopService;
    private RadioButton mRb1;
    private RadioButton mRb2;
    private CheckBox mCbIsRemember, mCbAutoConnect;

    private String number;

    private AutoConnectService.InfoBinder binder;
    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (AutoConnectService.InfoBinder) service;

//            String number = mEtNumber.getText().toString();
//            if (TextUtils.isEmpty(number)) {
//                number = "1";
//            }

            String timeGapStr = mEtTimeGap.getText().toString();
            Log.d(TAG, "输入值:" + timeGapStr);
            if (TextUtils.isEmpty(timeGapStr)) {
                timeGapStr = "5";
                Log.d(TAG, "时间为空，初始化为5");
            }
            int timeGap = Integer.parseInt(timeGapStr);
            if (timeGap <= 0 || timeGap > 600) {
                Log.d(TAG, "时间间隔不符合，定义为 5");
                timeGap = 5;
            }

            binder.setNumber("9");
            binder.setOrg("xgqg");
            binder.setTimeGap(timeGap);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
//        initData();
    }

//    private void initData() {
//        spRead();
//    }

    private void initView() {
//        findViewById(R.id.btn_start_get).setOnClickListener(this);
        findViewById(R.id.btn_start_12).setOnClickListener(this);
        findViewById(R.id.btn_logout).setOnClickListener(this);
//        findViewById(R.id.btn_connect_WiFi).setOnClickListener(this);

        mTvState = findViewById(R.id.tv_state);
//        mEtNumber = findViewById(R.id.et_net_number);
//        mRb1 = findViewById(R.id.rb_1);
//        mRb2 = findViewById(R.id.rb_2);
        mCpiLoading = findViewById(R.id.cpiLoading);
//        mBtnIsOnline = findViewById(R.id.btn_is_online);
        mBtnStartService = findViewById(R.id.btn_start_service);
        mBtnStopService = findViewById(R.id.btn_stop_service);
        mEtTimeGap = findViewById(R.id.et_time_gap);


//        mCbIsRemember = findViewById(R.id.cb_remember);
//        mCbAutoConnect = findViewById(R.id.cb_auto_connect);

//        mRb1.setOnClickListener(this);
//        mRb2.setOnClickListener(this);
//        mBtnIsOnline.setOnClickListener(this);
        mBtnStartService.setOnClickListener(this);
        mBtnStopService.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
//        // 登录指定
//        if (v.getId() == R.id.btn_start_get) {
//            spWrite();
//            String number = mEtNumber.getText().toString();
//            if (TextUtils.isEmpty(number)) {
//                number = "1";
//            }
//            startNumber(number);
//        }
//
        // 注销
        if (v.getId() == R.id.btn_logout) {
            startLogout();
        }

//        if (v.getId() == R.id.btn_start_12) {
//            start12();
//        }

//        // 选择组织
//        if (v.getId() == R.id.rb_1) {
//            // 选择勤工助学
//            Log.d(TAG, "选择勤工助学");
//            orgName = "xgqg";
//        }
//        if (v.getId() == R.id.rb_2) {
//            // 选择事务中心
//            Log.d(TAG, "选择事务中心");
//            orgName = "xgswzx00";
//        }

        // 连接 WiFi
//        if (v.getId() == R.id.btn_connect_WiFi) {
//            connectWiFi();
//        }
//
//        // 判断是否可上网
//        if (v.getId() == R.id.btn_is_online) {
//            isNetworkOnline();
//        }

        // 启动服务
        if (v.getId() == R.id.btn_start_service) {
            mBtnStopService.setEnabled(true);
            mBtnStartService.setEnabled(false);
//            String number = mEtNumber.getText().toString();
//            if (TextUtils.isEmpty(number)) {
//                number = "1";
//            }
            setText("开启防顶号服务\n账号为:" + 9);
            startService();
        }

        // 停止服务
        if (v.getId() == R.id.btn_stop_service) {
            mBtnStopService.setEnabled(false);
            mBtnStartService.setEnabled(true);
            setText("关闭了防顶号");
            stopService();
        }
    }

    /**
     * 指定账号登录
     */
    private void startNumber(String number) {

        String url = "http://172.16.2.2/drcom/login?callback=dr1003&DDDDD=" +
                orgName +
                number +
                "&upass=" +
                orgName +
                number +
                "&0MKKey=123456&R1=0&R2=&R3=0&R6=1&para=00&v6ip=&terminal_type=2&lang=zh-cn&jsVersion=4.1&v=970&lang=zh";

        startGet(url);

    }

//
//    /**
//     * 12 登录
//     */
//    private void start12() {
//        // 开始网络请求
//        String url = "http://172.16.2.2/drcom/login?callback=dr1003&DDDDD=xgqg12&upass=xgqg12&0MKKey=123456&R1=0&R2=&R3=0&R6=1&para=00&v6ip=&terminal_type=2&lang=zh-cn&jsVersion=4.1&v=970&lang=zh";
//        startGet(url);
//    }

    /**
     * 网络请求
     */
    private void startGet(String url) {
        Log.d(TAG, "请求url:" + url);
        HttpUtil.sendGetRequest(url, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "发送请求失败", Toast.LENGTH_LONG).show();
                    setText("发送请求失败\n请检查是否连到 Glut_web");
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String backStr = Objects.requireNonNull(response.body()).string();
                backStr = backStr.substring(12, backStr.length() - 4);
                Log.d(TAG, "返回内容:" + backStr);
                String finalBackStr = backStr;
                runOnUiThread(() -> parseLogin(finalBackStr));

            }
        });
    }

    /**
     * 注销请求
     */
    private void startLogout() {
        String url = "http://172.16.2.2/drcom/logout?callback=dr1002&jsVersion=4.1&v=1216&lang=zh";
        HttpUtil.sendGetRequest(url, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, "发送请求失败", Toast.LENGTH_LONG).show();
                    setText("发送注销请求失败\n请检查是否连接到对应 Wi-fi");
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String backStr = Objects.requireNonNull(response.body()).string();
                backStr = backStr.substring(12, backStr.length() - 4);
                Log.d(TAG, "返回内容:" + backStr);
                String finalBackStr = backStr;
                runOnUiThread(() -> {
//                    Toast.makeText(MainActivity.this, "发送请求成功:" + finalBackStr, Toast.LENGTH_LONG).show();
                    parseSignOut(finalBackStr);
                });
            }
        });
    }


    /**
     * textview 显示
     */
    private void setText(String msg) {
        mTvState.setText(msg);
    }

    /**
     * 登录成功/失败解析，加载到tv
     */
    private void parseLogin(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            int result = jsonObject.getInt("result");

            if (result == 0) {
                Log.d(TAG, "登录失败，开始显示 tv");
                String msg = "登录失败 \n" +
                        "错误原因:";
                String msgError = jsonObject.getString("msga");
                if ("userid error1".equals(msgError)) {
                    // 账号不存在
                    msg = msg + "账号不存在";
                } else if ("userid error2".equals(msgError)) {
                    // 密码错误
                    msg = msg + "密码错误";
                } else {
                    msg = msg + msgError;
                }

                setText(msg);
            }

            if (result == 1) {
                Log.d(TAG, "登录成功");
                String uid = jsonObject.getString("uid");
                String nid = jsonObject.getString("NID");
                String msg = "登录成功 \n" +
                        "账号:" + uid + "\n" +
                        "组织:" + nid;
                setText(msg);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 注销返回信息解析，判断是否注销成功
     */
    private void parseSignOut(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            int result = jsonObject.getInt("result");

            if (result == 0) {
                Log.d(TAG, "注销失败");
                String msgError = jsonObject.getString("msga");
                String msg = "登录失败 \n" +
                        "错误原因:" + msgError;
                setText(msg);
            }

            if (result == 1) {
                Log.d(TAG, "注销成功");
                String uid = jsonObject.getString("uid");
                String msg = "注销成功 \n" +
                        "账号:" + uid;
                setText(msg);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * 自动连接 Wi-Fi
     */
    private void connectWiFi() {

        // 1、注意热点和密码均包含引号，此处需要需要转义引号
        String ssid = "\"" + "glut_Web" + "\"";
//        String psd = "\"" + targetPsd + "\"";

        //2、配置wifi信息
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = ssid;
        conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

        //3、链接wifi
        WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifiManager.addNetwork(conf);

        @SuppressLint("MissingPermission") List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration i : list) {
            if (i.SSID != null && i.SSID.equals(ssid)) {
                wifiManager.disconnect();
                wifiManager.enableNetwork(i.networkId, true);
                wifiManager.reconnect();
                break;
            }
        }
    }


    private boolean isNetworkConnected() {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    /**
     * 诊断网络
     */
    public void isNetworkOnline() {
        Log.d(TAG, "开始判断网络能否联网");
        setText("正在诊断...");
        mBtnIsOnline.setEnabled(false);
        mCpiLoading.setVisibility(View.VISIBLE);
        new Thread(() -> {
            try {
                Process ipProcess = Runtime.getRuntime().exec("ping -c 1 -w 1 www.baidu.com");
                int exitValue = ipProcess.waitFor();
                Log.d(TAG, "网络可用性:" + exitValue);
                if (exitValue == 0) {
                    Log.d(TAG, "网络可上网");
                    runOnUiThread(() -> {
                        mCpiLoading.setVisibility(View.GONE);
                        setText("网络已畅通");
                        mBtnIsOnline.setEnabled(true);
                    });
                } else if (exitValue == 1) {
                    Log.d(TAG, "网络需要认证");
                    runOnUiThread(() -> {
                        mCpiLoading.setVisibility(View.GONE);
                        setText("网络需要认证\n目前仅可访问校内网");
                        mBtnIsOnline.setEnabled(true);
                        if (mCbAutoConnect.isChecked()) {
                            String number = mEtNumber.getText().toString();
                            if (TextUtils.isEmpty(number)) {
                                number = "1";
                            }
                            startNumber(number);
                        }

                    });
                } else {
                    Log.d(TAG, "网络未连接");
                    runOnUiThread(() -> {
                        mCpiLoading.setVisibility(View.GONE);
                        setText("网络未连接");
                        mBtnIsOnline.setEnabled(true);
                    });
                }
//                    return (exitValue == 0);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
//                return false;

        }).start();


    }

    /**
     * 读取 sp
     */
    private void spRead() {
        SharedPreferences preferences = getSharedPreferences("data", MODE_PRIVATE);
        boolean isRemember = preferences.getBoolean("isRemember", false);
        if (isRemember) {
            mCbIsRemember.setChecked(true);
            number = preferences.getString("number", "");
            orgName = preferences.getString("org", "xgqg");

            if (!TextUtils.isEmpty(number)) {
                Log.d(TAG, "sp 读取 number:" + number);
                mEtNumber.setText(number);
            } else {
                Log.d(TAG, "sp 读取 number 为空");
            }

            if (orgName.equals("xgswzx00")) {
                mRb1.setChecked(false);
                mRb2.setChecked(true);
            }

            mCbAutoConnect.setChecked(preferences.getBoolean("autoConnect", false));
        }



    }

    /**
     * 写入 sp
     */
    private void spWrite() {
        SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();

        if (mCbIsRemember.isChecked()) {
            // todo 写入
            editor.putBoolean("isRemember", true);
            editor.putString("number", mEtNumber.getText().toString());
            editor.putString("org", mRb1.isChecked() ? "xgqg" : "xgswzx00");
            editor.putBoolean("autoConnect", mCbAutoConnect.isChecked());
            editor.apply();
            Log.d(TAG, "写入 sp:" + number);
        } else {
            // todo 清空
            editor.remove("isRemember");
            editor.remove("number");
            editor.remove("org");
            editor.remove("autoConnect");
            editor.apply();
        }
    }

    /**
     * 启动服务
     */
    private void startService() {
        Intent intent = new Intent(this, AutoConnectService.class);
        if (Build.VERSION.SDK_INT >= 26) {
            startForegroundService(intent);
            bindService(intent, connection, BIND_AUTO_CREATE);
        } else {
            startService(intent);
        }
    }

    /**
     * 停止服务
     */
    private void stopService() {
        unbindService(connection);
        Intent intent = new Intent(this, AutoConnectService.class);
        stopService(intent);
    }

}
package com.jiyehoo.glutnethelper;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.security.PublicKey;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class AutoConnectService extends Service {

    private final String TAG = "###AutoConnectService";

    private static final String ID = "channel_1";
    private static final String NAME = "前台服务";

    private Thread checkThread;
    private Boolean isClose = true;
    private String number = "1";
    private String org = "xgqg";
    private int timeGap = 5;

    private NotificationManager manager;
    private Context context;

    private final InfoBinder binder = new InfoBinder();

    private int autoLoginTimes = 0;

    public AutoConnectService() {
    }

    // todo 通知栏点击，使用广播通知
    // todo 格式化数值 %02d 这样保证尾数补0
    /**
     * 通信
     */
     class InfoBinder extends Binder {

        public void setNumber(String numStr) {
            // 设置编号
            AutoConnectService.this.number = numStr;
        }

        public void setOrg(String orgStr) {
            // 设置组织
            AutoConnectService.this.org = orgStr;
        }

        public void setTimeGap(int s) {
            // 设置时间间隔
            AutoConnectService.this.timeGap = s;
        }

    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "服务启动");
        if(Build.VERSION.SDK_INT>=26){
            setForeground();
        }
        checkThread = new Thread(() -> {
            while (isClose) {
                try {
                    checkNet();
                    Thread.sleep(timeGap * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // 启动时执行
        Log.d(TAG, "开始执行功能代码");
        checkThread.start();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isClose = false;
        Log.d(TAG, "停止服务");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @TargetApi(26)
    private void setForeground(){
        Log.d(TAG, "开启前台");
        context = this;
        manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel(ID, NAME, NotificationManager.IMPORTANCE_HIGH);
        manager.createNotificationChannel(channel);
        Notification notification = new Notification.Builder(this, ID)
                .setContentTitle("校园网助手")
                .setContentText("已开启防掉线服务")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                .build();
        startForeground (1, notification);
    }

    /**
     * 检查网络连接状态
     */
    private void checkNet() {
        Log.d(TAG, "服务开始检查网络");
        new Thread(() -> {
            try {
                Process ipProcess = Runtime.getRuntime().exec("ping -c 1 -w 1 www.baidu.com");
                int exitValue = ipProcess.waitFor();
                Log.d(TAG, "网络可用性:" + exitValue);
                if (exitValue == 0) {
                    Log.d(TAG, "网络可上网");

                } else if (exitValue == 1) {
                    Log.d(TAG, "网络需要认证");
                    // 开始认证
                    autoLoginTimes++;
                    startConnect(number, org);
                } else {
                    Log.d(TAG, "网络未连接");
                }
//                    return (exitValue == 0);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
//                return false;

        }).start();
    }

    /**
     * 指定 账号+组织 登录
     */
    private void startConnect(String number, String orgName) {

        String url = "http://172.16.2.2/drcom/login?callback=dr1003&DDDDD=" +
                orgName +
                number +
                "&upass=" +
                orgName +
                number +
                "&0MKKey=123456&R1=0&R2=&R3=0&R6=1&para=00&v6ip=&terminal_type=2&lang=zh-cn&jsVersion=4.1&v=970&lang=zh";

        Log.d(TAG, "请求url:" + url);
        HttpUtil.sendGetRequest(url, new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d(TAG, "请求失败");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String backStr = Objects.requireNonNull(response.body()).string();
                backStr = backStr.substring(12, backStr.length() - 4);
                Log.d(TAG, "返回内容:" + backStr);
                // todo 更新通知
                NotificationChannel channel = new NotificationChannel(ID, NAME, NotificationManager.IMPORTANCE_HIGH);
                manager.createNotificationChannel(channel);
                Notification notification = new Notification.Builder(context, ID)
                        .setContentTitle("校园网助手")
                        .setContentText("已开启防掉线服务，为你防顶号 " + autoLoginTimes + " 次")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
                        .build();
                manager.notify(1, notification);
                // todo 对返回的结果解析：成功还是失败
                Handler handlerThree=new Handler(Looper.getMainLooper());
                handlerThree.post(() -> Toast.makeText(getApplicationContext() ,"自动重新认证：" + autoLoginTimes + "次", Toast.LENGTH_SHORT).show());
            }
        });
    }
}
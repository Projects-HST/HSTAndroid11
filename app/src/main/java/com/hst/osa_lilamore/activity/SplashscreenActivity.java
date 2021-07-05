package com.hst.osa_lilamore.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.hst.osa_lilamore.R;
import com.hst.osa_lilamore.bean.database.SQLiteHelper;
import com.hst.osa_lilamore.utils.OSAValidator;
import com.hst.osa_lilamore.utils.PreferenceStorage;

public class SplashscreenActivity extends AppCompatActivity {
    private final String TAG = getClass().getSimpleName();
    private static int SPLASH_TIME_OUT = 2000;
    private static final int PERMISSION_REQUEST_CODE = 1;
    SQLiteHelper database;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashscreen);
        database = new SQLiteHelper(getApplicationContext());
//        appSignatureHelper = new AppSignatureHelper(this);
//        appSignatureHelper.getAppSignatures();
        final int getStatus = database.appInfoCheck();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Boolean b = PreferenceStorage.isFirstTimeLaunch(getApplicationContext());
                String id = PreferenceStorage.getUserId(getApplicationContext());
                if (getStatus != 0 && OSAValidator.checkNullString(id)) {
                    Intent i = new Intent(SplashscreenActivity.this, com.hst.osa_lilamore.activity.MainActivity.class);
                    startActivity(i);
                    finish();
                } else {
                    Intent i = new Intent(getApplicationContext(), WelcomeActivity.class);
                    FirebaseMessaging.getInstance().getToken()
                            .addOnCompleteListener(new OnCompleteListener<String>() {
                                @Override
                                public void onComplete(@NonNull Task<String> task) {
                                    if (!task.isSuccessful()) {
                                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                                        return;
                                    }
                                    // Get new FCM registration token
                                    String token = task.getResult();
                                    PreferenceStorage.saveGCM(getApplicationContext(), token);
                                    // Log and toast
                                }
                            });
                    startActivity(i);
                    finish();
                }
            }
        }, SPLASH_TIME_OUT);

    }

//    public void hashFromSHA1() {
//        sh = "28:D6:E7:CF:CC:66:02:16:B1:97:3D:9A:FE:E7:8E:CB:20:1B:DE:03";
//        String[] arr = sh.split(":");
//        byte[] byteArr = new  byte[arr.length];
//
//        for (int i = 0; i< arr.length; i++) {
//            byteArr[i] = Integer.decode("0x" + arr[i]).byteValue();
//        }
//
//        Log.e("hash : ", Base64.encodeToString(byteArr, Base64.NO_WRAP));
//    }
}

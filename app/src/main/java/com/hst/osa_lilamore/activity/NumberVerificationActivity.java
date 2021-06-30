package com.hst.osa_lilamore.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.auth.api.phone.SmsRetrieverClient;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.hst.osa_lilamore.R;
import com.hst.osa_lilamore.customview.CustomOtpEditText;
import com.hst.osa_lilamore.helpers.AlertDialogHelper;
import com.hst.osa_lilamore.helpers.ProgressDialogHelper;
import com.hst.osa_lilamore.interfaces.DialogClickListener;
import com.hst.osa_lilamore.servicehelpers.ServiceHelper;
import com.hst.osa_lilamore.serviceinterfaces.IServiceListener;
import com.hst.osa_lilamore.utils.CommonUtils;
import com.hst.osa_lilamore.utils.OSAConstants;
import com.hst.osa_lilamore.utils.PreferenceStorage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.util.Log.d;

public class NumberVerificationActivity extends AppCompatActivity implements View.OnClickListener, IServiceListener, DialogClickListener {
    private static final String TAG = NumberVerificationActivity.class.getName();

    private CustomOtpEditText otpEditText;
    private TextView tvResendOTP, tvCountDown, tvNumber;
    private Button btnConfirm;
    private TextView btnChangeNumber;
    private String mobileNo;
    private String checkVerify;
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    private static final int SMS_CONSENT_REQUEST = 2;  // Set to an unused request code
    private SmsBrReceiver smsReceiver;
    String page = "", productID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_numberverification);

        productID = getIntent().getExtras().getString("productObj");
        page = getIntent().getExtras().getString("page");

        Toolbar toolbar = (Toolbar)findViewById(R.id.activity_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_left_arrow));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mobileNo = PreferenceStorage.getMobileNo(getApplicationContext());
        otpEditText = (CustomOtpEditText) findViewById(R.id.otp_view);
        tvNumber = (TextView) findViewById(R.id.number);
        tvNumber.setText(mobileNo);
        tvResendOTP = (TextView) findViewById(R.id.resend);
        tvResendOTP.setOnClickListener(this);
        btnConfirm = (Button) findViewById(R.id.sendcode);
        btnConfirm.setOnClickListener(this);

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);

        // Start listening for SMS User Consent broadcasts from senderPhoneNumber
        // The Task<Void> will be successful if SmsRetriever was able to start
        // SMS User Consent, and will error if there was an error starting.
        SmsRetrieverClient client = SmsRetriever.getClient(this);
        Task<Void> task = client.startSmsRetriever();
//        Task<Void> task = SmsRetriever.getClient(this).startSmsUserConsent(senderPhoneNumber /* or null */);
        task.addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                // Successfully started retriever, expect broadcast intent
                // ...
                Toast.makeText(NumberVerificationActivity.this, "Listening for otp...", Toast.LENGTH_SHORT).show();
                IntentFilter filter = new IntentFilter();
                filter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION);
                if (smsReceiver == null) {
                    smsReceiver = new NumberVerificationActivity.SmsBrReceiver();
                }
                getApplicationContext().registerReceiver(smsReceiver, filter);
//                            startActivity(homeIntent);
//                            finish();
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Failed to start retriever, inspect Exception for more details
                // ...
                Toast.makeText(NumberVerificationActivity.this, "Failed listening for otp...", Toast.LENGTH_SHORT).show();
            }
        });

//        countDownTimers();
    }

    void countDownTimers() {
        new CountDownTimer(30 * 1000 + 1000, 1000) {

            public void onTick(long millisUntilFinished) {
                tvResendOTP.setVisibility(View.GONE);
                int seconds = (int) (millisUntilFinished / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;
//                tvCountDown.setText("Resend in " + String.format("%02d", minutes)
//                        + ":" + String.format("%02d", seconds) + " seconds");
            }

            public void onFinish() {
                tvCountDown.setText("Try again...");
                tvCountDown.setVisibility(View.GONE);
                tvResendOTP.setVisibility(View.VISIBLE);
            }
        }.start();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View view) {

        if (CommonUtils.isNetworkAvailable(getApplicationContext())) {

            if (view == tvResendOTP) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setTitle(R.string.resend);
                alertDialogBuilder.setMessage(getString(R.string.mobile) + getString(R.string.mobile_number_tag) + PreferenceStorage.getMobileNo(getApplicationContext()));
                alertDialogBuilder.setPositiveButton(R.string.alert_button_ok,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                checkVerify = "Resend";
//                                countDownTimers();
//                                tvCountDown.setVisibility(View.VISIBLE);
                                JSONObject jsonObject = new JSONObject();
                                try {

                                    jsonObject.put(OSAConstants.PARAMS_MOBILE_NUMBER, PreferenceStorage.getMobileNo(getApplicationContext()));

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
                                String url = OSAConstants.BUILD_URL + OSAConstants.MOBILE_VERIFY;
                                serviceHelper.makeGetServiceCall(jsonObject.toString(), url);

                            }
                        });
                alertDialogBuilder.setNegativeButton(R.string.alert_button_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

//                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialogBuilder.show();

            } else if (view == btnConfirm) {
                if (otpEditText.hasValidOTP()) {
                    checkVerify = "Confirm";
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put(OSAConstants.PARAMS_MOBILE_NUMBER, PreferenceStorage.getMobileNo(getApplicationContext()));
                        jsonObject.put(OSAConstants.PARAMS_OTP, otpEditText.getOTP());
                        jsonObject.put(OSAConstants.PARAMS_GCM_KEY, PreferenceStorage.getGCM(getApplicationContext()));
                        jsonObject.put(OSAConstants.PARAMS_MOBILE_TYPE, "1");
                        jsonObject.put(OSAConstants.PARAMS_LOGIN_TYPE, "Mobile");
                        jsonObject.put(OSAConstants.PARAMS_LOGIN_PORTAL, "App");

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
                    String url = OSAConstants.BUILD_URL + OSAConstants.NUMBER_LOGIN;
                    serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
                } else {
                    
                }

            }
        } else {
            AlertDialogHelper.showSimpleAlertDialog(this, getString(R.string.error_no_net));
        }
    }

    @Override
    public void onAlertPositiveClicked(int tag) {

    }

    @Override
    public void onAlertNegativeClicked(int tag) {

    }

    private boolean validateResponse(JSONObject response) {
        boolean signInSuccess = false;
        if ((response != null)) {
            try {
                String status = response.getString("status");
                String msg = response.getString(OSAConstants.PARAM_MESSAGE);
                d(TAG, "status val" + status + "msg" + msg);

                if ((status != null)) {
                    if (((status.equalsIgnoreCase("activationError")) || (status.equalsIgnoreCase("alreadyRegistered")) ||
                            (status.equalsIgnoreCase("notRegistered")) || (status.equalsIgnoreCase("error")))) {
                        signInSuccess = false;
                        d(TAG, "Show error dialog");
                        AlertDialogHelper.showSimpleAlertDialog(this, msg);
                    } else {
                        signInSuccess = true;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return signInSuccess;
    }

    @Override
    public void onResponse(JSONObject response) {
        progressDialogHelper.hideProgressDialog();
        if (validateResponse(response)) {
            try {
                if (checkVerify.equalsIgnoreCase("Resend")) {

                    Toast.makeText(getApplicationContext(), "OTP resent successfully", Toast.LENGTH_SHORT).show();

                } else if (checkVerify.equalsIgnoreCase("Confirm") || checkVerify.equalsIgnoreCase("verified")) {
                    PreferenceStorage.setFirstTimeLaunch(getApplicationContext(), false);
                    PreferenceStorage.setMobileLogin(getApplicationContext(), true);
//                    Toast.makeText(getApplicationContext(), "Login successfully", Toast.LENGTH_SHORT).show();
                    JSONObject data = response.getJSONObject("userData");

                    String userId = data.getString("customer_id");
                    String fullName = data.getString("first_name");
                    String gender = data.getString("gender");
                    String profilePic = data.getString("profile_picture");
                    String email = data.getString("email");

                    PreferenceStorage.saveUserId(getApplicationContext(), userId);
                    PreferenceStorage.saveName(getApplicationContext(), fullName);
                    PreferenceStorage.saveGender(getApplicationContext(), gender);
                    PreferenceStorage.saveProfilePic(getApplicationContext(), profilePic);
                    PreferenceStorage.saveEmail(getApplicationContext(), email);

                    Intent homeIntent;
                    if (page.equalsIgnoreCase("product")) {
                        homeIntent = new Intent(getApplicationContext(), ProductDetailActivity.class);
                        homeIntent.putExtra("productObj", productID);
                    } else {
                        homeIntent = new Intent(getApplicationContext(), MainActivity.class);
                        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
////                    homeIntent.putExtra("profile_state", "new");
                    }
                    startActivity(homeIntent);
                    finish();

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onError(String error) {

    }

    class SmsBrReceiver extends BroadcastReceiver {

        private String parseCode(String message) {
            Pattern p = Pattern.compile("\\b\\d{4}\\b");
            Matcher m = p.matcher(message);
            String code = "";
            while (m.find()) {
                code = m.group(0);
            }
            return code;
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            if (SmsRetriever.SMS_RETRIEVED_ACTION.equals(intent.getAction())) {
                Bundle extras = intent.getExtras();
                Status status = (Status) extras.get(SmsRetriever.EXTRA_STATUS);

                switch (status.getStatusCode()) {
                    case CommonStatusCodes.SUCCESS:
                        // Get SMS message contents
                        String smsMessage = (String) extras.get(SmsRetriever.EXTRA_SMS_MESSAGE);
                        // Extract one-time code from the message and complete verification
                        // by sending the code back to your server.
                        Log.d(TAG, "Retrieved sms code: " + smsMessage);
                        if (smsMessage != null) {
                            String sms = parseCode(smsMessage);
                            verifyMessage(sms);
                        }
                        break;
                    case CommonStatusCodes.TIMEOUT:
                        // Waiting for SMS timed out (5 minutes)
                        // Handle the error ...
                        break;
                }
            }
        }

    }

    public void verifyMessage(String otp) {
        checkVerify = "verified";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(OSAConstants.PARAMS_MOBILE_NUMBER, PreferenceStorage.getMobileNo(getApplicationContext()));
            jsonObject.put(OSAConstants.PARAMS_OTP, otp);
            jsonObject.put(OSAConstants.PARAMS_GCM_KEY, PreferenceStorage.getGCM(getApplicationContext()));
            jsonObject.put(OSAConstants.PARAMS_MOBILE_TYPE, "1");
            jsonObject.put(OSAConstants.PARAMS_LOGIN_TYPE, "Mobile");
            jsonObject.put(OSAConstants.PARAMS_LOGIN_PORTAL, "App");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = OSAConstants.BUILD_URL + OSAConstants.NUMBER_LOGIN;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        View v = getCurrentFocus();

        if (v != null &&
                (ev.getAction() == MotionEvent.ACTION_UP || ev.getAction() == MotionEvent.ACTION_MOVE) &&
                v instanceof EditText &&
                !v.getClass().getName().startsWith("android.webkit.")) {
            int scrcoords[] = new int[2];
            v.getLocationOnScreen(scrcoords);
            float x = ev.getRawX() + v.getLeft() - scrcoords[0];
            float y = ev.getRawY() + v.getTop() - scrcoords[1];

            if (x < v.getLeft() || x > v.getRight() || y < v.getTop() || y > v.getBottom())
                hideKeyboard(this);
        }
        return super.dispatchTouchEvent(ev);
    }

    public static void hideKeyboard(Activity activity) {
        if (activity != null && activity.getWindow() != null && activity.getWindow().getDecorView() != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }
}

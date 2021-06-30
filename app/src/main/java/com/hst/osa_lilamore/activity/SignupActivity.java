package com.hst.osa_lilamore.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.phone.SmsRetriever;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;
import com.google.android.material.textfield.TextInputLayout;
import com.hst.osa_lilamore.R;
import com.hst.osa_lilamore.customview.CustomOtpEditText;
import com.hst.osa_lilamore.helpers.AlertDialogHelper;
import com.hst.osa_lilamore.helpers.ProgressDialogHelper;
import com.hst.osa_lilamore.interfaces.DialogClickListener;
import com.hst.osa_lilamore.servicehelpers.ServiceHelper;
import com.hst.osa_lilamore.serviceinterfaces.IServiceListener;
import com.hst.osa_lilamore.utils.CommonUtils;
import com.hst.osa_lilamore.utils.OSAConstants;
import com.hst.osa_lilamore.utils.OSAValidator;
import com.hst.osa_lilamore.utils.PreferenceStorage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.util.Log.d;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener, IServiceListener, DialogClickListener {
    private static final String TAG = SignupActivity.class.getName();

    private CustomOtpEditText otpEditText;
    private TextInputLayout tiName, tiNumber, tiEmail, tiPassword;
    private Button btnConfirm;
    private TextView btnChangeNumber;
    private String mobileNo;
    private String checkVerify;
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    private EditText txtName, txtNumber, txtEmail, txtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        Toolbar toolbar = (Toolbar)findViewById(R.id.activity_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_left_arrow));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        tiName = findViewById(R.id.ti_name);
        txtName = findViewById(R.id.txt_name);

        tiNumber = findViewById(R.id.ti_mobile_number);
        txtNumber = findViewById(R.id.txt_mobile_number);

        tiEmail = findViewById(R.id.ti_email);
        txtEmail = findViewById(R.id.txt_email);

        tiPassword = findViewById(R.id.ti_password);
        txtPassword = findViewById(R.id.txt_password);


        btnConfirm = (Button) findViewById(R.id.btn_signup);
        btnConfirm.setOnClickListener(this);

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onClick(View view) {

        if (CommonUtils.isNetworkAvailable(getApplicationContext())) {

             if (view == btnConfirm) {
                 if (validateFields()) {
                     checkVerify = "Confirm";
                     JSONObject jsonObject = new JSONObject();
                     try {
                         jsonObject.put(OSAConstants.KEY_NAME, txtName.getText().toString());
                         jsonObject.put(OSAConstants.PARAMS_MOBILE_NUMBER, txtNumber.getText().toString());
                         jsonObject.put(OSAConstants.PARAMS_EMAIL, txtEmail.getText().toString());
                         jsonObject.put(OSAConstants.PARAMS_PASSWORD, txtPassword.getText().toString());

                     } catch (JSONException e) {
                         e.printStackTrace();
                     }

                     progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
                     String url = OSAConstants.BUILD_URL + OSAConstants.REGISTER;
                     serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
                 }
             }
        } else {
            AlertDialogHelper.showSimpleAlertDialog(this, getString(R.string.error_no_net));
        }
    }

    private boolean validateFields() {
        if (!OSAValidator.checkNullString(this.txtName.getText().toString().trim())) {
            tiName.setError(getString(R.string.error_name));
            requestFocus(tiName);
            return false;
        }if (!OSAValidator.checkNullString(this.txtNumber.getText().toString().trim())) {
            tiNumber.setError(getString(R.string.error_number));
            requestFocus(txtNumber);
            return false;
        }
        if (!OSAValidator.checkMobileNumLength(this.txtNumber.getText().toString().trim())) {
            tiNumber.setError(getString(R.string.error_number_min));
            requestFocus(txtNumber);
            return false;
        }
        if (!OSAValidator.isEmailValid(this.txtEmail.getText().toString().trim())) {
            tiEmail.setError(getString(R.string.error_email));
            requestFocus(txtEmail);
            return false;
        } if (!OSAValidator.checkNullString(this.txtEmail.getText().toString().trim())) {
            tiEmail.setError(getString(R.string.error_email));
            requestFocus(txtEmail);
            return false;
        } if (!OSAValidator.checkNullString(this.txtPassword.getText().toString().trim())) {
            tiPassword.setError(getString(R.string.error_password));
            requestFocus(txtPassword);
            return false;
        } if (!OSAValidator.checkStringMinLength(6, this.txtPassword.getText().toString().trim())) {
            tiPassword.setError(getString(R.string.error_password_min));
            requestFocus(txtPassword);
            return false;
        } else {
            return true;
        }
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
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

                } else if (checkVerify.equalsIgnoreCase("Confirm")) {
                    PreferenceStorage.setFirstTimeLaunch(getApplicationContext(), false);
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

                    PreferenceStorage.saveUserId(getApplicationContext(), userId);
                    Intent homeIntent = new Intent(getApplicationContext(), MainActivity.class);
                    homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    homeIntent.putExtra("profile_state", "new");
                    startActivity(homeIntent);
//                    this.finish();
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

package com.hst.osa_lilamore.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputEditText;
import com.hst.osa_lilamore.R;
import com.hst.osa_lilamore.helpers.AlertDialogHelper;
import com.hst.osa_lilamore.helpers.ProgressDialogHelper;
import com.hst.osa_lilamore.interfaces.DialogClickListener;
import com.hst.osa_lilamore.servicehelpers.ServiceHelper;
import com.hst.osa_lilamore.serviceinterfaces.IServiceListener;
import com.hst.osa_lilamore.utils.OSAConstants;
import com.hst.osa_lilamore.utils.PreferenceStorage;

import org.json.JSONException;
import org.json.JSONObject;

import static android.util.Log.d;

public class ChangePasswordActivity extends AppCompatActivity implements IServiceListener, DialogClickListener {

    public static final String TAG = ChangePasswordActivity.class.getName();

    private TextInputEditText oldPass, newPass, cfmPass;
    Button reset;

    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;

    private String reStr;
    private String txt1, txt2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_left_arrow));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        oldPass = (TextInputEditText) findViewById(R.id.txt_old_password);
        newPass = (TextInputEditText) findViewById(R.id.txt_new_password);
        cfmPass = (TextInputEditText) findViewById(R.id.txt_confirm_password);
        reset = (Button) findViewById(R.id.btn_reset);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPassword();
            }
        });

        initiateServices();
//        checkPassword();

    }

    public void initiateServices() {
        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);
    }

    private boolean validateFields() {

        cfmPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                txt1 = newPass.getText().toString().trim();
                txt2 = cfmPass.getText().toString().trim();
                if (txt1.equals(txt2)) {
                    cfmPass.setError(getString(R.string.password_match));
                } else {
                    newPass.setError(getString(R.string.password_error));
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        return false;
    }

//    private void reqFocus(View view) {
//        if (view.requestFocus()) {
//            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
//        }
//    }

    private void checkPassword() {
        reStr = "checkPassword";
        JSONObject jsonObject = new JSONObject();
        String id = PreferenceStorage.getUserId(this);
        try {
            jsonObject.put(OSAConstants.KEY_USER_ID, id);
            jsonObject.put(OSAConstants.PARAMS_PASSWORD, oldPass.getText().toString().trim());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String serverUrl = OSAConstants.BUILD_URL + OSAConstants.CHECK_PASSWORD;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), serverUrl);
    }

    private void changePassword() {
        reStr = "changePassword";
        JSONObject jsonObject = new JSONObject();
        String id = PreferenceStorage.getUserId(this);
        if (validateFields()) {
            try {
                jsonObject.put(OSAConstants.KEY_USER_ID, id);
                jsonObject.put(OSAConstants.PARAMS_PASSWORD, txt1);
                jsonObject.put(OSAConstants.PARAMS_PASSWORD, txt2);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String serverUrl = OSAConstants.BUILD_URL + OSAConstants.CONFIRM_PASSWORD;
            serviceHelper.makeGetServiceCall(jsonObject.toString(), serverUrl);
        }
    }

    private boolean validateSignInResponse(JSONObject response) {
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
        if (validateSignInResponse(response)) {

            if (reStr.equalsIgnoreCase("checkPassword")) {
                changePassword();
            }
            if (reStr.equalsIgnoreCase("changePassword")) {
                Intent homeInt = new Intent(this, MainActivity.class);
                homeInt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeInt);
                finish();
            }
        }
    }

    @Override
    public void onError(String error) {

    }

    @Override
    public void onAlertPositiveClicked(int tag) {

    }

    @Override
    public void onAlertNegativeClicked(int tag) {

    }
}
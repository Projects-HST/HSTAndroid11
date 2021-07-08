package com.hst.osa_lilamore.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.hst.osa_lilamore.R;
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

import static android.util.Log.d;

public class SettingsActivity extends AppCompatActivity implements IServiceListener, View.OnClickListener,
        DialogClickListener {

    public static final String TAG = SettingsActivity.class.getName();

    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;

    private String resStr;
    private boolean isMobile = false;
    private ImageView passClick, abtClick, secClick, helpClick;
    private SwitchCompat subNotify, subNews;
    private RelativeLayout passWordLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_left_arrow));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        passWordLayout = (RelativeLayout)findViewById(R.id.changeLayout);
        passClick = (ImageView)findViewById(R.id.passClick);
        passClick.setOnClickListener(this);
        abtClick = (ImageView)findViewById(R.id.abtClick);
        abtClick.setOnClickListener(this);
        secClick = (ImageView)findViewById(R.id.privacyClick);
        secClick.setOnClickListener(this);
        helpClick = (ImageView)findViewById(R.id.helpClick);
        helpClick.setOnClickListener(this);
        subNotify = (SwitchCompat)findViewById(R.id.subNotification);
        subNews = (SwitchCompat)findViewById(R.id.subNews);

        boolean isMobile = PreferenceStorage.isMobileLogin(this);
       if (isMobile){
            PreferenceStorage.setMobileLogin(getApplicationContext(), true);
            passWordLayout.setVisibility(View.GONE);
        }
        else {
            PreferenceStorage.setMobileLogin(getApplicationContext(), false);
            passWordLayout.setVisibility(View.VISIBLE);
       }


        subNotify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                subNotify.isChecked();
                getNotification();
            }
        });

        subNews.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                getNewsLetter();
            }
        });
        initiateServices();
    }

    public void initiateServices() {
        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);
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

    private void getNotification() {

        if (CommonUtils.isNetworkAvailable(getApplicationContext())) {
            resStr = "notificationSubscription";
            JSONObject jsonObject = new JSONObject();
            String id = PreferenceStorage.getUserId(this);
            boolean state = subNotify.isChecked();
            String status = "";

            if (state){
                status = "Y";
            } else {
                status = "N";
            }
            try {
                jsonObject.put(OSAConstants.KEY_USER_ID, id);
                jsonObject.put(OSAConstants.KEY_STATUS, status);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String url = OSAConstants.BUILD_URL + OSAConstants.NOTIFICATION_SUBSCRIPTION;
            serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
        } else {
            AlertDialogHelper.showSimpleAlertDialog(this, getString(R.string.error_no_net));
        }
    }

    private void getNewsLetter() {

        if (CommonUtils.isNetworkAvailable(getApplicationContext())) {
            resStr = "newsSubscription";
            JSONObject jsonObject = new JSONObject();
            String id = PreferenceStorage.getUserId(this);
            boolean state = subNews.isChecked();
            String status = "";

            if (state){
                status = "Y";
            } else {
                status = "N";
            }
            try {
                jsonObject.put(OSAConstants.KEY_USER_ID, id);
                jsonObject.put(OSAConstants.KEY_STATUS, status);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String url = OSAConstants.BUILD_URL + OSAConstants.NEWS_SUBSCRIPTION;
            serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
        } else {
            AlertDialogHelper.showSimpleAlertDialog(this, getString(R.string.error_no_net));
        }
    }


    @Override
    public void onResponse(JSONObject response) {

        if (validateSignInResponse(response)){

            if (resStr.equalsIgnoreCase("notificationSubscription")){

            }
        }
    }

    @Override
    public void onError(String error) {

    }

    @Override
    public void onClick(View v) {

        if (v == passClick){
            Intent passInt = new Intent(this, ChangePasswordActivity.class);
            startActivity(passInt);
        }
        if (v == abtClick){

        }
        if (v == secClick){

        }
        if (v == helpClick){

        }

    }

    @Override
    public void onAlertPositiveClicked(int tag) {

    }

    @Override
    public void onAlertNegativeClicked(int tag) {

    }
}
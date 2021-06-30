package com.hst.osa_lilamore.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.hst.osa_lilamore.R;
import com.hst.osa_lilamore.helpers.AlertDialogHelper;
import com.hst.osa_lilamore.helpers.ProgressDialogHelper;
import com.hst.osa_lilamore.interfaces.DialogClickListener;
import com.hst.osa_lilamore.servicehelpers.ServiceHelper;
import com.hst.osa_lilamore.serviceinterfaces.IServiceListener;
import com.hst.osa_lilamore.utils.OSAConstants;
import com.hst.osa_lilamore.utils.OSAValidator;
import com.hst.osa_lilamore.utils.PreferenceStorage;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener, IServiceListener, DialogClickListener {

    private static final String TAG = LoginActivity.class.getName();

    private TextView btnFacebook, btnGoogle, txtUseEmail, txtUseNumber, txtSignUp;
    private TextInputLayout tiNumber, tiEmail, tiPassword;
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    private CallbackManager callbackManager;
    private LoginManager loginManager;
    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 9001;
    private int mSelectedLoginMode = 0;
    private String whichService = "", url = "", loginMethod = "number";
    private boolean isMobileLogin = true;

    private Button btnContinue;
    private EditText txtNumber, txtEmail, txtPassword;

    private RelativeLayout layoutNumber, layoutEmail;

    String page = "", productID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        page = getIntent().getExtras().getString("page");
        productID = getIntent().getExtras().getString("productObj");

        FacebookSdk.sdkInitialize(getApplicationContext());

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_left_arrow));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ProductDetailActivity.class);
                i.putExtra("productObj", productID);
                startActivity(i);
                finish();
            }
        });


        txtUseEmail = findViewById(R.id.mail_login);
        txtUseNumber = findViewById(R.id.ph_login);
        txtUseEmail.setOnClickListener(this);
        txtUseNumber.setOnClickListener(this);

        layoutNumber = findViewById(R.id.layout_number);
        layoutEmail = findViewById(R.id.layout_email);

        tiNumber = findViewById(R.id.ti_mobile_number);
        txtNumber = findViewById(R.id.txt_mobile_number);

        tiEmail = findViewById(R.id.ti_email);
        txtEmail = findViewById(R.id.txt_email);

        tiPassword = findViewById(R.id.ti_password);
        txtPassword = findViewById(R.id.txt_password);

        btnContinue = findViewById(R.id.btn_login);
        btnFacebook = findViewById(R.id.btn_fb_login);
        btnGoogle = findViewById(R.id.btn_gg_login);
        btnContinue.setOnClickListener(this);
        btnFacebook.setOnClickListener(this);
        btnGoogle.setOnClickListener(this);

        txtSignUp = findViewById(R.id.txt_signUp);
        txtSignUp.setOnClickListener(this);

        try {
            PackageInfo info = getApplicationContext().getPackageManager().getPackageInfo(
                    "com.hst.osa",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
        callbackManager = CallbackManager.Factory.create();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);

        progressDialogHelper = new ProgressDialogHelper(this);

//        facebookLogin();
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
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    @Override
    public void onClick(View v) {

        if (v == btnContinue) {
            if (loginMethod.equalsIgnoreCase("number")) {
                if (validateFields()) {
                    PreferenceStorage.saveMobileNo(this, txtNumber.getText().toString());
                    continueWithNumber();
                }
            } else {
                if (validateFieldEmail()) {
                    PreferenceStorage.saveEmail(this, txtEmail.getText().toString());
                    continueWithEmail();
                }
            }
        }
        if (v == btnGoogle) {
            signIn();
        }
        if (v == btnFacebook) {
            loginManager.logInWithReadPermissions(LoginActivity.this, (Arrays.asList("email", "public_profile")));
//            PreferenceStorage.saveLoginMode(this, OSAConstants.FACEBOOK);
//            mSelectedLoginMode = OSAConstants.FACEBOOK;
        }
        if (v == txtUseEmail) {
            loginMethod = "email";
            layoutEmail.setVisibility(View.VISIBLE);
            layoutNumber.setVisibility(View.GONE);
        }
        if (v == txtUseNumber) {
            loginMethod = "number";
            layoutEmail.setVisibility(View.GONE);
            layoutNumber.setVisibility(View.VISIBLE);
        }
        if (v == txtSignUp) {
            Intent i = new Intent(getApplicationContext(), SignupActivity.class);
            i.putExtra("page", page);
            i.putExtra("product_id", productID);
            startActivity(i);
        }
    }

    private boolean validateFields() {
        if (!OSAValidator.checkNullString(this.txtNumber.getText().toString().trim())) {
            tiNumber.setError(getString(R.string.error_number));
            requestFocus(txtNumber);
            return false;
        }
        if (!OSAValidator.checkMobileNumLength(this.txtNumber.getText().toString().trim())) {
            tiNumber.setError(getString(R.string.error_number_min));
            requestFocus(txtNumber);
            return false;
        } else {
            return true;
        }
    }

    private boolean validateFieldEmail() {
        if (!OSAValidator.isEmailValid(this.txtEmail.getText().toString().trim())) {
            tiEmail.setError(getString(R.string.error_email));
            requestFocus(txtEmail);
            return false;
        }
        if (!OSAValidator.checkNullString(this.txtEmail.getText().toString().trim())) {
            tiEmail.setError(getString(R.string.error_email));
            requestFocus(txtEmail);
            return false;
        }
        if (!OSAValidator.checkNullString(this.txtPassword.getText().toString().trim())) {
            tiPassword.setError(getString(R.string.error_password));
            requestFocus(txtPassword);
            return false;
        }
        if (!OSAValidator.checkStringMinLength(6, this.txtPassword.getText().toString().trim())) {
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

    private void continueWithNumber() {
        whichService = "mobile";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(OSAConstants.PARAMS_MOBILE_NUMBER, txtNumber.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String serverURL = OSAConstants.BUILD_URL + OSAConstants.MOBILE_VERIFY;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), serverURL);
    }

    private void continueWithEmail() {
        whichService = "email";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(OSAConstants.PARAMS_EMAIL, txtEmail.getText().toString());
            jsonObject.put(OSAConstants.PARAMS_PASSWORD, txtPassword.getText().toString());
            jsonObject.put(OSAConstants.PARAMS_GCM_KEY, PreferenceStorage.getGCM(getApplicationContext()));
            jsonObject.put(OSAConstants.PARAMS_MOBILE_TYPE, "1");
            jsonObject.put(OSAConstants.PARAMS_LOGIN_TYPE, "Mobile");
            jsonObject.put(OSAConstants.PARAMS_LOGIN_PORTAL, "App");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = OSAConstants.BUILD_URL + OSAConstants.EMAIL_LOGIN;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    private void sendGoogleLogin(GoogleSignInAccount account) {
        whichService = "google";
        String first = "" + account.getGivenName();
        String last = "" + account.getFamilyName();
        String mail = "" + account.getEmail();
        String ggId = "" + account.getId();
        String photoUrl = "" + account.getPhotoUrl();

        Log.d(TAG, "id" + ggId);
        PreferenceStorage.saveSocialNetworkProfilePic(getApplicationContext(), photoUrl);

        String GCMKey = PreferenceStorage.getGCM(this);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(OSAConstants.PARAMS_EMAIL, mail);
            jsonObject.put(OSAConstants.PARAMS_FIRST_NAME, first);
            jsonObject.put(OSAConstants.PARAMS_LAST_NAME, last);
            jsonObject.put(OSAConstants.PARAMS_GCM_KEY, GCMKey);
            jsonObject.put(OSAConstants.PARAMS_LOGIN_TYPE, "Gplus");
            jsonObject.put(OSAConstants.PARAMS_MOBILE_TYPE, "1");
            jsonObject.put(OSAConstants.PARAMS_LOGIN_PORTAL, "App");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String serverURL = OSAConstants.BUILD_URL + OSAConstants.FB_GPLUS_LOGIN;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), serverURL);
    }

    private void signIn() {

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if (account != null) {
            sendGoogleLogin(account);
        } else {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // [START_EXCLUDE]
//                        updateUI(false);
                        // [END_EXCLUDE]
                    }
                });
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            String name = "" + account.getDisplayName();
            String first = "" + account.getGivenName();
            String last = "" + account.getFamilyName();
            String mail = "" + account.getEmail();
            String googleId = "" + account.getId();
            String photoUrl = "" + account.getPhotoUrl();

            Log.d(TAG, "name" + name + "id" + googleId);
            PreferenceStorage.saveSocialNetworkProfilePic(getApplicationContext(), photoUrl);

            String GCMKey = PreferenceStorage.getGCM(this);
            whichService = "google";
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put(OSAConstants.PARAMS_EMAIL, mail);
                jsonObject.put(OSAConstants.PARAMS_FIRST_NAME, first);
                jsonObject.put(OSAConstants.PARAMS_LAST_NAME, last);
                jsonObject.put(OSAConstants.PARAMS_GCM_KEY, GCMKey);
                jsonObject.put(OSAConstants.PARAMS_LOGIN_TYPE, "Gplus");
                jsonObject.put(OSAConstants.PARAMS_MOBILE_TYPE, "1");
                jsonObject.put(OSAConstants.PARAMS_LOGIN_PORTAL, "App");
            } catch (JSONException e) {
                e.printStackTrace();
            }

            progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
            String serverURL = OSAConstants.BUILD_URL + OSAConstants.FB_GPLUS_LOGIN;
            serviceHelper.makeGetServiceCall(jsonObject.toString(), serverURL);
            // Signed in successfully, show authenticated UI.
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }

//    public void facebookLogin() {
//
//        loginManager = LoginManager.getInstance();
////        callbackManager = CallbackManager.Factory.create();
//
//        loginManager.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
//            //        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                AccessToken accessToken = loginResult.getAccessToken();
//                GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
//                    @Override
//                    public void onCompleted(JSONObject object, GraphResponse response) {
//                        if (object != null) {
//                            String first = object.optString("first_name");
//                            String last = object.optString("last_name");
//                            String email = object.optString("email");
//                            String fbUserID = object.optString("id");
//                            String gender = object.optString("gender");
//                            String birthday = object.optString("birthday");
//
//                            Log.d(TAG, "email" + email + "facebook gender" + gender + "birthday" + birthday);
//
//                            url = "https://graph.facebook.com/" + fbUserID + "/picture?type=large";
////                            disconnectFromFacebook();
//                            // do action after Facebook login success
//                            // or call your API
////                            PreferenceStorage.saveSocialNetworkProfilePic(getApplicationContext(), url);
//                            whichService = "google";
//
//                            String GCMKey = PreferenceStorage.getGCM(getApplicationContext());
//                            JSONObject jsonObject = new JSONObject();
//                            try {
//                                jsonObject.put(OSAConstants.PARAMS_EMAIL, email);
//                                jsonObject.put(OSAConstants.PARAMS_FIRST_NAME, first);
//                                jsonObject.put(OSAConstants.PARAMS_LAST_NAME, last);
//                                jsonObject.put(OSAConstants.PARAMS_GCM_KEY, GCMKey);
//                                jsonObject.put(OSAConstants.PARAMS_LOGIN_TYPE, "FB");
//                                jsonObject.put(OSAConstants.PARAMS_MOBILE_TYPE, "1");
//                                jsonObject.put(OSAConstants.PARAMS_LOGIN_PORTAL, "App");
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                            progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
//                            String serverURL = OSAConstants.BUILD_URL + OSAConstants.FB_GPLUS_LOGIN;
//                            serviceHelper.makeGetServiceCall(jsonObject.toString(), serverURL);
//                        }
//                    }
//                });
//                Bundle parameters = new Bundle();
//                parameters.putString("fields", "id,name,first_name,last_name,email,gender,birthday");
//                request.setParameters(parameters);
//                request.executeAsync();
//            }
//
//            @Override
//            public void onCancel() {
//                Log.v("LoginScreen", "---onCancel");
//            }
//
//            @Override
//            public void onError(FacebookException error) {
//                // here write code when get error
//                Log.v("LoginScreen", "----onError: " + error.getMessage());
//            }
//        });
//    }

    public void disconnectFromFacebook() {
        if (AccessToken.getCurrentAccessToken() == null) {
            return; // already logged out
        }
        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/",
                null, HttpMethod.DELETE, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {
                LoginManager.getInstance().logOut();
            }
        }).executeAsync();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // add this line
        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        } else {
            // Signed out, show unauthenticated UI.
//                tvDetails.setText("error occured..!");
//                updateUI(false);
            String okSet = "0";
            String newOk = okSet;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private boolean validateResponse(JSONObject response) {
        boolean signInSuccess = false;
        if ((response != null)) {
            try {
                String status = response.getString("status");
                String msg = response.getString(OSAConstants.PARAM_MESSAGE);
                Log.d(TAG, "status val" + status + "msg" + msg);

                if ((status != null)) {
                    if (status.equalsIgnoreCase("success")) {
                        signInSuccess = true;
                    } else {
                        signInSuccess = false;
                        Log.d(TAG, "Show error dialog");
                        AlertDialogHelper.showSimpleAlertDialog(this, msg);
                    }
//                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
//                    sharedPreferences.edit().clear().apply();
//                    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                            .requestEmail()
//                            .build();
//                    // Build a GoogleSignInClient with the options specified by gso.
//                    LoginManager.getInstance().logOut();
//                    mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
//                    mGoogleSignInClient.signOut();
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
            if (whichService.equalsIgnoreCase("mobile")) {
                Intent i = new Intent(getApplicationContext(), NumberVerificationActivity.class);
                i.putExtra("page", page);
                i.putExtra("productObj", productID);
                startActivity(i);

            }
            if (whichService.equalsIgnoreCase("google")) {
                Intent i;
                if (page.equalsIgnoreCase("product")) {
                    i = new Intent(getApplicationContext(), ProductDetailActivity.class);
                    i.putExtra("productObj", productID);
                } else {
                    i = new Intent(getApplicationContext(), MainActivity.class);
                }
                startActivity(i);
                finish();
            }
            if (whichService.equalsIgnoreCase("email")) {
                PreferenceStorage.setFirstTimeLaunch(getApplicationContext(), false);
//                    Toast.makeText(getApplicationContext(), "Login successfully", Toast.LENGTH_SHORT).show();
                JSONObject data = null;
                try {
                    data = response.getJSONObject("userData");
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

                } catch (JSONException e) {
                    e.printStackTrace();
                }
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

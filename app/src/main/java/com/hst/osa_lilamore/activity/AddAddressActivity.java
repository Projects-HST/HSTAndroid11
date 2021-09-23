package com.hst.osa_lilamore.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.hst.osa_lilamore.R;
import com.hst.osa_lilamore.bean.support.AddressList;
import com.hst.osa_lilamore.helpers.AlertDialogHelper;
import com.hst.osa_lilamore.helpers.ProgressDialogHelper;
import com.hst.osa_lilamore.interfaces.DialogClickListener;
import com.hst.osa_lilamore.servicehelpers.ServiceHelper;
import com.hst.osa_lilamore.serviceinterfaces.IServiceListener;
import com.hst.osa_lilamore.utils.OSAConstants;
import com.hst.osa_lilamore.utils.OSAValidator;
import com.hst.osa_lilamore.utils.PreferenceStorage;


import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.util.Log.d;

public class AddAddressActivity extends AppCompatActivity implements IServiceListener, DialogClickListener, View.OnClickListener {

    private static final String TAG = AddAddressActivity.class.getName();
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;

    private TextView location, save;
    private TextInputEditText cus_name, cus_mobile, address1, address2, area, state, pinCode;

    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();
    private List<Address> addressList;

    private AddressList addressArrayList;

    String lat, lng;
    private String resStr = "";
    private String addId = "";
    private String fullName = "";
    private String mobNum = "";
    private String addDetailLine1 = "";
    private String addDetailLine2 = "";
    private String cityDetail = "";
    private String stateDetail = "";
    private String postalCode = "";

    int pos;

    Geocoder geocoder;
    private FusedLocationProviderClient fusedLocationClient;
    private GoogleMap mMap;
    private LocationManager locationManager;
    LocationListener listener = new MyLocationListener();
    private SwitchCompat defaultAddress;

    private boolean enable_gps = false;
    private boolean enable_network = false;
    private static final String MAP_VIEW_BUNDLE_KEY = "MapViewBundleKey";
    private static final int ALL_PERMISSIONS_RESULT = 1011;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_left_arrow));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        location = (TextView) findViewById(R.id.location);
        cus_name = (TextInputEditText) findViewById(R.id.txt_name);
//        cus_name.setEnabled(false);
        cus_mobile = (TextInputEditText) findViewById(R.id.txt_mobile_number);
//        cus_mobile.setEnabled(false);
        address1 = (TextInputEditText) findViewById(R.id.addLine1);
//        address1.setEnabled(false);
        address2 = (TextInputEditText) findViewById(R.id.addLine2);
//        address2.setEnabled(false);
        area = (TextInputEditText) findViewById(R.id.st_name);
//        area.setEnabled(false);
        state = (TextInputEditText) findViewById(R.id.city_name);
//        state.setEnabled(false);
        pinCode = (TextInputEditText) findViewById(R.id.pincode);
//        pinCode.setEnabled(false);
        defaultAddress = (SwitchCompat) findViewById(R.id.def_address);
        save = (TextView) findViewById(R.id.save);

        save.setOnClickListener(this);
        location.setOnClickListener(this);

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);

        initViews();
    }

    private void initViews() {

        defaultAddress.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                getDefaultAddress();
                defaultAddress.isChecked();
            }
        });

        Intent get = getIntent();
        Bundle bundle = get.getExtras();
        if (bundle != null) {
            addressArrayList = (AddressList)bundle.getSerializable("addressObj");
            addId = addressArrayList.getId();
            fullName = addressArrayList.getFull_name();
            cus_name.setText(fullName);
            mobNum = addressArrayList.getMobile_number();
            cus_mobile.setText(mobNum);
            addDetailLine1 = addressArrayList.getHouse_no();
            address1.setText(addDetailLine1);
            addDetailLine2 = addressArrayList.getStreet();
            address2.setText(addDetailLine2);
            cityDetail = addressArrayList.getCity();
            area.setText(cityDetail);
            stateDetail = addressArrayList.getState();
            state.setText(stateDetail);
            postalCode = addressArrayList.getPincode();
            pinCode.setText(postalCode);
            defaultAddress.setVisibility(View.GONE);
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        checkPermissions();
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

    private void addAddress() {
        resStr = "addAddress";
        if (validateFields()) {
            JSONObject jsonObject = new JSONObject();
            String id = PreferenceStorage.getUserId(this);
            try {
                jsonObject.put(OSAConstants.KEY_USER_ID, id);
                jsonObject.put(OSAConstants.KEY_COUNTRY_ID, "1");
                jsonObject.put(OSAConstants.KEY_STATE, state.getText().toString());
                jsonObject.put(OSAConstants.KEY_CITY, area.getText().toString());
                jsonObject.put(OSAConstants.KEY_PIN_CODE, pinCode.getText().toString());
                jsonObject.put(OSAConstants.KEY_ADD_1, address1.getText().toString());
                jsonObject.put(OSAConstants.KEY_ADD_2, address2.getText().toString());
                jsonObject.put(OSAConstants.KEY_LANDMARK, "");
                jsonObject.put(OSAConstants.KEY_FULL_NAME, cus_name.getText().toString());
                jsonObject.put(OSAConstants.KEY_MOB_NUM, cus_mobile.getText().toString());
                jsonObject.put(OSAConstants.KEY_ALT_MOB_NUM, "");
                jsonObject.put(OSAConstants.KEY_EMAIL_ADDRESS, "");
                jsonObject.put(OSAConstants.KEY_ADDRESS_TYPE, OSAConstants.TYPE_HOME);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String serverUrl = OSAConstants.BUILD_URL + OSAConstants.ADD_ADDRESS;
            serviceHelper.makeGetServiceCall(jsonObject.toString(), serverUrl);
        }
    }

    private void editAddress() {
        resStr = "editAddress";
//        defaultAddress.setVisibility(View.GONE);
        if (validateFields()) {
//            addId = getIntent().getStringExtra("addressObj");
            JSONObject jsonObject = new JSONObject();
            String id = PreferenceStorage.getUserId(this);
            try {
                jsonObject.put(OSAConstants.KEY_USER_ID, id);
                jsonObject.put(OSAConstants.KEY_ADDRESS_ID, addId);
                jsonObject.put(OSAConstants.KEY_COUNTRY_ID, "1");
                jsonObject.put(OSAConstants.KEY_STATE, state.getText().toString());
                jsonObject.put(OSAConstants.KEY_CITY, area.getText().toString());
                jsonObject.put(OSAConstants.KEY_PIN_CODE, pinCode.getText().toString());
                jsonObject.put(OSAConstants.KEY_ADD_1, address1.getText().toString());
                jsonObject.put(OSAConstants.KEY_ADD_2, address2.getText().toString());
                jsonObject.put(OSAConstants.KEY_LANDMARK, "");
                jsonObject.put(OSAConstants.KEY_FULL_NAME, cus_name.getText().toString());
                jsonObject.put(OSAConstants.KEY_MOB_NUM, cus_mobile.getText().toString());
                jsonObject.put(OSAConstants.KEY_ALT_MOB_NUM, "");
                jsonObject.put(OSAConstants.KEY_EMAIL_ADDRESS, "");
                jsonObject.put(OSAConstants.KEY_ADDRESS_TYPE, OSAConstants.TYPE_HOME);
                jsonObject.put(OSAConstants.KEY_STATUS, "");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            String serverUrl = OSAConstants.BUILD_URL + OSAConstants.EDIT_ADDRESS;
            serviceHelper.makeGetServiceCall(jsonObject.toString(), serverUrl);
        }
    }

    public class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(@NonNull Location location) {

            locationManager.removeUpdates(listener);
            lat = "" + location.getLatitude();
            lng = "" + location.getLongitude();

            PreferenceStorage.saveLat(getApplicationContext(), lat);
            PreferenceStorage.saveLng(getApplicationContext(), lng);

            geocoder = new Geocoder(AddAddressActivity.this, Locale.getDefault());
            try {
                addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 2);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String add1 = addressList.get(0).getAddressLine(0);
            address1.setEnabled(true);
            address1.setText(add1);
            String add2 = addressList.get(1).getAddressLine(0);
            address2.setEnabled(true);
            address2.setText(add2);
            String city = addressList.get(0).getSubAdminArea();
            area.setEnabled(true);
            area.setText(city);
            String stateName = addressList.get(0).getAdminArea();
            state.setEnabled(true);
            state.setText(stateName);
            String postal = addressList.get(0).getPostalCode();
            pinCode.setEnabled(true);
            pinCode.setText(postal);
        }

        @Override
        public void onProviderEnabled(@NonNull String provider) {

        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }
    }

    private ArrayList<String> permissionsToRequest(ArrayList<String> permissions) {

        ArrayList<String> result = new ArrayList<>();

        for (String perm : permissions) {
            if (!hasPermission(perm)) {
                result.add(perm);
            }
        }
        return result;
    }

    private boolean hasPermission(String perm) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return checkSelfPermission(perm) == PackageManager.PERMISSION_GRANTED;
        }
        return true;
    }

    private boolean validateFields() {
        if (!OSAValidator.checkMobileNumLength(this.cus_mobile.getText().toString().trim())) {
            cus_mobile.setError(getString(R.string.empty_entry));
            requestFocus(cus_mobile);
            return false;
        }
        if (!OSAValidator.checkNullString(this.cus_mobile.getText().toString().trim())) {
            cus_mobile.setError(getString(R.string.empty_entry));
            requestFocus(cus_mobile);
            return false;
        }
        if (!OSAValidator.checkNullString(this.cus_name.getText().toString().trim())) {
            cus_name.setError(getString(R.string.empty_entry));
            requestFocus(cus_name);
            return false;
        }
        if (!OSAValidator.checkNullString(this.address1.getText().toString().trim())) {
//            customerAddress.setError(getString(R.string.empty_address));
//            requestFocus(customerAddress);
            showDialog();
            return false;
        }
        if (!OSAValidator.checkNullString(this.area.getText().toString().trim())) {
            area.setError(getString(R.string.empty_locality));
            requestFocus(area);
            return false;
        }
        if (!OSAValidator.checkNullString(this.state.getText().toString().trim())) {
            state.setError(getString(R.string.empty_entry));
            requestFocus(state);
            return false;
        }
        if (!OSAValidator.checkNullString(this.pinCode.getText().toString().trim())) {
            pinCode.setError(getString(R.string.empty_entry));
            requestFocus(pinCode);
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

            if (resStr.equalsIgnoreCase("addAddress")) {
                try {
                    String id = "";
                    id = response.getString("address_id");
                    PreferenceStorage.saveAddressId(this, id);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Intent addressInt = new Intent(this, ShippingAddressActivity.class);
                startActivity(addressInt);
            }
        }
        if (resStr.equalsIgnoreCase("editAddress")) {
            Intent addressInt = new Intent(this, ShippingAddressActivity.class);
            startActivity(addressInt);
        }
        if (resStr.equalsIgnoreCase("sel_def_address")) {
            Log.d(TAG, response.toString());
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

    @Override
    public void onClick(View v) {

        if (v == location) {
            getCurrentLocation();
        }

        if (v == save) {
            if (!addId.equals("")) {
                editAddress();
            }
            else {
                addAddress();
            }
        }
    }
    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void getCurrentLocation() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();

        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
//        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, listener);
        progressDialogHelper.showProgressDialog("Getting Location");
        CancellationToken cancellationToken = new CancellationToken() {
            @Override
            public boolean isCancellationRequested() {
                return false;
            }

            @NonNull
            @NotNull
            @Override
            public CancellationToken onCanceledRequested(@NonNull @NotNull OnTokenCanceledListener onTokenCanceledListener) {
                return null;
            }
        };
        fusedLocationClient.getCurrentLocation(100, cancellationToken)
                .addOnCompleteListener(new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        progressDialogHelper.cancelProgressDialog();
                        if (task.isSuccessful() && task.getResult() != null) {
                            Location location;
                            location = task.getResult();
                            lat = "" + location.getLatitude();
                            lng = "" + location.getLongitude();

                            PreferenceStorage.saveLat(getApplicationContext(), lat);
                            PreferenceStorage.saveLng(getApplicationContext(), lng);

                            geocoder = new Geocoder(AddAddressActivity.this, Locale.getDefault());
                            try {
                                addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 2);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            String add1 = addressList.get(0).getAddressLine(0);
                            address1.setEnabled(true);
                            address1.setText(add1);
                            String add2 = addressList.get(1).getAddressLine(0);
                            address2.setEnabled(true);
                            address2.setText(add2);
                            String city = addressList.get(0).getSubAdminArea();
                            area.setEnabled(true);
                            area.setText(city);
                            String stateName = addressList.get(0).getAdminArea();
                            state.setEnabled(true);
                            state.setText(stateName);
                            String postal = addressList.get(0).getPostalCode();
                            pinCode.setEnabled(true);
                            pinCode.setText(postal);
                        } else {
                            Log.w(TAG, "Failed to get location.");
                        }
                    }
                });

    }

    private boolean checkPermissions() {

        permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
        permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);

        permissionsToRequest = permissionsToRequest(permissions);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0) {
                requestPermissions(permissionsToRequest.toArray(
                        new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
            }
        }
        return true;
    }

    private void showDialog() {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this, R.style.alertDialogueTheme);
        alertDialogBuilder.setTitle("Location");
        alertDialogBuilder.setMessage(R.string.empty_address);
        alertDialogBuilder.setPositiveButton(R.string.alert_button_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialogBuilder.show();
    }
}

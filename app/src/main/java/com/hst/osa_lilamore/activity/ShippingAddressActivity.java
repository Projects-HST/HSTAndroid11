package com.hst.osa_lilamore.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.hst.osa_lilamore.R;
import com.hst.osa_lilamore.adapter.AddressListAdapter;
import com.hst.osa_lilamore.bean.support.AddressArrayList;
import com.hst.osa_lilamore.bean.support.AddressList;
import com.hst.osa_lilamore.helpers.AlertDialogHelper;
import com.hst.osa_lilamore.helpers.ProgressDialogHelper;
import com.hst.osa_lilamore.interfaces.DialogClickListener;
import com.hst.osa_lilamore.servicehelpers.ServiceHelper;
import com.hst.osa_lilamore.serviceinterfaces.IServiceListener;
import com.hst.osa_lilamore.utils.OSAConstants;
import com.hst.osa_lilamore.utils.PreferenceStorage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.util.Log.d;

public class ShippingAddressActivity extends AppCompatActivity implements IServiceListener, DialogClickListener, View.OnClickListener, AddressListAdapter.OnItemClickListener {

    private static final String TAG = ShippingAddressActivity.class.getName();
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;

    private ArrayList<AddressList> addressArrayList = new ArrayList<>();
    AddressArrayList arrayList;
    AddressList addressList;

    private String addressId;
    private String resStr;
    private String page;
    private Context context;
    RadioButton radioButton;
    int pos;
    RecyclerView recyclerAddList;
    private Button add, next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shipping_address);

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_left_arrow));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("addressMode"));

        add = (Button) findViewById(R.id.btnAdd);
        next = (Button) findViewById(R.id.cont);
        recyclerAddList = (RecyclerView) findViewById(R.id.addList);

        add.setOnClickListener(this);
        next.setOnClickListener(this);

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);

        showAddressList();
    }

    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            addressId = intent.getStringExtra("addId");
        }
    };

    private void showAddressList() {
        resStr = "addressList";
//        recentSearchLay.setVisibility(View.VISIBLE);
//        serviceCall = "recentSearch";
        JSONObject jsonObject = new JSONObject();
        String id = PreferenceStorage.getUserId(this);
        try {
            jsonObject.put(OSAConstants.KEY_USER_ID, id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = OSAConstants.BUILD_URL + OSAConstants.ADDRESS_LIST;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    private void setDefaultAddress() {

//        Intent get =  getIntent();
//        Bundle bundle = get.getExtras();
//        if (bundle != null){
//            addressList = (AddressList)bundle.getSerializable("addressObj");
//            addressId = addressList.getId();
//        }
        resStr = "setDefault";
        JSONObject jsonObject = new JSONObject();
        String id = PreferenceStorage.getUserId(this);
        try {
            jsonObject.put(OSAConstants.KEY_USER_ID, id);
            jsonObject.put(OSAConstants.KEY_ADDRESS_ID, addressId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = OSAConstants.BUILD_URL + OSAConstants.DEFAULT_ADDRESS;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
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

    public void reLoadPage() {
        finish();
        startActivity(getIntent());
    }

    @Override
    public void onResponse(JSONObject response) {

        if (validateSignInResponse(response)) {
            if (resStr.equalsIgnoreCase("addressList")) {
                try {
                    Gson gson = new Gson();
                    arrayList = gson.fromJson(response.toString(), AddressArrayList.class);
                    addressArrayList.addAll(arrayList.getAddressArrayList());
                    AddressListAdapter aladapter = new AddressListAdapter(this, addressArrayList, this);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                    recyclerAddList.setLayoutManager(layoutManager);
                    recyclerAddList.setAdapter(aladapter);
                } catch (JsonSyntaxException e) {
                    e.printStackTrace();
                }
            }
            if (resStr.equalsIgnoreCase("setDefault")) {
                Intent checkInt;
//                if (resStr.equalsIgnoreCase("shippingAddress")) {
                    checkInt = new Intent(this, MainActivity.class);
                    checkInt.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(checkInt);
                    finish();
//                }
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

    @Override
    public void onClick(View v) {

        if (v == add) {
            Intent addInt = new Intent(this, AddAddressActivity.class);
            startActivity(addInt);
        }

        if (v == next) {
            setDefaultAddress();
        }
    }

    @Override
    public void onItemClickAddress(View view, int position) {

    }
}
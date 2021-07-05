package com.hst.osa_lilamore.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.hst.osa_lilamore.R;
import com.hst.osa_lilamore.adapter.NotificationListAdapter;
import com.hst.osa_lilamore.bean.support.ProductOffer;
import com.hst.osa_lilamore.bean.support.ProductOfferList;
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

public class NotificationActivity extends AppCompatActivity implements IServiceListener, DialogClickListener, NotificationListAdapter.OnItemClickListener, View.OnClickListener {

    public static final String TAG = ChangePasswordActivity.class.getName();

    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;

    private ArrayList<ProductOffer> productOfferArrayList = new ArrayList<>();
    ProductOfferList productOfferList;
    RecyclerView recyclerNotifyList;

    private LinearLayout notifyList;
    private RelativeLayout noNotification;
    private Button returnPage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_left_arrow));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        noNotification = (RelativeLayout) findViewById(R.id.no_notify);
        notifyList = (LinearLayout) findViewById(R.id.notify_history);
        recyclerNotifyList = (RecyclerView) findViewById(R.id.newNotifyList);
        returnPage = (Button) findViewById(R.id.returnPage);
        returnPage.setOnClickListener(this);

        if (productOfferArrayList != null) {
            noNotification.setVisibility(View.GONE);
            notifyList.setVisibility(View.VISIBLE);
        } else {
            noNotification.setVisibility(View.VISIBLE);
        }
        initiateServices();
        checkNotification();
    }

    public void initiateServices() {
        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);
    }

    private void checkNotification() {

        JSONObject jsonObject = new JSONObject();
        String id = PreferenceStorage.getUserId(this);
        try {
            jsonObject.put(OSAConstants.KEY_USER_ID, id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String serverUrl = OSAConstants.BUILD_URL + OSAConstants.NOTIFICATION_HISTORY;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), serverUrl);
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
            Gson gson = new Gson();
            productOfferList = gson.fromJson(response.toString(), ProductOfferList.class);
            productOfferArrayList.addAll(productOfferList.getProductArrayList());
            NotificationListAdapter mAdapter = new NotificationListAdapter(productOfferArrayList, this);
            LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
            recyclerNotifyList.setLayoutManager(layoutManager);
            recyclerNotifyList.setAdapter(mAdapter);
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
    public void onItemClickNotification(View view, int position) {
        ProductOffer productOffer = null;
        productOffer = productOfferArrayList.get(position);
        Intent intent;
        intent = new Intent(this, ProductDetailActivity.class);
        intent.putExtra("page", "notification");
        intent.putExtra("productObj", productOffer.getId());
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {

    }
}
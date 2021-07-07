package com.hst.osa_lilamore.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.hst.osa_lilamore.R;
import com.hst.osa_lilamore.adapter.OrderHistoryListAdapter;
import com.hst.osa_lilamore.bean.support.OrderHistory;
import com.hst.osa_lilamore.bean.support.OrderHistoryList;
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

public class OrderHistoryActivity extends AppCompatActivity implements IServiceListener, DialogClickListener, View.OnClickListener, OrderHistoryListAdapter.OnItemClickListener {
    private static final String TAG = OrderHistoryActivity.class.getName();

    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;

    private TextView delivered, transit, orderCount;

    private ArrayList<OrderHistory> orderHistoryArrayList = new ArrayList<>();
    OrderHistoryList orderHistoryList;
    private OrderHistoryListAdapter mAdapter;
    private RecyclerView recyclerViewCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history);

        Toolbar toolbar = (Toolbar)findViewById(R.id.activity_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_left_arrow));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        initiateServices();
        orderCount = (TextView) findViewById(R.id.order_count);
        delivered = (TextView) findViewById(R.id.delivered);
        delivered.setOnClickListener(this);
        transit = (TextView) findViewById(R.id.in_transit);
        transit.setOnClickListener(this);

        recyclerViewCategory = (RecyclerView) findViewById(R.id.listView_history);
        getOrderHistory("Delivered");
    }

    @Override
    public void onClick(View view) {
        if (view == delivered) {
            transit.setBackground(null);
            transit.setTextColor(ContextCompat.getColor(this, R.color.text_black));
            delivered.setBackground(ContextCompat.getDrawable(this, R.drawable.btn_size_color));
            delivered.setTextColor(ContextCompat.getColor(this, R.color.text_black));
            getOrderHistory("Delivered");
        } if (view == transit) {
            delivered.setBackground(null);
            delivered.setTextColor(ContextCompat.getColor(this, R.color.text_black));
            transit.setBackground(ContextCompat.getDrawable(this, R.drawable.btn_size_color));
            transit.setTextColor(ContextCompat.getColor(this, R.color.text_black));
            getOrderHistory("Transit");
        }
    }

    public void initiateServices() {
        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);
    }

    private void getOrderHistory(String orderType) {
        JSONObject jsonObject = new JSONObject();
        String id = PreferenceStorage.getUserId(this);
        try {
            jsonObject.put(OSAConstants.KEY_USER_ID, id);
            jsonObject.put(OSAConstants.KEY_STATUS, orderType);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = OSAConstants.BUILD_URL + OSAConstants.ORDER_HISTORY;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    @Override
    public void onAlertPositiveClicked(int tag) {

    }

    @Override
    public void onAlertNegativeClicked(int tag) {

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
    public void onResponse(final JSONObject response) {
        progressDialogHelper.hideProgressDialog();
        if (validateSignInResponse(response)) {
            try {
                String count = response.getString("order_count");
                orderCount.setText(count.concat(" ").concat(getString(R.string.orders)));
                Gson gson = new Gson();

                orderHistoryList = null;
                orderHistoryList = gson.fromJson(response.toString(), OrderHistoryList.class);
                orderHistoryArrayList.clear();
                orderHistoryArrayList.addAll(orderHistoryList.getOrderHistoryArrayList());
                mAdapter = new OrderHistoryListAdapter(this, orderHistoryArrayList, this);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
                recyclerViewCategory.setLayoutManager(mLayoutManager);
                recyclerViewCategory.setAdapter(mAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            Log.d(TAG, "Error while sign In");
        }
    }

    @Override
    public void onError(String error) {

    }

    @Override
    public void onItemClickHistory(View view, int position) {
        OrderHistory orderHistory = null;
        orderHistory = orderHistoryArrayList.get(position);
        Intent intent;
        intent = new Intent(this, com.hst.osa_lilamore.activity.OrderHistoryDetailPage.class);
        PreferenceStorage.saveOrderId(this, orderHistory.getorder_id());
        startActivity(intent);
    }
}
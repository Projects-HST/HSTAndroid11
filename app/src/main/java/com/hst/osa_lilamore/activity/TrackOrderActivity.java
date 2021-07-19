package com.hst.osa_lilamore.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.hst.osa_lilamore.R;
import com.hst.osa_lilamore.adapter.OrderHistoryDetailListAdapter;
import com.hst.osa_lilamore.adapter.OrderStatusListAdapter;
import com.hst.osa_lilamore.adapter.QuestionListAdapter;
import com.hst.osa_lilamore.bean.support.CartItem;
import com.hst.osa_lilamore.bean.support.CartOrderList;
import com.hst.osa_lilamore.bean.support.OrderStatus;
import com.hst.osa_lilamore.bean.support.OrderStatusList;
import com.hst.osa_lilamore.helpers.AlertDialogHelper;
import com.hst.osa_lilamore.helpers.ProgressDialogHelper;
import com.hst.osa_lilamore.interfaces.DialogClickListener;
import com.hst.osa_lilamore.servicehelpers.ServiceHelper;
import com.hst.osa_lilamore.serviceinterfaces.IServiceListener;
import com.hst.osa_lilamore.utils.OSAConstants;
import com.hst.osa_lilamore.utils.PreferenceStorage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.util.Log.d;

public class TrackOrderActivity extends AppCompatActivity implements IServiceListener, DialogClickListener, OrderHistoryDetailListAdapter.OnItemClickListener, QuestionListAdapter.OnItemClickListener, View.OnClickListener, OrderStatusListAdapter.OnItemClickListener {

    private static final String TAG = com.hst.osa_lilamore.activity.ReplaceProductActivity.class.getName();
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    private String resCheck = "";
    private String prodOrderID = "";
    private String questionID = "1";

    private TextView btnSubmit;

    private ArrayList<OrderStatus> orderStatusArrayList = new ArrayList<>();
    OrderStatusList orderStatusList;
    private OrderStatusListAdapter mAdapter;

    private ArrayList<CartItem> cartItemArrayList = new ArrayList<>();
    CartOrderList cartItemList;
    private RecyclerView recyclerViewCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_order);

        Toolbar toolbar = (Toolbar)findViewById(R.id.activity_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_left_arrow));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        cartItemArrayList = (ArrayList<CartItem>) getIntent().getSerializableExtra("prod");
//        recyclerViewStat = (RecyclerView) findViewById(R.id.listView_order_status);
        recyclerViewCategory = (RecyclerView) findViewById(R.id.listView_cart);

        OrderHistoryDetailListAdapter orderHistoryDetailListAdapter = new OrderHistoryDetailListAdapter(this, cartItemArrayList,this);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerViewCategory.setLayoutManager(mLayoutManager);
        recyclerViewCategory.setAdapter(orderHistoryDetailListAdapter);
        orderHistoryDetailListAdapter.resFor = true;
        orderHistoryDetailListAdapter.notifyDataSetChanged();

        btnSubmit = findViewById(R.id.track_order);
        btnSubmit.setOnClickListener(this);

        initiateServices();
        getTrackStatus();
    }

    public void initiateServices() {
        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);
    }

    private void getTrackStatus() {
        resCheck = "track_status";
        JSONObject jsonObject = new JSONObject();
        String id = PreferenceStorage.getUserId(this);
        try {
            jsonObject.put(OSAConstants.KEY_USER_ID, id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = OSAConstants.BUILD_URL + OSAConstants.TRACK_STATUS;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    private void trackOrder() {
        resCheck = "track";
        JSONObject jsonObject = new JSONObject();
        String id = PreferenceStorage.getUserId(this);
        String oid = PreferenceStorage.getOrderId(this);
        try {
            jsonObject.put(OSAConstants.KEY_USER_ID, id);
            jsonObject.put(OSAConstants.PARAMS_ORDER_ID, oid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = OSAConstants.BUILD_URL + OSAConstants.TRACK_ORDER;
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
    public void onResponse(JSONObject response) {
        progressDialogHelper.hideProgressDialog();
        if (validateSignInResponse(response)) {
            try {
                if (resCheck.equalsIgnoreCase("track")) {
                    JSONArray orderObjData = response.getJSONArray("track_details");

                    JSONObject data = orderObjData.getJSONObject(0);

                    for (int i = 0; i < orderStatusArrayList.size(); i ++) {
                        if (data.getString("status").equalsIgnoreCase(orderStatusArrayList.get(i).getorder_status())) {
                            int position = i;
                            for (int j = 0; j <= position; j++) {
                                orderStatusArrayList.get(j).setstatus("1");
                                recyclerViewCategory.getAdapter().notifyDataSetChanged();
                            }
                        }
                    }

                } if (resCheck.equalsIgnoreCase("track_status")) {
                    Gson gson = new Gson();
                    orderStatusList = gson.fromJson(response.toString(), OrderStatusList.class);
                    orderStatusArrayList.addAll(orderStatusList.getOrderStatusArrayList());
                    for (int i = 0; i < orderStatusArrayList.size(); i ++) {
                        orderStatusArrayList.get(i).setstatus("0");
                    }
                    mAdapter = new OrderStatusListAdapter(this, orderStatusArrayList,this);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
                    recyclerViewCategory.setLayoutManager(mLayoutManager);
                    recyclerViewCategory.setAdapter(mAdapter);
                    trackOrder();
                }

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
    public void onItemClickCart(View view, int position) {

    }

    @Override
    public void onItemClickSize(View view, int position) {

    }

    @Override
    public void onClick(View view) {
        if (view == btnSubmit) {
        }
    }

    @Override
    public void onItemClickHistory(View view, int position) {

    }
}
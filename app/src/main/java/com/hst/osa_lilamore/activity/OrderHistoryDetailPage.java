package com.hst.osa_lilamore.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.hst.osa_lilamore.R;
import com.hst.osa_lilamore.adapter.OrderHistoryDetailListAdapter;
import com.hst.osa_lilamore.adapter.ReviewOrderListAdapter;
import com.hst.osa_lilamore.bean.support.CartItem;
import com.hst.osa_lilamore.bean.support.CartOrderList;
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

public class OrderHistoryDetailPage extends AppCompatActivity implements IServiceListener, DialogClickListener, View.OnClickListener, ReviewOrderListAdapter.OnItemClickListener, OrderHistoryDetailListAdapter.OnItemClickListener {

    private static final String TAG = OrderHistoryDetailPage.class.getName();
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    private String resCheck = "";
    private RatingBar rtbComments;
    private EditText edtComments;
    private String reviewID = "", prdName = "";
    private boolean newReview = true;
    private TextView paymentMethod;
    private TextView replacement;
    private TextView name, phone, address;
    private TextView txtOrderId, txtOrderDate, txtOrderTotal, productName;
    private TextView itemPrice, txtDelivery, deliveryPrice, offerPrice, totalPrice, trackOrder, submitBtn;
    private RelativeLayout originalLayout, orderPlacedLayout;



    private ArrayList<CartItem> cartItemArrayList = new ArrayList<>();
    CartOrderList cartItemList;
    private OrderHistoryDetailListAdapter mAdapter;
    private RecyclerView recyclerViewCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_history_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_left_arrow));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        originalLayout = (RelativeLayout) findViewById(R.id.original_layout);
        orderPlacedLayout = (RelativeLayout) findViewById(R.id.order_played_layout);
        submitBtn = (TextView) findViewById(R.id.submit_review);
        submitBtn.setOnClickListener(this);


        productName = findViewById(R.id.product_name);
        rtbComments = findViewById(R.id.ratingBar);
        edtComments = findViewById(R.id.edtComments);

        recyclerViewCategory = (RecyclerView) findViewById(R.id.listView_cart);
        paymentMethod = (TextView) findViewById(R.id.payment_method);

        txtOrderId = (TextView) findViewById(R.id.order_id);
        txtOrderDate = (TextView) findViewById(R.id.order_date);
        txtOrderTotal = (TextView) findViewById(R.id.order_total);

        name = (TextView) findViewById(R.id.name);
        phone = (TextView) findViewById(R.id.mobile);
        address = (TextView) findViewById(R.id.address);
//        promoCode = (EditText) findViewById(R.id.promo_code);
        trackOrder = (TextView) findViewById(R.id.track_order);
        trackOrder.setOnClickListener(this);

        itemPrice = (TextView) findViewById(R.id.item_price);

        txtDelivery = (TextView) findViewById(R.id.txt_delivery);
        txtDelivery.setText(getString(R.string.wallet));

        deliveryPrice = (TextView) findViewById(R.id.delivery_price);
        offerPrice = (TextView) findViewById(R.id.offer_price);
        totalPrice = (TextView) findViewById(R.id.total_price);
        replacement = (TextView) findViewById(R.id.replacement);
        replacement.setOnClickListener(this);

        initiateServices();
        getOrderDetails();
    }

    public void initiateServices() {
        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);
    }

    private void getOrderDetails() {
        resCheck = "detail";
        JSONObject jsonObject = new JSONObject();
        String id = PreferenceStorage.getUserId(this);
        String oid = PreferenceStorage.getOrderId(this);
        try {
            jsonObject.put(OSAConstants.KEY_USER_ID, id);
            jsonObject.put(OSAConstants.PARAMS_ORDER_ID, oid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = OSAConstants.BUILD_URL + OSAConstants.ORDER_DETAILS;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    private void checkReview() {
        resCheck = "check";
        JSONObject jsonObject = new JSONObject();
        String id = PreferenceStorage.getUserId(this);
        String pid = PreferenceStorage.getProductId(this);
        try {
            jsonObject.put(OSAConstants.KEY_USER_ID, id);
            jsonObject.put(OSAConstants.PARAMS_PROD_ID, pid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = OSAConstants.BUILD_URL + OSAConstants.CHECK_REVIEWS;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    private void submitReview() {
        resCheck = "sumbit";
        JSONObject jsonObject = new JSONObject();
        String id = PreferenceStorage.getUserId(this);
        String oid = PreferenceStorage.getOrderId(this);
        try {
            jsonObject.put(OSAConstants.KEY_USER_ID, id);
            jsonObject.put(OSAConstants.PARAMS_PROD_ID, oid);
            jsonObject.put(OSAConstants.KEY_COMMENT, edtComments.getText().toString());
            jsonObject.put(OSAConstants.KEY_RATING,  "" + Integer.getInteger(String.valueOf(rtbComments.getRating())));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = OSAConstants.BUILD_URL + OSAConstants.SUBMIT_REVIEW;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }
    private void updateReview() {
        resCheck = "update";
        JSONObject jsonObject = new JSONObject();
        String id = PreferenceStorage.getUserId(this);
        String oid = PreferenceStorage.getProductId(this);
        try {
            jsonObject.put(OSAConstants.KEY_USER_ID, id);
            jsonObject.put(OSAConstants.PARAMS_PROD_ID, oid);
            jsonObject.put(OSAConstants.KEY_REVIEW_ID, reviewID);
            jsonObject.put(OSAConstants.KEY_COMMENT, edtComments.getText().toString());
            jsonObject.put(OSAConstants.KEY_RATING,  "" + rtbComments.getRating());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = OSAConstants.BUILD_URL + OSAConstants.UPDATE_REVIEW;
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
    public void onBackPressed() {
        //Checking for fragment count on backstack
        if (orderPlacedLayout.getVisibility() == View.VISIBLE) {
            layoutGone();
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                getSupportFragmentManager().popBackStack();
            } else {
                super.onBackPressed();
            }
        }
    }

    @Override
    public void onResponse(final JSONObject response) {
        progressDialogHelper.hideProgressDialog();
        if (validateSignInResponse(response)) {
            try {
                if (resCheck.equalsIgnoreCase("detail")) {
                    JSONArray orderObjData = response.getJSONArray("order_details");

                    JSONObject data = orderObjData.getJSONObject(0);

                    String orderidd = data.getString("order_id");
                    String orderdate = data.getString("purchase_date");

                    String nameString = data.getString("full_name");
                    String mobileString = data.getString("mobile_number");
                    String houseString = data.getString("house_no");
                    String streetString = data.getString("street");
                    String cityString = data.getString("city");
                    String pincodeString = data.getString("pincode");

                    String itemString = data.getString("total_amount");
                    String promoString = data.getString("promo_amount");
                    String walletString = data.getString("wallet_amount");
                    String paidString = data.getString("paid_amount");
                    String payment = data.getString("payment_status");
                    paymentMethod.setText(payment);

                    String addressFinal = houseString.concat(streetString).concat("\n").concat(cityString).concat(" - ").concat(pincodeString);
                    String temMobile = "";
                    String mobileFinal = temMobile.concat("+91").concat(mobileString);
                    String itemFinal = ("₹").concat(itemString);
                    String promoFinal = ("-₹").concat(promoString);
                    String walletFinal = ("₹").concat(walletString);
                    String paidFinal = ("₹").concat(paidString);

                    txtOrderId.setText(orderidd);
                    txtOrderDate.setText(orderdate);
                    txtOrderTotal.setText(paidFinal);

                    name.setText(nameString);
                    phone.setText(mobileFinal);
                    address.setText(addressFinal);

                    itemPrice.setText(itemFinal);
                    deliveryPrice.setText(walletFinal);
                    offerPrice.setText(promoFinal);
                    totalPrice.setText(paidFinal);

                    Gson gson = new Gson();

                    cartItemList = gson.fromJson(response.toString(), CartOrderList.class);
                    cartItemArrayList.addAll(cartItemList.getCartItemArrayList());
                    mAdapter = new OrderHistoryDetailListAdapter(this, cartItemArrayList,this);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
                    recyclerViewCategory.setLayoutManager(mLayoutManager);
                    recyclerViewCategory.setAdapter(mAdapter);

                }
                if (resCheck.equalsIgnoreCase("check")) {
                    if (response.getString("msg").equalsIgnoreCase("Reviews found")) {
                        newReview = false;
                        JSONObject data = response.getJSONObject("product_review");
                        reviewID = data.getString("id");
                        String rating = data.getString("rating");
                        String comment = data.getString("comment");
                        rtbComments.setRating(Integer.getInteger(rating));
                        edtComments.setText(comment);
                    } else {
                        newReview = true;
                    }
                }
                if (resCheck.equalsIgnoreCase("sumbit")) {
                    layoutGone();
                }
                if (resCheck.equalsIgnoreCase("update")) {
                    layoutGone();
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
    public void onClick(View view) {
        if (view == findViewById(R.id.click_detect)) {
            layoutGone();
        }
        if (view == submitBtn) {
            if (newReview) {
                submitReview();
            } else {
                updateReview();
            }
        }
//        if (view == replacement) {
//            Intent intent = new Intent(this, ReplaceProductActivity.class);
//            startActivity(intent);
//        }
        if (view == trackOrder) {
            Intent intent = new Intent(this, TrackOrderActivity.class);
            intent.putExtra("prod", cartItemArrayList);
            startActivity(intent);
        }
    }

    public void replaceProduct(int position) {
        Intent intent = new Intent(this, com.hst.osa_lilamore.activity.ReplaceProductActivity.class);
        intent.putExtra("prod", cartItemArrayList.get(position));
        startActivity(intent);
    }

    public void layoutVisible(String prodName) {
        checkReview();
        productName.setText(prodName);
        orderPlacedLayout.setVisibility(View.VISIBLE);
        originalLayout.setClickable(false);
        originalLayout.setFocusable(false);
        trackOrder.setClickable(false);
        trackOrder.setFocusable(false);
    }

    public void layoutGone() {
        orderPlacedLayout.setVisibility(View.GONE);
        originalLayout.setClickable(true);
        originalLayout.setFocusable(true);
        trackOrder.setClickable(true);
        trackOrder.setFocusable(true);
    }

    @Override
    public void onItemClickCart(View view, int position) {

    }
}

package com.hst.osa_lilamore.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.hst.osa_lilamore.R;
import com.hst.osa_lilamore.adapter.ReviewOrderListAdapter;
import com.hst.osa_lilamore.bean.support.AddressArrayList;
import com.hst.osa_lilamore.bean.support.AddressList;
import com.hst.osa_lilamore.bean.support.CartItem;
import com.hst.osa_lilamore.bean.support.CartOrderList;
import com.hst.osa_lilamore.bean.support.OrderHistory;
import com.hst.osa_lilamore.ccavenue.activity.InitialScreenActivity;
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

public class ReviewOrderActivity extends AppCompatActivity implements IServiceListener, DialogClickListener, View.OnClickListener, ReviewOrderListAdapter.OnItemClickListener {

    private static final String TAG = com.hst.osa_lilamore.activity.CheckoutActivity.class.getName();
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    private String resCheck = "";
    private String addressID = "";
    private String orderID = "";
    private TextView paymentMethod;
    private TextView name, phone, address;
    private TextView itemPrice, txtDelivery, deliveryPrice, offerPrice, totalPrice, placeOrder, continueShopping, goToOrders;
    private RelativeLayout originalLayout, orderPlacedLayout;
    AddressArrayList addressList;
    ArrayList<AddressList> addressArrayList = new ArrayList<>();


    private ArrayList<CartItem> cartItemArrayList = new ArrayList<>();
    CartOrderList cartItemList;
    private ReviewOrderListAdapter mAdapter;
    private RecyclerView recyclerViewCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_order);

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
        continueShopping = (TextView) findViewById(R.id.continue_shopping);
        continueShopping.setOnClickListener(this);
        goToOrders = (TextView) findViewById(R.id.go_to_orders);
        goToOrders.setOnClickListener(this);

        recyclerViewCategory = (RecyclerView) findViewById(R.id.listView_cart);
        paymentMethod = (TextView) findViewById(R.id.payment_method);
        name = (TextView) findViewById(R.id.name);
        phone = (TextView) findViewById(R.id.mobile);
        address = (TextView) findViewById(R.id.address);
//        promoCode = (EditText) findViewById(R.id.promo_code);
        placeOrder = (TextView) findViewById(R.id.place_order);
        placeOrder.setOnClickListener(this);

        itemPrice = (TextView) findViewById(R.id.item_price);

        txtDelivery = (TextView) findViewById(R.id.txt_delivery);
        txtDelivery.setText(getString(R.string.wallet));

        deliveryPrice = (TextView) findViewById(R.id.delivery_price);
        offerPrice = (TextView) findViewById(R.id.offer_price);
        totalPrice = (TextView) findViewById(R.id.total_price);

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

    private void cashPayment() {
        resCheck = "COD";
        JSONObject jsonObject = new JSONObject();
        String id = PreferenceStorage.getUserId(this);
        String oid = PreferenceStorage.getOrderId(this);
        try {
            jsonObject.put(OSAConstants.KEY_USER_ID, id);
            jsonObject.put(OSAConstants.PARAMS_ORDER_ID, oid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = OSAConstants.BUILD_URL + OSAConstants.PAY_COD;
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
                if (resCheck.equalsIgnoreCase("detail")) {
                    JSONArray orderObjData = response.getJSONArray("order_details");

                    JSONObject data = orderObjData.getJSONObject(0);
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

                    String addressFinal = houseString.concat(streetString).concat("\n").concat(cityString).concat(" - ").concat(pincodeString);
                    String temMobile = "";
                    String mobileFinal = temMobile.concat("+91").concat(mobileString);
                    String itemFinal = ("₹").concat(itemString);
                    String promoFinal = ("-₹").concat(promoString);
                    String walletFinal = ("₹").concat(walletString);
                    String paidFinal = ("₹").concat(paidString);

                    String payment = getIntent().getExtras().getString("payment");
                    if (payment.equalsIgnoreCase("")) {
                        paymentMethod.setText("Online Payment");
                    } else {
                        paymentMethod.setText(payment);
                    }
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
                    mAdapter = new ReviewOrderListAdapter(this, cartItemArrayList, this);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
                    recyclerViewCategory.setLayoutManager(mLayoutManager);
                    recyclerViewCategory.setAdapter(mAdapter);

                }
                if (resCheck.equalsIgnoreCase("COD")) {
                    layoutVisible();
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
        if (view == placeOrder) {
            if (paymentMethod.getText().toString().equalsIgnoreCase("Online Payment")) {
                String orderID = PreferenceStorage.getOrderId(this);
                Intent i = new Intent(this, InitialScreenActivity.class);
                i.putExtra("advpay", totalPrice.getText().toString());
                i.putExtra("page", "payment");
                startActivity(i);
                finish();
            } else if (paymentMethod.getText().toString().equalsIgnoreCase("Wallet")) {
                layoutVisible();
            } else if (paymentMethod.getText().toString().equalsIgnoreCase("COD")) {
                cashPayment();
            }
        } if (view == continueShopping) {
            Intent homeIntent = new Intent(this, com.hst.osa_lilamore.activity.MainActivity.class);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(homeIntent);
            finish();
        } if (view == goToOrders) {
            Intent homeIntent = new Intent(this, OrderHistory.class);
            homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(homeIntent);
            finish();
        }
    }

    public void reLoadPage() {
        finish();
        startActivity(getIntent());
    }

    private void layoutVisible() {
        orderPlacedLayout.setVisibility(View.VISIBLE);
        originalLayout.setClickable(false);
        originalLayout.setFocusable(false);
        placeOrder.setClickable(false);
        placeOrder.setFocusable(false);
    }

    @Override
    public void onItemClickCart(View view, int position) {

    }
}

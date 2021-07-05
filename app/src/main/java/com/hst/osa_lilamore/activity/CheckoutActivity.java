package com.hst.osa_lilamore.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.hst.osa_lilamore.R;
import com.hst.osa_lilamore.bean.support.AddressArrayList;
import com.hst.osa_lilamore.bean.support.AddressList;
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

public class CheckoutActivity extends AppCompatActivity implements IServiceListener, DialogClickListener, View.OnClickListener {

    private static final String TAG = CheckoutActivity.class.getName();
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    private String resCheck = "";
    private String addressID = "";
    private String orderID = "";
    private String purchaseOrderID = "";
    private String paymentStatus = "";
    private TextView name, phone, address;
    private EditText promoCode;
    private TextView checkPromo;
    private TextView itemPrice, txtDelivery, deliveryPrice, offerPrice, totalPrice, reviewOrder;
    private ImageView walletImg, codImg;
    private boolean walletClick = false, codClick = false;
    private int pos;
    AddressArrayList addressList;
    ArrayList<AddressList> addressArrayList = new ArrayList<>();
    AddressList addList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_left_arrow));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        name = (TextView) findViewById(R.id.name);
        phone = (TextView) findViewById(R.id.mobile);
        address = (TextView) findViewById(R.id.address);
        promoCode = (EditText) findViewById(R.id.promo_code);
        checkPromo = (TextView) findViewById(R.id.apply_promo);
        checkPromo.setOnClickListener(this);

        itemPrice = (TextView) findViewById(R.id.item_price);

        txtDelivery = (TextView) findViewById(R.id.txt_delivery);
        txtDelivery.setText(getString(R.string.wallet));

        deliveryPrice = (TextView) findViewById(R.id.delivery_price);
        offerPrice = (TextView) findViewById(R.id.offer_price);
        totalPrice = (TextView) findViewById(R.id.total_price);

        reviewOrder = (TextView) findViewById(R.id.review_order);
        reviewOrder.setOnClickListener(this);

        walletImg = (ImageView) findViewById(R.id.wallet_radio);
        walletImg.setOnClickListener(this);

        codImg = (ImageView) findViewById(R.id.cash_radio);
        codImg.setOnClickListener(this);

        initiateServices();
        getAddressList();
    }

    public void initiateServices() {
        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);
    }

    private void getAddressList() {
        resCheck = "address";
        JSONObject jsonObject = new JSONObject();
        String id = PreferenceStorage.getUserId(this);
        try {
            jsonObject.put(OSAConstants.KEY_USER_ID, id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = OSAConstants.BUILD_URL + OSAConstants.ADDRESS_LIST;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    private void placeOrder() {
        resCheck = "place";
        JSONObject jsonObject = new JSONObject();
        String id = PreferenceStorage.getUserId(this);
        try {
            jsonObject.put(OSAConstants.KEY_USER_ID, id);
            jsonObject.put(OSAConstants.PARAMS_ADDRESS_ID, addressID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = OSAConstants.BUILD_URL + OSAConstants.PLACE_ORDER;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    private void getOrderDetails() {
        resCheck = "detail";
        JSONObject jsonObject = new JSONObject();
        String id = PreferenceStorage.getUserId(this);
        try {
            jsonObject.put(OSAConstants.KEY_USER_ID, id);
            jsonObject.put(OSAConstants.PARAMS_ORDER_ID, orderID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = OSAConstants.BUILD_URL + OSAConstants.ORDER_DETAILS;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    private void checkPromoCode() {
        resCheck = "promo";
        JSONObject jsonObject = new JSONObject();
        String id = PreferenceStorage.getUserId(this);
        try {
            jsonObject.put(OSAConstants.KEY_USER_ID, id);
            jsonObject.put(OSAConstants.PARAMS_PURCHASE_ORDER_ID, purchaseOrderID);
            jsonObject.put(OSAConstants.KEY_PROMO, promoCode.getText().toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = OSAConstants.BUILD_URL + OSAConstants.APPLY_PROMO;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    private void useWallet() {
        resCheck = "wallet";
        JSONObject jsonObject = new JSONObject();
        String id = PreferenceStorage.getUserId(this);
        try {
            jsonObject.put(OSAConstants.KEY_USER_ID, id);
            jsonObject.put(OSAConstants.PARAMS_PURCHASE_ORDER_ID, purchaseOrderID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = OSAConstants.BUILD_URL + OSAConstants.USE_WALLET;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    private void removeWallet() {
        resCheck = "wallet";
        JSONObject jsonObject = new JSONObject();
        String id = PreferenceStorage.getUserId(this);
        try {
            jsonObject.put(OSAConstants.KEY_USER_ID, id);
            jsonObject.put(OSAConstants.PARAMS_PURCHASE_ORDER_ID, purchaseOrderID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = OSAConstants.BUILD_URL + OSAConstants.REMOVE_WALLET;
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

    public void reLoadPage() {
        finish();
        startActivity(getIntent());
    }

    @Override
    public void onResponse(final JSONObject response) {
        progressDialogHelper.hideProgressDialog();
        if (validateSignInResponse(response)) {
            try {
                if (resCheck.equalsIgnoreCase("address")) {
                    Gson gson = new Gson();

                    JSONArray categoryObjData = response.getJSONArray("address_list");
                    addressList = gson.fromJson(response.toString(), AddressArrayList.class);
                    addressArrayList.addAll(addressList.getAddressArrayList());
                    addressID = addressArrayList.get(0).getId();
//                for (int i = 0; i < addressArrayList.size(); i++) {
//                    if (addressArrayList.get(i).get)
//                }
                    placeOrder();
                }
                if (resCheck.equalsIgnoreCase("place")) {
                    orderID = response.getString("order_id");
                    PreferenceStorage.saveOrderId(this, orderID);
                    getOrderDetails();
                }
                if (resCheck.equalsIgnoreCase("detail")) {
                    JSONArray orderObjData = response.getJSONArray("cart_items");

                    JSONObject data = orderObjData.getJSONObject(0);
                    purchaseOrderID = data.getString("purchase_order_id");
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
                    paymentStatus = data.getString("payment_status");

                    String addressFinal = houseString.concat(streetString).concat("\n").concat(cityString).concat(" - ").concat(pincodeString);
                    String temMobile = "";
                    String mobileFinal = temMobile.concat("+91").concat(mobileString);
                    String itemFinal = ("₹").concat(itemString);
                    String promoFinal = ("-₹").concat(promoString);
                    String walletFinal = ("₹").concat(walletString);
                    String paidFinal = ("₹").concat(paidString);

                    name.setText(nameString);
                    phone.setText(mobileFinal);
                    address.setText(addressFinal);

                    itemPrice.setText(itemFinal);
                    deliveryPrice.setText(walletFinal);
                    offerPrice.setText(promoFinal);
                    totalPrice.setText(paidFinal);

                }
                if (resCheck.equalsIgnoreCase("promo")) {
                    JSONArray orderObjData = response.getJSONArray("order_details");

                    JSONObject data = orderObjData.getJSONObject(0);
                    purchaseOrderID = data.getString("purchase_order_id");
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
                    paymentStatus = data.getString("payment_status");

                    String addressFinal = houseString.concat(streetString).concat("\n").concat(cityString).concat(" - ").concat(pincodeString);
                    String temMobile = "";
                    String mobileFinal = temMobile.concat("+91").concat(mobileString);
                    String itemFinal = ("₹").concat(itemString);
                    String promoFinal = ("-₹").concat(promoString);
                    String walletFinal = ("₹").concat(walletString);
                    String paidFinal = ("₹").concat(paidString);

                    name.setText(nameString);
                    phone.setText(mobileFinal);
                    address.setText(addressFinal);

                    itemPrice.setText(itemFinal);
                    deliveryPrice.setText(walletFinal);
                    offerPrice.setText(promoFinal);
                    totalPrice.setText(paidFinal);
                }
                if (resCheck.equalsIgnoreCase("wallet")) {
                    JSONArray orderObjData = response.getJSONArray("order_details");

                    JSONObject data = orderObjData.getJSONObject(0);
                    purchaseOrderID = data.getString("purchase_order_id");
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
                    paymentStatus = data.getString("payment_status");

                    String addressFinal = houseString.concat(streetString).concat("\n").concat(cityString).concat(" - ").concat(pincodeString);
                    String temMobile = "";
                    String mobileFinal = temMobile.concat("+91").concat(mobileString);
                    String itemFinal = ("₹").concat(itemString);
                    String promoFinal = ("-₹").concat(promoString);
                    String walletFinal = ("₹").concat(walletString);
                    String paidFinal = ("₹").concat(paidString);

                    name.setText(nameString);
                    phone.setText(mobileFinal);
                    address.setText(addressFinal);

                    itemPrice.setText(itemFinal);
                    deliveryPrice.setText(walletFinal);
                    offerPrice.setText(promoFinal);
                    totalPrice.setText(paidFinal);
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
        if (view == checkPromo) {
            if (!promoCode.getText().toString().isEmpty()) {
                checkPromoCode();
            }
        }
        if (view == walletImg) {
            assert walletImg != null;
            if (walletClick) {
                walletImg.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_check_mark_unchecked));
                walletClick = false;
                removeWallet();
            } else {
                walletImg.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_check_mark_checked));
                walletClick = true;
                useWallet();
            }
        }
        if (view == codImg) {
            assert codImg != null;
            if (walletClick) {
                codImg.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_check_mark_unchecked));
                codClick = false;
            } else {
                codImg.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_check_mark_checked));
                codClick = true;
            }
        }
        if (view == reviewOrder) {
            Intent i = new Intent(this, ReviewOrderActivity.class);
            if (codClick) {
                i.putExtra("payment", "COD");
            } else {

                i.putExtra("payment", paymentStatus);
            }
            startActivity(i);
        }
    }
}

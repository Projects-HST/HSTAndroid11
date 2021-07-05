package com.hst.osa_lilamore.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.hst.osa_lilamore.R;
import com.hst.osa_lilamore.adapter.CartItemListAdapter;
import com.hst.osa_lilamore.bean.support.CartItem;
import com.hst.osa_lilamore.bean.support.CartItemList;
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

public class CartActivity extends AppCompatActivity implements IServiceListener, DialogClickListener, View.OnClickListener, CartItemListAdapter.OnItemClickListener {

    private static final String TAG = CartActivity.class.getName();
    Context context;
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;

    private ArrayList<CartItem> cartItemArrayList = new ArrayList<>();
    CartItemList cartItemList;
    private CartItemListAdapter mAdapter;

    private RecyclerView recyclerViewCategory;
    private View rootView;
    private TextView totalPrice, btnCheckout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);
        Toolbar toolbar = (Toolbar)findViewById(R.id.activity_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_left_arrow));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recyclerViewCategory = (RecyclerView) findViewById(R.id.listView_cart);
        totalPrice = (TextView) findViewById(R.id.total_price);
        btnCheckout = (TextView) findViewById(R.id.checkout);
        btnCheckout.setOnClickListener(this);
        initiateServices();
        getDashboardServices();

    }
    public void initiateServices() {
        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);
    }

    private void getDashboardServices() {
        JSONObject jsonObject = new JSONObject();
        String id = PreferenceStorage.getUserId(this);
        try {
            jsonObject.put(OSAConstants.KEY_USER_ID, id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = OSAConstants.BUILD_URL + OSAConstants.CART_LIST;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    @Override
    public void onClick(View v) {
        if (v == btnCheckout) {
            if (cartItemArrayList.size() > 0) {
                Intent i = new Intent(this, CheckoutActivity.class);
                startActivity(i);
                finish();
            }
        }
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
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
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
                        btnCheckout.setClickable(false);
                    } else {
                        signInSuccess = true;
                        btnCheckout.setClickable(true);
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
                Gson gson = new Gson();

                JSONObject categoryObjData = response.getJSONObject("cart_payment");
                cartItemList = gson.fromJson(response.toString(), CartItemList.class);
                cartItemArrayList.addAll(cartItemList.getCartItemArrayList());
                mAdapter = new CartItemListAdapter(this, cartItemArrayList, this);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
                recyclerViewCategory.setLayoutManager(mLayoutManager);
                recyclerViewCategory.setAdapter(mAdapter);
                totalPrice.setText("₹" + categoryObjData.getString("cart_payment"));
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
}
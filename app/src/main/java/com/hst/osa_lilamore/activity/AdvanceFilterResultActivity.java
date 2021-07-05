package com.hst.osa_lilamore.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.hst.osa_lilamore.R;
import com.hst.osa_lilamore.adapter.BestSellingListAdapter;
import com.hst.osa_lilamore.bean.support.Product;
import com.hst.osa_lilamore.bean.support.SubProductList;
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

public class AdvanceFilterResultActivity extends AppCompatActivity implements IServiceListener, DialogClickListener, View.OnClickListener, BestSellingListAdapter.OnItemClickListener {

    private static final String TAG = com.hst.osa_lilamore.activity.ReplaceProductActivity.class.getName();
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    private String resCheck = "";
    private String prodOrderID = "";
    private String questionID = "1";

    private TextView btnSubmit;

    private String productID, sizeID = "0", colourID = "0", minRange = "0", maxRange = "0", catId = "", subCatId = "0";

    private ArrayList<Product> productArrayList = new ArrayList<>();
    SubProductList productList;
    private RecyclerView recyclerViewSubCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_filter_result);

        Toolbar toolbar = (Toolbar)findViewById(R.id.activity_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_left_arrow));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recyclerViewSubCategory = (RecyclerView) findViewById(R.id.sub_product_list);

        catId = getIntent().getStringExtra("catID");
        subCatId = getIntent().getStringExtra("subcatID");
        minRange = getIntent().getStringExtra("minRange");
        maxRange = getIntent().getStringExtra("maxRange");
        sizeID = getIntent().getStringExtra("sizeID");
        colourID = getIntent().getStringExtra("colourID");

        initiateServices();
        getResults();
    }

    public void initiateServices() {
        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);
    }

    private void getResults() {

        resCheck = "result";

        JSONObject jsonObject = new JSONObject();
        String id = PreferenceStorage.getUserId(this);
        try {
            jsonObject.put(OSAConstants.KEY_USER_ID, id);
            jsonObject.put(OSAConstants.KEY_CAT_ID, catId);
            jsonObject.put(OSAConstants.KEY_SUB_CAT_ID, subCatId);
            jsonObject.put(OSAConstants.KEY_PRODUCT_SIZE_ID, sizeID);
            jsonObject.put(OSAConstants.KEY_PRODUCT_COLOUR_ID, colourID);
            jsonObject.put(OSAConstants.KEY_MIN_PRICE, minRange);
            jsonObject.put(OSAConstants.KEY_MAX_PRICE, maxRange);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String serverUrl = OSAConstants.BUILD_URL + OSAConstants.FILTER_RESULT;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), serverUrl);
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
            Gson gson = new Gson();
            productList = gson.fromJson(response.toString(), SubProductList.class);
            productArrayList.addAll(productList.getProductArrayList());
            BestSellingListAdapter adasd = new BestSellingListAdapter(this, productArrayList, this);
            GridLayoutManager mLayoutManager = new GridLayoutManager(this, 4);
            mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (adasd.getItemViewType(position) > 0) {
                        return adasd.getItemViewType(position);
                    } else {
                        return 1;
                    }
                    //return 2;
                }
            });
            recyclerViewSubCategory.setLayoutManager(mLayoutManager);
            recyclerViewSubCategory.setAdapter(adasd);

        } else {
            Log.d(TAG, "Error while sign In");
        }
    }

    @Override
    public void onError(String error) {

    }

    @Override
    public void onClick(View view) {
        if (view == btnSubmit) {
        }
    }

    @Override
    public void onItemClickBestSelling(View view, int position) {
        Product product = null;
        product = productArrayList.get(position);
        Intent intent;
        intent = new Intent(this, com.hst.osa_lilamore.activity.ProductDetailActivity.class);
        intent.putExtra("page", "AdvFilter");
        intent.putExtra("productObj", product.getid());
        startActivity(intent);
    }
}
package com.hst.osa_lilamore.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.slider.RangeSlider;
import com.google.gson.Gson;
import com.hst.osa_lilamore.R;
import com.hst.osa_lilamore.adapter.FilterColourListAdapter;
import com.hst.osa_lilamore.adapter.FilterSizeListAdapter;
import com.hst.osa_lilamore.adapter.SubCategoryListAdapter;
import com.hst.osa_lilamore.bean.support.FilterColor;
import com.hst.osa_lilamore.bean.support.FilterColorList;
import com.hst.osa_lilamore.bean.support.FilterSize;
import com.hst.osa_lilamore.bean.support.FilterSizeList;
import com.hst.osa_lilamore.bean.support.SubCategory;
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
import java.util.List;

import static android.util.Log.d;

public class AdvancedFilterActivity extends AppCompatActivity implements IServiceListener, DialogClickListener, FilterSizeListAdapter.OnItemClickListener, FilterColourListAdapter.OnItemClickListener, View.OnClickListener, SubCategoryListAdapter.OnItemClickListener {

    private static final String TAG = com.hst.osa_lilamore.activity.ReplaceProductActivity.class.getName();
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    private String resCheck = "";
    private String prodOrderID = "";
    private String questionID = "1";

    private TextView btnSubmit;

    private RecyclerView recyclerViewSubCategory, recyclerViewSize, recyclerViewColour;
    private String catId = "";
    private String subCatId = "0";
    private String serviceCall = "";

    private ArrayList<SubCategory> subCategoryArrayList = new ArrayList<>();
    private SubCategoryListAdapter mAdapter;

    private String productID, sizeID = "0", colourID = "0", minRange = "0", maxRange = "0";
    private ArrayList<FilterSize> sizeArrayList = new ArrayList<>();
    private FilterSizeListAdapter sizeListAdapter;
    FilterSize size;
    FilterSizeList sizeList;

    private ArrayList<FilterColor> colourArrayList = new ArrayList<>();
    FilterColor colour;
    FilterColorList colourList;

    private RangeSlider rangeSlider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_advanced_filter);

        Toolbar toolbar = (Toolbar)findViewById(R.id.activity_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_left_arrow));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recyclerViewSubCategory = (RecyclerView) findViewById(R.id.sub_cat);
        recyclerViewSize = (RecyclerView) findViewById(R.id.listView_size);
        recyclerViewColour = (RecyclerView) findViewById(R.id.listView_colors);

        btnSubmit = (TextView) findViewById(R.id.result);
        btnSubmit.setOnClickListener(this);

        rangeSlider = (RangeSlider) findViewById(R.id.range);
        rangeSlider.addOnSliderTouchListener(new RangeSlider.OnSliderTouchListener() {
            public void onStartTrackingTouch(RangeSlider slider) {
                // Responds to when slider's touch event is being started
                List<Float> vals = slider.getValues();
            }

            public void onStopTrackingTouch(RangeSlider slider) {
                // Responds to when slider's touch event is being stopped
                List<Float> vals = slider.getValues();
                minRange = String.valueOf(((Number)vals.get(0)).floatValue());
                maxRange = String.valueOf(((Number)vals.get(1)).floatValue());
            }
        });

        catId = getIntent().getStringExtra("categoryObj");


        initiateServices();
        showSubCategory();
    }

    public void initiateServices() {
        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);
    }

    private void showSubCategory() {
        serviceCall = "sub_category";

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(OSAConstants.KEY_CAT_ID, catId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String serverUrl = OSAConstants.BUILD_URL + OSAConstants.SUB_CATEGORY_LIST;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), serverUrl);
    }

    private void getFilter() {
        serviceCall = "filter";
        String id = PreferenceStorage.getUserId(this);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(OSAConstants.KEY_USER_ID, id);
            jsonObject.put(OSAConstants.KEY_SUB_CAT_ID, subCatId);
            jsonObject.put(OSAConstants.KEY_CAT_ID, catId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String serverUrl = OSAConstants.BUILD_URL + OSAConstants.GET_FILTER;
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
            try {
                if (serviceCall.equalsIgnoreCase("sub_category")) {

                    JSONArray subCategoryArray = response.getJSONArray("sub_category_list");
                    JSONObject subCatObj = subCategoryArray.getJSONObject(0);

                    Log.d(TAG, subCatObj.toString());

                    String id = "";
                    String txtSubCat = "";
                    subCategoryArrayList = new ArrayList<>();
                    subCategoryArrayList.add(new SubCategory("0", "ALL"));

                    for (int i = 0; i < subCategoryArray.length(); i++) {
                        id = subCategoryArray.getJSONObject(i).getString("id");
                        txtSubCat = subCategoryArray.getJSONObject(i).getString("category_name");
                        subCategoryArrayList.add(new SubCategory(id, txtSubCat));
                    }
                    mAdapter = new SubCategoryListAdapter(this, subCategoryArrayList, this);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
                    recyclerViewSubCategory.setLayoutManager(layoutManager);
                    recyclerViewSubCategory.setAdapter(mAdapter);
                }if (serviceCall.equalsIgnoreCase("filter")) {

                    JSONArray maxPriceArray = response.getJSONArray("max_price_amount");
                    String maxPrice = maxPriceArray.getJSONObject(0).getString("max_amount");
                    float maxP = Float.parseFloat(maxPrice);
                    rangeSlider.setValueFrom((float) 0);
                    rangeSlider.setValueTo(maxP);
//                    rangeSlider.setValues((float)(R.array.initial_slider_values));
                    Gson gson = new Gson();

                    Object obj = response.get("size_list");
                    if (obj instanceof JSONArray)
                    {
                        // it's an array
                        sizeList = null;
                        sizeList = gson.fromJson(response.toString(), FilterSizeList.class);
                        sizeArrayList.clear();
                        sizeArrayList.addAll(sizeList.getSizeArrayList());
                        sizeListAdapter = new FilterSizeListAdapter(sizeArrayList, this);
                        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
                        recyclerViewSize.setLayoutManager(layoutManager);
                        recyclerViewSize.setAdapter(sizeListAdapter);
                        sizeID = sizeArrayList.get(0).getid();

                        colourList = null;
                        colourList = gson.fromJson(response.toString(), FilterColorList.class);
                        colourArrayList.clear();
                        colourArrayList.addAll(colourList.getColourArrayList());
                        FilterColourListAdapter adapter = new FilterColourListAdapter(colourArrayList, this);
                        LinearLayoutManager mlayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
                        recyclerViewColour.setLayoutManager(mlayoutManager);
                        recyclerViewColour.setAdapter(adapter);
//                        colourID = colourArrayList.get(0).getid();
                        // do all kinds of JSONArray'ish things with urlArray
                    }
                    else
                    {
                        // if you know it's either an array or an object, then it's an object
                        // do objecty stuff with urlObject

                    }
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
    public void onItemClickSize(View view, int position) {
        FilterSize filterSize = null;
        filterSize = sizeArrayList.get(position);
        sizeID = filterSize.getmas_size_id();
        FilterSizeListAdapter.selected_item = position;
        recyclerViewSize.getAdapter().notifyDataSetChanged();
    }

    @Override
    public void onClick(View view) {
        if (view == btnSubmit) {
            Intent intent = new Intent(this, AdvanceFilterResultActivity.class);
            intent.putExtra("catID", catId);
            intent.putExtra("subcatID", subCatId);
            intent.putExtra("minRange", minRange);
            intent.putExtra("maxRange", maxRange);
            intent.putExtra("sizeID", sizeID);
            intent.putExtra("colourID", colourID);
            startActivity(intent);
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        SubCategory subCategory = null;
        subCategory = subCategoryArrayList.get(position);
        subCatId = subCategory.getId();
        getFilter();
    }

    @Override
    public void onItemClickColour(View view, int position) {
        FilterColor filterColor = null;
        filterColor = colourArrayList.get(position);
        FilterColourListAdapter.selected_item = position;
        recyclerViewColour.getAdapter().notifyDataSetChanged();
        colourID = filterColor.getid();
    }
}

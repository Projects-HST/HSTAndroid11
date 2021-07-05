package com.hst.osa_lilamore.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.hst.osa_lilamore.R;
import com.hst.osa_lilamore.adapter.BestSellingListAdapter;
import com.hst.osa_lilamore.adapter.RecentSearchListAdapter;
import com.hst.osa_lilamore.adapter.SubCategoryListAdapter;
import com.hst.osa_lilamore.bean.support.Product;
import com.hst.osa_lilamore.bean.support.RecentSearch;
import com.hst.osa_lilamore.bean.support.RecentSearchList;
import com.hst.osa_lilamore.bean.support.SubCategory;
import com.hst.osa_lilamore.bean.support.SubProductList;
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

public class SubCategoryActivity extends AppCompatActivity implements IServiceListener, SubCategoryListAdapter.OnItemClickListener, DialogClickListener, BestSellingListAdapter.OnItemClickListener, RecentSearchListAdapter.OnItemClickListener {

    private static final String TAG = com.hst.osa_lilamore.activity.MainActivity.class.getName();
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;

    private ArrayList<SubCategory> subCategoryArrayList = new ArrayList<>();
    private SubCategoryListAdapter mAdapter;

    private ArrayList<Product> productArrayList = new ArrayList<>();
    SubProductList productList;

    private ArrayList<RecentSearch> recentSearchArrayList = new ArrayList<>();
    RecentSearchList searchList;
    RecentSearchListAdapter recentSearchListAdapter;

    private LinearLayout recentSearchLay, subCatLay;

    private RecyclerView recentSearchList, recyclerViewPopularProduct, recyclerViewSubCategory;

    private SearchView mSearchView;

    private String catId, subCatId, serviceCall;

    private ImageView imgFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_category);

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_left_arrow));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recentSearchLay = (LinearLayout)findViewById(R.id.recentList);
        subCatLay = (LinearLayout)findViewById(R.id.subCatLay);

        mSearchView = (SearchView)findViewById(R.id.sub_cat_search);
        mSearchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchView.setIconified(false);
                subCatLay.setVisibility(View.GONE);
                recentSearchLay.setVisibility(View.VISIBLE);
                getRecentSearch();
            }
        });

        mSearchView.setQueryHint("What are you looking");

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if (query != null){
                    makeSearch(query);
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                return false;
            }
        });

        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                recentSearchLay.setVisibility(View.GONE);
                subCatLay.setVisibility(View.VISIBLE);
                recentSearchArrayList.clear();
                return false;
            }
        });

        recentSearchList = (RecyclerView)findViewById(R.id.recent_search);
        recyclerViewSubCategory = (RecyclerView) findViewById(R.id.sub_cat);
        recyclerViewPopularProduct = (RecyclerView) findViewById(R.id.sub_product_list);

        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerViewSubCategory.setLayoutManager(mLayoutManager);
        recyclerViewSubCategory.setHasFixedSize(true);

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);

        progressDialogHelper = new ProgressDialogHelper(this);

        catId = getIntent().getStringExtra("categoryObj");


        imgFilter = (ImageView) findViewById(R.id.img_filter);
        imgFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), com.hst.osa_lilamore.activity.AdvancedFilterActivity.class);
                i.putExtra("categoryObj", catId);
                startActivity(i);
            }
        });

        showSubCategory();
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
            try {
                if (serviceCall.equalsIgnoreCase("sub_category")) {

                    JSONArray subCategoryArray = response.getJSONArray("sub_category_list");
                    JSONObject subCatObj = subCategoryArray.getJSONObject(0);

                    Log.d(TAG, subCatObj.toString());

                    String id = "";
                    String txtSubCat = "";
                    subCategoryArrayList = new ArrayList<>();
                    subCategoryArrayList.add(new SubCategory("", "ALL"));

                    for (int i = 0; i < subCategoryArray.length(); i++) {
                        id = subCategoryArray.getJSONObject(i).getString("id");
                        txtSubCat = subCategoryArray.getJSONObject(i).getString("category_name");
                        subCategoryArrayList.add(new SubCategory(id, txtSubCat));
                    }
                    mAdapter = new SubCategoryListAdapter(this, subCategoryArrayList, this);
                    recyclerViewSubCategory.setAdapter(mAdapter);
                    subCatProductList();
                }
                if (serviceCall.equalsIgnoreCase("sub_cat_product_list")) {
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
                    recyclerViewPopularProduct.setLayoutManager(mLayoutManager);
                    recyclerViewPopularProduct.setAdapter(adasd);
                }
                if(serviceCall.equalsIgnoreCase("search")){
                    Gson gson = new Gson();
                    productList = gson.fromJson(response.toString(), SubProductList.class);
                    productArrayList.addAll(productList.getProductArrayList());

                    Intent intentSearch = new Intent(this, com.hst.osa_lilamore.activity.SearchResultActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("searchObj", productArrayList);
                    intentSearch.putExtras(bundle);
                    startActivity(intentSearch);
                }
                if(serviceCall.equalsIgnoreCase("recentSearch")){
                    Gson gson = new Gson();
                    searchList = gson.fromJson(response.toString(), RecentSearchList.class);
                    recentSearchArrayList.addAll(searchList.getRecentSearchArrayList());
                    recentSearchListAdapter = new RecentSearchListAdapter(recentSearchArrayList, this);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                    recentSearchList.setLayoutManager(linearLayoutManager);
                    recentSearchList.setAdapter(recentSearchListAdapter);
                    recentSearchList.invalidate();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onError(String error) {

    }

    @Override
    public void onItemClick(View view, int position) {
        SubCategory category = null;
//        if (mAdapter != null) {
//            d(TAG, "while clicking");
//            int actualIndex = mAdapter.getItemViewType(position);
//            d(TAG, "actual index" + actualIndex);
//            category = subCategoryArrayList.get(position);
//        } else {
//            category = subCategoryArrayList.get(position);
//        }
        SubCategoryListAdapter.selected_item = position;
        recyclerViewSubCategory.getAdapter().notifyDataSetChanged();
        category = subCategoryArrayList.get(position);
        subCatId = category.getId();
        productArrayList.clear();
        subCatProductList();
    }

    private void subCatProductList() {

        serviceCall = "sub_cat_product_list";

        JSONObject jsonObject = new JSONObject();
        String id = PreferenceStorage.getUserId(this);
        try {
            jsonObject.put(OSAConstants.KEY_USER_ID, id);
            jsonObject.put(OSAConstants.KEY_CAT_ID, catId);
            jsonObject.put(OSAConstants.KEY_SUB_CAT_ID, subCatId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String serverUrl = OSAConstants.BUILD_URL + OSAConstants.PRODUCT_LIST;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), serverUrl);
    }

    private void makeSearch(String searchName){

        serviceCall = "search";

        JSONObject jsonObject = new JSONObject();
        String id = PreferenceStorage.getUserId(this);
        try {
            jsonObject.put(OSAConstants.KEY_SEARCH, searchName);
            jsonObject.put(OSAConstants.KEY_USER_ID, id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = OSAConstants.BUILD_URL + OSAConstants.SEARCH_PRODUCT;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    private void getRecentSearch(){

//        recentSearchLay.setVisibility(View.VISIBLE);
        serviceCall = "recentSearch";
        JSONObject jsonObject = new JSONObject();
        String id = PreferenceStorage.getUserId(this);
        try {
            jsonObject.put(OSAConstants.KEY_USER_ID, id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = OSAConstants.BUILD_URL + OSAConstants.RECENT_SEARCH;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    @Override
    public void onItemClickBestSelling(View view, int position) {

        Product product = null;
        product = productArrayList.get(position);
        Intent detailInt = new Intent(this, com.hst.osa_lilamore.activity.ProductDetailActivity.class);
        detailInt.putExtra("page", "subcat");
        detailInt.putExtra("productObj", product.getid());
        startActivity(detailInt);
    }

    @Override
    public void onAlertPositiveClicked(int tag) {

    }

    @Override
    public void onAlertNegativeClicked(int tag) {

    }

    @Override
    public void onItemClickRecentSearch(View view, int position) {

    }
}
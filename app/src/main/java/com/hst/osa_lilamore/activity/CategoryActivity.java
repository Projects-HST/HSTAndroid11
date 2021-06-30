package com.hst.osa_lilamore.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.hst.osa_lilamore.R;
import com.hst.osa_lilamore.adapter.CategoryListAdapter;
import com.hst.osa_lilamore.adapter.RecentSearchListAdapter;
import com.hst.osa_lilamore.bean.support.Category;
import com.hst.osa_lilamore.bean.support.CategoryList;
import com.hst.osa_lilamore.bean.support.Product;
import com.hst.osa_lilamore.bean.support.RecentSearch;
import com.hst.osa_lilamore.bean.support.RecentSearchList;
import com.hst.osa_lilamore.bean.support.SubProductList;
import com.hst.osa_lilamore.fragment.BestSellingFragment;
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

public class CategoryActivity extends AppCompatActivity implements IServiceListener, DialogClickListener, CategoryListAdapter.OnItemClickListener, RecentSearchListAdapter.OnItemClickListener {

    private static final String TAG = CategoryActivity.class.getName();
    Context context;
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    private Handler mHandler = new Handler();
    int totalCount = 0, checkrun = 0;
    protected boolean isLoadingForFirstTime = true;

    private ArrayList<Category> categoryArrayList = new ArrayList<>();
    CategoryList categoryList;
    private CategoryListAdapter mAdapter;
    private ArrayList<Product> productArrayList = new ArrayList<>();
    SubProductList productList;
    private ArrayList<RecentSearch> recentSearchArrayList = new ArrayList<>();
    RecentSearchList searchList;
    RecentSearchListAdapter recentSearchListAdapter;

    private LinearLayout recentSearchLay, catLay;
    private SearchView mSearchView;
    private RecyclerView recyclerViewCategory,  recentSearchList;
    private View rootView;
    private TextView itemCount;
    private String serviceCall;

    public static BestSellingFragment newInstance(int position) {
        BestSellingFragment frag = new BestSellingFragment();
        Bundle b = new Bundle();
        b.putInt("position", position);
        frag.setArguments(b);
        return frag;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        Toolbar toolbar = (Toolbar)findViewById(R.id.activity_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_left_arrow));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recentSearchLay = (LinearLayout)findViewById(R.id.recentCatLay);
        catLay = (LinearLayout)findViewById(R.id.catLay);
        recyclerViewCategory = (RecyclerView) findViewById(R.id.listView_best_selling);
        recentSearchList = (RecyclerView)findViewById(R.id.recentSearchCat);
        mSearchView = (SearchView)findViewById(R.id.search_dash);
        itemCount = (TextView) findViewById(R.id.item_count);
        initiateServices();
        getDashboardServices();
    }

    public void initiateServices() {

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);

        mSearchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchView.setIconified(false);
                catLay.setVisibility(View.GONE);
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
//
//                if (newText.length() == 0) {
//                    recentSearchListAdapter.clearText();
//                } else {
//                    recentSearchList.invalidate();
//                }
                return false;
            }
        });

        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                recentSearchLay.setVisibility(View.GONE);
                catLay.setVisibility(View.VISIBLE);
                recentSearchArrayList.clear();
                return false;
            }
        });

    }

    private void makeSearch(String searchName){

        serviceCall = "search";

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(OSAConstants.KEY_SEARCH, searchName);
            jsonObject.put(OSAConstants.KEY_USER_ID, "");
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
        try {
            jsonObject.put(OSAConstants.KEY_USER_ID, "0");
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = OSAConstants.BUILD_URL + OSAConstants.RECENT_SEARCH;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
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
                if (serviceCall.equalsIgnoreCase("homepage")) {
                    Gson gson = new Gson();

                    JSONObject categoryObjData = response.getJSONObject("cat_list");
                    categoryList = gson.fromJson(categoryObjData.toString(), CategoryList.class);
                    categoryArrayList.addAll(categoryList.getCategoryArrayList());
                    mAdapter = new CategoryListAdapter(categoryArrayList, this);
                    GridLayoutManager mLayoutManager = new GridLayoutManager(this, 4);
                    mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                        @Override
                        public int getSpanSize(int position) {
                            if (mAdapter.getItemViewType(position) > 0) {
                                return mAdapter.getItemViewType(position);
                            } else {
                                return 1;
                            }
                            //return 2;
                        }
                    });
                    recyclerViewCategory.setLayoutManager(mLayoutManager);
                    recyclerViewCategory.setAdapter(mAdapter);
//                itemCount.setText(categoryArrayList.size() + " Items");
                }
                if(serviceCall.equalsIgnoreCase("search")){
                    Gson gson = new Gson();
                    productList = gson.fromJson(response.toString(), SubProductList.class);
                    productArrayList.addAll(productList.getProductArrayList());
//                    BestSellingListAdapter adasd = new BestSellingListAdapter(productArrayList, this);
//                    GridLayoutManager mLayoutManager = new GridLayoutManager(this, 4);
//                    mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
//                        @Override
//                        public int getSpanSize(int position) {
//                            if (adasd.getItemViewType(position) > 0) {
//                                return adasd.getItemViewType(position);
//                            } else {
//                                return 1;
//                            }
//                            //return 2;
//                        }
//                    });
//                    recyclerViewPopularProduct.setLayoutManager(mLayoutManager);
//                    recyclerViewPopularProduct.setAdapter(adasd);

                    Intent intentSearch = new Intent(this, SearchResultActivity.class);
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
        else {
            Log.d(TAG, "Error while sign In");
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
    public void onItemClickCategory(View view, int position) {
        Category category = null;
        category = categoryArrayList.get(position);
        Intent intent;
        intent = new Intent(this, SubCategoryActivity.class);
        intent.putExtra("categoryObj", category.getid());
        startActivity(intent);
    }

    private void getDashboardServices() {

        serviceCall = "homepage";
        JSONObject jsonObject = new JSONObject();
        String id = PreferenceStorage.getUserId(this);
        try {
//            jsonObject.put(SkilExConstants.USER_MASTER_ID, PreferenceStorage.getUserId(this));
            jsonObject.put(OSAConstants.KEY_USER_ID, id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = OSAConstants.BUILD_URL + OSAConstants.DASHBOARD;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    @Override
    public void onItemClickRecentSearch(View view, int position) {

    }
}

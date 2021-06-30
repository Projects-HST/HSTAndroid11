package com.hst.osa_lilamore.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.hst.osa_lilamore.R;
import com.hst.osa_lilamore.adapter.BestSellingListAdapter;
import com.hst.osa_lilamore.adapter.RecentSearchListAdapter;
import com.hst.osa_lilamore.bean.support.Product;
import com.hst.osa_lilamore.bean.support.RecentSearch;
import com.hst.osa_lilamore.bean.support.RecentSearchList;
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

public class WishListActivity extends AppCompatActivity implements IServiceListener, DialogClickListener, BestSellingListAdapter.OnItemClickListener, RecentSearchListAdapter.OnItemClickListener {

    public static final String TAG = WishListActivity.class.getName();

    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;

    private SearchView mSearchView;
    private RecyclerView recyclerWishList, recentSearchList;
    private TextView wishCount;
    private LinearLayout recentSearchLay, wishListLay;

    private String serviceCall;
    private ArrayList<RecentSearch> recentSearchArrayList = new ArrayList<>();
    RecentSearchList searchList;
    RecentSearchListAdapter recentSearchListAdapter;
    private ArrayList<Product> productArrayList = new ArrayList<>();
    SubProductList productList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_left_arrow));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        recyclerWishList = (RecyclerView) findViewById(R.id.favList);
        recentSearchList = (RecyclerView)findViewById(R.id.recent_search);
        wishCount = (TextView) findViewById(R.id.wishCount);
        recentSearchLay = (LinearLayout)findViewById(R.id.recentList);
        wishListLay = (LinearLayout)findViewById(R.id.favListLay);

        mSearchView = (SearchView)findViewById(R.id.searchWish);
        mSearchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchView.setIconified(false);
                wishListLay.setVisibility(View.GONE);
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
                wishListLay.setVisibility(View.VISIBLE);
                recentSearchArrayList.clear();
                return false;
            }
        });

        initiateServices();
        getWishList();
    }

    public void initiateServices() {
        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);
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
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(activity.getWindow().getDecorView().getWindowToken(), 0);
        }
    }

    private void getWishList() {
        serviceCall = "wishList";
        JSONObject jsonObject = new JSONObject();
        String id = PreferenceStorage.getUserId(this);
        try {
            jsonObject.put(OSAConstants.KEY_USER_ID, id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String serverUrl = OSAConstants.BUILD_URL + OSAConstants.VIEW_WISHLIST;
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
        String url = OSAConstants.BUILD_URL + OSAConstants.SEARCH_PRODUCT;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    private void getRecentSearch(){
        serviceCall = "recentSearch";
        JSONObject jsonObject = new JSONObject();
        String id = PreferenceStorage.getUserId(this);
        try {
            jsonObject.put(OSAConstants.KEY_USER_ID, id);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = OSAConstants.BUILD_URL + OSAConstants.RECENT_SEARCH;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    @Override
    public void onResponse(JSONObject response) {

        if (validateSignInResponse(response)) {

            try {
                if (serviceCall.equalsIgnoreCase("wishList")) {
                    String count = response.getString("wishlist_count");
                    wishCount.setText(count + " Items ");

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
                    recyclerWishList.setLayoutManager(mLayoutManager);
                    recyclerWishList.setAdapter(adasd);
                }
                if(serviceCall.equalsIgnoreCase("search")){
                    Gson gson = new Gson();
                    productList = gson.fromJson(response.toString(), SubProductList.class);
                    productArrayList.addAll(productList.getProductArrayList());

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
    public void onItemClickBestSelling(View view, int position) {

    }

    @Override
    public void onItemClickRecentSearch(View view, int position) {

    }
}
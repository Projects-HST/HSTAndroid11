package com.hst.osa_lilamore.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.hst.osa_lilamore.R;
import com.hst.osa_lilamore.adapter.RecentSearchListAdapter;
import com.hst.osa_lilamore.bean.support.Product;
import com.hst.osa_lilamore.bean.support.RecentSearch;
import com.hst.osa_lilamore.bean.support.RecentSearchList;
import com.hst.osa_lilamore.bean.support.SubProductList;
import com.hst.osa_lilamore.fragment.CategoryFragment;
import com.hst.osa_lilamore.fragment.DashboardFragment;
import com.hst.osa_lilamore.helpers.AlertDialogHelper;
import com.hst.osa_lilamore.helpers.ProgressDialogHelper;
import com.hst.osa_lilamore.interfaces.DialogClickListener;
import com.hst.osa_lilamore.interfaces.OnBackPressedListener;
import com.hst.osa_lilamore.servicehelpers.ServiceHelper;
import com.hst.osa_lilamore.serviceinterfaces.IServiceListener;
import com.hst.osa_lilamore.utils.OSAConstants;
import com.hst.osa_lilamore.utils.OSAValidator;
import com.hst.osa_lilamore.utils.PreferenceStorage;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.util.Log.d;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, OnBackPressedListener, IServiceListener, DialogClickListener, RecentSearchListAdapter.OnItemClickListener {

    private static final String TAG = MainActivity.class.getName();
    Toolbar toolbar;
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private LinearLayout sideDash, sideProfile, sideCat, sideWish, sideOrder, sideWallet, sideAddress, sideSettings, sideLogout, recentSearchLay;
    private FrameLayout mainLay;
    private TextView name, mailId;
    private SearchView mSearchView;
    private RecyclerView recentSearchList;
    private boolean doubleBackToExitPressedOnce = false;
    private ImageView profilePic;
    NavigationView navigationView;
    private String page = "", serviceCall;

    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;

    private ArrayList<Product> productArrayList = new ArrayList<>();
    SubProductList productList;
    private ArrayList<RecentSearch> recentSearchArrayList = new ArrayList<>();
    RecentSearchList searchList;
    RecentSearchListAdapter recentSearchListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.activity_toolbar);
        setSupportActionBar(toolbar);

//        page = getIntent().getExtras().getString("page");

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);

        progressDialogHelper = new ProgressDialogHelper(this);
        initializeNavigationDrawer();
        initializeIDs();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.right_menu, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //Workaround for SearchView close listener
        switch (item.getItemId()) {
            case R.id.menu_cart:
                startActivity(new Intent(this, CartActivity.class));
                break;
            case R.id.menu_notification:
                startActivity(new Intent(this, NotificationActivity.class));
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return super.onOptionsItemSelected(item);
    }

    private void initializeIDs() {

        recentSearchLay = (LinearLayout) findViewById(R.id.recentMainLay);
        mainLay = (FrameLayout) findViewById(R.id.fragmentContainer);
        recentSearchList = (RecyclerView) findViewById(R.id.recentSearchMain);
        mSearchView = (SearchView) findViewById(R.id.search_main);
        navigationView = findViewById(R.id.nav_view);
        profilePic = navigationView.getHeaderView(0).findViewById(R.id.user_img);
        name = navigationView.getHeaderView(0).findViewById(R.id.full_name);
        mailId = navigationView.getHeaderView(0).findViewById(R.id.area);


        mSearchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchView.setIconified(false);
                mainLay.setVisibility(View.GONE);
                recentSearchLay.setVisibility(View.VISIBLE);
                getRecentSearch();
            }
        });

        mSearchView.setQueryHint("What are you looking");

        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if (query != null) {
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
                mainLay.setVisibility(View.VISIBLE);
                recentSearchArrayList.clear();
                return false;
            }
        });

        if (PreferenceStorage.getName(this).equalsIgnoreCase("") || PreferenceStorage.getName(this).isEmpty()) {
            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //What to do on back clicked
                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                    i.putExtra("page", "dash");
                    startActivity(i);
                }
            });
        } else {
            name.setText(PreferenceStorage.getName(this));
            mailId.setText(PreferenceStorage.getEmail(this));
        }

        if (PreferenceStorage.getFullName(this).equalsIgnoreCase("") || PreferenceStorage.getFullName(this).isEmpty()) {
            name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //What to do on back clicked
                    Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                    i.putExtra("page", "dash");
                    startActivity(i);
                }
            });
        } else {
            name.setText(PreferenceStorage.getFullName(this));
            mailId.setText(PreferenceStorage.getEmail(this));
        }
        String url = PreferenceStorage.getProfilePic(this);
        String getSocialUrl = PreferenceStorage.getSocialNetworkProfileUrl(this);
        if (((url != null) && !(url.isEmpty()))) {
            Picasso.get().load(url).placeholder(R.drawable.ic_profile)
                    .error(R.drawable.ic_profile).into(profilePic);
        } else if (((getSocialUrl != null) && !(getSocialUrl.isEmpty()))) {
            Picasso.get().load(getSocialUrl).placeholder(R.drawable.ic_profile)
                    .error(R.drawable.ic_profile).into(profilePic);
        }

        sideDash = navigationView.getHeaderView(0).findViewById(R.id.side_dashboard);
        sideProfile = navigationView.getHeaderView(0).findViewById(R.id.side_profile);
        sideCat = navigationView.getHeaderView(0).findViewById(R.id.side_category);
        sideWish = navigationView.getHeaderView(0).findViewById(R.id.side_wishlist);
        sideOrder = navigationView.getHeaderView(0).findViewById(R.id.side_order_history);
        sideWallet = navigationView.getHeaderView(0).findViewById(R.id.side_wallet);
        sideAddress = navigationView.getHeaderView(0).findViewById(R.id.side_address);
        sideSettings = navigationView.getHeaderView(0).findViewById(R.id.side_settings);
        sideLogout = navigationView.getHeaderView(0).findViewById(R.id.side_logout);

        if (((PreferenceStorage.getFullName(this).equalsIgnoreCase(""))&&
                (PreferenceStorage.getFullName(this).isEmpty()))){
            sideLogout.setVisibility(View.GONE);
        }else {
            sideLogout.setVisibility(View.VISIBLE);
        }

        sideDash.setOnClickListener(this);
        sideProfile.setOnClickListener(this);
        sideCat.setOnClickListener(this);
        sideWish.setOnClickListener(this);
        sideOrder.setOnClickListener(this);
        sideWallet.setOnClickListener(this);
        sideAddress.setOnClickListener(this);
        sideSettings.setOnClickListener(this);
        sideLogout.setOnClickListener(this);

        changePage(0);
    }

//    @Override
//    public void onBackPressed() {
//        //Checking for fragment count on backstack
//        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
//            getSupportFragmentManager().popBackStack();
//        } else if (!doubleBackToExitPressedOnce) {
//            this.doubleBackToExitPressedOnce = true;
//            Toast.makeText(this, "Please click BACK again to exit.", Toast.LENGTH_SHORT).show();
//            new Handler().postDelayed(new Runnable() {
//
//                @Override
//                public void run() {
//                    doubleBackToExitPressedOnce = false;
//                }
//            }, 2000);
//        } else {
//            super.onBackPressed();
//            return;
//        }
//    }

    public void changePage(int position) {

        Fragment newFragment = null;

        if (position == 0) {
            toolbar.setTitle(getString(R.string.side_menu_dash_title));
            mDrawerLayout.closeDrawers();
            newFragment = new DashboardFragment();
        } else if (position == 1) {
            newFragment = new CategoryFragment();
            toolbar.setTitle(getString(R.string.side_menu_category));
            mDrawerLayout.closeDrawers();
        }
        getSupportFragmentManager().beginTransaction().replace(
                R.id.fragmentContainer, newFragment)
                .commit();
    }

    private void logout() {
        android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(new ContextThemeWrapper(this, R.style.alertDialogueTheme));
        alertDialogBuilder.setTitle(getString(R.string.sign_out));
        alertDialogBuilder.setMessage(getString(R.string.sign_out_alert));
        alertDialogBuilder.setPositiveButton(R.string.alert_button_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(alertDialogBuilder.getContext());
                sharedPreferences.edit().clear().apply();
//        TwitterUtil.getInstance().resetTwitterRequestToken();


                Intent homeIntent = new Intent(alertDialogBuilder.getContext(), SplashscreenActivity.class);
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
            }
        });
        alertDialogBuilder.setNegativeButton(R.string.alert_button_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertDialogBuilder.show();
    }

    private void initializeNavigationDrawer() {

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                toolbar,
                R.string.drawer_open,
                R.string.drawer_close
        ) {
            public void onDrawerClosed(View view) {

            }

            public void onDrawerOpened(View drawerView) {
//                String userProfileName = PreferenceStorage.getName(getApplicationContext());
//                String url = PreferenceStorage.getUserPicture(ParentDashBoardActivity.this);
//
//                Log.d(TAG, "user name value" + userProfileName);
//                if ((userProfileName != null) && !userProfileName.isEmpty()) {
//                    String[] splitStr = userProfileName.split("\\s+");
//                    navUserProfileName.setText("Hi, " + splitStr[0]);
//                }
//
//                if (((url != null) && !(url.isEmpty())) && !(url.equalsIgnoreCase(mCurrentUserProfileUrl))) {
//                    Log.d(TAG, "image url is " + url);
//                    mCurrentUserProfileUrl = url;
//                    Picasso.with(ParentDashBoardActivity.this).load(url).noPlaceholder().error(R.drawable.ab_logo).into(imgNavProfileImage);
//                }
            }
        };
        mDrawerToggle.setDrawerIndicatorEnabled(false);
        mDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_hambugger);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mDrawerLayout.isDrawerVisible(GravityCompat.START)) {
                    mDrawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    mDrawerLayout.openDrawer(GravityCompat.START);
                }
            }
        });
        // enable ActionBar app icon to behave as action to toggle nav drawer
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * Using in Base Fragment
     */
    public ActionBarDrawerToggle getToggle() {
        return mDrawerToggle;
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);

        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else if (fragment instanceof OnBackPressedListener) {
            ((OnBackPressedListener) fragment).onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onClick(View view) {
        if (view == sideDash) {
            changePage(0);
        }
        if (view == sideProfile) {
            Intent i = new Intent(this, com.hst.osa_lilamore.activity.EditProfile.class);
//            i.putExtra("page", "editProfile");
            startActivity(i);
        }
        if (view == sideCat) {
            Intent i = new Intent(this, com.hst.osa_lilamore.activity.CategoryActivity.class);
            startActivity(i);
//            changePage(1);
        }
        if (view == sideWish) {
            Intent i = new Intent(this, com.hst.osa_lilamore.activity.WishListActivity.class);
            startActivity(i);
        }
        if (view == sideOrder) {
            Intent i = new Intent(this, com.hst.osa_lilamore.activity.OrderHistoryActivity.class);
            startActivity(i);
        }
        if (view == sideWallet) {
            Intent i = new Intent(this, com.hst.osa_lilamore.activity.WalletActivity.class);
            startActivity(i);
        }
        if (view == sideAddress) {
            Intent i = new Intent(this, com.hst.osa_lilamore.activity.ShippingAddressActivity.class);
            i.putExtra("page", "shippingAddress");
            startActivity(i);

        }
        if (view == sideSettings) {
            Intent i = new Intent(this, com.hst.osa_lilamore.activity.SettingsActivity.class);
            startActivity(i);
        }
        if (view == sideLogout) {
            logout();
        }
    }

    private void makeSearch(String searchName) {

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

    private void getRecentSearch() {

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
                if (serviceCall.equalsIgnoreCase("search")) {
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

                    Intent intentSearch = new Intent(this, com.hst.osa_lilamore.activity.SearchResultActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("searchObj", productArrayList);
                    intentSearch.putExtras(bundle);
                    startActivity(intentSearch);
                }
                if (serviceCall.equalsIgnoreCase("recentSearch")) {
                    Gson gson = new Gson();
                    searchList = gson.fromJson(response.toString(), RecentSearchList.class);
                    recentSearchArrayList.addAll(searchList.getRecentSearchArrayList());
                    recentSearchListAdapter = new RecentSearchListAdapter(recentSearchArrayList, this);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
                    recentSearchList.setLayoutManager(linearLayoutManager);
                    recentSearchList.setAdapter(recentSearchListAdapter);
                    recentSearchList.invalidate();
                }
            } catch (Exception e) {
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
    public void onItemClickRecentSearch(View view, int position) {

    }
}
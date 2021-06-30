package com.hst.osa_lilamore.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.hst.osa_lilamore.R;
import com.hst.osa_lilamore.activity.SubCategoryActivity;
import com.hst.osa_lilamore.adapter.CategoryListAdapter;
import com.hst.osa_lilamore.bean.support.Category;
import com.hst.osa_lilamore.bean.support.CategoryList;
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

public class CategoryFragment extends BaseFragment implements IServiceListener, DialogClickListener, CategoryListAdapter.OnItemClickListener {

    private static final String TAG = BestSellingFragment.class.getName();
    Context context;
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    private Handler mHandler = new Handler();
    int totalCount = 0, checkrun = 0;
    protected boolean isLoadingForFirstTime = true;

    private ArrayList<Category> categoryArrayList = new ArrayList<>();
    Category category;
    CategoryList categoryList;
    private CategoryListAdapter mAdapter;
    private RecyclerView recyclerViewCategory;
    private View rootView;
    private TextView itemCount;
    private AppCompatActivity activity;

    public static BestSellingFragment newInstance(int position) {
        BestSellingFragment frag = new BestSellingFragment();
        Bundle b = new Bundle();
        b.putInt("position", position);
        frag.setArguments(b);
        return frag;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_category, container, false);
        initiateServices();

//        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.activity_toolbar);
//        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
//        toolbar.setNavigationIcon(ContextCompat.getDrawable(getActivity(), R.drawable.ic_left_arrow));
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Fragment newFragment = null;
//                newFragment = new DashboardFragment();
//                replaceFragment(newFragment);
//            }
//        });

        activity = (AppCompatActivity) getActivity();
        activity.getSupportActionBar().setTitle(R.string.category);
        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        activity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_left_arrow);
        activity.getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_left_arrow);


        recyclerViewCategory = (RecyclerView) rootView.findViewById(R.id.listView_best_selling);
        itemCount = (TextView) rootView.findViewById(R.id.item_count);
        getDashboardServices();
//        loadMob();
        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (activity.getSupportFragmentManager().getBackStackEntryCount() > 0) {
            if (item.getItemId() == R.id.home) {
                Fragment newFragment = null;
                newFragment = new DashboardFragment();
                replaceFragment(newFragment);
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }

    }

    public void initiateServices() {

        serviceHelper = new ServiceHelper(getActivity());
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(getActivity());

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
                        AlertDialogHelper.showSimpleAlertDialog(rootView.getContext(), msg);

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
                Gson gson = new Gson();

                JSONObject categoryObjData = response.getJSONObject("cat_list");
                categoryList = gson.fromJson(categoryObjData.toString(), CategoryList.class);
                categoryArrayList.addAll(categoryList.getCategoryArrayList());
                mAdapter = new CategoryListAdapter(categoryArrayList, this);
                GridLayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 4);
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
    public void onAlertPositiveClicked(int tag) {

    }

    @Override
    public void onAlertNegativeClicked(int tag) {

    }

    private void getDashboardServices() {
        JSONObject jsonObject = new JSONObject();
        String id = PreferenceStorage.getUserId(getActivity());
        try {
//            jsonObject.put(SkilExConstants.USER_MASTER_ID, PreferenceStorage.getUserId(getActivity()));
            jsonObject.put(OSAConstants.KEY_USER_ID, id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = OSAConstants.BUILD_URL + OSAConstants.DASHBOARD;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    public void replaceFragment(Fragment someFragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragmentContainer, someFragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onBackPressed() {
        activity.getSupportActionBar().setTitle(R.string.category);
        Fragment newFragment = null;
        newFragment = new DashboardFragment();
        replaceFragment(newFragment);
    }

    @Override
    public void onItemClickCategory(View view, int position) {
        Category category = null;
        category = categoryArrayList.get(position);
        Intent intent;
        intent = new Intent(getActivity(), SubCategoryActivity.class);
        intent.putExtra("categoryObj", category.getid());
        startActivity(intent);
    }
}

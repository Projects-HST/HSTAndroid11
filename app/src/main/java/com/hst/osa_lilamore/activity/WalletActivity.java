package com.hst.osa_lilamore.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.hst.osa_lilamore.R;
import com.hst.osa_lilamore.adapter.WalletListAdapter;
import com.hst.osa_lilamore.bean.support.Wallet;
import com.hst.osa_lilamore.bean.support.WalletList;
import com.hst.osa_lilamore.helpers.AlertDialogHelper;
import com.hst.osa_lilamore.helpers.ProgressDialogHelper;
import com.hst.osa_lilamore.interfaces.DialogClickListener;
import com.hst.osa_lilamore.servicehelpers.ServiceHelper;
import com.hst.osa_lilamore.serviceinterfaces.IServiceListener;
import com.hst.osa_lilamore.utils.CommonUtils;
import com.hst.osa_lilamore.utils.OSAConstants;
import com.hst.osa_lilamore.utils.PreferenceStorage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.util.Log.d;

public class WalletActivity extends AppCompatActivity implements IServiceListener, View.OnClickListener, DialogClickListener, WalletListAdapter.OnItemClickListener {
    private static final String TAG = WalletActivity.class.getName();

    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    private ImageView serviceImage;
    private TextView walletBal, addMoney;
    private RecyclerView walletTrans;
    private ArrayList<Wallet> walletArrayList = new ArrayList<>();
    WalletListAdapter walletListAdapter;
    int totalCount = 0, checkrun = 0;
    protected boolean isLoadingForFirstTime = true;
//    Service service;
    String res = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);

        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);
        Toolbar toolbar = (Toolbar)findViewById(R.id.activity_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_left_arrow));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        walletBal = (TextView) findViewById(R.id.wallet_bal);
        addMoney = (TextView) findViewById(R.id.btn_refer);
        addMoney.setOnClickListener(this);
        walletTrans = (RecyclerView) findViewById(R.id.listView_cart);

        callGetSubCategoryService();
    }

    public void callGetSubCategoryService() {
//        if (classTestArrayList != null)
//            classTestArrayList.clear();

        if (CommonUtils.isNetworkAvailable(this)) {
            progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
            getServiceDetail();
        } else {
            AlertDialogHelper.showSimpleAlertDialog(this, getString(R.string.error_no_net));
        }
    }

    private void getServiceDetail() {
        res = "detail";
        JSONObject jsonObject = new JSONObject();
        String id = "";
        id = PreferenceStorage.getUserId(this);
        try {
            jsonObject.put(OSAConstants.KEY_USER_ID, id);

        } catch (JSONException e) {
            e.printStackTrace();
        }

//        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = OSAConstants.BUILD_URL + OSAConstants.WALLET_DETAIL;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    private boolean validateResponse(JSONObject response) {
        boolean signInSuccess = false;
        if ((response != null)) {
            try {
                String status = response.getString("status");
//                String msg = response.getString(OSAConstants.PARAM_MESSAGE);
//                String msg_en = response.getString(OSAConstants.PARAM_MESSAGE_ENG);
//                String msg_ta = response.getString(OSAConstants.PARAM_MESSAGE_TAMIL);
//                d(TAG, "status val" + status + "msg" + msg);

                if ((status != null)) {
                    if (((status.equalsIgnoreCase("activationError")) || (status.equalsIgnoreCase("alreadyRegistered")) ||
                            (status.equalsIgnoreCase("notRegistered")) || (status.equalsIgnoreCase("error")))) {
                        signInSuccess = false;
                        d(TAG, "Show error dialog");

//                        if (PreferenceStorage.getLang(this).equalsIgnoreCase("tamil")) {
//                            AlertDialogHelper.showSimpleAlertDialog(this, msg_ta);
//                        } else {
//                            AlertDialogHelper.showSimpleAlertDialog(this, msg);
//                        }

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
        if (validateResponse(response)) {
            try {
                if (res.equalsIgnoreCase("detail")) {
                    walletBal.setText("₹"+response.getString("wallet_amount"));
//
                    Gson gson = new Gson();
                    WalletList walletList = gson.fromJson(response.toString(), WalletList.class);
                    walletArrayList.addAll(walletList.getWalletArrayList());
                    walletListAdapter = new WalletListAdapter( walletArrayList, this);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this);
                    walletTrans.setLayoutManager(mLayoutManager);
                    walletTrans.setAdapter(walletListAdapter);

                } else if (res.equalsIgnoreCase("cart")) {
                    Toast.makeText(this, response.getString("msg"), Toast.LENGTH_SHORT).show();
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

//    protected void updateListAdapter(ArrayList<Wallet> ongoingServiceArrayLists) {
//        walletArrayList.clear();
//        walletArrayList.addAll(ongoingServiceArrayLists);
//        if (walletListAdapter == null) {
//            walletListAdapter = new WalletListAdapter(this, walletArrayList);
//            walletTrans.setAdapter(walletListAdapter);
//        } else {
//            walletListAdapter.notifyDataSetChanged();
//        }
//    }

    @Override
    public void onError(String error) {

    }

    @Override
    public void onClick(View v) {
        if (v == addMoney) {
            if (PreferenceStorage.getUserId(this).equalsIgnoreCase("")) {
                android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(this, R.style.alertDialogueTheme);
                alertDialogBuilder.setTitle(R.string.login);
                alertDialogBuilder.setMessage(R.string.login_to_continue);
                alertDialogBuilder.setPositiveButton(R.string.alert_button_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        doLogout();
                    }
                });
                alertDialogBuilder.setNegativeButton(R.string.alert_button_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialogBuilder.show();
            } else {
                Intent homeIntent = new Intent(this, WalletAddMoneyActivity.class);
                homeIntent.putExtra("bal_amt", walletBal.getText().toString());
                startActivity(homeIntent);
            }

        }
    }

    private void doLogout() {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().clear().apply();
//        TwitterUtil.getInstance().resetTwitterRequestToken();

        Intent homeIntent = new Intent(this, SplashscreenActivity.class);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(homeIntent);
        finish();
    }

    @Override
    public void onAlertPositiveClicked(int tag) {

    }

    @Override
    public void onAlertNegativeClicked(int tag) {

    }

    @Override
    public void onItemClick(View view, int position) {

    }
}
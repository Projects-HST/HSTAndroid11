package com.hst.osa_lilamore.ccavenue.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.hst.osa_lilamore.R;
import com.hst.osa_lilamore.ccavenue.utility.AvenuesParams;
import com.hst.osa_lilamore.ccavenue.utility.ServiceUtility;
import com.hst.osa_lilamore.helpers.ProgressDialogHelper;
import com.hst.osa_lilamore.interfaces.DialogClickListener;
import com.hst.osa_lilamore.servicehelpers.ServiceHelper;
import com.hst.osa_lilamore.serviceinterfaces.IServiceListener;
import com.hst.osa_lilamore.utils.PreferenceStorage;

import org.json.JSONObject;

import static com.hst.osa_lilamore.utils.OSAConstants.API_ADVANCE_PAYMENT_URL;
import static com.hst.osa_lilamore.utils.OSAConstants.API_PAYMENT_URL;
import static com.hst.osa_lilamore.utils.OSAConstants.API_RSA_URL;
import static com.hst.osa_lilamore.utils.OSAConstants.API_WALLET_URL;


public class InitialScreenActivity extends AppCompatActivity implements IServiceListener, DialogClickListener {

    private EditText accessCode, merchantId, currency, amount, orderId, rsaKeyUrl, redirectUrl, cancelUrl;
    private TextView amountDisplay, amtP, walletAmt;
    String page;
    RelativeLayout advPay, walletPay;
    LinearLayout servPay;
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;

    private void init() {
        accessCode = (EditText) findViewById(R.id.accessCode);
        merchantId = (EditText) findViewById(R.id.merchantId);
        orderId = (EditText) findViewById(R.id.orderId);
        currency = (EditText) findViewById(R.id.currency);
        amount = (EditText) findViewById(R.id.amount);
        rsaKeyUrl = (EditText) findViewById(R.id.rsaUrl);
        redirectUrl = (EditText) findViewById(R.id.redirectUrl);
        cancelUrl = (EditText) findViewById(R.id.cancelUrl);
        amountDisplay = (TextView) findViewById(R.id.amount_display);
        amtP = (TextView) findViewById(R.id.amt);
        advPay = (RelativeLayout) findViewById(R.id.adv_pay_layout);
        walletPay = (RelativeLayout) findViewById(R.id.wallet_pay_layout);
        walletAmt = (TextView) findViewById(R.id.wallet_amount_display);
        servPay = (LinearLayout) findViewById(R.id.serv_pay_layout);

        String adv = (String) getIntent().getSerializableExtra("advpay");
        page = (String) getIntent().getSerializableExtra("page");

        if (page.equalsIgnoreCase("advance_pay")) {
            redirectUrl.setText(API_ADVANCE_PAYMENT_URL);
            cancelUrl.setText(API_ADVANCE_PAYMENT_URL);
            rsaKeyUrl.setText(API_RSA_URL);
            advPay.setVisibility(View.VISIBLE);
            amountDisplay.setText("₹" + adv);
        } else if (page.equalsIgnoreCase("wallet")) {
            redirectUrl.setText(API_WALLET_URL);
            cancelUrl.setText(API_WALLET_URL);
            rsaKeyUrl.setText(API_RSA_URL);
            walletPay.setVisibility(View.VISIBLE);
            walletAmt.setText("₹" + adv);
        } else {
            redirectUrl.setText(API_PAYMENT_URL);
            cancelUrl.setText(API_PAYMENT_URL);
            rsaKeyUrl.setText(API_RSA_URL);
            servPay.setVisibility(View.VISIBLE);
            amtP.setText("₹" + adv);
        }
        amount.setText(adv);
        orderId.setText(PreferenceStorage.getOrderId(this));

        Toolbar toolbar = (Toolbar)findViewById(R.id.activity_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_left_arrow));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(getApplicationContext(), R.style.alertDialogueTheme);
                alertDialogBuilder.setTitle("Payment");
                alertDialogBuilder.setMessage("Are you sure you want to cancel your order?");
                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent i = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(i);
                        finish();
                    }
                });
                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertDialogBuilder.show();
            }
        });

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_screen);
        init();
        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);
        onClick();
    }

    public void onClick() {
        //Mandatory parameters. Other parameters can be added if required.
        String vAccessCode = ServiceUtility.chkNull(accessCode.getText()).toString().trim();
        String vMerchantId = ServiceUtility.chkNull(merchantId.getText()).toString().trim();
        String vCurrency = ServiceUtility.chkNull(currency.getText()).toString().trim();
        String vAmount = ServiceUtility.chkNull(amount.getText()).toString().trim();
        if (!vAccessCode.equals("") && !vMerchantId.equals("") && !vCurrency.equals("") && !vAmount.equals("")) {
            Intent intent = new Intent(this, WebViewActivity.class);
            intent.putExtra(AvenuesParams.ACCESS_CODE, ServiceUtility.chkNull(accessCode.getText()).toString().trim());
            intent.putExtra(AvenuesParams.MERCHANT_ID, ServiceUtility.chkNull(merchantId.getText()).toString().trim());
            intent.putExtra(AvenuesParams.ORDER_ID, ServiceUtility.chkNull(orderId.getText()).toString().trim());
            intent.putExtra(AvenuesParams.CURRENCY, ServiceUtility.chkNull(currency.getText()).toString().trim());
            intent.putExtra(AvenuesParams.AMOUNT, ServiceUtility.chkNull(amount.getText()).toString().trim());

            intent.putExtra(AvenuesParams.REDIRECT_URL, ServiceUtility.chkNull(redirectUrl.getText()).toString().trim());
            intent.putExtra(AvenuesParams.CANCEL_URL, ServiceUtility.chkNull(cancelUrl.getText()).toString().trim());
            intent.putExtra(AvenuesParams.RSA_KEY_URL, ServiceUtility.chkNull(rsaKeyUrl.getText()).toString().trim());

            intent.putExtra("page", page);

            startActivity(intent);
            finish();
        } else {
            showToast("All parameters are mandatory.");
        }
    }

    public void showToast(String msg) {
        Toast.makeText(this, "Toast: " + msg, Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onStart() {
        super.onStart();
        //generating new order number for every transaction
//        Integer randomNum = ServiceUtility.randInt(0, 9999999);
//        orderId.setText(randomNum.toString());
    }

    @Override
    public void onAlertPositiveClicked(int tag) {

    }

    @Override
    public void onAlertNegativeClicked(int tag) {

    }

    @Override
    public void onResponse(JSONObject response) {

    }

    @Override
    public void onError(String error) {

    }
}
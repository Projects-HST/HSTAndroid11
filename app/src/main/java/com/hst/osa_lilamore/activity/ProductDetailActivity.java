package com.hst.osa_lilamore.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.hst.osa_lilamore.R;
import com.hst.osa_lilamore.adapter.BestSellingListAdapter;
import com.hst.osa_lilamore.adapter.ColourListAdapter;
import com.hst.osa_lilamore.adapter.ReviewListAdapter;
import com.hst.osa_lilamore.adapter.SizeListAdapter;
import com.hst.osa_lilamore.bean.support.Colour;
import com.hst.osa_lilamore.bean.support.ColourList;
import com.hst.osa_lilamore.bean.support.Product;
import com.hst.osa_lilamore.bean.support.RelatedProductList;
import com.hst.osa_lilamore.bean.support.Review;
import com.hst.osa_lilamore.bean.support.ReviewList;
import com.hst.osa_lilamore.bean.support.Size;
import com.hst.osa_lilamore.bean.support.SizeList;
import com.hst.osa_lilamore.customview.AViewFlipper;
import com.hst.osa_lilamore.helpers.AlertDialogHelper;
import com.hst.osa_lilamore.helpers.ProgressDialogHelper;
import com.hst.osa_lilamore.interfaces.DialogClickListener;
import com.hst.osa_lilamore.servicehelpers.ServiceHelper;
import com.hst.osa_lilamore.serviceinterfaces.IServiceListener;
import com.hst.osa_lilamore.utils.OSAConstants;
import com.hst.osa_lilamore.utils.PreferenceStorage;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.util.Log.d;

public class ProductDetailActivity extends AppCompatActivity implements IServiceListener, DialogClickListener, SizeListAdapter.OnItemClickListener, ColourListAdapter.OnItemClickListener, View.OnClickListener, ReviewListAdapter.OnItemClickListener, BestSellingListAdapter.OnItemClickListener {

    private static final String TAG = ProductDetailActivity.class.getName();
    Context context;
    private ServiceHelper serviceHelper;
    private ProgressDialogHelper progressDialogHelper;
    private String productID, sizeID = "0", colourID = "0", wishState;
    AViewFlipper aViewFlipper;

    private LinearLayout dotsLayout;
    private TextView[] dots;
    private ArrayList<String> imgUrl = new ArrayList<>();

    private RelativeLayout sizeLayout, colourLayout, reviewLayout;
    public RatingBar productRating;
    public TextView productName, productReviewName, productPrice, productShare, productQuantity, productStockStatus;
    public ImageView btnPlus, btnMinus;
    public EditText deliverCode;
    public TextView checkCode;
    public TextView productDetail, viewMore, productReviews, submitBtn;
    public TextView totalPrice, addCart;

    private RatingBar rtbComments;
    private EditText edtComments;
    private String reviewID = "", prdName = "";
    private RelativeLayout originalLayout, orderPlacedLayout;

    private ArrayList<Size> sizeArrayList = new ArrayList<>();
    private SizeListAdapter mAdapter;
    private RecyclerView recyclerViewSize;
    Size size;
    SizeList sizeList;

    private ArrayList<Colour> colourArrayList = new ArrayList<>();
    private RecyclerView recyclerViewColour;
    Colour colour;
    ColourList colourList;

    private ArrayList<Review> reviewArrayList = new ArrayList<>();
    private RecyclerView recyclerViewReview;
    Review review;
    ReviewList reviewList;

    private ArrayList<Product> productArrayList = new ArrayList<>();
    private RecyclerView recyclerViewRelatedProduct;
    Product product;
    RelatedProductList relatedProductList;

    ImageView imgLike;
    boolean likeClick = false;
    private boolean newReview = true;

    String resFor = "", offPer = "", offStatus = "";
    float currentPrice = 0;
    int stockCount = 0;
    String page = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_toolbar);
        setSupportActionBar(toolbar);
        page = getIntent().getExtras().getString("page");
        findViewById(R.id.img_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (page.equalsIgnoreCase("product")) {
                    Intent i = new Intent(getApplicationContext(), MainActivity.class);
                    i.putExtra("page", "product");
                    startActivity(i);
                }
                finish();
            }
        });
        findViewById(R.id.txt_write_review).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkLogin()) {
                    layoutVisible();
                }
            }
        });
        findViewById(R.id.click_detect).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layoutVisible();
            }
        });

//        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("wishListed"));

        productReviewName = findViewById(R.id.product_name_review);
        rtbComments = findViewById(R.id.rating_bar);
        edtComments = findViewById(R.id.edtComments);
        submitBtn = (TextView) findViewById(R.id.submit_review);
        submitBtn.setOnClickListener(this);
        originalLayout = (RelativeLayout) findViewById(R.id.original_layout);
        orderPlacedLayout = (RelativeLayout) findViewById(R.id.order_played_layout);

        productID = getIntent().getStringExtra("productObj");
        aViewFlipper = findViewById(R.id.view_flipper);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);

        sizeLayout = (RelativeLayout) findViewById(R.id.size_layout);
        colourLayout = (RelativeLayout) findViewById(R.id.colours_layout);
        reviewLayout = (RelativeLayout) findViewById(R.id.review_layout);

        recyclerViewSize = (RecyclerView) findViewById(R.id.listView_size);
        recyclerViewColour = (RecyclerView) findViewById(R.id.listView_colors);
        recyclerViewReview = (RecyclerView) findViewById(R.id.listView_reviews);
        recyclerViewRelatedProduct = (RecyclerView) findViewById(R.id.listView_related);

        productRating = (RatingBar) findViewById(R.id.ratingBar);
        productName = (TextView) findViewById(R.id.product_name);
        productPrice = (TextView) findViewById(R.id.product_price);
        productShare = (TextView) findViewById(R.id.share);
        productQuantity = (TextView) findViewById(R.id.quantity);
        productStockStatus = (TextView) findViewById(R.id.stock_status);
        imgLike = (ImageView) findViewById(R.id.product_like);
        imgLike.setOnClickListener(this);
        btnMinus = (ImageView) findViewById(R.id.minus);
        btnMinus.setOnClickListener(this);
        deliverCode = (EditText) findViewById(R.id.pincode);
        btnPlus = (ImageView) findViewById(R.id.plus);
        btnPlus.setOnClickListener(this);
        productDetail = (TextView) findViewById(R.id.product_detail);
        viewMore = (TextView) findViewById(R.id.product_detail_more);
        productReviews = (TextView) findViewById(R.id.txt_reviews);
        totalPrice = (TextView) findViewById(R.id.total_price);
        addCart = (TextView) findViewById(R.id.add_to_cart);
        addCart.setOnClickListener(this);

        initiateServices();
        getDashboardServices();
    }

//    public BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//
//            wishState = intent.getStringExtra("wishState");
//        }
//    };


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        super.dispatchTouchEvent(event);
        return gestureDetector.onTouchEvent(event);
    }

    GestureDetector.SimpleOnGestureListener simpleOnGestureListener = new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
                               float velocityY) {

            float sensitvity = 100;
            if (imgUrl.size() >= 1) {
                if ((e1.getX() - e2.getX()) > sensitvity) {
                    SwipeLeft();
                } else if ((e2.getX() - e1.getX()) > sensitvity) {
                    SwipeRight();
                }
            }
            return true;
        }
    };

    GestureDetector gestureDetector = new GestureDetector(simpleOnGestureListener);


    private void SwipeLeft() {
        aViewFlipper.setInAnimation(this, R.anim.in_from_right);
        aViewFlipper.showNext();
    }

    private void SwipeRight() {
        aViewFlipper.setInAnimation(this, R.anim.in_from_left);
        aViewFlipper.showPrevious();
    }

    private void setImageInFlipr(String imgUrl) {
        ImageView image = new ImageView(this);
        Picasso.get().load(imgUrl).into(image);
        image.setScaleType(ImageView.ScaleType.FIT_XY);
        aViewFlipper.addView(image);
    }

    public void initiateServices() {
        serviceHelper = new ServiceHelper(this);
        serviceHelper.setServiceListener(this);
        progressDialogHelper = new ProgressDialogHelper(this);
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
                        if (!resFor.equalsIgnoreCase("review")) {
                            AlertDialogHelper.showSimpleAlertDialog(this, msg);
                        }
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
                if (resFor.equalsIgnoreCase("detail")) {
                    JSONObject productDetails = response.getJSONObject("product_details").getJSONObject("product_details");
                    imgUrl.add(productDetails.getString("product_cover_img"));
                    setImageInFlipr(imgUrl.get(0));
                    productName.setText(productDetails.getString("product_name"));
                    productDetail.setText(productDetails.getString("product_description"));
                    offStatus = productDetails.getString("offer_status");
                    offPer = productDetails.getString("offer_percentage");
                    stockCount = Integer.parseInt(productDetails.getString("stocks_left"));
                    wishState = productDetails.getString("wishlisted");
                    if (wishState.equalsIgnoreCase("1")){
//                        likeClick = true;
                        imgLike.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_heart_filled));
                    }
                    else {
//                        likeClick = false;
                        imgLike.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_heart));
                    }
                    if (stockCount == 0) {
                        productStockStatus.setText(getString(R.string.out_stock));
                        productStockStatus.setTextColor(ContextCompat.getColor(this, R.color.out_of_stock));
                    }
                    if (offStatus.equalsIgnoreCase("0")) {
                        productPrice.setText("Rs." + productDetails.getString("prod_actual_price"));
                        currentPrice = Float.parseFloat(productDetails.getString("prod_actual_price"));
                    } else {
                        String offPrice = productDetails.getString("prod_actual_price");
                        double offer = Double.parseDouble(offPer);
                        double offCal = Double.parseDouble(offPrice);
                        double actualAmt = (offCal / 100.0f) * offer;
                        double amount = (offCal - actualAmt);
                        productPrice.setText("Rs." + amount);
                        currentPrice = Float.parseFloat(String.valueOf(amount));
                    }
                    calculatePrice(1);

                    if (response.getJSONArray("related_products").length() > 0) {
                        Gson gson = new Gson();
                        relatedProductList = null;
                        relatedProductList = gson.fromJson(response.toString(), RelatedProductList.class);
                        productArrayList.addAll(relatedProductList.getProductArrayList());
                        BestSellingListAdapter adapter = new BestSellingListAdapter(this, productArrayList, this);
//                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
                        recyclerViewRelatedProduct.setLayoutManager(layoutManager);
//                    mRecyclerView.setLayoutManager(mLayoutManager);
                        recyclerViewRelatedProduct.setAdapter(adapter);
                    }

                    if (response.getJSONObject("product_review").getString("status").equalsIgnoreCase("success")) {
                        if (!response.getJSONObject("product_review").getJSONObject("product_review").getString("review_count").equalsIgnoreCase("0")) {
                            JSONObject reviewDetails = response.getJSONObject("product_review").getJSONObject("product_review");
                            productRating.setRating(Float.parseFloat(reviewDetails.getString("average")));
                        }
                        productReviews.setText(getString(R.string.reviews) + " (" + response.getJSONObject("product_review").getJSONObject("product_review").getString("review_count") + ")");
                    }
                    if (productDetails.getString("combined_status").equalsIgnoreCase("0")) {
                        sizeLayout.setVisibility(View.GONE);
                        colourLayout.setVisibility(View.GONE);
                        getProductReviews();
                    } else {
                        getProductSize();
                    }
                } else if (resFor.equalsIgnoreCase("size")) {
                    Gson gson = new Gson();
                    sizeList = null;
                    sizeList = gson.fromJson(response.toString(), SizeList.class);
                    sizeArrayList.clear();
                    sizeArrayList.addAll(sizeList.getSizeArrayList());
                    mAdapter = new SizeListAdapter(sizeArrayList, this);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
                    recyclerViewSize.setLayoutManager(layoutManager);
                    recyclerViewSize.setAdapter(mAdapter);
                    sizeID = sizeArrayList.get(0).getid();
                    stockCount = Integer.parseInt(sizeArrayList.get(0).getstocks_left());
                    getProductColour();
                } else if (resFor.equalsIgnoreCase("colour")) {
                    Gson gson = new Gson();
                    colourList = null;
                    colourList = gson.fromJson(response.toString(), ColourList.class);
                    colourArrayList.clear();
                    colourArrayList.addAll(colourList.getColourArrayList());
                    ColourListAdapter adapter = new ColourListAdapter(colourArrayList, this);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
                    recyclerViewColour.setLayoutManager(layoutManager);
                    recyclerViewColour.setAdapter(adapter);
                    if (offStatus.equalsIgnoreCase("1")) {
                        String offPrice = colourArrayList.get(0).getProd_actual_price();
                        double offer = Double.parseDouble(offPer);
                        double offCal = Double.parseDouble(offPrice);
                        double actualAmt = (offCal / 100.0f) * offer;
                        double amount = (offCal - actualAmt);
                        productPrice.setText("Rs." + amount);
                        currentPrice = Float.parseFloat(String.valueOf(amount));
                    } else {
                        productPrice.setText("Rs." + colourArrayList.get(0).getProd_actual_price());
                        currentPrice = Float.parseFloat(colourArrayList.get(0).getProd_actual_price());
                    }
                    colourID = colourArrayList.get(0).getid();
                    stockCount = Integer.parseInt(colourArrayList.get(0).getstocks_left());
                    calculatePrice(Integer.parseInt(productQuantity.getText().toString()));
                    getProductReviews();
                } else if (resFor.equalsIgnoreCase("review")) {
                    Gson gson = new Gson();
                    reviewList = null;
                    reviewList = gson.fromJson(response.toString(), ReviewList.class);
                    reviewArrayList.clear();
                    reviewArrayList.addAll(reviewList.getReviewArrayList());
                    ReviewListAdapter adapter = new ReviewListAdapter(reviewArrayList, this);
                    RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
                    recyclerViewReview.setLayoutManager(layoutManager);
                    recyclerViewReview.setAdapter(adapter);
                } else if (resFor.equalsIgnoreCase("addCart")) {
                    Intent intent = new Intent(this, CartActivity.class);
                    startActivity(intent);
                } else if (resFor.equalsIgnoreCase("addWish")) {
                    likeClick = true;
                    imgLike.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_heart_filled));
                } else if (resFor.equalsIgnoreCase("removeWish")) {
                    likeClick = false;
                    imgLike.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_heart));
                }
                if (resFor.equalsIgnoreCase("check")) {
                    if (response.getString("msg").equalsIgnoreCase("Reviews found")) {
                        newReview = false;
                        JSONObject data = response.getJSONObject("product_review");
                        reviewID = data.getString("id");
                        String rating = data.getString("rating");
                        String comment = data.getString("comment");
                        rtbComments.setRating(Integer.getInteger(rating));
                        edtComments.setText(comment);
                    } else {
                        newReview = true;
                    }
                }
                if (resFor.equalsIgnoreCase("sumbit")) {
                    layoutGone();
                }
                if (resFor.equalsIgnoreCase("update")) {
                    layoutGone();
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
    public void onAlertPositiveClicked(int tag) {

    }

    @Override
    public void onAlertNegativeClicked(int tag) {

    }

    private void getDashboardServices() {
        resFor = "detail";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(OSAConstants.KEY_USER_ID, PreferenceStorage.getUserId(this));
            jsonObject.put(OSAConstants.PARAMS_PROD_ID, productID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

//        progressDialogHelper.showProgressDialog(getString(R.string.progress_loading));
        String url = OSAConstants.BUILD_URL + OSAConstants.PRODUCT_DETAIL;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    private void getProductSize() {
        resFor = "size";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(OSAConstants.PARAMS_PROD_ID, productID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = OSAConstants.BUILD_URL + OSAConstants.GET_PRODUCT_SIZE;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    private void getProductColour() {
        resFor = "colour";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(OSAConstants.PARAMS_PROD_ID, productID);
            jsonObject.put(OSAConstants.PARAMS_SIZE_ID, sizeID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = OSAConstants.BUILD_URL + OSAConstants.GET_PRODUCT_COLOUR;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    private void getProductReviews() {
        resFor = "review";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(OSAConstants.PARAMS_PROD_ID, productID);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = OSAConstants.BUILD_URL + OSAConstants.GET_PRODUCT_REVIEWS;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    private void addToCart() {
        resFor = "addCart";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(OSAConstants.PARAMS_PROD_ID, productID);
            jsonObject.put(OSAConstants.PARAMS_COMBINED_ID, colourID);
            jsonObject.put(OSAConstants.PARAMS_QUANTITY, productQuantity.getText().toString());
            jsonObject.put(OSAConstants.KEY_USER_ID, PreferenceStorage.getUserId(this));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = OSAConstants.BUILD_URL + OSAConstants.ADD_TO_CART;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    private void addToWishlist() {
        resFor = "addWish";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(OSAConstants.PARAMS_PROD_ID, productID);
            jsonObject.put(OSAConstants.KEY_USER_ID, PreferenceStorage.getUserId(this));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = OSAConstants.BUILD_URL + OSAConstants.ADD_TO_WISHLIST;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    private void removeWishlist() {
        resFor = "removeWish";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(OSAConstants.PARAMS_PROD_ID, productID);
            jsonObject.put(OSAConstants.KEY_USER_ID, PreferenceStorage.getUserId(this));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String url = OSAConstants.BUILD_URL + OSAConstants.DELETE_WISHLIST;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    private void checkReview() {
        resFor = "check";
        JSONObject jsonObject = new JSONObject();
        String id = PreferenceStorage.getUserId(this);
        String pid = PreferenceStorage.getProductId(this);
        try {
            jsonObject.put(OSAConstants.KEY_USER_ID, id);
            jsonObject.put(OSAConstants.PARAMS_PROD_ID, pid);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = OSAConstants.BUILD_URL + OSAConstants.CHECK_REVIEWS;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    private void submitReview() {
        resFor = "sumbit";
        JSONObject jsonObject = new JSONObject();
        String id = PreferenceStorage.getUserId(this);
        String oid = PreferenceStorage.getOrderId(this);
        try {
            jsonObject.put(OSAConstants.KEY_USER_ID, id);
            jsonObject.put(OSAConstants.PARAMS_PROD_ID, oid);
            jsonObject.put(OSAConstants.KEY_COMMENT, edtComments.getText().toString());
            jsonObject.put(OSAConstants.KEY_RATING, "" + Integer.getInteger(String.valueOf(rtbComments.getRating())));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = OSAConstants.BUILD_URL + OSAConstants.SUBMIT_REVIEW;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    private void updateReview() {
        resFor = "update";
        JSONObject jsonObject = new JSONObject();
        String id = PreferenceStorage.getUserId(this);
        String oid = PreferenceStorage.getProductId(this);
        try {
            jsonObject.put(OSAConstants.KEY_USER_ID, id);
            jsonObject.put(OSAConstants.PARAMS_PROD_ID, oid);
            jsonObject.put(OSAConstants.KEY_REVIEW_ID, reviewID);
            jsonObject.put(OSAConstants.KEY_COMMENT, edtComments.getText().toString());
            jsonObject.put(OSAConstants.KEY_RATING, "" + rtbComments.getRating());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String url = OSAConstants.BUILD_URL + OSAConstants.UPDATE_REVIEW;
        serviceHelper.makeGetServiceCall(jsonObject.toString(), url);
    }

    @Override
    public void onItemClickSize(View view, int position) {
        Size size = null;
        size = sizeArrayList.get(position);
        SizeListAdapter.selected_item = position;
        recyclerViewSize.getAdapter().notifyDataSetChanged();
        sizeID = size.getid();
        getProductColour();
    }

    @Override
    public void onItemClickColour(View view, int position) {
        Colour colour = null;
        colour = colourArrayList.get(position);
        ColourListAdapter.selected_item = position;
        recyclerViewColour.getAdapter().notifyDataSetChanged();
        if (offStatus.equalsIgnoreCase("1")) {
            String offPrice = colour.getProd_actual_price();
            double offer = Double.parseDouble(offPer);
            double offCal = Double.parseDouble(offPrice);
            double actualAmt = (offCal / 100.0f) * offer;
            double amount = (offCal - actualAmt);
            productPrice.setText("Rs." + amount);
            currentPrice = Float.parseFloat(String.valueOf(amount));
        }
        else {
            productPrice.setText("Rs." + colour.getProd_actual_price());
            currentPrice = Float.parseFloat(colour.getProd_actual_price());
        }
        colourID = colour.getid();
        calculatePrice(Integer.parseInt(productQuantity.getText().toString()));
    }

    private boolean checkValueMinus() {
        return (Integer.parseInt(productQuantity.getText().toString()) > 1) && (Integer.parseInt(productQuantity.getText().toString()) <= 10);
    }

    private boolean checkValuePlus() {
        return (Integer.parseInt(productQuantity.getText().toString()) >= 1) && (Integer.parseInt(productQuantity.getText().toString()) < 10);
    }

    private void calculatePrice(int val) {
        float finalPrice = currentPrice * val;
        totalPrice.setText(String.valueOf(finalPrice));
    }

    @Override
    public void onClick(View view) {
        if (view == btnMinus) {
            if (checkValueMinus()) {
                int currentVal = Integer.parseInt(productQuantity.getText().toString());
                currentVal--;
                productQuantity.setText(String.valueOf(currentVal));
                calculatePrice(currentVal);
                if (currentVal <= stockCount) {
                    productStockStatus.setText(getString(R.string.in_stock));
                    productStockStatus.setTextColor(ContextCompat.getColor(this, R.color.in_stock));
                }
            }
        }
        if (view == btnPlus) {
            if (checkValuePlus()) {
                int currentVal = Integer.parseInt(productQuantity.getText().toString());
                currentVal++;
                productQuantity.setText(String.valueOf(currentVal));
                calculatePrice(currentVal);
                if (currentVal > stockCount) {
                    productStockStatus.setText(getString(R.string.out_stock));
                    productStockStatus.setTextColor(ContextCompat.getColor(this, R.color.out_of_stock));
                }
            }
        }
        if (view == addCart) {
            if (PreferenceStorage.getUserId(this).isEmpty()) {
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
                }
            } else {
                addToCart();
            }
        }
        if (view == imgLike) {
            if (PreferenceStorage.getUserId(this).isEmpty()) {
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
                }
            } else {
                if (!likeClick) {
                    addToWishlist();
                } else {
                    removeWishlist();
                }
            }
        }
        if (view == submitBtn) {
            if (newReview) {
                submitReview();
            } else {
                updateReview();
            }
        }
    }

    public void layoutVisible() {
        checkReview();
        orderPlacedLayout.setVisibility(View.VISIBLE);
        originalLayout.setClickable(false);
        originalLayout.setFocusable(false);
    }

    public boolean checkLogin() {
        if (PreferenceStorage.getUserId(this).equalsIgnoreCase("") || PreferenceStorage.getUserId(this).isEmpty()) {
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
            return false;
        } else {
            return true;
        }
    }

    public void layoutGone() {
        orderPlacedLayout.setVisibility(View.GONE);
        originalLayout.setClickable(true);
        originalLayout.setFocusable(true);
    }

    private void doLogout() {
        SharedPreferences sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);
        sharedPreferences.edit().clear().apply();
//        TwitterUtil.getInstance().resetTwitterRequestToken();

        Intent homeIntent = new Intent(this, LoginActivity.class);
        homeIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        homeIntent.putExtra("page", "product");
        homeIntent.putExtra("productObj", productID);
        startActivity(homeIntent);
        finish();
    }

    @Override
    public void onItemClickReview(View view, int position) {

    }

    @Override
    public void onItemClickBestSelling(View view, int position) {

    }
}
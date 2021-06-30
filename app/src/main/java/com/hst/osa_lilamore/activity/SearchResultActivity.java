package com.hst.osa_lilamore.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hst.osa_lilamore.R;
import com.hst.osa_lilamore.adapter.BestSellingListAdapter;
import com.hst.osa_lilamore.bean.support.Product;
import com.hst.osa_lilamore.interfaces.DialogClickListener;

import java.util.ArrayList;

public class SearchResultActivity extends AppCompatActivity implements BestSellingListAdapter.OnItemClickListener, DialogClickListener {

    RecyclerView searchResult;
    private ArrayList<Product> productArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        Toolbar toolbar = (Toolbar) findViewById(R.id.activity_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_left_arrow));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        searchResult = (RecyclerView)findViewById(R.id.search_result);
        searchResult.setHasFixedSize(true);

        productArrayList = new ArrayList<>();

        Intent getInt = getIntent();
        Bundle bundle = getInt.getExtras();

        if (bundle != null){
            productArrayList = (ArrayList<Product>)bundle.getSerializable("searchObj");
            BestSellingListAdapter bsAdapter = new BestSellingListAdapter(this, productArrayList, this);
            GridLayoutManager mLayoutManager = new GridLayoutManager(this, 4);
            mLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    if (bsAdapter.getItemViewType(position) > 0) {
                        return bsAdapter.getItemViewType(position);
                    } else {
                        return 1;
                    }
                    //return 2;
                }
            });
            searchResult.setLayoutManager(mLayoutManager);
            searchResult.setAdapter(bsAdapter);
            searchResult.invalidate();
//            productArrayList.clear();
//            bsAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemClickBestSelling(View view, int position) {

    }

    @Override
    public void onAlertPositiveClicked(int tag) {

    }

    @Override
    public void onAlertNegativeClicked(int tag) {

    }
}
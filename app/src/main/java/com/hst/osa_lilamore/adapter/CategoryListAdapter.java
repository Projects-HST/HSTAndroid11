package com.hst.osa_lilamore.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.hst.osa_lilamore.R;
import com.hst.osa_lilamore.bean.support.Category;
import com.hst.osa_lilamore.utils.OSAValidator;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CategoryListAdapter extends RecyclerView.Adapter<CategoryListAdapter.MyViewHolder> {

    private ArrayList<Category> categoryArrayList;
    Context context;
    private OnItemClickListener onItemClickListener;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView txtCategoryName;
        private ImageView categoryImage, CategoryStatus;
        public CardView categoryLay;
        public MyViewHolder(View view) {
            super(view);
            categoryLay = (CardView) view.findViewById(R.id.category_layout);
            categoryLay.setOnClickListener(this);
            categoryImage = (ImageView) view.findViewById(R.id.category_image);
            txtCategoryName = (TextView) view.findViewById(R.id.txt_category_name);
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClickCategory(v, getAdapterPosition());
            }
//            else {
//                onClickListener.onClick(Selecttick);
//            }
        }
    }


    public CategoryListAdapter(ArrayList<Category> CategoryArrayList, OnItemClickListener onItemClickListener) {
        this.categoryArrayList = CategoryArrayList;
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        public void onItemClickCategory(View view, int position);
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_category, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Category category = categoryArrayList.get(position);
        holder.txtCategoryName.setText(category.getcategory_name());

        if (OSAValidator.checkNullString(category.getcategory_image())) {
            Picasso.get().load(category.getcategory_image()).into(holder.categoryImage);
        } else {
            holder.categoryImage.setImageResource(R.drawable.ic_profile);
        }

    }

    @Override
    public int getItemCount() {
        return categoryArrayList.size();
    }

    @Override
    public int getItemViewType(int position) {
        /*if ((position + 1) % 7 == 4 || (position + 1) % 7 == 0) {
            return 2;
        } else {
            return 1;
        }*/
        if (categoryArrayList.get(position) != null || categoryArrayList.get(position).getSize() > 0)
            return 2;
        else
            return 1;
    }

}

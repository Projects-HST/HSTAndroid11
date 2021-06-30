package com.hst.osa_lilamore.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.hst.osa_lilamore.R;
import com.hst.osa_lilamore.bean.support.Review;

import java.util.ArrayList;

public class ReviewListAdapter extends RecyclerView.Adapter<ReviewListAdapter.MyViewHolder> {

    private ArrayList<Review> ReviewArrayList;
    Context context;
    private OnItemClickListener onItemClickListener;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView txtUserName, txtReviewComment;
        public LinearLayout ReviewLayout;
        public RatingBar ReviewRating;
        public MyViewHolder(View view) {
            super(view);
            ReviewLayout = (LinearLayout) view.findViewById(R.id.review_layout);
            ReviewLayout.setOnClickListener(this);
            txtUserName = (TextView) view.findViewById(R.id.user_name);
            txtReviewComment = (TextView) view.findViewById(R.id.product_comment);
            ReviewRating = (RatingBar) view.findViewById(R.id.ratingBar);
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClickReview(v, getAdapterPosition());
            }
//            else {
//                onClickListener.onClick(Selecttick);
//            }
        }
    }


    public ReviewListAdapter(ArrayList<Review> ReviewArrayList, ReviewListAdapter.OnItemClickListener onItemClickListener) {
        this.ReviewArrayList = ReviewArrayList;
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        public void onItemClickReview(View view, int position);
    }


    @Override
    public ReviewListAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_review, parent, false);

        return new ReviewListAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ReviewListAdapter.MyViewHolder holder, int position) {
        Review review = ReviewArrayList.get(position);

        holder.txtUserName.setText(review.getcustomer_name());
        holder.txtReviewComment.setText(review.getcomment());
        holder.ReviewRating.setRating(Float.parseFloat(review.getrating()));

    }

    @Override
    public int getItemCount() {
        return ReviewArrayList.size();
    }
}

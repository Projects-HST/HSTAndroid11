package com.hst.osa_lilamore.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.hst.osa_lilamore.R;
import com.hst.osa_lilamore.bean.support.Question;
import com.hst.osa_lilamore.bean.support.Size;

import java.util.ArrayList;

public class QuestionListAdapter extends RecyclerView.Adapter<QuestionListAdapter.MyViewHolder> {

    private ArrayList<Question> questionArrayList;
    Context context;
    private OnItemClickListener onItemClickListener;
    public static int selected_item = 0;

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView txtSizeName;
        public RadioButton radioButton;
        public LinearLayout sizeLay;

        public MyViewHolder(View view) {
            super(view);
            sizeLay = (LinearLayout) view.findViewById(R.id.product_layout);
            sizeLay.setOnClickListener(this);
            txtSizeName = (TextView) view.findViewById(R.id.question);
            radioButton = (RadioButton) view.findViewById(R.id.rad_check);
            radioButton.setClickable(false);
//                if (!questionArrayList.get(i).getstatus().equalsIgnoreCase("Active")) {
//                    radioButton.setChecked(true);
//                } else {
//                    radioButton.setChecked(false);
//                }
        }

        @Override
        public void onClick(View v) {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClickSize(v, getAdapterPosition());
            }
//            else {
//                onClickListener.onClick(Selecttick);
//            }
        }
    }


    public QuestionListAdapter(ArrayList<Question> questionArrayList, OnItemClickListener onItemClickListener) {
        this.questionArrayList = questionArrayList;
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {
        public void onItemClickSize(View view, int position);
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item_question, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Question question = questionArrayList.get(position);
        holder.txtSizeName.setText(question.getquestion());
        holder.radioButton.setChecked(!question.getstatus().equalsIgnoreCase("Active"));
    }

    @Override
    public int getItemCount() {
        return questionArrayList.size();
    }
}

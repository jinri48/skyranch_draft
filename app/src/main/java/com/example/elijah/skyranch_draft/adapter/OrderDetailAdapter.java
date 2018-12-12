package com.example.elijah.skyranch_draft.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.elijah.skyranch_draft.R;
import com.example.elijah.skyranch_draft.model.OrderDetail;

import java.util.ArrayList;

public class OrderDetailAdapter extends RecyclerView.Adapter<OrderDetailAdapter.ViewHolder> {

    private Context mContext;
    private ArrayList<OrderDetail> mList;

    public OrderDetailAdapter(Context mContext, ArrayList<OrderDetail> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.order_detail_item, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        OrderDetail item =  mList.get(i);
        String status = "Pending";

        switch (item.getStatus()){
            case "S" : status = "Served"; break;
            case "C" : status = "Completed"; break;
            case "I" : status = "Invoiced"; break;
            case "P" : status = "Pending"; break;
            default  : status = ""; break;
        }

        viewHolder.tvDetail_num.setText("Detail No: " +item.getId());
        viewHolder.tvProname.setText("Product: " +item.getProduct().getName());
        viewHolder.tvSRP.setText("SRP: " +item.getProduct().getO_price());
        viewHolder.tvAmount.setText("Amount: " +item.getTotal_amount());
        viewHolder.tvNetAmount.setText("Net Amount: " +item.getNet_amount());
        viewHolder.tvDiscount.setText("Discount: " +item.getDiscount());
        viewHolder.tvQty.setText("Qty: " +item.getQty());
        viewHolder.tvStatus.setText(status);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tvDetail_num, tvProname, tvSRP, tvAmount, tvNetAmount, tvDiscount,
        tvQty, tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDetail_num = itemView.findViewById(R.id.tvOSD_no);
            tvProname    = itemView.findViewById(R.id.tvOSD_Proname);
            tvSRP        = itemView.findViewById(R.id.tvOSD_srp);

            tvAmount    = itemView.findViewById(R.id.tvOSD_total);
            tvDiscount  = itemView.findViewById(R.id.tvOSD_discount);
            tvNetAmount = itemView.findViewById(R.id.tvOSD_net);
            tvQty       = itemView.findViewById(R.id.tvOSD_qty);
            tvStatus    = itemView.findViewById(R.id.tvOSD_status);

        }
    }
}

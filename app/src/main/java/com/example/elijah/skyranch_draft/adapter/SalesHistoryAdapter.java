package com.example.elijah.skyranch_draft.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.elijah.skyranch_draft.CustomerAdapter;
import com.example.elijah.skyranch_draft.OrderHeader;
import com.example.elijah.skyranch_draft.ProductAdapter;
import com.example.elijah.skyranch_draft.R;
import com.example.elijah.skyranch_draft.activity.OrderDetailsActivity;
import com.example.elijah.skyranch_draft.activity.SalesActivity;
import com.example.elijah.skyranch_draft.model.SalesHistory;

import java.util.ArrayList;
import java.util.List;

public class SalesHistoryAdapter extends RecyclerView.Adapter<SalesHistoryAdapter.SalesHistoryViewHolder>{

    public static final String KEY_ORDER_ID = "OSD_ID";

    private static final String TAG = SalesHistoryAdapter.class.getSimpleName();
    private Context mContext;
    private SalesHistory mHistory;
    public SalesHistoryAdapter(Context context, SalesHistory history) {
        mContext = context;
        mHistory = history;
    }

    @NonNull
    @Override
    public SalesHistoryViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.sales_item, viewGroup, false);
        return new SalesHistoryAdapter.SalesHistoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SalesHistoryViewHolder viewHolder, int i) {
        OrderHeader salesOrder = mHistory.getOrders().get(i);
        String status = "Pending";

        switch (salesOrder.getStatus()){
            case "S" : status = "Served"; break;
            case "C" : status = "Completed"; break;
            case "I" : status = "Invoiced"; break;
            default  : status = "Pending"; break;
        }
        viewHolder.so_cust.setText(salesOrder.getCustomer().getName());
        viewHolder.so_no.setText("OS_NO: " +salesOrder.getOr_no());
        viewHolder.so_amount.setText("PHP " +String.format("%,.2f",salesOrder.getTotal_amount()));
        viewHolder.so_status.setText(status);
        viewHolder.so_osdate.setText(salesOrder.getOs_date());
    }


    @Override
    public int getItemCount() {

        Log.d(TAG, "getItemCount: hist order " +mHistory.getOrders());
        if ( mHistory.getOrders() == null ){
            return 0;
        }

        return mHistory.getOrders().size();


    }

    public class SalesHistoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView so_no, so_cust, so_status, so_amount, so_osdate;
        View item;

        public SalesHistoryViewHolder(@NonNull View itemView) {
            super(itemView);

            so_no = itemView.findViewById(R.id.tvSO_no);
            so_cust = itemView.findViewById(R.id.tvSO_customer);
            so_status = itemView.findViewById(R.id.tvSO_status);
            so_amount = itemView.findViewById(R.id.tvSO_total);
            so_osdate = itemView.findViewById(R.id.tvSO_date);
            itemView.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {
            if (v.equals(itemView)){
                long id = mHistory.getOrders().get(getAdapterPosition()).getOr_no();
                Log.d(TAG, "onClick: sales advsa " +id);
                Intent i = new Intent(mContext, OrderDetailsActivity.class);
                i.putExtra(KEY_ORDER_ID, id);
                mContext.startActivity(i);
            }




        }
    }
}

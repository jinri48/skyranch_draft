package com.example.elijah.skyranch_draft.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.elijah.skyranch_draft.Interface.SalesHistoryAdapterListener;
import com.example.elijah.skyranch_draft.OrderHeader;
import com.example.elijah.skyranch_draft.R;
import com.example.elijah.skyranch_draft.activity.OrderDetailsActivity;
import com.example.elijah.skyranch_draft.activity.SalesActivity;
import com.example.elijah.skyranch_draft.model.SalesHistory;
import com.example.elijah.skyranch_draft.utils.AidlUtil;

public class SalesHistoryAdapter extends RecyclerView.Adapter<SalesHistoryAdapter.SalesHistoryViewHolder>{

    public static final String KEY_ORDER_ID = "OSD_ID";
    public static final String KEY_ORDER = "OSH";

    private static final String TAG = SalesHistoryAdapter.class.getSimpleName();
    private Context mContext;
    private SalesHistory mHistory;

    public SalesHistoryAdapterListener onClickListener;
    public SalesHistoryAdapter(Context context, SalesHistory history, SalesHistoryAdapterListener listener) {
        mContext = context;
        mHistory = history;
        onClickListener = listener;
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
        viewHolder.so_cust_no.setText(String.valueOf(salesOrder.getCustomer().getId()));

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

        TextView so_no, so_cust, so_cust_no, so_status, so_amount, so_osdate;

        ImageView viewOr, printOr;

        public SalesHistoryViewHolder(@NonNull View itemView) {
            super(itemView);

            so_no = itemView.findViewById(R.id.tvSO_no);
            so_cust = itemView.findViewById(R.id.tvSO_customer);
            so_status = itemView.findViewById(R.id.tvSO_status);
            so_amount = itemView.findViewById(R.id.tvSO_total);
            so_osdate = itemView.findViewById(R.id.tvSO_date);
            so_cust_no = itemView.findViewById(R.id.tvSO_customer_no);

            viewOr = itemView.findViewById(R.id.so_view);
            printOr = itemView.findViewById(R.id.so_print);
            viewOr.setOnClickListener(this);
            printOr.setOnClickListener(this);
            itemView.setOnClickListener(this);

            /* is printer connected*/
            if (AidlUtil.getInstance().isConnect()){
                printOr.setVisibility(View.VISIBLE);
            }else{
                printOr .setVisibility(View.GONE);
            }


        }

        @Override
        public void onClick(View v) {
            if (v.equals(itemView)){
                long id = mHistory.getOrders().get(getAdapterPosition()).getOr_no();
                Log.d(TAG, "onClick: sales advsa " +id);
                Intent i = new Intent(mContext, OrderDetailsActivity.class);
                i.putExtra(KEY_ORDER_ID, id);
                i.putExtra(KEY_ORDER, mHistory.getOrders().get(getAdapterPosition()));
                mContext.startActivity(i);
            }else if(v.equals(viewOr)){
                onClickListener.viewORDialog(v, getAdapterPosition());
            }else if (v.equals(printOr)){
                onClickListener.printORDialog(v, getAdapterPosition());
            }

        }
    }



}

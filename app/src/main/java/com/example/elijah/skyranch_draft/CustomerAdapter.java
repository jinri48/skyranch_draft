package com.example.elijah.skyranch_draft;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.elijah.skyranch_draft.Interface.SingleClickItemListener;

import java.util.ArrayList;
import java.util.List;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.CustomerViewHolder> {
    private static final String TAG = CustomerAdapter.class.getSimpleName();
    private Context mContext;
    private ArrayList<Customer> customerList;
    public static SingleClickItemListener sClickListener;
    private static int sSelected = -1;

    public CustomerAdapter(Context mContext, ArrayList<Customer> customerList) {
        this.mContext = mContext;
        this.customerList = customerList;
    }

    @NonNull
    @Override
    public CustomerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.cust_item, viewGroup, false);
        return new CustomerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomerViewHolder customerViewHolder, int i) {

        Customer customer = customerList.get(i);
        customerViewHolder.mCustName.setText(customer.getName());
        customerViewHolder.mCustBday .setText(customer.getBday());
        customerViewHolder. mCustMobile.setText(customer.getMobile());
        customerViewHolder.mCustEmail.setText(customer.getEmail());

        if (sSelected == i){
            customerViewHolder.selector.setChecked(true);
        }else{
            customerViewHolder.selector.setChecked(false);
        }

    }

    @Override
    public int getItemCount() {
        return customerList.size();
    }

    public void selectedItem() {
        notifyDataSetChanged();
    }

    public void setSelectedItem(int position){
        sSelected = position;
    }


    public void setOnItemClickListener(SingleClickItemListener clickListener) {
        sClickListener = clickListener;
    }

    public class CustomerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView mCustName, mCustMobile, mCustEmail, mCustBday;
        public RadioButton selector;

        public CustomerViewHolder(@NonNull View itemView) {
            super(itemView);

            mCustName   = itemView.findViewById(R.id.tvCust_Name);
            mCustMobile = itemView.findViewById(R.id.tvCust_mobile);
            mCustBday   = itemView.findViewById(R.id.tvCust_bday);
            mCustEmail  = itemView.findViewById(R.id.tvCust_email);
            selector = itemView.findViewById(R.id.single_list_item_check_button);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            sSelected = getAdapterPosition();
            sClickListener.onItemClickListener(getAdapterPosition(), v);
        }
    }
}

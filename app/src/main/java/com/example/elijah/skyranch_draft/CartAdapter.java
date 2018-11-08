package com.example.elijah.skyranch_draft;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder>{

    private static final String TAG = CartAdapter.class.getSimpleName();
    private Context mContext;
    private ArrayList<OrderItem> orderItemList;
    private DatabaseHelper mDBHelper;
    private OnItemClickListener mItemClickListener;

    interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    public CartAdapter(Context mContext, ArrayList<OrderItem> orderItemList) {
        this.mContext = mContext;
        this.orderItemList = orderItemList;
        mDBHelper = DatabaseHelper.newInstance(mContext);
    }

    @Override
    public CartAdapter.CartViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.cart_item, viewGroup, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CartAdapter.CartViewHolder cartViewHolder, int i) {
       final OrderItem currentItem = orderItemList.get(i);
       long pro_id = currentItem.getProduct().getId();
       Log.d(TAG, "onBindViewHolder: " +currentItem);
       cartViewHolder.mProId.setText("Pro id: " +pro_id);
       cartViewHolder.mProName.setText(currentItem.getProduct().getName());
       cartViewHolder.mProQty.setText("Qty: " + currentItem.getQty());
       cartViewHolder.mProTotalAmount.setText("Amount: " + String.format ("%,.2f", currentItem.getAmount()));
       Picasso.get().load(currentItem.getProduct().getImgUrl()).fit().centerCrop().into(cartViewHolder.mProImg);

    }

    @Override
    public int getItemCount() {
        return orderItemList.size();
    }


    public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView mProQty, mProName, mProTotalAmount, mProId;
        public ImageView mImgRemove, mProImg;

        private CartViewHolder(View itemView) {
            super(itemView);
            mProName = itemView.findViewById(R.id.tvCartProName);
            mProQty = itemView.findViewById(R.id.tvCartProQty);
            mProId = itemView.findViewById(R.id.tvCartProId);
            mProImg = itemView.findViewById(R.id.cart_img);
            mProTotalAmount = itemView.findViewById(R.id.tvCartProPriceTotal);
            mImgRemove = itemView.findViewById(R.id.bRemoveCartItem);
            mImgRemove.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(v.equals(mImgRemove)){

                removeAt(getAdapterPosition());
            }else if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }


    public void removeAt(int position) {
        mDBHelper.deleteItem(orderItemList.get(position));
        orderItemList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, orderItemList.size());

    }

    public double getTotalItems(ArrayList<OrderItem> items){
        double totalAmount = 0;
        for (OrderItem item : items ){
            totalAmount += item.getAmount();
        }
        return totalAmount;

    }
}

package com.example.elijah.skyranch_draft;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

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
        View view = LayoutInflater.from(mContext).inflate(R.layout.cart_item2, viewGroup, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CartAdapter.CartViewHolder cartViewHolder, int i) {
        final OrderItem currentItem = orderItemList.get(i);
        long pro_id = currentItem.getProduct().getId();
        Log.d(TAG, "onBindViewHolder: " + currentItem);

//        cartViewHolder.mProId.setText("Pro id: " +pro_id);
//        cartViewHolder.mProName.setText(currentItem.getProduct().getName());
//        cartViewHolder.mProQty.setText("Qty: " + currentItem.getQty());
//        cartViewHolder.mProTotalAmount.setText("Amount: " + String.format ("%,.2f", currentItem.getAmount()));
//        Picasso.get().load(currentItem.getProduct().getImgUrl()).error(R.drawable.ic_shopping_cart_primary_24dp).fit().centerCrop().into(cartViewHolder.mProImg);

        cartViewHolder.mProName.setText(currentItem.getProduct().getName());
        cartViewHolder.mProQty.setText("" + currentItem.getQty());
        cartViewHolder.mProTotalAmount.setText("Amount: " + String.format("%,.2f", currentItem.getAmount()));
        Picasso.get().load(currentItem.getProduct().getImgUrl()).error(R.drawable.hat).fit().centerCrop().into(cartViewHolder.mProImg);

    }

    @Override
    public int getItemCount() {
        return orderItemList.size();
    }


    public class CartViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        //        public TextView mProQty, mProName, mProTotalAmount, mProId;
        public EditText mProQty;
        public ImageView mImgRemove, mProImg;
        public ImageView mProQtyAdd, mProQtySub;
        public TextView mProName, mProTotalAmount;


        private CartViewHolder(View itemView) {
            super(itemView);
//            mProName = itemView.findViewById(R.id.tvCartProName);
//            mProQty = itemView.findViewById(R.id.tvCartProQty);
//            mProId = itemView.findViewById(R.id.tvCartProId);

//            mProImg = itemView.findViewById(R.id.ivCartPro_Img);
//            mProTotalAmount = itemView.findViewById(R.id.tvCartProPriceTotal);
//            mImgRemove = itemView.findViewById(R.id.bRemoveCartItem);
//            mImgRemove.setOnClickListener(this);


            mProName = itemView.findViewById(R.id.tvCartProName);
            mProQty = itemView.findViewById(R.id.etQty);
            mProImg = itemView.findViewById(R.id.ivCartPro_Img);
            mProTotalAmount = itemView.findViewById(R.id.tvCartProPriceTotal);
            mImgRemove = itemView.findViewById(R.id.bRemoveCartItem);
            mProQtyAdd = itemView.findViewById(R.id.ivPro_add_qty);
            mProQtySub = itemView.findViewById(R.id.ivPro_sub_qty);
            mImgRemove.setOnClickListener(this);
            mProQtyAdd.setOnClickListener(this);
            mProQtySub.setOnClickListener(this);

            mProQty.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                @Override
                public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                    if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_GO || event.getAction() == KeyEvent.ACTION_DOWN){
                        if (v.getText().toString().trim().isEmpty()){
                            // todo: create snack bar
                           Snackbar.make(Cart.layout, "Please fill up a valid quantity", Snackbar.LENGTH_SHORT).show();
                           return false;
                        }
                        long qty = Long.parseLong(v.getText().toString().trim());
                        if (qty < 1){
                            qty = 1;
                            v.setText(String.valueOf(qty));
                        }
                        OrderItem item = orderItemList.get(getAdapterPosition());
                        item.setQty(qty);
                        double price = item.getQty() * item.getProduct().getO_price();
                        item.setAmount(price);
                        updateItem(item, getAdapterPosition());
                        return true;
                    }
                    return false;
                }
            });

        }

        @Override
        public void onClick(View v) {
            if (v.equals(mImgRemove)) {
                removeAt(getAdapterPosition());
            } else if (mItemClickListener != null) {
                mItemClickListener.onItemClick(v, getAdapterPosition());
            } else if (v.equals(mProQtyAdd)) {
                OrderItem item = orderItemList.get(getAdapterPosition());
                long prev_qty = item.getQty();
                item.setQty(prev_qty + 1);
                double price = item.getQty() * item.getProduct().getO_price();
                item.setAmount(price);

                updateItem(item, getAdapterPosition());

            } else if (v.equals(mProQtySub)) {
                OrderItem item = orderItemList.get(getAdapterPosition());
                long prev_qty = item.getQty();
                prev_qty -= 1;
                if (prev_qty < 1){
                    prev_qty = 1;
                }
                item.setQty(prev_qty);
                double price = item.getQty() * item.getProduct().getO_price();
                item.setAmount(price);
                updateItem(item, getAdapterPosition());
            }
        }
    }

    public void updateItem(OrderItem item, int position){
        long result = mDBHelper.updateCartItemQty(item);
        if (result > 0) {
            notifyItemChanged(position);
            Cart.tvCartPriceTotal.setText("Total: P" + String.format("%,.2f", getTotalItems(orderItemList)));
        } else {
            Toast.makeText(mContext, "Cannot update product. Please try again", Toast.LENGTH_SHORT).show();
        }
        Log.d(TAG, "onClick: recycler update" + result);
    }
    public void removeAt(int position) {
        int deletedRow = mDBHelper.deleteItem(orderItemList.get(position));
        if (deletedRow == 1) {
            orderItemList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, orderItemList.size());
            Cart.tvCartPriceTotal.setText("Total: P" + String.format("%,.2f", getTotalItems(orderItemList)));
        } else {
            Toast.makeText(mContext, "Error in deleting a record. Try again", Toast.LENGTH_SHORT).show();
        }

    }

    public double getTotalItems(ArrayList<OrderItem> items) {
        double totalAmount = 0;
        for (OrderItem item : items) {
            totalAmount += item.getAmount();
        }
        return totalAmount;

    }
}


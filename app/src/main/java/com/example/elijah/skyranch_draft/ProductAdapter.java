package com.example.elijah.skyranch_draft;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.elijah.skyranch_draft.activity.CustomerActivity;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder>  {

        private static final String TAG = ProductAdapter.class.getSimpleName();
        private Context mContext;
        private ArrayList<Product> productList;
        private ArrayList<Product> itemsIncart;
        private DatabaseHelper mDBHelper;


    public interface RVItemClickListener {
            public void onClick(View view, int position);
        }

        public ProductAdapter(Context mContext, ArrayList<Product> listItems) {
            this.mContext = mContext;
            this.productList = listItems;
            mDBHelper = DatabaseHelper.newInstance(mContext);
        }

        @Override
        public ProductAdapter.ProductViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.product_item, viewGroup, false);
            return new ProductViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ProductViewHolder productViewHolder, int i) {
            final Product currentItem = productList.get(i);

            String pro_imgUrl = currentItem.getImgUrl();
            String pro_name = currentItem.getName();
            Double pro_price = currentItem.getO_price();
            productViewHolder.mProName.setText(pro_name);

            Picasso.get().load(pro_imgUrl).error(R.drawable.pro_img_placeholder).fit().centerCrop().into(productViewHolder.mProImg);
            productViewHolder.mRetailPrice.setText(String.format("%,.2f", pro_price));


            // go to product detail page
            productViewHolder.mProImg.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    Intent intent = new Intent(mContext, ProductDetails.class);
                    intent.putExtra("parcel_data", currentItem);
                    mContext.startActivity(intent);
                    return true;
                }
            });


            // add up the qty decalared in the cart qty edittext for each click
            productViewHolder.mAddQty.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int qty = 0;
                    if (!productViewHolder.mQty.getText().toString().trim().isEmpty()){
                        qty = Integer.parseInt(productViewHolder.mQty.getText().toString());
                    }
                    qty += 1;

                    productViewHolder.mQty.setText(String.valueOf(qty));

                }
            });

            // add up the qty decalared in the cart qty edittext for each click
            productViewHolder.mSubQty.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int qty = 0;
                    if (!productViewHolder.mQty.getText().toString().trim().isEmpty()){
                        qty = Integer.parseInt(productViewHolder.mQty.getText().toString());
                    }

                    qty -= 1;
                    if (qty < 1){
                        qty = 1;
                    }
                    productViewHolder.mQty.setText(String.valueOf(qty));

                }
            });

        }

        @Override
        public int getItemCount() {
            return productList.size();
        }

        public class ProductViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            public View mItemView;
            public ImageView mProImg;
            public TextView mProName;
            public Button mAddToCart;
            public TextView mRetailPrice;

            // for adding qty
            public EditText mQty;
            public ImageView mAddQty;
            public ImageView mSubQty;


            public ProductViewHolder(View itemView) {
                super(itemView);
                mItemView = itemView;
                mProImg = itemView.findViewById(R.id.ivProduct);
                mProName = itemView.findViewById(R.id.tvName);
                mRetailPrice = itemView.findViewById(R.id.tvPrice);

                mQty = itemView.findViewById(R.id.etQty);
                mAddQty = itemView.findViewById(R.id.ivQtyAdd);
                mSubQty = itemView.findViewById(R.id.ivQtySub);

                mAddToCart = itemView.findViewById(R.id.bAddToCart);
                mAddToCart.setOnClickListener(this);
                mAddQty.setOnClickListener(this);
                mSubQty.setOnClickListener(this);

            }

            @Override
            public void onClick(View v) {
                if (v.getId() == mAddToCart.getId()){
                    Product item =  productList.get(getAdapterPosition());
                    if (mQty.getText().toString().trim().equals("")){
                        Toast.makeText(mContext, "Please set a quantity", Toast.LENGTH_SHORT).show();
                        return;
                    }else if (Long.parseLong(mQty.getText().toString().trim()) <= 0 ){
                        Toast.makeText(mContext, "Can't add to cart when item is 0 or less", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    long qty =  Long.parseLong(mQty.getText().toString().trim());
                    double price = item.getO_price() * qty;
                    OrderItem order = new OrderItem();
                    order.setQty(Long.parseLong(mQty.getText().toString()));
                    order.setAmount(price);
                    order.setProduct(item);

                    Log.d(TAG, "onClick: " +item);
                    Log.d(TAG, "onClick: order " +order);
//                    mDBHelper.addToCart(order);
                    mDBHelper.addToCart(order);
                }

            }
        }
}

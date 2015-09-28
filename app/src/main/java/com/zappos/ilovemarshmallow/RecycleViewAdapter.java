package com.zappos.ilovemarshmallow;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * Provide views to RecyclerView with data from mDataSet.
 */
public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ViewHolder> {

    private static final String LOG_TAG = RecycleViewAdapter.class.getSimpleName();

    private SingleItem[] mDataSet;
    private Context mContext;

    /**
     * Provide a reference to the type of views that you are using (custom ViewHolder)
     */
    public class ViewHolder extends RecyclerView.ViewHolder {
        //private final TextView imageUrl;
        private final TextView productName;
        private final TextView price;
        private final TextView brandName;
        private final RatingBar productRating;
        private final ImageView productImage;

        public ViewHolder(View v) {
            super(v);
            // Define click listener for the ViewHolder's View.
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDetailActivity(getPosition());
                }
            });
            productName = (TextView) v.findViewById(R.id.product_name);
            price = (TextView) v.findViewById(R.id.price);
            brandName = (TextView) v.findViewById(R.id.brand_name);
            productRating = (RatingBar) v.findViewById(R.id.product_rating);
            productImage = (ImageView) v.findViewById(R.id.product_image);
        }

        public TextView getProductName() {
            return productName;
        }

        public TextView getPrice() {
            return price;
        }

        public TextView getBrandName() {
            return brandName;
        }

        public RatingBar getProductRating() {
            return productRating;
        }

        public ImageView getProductImage() {
            return productImage;
        }
    }

    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used by RecyclerView.
     */
    public RecycleViewAdapter(SingleItem[] dataSet, Context context) {
        mDataSet = dataSet;
        mContext = context;
    }

    private int mWidth;

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view.
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_item_base, viewGroup, false);

        return new ViewHolder(v);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        float productRating = mDataSet[position].getProductRating();

        // Get element from your dataset at this position and replace the contents of the view
        // with that element
        viewHolder.getProductName().setText(mDataSet[position].getProductName());
        viewHolder.getPrice().setText(mDataSet[position].getPrice());
        viewHolder.getBrandName().setText(mDataSet[position].getBrandName());
        if (productRating > 0) {
            viewHolder.getProductRating().setRating(productRating);
        } else {
            viewHolder.getProductRating().setVisibility(View.INVISIBLE);
        }
        Picasso.with(mContext).load(mDataSet[position].getImageUrl()).into(viewHolder.getProductImage());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataSet.length;
    }

    private void showDetailActivity(int position) {
        String asin = mDataSet[position].getAsin();
        String price = mDataSet[position].getPrice();
        float rating = mDataSet[position].getProductRating();
        Intent intent = new Intent(mContext, DetailActivity.class);

        intent.putExtra(mContext.getResources().getString(R.string.asin), asin);
        intent.putExtra(mContext.getResources().getString(R.string.price), price);
        intent.putExtra(mContext.getResources().getString(R.string.rating), rating);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(intent);
    }
}

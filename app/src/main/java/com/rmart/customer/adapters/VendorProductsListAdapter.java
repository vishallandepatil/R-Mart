package com.rmart.customer.adapters;

import android.content.Context;
import android.graphics.Paint;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.rmart.R;
import com.rmart.RMartApplication;
import com.rmart.baseclass.CallBackInterface;
import com.rmart.baseclass.Constants;
import com.rmart.customer.models.ContentModel;
import com.rmart.customer.models.CustomerProductDetailsModel;
import com.rmart.customer.models.CustomerProductsDetailsUnitModel;
import com.rmart.customer.models.VendorProductDetailsResponse;
import com.rmart.utilits.HttpsTrustManager;
import com.rmart.utilits.custom_views.SpacesItemDecoration;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Satya Seshu on 10/09/20.
 */
public class VendorProductsListAdapter extends RecyclerView.Adapter<VendorProductsListAdapter.ItemsViewHolder> implements Filterable {

    private LayoutInflater layoutInflater;
    private List<CustomerProductDetailsModel> filteredListData;
    private MyFilter myFilter;

    private List<CustomerProductDetailsModel> productsList;
    private ImageLoader imageLoader;

    public VendorProductsListAdapter(Context context, List<CustomerProductDetailsModel> productsList) {
        layoutInflater = LayoutInflater.from(context);
        this.productsList = productsList;
        filteredListData = new ArrayList<>();
        this.filteredListData.addAll(productsList);
        imageLoader = RMartApplication.getInstance().getImageLoader();
    }

    public void updateItems(List<CustomerProductDetailsModel> listData) {
        this.productsList = listData;
        this.filteredListData.clear();
        this.filteredListData.addAll(productsList);
    }

    @NonNull
    @Override
    public ItemsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View lItemView = layoutInflater.inflate(R.layout.vendor_product_types_list_items, parent, false);
        return new ItemsViewHolder(lItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemsViewHolder holder, int position) {
        CustomerProductDetailsModel dataObject = filteredListData.get(position);
        String productImageUrl = dataObject.getProductImage();
        if (!TextUtils.isEmpty(productImageUrl)) {
            HttpsTrustManager.allowAllSSL();
            imageLoader.get(productImageUrl, ImageLoader.getImageListener(holder.ivProductImageField,
                    R.mipmap.ic_launcher, android.R.drawable
                            .ic_dialog_alert));
            holder.ivProductImageField.setImageUrl(productImageUrl, imageLoader);
        }
        holder.tvProductNameField.setText(dataObject.getProductName());

        List<CustomerProductsDetailsUnitModel>  unitsList = dataObject.getUnits();
        if(unitsList != null && !unitsList.isEmpty()) {
            CustomerProductsDetailsUnitModel unitModelDetails = unitsList.get(0);
            String quantityDetails = String.format("%s %s", unitModelDetails.getUnitNumber(), unitModelDetails.getShortUnitMeasure());
            holder.tvQuantityField.setText(quantityDetails);
            String sellingPrice = String.format("Rs.%s", unitModelDetails.getSellingPrice());
            holder.tvSellingPriceField.setText(sellingPrice);

            holder.tvTotalPriceField.setText(unitModelDetails.getUnitPrice());
            holder.tvTotalPriceField.setPaintFlags(holder.tvTotalPriceField.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            int productDiscount = unitModelDetails.getProductDiscount();
            String productDiscountDetails = productDiscount + "% \n Off";
            holder.tvProductDiscountField.setText(productDiscountDetails);
        }
    }

    @Override
    public int getItemCount() {
        return filteredListData.size();
    }

    @Override
    public Filter getFilter() {
        if (myFilter == null) {
            myFilter = new MyFilter();
        }
        return myFilter;
    }

    private class MyFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if (constraint != null && constraint.length() > 0) {
                filteredListData.clear();
                for (CustomerProductDetailsModel productDetails : productsList) {
                    if (productDetails.getProductName().toLowerCase().contains(constraint.toString().toLowerCase())) {
                        filteredListData.add(productDetails);
                    }
                }
            }
            results.count = filteredListData.size();
            results.values = filteredListData;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredListData = (List<CustomerProductDetailsModel>) results.values;
            notifyDataSetChanged();
        }
    }

    public static class ItemsViewHolder extends RecyclerView.ViewHolder {

        NetworkImageView ivProductImageField;
        TextView tvProductNameField;
        TextView tvProductDiscountField;
        TextView tvQuantityField;
        TextView tvSellingPriceField;
        TextView tvTotalPriceField;

        public ItemsViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProductImageField = itemView.findViewById(R.id.iv_product_image_field);
            tvProductNameField = itemView.findViewById(R.id.tv_product_name_field);
            tvProductDiscountField = itemView.findViewById(R.id.tv_product_discount_field);
            tvQuantityField = itemView.findViewById(R.id.tv_quantity_field);
            tvSellingPriceField = itemView.findViewById(R.id.tv_selling_price_field);
            tvTotalPriceField = itemView.findViewById(R.id.tv_total_price_field);
        }
    }
}
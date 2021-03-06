package com.rmart.customer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rmart.R;
import com.rmart.customer.models.ShoppingCartResponseDetails;

import java.util.List;

/**
 * Created by Satya Seshu on 14/09/20.
 */
public class ShoppingCartAdapter extends RecyclerView.Adapter<ShoppingCartAdapter.ViewHolder> {

    private List<ShoppingCartResponseDetails> listData;
    private LayoutInflater layoutInflater;
    private String productsText;
    private String productText;

    public ShoppingCartAdapter(Context context, List<ShoppingCartResponseDetails> listData) {
        this.listData = listData;
        layoutInflater = LayoutInflater.from(context);
        productsText = context.getString(R.string.products);
        productText = context.getString(R.string.product);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = layoutInflater.inflate(R.layout.shopping_cart_list_items, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ShoppingCartResponseDetails dataObject = listData.get(position);
        holder.tvShopNameField.setText(dataObject.getShopName());
        holder.tvContactNoField.setText(dataObject.getMobileNumber());
        String totalPrice = "Rs." + dataObject.getCartPrice();
        holder.tvTotalPriceField.setText(totalPrice);
        int count = dataObject.getCartCount();
        holder.tvTotalCountField.setText(String.valueOf(count));
        holder.tvProductTextField.setText(count == 1 ? productText : productsText);
    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public void updateItems(List<ShoppingCartResponseDetails> shopWiseCartList) {
        this.listData = shopWiseCartList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvShopNameField;
        TextView tvContactNoField;
        TextView tvTotalPriceField;
        TextView tvTotalCountField;
        TextView tvProductTextField;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvShopNameField = itemView.findViewById(R.id.tv_shop_name_field);
            tvContactNoField = itemView.findViewById(R.id.tv_contact_no_field);
            tvTotalPriceField = itemView.findViewById(R.id.tv_total_price_field);
            tvTotalCountField = itemView.findViewById(R.id.tv_total_products_count_field);
            tvProductTextField = itemView.findViewById(R.id.tv_product_text_field);
        }
    }
}

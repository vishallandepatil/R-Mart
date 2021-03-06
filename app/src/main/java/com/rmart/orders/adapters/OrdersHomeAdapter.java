package com.rmart.orders.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rmart.R;
import com.rmart.orders.models.SelectedOrderGroup;
import com.rmart.orders.views.viewholders.OrdersHomeViewHolder;
import com.rmart.utilits.pojos.orders.StateOfOrders;

import java.util.ArrayList;

public class OrdersHomeAdapter extends RecyclerView.Adapter<OrdersHomeViewHolder> {

    View.OnClickListener onClickListener;
    ArrayList<StateOfOrders> orderByTypes;
    public OrdersHomeAdapter(ArrayList<StateOfOrders> orderByTypes, View.OnClickListener onClickListener) {
        this.orderByTypes = orderByTypes;
        this.onClickListener =onClickListener;
    }
    @NonNull
    @Override
    public OrdersHomeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.item_grid_order_home_layout, parent, false);
        listItem.setOnClickListener(onClickListener);
        return new OrdersHomeViewHolder(listItem) ;
    }

    @Override
    public void onBindViewHolder(@NonNull OrdersHomeViewHolder holder, int position) {
        StateOfOrders selectedOrderGroup = this.orderByTypes.get(position);
        holder.itemView.setTag(selectedOrderGroup);
        holder.orderCount.setText(selectedOrderGroup.getCount());
        holder.orderTitle.setText(selectedOrderGroup.getStatusName());
        holder.itemView.setBackgroundResource(selectedOrderGroup.getBgTop());
    }

    @Override
    public int getItemCount() {
        return orderByTypes.size();
    }
}

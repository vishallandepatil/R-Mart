package com.rmart.orders.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.rmart.R;
import com.rmart.orders.models.OrderListObject;
import com.rmart.orders.views.viewholders.OrdersListItemViewHolder;

import java.util.ArrayList;

public class OrdersListAdapter extends RecyclerView.Adapter<OrdersListItemViewHolder> {

    View.OnClickListener onClickListener;
    ArrayList<OrderListObject> orderList;
    public OrdersListAdapter(ArrayList<OrderListObject> orderList, View.OnClickListener onClickListener) {
        this.orderList = orderList;
        this.onClickListener =onClickListener;
    }
    @NonNull
    @Override
    public OrdersListItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.item_order_list, parent, false);
        listItem.setOnClickListener(onClickListener);
        return new OrdersListItemViewHolder(listItem) ;
    }

    @Override
    public void onBindViewHolder(@NonNull OrdersListItemViewHolder holder, int position) {
        OrderListObject orderListObject = orderList.get(position);
        holder.orderCount.setText(orderListObject.getCount());
        holder.orderID.setText(orderListObject.getOrderID());
        holder.date.setText(orderListObject.getDate());
        holder.itemView.setTag(orderListObject);
    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }
}
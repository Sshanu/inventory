package com.example.shanu.mode;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import java.util.*;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.MyViewHolder> {

    private List<Item> ItemList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, quantity, price;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.itemName);
            quantity = (TextView) view.findViewById(R.id.itemQuantity);
            price = (TextView) view.findViewById(R.id.itemPrice);
        }
    }


    public ItemAdapter(List<Item> ItemList) {
        this.ItemList = ItemList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.billing_item_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Item item = ItemList.get(position);
        holder.name.setText(item.getname());
        holder.quantity.setText(Double.toString(item.getquantity()));
        holder.price.setText(Double.toString(item.getprice()));
    }

    @Override
    public int getItemCount() {
        return ItemList.size();
    }
}

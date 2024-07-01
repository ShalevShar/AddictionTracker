package com.example.common;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RelapseAdapter extends RecyclerView.Adapter<RelapseAdapter.RelapseViewHolder> {

    private final List<RelapseItem> relapseItemList;

    public RelapseAdapter(List<RelapseItem> relapseItemList) {
        this.relapseItemList = relapseItemList;
    }

    @NonNull
    @Override
    public RelapseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.relapse_item, parent, false);
        return new RelapseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RelapseViewHolder holder, int position) {
        RelapseItem item = relapseItemList.get(position);
        holder.date.setText(item.getDate());
        holder.description.setText(item.getDescription());
        holder.dose.setText(item.getDose());
    }

    @Override
    public int getItemCount() {
        return relapseItemList.size();
    }

    public void addItem(RelapseItem item) {
        relapseItemList.add(item);
        notifyItemInserted(relapseItemList.size() - 1);
    }

    public static class RelapseViewHolder extends RecyclerView.ViewHolder {
        TextView date, description, dose;

        public RelapseViewHolder(@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.relapse_item_date);
            description = itemView.findViewById(R.id.relapse_item_description);
            dose = itemView.findViewById(R.id.relapse_item_dose);
        }
    }
}

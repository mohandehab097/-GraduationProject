package com.example.smartparking.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smartparking.R;
import com.example.smartparking.models.ParkingSlots;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SlotAdapter extends ListAdapter<Map<String,String>, SlotAdapter.ViewHolder> {
    public SlotAdapter() {
        super(new SlotDiffer());
    }

    @NonNull
    @Override
    public SlotAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        return new ViewHolder(layoutInflater.inflate(R.layout.item_rv_slots, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull SlotAdapter.ViewHolder holder, int position) {
        List<String> list =new ArrayList<>(getItem(position).keySet());

        holder.slotName.setText(list.get(0));

        List<String> list1 =new ArrayList<>(getItem(position).values());

        if (list1.get(0).equals("on")) {
            holder.slotImage.setImageResource(
                    R.drawable.parking
            );
        }
        else {
            holder.slotImage.setImageResource(
                    R.drawable.no_parking
            );
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView slotName;
        ImageView slotImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            slotName = itemView.findViewById(R.id.tv_name);
            slotImage = itemView.findViewById(R.id.iv_slots);
        }
    }
}

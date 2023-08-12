package com.abdulkareemMashabi.wishapp.AppServices.RecyclerViewServices;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.abdulkareemMashabi.wishapp.R;

public class MyViewHolder extends RecyclerView.ViewHolder {
    TextView date;
    TextView wish;
    ConstraintLayout deleteWish;

    // helper to display the wish and the date of the user's wishes when he create it
    public MyViewHolder(@NonNull View itemView) {
        super(itemView);
        date = itemView.findViewById(R.id.date);
        wish = itemView.findViewById(R.id.wish);
        deleteWish = itemView.findViewById(R.id.delete_button);
    }
}

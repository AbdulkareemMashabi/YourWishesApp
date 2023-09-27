package com.abdulkareemMashabi.wishapp.AppServices.RecyclerViewServices;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.airbnb.lottie.LottieAnimationView;
import com.abdulkareemMashabi.wishapp.AppServices.Services;
import com.abdulkareemMashabi.wishapp.AppServices.Wish;
import com.abdulkareemMashabi.wishapp.R;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {

    Context context;
    List<Wish> items;
    FirebaseFirestore db;
    String userEmail;
    FrameLayout overlay;
    LottieAnimationView overlayAnimation;
    Activity activity;


    // helper to display the wish and the date of the user's wishes when he create it
    public MyAdapter(Context context, List<Wish> items, String userEmail, FrameLayout overlay, LottieAnimationView overlayAnimation, Activity activity) {
        this.context = context;
        this.items = items;
        this.db = FirebaseFirestore.getInstance();
        this.userEmail=userEmail;
        this.overlay=overlay;
        this.overlayAnimation=overlayAnimation;
        this.activity=activity;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // display different component based on app language
        if (Services.getAppLanguage()) {
            return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.read_item_ldrtl,parent,false));
        } else {
            return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.read_item,parent,false));
        }

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Wish item = items.get(holder.getAdapterPosition());
        holder.date.setText(item.getDate());
        holder.wish.setText(item.getWish());
        // delete the wish from firebase when the user click on delete button
        holder.deleteWish.setOnClickListener(view -> {
            overlay.setVisibility(View.VISIBLE);
            overlayAnimation.setVisibility(View.VISIBLE);

            // delete the wish from firebase
            db.document(userEmail + "/" + item.getDocumentId()).delete().addOnSuccessListener(unused -> {
                // remove the item from the list locally
                items.remove(holder.getAdapterPosition());
                // notify recycler view about deleted item
                notifyItemRemoved(holder.getAdapterPosition());
                hideOverLay();
                Services.toastMessages("onSuccess",null,null, "Display Data", null, activity);
            }).addOnFailureListener(e -> {
                Services.toastMessages("onFailure",null,null, "Display Data", e, activity);
                hideOverLay();
            });

        });
        Services.buttonPressed(holder.deleteWish);
    }

    private void hideOverLay()
    {
        overlay.setVisibility(View.INVISIBLE);
        overlayAnimation.setVisibility(View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}

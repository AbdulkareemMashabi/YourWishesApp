package com.abdulkareemMashabi.wishapp.Fragments;

import android.animation.Animator;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.airbnb.lottie.LottieAnimationView;
import com.abdulkareemMashabi.wishapp.AppServices.Services;
import com.abdulkareemMashabi.wishapp.AppServices.ViewModalServices.UserDataViewModal;
import com.abdulkareemMashabi.wishapp.R;

// this fragment is displayed when the app start, to display lottie animation
public class LottieFragment extends Fragment {
    UserDataViewModal viewModal;
    LottieAnimationView loading;

    // this fragment is used to display lottie when the app is started
    public LottieFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_lottie, container, false);

        // declare the loading animation that appear when the app start
        loading = view.findViewById(R.id.loading);
        // declare the view model where we read the user email
        viewModal = new ViewModelProvider(requireActivity()).get(UserDataViewModal.class);

        loading.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animator) {

            }

            @Override
            public void onAnimationEnd(@NonNull Animator animator) {

                // make the bottom tab visible
                requireActivity().findViewById(R.id.constraint_contains_buttons).setVisibility(View.VISIBLE);
                // set the background color of the app
                requireActivity().findViewById(R.id.mainLayout).setBackgroundColor(getResources().getColor(R.color.background_color,null));
                // make language icon visible
                requireActivity().findViewById(R.id.language).setVisibility(View.VISIBLE);

                readUserEmail();

                // replace the fragment with Write fragment
                Services.replacement(new Write(), requireActivity(),"Write");
            }

            @Override
            public void onAnimationCancel(@NonNull Animator animator) {

            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animator) {

            }
        });
        return view;
    }

    // read user email when the app is lunched, to make him signed in if he already signed in
    // if there is an email in local storage, make the user signed in, and if the local storage is empty make him unsigned in
    private void readUserEmail()
    {
        SharedPreferences prefs = requireActivity().getSharedPreferences("wish_app", Context.MODE_PRIVATE);
        String userEmail = prefs.getString("email", "");
        viewModal.setUserEmail(userEmail);
    }
}
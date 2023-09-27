package com.abdulkareemMashabi.wishapp.Fragments;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.abdulkareemMashabi.wishapp.AppServices.RecyclerViewServices.MyAdapter;
import com.abdulkareemMashabi.wishapp.AppServices.Services;
import com.abdulkareemMashabi.wishapp.AppServices.ViewModalServices.UserDataViewModal;
import com.abdulkareemMashabi.wishapp.AppServices.Wish;
import com.abdulkareemMashabi.wishapp.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DisplayData extends Fragment {

    UserDataViewModal viewModal;
    String userEmail;
    FrameLayout overlay;
    LottieAnimationView overlayAnimation;
    FrameLayout frameLayoutPage;
    ConstraintLayout bottomButtons;
    FirebaseFirestore db;
    RecyclerView recyclerView;
    List<Wish> data;
    ConstraintLayout header;
    TextView date;
    TextView wish;
    TextView emptyDataMessage;


    // this fragment is used to display the wishes of the user, it is displayed after the Read fragment always
    public DisplayData() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // to hide bottomTab that has read button and write button
        requireActivity().findViewById(R.id.constraint_contains_buttons).setVisibility(View.INVISIBLE);

        setWeight("onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_display_data, container, false);
        variablesDeclarations(view);
        rotateInArabic();

        //read the data of the user from the firebase by using the email that we saved it in view modal in the Read fragment
        fetchData();

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // hide overlay when it is visible
        hideOverlay();
        setWeight("onDestroy");
    }

    // the purpose of this function is to let the recycler view take the full page, and hide bottomBar by changing the weight of each view inside Linear layout
    private void setWeight(String fragmentSituation){
        // make thw weight of the recycler view = 1 and the bottom tabs = 0
        int frameLayoutWeight = 1;
        int buttonsLayoutWeight =0;
        // return to previous setup when the page is destroy which means that we are going to another fragment
        if(fragmentSituation.equals("onDestroy"))
        {
            frameLayoutWeight=9;
            buttonsLayoutWeight=1;
        }
        // process of assigning the new weights
        frameLayoutPage= requireActivity().findViewById(R.id.frameLayout);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0,frameLayoutWeight);
        frameLayoutPage.setLayoutParams(lp);
        bottomButtons = requireActivity().findViewById(R.id.constraint_contains_buttons);
        LinearLayout.LayoutParams lx = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 0,buttonsLayoutWeight);
        bottomButtons.setLayoutParams(lx);
    }

    private void variablesDeclarations(View view)
    {
        viewModal = new ViewModelProvider(requireActivity()).get(UserDataViewModal.class);
        //get the email of the user from view modal
        userEmail= viewModal.getUserEmail().getValue();

        // the message that will be displayed to the user when there are no wishes
        emptyDataMessage = view.findViewById(R.id.empty_data_message);

        recyclerView = view.findViewById(R.id.recyclerView);

        // the header which contains date and the wish titles
        header= view.findViewById(R.id.header);
        date = view.findViewById(R.id.date_title);
        wish = view.findViewById(R.id.wish_title);

        //the data to be fetched from firebase and display it to the user via recyclerView
        data= new ArrayList<>();

        //get instance from firebase
        db = FirebaseFirestore.getInstance();

        // overlay=(black background color)
        overlay = requireActivity().findViewById(R.id.overlay);
        // overlayAnimation= (lottie view)
        overlayAnimation = requireActivity().findViewById(R.id.overlayLoading);

        overlay.setVisibility(View.VISIBLE);
        overlayAnimation.setVisibility(View.VISIBLE);
    }

    private void hideWhenThereIsNoContent()
    {
        emptyDataMessage.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.INVISIBLE);
    }

    private void fetchData()
    {
        // fetch data from firebase by using userEmail that is saved in view modal
        db.collection(userEmail)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            //destructuring data from firebase then added it to the array, there are two data only, the wish of the user and the date of the wish when it is created
                            String wish = document.getString("wish");
                            String date = document.getString("date");
                            data.add(new Wish(wish,date, document.getId()));
                            // sort the list based on date from latest to oldest
                            Collections.sort(data);

                        }
                        // if there is data, display the data, otherwise display a text for no data availability
                        if(data.size()!=0) {
                            recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
                            recyclerView.setAdapter(new MyAdapter(requireActivity().getApplicationContext(), data, userEmail, overlay, overlayAnimation, requireActivity()));
                            header.setVisibility(View.VISIBLE);
                        }
                        else
                            //display a text for no data availability
                            hideWhenThereIsNoContent();

                    } else {
                        // if the API call fail, display a toast and display a text in the middle of the fragment for no data availability
                        Services.toastMessages("onFailure",null,null, "Display Data", task.getException(), requireActivity());
                        hideWhenThereIsNoContent();
                    }
                    // hide the overlay
                    hideOverlay();

                    // to display the back arrow
                    requireActivity().findViewById(R.id.back_arrow).setVisibility(View.VISIBLE);
                });
    }

    private void rotateInArabic()
    {
        if(Services.getAppLanguage()) {
            header.setScaleX(-1);
            date.setScaleX(-1);
            wish.setScaleX(-1);
        }
    }

    private void hideOverlay()
    {
        overlay.setVisibility(View.INVISIBLE);
        overlayAnimation.setVisibility(View.INVISIBLE);
    }
}
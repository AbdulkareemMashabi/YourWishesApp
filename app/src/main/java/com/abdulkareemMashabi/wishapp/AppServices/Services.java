package com.abdulkareemMashabi.wishapp.AppServices;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.airbnb.lottie.LottieAnimationView;
import com.abdulkareemMashabi.wishapp.R;

import java.util.Locale;

public class Services {

    // This method is handling the toast after calling Firebase API, and the toast for different purposes
    public static void toastMessages (String situation, TextView submitButtonText, LottieAnimationView submitButtonLoading, String flow, String errorMessage, Activity currentActivity)
    {
        //variables declaration for the toast
        Toast appToast = new Toast(currentActivity.getBaseContext());
        View toastView = currentActivity.getLayoutInflater().inflate( R.layout.toast, null);
        TextView toastText=toastView.findViewById(R.id.toast_text);
        ImageView toastImage=toastView.findViewById(R.id.toast_icon);

        // stop the animation loader of the submit button and display the text of the button, which indicate that the calling API is finished
        if(submitButtonText!=null && submitButtonLoading!=null) {
            submitButtonText.setVisibility(View.VISIBLE);
            submitButtonLoading.setVisibility(View.INVISIBLE);
            submitButtonLoading.pauseAnimation();
        }

        // handle success messages for API calling for different flows
        if(situation.equals("onSuccess")) {
            // set the toast icon to check mark
            toastImage.setImageResource(R.drawable.check_mark);
            if (flow.equals("signUp"))
                toastText.setText(currentActivity.getResources().getString(R.string.sign_up_toast));
            else if (flow.equals("reset"))
                toastText.setText(currentActivity.getResources().getString(R.string.email_reset_password));
            else if(flow.equals("Display Data"))
                toastText.setText(currentActivity.getResources().getString(R.string.successfully_delete));
                else
                toastText.setText(currentActivity.getResources().getString(R.string.add_wish_toast));
        }
        // handle failure message for API calling
        else if (situation.equals("onFailure")){
            toastImage.setImageResource(R.drawable.x_mark);
            toastText.setText(errorMessage);
        }
        //set the view toast and show it to the user
        appToast.setView(toastView);
        appToast.show();
    }

    public static void buttonPressed(View view)
    {
        //to make the view transparent when the user click on it, otherwise not
        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        view.setAlpha(0.5f); // Set button visibility to 0.5 when pressed
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        view.setAlpha(1.0f); // Set button visibility back to 1.0 when released or canceled
                        break;
                }
                return false;
            }
        });
    }

    // used to replace fragments by tag and put the transaction to backStack
    public static void replacement(Fragment fragment, FragmentActivity activity, String tag){
        FragmentManager fm = activity.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frameLayout,fragment).addToBackStack(tag).commit();
    }

    public static void replacementWithoutAddingToBackStack(Fragment fragment, FragmentActivity activity){
        FragmentManager fm =  activity.getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frameLayout,fragment).commit();
    }

    public static boolean isConnectedToNetwork(Context context) {
        // Get the ConnectivityManager
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get the active network information
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        // Check if the device is connected to any network
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static boolean getAppLanguage() {
        // get if the current app language is arabic or not
        return Locale.getDefault().getLanguage().equals("ar");
    }

}

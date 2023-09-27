package com.abdulkareemMashabi.wishapp.Fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import com.airbnb.lottie.LottieAnimationView;
import com.abdulkareemMashabi.wishapp.AppServices.Services;
import com.abdulkareemMashabi.wishapp.Components.CustomButton;
import com.abdulkareemMashabi.wishapp.R;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordPage extends Fragment {

    EditText email;
    TextView submitButtonText;
    LottieAnimationView submitButtonLoading;
    FirebaseAuth auth;
    CustomButton resetPasswordButton;


    // This fragment is used to let the user enter his email to reset his password
    public ResetPasswordPage() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // make the back arrow visible
        requireActivity().findViewById(R.id.back_arrow).setVisibility(View.VISIBLE);
        // hide bottom bar
        requireActivity().findViewById(R.id.constraint_contains_buttons).setVisibility(View.INVISIBLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_reset_password_page, container, false);

        variablesDeclarations(view);
        // change reset confirmation button text
        resetPasswordButton.setText(getResources().getString(R.string.reset_password));

        resetConfirmationButtonPressed();

        return view;
    }

    private void reset ()
    {
        // call firebase to reset email
        auth.sendPasswordResetEmail(email.getText().toString())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // if the reset email send successfully, success message toast should appear
                        Services.toastMessages("onSuccess",submitButtonText,submitButtonLoading, "reset", null, requireActivity());

                        //return to previous page
                        FragmentManager fm = requireActivity().getSupportFragmentManager();
                        fm.popBackStack();

                    } else {
                        /// if the reset email does not send, failure message toast should appear
                        Services.toastMessages("onFailure",submitButtonText,submitButtonLoading, "reset", task.getException(), requireActivity());
                    }
                });
    }

    private void variablesDeclarations(View view){
        // where the user put his email to reset
        email = view.findViewById(R.id.email);
        // confirmation button
        resetPasswordButton = view.findViewById(R.id.confirm_reset_password);
        auth= FirebaseAuth.getInstance();

        // lottie animation and text. The lottie for button API submission
        submitButtonText =  resetPasswordButton.findViewById(R.id.submit_button_text);
        submitButtonLoading =  resetPasswordButton.findViewById(R.id.loading_button);

    }

    private void resetConfirmationButtonPressed()
    {
        // confirmation button pressed
        resetPasswordButton.setOnClickListener(view -> {
            boolean checkEmail =!email.getText().toString().isEmpty()&& Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches();

            // check if the email is valid
            if(checkEmail)
            {
                // hide the text of the button and start the lottie animation
                submitButtonText.setVisibility(View.INVISIBLE);
                submitButtonLoading.setVisibility(View.VISIBLE);
                submitButtonLoading.playAnimation();

                reset();
            }
            else
                // toast will appear with validation reason
                Services.toastMessages("onFailure",submitButtonText,submitButtonLoading, "reset", new Exception(getResources().getString(R.string.validation_toast_4)), requireActivity());
        });
    }
}
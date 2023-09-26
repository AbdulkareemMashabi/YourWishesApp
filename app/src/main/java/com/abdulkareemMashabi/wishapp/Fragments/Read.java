package com.abdulkareemMashabi.wishapp.Fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.airbnb.lottie.LottieAnimationView;
import com.abdulkareemMashabi.wishapp.AppServices.Services;
import com.abdulkareemMashabi.wishapp.AppServices.ViewModalServices.UserDataViewModal;
import com.abdulkareemMashabi.wishapp.Components.CustomButton;
import com.abdulkareemMashabi.wishapp.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class Read extends Fragment {
    EditText email;
    EditText password;
    CustomButton signUpButton;
    CustomButton submitButton;
    TextView submitButtonText;
    LottieAnimationView submitButtonLoading;
    UserDataViewModal viewModal;
    FirebaseAuth user;
    RelativeLayout forgetPassword;


    // This fragment is used to let the user sign in then redirect the user to DisplayData fragment to display his wishes
    public Read() {
        // Required empty public constructor
    }

    @Override
    public void onStart() {
        super.onStart();
        // define FirebaseAuth variable
        user = FirebaseAuth.getInstance();
        // make the bottom tab visible when it is invisible
        requireActivity().findViewById(R.id.constraint_contains_buttons).setVisibility(View.VISIBLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_read, container, false);

        variablesDeclarations(view);
        buttonPressed();
        //change the text of the sign up button
        signUpButton.setText(getResources().getString(R.string.sign_up));

        return view;


    }

    private void signIn(String emailText, String passwordText){
        // sign in using firebase with the email and the password of the user, using firebase API

        // check network connectivity
        if(!Services.isConnectedToNetwork(requireContext()))
        {
            Services.toastMessages("onFailure",submitButtonText,submitButtonLoading, "read", getResources().getString(R.string.internet_connection), requireActivity());
        }
        // sign in process
        else
            user.signInWithEmailAndPassword(emailText, passwordText)
                    .addOnCompleteListener(requireActivity(), task -> {
                        if (task.isSuccessful()) {
                            // if the sign in process completed successfully, we will save the email of the user in view modal
                            viewModal.setUserEmail(emailText);

                            // direct the user to Display Data Fragment
                            Services.replacement(new DisplayData(), requireActivity(), "Display Data");
                        } else {
                            // If sign in fails, display a message to the user.
                            Services.toastMessages("onFailure",submitButtonText,submitButtonLoading, "read", Objects.requireNonNull(task.getException()).getMessage(), requireActivity());
                        }
                    });
    }

    private void variablesDeclarations(View view)
    {
        viewModal= new ViewModelProvider(requireActivity()).get(UserDataViewModal.class);

        // declare email and password EditText field
        email = view.findViewById(R.id.email_read);
        password= view.findViewById(R.id.password);


        // declare submit button with its lottie animation when calling an API and its text
        submitButton=view.findViewById(R.id.confirm_read);
        submitButtonText=submitButton.findViewById(R.id.submit_button_text);
        submitButtonLoading=submitButton.findViewById(R.id.loading_button);

        // declare sign up button and forget password button
        signUpButton= view.findViewById(R.id.signUpReadScreen);
        forgetPassword = view.findViewById(R.id.read_forget_password);
    }

    private void buttonPressed()
    {
        // when the user clicked on sign up button, it will direct him to sign up page
        signUpButton.setOnClickListener(view -> Services.replacement(new SignUp(),requireActivity(), "Sign Up"));

        // when the user clicked on forget password button, it will direct him to forget password page
        forgetPassword.setOnClickListener(view -> Services.replacement(new ResetPasswordPage(),requireActivity(), "Reset"));

        //when the user click on submit button after entering his email and password
        submitButton.setOnClickListener(view -> {
            String emailText = email.getText().toString();
            String passwordText =password.getText().toString();
            boolean checkEmail =!emailText.isEmpty()&& Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches();
            boolean checkWish = !passwordText.isEmpty();
            //check either the wish or the email are empty
            if( checkWish && checkEmail)
            {
                // hide the text of the button and display the animation of the button for calling API
                submitButtonText.setVisibility(View.INVISIBLE);
                submitButtonLoading.setVisibility(View.VISIBLE);
                submitButtonLoading.playAnimation();

                signIn(emailText, passwordText);
            }
            //if either the wish or the email are empty, the toast will be displayed
            else
                Services.toastMessages("onFailure",submitButtonText,submitButtonLoading, "Read", getResources().getString(R.string.validation_toast_2), requireActivity());

        });
    }
}
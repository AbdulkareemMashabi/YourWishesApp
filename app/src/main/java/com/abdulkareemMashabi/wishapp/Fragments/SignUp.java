package com.abdulkareemMashabi.wishapp.Fragments;

import android.content.res.ColorStateList;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import com.airbnb.lottie.LottieAnimationView;
import com.abdulkareemMashabi.wishapp.AppServices.Services;
import com.abdulkareemMashabi.wishapp.AppServices.ViewModalServices.UserDataViewModal;
import com.abdulkareemMashabi.wishapp.Components.CustomButton;
import com.abdulkareemMashabi.wishapp.Components.PasswordInputField;
import com.abdulkareemMashabi.wishapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class SignUp extends Fragment {

    FirebaseAuth mAuth;
    TextView submitButtonText;
    LottieAnimationView submitButtonLoading;
    EditText password;
    PasswordInputField passwordConfirmation;

    EditText passwordConfirmationText;
    EditText email;
    CustomButton confirmButton;
    UserDataViewModal viewModal;

    // This fragment is used to let the user sign up using email and password
    public SignUp() {
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
    public void onDestroy() {
        super.onDestroy();
        // make the back arrow inVisible
        requireActivity().findViewById(R.id.back_arrow).setVisibility(View.INVISIBLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_sign_up, container, false);

        variablesDeclarations(view);
        passwordConfirmation.changeHintText(getResources().getString(R.string.password_confirmation));
        signUpConfirmationButtonPressed();

        return view;
    }

    private void signUp(String emailText, String passwordText)
    {
        // if there is no connection, the toast will appear
        if(!Services.isConnectedToNetwork(requireContext()))
        {
            Services.toastMessages("onFailure",submitButtonText,submitButtonLoading, "signUp", new Exception(getResources().getString(R.string.internet_connection)), requireActivity());
        }
        // sign up process
        else
                mAuth.createUserWithEmailAndPassword(emailText, passwordText)
                .addOnCompleteListener( requireActivity(), task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        View menu=requireActivity().findViewById(R.id.constraint_contains_buttons);

                        // save the email of the user in the view modal to indicate that he is signing in in other pages and to fetch his data in other pages
                        viewModal = new ViewModelProvider(requireActivity()).get(UserDataViewModal.class);

                        viewModal.setUserEmail(Objects.requireNonNull(user).getEmail());

                        // got back to write page
                        requireActivity().getSupportFragmentManager().popBackStack("Write",0);

                        // decorate the write button in the bottom tab to simulate that the write page is appear in the screen
                        menu.findViewById(R.id.writeButton).setBackgroundTintList(ColorStateList.valueOf((getResources().getColor(R.color.text_input_field, null))));
                        menu.findViewById(R.id.readButton).setBackgroundTintList(ColorStateList.valueOf((getResources().getColor(R.color.black, null))));
                        menu.findViewById(R.id.write_word).setVisibility(View.VISIBLE);
                        menu.findViewById(R.id.read_word).setVisibility(View.INVISIBLE);

                        // if the user signed up successfully, success message toast should appear
                        Services.toastMessages("onSuccess",submitButtonText,submitButtonLoading, "signUp", null, requireActivity());
                    } else {
                        // If sign up fails, display a message to the user.
                        Services.toastMessages("onFailure",submitButtonText,submitButtonLoading, "signUp", task.getException(), requireActivity());
                    }
                });
    }

    private void variablesDeclarations(View view){
        mAuth = FirebaseAuth.getInstance();
        // confirmation button
        confirmButton = view.findViewById(R.id.confirmSignUp);
        // lottie animation and text. The lottie for button API submission
        submitButtonText =  confirmButton.findViewById(R.id.submit_button_text);
        submitButtonLoading =  confirmButton.findViewById(R.id.loading_button);

        // define password and email edit text and confirmation password
        email = view.findViewById(R.id.emailSignUp);
        password = view.findViewById(R.id.passwordSignUp).findViewById(R.id.password);
        passwordConfirmation = view.findViewById(R.id.passwordSignUpConfirmation);
        passwordConfirmationText= passwordConfirmation.findViewById(R.id.password);


    }

    private void signUpConfirmationButtonPressed()
    {
        confirmButton.setOnClickListener(view -> {
            String emailText=email.getText().toString();
            String passwordText = password.getText().toString();
            String confirmationPasswordText = passwordConfirmationText.getText().toString();
            // check if the email match email pattern, and the last three letters are "com"
            boolean checkEmail =!emailText.isEmpty()&& Patterns.EMAIL_ADDRESS.matcher(emailText).matches() && emailText.endsWith("com");
            boolean checkPassword = !passwordText.isEmpty();
            boolean checkPasswordConfirmation = !confirmationPasswordText.isEmpty();
            //check the wish or the email are empty and the passwords are match each other
            if(checkEmail && checkPassword && checkPasswordConfirmation && passwordText.equals(confirmationPasswordText)) {
                // hide the text of the button and start the lottie animation
                submitButtonText.setVisibility(View.INVISIBLE);
                submitButtonLoading.setVisibility(View.VISIBLE);
                submitButtonLoading.playAnimation();
                signUp(emailText,passwordText);
            }
            else {
                // toast will appear with validation reason
                if (!passwordText.equals(confirmationPasswordText) )
                    Services.toastMessages("onFailure", submitButtonText, submitButtonLoading, "signUp", new Exception(getResources().getString(R.string.password_matching)), requireActivity());
                else
                    Services.toastMessages("onFailure", submitButtonText, submitButtonLoading, "signUp", new Exception(getResources().getString(R.string.validation_toast_2)), requireActivity());
            }
        });
    }

}
package com.abdulkareemMashabi.wishapp.Fragments;

import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
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
import com.abdulkareemMashabi.wishapp.AppServices.Wish;
import com.abdulkareemMashabi.wishapp.Components.CustomButton;
import com.abdulkareemMashabi.wishapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class Write extends Fragment {
    CustomButton confirmButton;
    TextView submitButtonText;
    LottieAnimationView submitButtonLoading;

    FirebaseFirestore db;
    EditText wish;
    EditText email;
    ConstraintLayout passwordView;
    EditText password;

    TextView description;
    String userEmail="";
    FirebaseAuth user;
    UserDataViewModal viewModal;
    CustomButton signUp;
    RelativeLayout forgetPassword;

    // This fragment is used to let the user sign in and write his wishes
    public Write() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // define FirebaseAuth variable
        user = FirebaseAuth.getInstance();

    }

    @Override
    public void onStart() {
        super.onStart();
        // make the bottom bar visible
        requireActivity().findViewById(R.id.constraint_contains_buttons).setVisibility(View.VISIBLE);
        // read the user email to indicate if the user is signed in form other fragments, if yes change the design of the fragment by "changeVisibility" function
        userEmail= viewModal.getUserEmail().getValue();
        changeVisibility();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_write, container, false);

        variablesDeclarations(view);
        // change sign up text
        signUp.setText(getResources().getString(R.string.sign_up));
        buttonsPressed();


        return view;
    }

    private void changeVisibility(){
        // if the user sign in and there is a network connection
        if(!userEmail.isEmpty()&& Services.isConnectedToNetwork(requireContext()))
        {
            // when the "userEmail" variable is not empty, which is mean that the user is signed in, so we will not need email EditText, password EditText, sign up button, and forget password button
            email.setVisibility(View.GONE);
            forgetPassword.setVisibility(View.GONE);
            passwordView.setVisibility(View.GONE);
            signUp.setVisibility(View.GONE);

            // change the text of the description when the user is signed in
            description.setText(R.string.new_write_title);


            // change the constraints of the remaining component so the design will not break
            ConstraintLayout constraintLayout = requireView().findViewById(R.id.write_layout);
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);
            constraintSet.clear(R.id.description_writePage, ConstraintSet.TOP);
            constraintSet.clear(R.id.confirm,ConstraintSet.BOTTOM);
            constraintSet.connect(R.id.description_writePage,ConstraintSet.BOTTOM,R.id.wish,ConstraintSet.TOP,100);
            constraintSet.connect(R.id.wish,ConstraintSet.TOP,R.id.write_layout,ConstraintSet.TOP,0);
            constraintSet.connect(R.id.wish,ConstraintSet.BOTTOM,R.id.write_layout,ConstraintSet.BOTTOM,0);
            constraintSet.connect(R.id.confirm,ConstraintSet.TOP,R.id.wish,ConstraintSet.BOTTOM,100);
            constraintSet.applyTo(constraintLayout);
        }
    }

    private void signIn(String emailText,String passwordText,String wishText){
        // if the user is not signed in before, we will signed him first then we will add the wish, but first we will check the internet connection

        // if there is no connection
        if(!Services.isConnectedToNetwork(requireContext()))
        {
            Services.toastMessages("onFailure",submitButtonText,submitButtonLoading, "write", getResources().getString(R.string.internet_connection), requireActivity());
        }
        // when the user not signed in before, the userEmail in the view modal will be empty
        else if(userEmail.isEmpty())
        {
            //sign in process using email and the password with the firebase
            user.signInWithEmailAndPassword(emailText, passwordText)
                    .addOnCompleteListener(requireActivity(), task -> {
                        if (task.isSuccessful()) {
                            // write the wish of the user to the firebase
                                writeToFireBase(emailText, wishText);

                        } else {
                            // If sign in fails, display a message to the user.
                            Services.toastMessages("onFailure",submitButtonText,submitButtonLoading, "write", Objects.requireNonNull(task.getException()).getMessage(), requireActivity());
                        }
                    });

        }
        // if he already signed in, write the wish in the firebase
        else
            writeToFireBase(userEmail, wishText);



    }

    private void writeToFireBase(String email, String wishText) {

        // create the date when the wish created and save it in the firebase with the wish
        Date date = new Date();
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        String dateFormatted = dt.format(date);
        Wish wishObject = new Wish(wishText, dateFormatted, null);

        // save the wish object in the database of the firebase
        db.collection(email)
                .add(wishObject)
                .addOnSuccessListener(documentReference -> {
                    // reset the wish EditText text
                    wish.setText("");

                    changeVisibility();
                    // display success message to the user
                   Services.toastMessages("onSuccess",submitButtonText,submitButtonLoading, "write", null, requireActivity());
                })
                .addOnFailureListener(e -> {
                    // display failure message to the user
                    Services.toastMessages("onFailure",submitButtonText,submitButtonLoading, "write", e.getMessage(), requireActivity());
                });
        // save the email of the user if he not signed in before, to indicate he is signing in, so we will change the design of this fragment by "changeVisibility" function
        if(userEmail.isEmpty())
            setUserEmail();
    }

    private void setUserEmail()
    {
        // write the user email in the view modal to indicate that the user is signed in for the other fragments
        userEmail=email.getText().toString();
        viewModal.setUserEmail(userEmail);
    }

    private void variablesDeclarations(View view){
        viewModal = new ViewModelProvider(requireActivity()).get(UserDataViewModal.class);

        //declare submit button and its text and lottie animation (loading animation) for API calling
        confirmButton = view.findViewById(R.id.confirm);
        submitButtonText =  confirmButton.findViewById(R.id.submit_button_text);
        submitButtonLoading =  confirmButton.findViewById(R.id.loading_button);

        db = FirebaseFirestore.getInstance();
        // declare the description that will be displayed in the top of the page
        description = (TextView) view.findViewById(R.id.description_writePage);

        // declare the wish, and the email edit text
        wish = (EditText)view.findViewById(R.id.wish);
        email = (EditText)view.findViewById(R.id.email);

        // declare password component
        passwordView = view.findViewById(R.id.password_write_eye_icon);
        // declare the EditText that used in passwordView for writing password
        password= passwordView.findViewById(R.id.password);

        //declare sign up, and forget password buttons
        signUp= view.findViewById(R.id.signUpWriteScreen);
        forgetPassword = view.findViewById(R.id.write_forget_password);

    }

    private void buttonsPressed()
    {
        // when the user click on the forget password button, redirect him to forget password fragment
        forgetPassword.setOnClickListener(view -> Services.replacement(new ResetPasswordPage(),requireActivity(), "Reset"));

        // when the user click on the sign up password button, redirect him to sign up password fragment
        signUp.setOnClickListener(view -> Services.replacement(new SignUp(),requireActivity(), "Sign Up"));

        // when the user click on the submit button, call firebase API
        confirmButton.setOnClickListener(view -> {
            String emailText = email.getText().toString();
            String passwordText =password.getText().toString();
            String wishText = wish.getText().toString();
            boolean checkEmail =!emailText.isEmpty()&& Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches();
            boolean checkPassword = !passwordText.isEmpty();
            boolean checkWish = !wishText.isEmpty();

            // if the user not signed in, there will be three fields the user must filled and they are email, password, and the wish. I will check if they are empty or not and if the email match the email conditions.
            // when the email EditText is Gone, that means the user is signed in, so the only field will be displayed in that time is the "new wish" which the user want to add, so I will check only if the wish EditText is empty or not.
            if(email.getVisibility()==View.GONE && checkWish || checkWish && checkPassword && checkEmail)
            {
                // hide the text of the button and display the animation of the button for calling API
                submitButtonText.setVisibility(View.INVISIBLE);
                submitButtonLoading.setVisibility(View.VISIBLE);
                submitButtonLoading.playAnimation();

                signIn(emailText,passwordText,wishText);
            }
            else {
                // handle toast message based if the user signed in or not. If the user signed in, the user email will be saved through view modal, otherwise the user email will be empty in view modal
                // if the user signed in (so the user email in the view modal is not empty), he should write the wish only correctly
                if (!userEmail.isEmpty())
                    Services.toastMessages("onFailure", submitButtonText, submitButtonLoading, "write", getResources().getString(R.string.validation_toast_3), requireActivity());
                    // if the user not signed in, he should write the email and the wish and the password correctly
                else
                    Services.toastMessages("onFailure", submitButtonText, submitButtonLoading, "write", getResources().getString(R.string.validation_toast_1), requireActivity());
            }
        });
    }
}


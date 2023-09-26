package com.abdulkareemMashabi.wishapp.Components;

import android.content.Context;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.abdulkareemMashabi.wishapp.AppServices.Services;
import com.abdulkareemMashabi.wishapp.R;

public class PasswordInputField extends ConstraintLayout {
    View view;
    public PasswordInputField(Context context) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view=inflater.inflate(R.layout.password_field, this);
        iconPressed();
    }

    public PasswordInputField(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view=inflater.inflate(R.layout.password_field, this);
        iconPressed();
    }

    public PasswordInputField(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view=inflater.inflate(R.layout.password_field, this);
        iconPressed();

    }

    private void iconPressed()
    {
        ImageView eyeIcon = findViewById(R.id.eye_icon);

        Services.buttonPressed(eyeIcon);

        //replace the hidden icon with visible icon when the user clicked on the eye icon and vise versa
        eyeIcon.setOnClickListener(view -> {

            EditText passwordField = findViewById(R.id.password);
            String tag =(String) getTag();

            // knowing the icon situation by the tag of the root view

            // if the previous situation is non-visible eye icon
            if(tag==null||tag.equals("not_visible")){
                setTag("visible");
                eyeIcon.setImageResource(R.drawable.eye_visible_icon);
                // make the hidden password unhidden
                passwordField.setTransformationMethod(null);
            }
            else
            {
                setTag("not_visible");
                eyeIcon.setImageResource(R.drawable.eye_not_visible_icon);
                // next line to make the numbers hidden
                passwordField.setTransformationMethod(PasswordTransformationMethod.getInstance());


            }
        });

    }

    // change the hint of the edit text
    public void changeHintText(String newHint)
    {
       EditText passwordArea= view.findViewById(R.id.password);
       passwordArea.setHint(newHint);
    }

}

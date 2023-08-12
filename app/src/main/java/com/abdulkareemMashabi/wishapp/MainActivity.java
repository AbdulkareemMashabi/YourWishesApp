package com.abdulkareemMashabi.wishapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.abdulkareemMashabi.wishapp.AppServices.Services;
import com.abdulkareemMashabi.wishapp.AppServices.ViewModalServices.UserDataViewModal;
import com.abdulkareemMashabi.wishapp.Components.CustomButton;
import com.abdulkareemMashabi.wishapp.Fragments.DisplayData;
import com.abdulkareemMashabi.wishapp.Fragments.LottieFragment;
import com.abdulkareemMashabi.wishapp.Fragments.Read;
import com.abdulkareemMashabi.wishapp.Fragments.Write;
import java.util.Locale;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {

    View menu;
    UserDataViewModal viewModal;
    ImageView backArrow;
    ImageView logout;
    ImageView languageIcon;
    ConstraintLayout language;
    TextView appTitle;
    CustomButton englishLanguageButton;
    CustomButton arabicLanguageButton;
    TextView writeWord;
    TextView readWord;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        defaultLanguage();
        setContentView(R.layout.activity_main);
        variablesDeclarations();
        //set custom action bar
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        handleToolbarButtonsPressed();
        bottomBarButtonsPressed();
        viewModalReadAction();
        onLanguageSelected();
        setViewTexts();
        appInitialEntryPoint();

    }

    private void variablesDeclarations(){

        viewModal = new ViewModelProvider(this).get(UserDataViewModal.class);

        //declare toolbar and its icons and title
        toolbar = findViewById(R.id.toolbar);
        backArrow = findViewById(R.id.back_arrow);
        languageIcon = findViewById(R.id.language);
        logout=findViewById(R.id.logout);
        // define the language view that has the two button of the languages and they are arabic and english
        language = findViewById(R.id.language_layout);

        appTitle= findViewById(R.id.app_name);
        englishLanguageButton=findViewById(R.id.english);
        arabicLanguageButton = findViewById(R.id.arabic);

        //declare bottom bar and its icon's words
        menu = findViewById(R.id.constraint_contains_buttons);
        writeWord=findViewById(R.id.write_word);
        readWord=findViewById(R.id.read_word);

    }

    private void handleToolbarButtonsPressed()
    {
        // if the language icon is clicked make the language view visible
        languageIcon.setOnClickListener(view -> {
            if(language.getVisibility()==View.INVISIBLE)
                language.setVisibility(View.VISIBLE);
        });

        //restart the app if the user logout
        logout.setOnClickListener(view -> {
            // delete the user email from local storage, so he will not be signed in when he use the app again because I will read the saved user email when the app lunched to make him signed in automatically, or read an empty email to make him unsigned in
            SharedPreferences.Editor editor = getSharedPreferences("wish_app", MODE_PRIVATE).edit();
            editor.putString("email", "");
            editor.apply();

            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finishAffinity();

        });

        // if the user clicked on backArrow, the app should return to the previous page
        backArrow.setOnClickListener(view -> onBackPressed());
    }

    private void bottomBarButtonsPressed()
    {
        // when the user clicked on read buttons in the bottom tab
        menu.findViewById(R.id.read_constraint).setOnClickListener(view -> {
            //if the read page is displayed now, do nothing
            if(menu.findViewById(R.id.read_word).getVisibility()==View.INVISIBLE) {
                String userEmail = viewModal.getUserEmail().getValue();
                bottomTabDecoration("Read");
                //if the user not logged in, guide him to the read page otherwise guide him to the displayData page to read the data
                if(Objects.requireNonNull(userEmail).isEmpty()|| !Services.isConnectedToNetwork(getApplicationContext()))
                    replacement(new Read(), "Read");
                else replacement(new DisplayData(), "Display Data");
            }
        });

        // when the user clicked on write buttons in the bottom tab
        menu.findViewById(R.id.write_constraint).setOnClickListener(view -> {
            //if the write page is displayed now, do nothing
            if(menu.findViewById(R.id.write_word).getVisibility()==View.INVISIBLE) {
                bottomTabDecoration("Write");
                replacement(new Write(), "Write");
            }
        });
    }

    private void viewModalReadAction()
    {
        // if the user is signed in, and the network is available, the logout buttons should be appear, otherwise not
        viewModal.getUserEmail().observe(this, item ->{
            if(!item.isEmpty()&&Services.isConnectedToNetwork(getApplicationContext())) {
                logout.setVisibility(View.VISIBLE);
                // save the email of the user to make him signed in even if the app is closed
                SharedPreferences.Editor editor = getSharedPreferences("wish_app", MODE_PRIVATE).edit();
                editor.putString("email", item);
                editor.apply();
            }
        });
    }

    @Override
    public void onBackPressed()
    {
        //make the backArrow Icon invisible because the maximum number of fragments in stack are two, so after pop from the stack, there will be one fragment only
        backArrow.setVisibility(View.INVISIBLE);

        FragmentManager fm = getSupportFragmentManager();
        int count = fm.getBackStackEntryCount();
        FragmentManager.BackStackEntry entry = fm.getBackStackEntryAt(count - 1);
        String tag = entry.getName();

        if (fm.getBackStackEntryCount() > 0) {
            // If the current page is sign up, pop from stack
            if(tag.equals("Sign Up"))
            {
                fm.popBackStack();
            }

            //if the current page is either write or read page, change the decoration of the buttons to either highlight read or write and pop the fragment
            else if(tag.equals("Read")||tag.equals("Write")) {
                if (menu.findViewById(R.id.write_word).getVisibility() == View.VISIBLE) {
                    bottomTabDecoration("Read");
                } else {
                    bottomTabDecoration("Write");
                }
                fm.popBackStack();
            }

            //if the current page is Display data, return to write page
            else if(tag.equals("Display Data"))
            {
                bottomTabDecoration("Write");
                getSupportFragmentManager().popBackStack("Write",0);
            }
        } else {
            // If the back stack is empty, perform the default back button behavior (finish the activity)
            finish();
        }

    }

    private void bottomTabDecoration(String situation){
        if(situation.equals("Read"))
        {
            // if the user clicked on the read tab make it white and make its text appear to the user, and the write tab will be black and its text will be disappear
            menu.findViewById(R.id.readButton).setBackgroundTintList(ColorStateList.valueOf((getResources().getColor(R.color.white, null))));
            menu.findViewById(R.id.writeButton).setBackgroundTintList(ColorStateList.valueOf((getResources().getColor(R.color.black, null))));
            menu.findViewById(R.id.read_word).setVisibility(View.VISIBLE);
            menu.findViewById(R.id.write_word).setVisibility(View.INVISIBLE);
        }
        else {
            // if the user clicked on the write tab make it white and make its text appear to the user, and the read tab will be black and its text will be disappear
            menu.findViewById(R.id.writeButton).setBackgroundTintList(ColorStateList.valueOf((getResources().getColor(R.color.white, null))));
            menu.findViewById(R.id.readButton).setBackgroundTintList(ColorStateList.valueOf((getResources().getColor(R.color.black, null))));
            menu.findViewById(R.id.write_word).setVisibility(View.VISIBLE);
            menu.findViewById(R.id.read_word).setVisibility(View.INVISIBLE);
        }

    }

    private void replacement(Fragment fr, String tag){
        // service that handle app navigation
        Services.replacement(fr, this, tag);
    }
    private void defaultLanguage()
    {
        // process for reading and setting the last language that the user selected. If the app not used before, set the arabic as default language
        SharedPreferences prefs = getSharedPreferences("wish_app", MODE_PRIVATE);
        String defaultLanguage = prefs.getString("language", "ar");
        Configuration config = getBaseContext().getResources().getConfiguration();
        Locale locale = new Locale(defaultLanguage);
        Locale.setDefault(locale);

        //support previous versions of android
        config.setLocale(locale);
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
    }
    private void onLanguageSelected(){
        Locale current;
        //support previous versions of android
        current = getResources().getConfiguration().getLocales().get(0);

        //if the arabic language button is selected
        language.findViewById(R.id.arabic).setOnClickListener(view -> {
            // if the current app language is english and the user selected arabic language then change the language, otherwise do not change the language and hide the language view
            if(!current.getLanguage().equals("ar"))
            setLanguage("ar");
            else
                language.setVisibility(View.INVISIBLE);

        });
        //if the english language button is selected
        language.findViewById(R.id.english).setOnClickListener(view -> {
            // if the current app language is arabic and the user selected english language then change the language, otherwise do not change the language and hide the language view
            if(!current.getLanguage().equals("en"))
            setLanguage("en");
            else
                language.setVisibility(View.INVISIBLE);
        });
    }

    private void setViewTexts()
    {
        // set the texts for the language buttons
        englishLanguageButton.setText(getResources().getString(R.string.english_language));
        arabicLanguageButton.setText(getResources().getString(R.string.arabic_language));
    }

    private void setLanguage(String lang){
        //save the language in local storage of the app
        SharedPreferences.Editor editor = getSharedPreferences("wish_app", MODE_PRIVATE).edit();
        editor.putString("language", lang);
        editor.apply();
        //restart the app
        Intent refresh = new Intent(this, MainActivity.class);
        startActivity(refresh);
        finish();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        Rect viewRect = new Rect();
        language.getGlobalVisibleRect(viewRect);
        // if the list of the languages view is visible and the user click outside of it, make it invisible
        if (!viewRect.contains((int) ev.getRawX(), (int) ev.getRawY())&&language.getVisibility()==View.VISIBLE) {
            language.setVisibility(View.INVISIBLE);
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    private void appInitialEntryPoint()
    {
        //start the app with initial route
        Services.replacementWithoutAddingToBackStack(new LottieFragment(),this);
    }

}
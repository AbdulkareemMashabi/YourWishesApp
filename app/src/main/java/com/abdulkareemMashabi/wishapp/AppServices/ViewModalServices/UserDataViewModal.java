package com.abdulkareemMashabi.wishapp.AppServices.ViewModalServices;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class UserDataViewModal extends ViewModel {
    private MutableLiveData<String> userEmail = new MutableLiveData<>("");

    // to get the email of the user
    public LiveData<String> getUserEmail()
    {
        return userEmail;
    }

    // to save the email of the user when he is logging in
    public void setUserEmail(String userEmail)
    {
        this.userEmail.setValue(userEmail);
    }
}

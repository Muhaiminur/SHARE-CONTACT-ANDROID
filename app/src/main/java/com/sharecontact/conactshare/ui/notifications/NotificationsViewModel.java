package com.sharecontact.conactshare.ui.notifications;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.sharecontact.conactshare.DATABASE.CONTACT;
import com.sharecontact.conactshare.DATABASE.CONTACT_REPO;

import java.util.List;

public class NotificationsViewModel extends AndroidViewModel {
    CONTACT_REPO contact_repo;
    MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    MutableLiveData<Boolean> error = new MutableLiveData<>();

    public NotificationsViewModel(@NonNull Application application) {
        super(application);
        contact_repo = new CONTACT_REPO();
    }

    public MutableLiveData<List<CONTACT>> getAllBlog() {
        isLoading.postValue(true);
        error.postValue(false);
        return contact_repo.getMutableLiveData();
    }

    public MutableLiveData<Boolean> getIsLoading() {
        return isLoading;
    }
}
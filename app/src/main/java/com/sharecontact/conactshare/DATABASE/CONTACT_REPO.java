package com.sharecontact.conactshare.DATABASE;

import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;

public class CONTACT_REPO {
    private ArrayList<CONTACT> movies = new ArrayList<>();
    private MutableLiveData<List<CONTACT>> mutableLiveData = new MutableLiveData<>();

    public CONTACT_REPO() {

    }

    public MutableLiveData<List<CONTACT>> getMutableLiveData() {
        mutableLiveData.postValue(movies);
        return mutableLiveData;
    }
}

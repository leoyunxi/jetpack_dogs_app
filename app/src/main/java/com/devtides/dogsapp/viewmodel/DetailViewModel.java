package com.devtides.dogsapp.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.devtides.dogsapp.model.DogBreed;

public class DetailViewModel extends ViewModel {

    public MutableLiveData<DogBreed> dogLiveData = new MutableLiveData<>();

    public void fetch(int uuid) {
        DogBreed dog1 = new DogBreed("1", "corgi", "15", "", "me", "calm", "");
        dogLiveData.setValue(dog1);
    }
}

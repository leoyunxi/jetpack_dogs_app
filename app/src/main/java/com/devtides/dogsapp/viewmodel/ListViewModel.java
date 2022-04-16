package com.devtides.dogsapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.devtides.dogsapp.model.DogBreed;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ListViewModel extends AndroidViewModel {

    public MutableLiveData<List<DogBreed>> dogs = new MutableLiveData<>();
    public MutableLiveData<Boolean> hasError = new MutableLiveData<>();
    public MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public ListViewModel(@NonNull Application application) {
        super(application);
    }

    public void refresh() {
        DogBreed dog1 = new DogBreed("1", "corgi", "15", "", "", "", "");
        DogBreed dog2 = new DogBreed("2", "rotwailler", "10", "", "", "", "");
        DogBreed dog3 = new DogBreed("3", "labrador", "13", "", "", "", "");
//        ArrayList<DogBreed> list = new ArrayList<>(Arrays.asList(dog1, dog2, dog3));
        ArrayList<DogBreed> list = new ArrayList<>(Collections.nCopies(8, dog1));

        dogs.setValue(list);
        hasError.setValue(false);
        isLoading.setValue(false);
    }
}

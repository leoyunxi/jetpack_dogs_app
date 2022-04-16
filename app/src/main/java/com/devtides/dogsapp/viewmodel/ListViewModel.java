package com.devtides.dogsapp.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.devtides.dogsapp.model.DogBreed;
import com.devtides.dogsapp.model.DogsApiService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;

public class ListViewModel extends AndroidViewModel {

    private DogsApiService apiService = new DogsApiService();
    private CompositeDisposable disposable = new CompositeDisposable();

    public MutableLiveData<List<DogBreed>> dogs = new MutableLiveData<>();
    public MutableLiveData<Boolean> hasError = new MutableLiveData<>();
    public MutableLiveData<Boolean> isLoading = new MutableLiveData<>();

    public ListViewModel(@NonNull Application application) {
        super(application);
    }

    public void refresh() {
        fetchFromRemote();
    }

    private void fetchFromRemote() {
        isLoading.setValue(true);
        disposable.add(
                apiService.getDogs()
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<List<DogBreed>>() {
                            @Override
                            public void onSuccess(List<DogBreed> dogBreeds) {
                                dogs.setValue(dogBreeds);
                                hasError.setValue(false);
                                isLoading.setValue(false);
                            }

                            @Override
                            public void onError(Throwable e) {
                                hasError.setValue(true);
                                isLoading.setValue(false);
                                e.printStackTrace();
                            }
                        })
        );
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        disposable.clear();
    }

    private void getDummyData() {
        DogBreed dog1 = new DogBreed("1", "corgi", "15", "", "", "", "");
        ArrayList<DogBreed> list = new ArrayList<>(Collections.nCopies(8, dog1));

        dogs.setValue(list);
        hasError.setValue(false);
        isLoading.setValue(false);
    }
}

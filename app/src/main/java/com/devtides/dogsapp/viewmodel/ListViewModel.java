package com.devtides.dogsapp.viewmodel;

import android.app.Application;
import android.os.AsyncTask;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import com.devtides.dogsapp.model.DogBreed;
import com.devtides.dogsapp.model.DogDao;
import com.devtides.dogsapp.model.DogDatabase;
import com.devtides.dogsapp.model.DogsApiService;

import java.util.ArrayList;
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
    private AsyncTask<List<DogBreed>, Void, List<DogBreed>> insertTask;

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
                                insertTask = new InsertDogsTask();
                                insertTask.execute(dogBreeds);
                                Toast.makeText(getApplication(), "Dogs retrieved from endpoint", Toast.LENGTH_SHORT).show();
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

    private class InsertDogsTask extends AsyncTask<List<DogBreed>, Void, List<DogBreed>> {

        @Override
        protected List<DogBreed> doInBackground(List<DogBreed>... lists) {
            List<DogBreed> list = lists[0];
            DogDao dao = DogDatabase.getInstance(getApplication()).dogDao();
            dao.deleteAllDogs();

            ArrayList<DogBreed> newList = new ArrayList<>(list);
            List<Long> result = dao.insertAll(newList.toArray(new DogBreed[0]));

            int i = 0;
            while (i < list.size()) {
                list.get(i).uuid = result.get(i).intValue();
                ++i;
            }

            return list;
        }

        @Override
        protected void onPostExecute(List<DogBreed> dogBreeds) {
            dogsRetrieved(dogBreeds);
        }
    }

    private void dogsRetrieved(List<DogBreed> dogList ) {
        dogs.setValue(dogList);
        hasError.setValue(false);
        isLoading.setValue(false);
    }
}

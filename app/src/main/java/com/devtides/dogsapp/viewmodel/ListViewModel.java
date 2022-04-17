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
import com.devtides.dogsapp.util.SharedPreferencesHelper;

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
    private AsyncTask<Void, Void, List<DogBreed>> retrieveTask;

    private SharedPreferencesHelper prefHelper = SharedPreferencesHelper.getInstance(getApplication());
    private long refreshTime = 5 * 60 * 1000 * 1000 * 1000L;

    public ListViewModel(@NonNull Application application) {
        super(application);
    }

    public void refresh() {
        checkCacheDuration();
        long updateTime = prefHelper.getUpdateTime();
        long currentTime = System.nanoTime();
        if(updateTime != 0 && currentTime - updateTime < refreshTime) {
            fetchFromDatabase();
        } else {
            fetchFromRemote();
        }
    }

    private void checkCacheDuration() {
        String cachePreference = prefHelper.getCacheDuration();

        if(!cachePreference.equals("")) {
            try {
                int cachePreferenceInt = Integer.parseInt(cachePreference);
                refreshTime = cachePreferenceInt * 1000 * 1000 * 1000L;
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }

    public void refreshBypassCache() {
        fetchFromRemote();
    }

    private void fetchFromDatabase() {
        isLoading.setValue(true);
        retrieveTask = new RetrieveDogsTask();
        retrieveTask.execute();
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

        if(insertTask != null) {
            insertTask.cancel(true);
            insertTask = null;
        }

        if(retrieveTask != null) {
            retrieveTask.cancel(true);
            retrieveTask = null;
        }
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
            prefHelper.saveUpdateTime(System.nanoTime());
        }
    }

    private void dogsRetrieved(List<DogBreed> dogList ) {
        dogs.setValue(dogList);
        hasError.setValue(false);
        isLoading.setValue(false);
    }

    private class RetrieveDogsTask extends AsyncTask<Void, Void, List<DogBreed>> {

        @Override
        protected List<DogBreed> doInBackground(Void... voids) {
            return DogDatabase.getInstance(getApplication()).dogDao().getAllDogs();
        }

        @Override
        protected void onPostExecute(List<DogBreed> dogBreeds) {
            dogsRetrieved(dogBreeds);
            Toast.makeText(getApplication(), "Dogs retrieved from database", Toast.LENGTH_SHORT).show();
        }
    }
}

package com.devtides.dogsapp.viewmodel;

import android.app.Application;
import android.os.AsyncTask;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.devtides.dogsapp.model.DogBreed;
import com.devtides.dogsapp.model.DogDatabase;

import java.util.List;

public class DetailViewModel extends AndroidViewModel {

    public MutableLiveData<DogBreed> dogLiveData = new MutableLiveData<>();
    private AsyncTask<Integer, Void, DogBreed> retrieveTask;

    public DetailViewModel(@NonNull Application application) {
        super(application);
    }

    public void fetch(int uuid) {
        retrieveTask = new RetrieveDogsTask();
        retrieveTask.execute(uuid);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if(retrieveTask != null) {
            retrieveTask.cancel(true);
            retrieveTask = null;
        }
    }

    private class RetrieveDogsTask extends AsyncTask<Integer, Void, DogBreed> {

        @Override
        protected DogBreed doInBackground(Integer... integers) {
            int uuid = integers[0];
            return DogDatabase.getInstance(getApplication()).dogDao().getDog(uuid);
        }

        @Override
        protected void onPostExecute(DogBreed dogBreed) {
            dogLiveData.setValue(dogBreed);
            Toast.makeText(getApplication(), "Dogs retrieved from database", Toast.LENGTH_SHORT).show();
        }
    }
}

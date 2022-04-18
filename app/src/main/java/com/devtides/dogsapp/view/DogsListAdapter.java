package com.devtides.dogsapp.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.navigation.NavAction;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.devtides.dogsapp.R;
import com.devtides.dogsapp.databinding.DogItemBinding;
import com.devtides.dogsapp.model.DogBreed;
import com.devtides.dogsapp.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class DogsListAdapter extends RecyclerView.Adapter<DogsListAdapter.DogViewHolder> implements DogClickListener {

    private ArrayList<DogBreed> dogsList;

    public DogsListAdapter(ArrayList<DogBreed> dogsList) {
        this.dogsList = dogsList;
    }

    @Override
    public void onDogClicked(View v) {
        String uuid = ((TextView) v.findViewById(R.id.clickedDogId)).getText().toString();
        ListFragmentDirections.ActionListFragmentToDetailFragment action = ListFragmentDirections.actionListFragmentToDetailFragment();
        action.setDogUuid(Integer.valueOf(uuid));
        Navigation.findNavController(v).navigate(action);
    }

    public void updateDogsList(List<DogBreed> newDogsList) {
        dogsList.clear();
        dogsList.addAll(newDogsList);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        DogItemBinding view = DataBindingUtil.inflate(inflater, R.layout.dog_item, parent, false);
        return new DogViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DogViewHolder holder, int position) {
        holder.itemView.setDog(dogsList.get(position));
        holder.itemView.setListener(this);
    }

    @Override
    public int getItemCount() {
        return dogsList.size();
    }

    class DogViewHolder extends RecyclerView.ViewHolder {

        public DogItemBinding itemView;

        public DogViewHolder(@NonNull DogItemBinding itemView) {
            super(itemView.getRoot());
            this.itemView = itemView;
        }
    }
}

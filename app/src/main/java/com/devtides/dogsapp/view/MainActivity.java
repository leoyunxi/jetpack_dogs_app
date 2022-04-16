package com.devtides.dogsapp.view;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.os.Bundle;

import com.devtides.dogsapp.R;

public class MainActivity extends AppCompatActivity {

    private NavController navController;
    private NavHostFragment navHostFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentContainerView);
//        navController = Navigation.findNavController(this,R.id.fragmentContainerView);
        navController = navHostFragment.getNavController();
        NavigationUI.setupActionBarWithNavController(this, navController);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, (DrawerLayout) null);
    }
}
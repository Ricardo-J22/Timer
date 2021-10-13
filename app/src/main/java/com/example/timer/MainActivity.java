package com.example.timer;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.timer.databinding.ActivityMainBinding;
import com.example.timer.ui.home.HomeViewModel;
import com.example.timer.ui.home.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.jetbrains.annotations.NotNull;

import tools.DatabaseHelper;
import tools.MyDialog;
import tools.SharedPreferencesUtils;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private static SharedPreferencesUtils sharedPreferencesUtils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //建立数据库
        DatabaseHelper databaseHelper = new DatabaseHelper(MainActivity.this,"timeRecord.db",null,1);
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        HomeViewModel homeViewModel = new ViewModelProvider(MainActivity.this).get(HomeViewModel.class);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        Toolbar toolbar = binding.toolBar;
        toolbar.inflateMenu(R.menu.toolbar_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.add:
                        MyDialog dialog = new MyDialog(MainActivity.this);
                        dialog.setYesOnclickListener(new MyDialog.onYesOnclickListener() {
                            @Override
                            public void onYesClick() {
                                HomeViewModel homeViewModel = new ViewModelProvider(MainActivity.this,new ViewModelProvider.AndroidViewModelFactory(getApplication())).get(HomeViewModel.class);
                                EditText editTittle = dialog.findViewById(R.id.task_title);
                                String title = editTittle.getText().toString();
                                if (title.equals("")){
                                    Toast.makeText(MainActivity.this, "输入事项不能为空",Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                EditText editDuration = dialog.findViewById(R.id.task_duration);
                                String duration = editDuration.getText().toString();
                                if (duration.equals("") || duration.equals("0")){
                                    Toast.makeText(MainActivity.this, "时长范围为1-300",Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                Task task = new Task(title, duration);
                                homeViewModel.insertToUndo(task);
                                dialog.dismiss();
                            }
                        });
                        dialog.setNoOnclickListener(new MyDialog.onNoOnclickListener() {
                            @Override
                            public void onNoClick() {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                        break;
                    default:

                }
                return true;
            }
        });
        //setSupportActionBar(toolbar);
//        setActionBar(toolbar);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
      //  NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(
                toolbar, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
        navController.addOnDestinationChangedListener(new NavController.OnDestinationChangedListener() {
            @Override
            public void onDestinationChanged(@NonNull @NotNull NavController controller, @NonNull @NotNull NavDestination destination, @Nullable @org.jetbrains.annotations.Nullable Bundle arguments) {
                Menu menu = toolbar.getMenu();
                if(destination.getId() == R.id.navigation_home){
                    menu.findItem(R.id.add).setVisible(true).setCheckable(true);
                    toolbar.setLogo(R.drawable.ic_todo_logo);
                    menu.findItem(R.id.add).setVisible(true).setCheckable(true);
                }
                else if(destination.getId() == R.id.navigation_dashboard){
                    toolbar.setLogo(R.drawable.ic_lock_logo);
                    menu.findItem(R.id.add).setVisible(false).setCheckable(false);
                }
                else {
                    toolbar.setLogo(R.drawable.ic_statistics_logo);
                    menu.findItem(R.id.add).setVisible(false).setCheckable(false);
                }
            }
        });
        
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
package com.akitektuo.smartlist.activity.light;

import android.Manifest;
import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.inputmethod.InputMethodManager;

import com.akitektuo.smartlist.R;
import com.akitektuo.smartlist.adapter.ViewPagerAdapter;
import com.akitektuo.smartlist.communicator.FileGenerationNotifier;
import com.akitektuo.smartlist.communicator.ImportNotifier;
import com.akitektuo.smartlist.database.DatabaseHelper;
import com.akitektuo.smartlist.fragment.FolderFragment;
import com.akitektuo.smartlist.fragment.ListFragment;
import com.akitektuo.smartlist.fragment.SettingsFragment;
import com.akitektuo.smartlist.fragment.StatsFragment;
import com.akitektuo.smartlist.fragment.TuneFragment;

public class MainActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener, FileGenerationNotifier, ImportNotifier {

    private ViewPager pager;
    private TabLayout tab;
    private FolderFragment folderFragment;
    private ListFragment listFragment;
    private StatsFragment statsFragment;
    private DatabaseHelper database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pager = findViewById(R.id.container_main);
        setupViewPager();

        tab = findViewById(R.id.tab_main);
        tab.setupWithViewPager(pager);
        tab.addOnTabSelectedListener(this);
        setupTabIcons();

        database = new DatabaseHelper(this);
        if (database.getCategoryId() == 0) {
            database.addCategory("Other");
            database.addCategory("Food");
            database.addCategory("Fees");
            database.addCategory("Utilities");
            database.addCategory("Transportation");
            database.addCategory("Clothes");
            database.addCategory("Entertainment");
        }

        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
    }

    private void setupViewPager() {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        listFragment = new ListFragment();
        adapter.addFragment(listFragment);
        statsFragment = new StatsFragment();
        adapter.addFragment(statsFragment);
        folderFragment = new FolderFragment();
        adapter.addFragment(folderFragment);
        adapter.addFragment(new TuneFragment());
        adapter.addFragment(new SettingsFragment());
        pager.setAdapter(adapter);
    }

    private void setupTabIcons() {
        tab.getTabAt(0).setIcon(R.drawable.light_list_selected);
        tab.getTabAt(1).setIcon(R.drawable.light_chart);
        tab.getTabAt(2).setIcon(R.drawable.light_folder);
        tab.getTabAt(3).setIcon(R.drawable.light_tune);
        tab.getTabAt(4).setIcon(R.drawable.light_settings);
    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        hideKeyboard();
        switch (tab.getPosition()) {
            case 0:
                tab.setIcon(R.drawable.light_list_selected);
                break;
            case 1:
                tab.setIcon(R.drawable.light_chart_selected);
                statsFragment.animatePie();
                break;
            case 2:
                tab.setIcon(R.drawable.light_folder_selected);
                break;
            case 3:
                tab.setIcon(R.drawable.light_tune_selected);
                break;
            case 4:
                tab.setIcon(R.drawable.light_settings_selected);
                break;
        }
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        switch (tab.getPosition()) {
            case 0:
                tab.setIcon(R.drawable.light_list);
                break;
            case 1:
                tab.setIcon(R.drawable.light_chart);
                break;
            case 2:
                tab.setIcon(R.drawable.light_folder);
                break;
            case 3:
                tab.setIcon(R.drawable.light_tune);
                break;
            case 4:
                tab.setIcon(R.drawable.light_settings);
                break;
        }
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    private void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    @Override
    public void change() {
//        folderFragment.scanItems();
        tab.getTabAt(2).select();
    }

    @Override
    public void refreshList() {
//        listFragment.populateList();
        tab.getTabAt(0).select();
    }
}

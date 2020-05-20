package com.sliebald.cula;

import android.os.Bundle;
import android.view.Menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.iconics.IconicsDrawable;
import com.sliebald.cula.data.database.Entities.LanguageEntry;
import com.sliebald.cula.databinding.MainActivityBinding;
import com.sliebald.cula.utilities.KeyboardUtils;
import com.sliebald.cula.utilities.PreferenceUtils;

import java.util.HashSet;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    /**
     * Tag for logging.
     */
    private static final String TAG = MainActivity.class.getSimpleName();

    private NavController mNavController;

    private AppBarConfiguration mAppBarConfiguration;

    private MainActivityBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.main_activity);
        Toolbar toolbar = mBinding.toolbar;
        MainViewModel mViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        //check whether there is a language set as active.
        mViewModel.getActiveLanguage().observe(this, this::checkActiveLanguage);

        // setup drawer icons
        Menu menu = mBinding.navView.getMenu();
        menu.findItem(R.id.startTraining_dest).setIcon(new IconicsDrawable(this).icon(FontAwesome.Icon.faw_pen));
        menu.findItem(R.id.library_dest).setIcon(new IconicsDrawable(this).icon(FontAwesome.Icon.faw_book));
        menu.findItem(R.id.lessons_dest).setIcon(new IconicsDrawable(this).icon(FontAwesome.Icon.faw_book_open));
        menu.findItem(R.id.statistics_dest).setIcon(new IconicsDrawable(this).icon(FontAwesome.Icon.faw_chart_pie));
        menu.findItem(R.id.settings_dest).setIcon(new IconicsDrawable(this).icon(FontAwesome.Icon.faw_cogs));
        menu.findItem(R.id.about_dest).setIcon(new IconicsDrawable(this).icon(FontAwesome.Icon.faw_info));
        //setup navigation top level destinations
        mNavController = Navigation.findNavController(this, R.id.nav_host_fragment);
        Set<Integer> tlds = new HashSet<>();
        tlds.add(R.id.startTraining_dest);
        tlds.add(R.id.library_dest);
        tlds.add(R.id.lessons_dest);
        tlds.add(R.id.statistics_dest);
        tlds.add(R.id.settings_dest);
        tlds.add(R.id.about_dest);

        // Create the AppBarConfiguration for the drawer
        mAppBarConfiguration = new AppBarConfiguration.Builder(tlds).setDrawerLayout(mBinding.drawerLayout).build();

        //setup navigation
        setSupportActionBar(toolbar);
        NavigationUI.setupActionBarWithNavController(this, mNavController, mAppBarConfiguration);
        NavigationView navDrawer = mBinding.navView;
        NavigationUI.setupWithNavController(navDrawer, mNavController);

    }

    /**
     * Checks that the active language is set correctly in the sharedPreferences. Also makes sure
     * that if no language is selected the user is forwarded to the settings and given a hint.
     *
     * @param languageEntry The currently active language in the database. If null none is set to
     *                      active.
     */
    private void checkActiveLanguage(LanguageEntry languageEntry) {
        String preferencesLanguage =
                PreferenceUtils.getActiveLanguage();
        if (languageEntry == null) {
            PreferenceUtils.setActiveLanguage("");
        } else if (!languageEntry.getLanguage().equals(preferencesLanguage)) {
            PreferenceUtils.setActiveLanguage(languageEntry.getLanguage());
        }
        // reflect the active language to the toolbar subtitle
        if (languageEntry != null)
            mBinding.toolbar.setSubtitle(languageEntry.getLanguage());
    }

    @Override
    public boolean onSupportNavigateUp() {
        // hide keyboard whenever back is pressed.
        KeyboardUtils.hideKeyboard(this, mBinding.getRoot());
        return NavigationUI.navigateUp(mNavController, mAppBarConfiguration);
    }
}

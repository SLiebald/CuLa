package com.sliebald.cula

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.mikepenz.fontawesome_typeface_library.FontAwesome
import com.mikepenz.iconics.IconicsDrawable
import com.sliebald.cula.data.database.entities.LanguageEntry
import com.sliebald.cula.databinding.MainActivityBinding
import com.sliebald.cula.utilities.KeyboardUtils
import com.sliebald.cula.utilities.PreferenceUtils

class MainActivity : AppCompatActivity() {
    private lateinit var mNavController: NavController
    private lateinit var mAppBarConfiguration: AppBarConfiguration
    private lateinit var mBinding: MainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.main_activity)
        val toolbar = mBinding.toolbar
        val mViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        //check whether there is a language set as active.
        mViewModel.activeLanguage.observe(this, Observer { languageEntry: LanguageEntry? -> checkActiveLanguage(languageEntry) })

        // setup drawer icons
        val menu = mBinding.navView.menu
        menu.findItem(R.id.startTraining_dest).icon = IconicsDrawable(this).icon(FontAwesome.Icon.faw_pen)
        menu.findItem(R.id.library_dest).icon = IconicsDrawable(this).icon(FontAwesome.Icon.faw_book)
        menu.findItem(R.id.lessons_dest).icon = IconicsDrawable(this).icon(FontAwesome.Icon.faw_book_open)
        menu.findItem(R.id.statistics_dest).icon = IconicsDrawable(this).icon(FontAwesome.Icon.faw_chart_pie)
        menu.findItem(R.id.settings_dest).icon = IconicsDrawable(this).icon(FontAwesome.Icon.faw_cogs)
        menu.findItem(R.id.about_dest).icon = IconicsDrawable(this).icon(FontAwesome.Icon.faw_info)

        //setup navigation top level destinations
        mNavController = Navigation.findNavController(this, R.id.nav_host_fragment)
        val tlds: Set<Int> = hashSetOf(
                R.id.startTraining_dest,
                R.id.library_dest,
                R.id.lessons_dest,
                R.id.statistics_dest,
                R.id.settings_dest,
                R.id.about_dest
        )

        // Create the AppBarConfiguration for the drawer
        mAppBarConfiguration = AppBarConfiguration.Builder(tlds).setDrawerLayout(mBinding.drawerLayout).build()

        //setup navigation
        setSupportActionBar(toolbar)
        NavigationUI.setupActionBarWithNavController(this, mNavController, mAppBarConfiguration)
        val navDrawer = mBinding.navView
        NavigationUI.setupWithNavController(navDrawer, mNavController)
    }

    /**
     * Checks that the active language is set correctly in the sharedPreferences. Also makes sure
     * that if no language is selected the user is forwarded to the settings and given a hint.
     *
     * @param languageEntry The currently active language in the database. If null none is set to
     * active.
     */
    private fun checkActiveLanguage(languageEntry: LanguageEntry?) {
        PreferenceUtils.setActiveLanguage(languageEntry?.language ?: "")
        // reflect the active language to the toolbar subtitle
        mBinding.toolbar.subtitle = languageEntry?.language ?: ""
    }

    override fun onSupportNavigateUp(): Boolean {
        // hide keyboard whenever back is pressed.
        KeyboardUtils.hideKeyboard(this, mBinding.root)
        return NavigationUI.navigateUp(mNavController, mAppBarConfiguration)
    }
}
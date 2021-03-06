package com.example.paulina.wisehome.base

import android.content.Intent
import android.support.annotation.LayoutRes
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.widget.Toolbar
import android.view.Gravity
import android.view.View
import com.example.paulina.wisehome.R
import com.example.paulina.wisehome.addaccount.AddAccountActivity
import com.example.paulina.wisehome.addroom.AddRoomActivity
import com.example.paulina.wisehome.changepassword.ChangePasswordActivity
import com.example.paulina.wisehome.model.businessobjects.AccountType
import com.example.paulina.wisehome.model.businessobjects.NavDrawerItemType
import com.example.paulina.wisehome.model.database.Database
import com.example.paulina.wisehome.rooms.RoomsActivity
import com.example.paulina.wisehome.unconfigdevices.UnconfigDevicesActivity
import com.patientcard.access.AccessActivity
import com.yalantis.jellytoolbar.widget.JellyToolbar
import kotlinx.android.synthetic.main.activity_rooms.*
import kotlinx.android.synthetic.main.toolbar.*


abstract class NavDrawerActivity : BaseActivity() {

    protected var shouldCreateNavDrawer: Boolean = false
    private var drawerToggle: ActionBarDrawerToggle? = null
    private var closeable: Boolean = false
    private var isInitializing = true
    private lateinit var drawerAdapter: NavDrawerAdapter

    override fun setContentView(@LayoutRes layoutResID: Int) {
        super.setContentView(layoutResID)
        shouldCreateNavDrawer = true
        onCreateDrawer()
        isInitializing = false
    }

    protected fun onCreateDrawer() {
        setSupportActionBar(toolbar.toolbar)
        getSupportActionBar()?.setDisplayShowTitleEnabled(false)

        if (shouldCreateNavDrawer) {
            setDrawerToggle()
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            setNavigationIcon(toolbar.toolbar as Toolbar)
            setupDrawerList()
        } else {
            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        }
    }

    private fun setDrawerToggle() {
        var drawer = drawerToggle
        drawer = object : ActionBarDrawerToggle(this, drawerLayout, 0, 0) {
            override fun onDrawerClosed(view: View?) {
                //do nothing
            }

            override fun onDrawerOpened(drawerView: View?) {
                //do nothing
            }
        }
        drawerLayout.addDrawerListener(drawer)
        drawer.setDrawerIndicatorEnabled(false)
    }

    private fun setNavigationIcon(toolbar: Toolbar) {
        toolbar.setNavigationIcon(if (closeable) R.mipmap.close else R.drawable.menu)
        toolbar.setNavigationOnClickListener({ v -> onNavIconClick() })
    }

    private fun onNavIconClick() {
        if (closeable) {
            startActivity(Intent(this, RoomsActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP))
            overridePendingTransition(R.anim.no_animation, R.anim.slide_out_down)
        } else {
            drawerLayout.openDrawer(Gravity.LEFT)
        }
    }

    protected fun setupToolbar(toolbar: JellyToolbar, title: String) {
        CustomToolbar().setupToolbar(toolbar, title)
    }

    private fun setupDrawerList() {
        val header = layoutInflater.inflate(R.layout.header_nav_drawer, null)
        drawerList.addHeaderView(header, null, false)
        drawerAdapter = NavDrawerAdapter(this)
        drawerAdapter.items = createNavDrawerItems()
        drawerList.adapter = drawerAdapter
        drawerList.setOnItemClickListener { parent, view, position, id -> handleDrawerItemClick(position) }
    }

    private fun handleDrawerItemClick(position: Int) {
        val navDrawerItems = drawerAdapter.items
        val clickedItem = navDrawerItems.get(position - 1)
        when (clickedItem) {
            NavDrawerItemType.MY_ROOMS -> openActivityFadeInFadeOut(RoomsActivity::class.java)
            NavDrawerItemType.ADD_ROOM -> openActivityFadeInFadeOut(AddRoomActivity::class.java)
            NavDrawerItemType.ADD_DEVICE -> openActivityFadeInFadeOut(UnconfigDevicesActivity::class.java)
            NavDrawerItemType.LOGOUT -> {
                openActivityFadeInFadeOut(AccessActivity::class.java)
            }
            NavDrawerItemType.ADD_ACCOUNT -> openActivityFadeInFadeOut(AddAccountActivity::class.java)
            NavDrawerItemType.CHANGE_PASSWORD -> openActivityFadeInFadeOut(ChangePasswordActivity::class.java)
        }
        drawerLayout.closeDrawer(Gravity.LEFT)
    }

    private fun openActivityFadeInFadeOut(classTo: Class<*>) {
        startActivity(Intent(this, classTo)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP))
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    private fun createNavDrawerItems(): MutableList<NavDrawerItemType> {
        val items: MutableList<NavDrawerItemType> = mutableListOf()
        items.add(NavDrawerItemType.MY_ROOMS)
        if (Database().getAccountType() == AccountType.ADMIN) {
            items.add(NavDrawerItemType.ADD_ROOM)
            items.add(NavDrawerItemType.ADD_DEVICE)
            items.add(NavDrawerItemType.ADD_ACCOUNT)
        }
        items.add(NavDrawerItemType.CHANGE_PASSWORD)
        items.add(NavDrawerItemType.LOGOUT)
        return items
    }
}
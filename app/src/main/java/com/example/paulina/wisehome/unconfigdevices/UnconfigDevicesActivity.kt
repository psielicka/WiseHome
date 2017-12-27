package com.example.paulina.wisehome.unconfigdevices

import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.example.paulina.wisehome.R
import com.example.paulina.wisehome.base.BasePresenter
import com.example.paulina.wisehome.base.NavDrawerActivity
import com.example.paulina.wisehome.model.businessobjects.DeviceType
import com.example.paulina.wisehome.model.transportobjects.UnconfigDevice
import com.example.paulina.wisehome.model.utils.ResUtil
import com.example.paulina.wisehome.rooms.RoomsActivity
import easymvp.annotation.ActivityView
import easymvp.annotation.Presenter
import kotlinx.android.synthetic.main.activity_unconfig_devices.*
import kotlinx.android.synthetic.main.check_unconfig_device_dialog.*
import kotlinx.android.synthetic.main.toolbar.*


@ActivityView(layout = R.layout.activity_unconfig_devices, presenter = UnconfigDevicesPresenterImpl::class)
class UnconfigDevicesActivity : NavDrawerActivity(), UnconfigDevicesView {

    @Presenter
    lateinit var presenter: UnconfigDevicesPresenter

    private val devicesAdapter: UnconfigDevicesAdapter = UnconfigDevicesAdapter(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupToolbar(toolbar, " 123")
        setupDeviceList()
        setupDialog()
    }

    override fun providePresenter(): BasePresenter? {
        return presenter
    }

    private fun setupDeviceList() {
        unconfigDevicesRecyclerView.layoutManager = LinearLayoutManager(this)
        unconfigDevicesRecyclerView.adapter = devicesAdapter
    }

    override fun setDevices(devices: List<UnconfigDevice>) {
        devicesAdapter.devices = devices
    }

    override fun onDeviceClick(deviceType: DeviceType) {
        if (deviceType == DeviceType.LIGHTS) {
            higlightInformation.text = ResUtil.getString(R.string.selected_light_was_turned_on)
        } else {
            higlightInformation.text = ResUtil.getString(R.string.red_diode_on_selected_device_is_blinking)
        }
        checkUnconfigDeviceDialog.visibility = View.VISIBLE
    }

    private fun setupDialog() {
        cancelButton.setOnClickListener(({ v -> checkUnconfigDeviceDialog.visibility = View.GONE }))
        //TODO: remember to turn off light or diode after cancel click
        continueButton.setOnClickListener(({ view -> }))
    }

    override fun onBackPressed() {
        if (checkUnconfigDeviceDialog.visibility == View.VISIBLE) {
            checkUnconfigDeviceDialog.visibility = View.GONE
        } else {
            startActivity(Intent(this, RoomsActivity::class.java)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP))
        }
    }
}
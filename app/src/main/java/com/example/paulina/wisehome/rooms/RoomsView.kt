package com.example.paulina.wisehome.rooms

import android.view.View
import com.example.paulina.wisehome.base.BaseView
import com.example.paulina.wisehome.model.businessobjects.Room


interface RoomsView: BaseView {
    fun setRooms(rooms : List<Room>)

    fun onLightsClick()

    fun onBlindsClick()

    fun onAlarmSensorsClick()

    fun onWeatherSensorsClick()

    fun devicesLayoutExpandCollapse(isExpanded: Boolean, viewToAnim: View)

    fun arrowAnimation(isExpanded: Boolean, arrowUp: View, arrowDown : View)
}
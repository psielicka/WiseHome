package com.example.paulina.wisehome.weather

import android.content.Intent
import com.example.paulina.wisehome.R
import com.example.paulina.wisehome.base.BaseAbstractPresenter
import com.example.paulina.wisehome.base.IntentKeys
import com.example.paulina.wisehome.model.businessobjects.NewWeather
import com.example.paulina.wisehome.model.utils.ResUtil
import com.example.paulina.wisehome.service.ServiceManager
import com.example.paulina.wisehome.service.receivers.GetWeatherReciever

class WeatherPresenterImpl : BaseAbstractPresenter<WeatherView>(), WeatherPresenter, GetWeatherReciever {

    private val presentationModel: WeatherModel by lazy { WeatherModel() }

    override fun initExtras(intent: Intent) {
        presentationModel.roomId = intent.getStringExtra(IntentKeys.ROOM_ID)
        presentationModel.roomName = intent.getStringExtra(IntentKeys.ROOM_NAME)
    }

    override fun onViewAttached(view: WeatherView?) {
        super.onViewAttached(view)
        getWeather()
        view?.setupRoomName(presentationModel.roomName)
    }

    fun getWeather() {
        view?.startProgressDialog(ResUtil.getString(R.string.progress_loading_text))
        ServiceManager.getWeather(this, presentationModel.roomId)
    }

    override fun onGetWeatherSuccess(weather: NewWeather) {
        view?.stopProgressDialog()
        view?.setupWeatherState(weather)
        view?.setupCharts(weather.historicMeasurements)
    }

    override fun onGetWeatherError() {
        view?.stopProgressDialog()
    }
}

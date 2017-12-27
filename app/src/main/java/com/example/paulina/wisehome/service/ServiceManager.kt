package com.example.paulina.wisehome.service

import android.widget.Toast
import com.example.paulina.wisehome.R
import com.example.paulina.wisehome.base.ApplicationContext
import com.example.paulina.wisehome.model.businessobjects.ResponseErrorMessage
import com.example.paulina.wisehome.model.transportobjects.Lights
import com.example.paulina.wisehome.model.transportobjects.Room
import com.example.paulina.wisehome.model.transportobjects.UnconfigDevice
import com.example.paulina.wisehome.model.utils.ResUtil
import com.example.paulina.wisehome.model.utils.ToastUtil
import com.example.paulina.wisehome.service.receivers.GetLightsReciever
import com.example.paulina.wisehome.service.receivers.GetRoomsReciever
import com.example.paulina.wisehome.service.receivers.GetUnconfigDevicesReciever
import com.google.gson.Gson
import retrofit2.HttpException
import rx.Observable
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.functions.Action0
import rx.functions.Action1
import rx.schedulers.Schedulers
import timber.log.Timber
import java.lang.Exception

object ServiceManager {

    fun getRooms(receiver: GetRoomsReciever) {
        setupRequest(ServiceProvider
                .roomsService
                .getRooms(),
                Action1 {
                    receiver.onGetRoomsSuccess(it as List<Room>) },
                Action1 {
                    handleError(it)
                    receiver.onGetRoomsError()
                },
                Action0 { Timber.e("OnCompleted") })
    }

    fun getUnconfigDevices(receiver: GetUnconfigDevicesReciever) {
        setupRequest(ServiceProvider
                .unconfigDeviceService
                .getUnconfigDevices(),
                Action1 {
                    receiver.onGetUnconfigDevicesSucces(it as List<UnconfigDevice>) },
                Action1 {
                    handleError(it)
                    receiver.onGetUnconfigDevicesError()
                },
                Action0 { Timber.e("OnCompleted") })
    }


    fun getights(receiver: GetLightsReciever, roomId : String) {
        setupRequest(ServiceProvider
                .lightsService
                .getLights(roomId),
                Action1 {
                    receiver.onGetLightsSuccess(it as Lights) },
                Action1 {
                    handleError(it)
                    receiver.onGetLightsError()
                },
                Action0 { Timber.e("OnCompleted") })
    }

    private fun setupRequest(observable: Observable<*>, onNext: Action1<Any>, onError: Action1<Throwable>, onCompleted: Action0): Subscription {
        return observable
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(onNext, onError, onCompleted)
    }

    fun handleError(error: Throwable) {
        val msg = getResponseMessage(error)

        if (error is HttpException) {
            val context = ApplicationContext.appContext

            if (error.code() != 401 && error.code() != 404) {
                if (error.code() == 409 && msg != null && !msg.isEmpty()) {
                    ToastUtil.show(context, msg, Toast.LENGTH_LONG)
                } else {
                    ToastUtil.show(context, ResUtil.getString(R.string.sth_wrong_check_internet_connection), Toast.LENGTH_LONG)
                }
            } else if (error.code() == 401) {
                ToastUtil.show(context, ResUtil.getString(R.string.authorization_error), Toast.LENGTH_LONG)
            }
        }
    }

    private fun getResponseMessage(error: Throwable): String? {
        var msg = ""
        if (error is HttpException && error.response() != null && error.response().errorBody() != null) {
            try {
                val response = error.response()
                msg = response.errorBody()?.string() ?: ""
                msg = Gson().fromJson(msg, ResponseErrorMessage::class.java).message
            } catch (e: Exception) {
                Timber.e("unable to get error message in ServiceManager::handleError()")
            }
        }
        return msg
    }
}
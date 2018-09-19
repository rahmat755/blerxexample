package test.mircod.com.test.model

import android.content.Context
import com.polidea.rxandroidble2.RxBleClient
import com.polidea.rxandroidble2.RxBleConnection
import com.polidea.rxandroidble2.RxBleDevice
import com.polidea.rxandroidble2.RxBleDeviceServices
import com.polidea.rxandroidble2.scan.ScanFilter
import com.polidea.rxandroidble2.scan.ScanResult
import com.polidea.rxandroidble2.scan.ScanSettings
import io.reactivex.Observable
import javax.inject.Inject
import javax.inject.Singleton

class BluetoothModule @Inject constructor(var ctx: Context) {

    fun getDevicesList(): Observable<ScanResult> {
        return bleClient!!.scanBleDevices(
                ScanSettings.Builder().build(),
                ScanFilter.Builder().build()
        )
    }

    fun getDevice(macAddress: String): RxBleDevice {
        rxBleDevice = bleClient?.getBleDevice(macAddress)
        return rxBleDevice!!
    }

    fun connectToDevice(): Observable<RxBleConnection> {
        rxBleConnection = rxBleDevice!!.establishConnection(false)
        return rxBleConnection!!
    }

    fun getServices(): Observable<RxBleDeviceServices> {
        return rxBleConnection!!.flatMapSingle {
            it.discoverServices()
        }
    }

    fun getBleClient(): RxBleClient {
        return if (bleClient != null) {
            bleClient!!
        } else {
            RxBleClient.create(ctx)
        }
    }

    companion object {
        var macAddress: String? = null
        var serviceUUID: String? = null
        var rxBleDevice: RxBleDevice? = null
        var rxBleConnection: Observable<RxBleConnection>? = null
        var rxBleDeviceServices: Observable<RxBleDeviceServices>? = null
        private var bleClient: RxBleClient? = null
    }
}
package test.mircod.com.test.view

import android.bluetooth.BluetoothGattCharacteristic
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.polidea.rxandroidble2.RxBleClient
import com.polidea.rxandroidble2.RxBleConnection
import com.polidea.rxandroidble2.RxBleDevice
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_characteristic.*
import test.mircod.com.test.App
import test.mircod.com.test.R
import test.mircod.com.test.adapter.CharacteristicsAdapter
import test.mircod.com.test.model.BLECharacteristics
import test.mircod.com.test.model.BluetoothModule
import java.util.*
import javax.inject.Inject

class CharacteristicActivity : AppCompatActivity(), CharacteristicsAdapter.OnItemClickListener {
    override fun onClick(item: BLECharacteristics) {
        rxbleConnection!!.setupNotification(UUID.fromString(item.UUID))
                .subscribe(
                        { obs ->
                            notifyObserver = obs
                            obs.observeOn(AndroidSchedulers.mainThread())?.subscribeOn(Schedulers.io())?.subscribe {
                                if (it != null)
                                    mAdapter.updateData(BLECharacteristics(UUID = item.UUID, data = it.contentToString()))
                            }
                        },
                        { error ->
                            Log.d("Error", error.localizedMessage)
                        }
                )
    }

    @Inject
    lateinit var bleModule: BluetoothModule
    private var rxbleConnection: RxBleConnection? = null
    private var notifyObserver: Observable<ByteArray>? = null
    var disposable: CompositeDisposable? = null
    private lateinit var serviceUUID: String
    private lateinit var device: RxBleDevice
    private lateinit var macAddress: String
    private lateinit var rxBleClient: RxBleClient
    lateinit var mAdapter: CharacteristicsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_characteristic)
        disposable = CompositeDisposable()
        App.appComponent.injectChar(this)
        serviceUUID = intent.getStringExtra("characteristicsUUID")
        macAddress = intent.getStringExtra("macAddress")
        rxBleClient = bleModule.getBleClient()
        mAdapter = CharacteristicsAdapter(this)
        characteristics_recyclerview.apply {
            layoutManager = LinearLayoutManager(this@CharacteristicActivity)
            adapter = mAdapter
        }
        connectToDevice()
    }

    override fun onPause() {
        super.onPause()
        if (isScanning())
            disposable?.dispose()
    }

    private fun isScanning() = disposable != null

    private fun connectToDevice() {
        device = rxBleClient.getBleDevice(macAddress)
        disposable?.add(device.establishConnection(true)
                .flatMapSingle {
                    rxbleConnection = it
                    return@flatMapSingle it.discoverServices()
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doFinally { disposable = null }
                .subscribe(
                        {
                            for (service in it.bluetoothGattServices) {
                                if (service.uuid.toString() == serviceUUID)
                                    for (servchar in service.characteristics) {
                                        if (servchar.properties == BluetoothGattCharacteristic.PROPERTY_NOTIFY)
                                            mAdapter.addItems(BLECharacteristics(UUID = servchar.uuid.toString()))
                                    }
                            }
                        },
                        { throwable ->
                            Log.d("ERROR", throwable.localizedMessage)
                        },
                        {
                            disposable?.dispose()
                        }
                ))
    }
}

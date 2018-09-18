package test.mircod.com.test.view

import android.bluetooth.BluetoothGattCharacteristic
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.widget.Toast
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
import java.util.*

class CharacteristicActivity : AppCompatActivity(), CharacteristicsAdapter.OnItemClickListener {
    override fun onClick(item: BLECharacteristics) {
            rxbleConnection!!.setupNotification(UUID.fromString(item.UUID))
                    .subscribe(
                            {
                                notifyObserver = it
                                it.observeOn(AndroidSchedulers.mainThread())?.subscribeOn(Schedulers.io())?.subscribe {
                                    if (it != null)
                                        mAdapter.updateData(BLECharacteristics(UUID = item.UUID, data = it.contentToString()))
                                }
                            },
                            { error ->
                                Log.d("Error", error.localizedMessage)
                            }
                    )
    }

    private var rxbleConnection: RxBleConnection? = null
    private var notifyObserver: Observable<ByteArray>? = null
    lateinit var disposable: CompositeDisposable
    private lateinit var serviceUUID: String
    private lateinit var device: RxBleDevice
    private lateinit var macAddress: String
    private lateinit var rxBleClient: RxBleClient
    lateinit var mAdapter: CharacteristicsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_characteristic)
        disposable = CompositeDisposable()
        serviceUUID = intent.getStringExtra("characteristicsUUID")
        macAddress = intent.getStringExtra("macAddress")
        rxBleClient = App.bleClient
        mAdapter = CharacteristicsAdapter(this)
        characteristics_recyclerview.apply {
            layoutManager = LinearLayoutManager(this@CharacteristicActivity)
            adapter = mAdapter
        }
        connectToDevice()
    }

    override fun onPause() {
        super.onPause()
        if (!disposable.isDisposed)
            disposable.dispose()
    }

    private fun connectToDevice() {
        device = rxBleClient.getBleDevice(macAddress)
        disposable.add(device.establishConnection(false)
                .flatMapSingle {
                    rxbleConnection = it
                    return@flatMapSingle it.discoverServices()
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(
                        {
                            for (service in it.bluetoothGattServices) {
                                for (servchar in service.characteristics) {
                                    mAdapter.addItems(BLECharacteristics(UUID = servchar.uuid.toString()))
                                }
                            }
                        },
                        { throwable ->
                            Log.d("ERROR", throwable.localizedMessage)
                        },
                        {

                        }
                ))
    }
}

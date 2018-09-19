package test.mircod.com.test.view

import android.bluetooth.BluetoothGattService
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.polidea.rxandroidble2.RxBleClient
import com.polidea.rxandroidble2.RxBleDevice
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_detail.*
import test.mircod.com.test.App
import test.mircod.com.test.R
import test.mircod.com.test.adapter.BluetoothServicesAdapter
import test.mircod.com.test.model.BluetoothModule
import javax.inject.Inject

class ServiceActivity : AppCompatActivity(), BluetoothServicesAdapter.OnItemClickListener {
    override fun onClick(item: BluetoothGattService) {
        val intent = Intent(this, CharacteristicActivity::class.java)
        intent.putExtra("macAddress", macAddress)
        intent.putExtra("characteristicsUUID", item.uuid.toString())
        startActivity(intent)
    }

    @Inject
    lateinit var bleModule: BluetoothModule
    var device: RxBleDevice? = null
    private lateinit var rxBleClient: RxBleClient
    var disposable: CompositeDisposable? = CompositeDisposable()
    var mAdapter = BluetoothServicesAdapter(this)
    lateinit var macAddress: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        disposable = CompositeDisposable()
        App.appComponent.injectServ(this)
        macAddress = intent.getStringExtra("macAddress")
        rxBleClient = bleModule.getBleClient()
        detail_recycler_view.apply {
            layoutManager = LinearLayoutManager(this@ServiceActivity)
            adapter = mAdapter
        }
        loadServices()
    }

    private fun isScanning() = disposable != null

    override fun onPause() {
        super.onPause()
        if (isScanning())
            disposable?.dispose()
        disposable = null
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable = null
    }

    private fun loadServices() {
        device = rxBleClient.getBleDevice(macAddress)
        disposable?.add(device!!.establishConnection(false)
                .flatMapSingle {
                    return@flatMapSingle it.discoverServices()
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .doFinally { disposable = null }
                .subscribe(
                        {
                            for (service in it.bluetoothGattServices) {
                                mAdapter.addItems(service)
                            }
                        },
                        { throwable ->
                            Log.d("ERROR", throwable.localizedMessage)
                        }, {
                    disposable?.dispose()
                }
                )
        )
    }
}

package test.mircod.com.test.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.polidea.rxandroidble2.RxBleClient
import com.polidea.rxandroidble2.scan.ScanFilter
import com.polidea.rxandroidble2.scan.ScanResult
import com.polidea.rxandroidble2.scan.ScanSettings
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import pub.devrel.easypermissions.EasyPermissions
import test.mircod.com.test.adapter.ScanResAdapter
import test.mircod.com.test.App
import test.mircod.com.test.R
import test.mircod.com.test.model.BluetoothModule
import javax.inject.Inject
import android.bluetooth.BluetoothAdapter


const val REQUEST_ENABLE_BT = 1

class MainActivity : AppCompatActivity(), ScanResAdapter.OnItemClickListener {


    private val perms = arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.BLUETOOTH_ADMIN,
            Manifest.permission.BLUETOOTH)
    @Inject
    lateinit var bleModule: BluetoothModule
    var macAddress: String? = null
    private lateinit var rxBleClient: RxBleClient
    var mAdapter: ScanResAdapter? = null
    var disposable: CompositeDisposable? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        disposable = CompositeDisposable()
        setContentView(R.layout.activity_main)
        App.appComponent.injectMain(this)
        mAdapter = ScanResAdapter(this)
        bluetooth_recycler.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }
        val mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (mBluetoothAdapter != null) {
            if (!mBluetoothAdapter.isEnabled) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            } else {
                startSearch()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_ENABLE_BT)
            if (resultCode == Activity.RESULT_OK)
                startSearch()
            else
                finish()
    }

    private fun startSearch() {
        if (EasyPermissions.hasPermissions(this, *perms)) {
            rxBleClient = bleModule.getBleClient()
            disposable?.add(rxBleClient.scanBleDevices(
                    ScanSettings.Builder().build(),
                    ScanFilter.Builder().build()
            )
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .doFinally { disposable = null }
                    .subscribe(
                            {
                                mAdapter?.addItems(it)
                            }, {
                        it.printStackTrace()
                        Log.d("Error", it.localizedMessage)
                    },{
                        disposable?.dispose()
                    }
                    ))
        } else {
            EasyPermissions.requestPermissions(this, "",
                    0, *perms)
        }
    }

    override fun onClick(item: ScanResult) {
        val intent = Intent(this, ServiceActivity::class.java)
        macAddress = item.bleDevice.macAddress
        intent.putExtra("macAddress", macAddress)
        startActivity(intent)
    }

    override fun onPause() {
        super.onPause()
        if (isScanning())
            disposable?.dispose()
    }

    private fun isScanning() = disposable != null

    override fun onDestroy() {
        super.onDestroy()
        disposable = null
    }

}

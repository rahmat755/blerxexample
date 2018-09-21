package test.mircod.com.bluetoothnaive

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import android.content.Intent
import android.os.Handler
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_main.*
import test.mircod.com.bluetoothnaive.adater.scan_adapter.ScanResultAdapter


class DeviceScanActivity : AppCompatActivity(), ScanResultAdapter.OnItemClickListener {
    override fun onClick(item: BluetoothDevice) {
        val intent = Intent(this, DeviceDetailActivity::class.java)
        intent.putExtra("ITEM_NAME",item.name)
        intent.putExtra("ITEM_MAC",item.address)
        startActivity(intent)
    }

    private val REQUEST_ENABLE_BT = 1
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var mScanning: Boolean = false
    private val SCAN_PERIOD: Long = 10000
    private var mHandler: Handler? = null
    lateinit var mAdapter: ScanResultAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mAdapter = ScanResultAdapter(this)
        mHandler = Handler()
        scan_result_list.apply {
            adapter = mAdapter
            layoutManager = LinearLayoutManager(this@DeviceScanActivity)
        }
        if (!packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, "BLE not supported", Toast.LENGTH_SHORT).show()
            finish()
        }
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        mBluetoothAdapter = bluetoothManager.adapter
        if (mBluetoothAdapter == null || !mBluetoothAdapter!!.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }
        scan(true)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_ENABLE_BT)
            if (resultCode == Activity.RESULT_OK)
                scan(true)
    }

    private fun scan(enable: Boolean) {
        if (enable) {
            mHandler?.postDelayed({
                mScanning = false
                mBluetoothAdapter?.stopLeScan(mLeScanCallback)
            }, SCAN_PERIOD)
            mScanning = true
            mBluetoothAdapter?.startLeScan(mLeScanCallback)
        } else {
            mScanning = false
            mBluetoothAdapter?.stopLeScan(mLeScanCallback)
        }
    }

    private val mLeScanCallback = BluetoothAdapter.LeScanCallback { device, rssi, scanRecord ->
        runOnUiThread {
            mAdapter.addItem(device)
        }

    }

    override fun onResume() {
        super.onResume()
        if (!mBluetoothAdapter!!.isEnabled)
            if (!mBluetoothAdapter!!.isEnabled) {
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
            }
        scan(true)
    }

    override fun onPause() {
        super.onPause()
        scan(false)
    }

    override fun onStop() {
        super.onStop()
        mAdapter.clearItems()
    }
}

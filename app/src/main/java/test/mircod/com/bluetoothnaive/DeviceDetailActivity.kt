package test.mircod.com.bluetoothnaive

import android.bluetooth.*
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_device_detail.*
import android.bluetooth.BluetoothGattDescriptor
import android.support.v7.widget.LinearLayoutManager
import test.mircod.com.bluetoothnaive.adater.detail_adapter.models.DeviceCharacteristics
import test.mircod.com.bluetoothnaive.adater.detail_adapter.models.DeviceService
import test.mircod.com.bluetoothnaive.adater.detail_adapter.DetailAdapter



class DeviceDetailActivity : AppCompatActivity(),  DetailAdapter.OnItemClickListener {
    override fun onClick(item: DeviceCharacteristics, flag: Boolean) {
         if (flag) {
            alreadySubscribed = false
            deviceServices?.forEach { deviceService ->
                //                Log.d(TAG, it.toString())
                val char = deviceService.characteristic?.firstOrNull {
                    it.uuid == item.uuid
                }
                var cvhar: BluetoothGattCharacteristic? = null
                mBluetoothGatt?.services?.forEach {
                    it.characteristics.forEach {
                        if (it.uuid == char?.uuid) {
                            cvhar = it
                        }
                    }
                }

                mBluetoothGatt?.setCharacteristicNotification(cvhar, false)
                val descriptor = cvhar!!.getDescriptor(
                        cvhar!!.descriptors[0].uuid)
                descriptor.value = BluetoothGattDescriptor.DISABLE_NOTIFICATION_VALUE
                mBluetoothGatt?.writeDescriptor(descriptor)
            }
        } else {
            if (alreadySubscribed)
                return
            alreadySubscribed = true
            deviceServices?.forEach { deviceService ->
                val char = deviceService.characteristic?.firstOrNull {
                    it.uuid == item.uuid
                }
                var cvhar: BluetoothGattCharacteristic? = null
                mBluetoothGatt?.services?.forEach {
                    it.characteristics.forEach {
                        if (it.uuid == char?.uuid) {
                            cvhar = it

                        }
                    }
                }
                mBluetoothGatt?.setCharacteristicNotification(cvhar, true)
                val descriptor = cvhar!!.getDescriptor(
                        cvhar!!.descriptors[0].uuid)
                descriptor.value = BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                mBluetoothGatt?.writeDescriptor(descriptor)
            }
        }
    }

    private var alreadySubscribed: Boolean = false
    private val TAG = DeviceDetailActivity::class.java.simpleName
    private val REQUEST_ENABLE_BT = 1
    private var mBluetoothAdapter: BluetoothAdapter? = null
    private var mBluetoothGatt: BluetoothGatt? = null
    lateinit var deviceName: String
    private var deviceMac: String? = null
    private var deviceServices: ArrayList<DeviceService>? = null
    lateinit var mAdapter: DetailAdapter
    val deviceCharacteristics: MutableList<DeviceCharacteristics> = arrayListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_detail)
        deviceName = intent.getStringExtra("ITEM_NAME")
        deviceMac = intent.getStringExtra("ITEM_MAC")
        supportActionBar!!.title = deviceName
        device_address_detail.text = deviceMac
        val bluetoothManager = getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        mBluetoothAdapter = bluetoothManager.adapter
        mAdapter = DetailAdapter(this)
        if (mBluetoothAdapter == null || !mBluetoothAdapter!!.isEnabled) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT)
        }
        characteristics_recyclerview.apply {
            layoutManager = LinearLayoutManager(this@DeviceDetailActivity)
            adapter = mAdapter
        }
        connect(deviceMac)
    }

    private fun updateConnectionState(resourceId: Int) {
        runOnUiThread { connection_state.setText(resourceId) }
    }

    fun getSupportedGattServices(): List<BluetoothGattService>? {
        return if (mBluetoothGatt == null) null else mBluetoothGatt?.services
    }

    private val mGattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                updateConnectionState(R.string.connected)
                Log.i(TAG, "Connected to GATT server.")
                Log.i(TAG, "Attempting to start service discovery:" +
                        mBluetoothGatt?.discoverServices())

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                updateConnectionState(R.string.disconnected)
                Log.i(TAG, "Disconnected from GATT server.")
            }
        }


        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {


                for (service in getSupportedGattServices()!!.iterator()) {

                        for (characteristic in service.characteristics)
                            if (characteristic.properties == BluetoothGattCharacteristic.PROPERTY_NOTIFY) {
                                runOnUiThread {
                                mAdapter.addItem(DeviceCharacteristics(characteristic.uuid))
                                }
                                deviceCharacteristics.add(DeviceCharacteristics(characteristic.uuid))
                            }
                        deviceServices = ArrayList(getSupportedGattServices()!!.map {
                            DeviceService(it.uuid, deviceCharacteristics as ArrayList<DeviceCharacteristics>)
                        })
                }

            } else {
                Log.w(TAG, "onServicesDiscovered received: $status")
            }
        }

        override fun onCharacteristicChanged(gatt: BluetoothGatt,
                                             characteristic: BluetoothGattCharacteristic) {

            deviceServices?.forEach { deviceService ->

                    deviceService.characteristic?.asSequence()?.filter {
                        it.uuid == characteristic.uuid
                    }?.map {
                        it.data = characteristic.value.toString()
                        runOnUiThread {
                        mAdapter.updateData(it)}
                    }?.toList()
                }
        }


    }

    private fun close() {
        if (mBluetoothGatt == null) {
            return
        }
        mBluetoothGatt?.close()
        mBluetoothGatt = null
    }

    private fun connect(address: String?): Boolean {
        if (mBluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.")
            return false
        }

        if (deviceMac != null && address == deviceMac
                && mBluetoothGatt != null) {
            Log.d(TAG, "Trying to use an existing mBluetoothGatt for connection.")
            return mBluetoothGatt!!.connect()
        }

        val device = mBluetoothAdapter?.getRemoteDevice(address)
        if (device == null) {
            Log.w(TAG, "Device not found.  Unable to connect.")
            return false
        }
        mBluetoothGatt = device.connectGatt(this, true, mGattCallback)
        Log.d(TAG, "Trying to create a new connection.")
        deviceMac = address
        return true
    }

    override fun onPause() {
        super.onPause()
        close()
    }
}

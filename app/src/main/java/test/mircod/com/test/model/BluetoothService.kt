package test.mircod.com.test.model

import android.bluetooth.BluetoothGattCharacteristic
import kotlin.collections.ArrayList
data class BluetoothService(val UUID: String,
                       var characteristic: ArrayList<BluetoothGattCharacteristic>)
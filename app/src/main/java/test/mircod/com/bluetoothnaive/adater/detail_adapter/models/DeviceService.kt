package test.mircod.com.bluetoothnaive.adater.detail_adapter.models

import java.util.*
import kotlin.collections.ArrayList

class DeviceService constructor(var uuid: UUID? = null,
                                var characteristic: ArrayList<DeviceCharacteristics>? = null) : Entity() {
    override fun isParentF(): Boolean {
        return true
    }

    override fun toString(): String {
        return "UUID: ${uuid.toString()}, Characteristics: {${characteristic.toString()}}"
    }
}
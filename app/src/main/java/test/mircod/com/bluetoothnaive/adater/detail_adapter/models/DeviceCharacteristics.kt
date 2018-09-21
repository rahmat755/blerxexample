package test.mircod.com.bluetoothnaive.adater.detail_adapter.models

import java.util.*

class DeviceCharacteristics constructor(var uuid: UUID? = null,
                                        var data: String? = null, var subscribed: Boolean = false) : Entity() {
    override fun isParentF(): Boolean {
        return true
    }

    override fun toString(): String {
        return "UUID: ${uuid.toString()}, Data: $data"
    }

}
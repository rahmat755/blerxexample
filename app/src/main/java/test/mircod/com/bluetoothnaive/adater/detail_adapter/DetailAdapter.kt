package test.mircod.com.bluetoothnaive.adater.detail_adapter


import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGattService
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kotlinx.android.synthetic.main.item_detail.view.*
import test.mircod.com.bluetoothnaive.R
import test.mircod.com.bluetoothnaive.adater.detail_adapter.models.DeviceCharacteristics

class DetailAdapter(private val viewAction: OnItemClickListener) : RecyclerView.Adapter<DetailAdapter.ViewHolder>() {

    var isNotified: Boolean = false

    interface OnItemClickListener {
        fun onClick(item: DeviceCharacteristics, flag: Boolean)
    }

    private val data = arrayListOf<DeviceCharacteristics>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_detail, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val deviceCharacteristics = data[position]
        holder.uuid?.text = deviceCharacteristics.uuid.toString()
        holder.data?.text = deviceCharacteristics.data
        holder.itemView.setOnClickListener {
            isNotified = if (isNotified) {
                viewAction.onClick(deviceCharacteristics, true)
                false
            } else {
                viewAction.onClick(deviceCharacteristics, false)
                true
            }
        }
    }

    class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        val uuid: TextView? = itemView?.findViewById(R.id.characteristic_uuid)
        val data: TextView? = itemView?.findViewById(R.id.characteristic_data)
    }

    fun addItem(item: DeviceCharacteristics) {
        if (!data.contains(item))
            data.add(item)
        notifyDataSetChanged()
    }

    fun clearItems() {
        data.clear()
        notifyDataSetChanged()
    }

    fun updateData(item: DeviceCharacteristics){
        for (i in 0 until data.size) {
            if (data[i].uuid == item.uuid) {
                data[i] = item
                notifyItemChanged(i)
                return
            }
        }
    }
}
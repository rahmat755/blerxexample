package test.mircod.com.test.adapter

import android.bluetooth.BluetoothGattService
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import test.mircod.com.test.R

class BluetoothServicesAdapter(private val viewAction: OnItemClickListener) : RecyclerView.Adapter<BluetoothServicesAdapter.ViewHolder>() {
    interface OnItemClickListener{
        fun onClick(item: BluetoothGattService)
    }

    private val data = arrayListOf<BluetoothGattService>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.detail_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bluetoothGattService = data[position]
        holder.serviceUUID?.text = bluetoothGattService.uuid.toString()
        holder.itemView.setOnClickListener { viewAction.onClick(bluetoothGattService) }
    }

    class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        val serviceUUID: TextView? = itemView?.findViewById(R.id.service_uuid)
    }
    fun addItems(item: BluetoothGattService){
        for (i in 0 until data.size) {
            if (data[i].uuid == item.uuid) {
                data[i] = item
                notifyDataSetChanged()
                return
            }
        }
        data.add(item)
        notifyDataSetChanged()
    }
    fun updateData(item: BluetoothGattService){
        for (i in 0 until data.size) {
            if (data[i].uuid == item.uuid) {
                data[i] = item
                notifyItemChanged(i)
                return
            }
        }
    }
    fun clearItems(){
        data.clear()
        notifyDataSetChanged()
    }
}
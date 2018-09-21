package test.mircod.com.bluetoothnaive.adater.scan_adapter

import android.bluetooth.BluetoothDevice
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import test.mircod.com.bluetoothnaive.R

class ScanResultAdapter(private val viewAction: OnItemClickListener) : RecyclerView.Adapter<ScanResultAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onClick(item: BluetoothDevice)
    }

    private val data = arrayListOf<BluetoothDevice>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.scan_result_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bleScanResult = data[position]
        if (bleScanResult.name != null) {
            holder.deviceName?.text = bleScanResult.name
        } else
            holder.deviceName?.setText(R.string.unknown_device_label)
        holder.macAddr?.text = bleScanResult.address
        holder.itemView.setOnClickListener { viewAction.onClick(bleScanResult) }
    }

    class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        val macAddr: TextView? = itemView?.findViewById(R.id.mac_address)
        val deviceName: TextView? = itemView?.findViewById(R.id.device_name)
    }

    fun addItem(item: BluetoothDevice) {
        if (!data.contains(item))
            data.add(item)
        notifyDataSetChanged()
    }

    fun clearItems() {
        data.clear()
        notifyDataSetChanged()
    }
}
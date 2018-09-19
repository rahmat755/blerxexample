package test.mircod.com.test.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.polidea.rxandroidble2.scan.ScanResult
import test.mircod.com.test.R
import java.util.*


class ScanResAdapter(private val viewAction: OnItemClickListener) : RecyclerView.Adapter<ScanResAdapter.ViewHolder>() {

    interface OnItemClickListener {
        fun onClick(item: ScanResult)
    }

    private val data = arrayListOf<ScanResult>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.result_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val rxBleScanResult = data[position]
        val bleDevice = rxBleScanResult.bleDevice
        if (bleDevice.name != null){
            holder.deviceName?.visibility = View.VISIBLE

            holder.deviceName?.text = String.format(Locale.getDefault(), "DEVICE NAME: %s",bleDevice.name ?: "")
        } else
            holder.deviceName?.visibility = View.GONE
        holder.macAddr?.text = String.format(Locale.getDefault(),"MAC: %s", bleDevice.macAddress)
        holder.rssi?.text = String.format(Locale.getDefault(), "RSSI: %d", rxBleScanResult.rssi)
        holder.itemView.setOnClickListener { viewAction.onClick(rxBleScanResult) }
    }

    class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        val macAddr: TextView? = itemView?.findViewById(R.id.mac_addr)
        val rssi: TextView? = itemView?.findViewById(R.id.rssi)
        val deviceName: TextView? = itemView?.findViewById(R.id.device_name)
    }

    fun addItems(item: ScanResult) {
        for (i in 0 until data.size) {
            if (data[i].bleDevice == item.bleDevice) {
                data[i] = item
                return
            }
        }
        data.add(item)
        notifyDataSetChanged()
    }

    fun clearItems() {
        data.clear()
        notifyDataSetChanged()
    }
}
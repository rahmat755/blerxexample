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

    interface OnItemClickListener{
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
        holder.macAddr?.text = String.format(Locale.getDefault(), "${bleDevice.macAddress} ${bleDevice.name ?: ""}")
        holder.rssi?.text = String.format(Locale.getDefault(), "RSSI: %d", rxBleScanResult.rssi)
        holder.itemView.setOnClickListener { viewAction.onClick(rxBleScanResult) }
    }

    class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        val macAddr: TextView? = itemView?.findViewById(R.id.mac_addr)
        val rssi: TextView? = itemView?.findViewById(R.id.rssi)
    }
    fun addItems(item: ScanResult){
        for (i in 0 until data.size) {
            if (data[i].bleDevice == item.bleDevice) {
                data[i] = item
                return
            }
        }
        data.add(item)
        notifyDataSetChanged()
    }
    fun clearItems(){
        data.clear()
        notifyDataSetChanged()
    }
}
package test.mircod.com.test.adapter

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import test.mircod.com.test.R
import test.mircod.com.test.model.BLECharacteristics


class CharacteristicsAdapter(private val viewAction: OnItemClickListener) : RecyclerView.Adapter<CharacteristicsAdapter.ViewHolder>() {
    interface OnItemClickListener {
        fun onClick(item: BLECharacteristics)
    }

    private val data = arrayListOf<BLECharacteristics>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.characteristic_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val bluetoothGattService = data[position]
        holder.characteristicsUUID?.text = bluetoothGattService.UUID.toString()
        holder.characteristicsData?.text = bluetoothGattService.data.toString()
        holder.itemView.setOnClickListener {
            viewAction.onClick(bluetoothGattService)
        }
    }

    class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {
        val characteristicsUUID: TextView? = itemView?.findViewById(R.id.characteristic_uuid)
        val characteristicsData: TextView? = itemView?.findViewById(R.id.characteristic_data)
    }

    fun addItems(item: BLECharacteristics) {
        for (i in 0 until data.size) {
            if (data[i].UUID == item.UUID) {
                data[i] = item
                notifyItemChanged(i)
                return
            }
        }
        data.add(item)
        notifyDataSetChanged()
    }

    fun updateData(item: BLECharacteristics) {
        for (i in 0 until data.size) {
            if (data[i].UUID == item.UUID) {
                data[i] = item
                notifyItemChanged(i)
                return
            }
        }

    }

    fun clearItems() {
        data.clear()
        notifyDataSetChanged()
    }
}
package com.example.lifestyleapp

import android.content.Context
import android.graphics.BitmapFactory
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import java.lang.ClassCastException

class MyRVAdapter(private val mListItems: MutableList<String>, private val picList: MutableList<String>) :
    RecyclerView.Adapter<MyRVAdapter.ViewHolder>() {
    private var mContext: Context? = null
    private var mDataPasser: ListPasser? = null

    class ViewHolder(var itemLayout: View) : RecyclerView.ViewHolder(
        itemLayout
    ) {
        var itemTvData: TextView = itemLayout.findViewById<View>(R.id.tv_user) as TextView
        var itemIv : ImageView = itemLayout.findViewById<View>(R.id.iv_pp) as ImageView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        mContext = parent.context
        mDataPasser = try {
            mContext as ListPasser?
        } catch (e: ClassCastException) {
            throw ClassCastException(mContext.toString() + " must implement DataPasser")
        }
        val layoutInflater = LayoutInflater.from(mContext)
        val myView = layoutInflater.inflate(R.layout.user_layout, parent, false)
        return ViewHolder(myView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.itemTvData.text = mListItems[holder.adapterPosition]
        if (holder.adapterPosition != 0)
            holder.itemIv.setImageBitmap(BitmapFactory.decodeFile(picList[holder.adapterPosition - 1]))
        holder.itemLayout.setOnClickListener { mDataPasser!!.passListData(holder.adapterPosition) }
    }

    fun remove(position: Int) {
        mListItems.removeAt(position)
        notifyItemRemoved(position)
    }

    override fun getItemCount(): Int {
        return mListItems.size
    }

    interface ListPasser {
        fun passListData(position: Int)
    }
}
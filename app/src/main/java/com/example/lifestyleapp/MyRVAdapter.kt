package com.example.lifestyleapp

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import java.lang.ClassCastException

class MyRVAdapter(private val mListItems: MutableList<String>) :
    RecyclerView.Adapter<MyRVAdapter.ViewHolder>() {
    private var mContext: Context? = null
    private var mDataPasser: ListPasser? = null

    class ViewHolder(var itemLayout: View) : RecyclerView.ViewHolder(
        itemLayout
    ) {
        var itemTvData: TextView = itemLayout.findViewById<View>(R.id.tv_user) as TextView
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
package com.example.lifestyleapp

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

// Pretty standard recyclerview
class UserRVAdapter(private val mListItems: List<UserTable>) :
    RecyclerView.Adapter<UserRVAdapter.ViewHolder>() {
    private var mContext: Context? = null
    private var mDataPasser: ListPasser? = null

    class ViewHolder(var itemLayout: View) : RecyclerView.ViewHolder(itemLayout) {
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
        val current = mListItems[position]
        holder.itemTvData.text = current.user_name
        if (holder.adapterPosition != 0)
            holder.itemIv.setImageBitmap(BitmapFactory.decodeFile(current.filename))
        holder.itemLayout.setOnClickListener {
            mDataPasser!!.passListData(holder.adapterPosition)
        }
    }

    override fun getItemCount(): Int {
        return mListItems.size
    }

    interface ListPasser {
        fun passListData(position: Int)
    }
}
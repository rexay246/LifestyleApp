package com.example.lifestyleapp

import android.os.Build
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager

class UserListFragment : Fragment() {
    private var mRecyclerView: RecyclerView? = null
    private var mAdapter: RecyclerView.Adapter<*>? = null
    private var layoutManager: RecyclerView.LayoutManager? = null

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val fragmentView = inflater.inflate(R.layout.userlist_layout_fragment, container, false)

        //Get the recycler view
        mRecyclerView = fragmentView.findViewById<View>(R.id.rv_Master) as RecyclerView

        //Tell Android that we know the size of the recyclerview doesn't change
        mRecyclerView!!.setHasFixedSize(true)

        //Set the layout manager
        layoutManager = LinearLayoutManager(activity)
        mRecyclerView!!.layoutManager = layoutManager

        //Get data from main activity
        val customListData = requireArguments().getParcelable("user_list", UsersData::class.java)
        val inputList = customListData!!.itemList
        val picList = customListData.picList

        //Set the adapter
        mAdapter = MyRVAdapter(inputList!!, picList!!)
        mRecyclerView!!.adapter = mAdapter
        return fragmentView
    }
}
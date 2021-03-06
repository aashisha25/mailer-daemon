package com.mailerdaemon.app

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import com.mailerdaemon.app.notices.NoticeAdapter
import com.mailerdaemon.app.notices.NoticesActivity
import com.mailerdaemon.app.notices.PostModel
import com.mailerdaemon.app.placement.DiffUtilCallback
import com.mailerdaemon.app.placement.PlacementActivity
import com.mailerdaemon.app.placement.PlacementAdapter
import com.mailerdaemon.app.placement.PlacementModel
import kotlinx.android.synthetic.main.activity_notices.*
import kotlinx.android.synthetic.main.fragment_placement.*
import kotlinx.android.synthetic.main.fragment_placement.refresh
import kotlinx.android.synthetic.main.fragment_placement.view.*
import kotlinx.android.synthetic.main.fragment_placement.view.refresh


class NoticesFragment : Fragment(), NoticesActivity.Companion.ShowNotices {
    var data = emptyList<PostModel>()
    private lateinit var adapter: NoticeAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_placement, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = NoticeAdapter(data)
        (activity as NoticesActivity).getNotices(this)
        view.refresh.setOnRefreshListener {
            refresh.visibility = View.GONE
            (activity as NoticesActivity).getNotices(this)
        }
    }

    override fun showNotices(list: List<PostModel>) {
        //val n = DiffUtilCallback(data, list)
        //val diffResult = DiffUtil.calculateDiff(n)
        data = list
        Log.d("update_list", "okok")
        adapter.list = data
        adapter.notifyDataSetChanged()
        //diffResult.dispatchUpdatesTo(adapter)
        rv_placement.adapter = adapter
        refresh.isRefreshing = false
    }

}

package com.byph.expandableview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        recycler_view.layoutManager = LinearLayoutManager(this)
//        recycler_view.adapter = RecyAdapter(this)
        // 设置RecyclerView可缓存ViewHolder最大数量 (解决布局错乱问题，太多了也是一样错乱)
//        recycler_view.setItemViewCacheSize(6)

        list_view.adapter = LvAdapter(this)
    }
}

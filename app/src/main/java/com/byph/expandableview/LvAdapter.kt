package com.byph.expandableview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.byph.lib.ExpandableView
import java.util.HashMap

/**
 * @author assen
 * @date 2019/6/21
 */
class LvAdapter(private val context: Context) : BaseAdapter() {

    private val sparseArray: MutableMap<Int, Any>

    init {
        sparseArray = HashMap<Int, Any>()
    }


    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return 50
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val viewHolder: ViewHolder
        val retView: View

        if (convertView == null) {
            retView = LayoutInflater.from(context).inflate(R.layout.item_layout_ev, null)
            viewHolder = ViewHolder()
            viewHolder.expandable_view = retView.findViewById(R.id.expanded_tv)
            retView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
            retView = convertView
        }

        viewHolder.expandable_view!!.setDatas(
            "常见问题", "热门问题1热门问题1热门问题1热门问题1热门问题1热门问题1热门 问题1热门问题1热门问" +
                    "题1热门问题1热门问题1热门问题1热门问题1热门 问题1热门问题1热门问题1热门问题1热门问题1热门问题1热门问题1热门 问题1热门问题1热门问题1热门问题1热门问题1热门问题1热门问题1热门 问题1热门问题1热门问" +
                    "题1热门问题1热门问题1热门问题1热门问题1热门 问题1热门问题1热门问" +
                    "题1热门问题1热门问题1热门问题1热门问题 1热门问题1"
        )

        // 取出存取的值，并赋值给当前view
        if (sparseArray[position] != null) {
            if (sparseArray[position] as Boolean) {
                viewHolder.expandable_view!!.setIsCollapsedR(true)
            } else {
                viewHolder.expandable_view!!.setIsCollapsedR(false)
            }
        }

        if (!sparseArray.containsKey(position)) {
            sparseArray[position] = true
        }

        viewHolder.expandable_view!!.setOnExpandStateChangeListener { isExpanded ->
            sparseArray[position] = isExpanded
        }

        return retView
    }

    internal inner class ViewHolder {
        var expandable_view: ExpandableView? = null
    }
}
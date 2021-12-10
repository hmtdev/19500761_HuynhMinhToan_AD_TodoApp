package iuh_ad.huynhminhtoan19500761.a19500761_huynhminhtoan

import android.content.Context
import android.system.Os.close
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*

class ToDoAdapter(context: Context,todoList:MutableList<ToDoModel>) : BaseAdapter() {
        private val inflater:LayoutInflater = LayoutInflater.from(context)
        private var itemList = todoList
        private var updateAndDelete : UpdateAndDelete = context as UpdateAndDelete
    override fun getCount(): Int {
        return itemList.size
    }

    override fun getItem(position: Int): Any {
        return itemList.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val UID : String  = itemList.get(position).UID as String
        val itemTextData = itemList.get(position).itemDataText as String
        val done : Boolean = itemList.get(position).done as Boolean

        val view : View
        val viewHolder :ListViewHolder
        if (convertView==null){
            view = inflater.inflate(R.layout.row_item_layout,parent,false)
            viewHolder = ListViewHolder(view)
            view.tag = viewHolder
        }else{
            view = convertView
            viewHolder = view.tag as ListViewHolder
        }
        viewHolder.textLabel.text = itemTextData
        viewHolder.isDone.isChecked = done
        viewHolder.isDone.setOnClickListener {
            updateAndDelete.modifyItem(UID,!done)

        }
        viewHolder.isDeleted.setOnClickListener {
            updateAndDelete.onItemDelete( UID,)
        }
        return view

    }
    private class ListViewHolder(row : View?){
        val  textLabel : TextView = row!!.findViewById(R.id.item_textView)
        val  isDone : CheckBox = row!!.findViewById(R.id.checkbox)
        val isDeleted : ImageButton = row!!.findViewById(R.id.close) as ImageButton
    }
}
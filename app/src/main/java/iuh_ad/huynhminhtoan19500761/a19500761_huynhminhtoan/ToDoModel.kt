package iuh_ad.huynhminhtoan19500761.a19500761_huynhminhtoan

import java.util.*


class ToDoModel{
    var UID : String? = null
    var itemDataText :String?=null
    var done : Boolean = false
    var time : String? = null
    companion object Factory {
        fun createList() : ToDoModel = ToDoModel()
    }
}
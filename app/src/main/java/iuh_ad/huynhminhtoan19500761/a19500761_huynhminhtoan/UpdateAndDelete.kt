package iuh_ad.huynhminhtoan19500761.a19500761_huynhminhtoan

interface UpdateAndDelete {
    fun modifyItem(itemUID : String, isDone : Boolean)
    fun onItemDelete(itemUID: String)
    fun onRepair(itemUID: String,text: String)
}

package iuh_ad.huynhminhtoan19500761.a19500761_huynhminhtoan

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.marginBottom
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*

class MainActivity : AppCompatActivity(),UpdateAndDelete {
    lateinit var database : DatabaseReference
    var toDoList: MutableList<ToDoModel>? = null
    private var listViewItem : ListView? = null
    lateinit var adapter: ToDoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val fab = findViewById(R.id.fab) as FloatingActionButton
        listViewItem = findViewById<ListView>(R.id.itemlistView)
        database =FirebaseDatabase.getInstance().reference


        fab.setOnClickListener {view ->
            val alertDialog = AlertDialog.Builder(this)
            val textEditText = EditText(this)
            alertDialog.setTitle("Add New Task")
            alertDialog.setView(textEditText)
            alertDialog.setPositiveButton("Add"){
                dialog,i ->
                val todoItemData = ToDoModel.createList()
                todoItemData.itemDataText= textEditText.text.toString()
                todoItemData.done = false
                val newItemData = database.child("todo").push()
                newItemData.setValue(todoItemData)
                dialog.dismiss()
                Toast.makeText(this, "item saved", Toast.LENGTH_SHORT).show()
                }
            alertDialog.setNegativeButton("Cancel"){
                dialog,i ->
                Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show()
            }
            alertDialog.show()
        }
        toDoList = mutableListOf<ToDoModel>()
        adapter = ToDoAdapter(this,toDoList!!)
        listViewItem!!.adapter = adapter
        database.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                toDoList!!.clear()
                addItemToList(snapshot)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "No item Added ", Toast.LENGTH_SHORT).show()
            }

        })

    }
    private fun addItemToList(snapshot : DataSnapshot){
        val item = snapshot.children.iterator()
        if(item.hasNext()){
            val toDoIndexedValue = item.next()
            val itemIterator = toDoIndexedValue.children.iterator()
            while (itemIterator.hasNext()){
                val currentItem = itemIterator.next()
                val toDoItemData = ToDoModel.createList()
                val map = currentItem.getValue() as HashMap<String , Any>
                toDoItemData.UID = currentItem.key
                toDoItemData.done = map.get("done") as Boolean
                toDoItemData.itemDataText = map.get("itemDataText") as String?
                toDoList!!.add(toDoItemData)
            }
        }
    adapter.notifyDataSetChanged()
    }

    override fun modifyItem(itemUID: String, isDone: Boolean) {
        val itemReference = database.child("todo").child(itemUID)
        itemReference.child("done").setValue(isDone)
    }

    override fun onItemDelete(itemUID: String) {
        val itemReference = database.child("todo").child(itemUID)
        itemReference.removeValue()
        adapter.notifyDataSetChanged()
    }
}
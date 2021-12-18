package iuh_ad.huynhminhtoan19500761.a19500761_huynhminhtoan

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.core.view.marginBottom
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import java.time.Year
import java.util.*
import kotlin.collections.HashMap

class MainActivity : AppCompatActivity(),UpdateAndDelete {
    lateinit var database : DatabaseReference
    var toDoList: MutableList<ToDoModel>? = null
    private var listViewItem : ListView? = null
    lateinit var adapter: ToDoAdapter

    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val fab = findViewById(R.id.fab) as FloatingActionButton
        listViewItem = findViewById<ListView>(R.id.itemlistView)
        database = FirebaseDatabase.getInstance().reference


        fab.setOnClickListener {view ->
            val alertDialog = AlertDialog.Builder(this)
            val textEditText = EditText(this)
            val textEditTextTime = EditText(this)
            alertDialog.setTitle("Add New Task")
            alertDialog.setMessage("Please set text before set time ^^ ")
            alertDialog.setView(textEditText)
            alertDialog.setPositiveButton("Save"){
                dialog,i ->
                val calendar : Calendar = Calendar.getInstance()
                val MONTH = calendar.get(Calendar.MONTH) + 1
                val YEAR = calendar.get(Calendar.YEAR).toString()
                val DAY = calendar.get(Calendar.DATE).toString()
                val date = DAY+ "/"+ MONTH + "/" + YEAR
                val todoItemData = ToDoModel.createList()
                todoItemData.itemDataText= textEditText.text.toString()
                todoItemData.done = false
                todoItemData.time = date
                val newItemData = database.child("todo").push()
                newItemData.setValue(todoItemData)
                dialog.dismiss()
                Toast.makeText(this, "item saved", Toast.LENGTH_SHORT).show()
                }
            alertDialog.setNegativeButton("Cancel"){
                dialog,i ->
                Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show()
            }

            alertDialog.setNeutralButton("Set Time"){
                dialog,i->
                    val calendar : Calendar = Calendar.getInstance()
                    val MONTH = calendar.get(Calendar.MONTH)
                    val YEAR = calendar.get(Calendar.YEAR)
                    val DAY = calendar.get(Calendar.DATE)
                    val datePickerDiaglog : DatePickerDialog = DatePickerDialog(this)
                    datePickerDiaglog.setOnDateSetListener { view, year, month, dayOfMonth ->
                        val month2 = month.toInt() + 1
                        val date = dayOfMonth.toString() + "/" + month2.toString() + "/" + year
                        Toast.makeText(this, date.toString(), Toast.LENGTH_SHORT).show()
                        val todoItemData = ToDoModel.createList()
                        todoItemData.itemDataText= textEditText.text.toString()
                        todoItemData.done = false
                        todoItemData.time = date.toString()
                        val newItemData = database.child("todo").push()
                        newItemData.setValue(todoItemData)
                        dialog.dismiss()
                    }
                    datePickerDiaglog.show();
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
                toDoItemData.time = map.get("time") as String?
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

    override fun onRepair(itemUID: String,text:String) {
        val itemReference = database.child("todo").child(itemUID)
        val alertDialog = AlertDialog.Builder(this)
        val textEditText : EditText= EditText(this)
        Toast.makeText(this, "click-click", Toast.LENGTH_SHORT).show()
        alertDialog.setTitle("Edit Task")
        alertDialog.setMessage("Task Current : " + text)
        alertDialog.setView(textEditText)
        alertDialog.setPositiveButton("Done"){
                dialog,i ->
            itemReference.child("itemDataText").setValue(textEditText.text.toString())
            dialog.dismiss()
            Toast.makeText(this, "item saved", Toast.LENGTH_SHORT).show()
        }
        alertDialog.setNegativeButton("Cancel"){
                dialog,i ->
            Toast.makeText(this, "Cancel", Toast.LENGTH_SHORT).show()
        }
        alertDialog.show()
    }
}
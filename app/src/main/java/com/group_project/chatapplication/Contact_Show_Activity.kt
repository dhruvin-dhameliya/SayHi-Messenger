package com.group_project.chatapplication

import android.content.Context
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Contact_Show_Activity : AppCompatActivity() {
    lateinit var database: DatabaseReference
    lateinit var auth: FirebaseAuth
    lateinit var name: String
    lateinit var number: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_show)

        val contact_list = findViewById<RecyclerView>(R.id.contact_list)
        contact_list.layoutManager = LinearLayoutManager(this)
        val contactList: MutableList<ContactDTO> = ArrayList()
        val map: HashMap<String, String> = HashMap<String, String>()

        auth = FirebaseAuth.getInstance()
        val login_phone = auth.currentUser?.phoneNumber.toString()
        database = FirebaseDatabase.getInstance().getReference("Contacts").child(login_phone)

        val contacts = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )

        if (contacts != null) {
            while (contacts.moveToNext()) {
                name =
                    contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                number =
                    contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                val obj = ContactDTO()
                obj.name = name
                obj.phone_number = number

                contactList.add(obj)

                map.put("contact_name", name)
                map.put("contact_number", number)
                database.push().setValue(map)
            }

            database.push().setValue(map)
                .addOnSuccessListener {
                    Toast.makeText(this, "Upload Successfully!", Toast.LENGTH_SHORT).show()
                }.addOnFailureListener {
                    Toast.makeText(this, "Not Upload!", Toast.LENGTH_SHORT).show()
                }

            contact_list.adapter = ContactAdapter(contactList, this)
            contacts.close()
        }

    }

    class ContactAdapter(items: MutableList<ContactDTO>, ctx: Context) :
        RecyclerView.Adapter<ContactAdapter.ViewHolder>() {

        private var list = items
        private var context = ctx

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.name.text = list[position].name
            holder.number.text = list[position].phone_number
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.item_contact, parent, false)
            )
        }

        class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val name: TextView = v.findViewById(R.id.tv_name)!!
            val number: TextView = v.findViewById(R.id.tv_number)!!

        }

    }

}
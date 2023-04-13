package com.group_project.chatapplication.contacts

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.group_project.chatapplication.R
import com.group_project.chatapplication.singleChat.single_chat_messages.Single_Chat_Messages_Activity

class Contact_Show_Activity : AppCompatActivity() {
    lateinit var name: String
    lateinit var number: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_contact_show)

        val back_main_screen = findViewById<ImageView>(R.id.back_main_screen)
        val contact_list = findViewById<RecyclerView>(R.id.contact_list_for_activity)
        val contact_search_view_for_activity =
            findViewById<SearchView>(R.id.contact_search_view_for_activity)
        contact_list.layoutManager = LinearLayoutManager(this)

        back_main_screen.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        val contactList: MutableList<ContactDTO> = ArrayList()
        val temporaryContactList: MutableList<ContactDTO> = ArrayList()

        val contacts = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC"
        )

        if (contacts != null) {
            while (contacts.moveToNext()) {
                name =
                    contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                number =
                    contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                        .replace(" ", "").replace("+91", "")
                val obj = ContactDTO()
                obj.name = name
                obj.phone_number = number
                contactList.add(obj)
            }
            contact_list.adapter = ContactAdapter(temporaryContactList, this)
            contacts.close()
        }
        temporaryContactList.addAll(contactList)

        contact_search_view_for_activity.setOnQueryTextListener(object :
            SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query2: String?): Boolean {
                temporaryContactList.clear()
                val searchText_2 = query2!!
                if (searchText_2.isNotEmpty()) {
                    contactList.forEach {
                        if (it.name.contains(searchText_2) || it.phone_number.contains(searchText_2)) {
                            temporaryContactList.add(it)
                        }
                    }
                    contact_list.adapter!!.notifyDataSetChanged()
                } else {
                    temporaryContactList.clear()
                    temporaryContactList.addAll(contactList)
                    contact_list.adapter!!.notifyDataSetChanged()
                }
                return false
            }

            override fun onQueryTextChange(newText2: String?): Boolean {
                temporaryContactList.clear()
                val searchText_2 = newText2!!
                if (searchText_2.isNotEmpty()) {
                    contactList.forEach {
                        if (it.name.contains(searchText_2) || it.phone_number.contains(searchText_2)) {
                            temporaryContactList.add(it)
                        }
                    }
                    contact_list.adapter!!.notifyDataSetChanged()
                } else {
                    temporaryContactList.clear()
                    temporaryContactList.addAll(contactList)
                    contact_list.adapter!!.notifyDataSetChanged()
                }
                return false
            }
        })

    }

    class ContactAdapter(items: MutableList<ContactDTO>, ctx: Context) :
        RecyclerView.Adapter<ContactAdapter.ViewHolder>() {

        private var list = items
        private var context = ctx
        private lateinit var database: DatabaseReference
        private lateinit var auth: FirebaseAuth

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.name.text = list[position].name
            holder.number.text = list[position].phone_number
            holder.contact_card_view.setOnClickListener {
                auth = FirebaseAuth.getInstance()
                val login_phone = auth.currentUser?.phoneNumber.toString()

                var contact_model =
                    Contact_Model(
                        list[position].name,
                        list[position].phone_number.trim().replace(" ", "").replace("-", "")
                    )

                database = FirebaseDatabase.getInstance().getReference("Contacts")
                    .child(login_phone.replace(" ", "").replace("-", ""))
                    .child(list[position].phone_number.trim().replace(" ", "").replace("-", ""))
                database.setValue(contact_model)

                val intent_id = Intent(context, Single_Chat_Messages_Activity::class.java)
                intent_id.putExtra("pass_receiver_name", list[position].name.trim())
                intent_id.putExtra("pass_receiver_number", list[position].phone_number.trim())
                context.startActivity(intent_id)
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(
                LayoutInflater.from(context).inflate(R.layout.item_contact, parent, false)
            )
        }

        class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
            val name: TextView = v.findViewById(R.id.tv_name)!!
            val number: TextView = v.findViewById(R.id.tv_number)!!
            val contact_card_view: CardView = v.findViewById(R.id.contact_card_view)!!
        }
    }
}
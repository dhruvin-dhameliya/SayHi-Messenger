package com.group_project.chatapplication.groupChat.add_participant

import android.app.AlertDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.group_project.chatapplication.R
import com.group_project.chatapplication.contacts.ContactDTO
import com.group_project.chatapplication.contacts.Contact_Model

class Group_Contacts_Activity : AppCompatActivity() {
    lateinit var name: String
    lateinit var number: String
    lateinit var groupId: String
    var firebaseAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_contacts)
        val contact_list = findViewById<RecyclerView>(R.id.group_contact_list_for_activity)
        val contact_search_view_for_activity =
            findViewById<SearchView>(R.id.group_contact_search_view_for_activity)

        firebaseAuth = FirebaseAuth.getInstance()
        groupId = intent.getStringExtra("groupId").toString()

        contact_list.layoutManager = LinearLayoutManager(this)

        val contactList: MutableList<ContactDTO> = ArrayList()
        val temporaryContactList: MutableList<ContactDTO> = ArrayList()

        val contacts = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null
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
            }
            contact_list.adapter = ContactAdapter(temporaryContactList, this, groupId)
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

    class ContactAdapter(items: MutableList<ContactDTO>, ctx: Context, groupID: String) :
        RecyclerView.Adapter<ContactAdapter.ViewHolder>() {

        private var list = items
        private var context = ctx
        private var groupID = groupID

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.name.text = list[position].name
            holder.number.text = list[position].phone_number
            holder.contact_card_view.setOnClickListener {
                //not exists/not participant-add
                //get data
                var contact_model =
                    Contact_Model(
                        list[position].name,
                        list[position].phone_number.trim().replace(" ", "").replace("-", "")
                    )

                //not exists/not participant-add
                val builder = AlertDialog.Builder(context)
                builder.setTitle("Add Participant")
                    .setMessage("Add this user in the group")
                    .setPositiveButton("ADD") { _, _ -> //add user
                        //addParticipant(contact_model)
                        val timestamp = "" + System.currentTimeMillis()
                        val hashMap = HashMap<String, String>()
                        hashMap["uid"] = contact_model.contact_number
                        hashMap["post"] = "participant"
                        hashMap["timestamp"] = "" + timestamp
                        hashMap["name"] = contact_model.contact_name
                        //add that user in Groups<groupId>Participant
                        val reference = FirebaseDatabase.getInstance().getReference("Groups")
                        reference.child(groupID).child("Participants")
                            .child(contact_model.contact_number).setValue(hashMap)
                            .addOnSuccessListener { //add success
                                Toast.makeText(context, "Added successfully", Toast.LENGTH_SHORT)
                                    .show()
                            }.addOnFailureListener { //failed
                                Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
                            }

                    }.setNegativeButton(
                        "CANCEL"
                    ) { dialogInterface, _ -> dialogInterface.dismiss() }.show()
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
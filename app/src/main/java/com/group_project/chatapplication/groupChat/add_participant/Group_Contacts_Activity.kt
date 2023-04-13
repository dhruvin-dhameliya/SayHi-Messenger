package com.group_project.chatapplication.groupChat.add_participant

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.group_project.chatapplication.R
import com.group_project.chatapplication.contacts.ContactDTO
import com.group_project.chatapplication.contacts.Contact_Model
import com.group_project.chatapplication.singleChat.single_chat_messages.Single_Chat_Messages_Activity

class Group_Contacts_Activity : AppCompatActivity() {
    lateinit var name: String
    lateinit var number: String
    lateinit var groupId: String
    var firebaseAuth: FirebaseAuth? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_group_contacts)

        val back_group_screen = findViewById<ImageView>(R.id.back_group_screen)
        val contact_list = findViewById<RecyclerView>(R.id.group_contact_list_for_activity)
        val contact_search_view_for_activity =
            findViewById<SearchView>(R.id.group_contact_search_view_for_activity)

        back_group_screen.setOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        firebaseAuth = FirebaseAuth.getInstance()
        groupId = intent.getStringExtra("groupId").toString()

        contact_list.layoutManager = LinearLayoutManager(this)

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
        var fetch_number = ""
        var fetch_phone_number: String? = null
        var firebaseAuth: FirebaseAuth? = null
        var myPost = ""
        var phone = ""

        override fun getItemCount(): Int {
            return list.size
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            firebaseAuth = FirebaseAuth.getInstance()
            val user = firebaseAuth!!.currentUser
            fetch_phone_number = user!!.phoneNumber

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

                fetch_number = contact_model.contact_number
                if (fetch_number.length == 10) {
                    fetch_number = "+91" + fetch_number
                }


                val def =
                    FirebaseDatabase.getInstance().getReference("Users Details").child(fetch_number)
                        .addValueEventListener(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                if (snapshot.exists()) {

                                    if (fetch_number == fetch_phone_number) {
                                        val ref =
                                            FirebaseDatabase.getInstance().getReference("Groups")
                                        ref.child(groupID).child("Participants").orderByChild("uid")
                                            .equalTo(fetch_phone_number)
                                            .addValueEventListener(object : ValueEventListener {
                                                override fun onDataChange(snapshot: DataSnapshot) {
                                                    for (ds in snapshot.children) {
                                                        myPost = "" + ds.child("post").value

                                                        //my number through add participant
                                                        val builder = AlertDialog.Builder(context)
                                                        builder.setTitle("Add Participant")
                                                            .setMessage("Add this user in the group")
                                                            .setPositiveButton("ADD") { dialogInterface, _ -> //add user
                                                                //addParticipant(contact_model)
                                                                val timestamp =
                                                                    "" + System.currentTimeMillis()
                                                                val hashMap =
                                                                    HashMap<String, String>()
                                                                hashMap["uid"] = fetch_number
                                                                hashMap["post"] = myPost
                                                                hashMap["timestamp"] =
                                                                    "" + timestamp
                                                                hashMap["name"] =
                                                                    contact_model.contact_name
                                                                //add that user in Groups<groupId>Participant
                                                                val reference =
                                                                    FirebaseDatabase.getInstance()
                                                                        .getReference("Groups")
                                                                reference.child(groupID)
                                                                    .child("Participants")
                                                                    .child(fetch_number)
                                                                    .setValue(hashMap)
                                                                    .addOnSuccessListener { //add success
                                                                        Toast.makeText(
                                                                            context,
                                                                            "Added successfully",
                                                                            Toast.LENGTH_SHORT
                                                                        )
                                                                            .show()
                                                                        dialogInterface.dismiss()
                                                                    }
                                                                    .addOnFailureListener { //failed
                                                                        Toast.makeText(
                                                                            context,
                                                                            "Failed",
                                                                            Toast.LENGTH_SHORT
                                                                        ).show()
                                                                    }

                                                            }.setNegativeButton(
                                                                "CANCEL"
                                                            ) { dialogInterface, _ -> dialogInterface.dismiss() }
                                                            .show()

                                                    }
                                                }

                                                override fun onCancelled(error: DatabaseError) {
                                                    TODO("Not yet implemented")
                                                }

                                            })

                                    }
                                    if (fetch_number != fetch_phone_number) {


//                                    //not exists/not participant-add
                                        val builder = AlertDialog.Builder(context)
                                        builder.setTitle("Add Participant")
                                            .setMessage("Add this user in the group")
                                            .setPositiveButton("ADD") { _, _ -> //add user
                                                //addParticipant(contact_model)
                                                val timestamp = "" + System.currentTimeMillis()
                                                val hashMap = HashMap<String, String>()
                                                hashMap["uid"] = fetch_number
                                                hashMap["post"] = "participant"
                                                hashMap["timestamp"] = "" + timestamp
                                                hashMap["name"] = contact_model.contact_name
                                                //add that user in Groups<groupId>Participant
                                                val reference = FirebaseDatabase.getInstance()
                                                    .getReference("Groups")
                                                reference.child(groupID).child("Participants")
                                                    .child(fetch_number).setValue(hashMap)
                                                    .addOnSuccessListener { //add success
                                                        Toast.makeText(
                                                            context,
                                                            "Added successfully",
                                                            Toast.LENGTH_SHORT
                                                        )
                                                            .show()
                                                    }.addOnFailureListener { //failed
                                                        Toast.makeText(
                                                            context,
                                                            "Failed",
                                                            Toast.LENGTH_SHORT
                                                        ).show()
                                                    }

                                            }.setNegativeButton(
                                                "CANCEL"
                                            ) { dialogInterface, _ -> dialogInterface.dismiss() }
                                            .show()
                                    }


                                } else {
                                    val intent_id =
                                        Intent(context, Single_Chat_Messages_Activity::class.java)
                                    intent_id.putExtra(
                                        "pass_receiver_name",
                                        contact_model.contact_name.trim()
                                    )
                                    intent_id.putExtra("pass_receiver_number", fetch_number.trim())
                                    context.startActivity(intent_id)
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                TODO("Not yet implemented")
                            }

                        })


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
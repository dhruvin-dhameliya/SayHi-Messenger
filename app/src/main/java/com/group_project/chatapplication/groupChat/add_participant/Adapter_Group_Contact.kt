package com.group_project.chatapplication.groupChat.add_participant

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.group_project.chatapplication.R
import com.group_project.chatapplication.contacts.ContactDTO

class Adapter_Group_Contact(items: MutableList<ContactDTO>, ctx: Context, groupId: String) :
    RecyclerView.Adapter<Adapter_Group_Contact.ViewHolder>() {

    private var list = items
    private var context = ctx
    private var groupId = groupId
    var myGroupPost = ""
    var fetch_phone_number: kotlin.String? = null
    var firebaseAuth: FirebaseAuth? = null

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        firebaseAuth = FirebaseAuth.getInstance()
        val user = firebaseAuth!!.currentUser
        fetch_phone_number = user!!.phoneNumber
        holder.name.text = list[position].name
        holder.contact_card_view.setOnLongClickListener() {
            val reference = FirebaseDatabase.getInstance().getReference("Groups")
            reference.child(groupId).child("Participants").orderByChild("uid")
                .equalTo(fetch_phone_number).addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (ds in snapshot.children) {
                            myGroupPost = "" + ds.child("post").value
                            if (myGroupPost == "participant") {
                                Toast.makeText(
                                    context, "You are not admin of this group", Toast.LENGTH_SHORT
                                ).show()
                            }
                            if (myGroupPost == "creator") {
                                val reference =
                                    FirebaseDatabase.getInstance().getReference("Groups")
                                reference.child(groupId).child("Participants").orderByChild("name")
                                    .equalTo(list[position].name)
                                    .addValueEventListener(object : ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            for (ds in snapshot.children) {
                                                val myid = "" + ds.child("uid").value
                                                val hispost = "" + ds.child("post").value
                                                if (fetch_phone_number == myid) {
                                                    Toast.makeText(
                                                        context,
                                                        "You are the admin of this group ",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                                if (hispost.equals("participant")) {
                                                    //I am creator ,He is participant
                                                    val options =
                                                        arrayOf("Make Admin", "Remove Participant")
                                                    val builder = AlertDialog.Builder(context)
                                                    builder.setTitle("Choose Options")
                                                        .setItems(options) { dialogInterface, i ->
                                                            if (i == 0) {
                                                                //Make Admin
                                                                val hashMap = HashMap<String, Any>()
                                                                hashMap["post"] = "admin"
                                                                ds.ref.updateChildren(hashMap)
                                                                dialogInterface.dismiss()
                                                            }
                                                            if (i == 1) {
                                                                //Remove Participant
                                                                ds.ref.removeValue()
                                                                dialogInterface.dismiss()
                                                            }
                                                        }
                                                    builder.create().show()

                                                }
                                                if (hispost.equals("admin")) {
                                                    val builder = AlertDialog.Builder(context)
                                                    builder.setTitle("Remove Admin")
                                                        .setMessage("Do you want to Remove admin of this group?")
                                                        .setPositiveButton("Remove") { _, _ -> //add user
                                                            val hashMap = HashMap<String, Any>()
                                                            hashMap["post"] = "participant"
                                                            ds.ref.updateChildren(hashMap)

                                                        }.setNegativeButton(
                                                            "CANCEL"
                                                        ) { dialogInterface, _ -> dialogInterface.dismiss() }
                                                        .show()
                                                }

                                            }
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT)
                                                .show()
                                        }
                                    })

                            }
                            if (myGroupPost == "admin") {
                                val reference =
                                    FirebaseDatabase.getInstance().getReference("Groups")
                                reference.child(groupId).child("Participants").orderByChild("name")
                                    .equalTo(list[position].name)
                                    .addValueEventListener(object : ValueEventListener {
                                        override fun onDataChange(snapshot: DataSnapshot) {
                                            for (ds in snapshot.children) {
                                                val myid = "" + ds.child("uid").value
                                                val hispost = "" + ds.child("post").value
                                                if (fetch_phone_number == myid) {
                                                    Toast.makeText(
                                                        context,
                                                        "You are the admin of this group ",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                }
                                                if (hispost.equals("participant")) {
                                                    //I am creator ,He is participant
                                                    val options =
                                                        arrayOf("Make Admin", "Remove Participant")
                                                    val builder = AlertDialog.Builder(context)
                                                    builder.setTitle("Choose Options")
                                                        .setItems(options) { dialogInterface, i ->
                                                            if (i == 0) {
                                                                //Make Admin
                                                                val hashMap = HashMap<String, Any>()
                                                                hashMap["post"] = "admin"
                                                                ds.ref.updateChildren(hashMap)
                                                                dialogInterface.dismiss()
                                                            }
                                                            if (i == 1) {
                                                                //Remove Participant
                                                                ds.ref.removeValue()
                                                                dialogInterface.dismiss()
                                                            }
                                                        }
                                                    builder.create().show()

                                                }
                                                if (hispost.equals("admin")) {
                                                    val builder = AlertDialog.Builder(context)
                                                    builder.setTitle("Remove Admin")
                                                        .setMessage("Do you want to Remove admin of this group?")
                                                        .setPositiveButton("Remove") { _, _ -> //add user
                                                            val hashMap = HashMap<String, Any>()
                                                            hashMap["post"] = "participant"
                                                            ds.ref.updateChildren(hashMap)

                                                        }.setNegativeButton(
                                                            "CANCEL"
                                                        ) { dialogInterface, _ -> dialogInterface.dismiss() }
                                                        .show()
                                                }

                                            }
                                        }

                                        override fun onCancelled(error: DatabaseError) {
                                            Toast.makeText(context, "Failed", Toast.LENGTH_SHORT)
                                                .show()
                                        }
                                    })

                            }
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
            true
        }

        val reference = FirebaseDatabase.getInstance().getReference("Groups")
        reference.child(groupId).child("Participants").orderByChild("name")
            .equalTo(list[position].name).addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (ds in snapshot.children) {
                        val post = "" + ds.child("post").value
                        holder.post.text = post
//                        if (post.equals("creator")) {
//                            holder.you.visibility = View.VISIBLE
//                        }
                        val number = "" + ds.child("uid").value
                        holder.post.text = post
                        if (number == fetch_phone_number) {
                            holder.you.visibility = View.VISIBLE
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
                }
            })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.row_add_participant, parent, false)
        )
    }

    class ViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        val name: TextView = v.findViewById(R.id.tv_name)!!
        val contact_card_view: CardView = v.findViewById(R.id.contact_card_view)!!
        val post: TextView = v.findViewById(R.id.tv_post)!!
        val you: TextView = v.findViewById(R.id.you)!!
    }
}
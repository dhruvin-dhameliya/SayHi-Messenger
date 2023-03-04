package com.group_project.chatapplication.groupChat.add_participant

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.group_project.chatapplication.R
import com.group_project.chatapplication.contacts.ContactDTO

class Adapter_Group_Contact(items: MutableList<ContactDTO>, ctx: Context) :
    RecyclerView.Adapter<Adapter_Group_Contact.ViewHolder>() {

    private var list = items
    private var context = ctx

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.name.text = list[position].name
        holder.number.text = list[position].phone_number
        holder.contact_card_view.setOnClickListener {}
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
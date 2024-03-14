package com.example.menuapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class add_recycler_adapter(val str : ArrayList<String>, val recycler : RecyclerView, val user_ref : DocumentReference?, val ref_flag : Int) : RecyclerView.Adapter<add_recycler_adapter.recycle_view>() {

    inner class recycle_view(itemView : View) : RecyclerView.ViewHolder(itemView){
        val text : TextView = itemView.findViewById(R.id.add_recycler_text)
        val btn : ImageButton = itemView.findViewById(R.id.add_recycler_btn)

        fun bind(position: Int){
            text.text = str[position]

            btn.setOnClickListener(){
                str.removeAt(position)
                recycler.adapter = add_recycler_adapter(str, recycler, user_ref, ref_flag)
                if(str.size == 0)
                    recycler.visibility = RecyclerView.INVISIBLE

                FirebaseFirestore.getInstance().collection("users").document(Firebase.auth.currentUser?.uid.toString()).get()
                    .addOnSuccessListener { task_aux ->

                        if(task_aux.exists()){

                            user_ref?.get()
                                ?.addOnSuccessListener { task ->
                                    if(task.exists()) {

                                        if(ref_flag == 0) {
                                            user_ref.update(
                                                mapOf(
                                                    "size" to str.size,
                                                    text.text.toString() to FieldValue.delete()
                                                )
                                            )
                                        }
                                        else{
                                            FirebaseFirestore.getInstance().collection("users").whereEqualTo("nickname", text.text.toString()).get()
                                                .addOnSuccessListener { task_aux2 ->

                                                    if(!task_aux2.isEmpty) {
                                                        user_ref.update(
                                                            mapOf(
                                                                "size" to str.size,
                                                                task_aux2.documents[0].id to false
                                                            )
                                                        )

                                                        if(task_aux.getString("nickname").toString() == text.text.toString()){

                                                            FirebaseFirestore.getInstance().collection("users").document(Firebase.auth.currentUser?.uid.toString()).collection("followers").document("follow").update(
                                                                mapOf(
                                                                    "size" to str.size,
                                                                    task_aux2.documents[0].id to false
                                                                )
                                                            )
                                                        }
                                                    }
                                                }
                                        }
                                    }
                                }
                        }
                    }

            }
        }
    }

    override fun getItemCount(): Int {
        return str.size
    }

    override fun onBindViewHolder(holder: recycle_view, position: Int) {
        holder.bind(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): recycle_view {
        val inf_view =  LayoutInflater.from(parent.context).inflate(R.layout.add_recycler_item, parent, false)
        return recycle_view(inf_view)
    }
}
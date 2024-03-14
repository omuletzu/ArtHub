package com.example.menuapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.animation.AlphaAnimation
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class profile_following_list : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_following_list)

        val zero_follow_text : TextView = findViewById(R.id.profile_following_text_zero)
        val btn_back : ImageButton = findViewById(R.id.edit_profile_back)
        val recycle : RecyclerView = findViewById(R.id.add_recycler)

        val following_arr = ArrayList<String>()
        val user_ref = FirebaseFirestore.getInstance().collection("users").document(Firebase.auth.currentUser?.uid.toString()).collection("followers").document("following")

        user_ref.get()
            .addOnSuccessListener { task ->

                if (task.exists()) {

                    for ((field, value) in task.data!!) {

                        if (value == true) {

                            FirebaseFirestore.getInstance().collection("users").document(field)
                                .get()
                                .addOnSuccessListener { task_user ->

                                    if (task_user.exists()) {
                                        following_arr.add(
                                            task_user.getString("nickname").toString()
                                        )
                                        recycle.adapter = add_recycler_adapter(
                                            following_arr,
                                            recycle,
                                            user_ref,
                                            1
                                        )

                                        recycle.visibility = RecyclerView.VISIBLE
                                        zero_follow_text.visibility = TextView.INVISIBLE
                                    }
                                }
                        }
                    }

                    recycle.layoutManager = LinearLayoutManager(this)
                }
            }

        btn_back.setOnClickListener(){
            val animation = AlphaAnimation(0.5f, 1.0f)
            animation.duration = 500

            btn_back.startAnimation(animation)

            finish()
        }
    }
}
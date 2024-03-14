package com.example.menuapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class settings_mutual_unblock : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_mutual_unblock)

        val btn_back : ImageButton = findViewById(R.id.edit_profile_back)
        val user : TextInputEditText = findViewById(R.id.add_input_tag)
        val recycle : RecyclerView = findViewById(R.id.add_recycler)
        val add_user : ImageButton = findViewById(R.id.add_tag_btn)
        val warning_btn : Button = findViewById(R.id.settings_warning_btn)
        val main_lay : ConstraintLayout = findViewById(R.id.settings_restrict_main_lay)

        val animation = AlphaAnimation(0.0f, 1.0f)
        animation.duration = 500
        main_lay.startAnimation(animation)

        btn_back.setOnClickListener(){
            val btn_back_animation = AlphaAnimation(0.1f, 1.0f)
            btn_back_animation.duration = 1000

            btn_back.startAnimation(btn_back_animation)
            finish()
        }

        val user_ref = FirebaseFirestore.getInstance().collection("users").document(Firebase.auth.currentUser?.uid.toString())
            .collection("blocked_users").document("mutual_unblock_users")

        user_ref.get()
            .addOnSuccessListener { task ->

                if(task.exists()) {

                    val str_arr = ArrayList<String>()
                    var size = task.getLong("size")

                    if(task.data != null){

                        for((field, value) in task.data!!){
                            if(field != "size")
                                str_arr.add(field)
                        }
                    }

                    recycle.layoutManager = LinearLayoutManager(this)
                    recycle.adapter = add_recycler_adapter(str_arr, recycle, user_ref, 0)

                    add_user.setOnClickListener(){

                        val anim = AnimationUtils.loadAnimation(this, R.anim.btn_rotation)
                        add_user.startAnimation(anim)

                        FirebaseFirestore.getInstance().collection("users").whereEqualTo("nickname", user.text.toString()).get()
                            .addOnSuccessListener { task_verify ->
                                if(!task_verify.isEmpty && !str_arr.contains(user.text.toString())){

                                    str_arr.add(user.text.toString())
                                    recycle.adapter = add_recycler_adapter(str_arr, recycle, user_ref, 0)
                                    recycle.visibility = RecyclerView.VISIBLE

                                    user_ref.update(
                                        mapOf(
                                            "size" to str_arr.size,
                                            user.text.toString() to task_verify.documents[0].id
                                        )
                                    )

                                    user.setText("")
                                }
                                else{
                                    if(task_verify.isEmpty)
                                        warning_btn.text = "Non-existing user"
                                    else
                                        warning_btn.text = "User already existing"

                                    warning_btn.visibility = Button.VISIBLE
                                    warning_btn.startAnimation(animation)

                                    main_lay.alpha = 0.1f
                                }
                            }
                    }

                    if(str_arr.size == 0)
                        recycle.visibility = RecyclerView.INVISIBLE
                }
            }

        warning_btn.setOnClickListener(){

            warning_btn.visibility = Button.INVISIBLE
            main_lay.alpha = 1.0f
        }
    }
}
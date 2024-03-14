package com.example.menuapp

import android.app.Activity
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AlphaAnimation
import android.widget.Button
import android.widget.ImageButton
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class settings_delete_account : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_delete_account)

        val btn_back : ImageButton = findViewById(R.id.edit_profile_back)
        val reason : TextInputEditText = findViewById(R.id.settings_reason)
        val disable_acc : Button = findViewById(R.id.settings_disable_acc)
        val delete_acc : Button = findViewById(R.id.settings_delete_acc)
        val warnign_lay : ConstraintLayout = findViewById(R.id.settings_delete_warning_lay)
        val warning_btn_yes : Button = findViewById(R.id.settings_delete_yes)
        val warning_btn_no : Button = findViewById(R.id.settings_delete_no)
        val main_lay : ConstraintLayout = findViewById(R.id.settings_delete_main_lay)

        val animation = AlphaAnimation(0.1f, 1.0f)
        animation.duration = 1000

        anim(main_lay)

        btn_back.setOnClickListener(){
            btn_back.startAnimation(animation)
            finish()
        }

        val user_ref = FirebaseFirestore.getInstance().collection("users").document(Firebase.auth.currentUser?.uid.toString())

        disable_acc.setOnClickListener(){

            disable_acc.startAnimation(animation)

            anim(warnign_lay)
            warnign_lay.visibility = ConstraintLayout.VISIBLE
            main_lay.alpha = 0.1f

            warning_btn_yes.setOnClickListener(){
                val final_reason : String = reason.text.toString()

                user_ref.update(
                    mapOf(
                        "reason_disable" to final_reason,
                        "disabled" to true
                    )
                )
                    .addOnSuccessListener {
                        Firebase.auth.signOut()
                        finish()
                        startActivity(Intent(this, MainActivity::class.java))
                    }
            }
        }

        delete_acc.setOnClickListener(){

            delete_acc.startAnimation(animation)

            anim(warnign_lay)
            warnign_lay.visibility = ConstraintLayout.VISIBLE
            main_lay.alpha = 0.1f

            warning_btn_yes.setOnClickListener(){
                val final_reason : String = reason.text.toString()

                FirebaseFirestore.getInstance().collection("users").document(Firebase.auth.currentUser?.uid.toString()).delete()
                    .addOnSuccessListener {

                        val str_type_document = ArrayList<String>()
                        str_type_document.add("PAINT")
                        str_type_document.add("DRAWING")
                        str_type_document.add("PHOTO")
                        str_type_document.add("ANIMATION")

                        for(doc_type in str_type_document) {
                            FirebaseStorage.getInstance().getReference()
                                .child("user_images/${Firebase.auth.currentUser?.uid.toString()}/$doc_type")
                                .listAll()
                                .addOnSuccessListener { task ->

                                    for (ind in task.items) {
                                        ind.delete()
                                    }
                                }
                        }

                        Firebase.auth.signOut()
                        finish()
                        startActivity(Intent(this, MainActivity::class.java))
                    }
            }
        }

        warning_btn_no.setOnClickListener(){
            warnign_lay.visibility = ConstraintLayout.INVISIBLE
            main_lay.alpha = 1.0f
        }
    }

    private fun anim(layout : ConstraintLayout){

        val alpha_anim = AlphaAnimation(0.0f, 1.0f)
        alpha_anim.duration = 500

        layout.startAnimation(alpha_anim)
    }
}
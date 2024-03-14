package com.example.menuapp

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class edit_profile : AppCompatActivity() {

    lateinit var img : ImageView
    var img_uri : Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        val username : TextView = findViewById(R.id.edit_profile_username)
        val bio : TextView = findViewById(R.id.edit_profile_bio)
        img = findViewById(R.id.edit_profile_img)
        val change_btn : ImageButton = findViewById(R.id.edit_profile_change_btn)
        val save : Button = findViewById(R.id.edit_profile_save)
        val back_btn : ImageButton = findViewById(R.id.edit_profile_back)
        val warning_btn : Button = findViewById(R.id.settings_warning_btn)
        val main_lay : ConstraintLayout = findViewById(R.id.edit_profile_main_lay)
        var str : String = ""

        val animation = AlphaAnimation(0.5f, 1.0f)
        animation.duration = 500
        main_lay.startAnimation(animation)

        val ref = FirebaseFirestore.getInstance().collection("users").document(Firebase.auth.currentUser?.uid.toString())
        val ref_avatar = FirebaseStorage.getInstance().getReference()
            .child("user_images/${Firebase.auth.currentUser?.uid.toString()}/AVATAR")

        ref_avatar.listAll()
            .addOnSuccessListener { task ->

                if(task.items.size > 0) {

                    task.items[0].downloadUrl
                        .addOnCompleteListener { task_aux ->
                            if (task_aux.isSuccessful)
                                Glide
                                    .with(this)
                                    .load(task_aux.result)
                                    .circleCrop()
                                    .into(img)
                        }
                }
            }

        ref.get()
            .addOnSuccessListener { task_aux ->
                if(task_aux.exists()){

                    username.text = task_aux.getString("nickname")
                    bio.text = task_aux.getString("bio")

                    save.setOnClickListener(){

                        save.startAnimation(animation)

                        FirebaseFirestore.getInstance().collection("users").whereEqualTo("nickname", username.text.toString()).get()
                            .addOnSuccessListener { task ->

                                if((task.isEmpty && Regex("[a-zA-z]").containsMatchIn(username.text.toString())) || username.text.toString() == task_aux.getString("nickname")){

                                    if(img_uri != null) {

                                            ref_avatar.listAll()
                                            .addOnSuccessListener { task ->

                                                val ref_child =
                                                    FirebaseStorage.getInstance()
                                                        .getReference("user_images/${Firebase.auth.currentUser?.uid.toString()}/AVATAR/AVATAR")

                                                ref_child.putFile(img_uri!!)
                                            }
                                    }

                                    ref.update(
                                        mapOf(
                                            "nickname" to username.text.toString(),
                                            "bio" to bio.text.toString(),
                                        )
                                    )
                                        .addOnSuccessListener {
                                            finish()
                                        }
                                }
                                else{

                                    main_lay.alpha = 0.1f

                                    warning_btn.visibility = Button.VISIBLE
                                    warning_btn.startAnimation(animation)

                                    if(!task.isEmpty)
                                        warning_btn.text = "Already existing username"
                                    else
                                        warning_btn.text = "Invalid username"
                                }
                            }
                    }
                }
            }

        change_btn.setOnClickListener(){

            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"

            startActivityForResult(intent, 0);
        }

        warning_btn.setOnClickListener(){
            warning_btn.visibility = Button.INVISIBLE
            main_lay.alpha = 1.0f
        }

        back_btn.setOnClickListener(){
            back_btn.startAnimation(animation)

            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(data?.data != null) {

            val img_data = data?.data
            img_uri = img_data

            val options = RequestOptions()

            Glide
                .with(this)
                .load(img_data)
                .apply(options)
                .circleCrop()
                .into(img)
        }
    }
}
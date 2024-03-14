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
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class settings_report_users : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_report_users)

        val btn_back : ImageButton = findViewById(R.id.edit_profile_back)
        val user : TextInputEditText = findViewById(R.id.add_input_tag)
        val reason : TextInputEditText = findViewById(R.id.settings_report_user_name)
        val send_btn : Button = findViewById(R.id.add_tag_btn)
        val warning_btn : Button = findViewById(R.id.settings_warning_btn)
        val main_lay : ConstraintLayout = findViewById(R.id.settings_restrict_main_lay)

        send_btn.visibility = Button.VISIBLE

        val animation = AlphaAnimation(0.0f, 1.0f)
        animation.duration = 500
        main_lay.startAnimation(animation)

        val animation_down = AlphaAnimation(1.0f, 0.0f)
        animation_down.duration = 500
        animation_down.fillAfter = true

        btn_back.setOnClickListener(){
            val btn_back_animation = AlphaAnimation(0.1f, 1.0f)
            btn_back_animation.duration = 1000

            btn_back.startAnimation(btn_back_animation)
            finish()
        }

        if(intent.getStringExtra("post_id") != "null") {
            user.visibility = TextInputEditText.INVISIBLE

            val reason_lay : TextInputLayout = findViewById(R.id.settings_report_user_name_lay)
            val reasonParLay = reason_lay.layoutParams as ConstraintLayout.LayoutParams
            reasonParLay.topToBottom = R.id.settings_restrict_notif_aux_text1

            reason_lay.layoutParams = reasonParLay
        }

        val user_ref = FirebaseFirestore.getInstance().collection("reported_users_posts").document("reported_users_posts")

        user_ref.get()
            .addOnSuccessListener { task ->

                var size_post = task.getLong("size_post")
                var size_user = task.getLong("size_user")

                if(task.exists()) {
                    send_btn.setOnClickListener(){

                        send_btn.startAnimation(animation_down)

                        warning_btn.visibility = Button.VISIBLE
                        warning_btn.startAnimation(animation)

                        main_lay.alpha = 0.1f

                        FirebaseFirestore.getInstance().collection("users").whereEqualTo("nickname", user.text.toString()).get()
                            .addOnSuccessListener { task_verify ->
                                if((!task_verify.isEmpty && Regex("[a-zA-z]").containsMatchIn(reason.text.toString())) || (user.visibility == TextInputEditText.INVISIBLE)){

                                    if(intent.getStringExtra("post_id") == "null") {

                                        size_user = size_user!! + 1

                                        var value_for_report =
                                            "${task_verify.documents[0].id} | (${reason.text.toString()})"

                                        user_ref.update(
                                            mapOf(
                                                "size_user" to size_user,
                                                "USER_$size_user" to value_for_report
                                            )
                                        )
                                    }
                                    else {
                                        Log.i("tagg", intent.getStringExtra("post_id").toString())
                                        size_post = size_post!! + 1

                                        var value_for_report: String =
                                            "${intent.getStringExtra("post_id")} | (${reason.text.toString()})"

                                        user_ref.update(
                                            mapOf(
                                                "size_post" to size_post,
                                                "POST_$size_post" to value_for_report
                                            )
                                        )
                                    }

                                    user.setText("")
                                    reason.setText("")

                                    warning_btn.text = "Report sent"
                                }
                                else{
                                    warning_btn.text = "Non-existing user"
                                }
                            }
                    }
                }
            }

        warning_btn.setOnClickListener(){

            warning_btn.visibility = Button.INVISIBLE
            main_lay.alpha = 1.0f
        }
    }
}
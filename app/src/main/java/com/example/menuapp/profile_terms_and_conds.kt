package com.example.menuapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.animation.AlphaAnimation
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore

class profile_terms_and_conds : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_terms_and_conds)

        val btn : Button = findViewById(R.id.terms_cond_btn)
        val text : TextView = findViewById(R.id.terms_cond)
        val db = FirebaseFirestore.getInstance()
        val main_lay : LinearLayout = findViewById(R.id.settings_terms_conds_main_lay)

        val animation = AlphaAnimation(0.0f, 1.0f)
        animation.duration = 500
        main_lay.startAnimation(animation)

        db.collection("adittional_info").document("terms_cond").get()
            .addOnSuccessListener { task ->
                if(task.exists()){
                    val terms_and_conds = task.getString("text")
                    text.text = Html.fromHtml(terms_and_conds, Html.FROM_HTML_MODE_LEGACY)
                }
            }

        btn.setOnClickListener(){
            btn.startAnimation(animation)

            finish()
        }
    }
}
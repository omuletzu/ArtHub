package com.example.menuapp

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class navigation_menu : AppCompatActivity() {

    lateinit var btn1 : ImageButton
    lateinit var btn2  : ImageButton
    lateinit var btn3 : ImageButton
    lateinit var btn4 : ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_navigation_menu)

        btn1 = findViewById(R.id.btn1)
        btn2 = findViewById(R.id.btn2)
        btn3 = findViewById(R.id.btn3)
        btn4 = findViewById(R.id.btn4)

        var btn_last : Int = 0

        btn1.setOnClickListener(){

            if(btn_last != 1) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.nav_frame,
                        frag_battle(R.layout.battle_art_lay)).commit()

                alphaOn(btn1)
                alphaOff(btn2, btn3, btn4)

                btn_last = 1
            }
        }

        btn2.setOnClickListener(){

            supportFragmentManager.beginTransaction().replace(
                R.id.nav_frame,
                frag_explore(R.layout.explore_lay, supportFragmentManager)
            ).commit()

            alphaOn(btn2)
            alphaOff(btn1, btn3, btn4)

            btn_last = 2
        }

        btn3.setOnClickListener(){

            if(btn_last != 3) {
                supportFragmentManager.beginTransaction().replace(
                    R.id.nav_frame,
                    frag_add_art(R.layout.add_art_lay, supportFragmentManager, intent.getStringExtra("username_main"))
                ).commit()

                alphaOn(btn3)
                alphaOff(btn2, btn1, btn4)

                btn_last = 3
            }
        }

        btn4.setOnClickListener(){

            if(btn_last != 4) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.nav_frame, frag_profile(R.layout.profile_lay, this, Firebase.auth.currentUser?.uid.toString(), false)).commit()

                alphaOn(btn4)
                alphaOff(btn2, btn3, btn1)

                btn_last = 4
            }
        }

        btn2.performClick()
    }

    private fun alphaOn(btn : ImageButton){
        btn.alpha = 0f
        btn.animate().alpha(0.5f).duration = 100
    }

    private fun alphaOff(btn1 : ImageButton, btn2 : ImageButton, btn3 : ImageButton){
        btn1.alpha = 1f
        btn2.alpha = 1f
        btn3.alpha = 1f
    }

    override fun onDestroy() {
        super.onDestroy()

        val db = FirebaseFirestore.getInstance()
        db.collection("users").document(Firebase.auth.currentUser?.uid.toString()).get()
            .addOnSuccessListener { querry ->
                if(querry.exists()){
                    if(querry.getBoolean("rememberMe") == false)
                        Firebase.auth.signOut()
                }
            }
    }
}
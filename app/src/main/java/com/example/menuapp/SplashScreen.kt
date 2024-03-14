package com.example.menuapp

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.animation.AlphaAnimation
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        /*val sharePref = this.getSharedPreferences("shared_pref", Context.MODE_PRIVATE)

        if(!sharePref.getBoolean("dark_mode", false))
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        else
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)*/

        val animation = AlphaAnimation(0.1f, 1.0f)
        animation.duration = 750

        val splash_screen: ConstraintLayout = findViewById(R.id.splash_screen)
        splash_screen.startAnimation(animation)

        FirebaseFirestore.getInstance().collection("users")
            .document(Firebase.auth.currentUser?.uid.toString())
            .get()
            .addOnCompleteListener { task ->

                if (Firebase.auth.currentUser == null) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
                else {

                    if (task.isSuccessful) {
                        if (task.result.getBoolean("dark_mode") == true)
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                        else
                            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

                        if (task.result.getBoolean("rememberMe").toString().toBoolean()) {
                            startActivity(Intent(this, navigation_menu::class.java))
                        } else {
                            Firebase.auth.signOut()
                            startActivity(Intent(this, MainActivity::class.java))
                        }

                        finish()
                    }
                }
            }
            .addOnFailureListener {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
    }
}
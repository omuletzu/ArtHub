package com.example.menuapp

import android.content.Context
import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.Fragment

class first_time_login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_time_login)

        val backImg : ImageView = findViewById(R.id.first_login_gif)                                             // gif animation
        val backImgGIF = backImg.drawable as AnimationDrawable

        backImgGIF.start()

        val btn1 : AppCompatImageButton = findViewById(R.id.first_login_button_left)
        val btn2 : AppCompatImageButton = findViewById(R.id.first_login_button_right)
        val descrp : TextView = findViewById(R.id.first_login_descrp)
        val ok_text : TextView = findViewById(R.id.first_login_ok_btn)
        var fragment_nr : Int = 1
        val animation = AlphaAnimation(0.0f, 1.0f)
        animation.duration = 500

        supportFragmentManager.beginTransaction().replace(R.id.first_login_frame, fragment_lay(R.layout.first_login_frame1)).commit()
        btn1.setImageDrawable(resources.getDrawable(R.drawable.trans_drawable))

        btn1.setOnClickListener(){

            btn1.startAnimation(animation)

            when(fragment_nr){

                1 -> null
                2 -> {supportFragmentManager.beginTransaction().replace(R.id.first_login_frame, fragment_lay(R.layout.first_login_frame1)).commit()
                        fragment_nr--
                        btn1.setImageDrawable(resources.getDrawable(R.drawable.trans_drawable))
                        descrp.text = "INTRODUCTION"}
                3 -> {supportFragmentManager.beginTransaction().replace(R.id.first_login_frame, fragment_lay(R.layout.first_login_frame2)).commit()
                        fragment_nr--
                        btn2.setImageDrawable(resources.getDrawable(R.drawable.left_arrow))
                        descrp.text = "FEATURES"
                        ok_text.visibility = TextView.INVISIBLE}
            }
        }

        btn2.setOnClickListener(){

            btn2.startAnimation(animation)

            when(fragment_nr){

                1 -> {supportFragmentManager.beginTransaction().replace(R.id.first_login_frame, fragment_lay(R.layout.first_login_frame2)).commit()
                        fragment_nr++
                    btn1.setImageDrawable(resources.getDrawable(R.drawable.left_arrow))
                    descrp.text = "FEATURES"}
                2 -> {supportFragmentManager.beginTransaction().replace(R.id.first_login_frame, fragment_lay(R.layout.first_login_frame3)).commit()
                    fragment_nr++
                    btn2.setImageDrawable(resources.getDrawable(R.drawable.trans_drawable))
                    descrp.text = "EXTRAS"
                    ok_text.visibility = TextView.VISIBLE}
                3 -> {
                    val intent = Intent(
                        this,
                        navigation_menu::class.java
                    )

                    intent.putExtra("username_main", getIntent().getStringExtra("username_temp").toString())

                    val sharedPref = getSharedPreferences("dark_mode", Context.MODE_PRIVATE).edit()
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                    sharedPref.putBoolean("dark_mode", false)
                    sharedPref.apply()

                    startActivity(intent)
                    finish()
                }
            }
        }
    }
}

class fragment_lay(val xml_layout : Int) : Fragment(){

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(xml_layout, container, false)
    }
}
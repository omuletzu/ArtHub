package com.example.menuapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.LinearLayout.LayoutParams
import android.widget.RadioButton
import android.widget.Spinner
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class settings_quiet_hours : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_quiet_hours)

        val btn_back : ImageButton = findViewById(R.id.edit_profile_back)
        val spinner_from : Spinner = findViewById(R.id.settings_quiet_from_spinner)
        val spinner_until : Spinner = findViewById(R.id.settings_quiet_until_spinner)
        val save : Button = findViewById(R.id.edit_profile_save)
        val btn_on : RadioButton = findViewById(R.id.settings_quiet_on)
        val btn_off : RadioButton = findViewById(R.id.settings_quiet_off)
        val const_lay : ConstraintLayout = findViewById(R.id.quiet_const_lay)
        val btn_save_ParLay = save.layoutParams as ConstraintLayout.LayoutParams
        var str_from : String = ""
        var str_until : String = ""

        val animation = AlphaAnimation(0.1f, 1.0f)
        animation.duration = 1000

        val countries = ArrayList<String>()

        for(ind in 0 until 25)
            countries.add(ind.toString())

        val adapter = ArrayAdapter(this, R.layout.spinner_list, countries)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spinner_until.adapter = adapter
        spinner_from.adapter = adapter

        FirebaseFirestore.getInstance().collection("users").document(Firebase.auth.currentUser?.uid.toString()).get()
            .addOnCompleteListener { task ->

                if(task.isSuccessful) {

                    str_from = task.result.getString("quiet_from").toString()
                    str_until = task.result.getString("quiet_until").toString()

                    if(task.result.getBoolean("quiet_enabled").toString() == "true") {
                        btn_on.isChecked = true

                        const_lay.visibility = ConstraintLayout.VISIBLE
                        btn_save_ParLay.topToBottom = R.id.quiet_const_lay
                        save.layoutParams = btn_save_ParLay
                    }
                    else {
                        btn_off.isChecked = true

                        const_lay.visibility = ConstraintLayout.INVISIBLE
                        btn_save_ParLay.topToBottom = R.id.quiet_radio_lay
                        save.layoutParams = btn_save_ParLay
                    }

                    spinner_from.setSelection(countries.indexOf(str_from))
                    spinner_until.setSelection(countries.indexOf(str_until))

                    spinner_from.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                            spinner_from.setSelection(pos)
                            str_from = countries[pos]
                        }

                        override fun onNothingSelected(p0: AdapterView<*>?) {
                        }
                    }

                    spinner_until.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                            spinner_until.setSelection(pos)
                            str_until = countries[pos]
                        }

                        override fun onNothingSelected(p0: AdapterView<*>?) {
                        }
                    }
                }

                save.setOnClickListener(){

                    save.startAnimation(animation)

                    FirebaseFirestore.getInstance().collection("users").document(Firebase.auth.currentUser?.uid.toString()).update(
                        mapOf(
                            "quiet_from" to str_from,
                            "quiet_until" to str_until,
                            "quiet_enabled" to btn_on.isChecked
                        )
                    )
                }
            }

        btn_on.setOnClickListener(){
            const_lay.visibility = ConstraintLayout.VISIBLE
            btn_off.isChecked = false

            btn_save_ParLay.topToBottom = R.id.quiet_const_lay
            save.layoutParams = btn_save_ParLay
        }

        btn_off.setOnClickListener(){
            const_lay.visibility = ConstraintLayout.INVISIBLE
            btn_on.isChecked = false

            btn_save_ParLay.topToBottom = R.id.quiet_radio_lay
            save.layoutParams = btn_save_ParLay
        }

        btn_back.setOnClickListener(){
            finish()
        }
    }
}
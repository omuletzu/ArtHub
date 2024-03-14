package com.example.menuapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.Spinner
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class settings_change_pers_info : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings_change_pers_info)

        val password : TextView = findViewById(R.id.edit_profile_username)      // password
        val conf_password : TextView = findViewById(R.id.edit_profile_bio)                // confirm password
        val email : TextView = findViewById(R.id.edit_profile_email)
        val phone : TextView = findViewById(R.id.edit_profile_phone)
        val save : Button = findViewById(R.id.edit_profile_save)
        val back_btn : ImageButton = findViewById(R.id.edit_profile_back)
        val spinner : Spinner = findViewById(R.id.edit_profile_spinner)
        var str : String = ""

        val warning : TextView = findViewById(R.id.settings_change_warning)
        val close_warning : Button = findViewById(R.id.settings_close_warning)
        val scrollview : ScrollView = findViewById(R.id.edit_profile_scrollView)
        val main_lay : ConstraintLayout = findViewById(R.id.settings_change_main_lay)

        val animation = AlphaAnimation(0.0f, 1.0f)
        animation.duration = 500
        main_lay.startAnimation(animation)

        close_warning.setOnClickListener(){
            warning.visibility = LinearLayout.INVISIBLE
            close_warning.visibility = Button.INVISIBLE
            scrollview.alpha = 1.0f
        }

        val ref = FirebaseFirestore.getInstance().collection("users").document(Firebase.auth.currentUser?.uid.toString())

        ref.get()
            .addOnSuccessListener { task ->
                if(task.exists()){

                    email.text = task.getString("adress")
                    phone.text = task.getString("phone_number")

                    val countries = ArrayList<String>()
                    val buf = resources.openRawResource(R.raw.countries).bufferedReader()
                    var str : String? = ""

                    while(str != null){
                        str = buf.readLine()
                        if(str != null)
                            countries.add(str)
                    }

                    val adapter = ArrayAdapter(this, R.layout.spinner_list, countries)
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                    spinner.adapter = adapter

                    val country : String? = task.getString("country")
                    spinner.setSelection(countries.indexOf(country))

                    spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

                        override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                            str = countries[pos]
                        }

                        override fun onNothingSelected(p0: AdapterView<*>?) {
                        }
                    }

                    save.setOnClickListener(){

                        val save_animation = AlphaAnimation(0.1f, 1.0f)
                        save_animation.duration = 1000
                        save.startAnimation(save_animation)

                        if(password.text.toString() == "" && conf_password.text.toString() == "") {
                            if(Regex("[0-9]").matches(phone.text.toString()) && email.text.toString().contains("@") && email.text.toString().contains(".")) {
                                ref.update(
                                    mapOf(
                                        "adress" to email.text.toString().trim(),
                                        "phone_number" to phone.text.toString().trim(),
                                        "country" to str
                                    )
                                )
                                    .addOnSuccessListener {
                                        finish()
                                    }
                            }
                        }
                        else{

                            val pass = password.text.toString()

                            if(pass.equals(conf_password.text.toString())){
                                if(Regex("[A-Z]").matches(pass) && Regex("[0-9]").matches(pass)){
                                    if(pass.length > 6){

                                        ref.update(
                                            mapOf(
                                                "password" to pass,
                                                "adress" to email.text.toString().trim(),
                                                "phone_number" to phone.text.toString().trim(),
                                                "country" to str
                                            )
                                        )
                                    }
                                    else{
                                        warning.text = "Too short password"
                                        warning.visibility = LinearLayout.VISIBLE
                                        close_warning.visibility = Button.VISIBLE
                                        scrollview.alpha = 0.1f
                                    }
                                }
                                else{
                                    warning.startAnimation(animation)
                                    warning.text = "Password doesn't contain uppercase letter or digits"
                                    warning.visibility = LinearLayout.VISIBLE
                                    close_warning.visibility = Button.VISIBLE
                                    scrollview.alpha = 0.1f
                                }
                            }
                            else{
                                warning.startAnimation(animation)
                                warning.text = "Passwords don't match"
                                warning.visibility = LinearLayout.VISIBLE
                                close_warning.visibility = Button.VISIBLE
                                scrollview.alpha = 0.1f
                            }
                        }
                    }
                }
            }

        back_btn.setOnClickListener(){
            finish()

            back_btn.startAnimation(animation)
        }
    }
}
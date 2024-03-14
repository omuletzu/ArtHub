package com.example.menuapp

import android.app.UiModeManager
import android.content.ContentResolver
import android.content.Intent
import android.content.res.Configuration
import android.graphics.drawable.AnimationDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Spinner
import android.widget.SpinnerAdapter
import android.widget.TextView
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firestore.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val backImg : ImageView = findViewById(R.id.background)                                             // gif animation
        val backImgGIF = backImg.drawable as AnimationDrawable

        backImgGIF.start()                                                                                  // starting the animation

        val btn1 : Button = findViewById(R.id.login_switch1)
        val btn2 : Button = findViewById(R.id.login_switch2)
        var ok : Int = 0

        btn1.setOnClickListener(){
            if(ok != 1) {                                           //btn1 e login
                supportFragmentManager.beginTransaction()
                    .replace(R.id.login_temp_frame, login_fragment(R.layout.login_layout, 0))
                    .commit()
                btn1.background = resources.getDrawable(R.drawable.login_button_background)
                btn2.background = resources.getDrawable(R.drawable.trans_drawable)
                ok = 1
            }
        }

        btn2.setOnClickListener(){
            if(ok != 0) {                                           //btn2 e register
                supportFragmentManager.beginTransaction()
                    .replace(R.id.login_temp_frame, login_fragment(R.layout.register_layout, 1))
                    .commit()
                btn1.background = resources.getDrawable(R.drawable.trans_drawable)
                btn2.background = resources.getDrawable(R.drawable.register_button_background)
                ok = 0
            }
        }

        btn1.performClick()                             //initial e la login
    }
}

class login_fragment(val xml_layout : Int, val flag : Int) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val inf_lay = inflater.inflate(xml_layout, container, false)
        val btn_sign_in: Button = inf_lay.findViewById(R.id.login_sign_in)

        val db_sql = user_database(inf_lay.context).writableDatabase

        val db = FirebaseFirestore.getInstance()

        val animation = AlphaAnimation(0.5f, 1.0f)
        animation.duration = 500

        if(Firebase.auth.currentUser != null){

            val uid = Firebase.auth.currentUser!!.uid.toString()
            db.collection("users").document(uid).get()
                .addOnSuccessListener { querry ->
                    if(querry.exists()){
                        val rememberMe = querry.getBoolean("rememberMe")
                        if(rememberMe != null)
                            if(rememberMe == true){

                                startActivity(Intent(this.context, navigation_menu::class.java))
                                (activity as MainActivity).finish()
                            }
                    }
                }
        }

        if (flag == 0) {
            btn_sign_in.setOnClickListener() {
                btn_sign_in.startAnimation(animation)

                var username: String =
                    inf_lay.findViewById<TextInputEditText>(R.id.username_inp).text.toString().trim().lowercase()
                val password: String =
                    inf_lay.findViewById<TextInputEditText>(R.id.password_inp).text.toString()

                val message: TextView = inf_lay.findViewById(R.id.login_message)
                val remember_me: CheckBox = inf_lay.findViewById(R.id.login_checkbox)

                if (password != "") {

                    val auth = Firebase.auth
                    db.collection("users").whereEqualTo("nickname", username).get()
                        .addOnSuccessListener { document ->
                            if (!document.isEmpty) {
                                auth.signInWithEmailAndPassword(
                                    document.documents.get(0).getString("adress").toString(),
                                    password
                                )
                                    .addOnCompleteListener { task ->

                                        if (task.isSuccessful) {
                                            val user = db.collection("users")
                                                .document(Firebase.auth.currentUser?.uid.toString())

                                            if (remember_me.isChecked) {
                                                Log.i("tagg", "tag")
                                                user.update(mapOf(
                                                    "rememberMe" to true
                                                ))

                                            } else {
                                                user.update(mapOf(
                                                    "rememberMe" to false
                                                ))

                                            }

                                            val intent = Intent(
                                                inf_lay.context,
                                                navigation_menu::class.java
                                            )

                                            user.update(mapOf(
                                                "disabled" to false
                                            ))

                                            user.update(
                                                mapOf(
                                                    "dark_mode" to false
                                                )
                                            )

                                            FirebaseMessaging.getInstance().token
                                                .addOnCompleteListener { token ->
                                                    if(token.isSuccessful){
                                                        user.update(
                                                            mapOf(
                                                                "token_fcm" to token.result.toString()
                                                            )
                                                        )
                                                    }
                                                }

                                            startActivity(intent)
                                            (activity as MainActivity).finish()

                                        } else
                                            message.text = "*INVALID PASSWORD OR USERNAME"
                                    }
                            } else {
                                auth.signInWithEmailAndPassword(username, password)
                                    .addOnCompleteListener { task ->

                                        if (task.isSuccessful) {
                                            val user = db.collection("users")
                                                .document(Firebase.auth.currentUser?.uid.toString())

                                            val intent = Intent(
                                                inf_lay.context,
                                                navigation_menu::class.java
                                            )

                                            if (remember_me.isChecked) {
                                                user.update(mapOf(
                                                    "rememberMe" to true
                                                ))
                                            } else {
                                                user.update(mapOf(
                                                    "rememberMe" to false
                                                ))
                                            }

                                            user.update(
                                                mapOf(
                                                    "dark_mode" to false
                                                )
                                            )

                                            startActivity(intent)
                                            (activity as MainActivity).finish()
                                        }
                                    }
                            }
                                .addOnFailureListener {
                                    message.text = "*INVALID PASSWORD OR USERNAME"
                                }
                        }
                }
            }
        }
            else{
            var country : String = "Select a country"

            btn_sign_in.setOnClickListener() {
                btn_sign_in.startAnimation(animation)

                val username: String =
                    inf_lay.findViewById<TextInputEditText>(R.id.username_inp).text.toString().trim().lowercase()
                val password: String =
                    inf_lay.findViewById<TextInputEditText>(R.id.password_inp).text.toString()

                val nickname: String =
                    inf_lay.findViewById<TextInputEditText>(R.id.nickname_inp).text.toString().trim().lowercase()
                val conf_password: String =
                    inf_lay.findViewById<TextInputEditText>(R.id.confirm_password_inp).text.toString()
                val message: TextView = inf_lay.findViewById(R.id.login_message)
                val phone_number : String = inf_lay.findViewById<TextInputEditText>(R.id.phone_number).text.toString().trim()

                if (password == conf_password && country != "Select a country" && (Regex("[0-9]+").matches(phone_number) || phone_number == "")) {

                    if(password.length < 6)
                        message.text = "*PASSWORD MUST CONTAIN AT LEAST 6 CHARACTERS"

                    if(Regex("[a-z]").containsMatchIn(username)) {
                        if (Regex("[A-Z]").containsMatchIn(password) && Regex("[0-9]").containsMatchIn(password)) {

                            val auth = Firebase.auth
                            auth.createUserWithEmailAndPassword(username, password)
                                .addOnCompleteListener { task ->
                                    if (task.isSuccessful) {

                                        db.collection("users").whereEqualTo("adress", username).get()
                                            .addOnSuccessListener { querry ->

                                                if(querry.isEmpty) {

                                                    var current_user : Long = 0

                                                    val reference = db.collection("adittional_info").document("current_user_number").get()
                                                        .addOnSuccessListener { querry ->
                                                            if(querry.exists())
                                                                current_user = querry.getLong("current_user_number")!!
                                                        }

                                                    FirebaseMessaging.getInstance().token
                                                        .addOnCompleteListener { token ->

                                                            db.collection("users")
                                                                .document(task.result.user?.uid.toString())
                                                                .set(
                                                                    hashMapOf(
                                                                        "nickname" to nickname,
                                                                        "adress" to username,
                                                                        "password" to password,
                                                                        "country" to country,
                                                                        "rememberMe" to true,
                                                                        "orderTag" to current_user.plus(1),
                                                                        "phone_number" to phone_number,
                                                                        "bio" to "(No bio)",
                                                                        "disabled" to false,
                                                                        "notification_disabled" to false,
                                                                        "dark_mode" to false,
                                                                        "token_fcm" to token.result.toString(),
                                                                        "quiet_from" to "0",
                                                                        "quiet_until" to "0",
                                                                        "quiet_enabled" to false
                                                                    )
                                                                )
                                                                .addOnFailureListener {
                                                                    message.text =
                                                                        "*SOMETHING WENT WRONG"
                                                                }
                                                                .addOnSuccessListener {
                                                                    db.collection("users")
                                                                        .document(task.result.user?.uid.toString())
                                                                        .collection("saved_liked_posts")
                                                                        .document("saved_posts")
                                                                        .set(
                                                                            hashMapOf("size" to 0)
                                                                        )

                                                                    db.collection("users")
                                                                        .document(task.result.user?.uid.toString())
                                                                        .collection("saved_liked_posts")
                                                                        .document("liked_posts")
                                                                        .set(
                                                                            hashMapOf("size" to 0)
                                                                        )

                                                                    db.collection("users")
                                                                        .document(task.result.user?.uid.toString())
                                                                        .collection("followers")
                                                                        .document("follow").set(
                                                                        hashMapOf("size" to 0)
                                                                    )

                                                                    db.collection("users")
                                                                        .document(task.result.user?.uid.toString())
                                                                        .collection("followers")
                                                                        .document("following").set(
                                                                        hashMapOf("size" to 0)
                                                                    )

                                                                    db.collection("users")
                                                                        .document(task.result.user?.uid.toString())
                                                                        .collection("notification_restricted")
                                                                        .document("notification_restricted")
                                                                        .set(
                                                                            hashMapOf("size" to 0)
                                                                        )

                                                                    db.collection("users")
                                                                        .document(task.result.user?.uid.toString())
                                                                        .collection("blocked_users")
                                                                        .document("blocked_users")
                                                                        .set(
                                                                            hashMapOf("size" to 0)
                                                                        )

                                                                    db.collection("users")
                                                                        .document(task.result.user?.uid.toString())
                                                                        .collection("PAINT_SAVE")
                                                                        .document("PAINT_SAVE")
                                                                        .set(
                                                                            hashMapOf("size" to 0)
                                                                        )

                                                                    db.collection("users")
                                                                        .document(task.result.user?.uid.toString())
                                                                        .collection("PAINT_LIKE")
                                                                        .document("PAINT_LIKE")
                                                                        .set(
                                                                            hashMapOf("size" to 0)
                                                                        )

                                                                    db.collection("users")
                                                                        .document(task.result.user?.uid.toString())
                                                                        .collection("DRAWING_SAVE")
                                                                        .document("DRAWING_SAVE")
                                                                        .set(
                                                                            hashMapOf("size" to 0)
                                                                        )

                                                                    db.collection("users")
                                                                        .document(task.result.user?.uid.toString())
                                                                        .collection("DRAWING_LIKE")
                                                                        .document("DRAWING_LIKE")
                                                                        .set(
                                                                            hashMapOf("size" to 0)
                                                                        )

                                                                    db.collection("users")
                                                                        .document(task.result.user?.uid.toString())
                                                                        .collection("PHOTO_SAVE")
                                                                        .document("PHOTO_SAVE")
                                                                        .set(
                                                                            hashMapOf("size" to 0)
                                                                        )

                                                                    db.collection("users")
                                                                        .document(task.result.user?.uid.toString())
                                                                        .collection("PHOTO_LIKE")
                                                                        .document("PHOTO_LIKE")
                                                                        .set(
                                                                            hashMapOf("size" to 0)
                                                                        )

                                                                    db.collection("users")
                                                                        .document(task.result.user?.uid.toString())
                                                                        .collection("ANIMATION_SAVE")
                                                                        .document("ANIMATION_SAVE")
                                                                        .set(
                                                                            hashMapOf("size" to 0)
                                                                        )

                                                                    db.collection("users")
                                                                        .document(task.result.user?.uid.toString())
                                                                        .collection("ANIMATION_LIKE")
                                                                        .document("ANIMATION_LIKE")
                                                                        .set(
                                                                            hashMapOf("size" to 0)
                                                                        )

                                                                    val intent = Intent(
                                                                        inf_lay.context,
                                                                        first_time_login::class.java
                                                                    )

                                                                    startActivity(intent)
                                                                    (activity as MainActivity).finish()
                                                                }
                                                        }
                                                }
                                                else
                                                    message.text = "*ALREADY EXISTING USERNAME"
                                            }

                                    } else {
                                        val excp = task.exception
                                        if (excp is FirebaseAuthUserCollisionException)
                                            message.text = "*ALREADY EXISTING EMAIL"
                                        else
                                            message.text = "*INVALID EMAIL"
                                    }
                                }

                        } else
                            message.text =
                                "*PASSWORD MUST CONTAINT AT LEAST A CAPITAL AND A DIGIT"
                    }
                    else
                        message.text = "*INVALID USERNAME"
                }
                else
                    if(Regex("[0-9]+").matches(phone_number) == false)
                        message.text = "INVALID PHONE NUMBER"
                    else {
                        if (password != conf_password)
                            message.text = "*PASSWORDS DON'T MATCH"
                        else
                            message.text = "*SELECT A COUNTRY"
                    }
            }

            val countries = ArrayList<String>()
            val buf = resources.openRawResource(R.raw.countries).bufferedReader()
            var str : String? = ""

            while(str != null){
                str = buf.readLine()
                if(str != null)
                    countries.add(str)
            }

            val spinner : Spinner = inf_lay.findViewById(R.id.register_spinner)
            val adapter = ArrayAdapter(inf_lay.context, R.layout.spinner_list, countries)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            spinner.adapter = adapter

            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

                override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                    country = countries[pos]
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                }
            }
        }

        return inf_lay
    }
}
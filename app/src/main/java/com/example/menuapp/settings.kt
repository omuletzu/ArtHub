package com.example.menuapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.ScrollView
import androidx.appcompat.app.AppCompatDelegate
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class settings : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val acc_set : ImageButton = findViewById(R.id.settings_btn1)
        val dark_mode : ImageButton = findViewById(R.id.settings_btn2)
        val notification : ImageButton = findViewById(R.id.settings_btn3)
        val block : ImageButton = findViewById(R.id.settings_btn4)
        val sign_out : Button = findViewById(R.id.settings_btn5)
        val frame1 : FrameLayout = findViewById(R.id.settings_frame1)
        val frame2 : FrameLayout = findViewById(R.id.settings_frame2)
        val frame3 : FrameLayout = findViewById(R.id.settings_frame3)
        val frame4 : FrameLayout = findViewById(R.id.settings_frame4)
        val linLay2 : LinearLayout = findViewById(R.id.settings_lay2)
        val linLay3 : LinearLayout = findViewById(R.id.settings_lay3)
        val linLay4 : LinearLayout = findViewById(R.id.settings_lay4)
        val view2 : View = findViewById(R.id.settings_aux_view2)
        val view3 : View = findViewById(R.id.settings_aux_view3)
        val view4 : View = findViewById(R.id.settings_aux_view4)
        val view5 : View = findViewById(R.id.settings_aux_view5)
        val view6 : View = findViewById(R.id.settings_aux_view6)
        val view7 : View = findViewById(R.id.settings_aux_view7)
        val view8 : View = findViewById(R.id.settings_aux_view8)
        val view9 : View = findViewById(R.id.settings_aux_view9)
        val btn_back : ImageButton = findViewById(R.id.gallery_btn_back)
        val warning : ConstraintLayout = findViewById(R.id.settings_delete_warning_lay)
        val warning_btn_yes : Button = findViewById(R.id.settings_delete_yes)
        val warning_btn_no : Button = findViewById(R.id.settings_delete_no)
        val scroll_view : ScrollView = findViewById(R.id.settings_scroll_view)
        val warning_dark : ConstraintLayout = findViewById(R.id.settings_dark_restart_warning_lay)
        val warning_dark_btn_ok : Button = findViewById(R.id.settings_dark_restart_ok)

        val animation = AlphaAnimation(0.1f, 1.0f)
        animation.duration = 500

        sign_out.setOnClickListener(){
            sign_out.startAnimation(animation)

            warning.visibility = ConstraintLayout.VISIBLE
            warning.startAnimation(animation)
            scroll_view.alpha = 0.1f

            warning_btn_yes.setOnClickListener() {

                val user_ref = FirebaseFirestore.getInstance().collection("users")
                    .document(Firebase.auth.currentUser?.uid.toString())

                user_ref.get()
                    .addOnSuccessListener { task ->
                        if (task.exists()) {

                            user_ref.update(
                                mapOf(
                                    "rememberMe" to false
                                )
                            )

                            Firebase.auth.signOut()
                            val intent = Intent(this, SplashScreen::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                    }
            }

            warning_btn_no.setOnClickListener(){
                warning.visibility = ConstraintLayout.INVISIBLE
                scroll_view.alpha = 1.0f
            }
        }

        supportFragmentManager.beginTransaction().replace(R.id.settings_frame1, settings_frag(R.layout.settings_account_set)).commit()
        supportFragmentManager.beginTransaction().replace(R.id.settings_frame2, settings_dark_mode_frame(R.layout.settings_dark_mode, warning_dark, warning_dark_btn_ok, scroll_view)).commit()
        supportFragmentManager.beginTransaction().replace(R.id.settings_frame3, settings_frag(R.layout.settings_notification)).commit()
        supportFragmentManager.beginTransaction().replace(R.id.settings_frame4, settings_frag(R.layout.settings_block)).commit()

        var btn1_flag = false
        var btn2_flag = false
        var btn3_flag = false
        var btn4_flag = false

        val layF2Par = linLay2.layoutParams as ConstraintLayout.LayoutParams
        val layF3Par = linLay3.layoutParams as ConstraintLayout.LayoutParams
        val layF4Par = linLay4.layoutParams as ConstraintLayout.LayoutParams
        val btnPar = sign_out.layoutParams as ConstraintLayout.LayoutParams

        acc_set.setOnClickListener(){

            if(btn1_flag == false) {
                frame1.visibility = FrameLayout.VISIBLE
                view2.visibility = View.VISIBLE
                view6.visibility = View.INVISIBLE
                layF2Par.topToBottom = R.id.settings_frame1

                btn_anim(acc_set, false, frame1)
            }
            else {
                frame1.visibility = FrameLayout.INVISIBLE
                view2.visibility = View.INVISIBLE
                view6.visibility = View.VISIBLE
                layF2Par.topToBottom = R.id.settings_lay1

                btn_anim(acc_set, true, frame1)
            }

            linLay2.layoutParams = layF2Par
            btn1_flag = !btn1_flag
        }

        dark_mode.setOnClickListener(){
            if(btn2_flag == false) {
                frame2.visibility = FrameLayout.VISIBLE
                view3.visibility = View.VISIBLE
                view7.visibility = View.INVISIBLE
                layF3Par.topToBottom = R.id.settings_frame2

                btn_anim(dark_mode, false, frame2)
            }
            else {
                frame2.visibility = FrameLayout.INVISIBLE
                view3.visibility = View.INVISIBLE
                view7.visibility = View.VISIBLE
                layF3Par.topToBottom = R.id.settings_lay2

                btn_anim(dark_mode, true, frame2)
            }

            linLay3.layoutParams = layF3Par
            btn2_flag = !btn2_flag
        }

        notification.setOnClickListener(){
            if(btn3_flag == false) {
                frame3.visibility = FrameLayout.VISIBLE
                view4.visibility = View.VISIBLE
                view8.visibility = View.INVISIBLE
                layF4Par.topToBottom = R.id.settings_frame3

                btn_anim(notification, false, frame3)
            }
            else {
                frame3.visibility = FrameLayout.INVISIBLE
                view4.visibility = View.INVISIBLE
                view8.visibility = View.VISIBLE
                layF4Par.topToBottom = R.id.settings_lay3

                btn_anim(notification, true, frame3)
            }

            linLay4.layoutParams = layF4Par
            btn3_flag = !btn3_flag
        }

        block.setOnClickListener(){
            if(btn4_flag == false) {
                frame4.visibility = FrameLayout.VISIBLE
                view5.visibility = View.VISIBLE
                view9.visibility = View.INVISIBLE
                btnPar.topToBottom = R.id.settings_frame4

                btn_anim(block, false, frame4)
            }
            else {
                frame4.visibility = FrameLayout.INVISIBLE
                view5.visibility = View.INVISIBLE
                view9.visibility = View.VISIBLE
                btnPar.topToBottom = R.id.settings_lay4

                btn_anim(block, true, frame4)
            }

            sign_out.layoutParams = btnPar
            btn4_flag = !btn4_flag
        }

        btn_back.setOnClickListener(){
            finish();
        }
    }

    private fun btn_anim(btn : ImageButton, mod : Boolean, frame : FrameLayout){

        if(mod == false) {
            val animation = AnimationUtils.loadAnimation(this, R.anim.btn_rotation_90)
            animation.fillAfter = true
            btn.startAnimation(animation)

            val anim_alpha = AlphaAnimation(0f, 1f)
            anim_alpha.duration = 500
            frame.startAnimation(anim_alpha)
        }
        else{
            val animation = AnimationUtils.loadAnimation(this, R.anim.btn_rotation_minus_90)
            animation.fillAfter = true
            btn.startAnimation(animation)
        }
    }
}

class settings_frag(val xml_layout : Int) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val inf_lay = inflater.inflate(xml_layout, container, false)

        if(xml_layout == R.layout.settings_account_set){

            val change_data : Button = inf_lay.findViewById(R.id.settings_change_data)
            val delete_acc : Button = inf_lay.findViewById(R.id.settings_delete_acc)
            val terms_conds : Button = inf_lay.findViewById(R.id.settings_terms_conds)

            change_data.setOnClickListener(){
                startActivity(Intent(this.context, settings_change_pers_info::class.java))
            }

            delete_acc.setOnClickListener(){
                startActivity(Intent(this.context, settings_delete_account::class.java))
            }

            terms_conds.setOnClickListener(){
                startActivity(Intent(this.context, profile_terms_and_conds::class.java))
            }
        }

        if(xml_layout == R.layout.settings_dark_mode){


        }

        if(xml_layout == R.layout.settings_notification){

            val stop_not_on : RadioButton = inf_lay.findViewById(R.id.settings_stop_not_on)
            val stop_not_off : RadioButton = inf_lay.findViewById(R.id.settings_stop_not_off)
            val restrict_notif : Button = inf_lay.findViewById(R.id.settings_restrict_notif)
            val quiet_hrs : Button = inf_lay.findViewById(R.id.settings_quiet_hrs)

            val user_ref = FirebaseFirestore.getInstance().collection("users").document(Firebase.auth.currentUser?.uid.toString())

            user_ref.get()
                .addOnSuccessListener { task ->
                    if(task.exists()){

                        stop_not_on.isChecked = task.getBoolean("notification_disabled").toString().toBoolean()
                        stop_not_off.isChecked = !(stop_not_on.isChecked)

                        stop_not_on.setOnClickListener(){
                            user_ref.update(mapOf("notification_disabled" to true))
                            stop_not_off.isChecked = false
                        }

                        stop_not_off.setOnClickListener(){
                            user_ref.update(mapOf("notification_disabled" to false))
                            stop_not_on.isChecked = false
                        }
                    }
                }

            restrict_notif.setOnClickListener(){
                startActivity(Intent(this.context, settings_restricted_notif::class.java))
            }

            quiet_hrs.setOnClickListener(){
                startActivity(Intent(this.context, settings_quiet_hours::class.java))
            }
        }

        if(xml_layout == R.layout.settings_block){

            val block_unblock : Button = inf_lay.findViewById(R.id.settings_block_unblock)
            val report_user : Button = inf_lay.findViewById(R.id.settings_report)
            //val mut_unblock : Button = inf_lay.findViewById(R.id.settings_mut_unblock)

            block_unblock.setOnClickListener(){
                startActivity(Intent(this.context, settings_block_users::class.java))
            }

            report_user.setOnClickListener(){
                val intent = Intent(this.context, settings_report_users::class.java)
                intent.putExtra("post_id", "null")
                startActivity(intent)
            }

            /*mut_unblock.setOnClickListener(){
                startActivity(Intent(this.context, settings_mutual_unblock::class.java))
            }*/
        }

        return inf_lay
    }
}

class settings_dark_mode_frame(val xml_layout : Int, val const_lay : ConstraintLayout, val ok_button : Button, val scrollView: ScrollView) : Fragment(){

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val inf_lay = inflater.inflate(xml_layout, container, false)

        val dark_on : RadioButton = inf_lay.findViewById(R.id.settings_dark_mode_on)
        val dark_off : RadioButton = inf_lay.findViewById(R.id.settings_dark_mode_off)

        val user_ref = FirebaseFirestore.getInstance().collection("users").document(Firebase.auth.currentUser?.uid.toString())

        val animation = AlphaAnimation(0.1f, 1.0f)
        animation.duration = 500

        val sharedPref = inf_lay.context.getSharedPreferences("shared_pref", Context.MODE_PRIVATE).edit()

        user_ref.get()
            .addOnSuccessListener { task ->
                if(task.exists()){

                    dark_on.isChecked = task.getBoolean("dark_mode").toString().toBoolean()
                    dark_off.isChecked = !(dark_on.isChecked)

                    val intent = Intent(this.context, SplashScreen::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)

                    dark_on.setOnClickListener(){

                        if(dark_off.isChecked) {

                            sharedPref.putBoolean("dark_mode", true).apply()
                            user_ref.update(mapOf("dark_mode" to true))
                            dark_off.isChecked = false

                            const_lay.visibility = ConstraintLayout.VISIBLE
                            const_lay.startAnimation(animation)
                            scrollView.alpha = 0.1f
                        }
                    }

                    dark_off.setOnClickListener(){

                        if(dark_on.isChecked) {

                            sharedPref.putBoolean("dark_mode", false).apply()
                            user_ref.update(mapOf("dark_mode" to false))
                            dark_on.isChecked = false

                            const_lay.visibility = ConstraintLayout.VISIBLE
                            const_lay.startAnimation(animation)
                            scrollView.alpha = 0.1f
                        }
                    }

                    ok_button.setOnClickListener() {
                        const_lay.visibility = ConstraintLayout.INVISIBLE
                        scrollView.alpha = 1.0f
                    }
                }
            }

        return inf_lay
    }
}




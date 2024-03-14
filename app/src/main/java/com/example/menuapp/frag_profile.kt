package com.example.menuapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toolbar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage

class frag_profile (val xml_layout : Int, context: Context, val user_id : String, val follow_visib : Boolean) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val inf_lay = inflater.inflate(xml_layout, container, false)
        val img: ImageView = inf_lay.findViewById(R.id.profile_img)
        val bio: TextView = inf_lay.findViewById(R.id.profile_bio)
        val btn_follow: Button = inf_lay.findViewById(R.id.btn_follow)
        val lay_settings_edit: LinearLayout =
            inf_lay.findViewById(R.id.profile_lay_edit_settings)

        val following_number_btn : Button = inf_lay.findViewById(R.id.profile_following_btn)

        val animation = AlphaAnimation(0.5f, 1.0f)
        animation.duration = 500

        FirebaseFirestore.getInstance().collection("users").document(user_id).get()
            .addOnSuccessListener { task ->

                val disabled : Boolean = task.getBoolean("disabled").toString().toBoolean()

                if(disabled == false) {

                    val username: TextView = inf_lay.findViewById(R.id.profile_username)
                    val edit_profile_btn: AppCompatButton = inf_lay.findViewById(R.id.profile_edit)
                    val setting: AppCompatButton = inf_lay.findViewById(R.id.profile_settings)
                    val follow1: TextView = inf_lay.findViewById(R.id.profile_follow1)
                    val follow2: TextView = inf_lay.findViewById(R.id.profile_follow2)
                    val uploads: TextView = inf_lay.findViewById(R.id.profile_upload)
                    val country: TextView = inf_lay.findViewById(R.id.profile_country)
                    val email: TextView = inf_lay.findViewById(R.id.profile_email)
                    val phone_numb: TextView = inf_lay.findViewById(R.id.profile_phone)
                    val share: TextView = inf_lay.findViewById(R.id.profile_share)
                    val menu: NavigationView = inf_lay.findViewById(R.id.profile_menu)

                    val toolbar: androidx.appcompat.widget.Toolbar =
                        inf_lay.findViewById(R.id.profile_btns)
                    val drawer_lay: DrawerLayout = inf_lay.findViewById(R.id.profile_drawer)

                    if(activity != null) {
                        val activity = requireActivity() as AppCompatActivity
                        activity.setSupportActionBar(toolbar)
                        activity.supportActionBar?.title = ""
                    }

                    val drawer_toggle = ActionBarDrawerToggle(
                        inf_lay.context as Activity?,
                        drawer_lay,
                        toolbar,
                        R.string.app_name,
                        R.string.app_name
                    )

                    drawer_lay.addDrawerListener(drawer_toggle)
                    drawer_toggle.syncState()

                    if(activity != null) {
                        val activity = requireActivity() as AppCompatActivity
                        activity.supportActionBar?.setHomeButtonEnabled(true)
                        activity.supportActionBar?.setDisplayHomeAsUpEnabled(true)
                        activity.supportActionBar?.setHomeAsUpIndicator(R.drawable.gallery_icon_sized)
                    }

                    val db = FirebaseFirestore.getInstance()
                    db.collection("users").document(user_id).get()
                        .addOnSuccessListener { task ->

                            if (task.exists()) {
                                username.text = task.getString("nickname")
                                country.text = task.getString("country")
                                phone_numb.text = task.getString("phone_number")
                                email.text = task.getString("adress")
                                bio.text = task.getString("bio")
                            }
                        }

                    val db_st = FirebaseStorage.getInstance().getReference()
                    var item_total: Int = 0

                    db_st.child("user_images/${user_id}/PAINT").listAll()
                        .addOnSuccessListener { task1 ->
                            item_total += task1.items.size

                            db_st.child("user_images/${user_id}/PHOTO").listAll()
                                .addOnSuccessListener { task2 ->
                                    item_total += task2.items.size

                                    db_st.child("user_images/${user_id}/DRAWING").listAll()
                                        .addOnSuccessListener { task3 ->
                                            item_total += task3.items.size

                                            db_st.child("user_images/${user_id}/ANIMATION")
                                                .listAll()
                                                .addOnSuccessListener { task4 ->
                                                    item_total += task4.items.size
                                                    uploads.text = item_total.toString()
                                                }
                                        }
                                }
                        }

                    menu.setNavigationItemSelectedListener { item ->

                        val intent = Intent(this.context, profile_your_gallery::class.java)
                        intent.putExtra("user_id", user_id)

                        if (item.itemId == R.id.profile_paint) {
                            intent.putExtra("gallery_type", "PAINT")
                            intent.putExtra("flag_layout", 0)
                            startActivity(intent)
                        }

                        if (item.itemId == R.id.profile_draw) {
                            intent.putExtra("gallery_type", "DRAWING")
                            intent.putExtra("flag_layout", 1)
                            startActivity(intent)
                        }

                        if (item.itemId == R.id.profile_photo) {
                            intent.putExtra("gallery_type", "PHOTO")
                            intent.putExtra("flag_layout", 2)
                            startActivity(intent)
                        }

                        /*if (item.itemId == R.id.profile_animation) {
                            intent.putExtra("gallery_type", "ANIMATION")
                            intent.putExtra("flag_layout", 3)
                            startActivity(intent)
                        }*/

                        if (item.itemId == R.id.profile_saved) {
                            intent.putExtra("gallery_type", "SAVED")
                            intent.putExtra("flag_layout", 4)
                            startActivity(intent)
                        }

                        if (item.itemId == R.id.profile_liked_posts) {
                            intent.putExtra("gallery_type", "LIKED")
                            intent.putExtra("flag_layout", 5)
                            startActivity(intent)
                        }

                        true
                    }

                    val ref_follow =
                        db.collection("users").document(user_id)
                            .collection("followers").document("follow")
                    val ref_following =
                        db.collection("users").document(Firebase.auth.currentUser?.uid.toString())
                            .collection("followers").document("following")

                    ref_follow.get().addOnSuccessListener { task ->
                        if (task.exists()) follow1.text = "${task.data?.get("size")} FOLLOWERS"
                    }
                    db.collection("users").document(user_id).collection("followers")
                        .document("following").get()
                        .addOnSuccessListener { task ->
                            if (task.exists())
                                follow2.text = "${task.data?.get("size").toString()} FOLLOWING"
                            Log.i("tagg", task.data?.get("size").toString())
                        }

                    if (follow_visib == true) {
                        btn_follow.visibility = Button.VISIBLE
                        lay_settings_edit.visibility = LinearLayout.INVISIBLE
                        following_number_btn.visibility = Button.INVISIBLE

                        ref_follow.get().addOnSuccessListener { task ->

                            if (task.exists()) {

                                if (task.contains(Firebase.auth.currentUser?.uid.toString()) && task.data?.get(
                                        Firebase.auth.currentUser?.uid.toString()
                                    ) == true
                                ) {
                                    btn_follow.text = "UNFOLLOW"
                                    btn_follow.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                        R.drawable.minus_icon_sized,
                                        0,
                                        0,
                                        0
                                    )
                                } else {
                                    btn_follow.text = "FOLLOW"
                                    btn_follow.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                        R.drawable.plus_icon_sized,
                                        0,
                                        0,
                                        0
                                    )
                                }

                                btn_follow.setOnClickListener() {

                                    ref_follow.get().addOnSuccessListener { task_follow ->
                                        ref_following.get().addOnSuccessListener { task_following ->

                                            val animation = AlphaAnimation(0.0f, 1.0f)
                                            animation.duration = 250
                                            btn_follow.startAnimation(animation)

                                            var nr_follower: Int =
                                                task_follow.data?.get("size").toString().toInt()
                                            var nr_following: Int =
                                                task_following.data?.get("size").toString().toInt()

                                            if (task_follow.contains(Firebase.auth.currentUser?.uid.toString()) && task_follow.data?.get(
                                                    Firebase.auth.currentUser?.uid.toString()
                                                ) == true
                                            ) {
                                                btn_follow.text = "FOLLOW"
                                                btn_follow.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                                    R.drawable.plus_icon_sized,
                                                    0,
                                                    0,
                                                    0
                                                )

                                                nr_follower--
                                                nr_following--

                                                ref_follow.update(
                                                    mapOf(
                                                        "size" to nr_follower,
                                                        Firebase.auth.currentUser?.uid.toString() to false
                                                    )
                                                )

                                                ref_following.update(
                                                    mapOf(
                                                        "size" to nr_following,
                                                        user_id to false
                                                    )
                                                )
                                            } else {
                                                btn_follow.text = "UNFOLLOW"
                                                btn_follow.setCompoundDrawablesRelativeWithIntrinsicBounds(
                                                    R.drawable.minus_icon_sized,
                                                    0,
                                                    0,
                                                    0
                                                )

                                                nr_follower++
                                                nr_following++

                                                ref_follow.update(
                                                    mapOf(
                                                        "size" to nr_follower,
                                                        Firebase.auth.currentUser?.uid.toString() to true
                                                    )
                                                )

                                                ref_following.update(
                                                    mapOf(
                                                        "size" to nr_following,
                                                        user_id to true
                                                    )
                                                )
                                            }

                                            follow1.text = "$nr_follower FOLLOWERS"
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        btn_follow.visibility = Button.INVISIBLE
                        lay_settings_edit.visibility = LinearLayout.VISIBLE
                    }

                    edit_profile_btn.setOnClickListener() {
                        startActivity(Intent(this.context, edit_profile::class.java))
                        edit_profile_btn.startAnimation(animation)
                    }

                    setting.setOnClickListener() {
                        startActivity(Intent(context, settings::class.java))
                        setting.startAnimation(animation)
                    }

                    img.setOnClickListener() {

                    }

                    share.setOnClickListener(){
                        share.startAnimation(animation)
                    }
                }
                else{
                    bio.text = "This account has been disabled"

                    btn_follow.visibility = Button.VISIBLE
                    lay_settings_edit.visibility = LinearLayout.INVISIBLE
                }

                FirebaseStorage.getInstance().getReference("user_images/$user_id/AVATAR").listAll()
                    .addOnCompleteListener { task ->

                        if(task.isSuccessful && task.result.items.size > 0) {
                            task.result.items[0].downloadUrl
                                .addOnCompleteListener { task_aux ->

                                    if (task_aux.isSuccessful && activity != null) {

                                        Glide
                                            .with(this)
                                            .load(task_aux.result)
                                            .circleCrop()
                                            .into(img)
                                    }
                                }
                        }
                    }
            }

        following_number_btn.setOnClickListener(){
            startActivity(Intent(this.context, profile_following_list::class.java))
        }

        return inf_lay
    }
}
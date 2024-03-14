package com.example.menuapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.animation.AlphaAnimation
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class profile_your_gallery : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_your_gallery)

        val gallery_btn_back: AppCompatImageButton = findViewById(R.id.gallery_btn_back)
        val gallery_text: TextView = findViewById(R.id.your_gallery_text)
        val gallery_type: String = intent.getStringExtra("gallery_type").toString()
        val recycle: RecyclerView = findViewById(R.id.gallery_recycle)
        val size: TextView = findViewById(R.id.your_gallery_size)
        recycle.layoutManager = LinearLayoutManager(this)

        gallery_btn_back.setOnClickListener() {
            val animation = AlphaAnimation(0.5f, 1.0f)
            animation.duration = 750

            gallery_btn_back.startAnimation(animation)

            super.onBackPressed()
        }

        if (gallery_type == "PAINT" || gallery_type == "DRAWING" || gallery_type == "PHOTO" || gallery_type == "ANIMATION") {
            when (gallery_type) {
                "PAINT" -> gallery_text.text = "PAINTINGS"
                "DRAWING" -> gallery_text.text = "DRAWINGS"
                "PHOTO" -> gallery_text.text = "PHOTOS"
                "ANIMATION" -> gallery_text.text = "ANIMATIONS"
            }

            val ref = FirebaseStorage.getInstance().getReference()
                .child("user_images/${intent.getStringExtra("user_id")}/$gallery_type").listAll()
                .addOnSuccessListener { task ->

                    val str_arr = ArrayList<Pair<StorageReference, StorageReference?>>()
                    var limit = task.items.size

                    var ind: Int = 0

                    while (ind < limit) {

                        val ref1 = task.items[ind]
                        var ref2: StorageReference? = null

                        if (ind + 1 < limit)
                            ref2 = task.items[ind + 1]

                        str_arr.add(Pair(ref1, ref2))

                        ind += 2
                    }

                    recycle.adapter =
                        profile_gallery_recycle(str_arr, this, intent.getIntExtra("flag_layout", 0))

                    size.text = "($limit)"
                }
        } else {
            var saved_liked_posts: String = "saved_posts"
            gallery_text.text = "SAVED POSTS"

            if (intent.getIntExtra("flag_layout", 4) == 5) {
                saved_liked_posts = "liked_posts"
                gallery_text.text = "LIKED POSTS"
            }

            val str_arr = ArrayList<StorageReference>()

            FirebaseFirestore.getInstance().collection("users")
                .document(Firebase.auth.currentUser?.uid.toString()).collection("saved_liked_posts")
                .document(saved_liked_posts).get()
                .addOnSuccessListener() { task ->
                    if (task.exists()) {

                        var flag_layout: Int = 0
                        val data = task.data?.entries?.toList()

                        var contor : Int = 0

                        if (data != null) {
                            for (ind in data) {

                                if (ind.value == true) {
                                    val element_info = ind.key.split("_")

                                    val ref = FirebaseStorage.getInstance().getReference()
                                        .child("user_images/${element_info[0]}/${element_info[2]}/${element_info[1]}")

                                    ref.downloadUrl
                                        .addOnSuccessListener {
                                            str_arr.add(ref)

                                            check_final(
                                                ind,
                                                data,
                                                str_arr,
                                                recycle,
                                                flag_layout,
                                                size,
                                                contor
                                            )
                                        }
                                        .addOnFailureListener {
                                            check_final(
                                                ind,
                                                data,
                                                str_arr,
                                                recycle,
                                                flag_layout,
                                                size,
                                                contor
                                            )
                                        }

                                    when (element_info[2]) {
                                        "PAINT" -> flag_layout = 0
                                        "DRAWING" -> flag_layout = 1
                                        "PHOTO" -> flag_layout = 2
                                        "ANIMATION" -> flag_layout = 3
                                    }
                                }

                                contor++
                            }
                        }
                    }
                }
        }
    }

    private fun check_final(
        ind: MutableMap.MutableEntry<String, Any>,
        data: List<MutableMap.MutableEntry<String, Any>>,
        str_arr: ArrayList<StorageReference>,
        recycle: RecyclerView,
        flag_layout: Int,
        size: TextView,
        contor : Int
    ) {

        if (contor == data.size) {

            Log.i("tagg", str_arr.size.toString())

            val str_arr_rooted =
                ArrayList<Pair<StorageReference, StorageReference?>>()

            var ind_str: Int = 0

            while (ind_str < str_arr.size) {
                val ref1 = str_arr[ind_str]
                var ref2: StorageReference? = null

                if (ind_str + 1 < str_arr.size)
                    ref2 = str_arr[ind_str + 1]

                str_arr_rooted.add(Pair(ref1, ref2))

                ind_str += 2
            }

            recycle.adapter =
                profile_gallery_recycle(
                    str_arr_rooted,
                    this,
                    flag_layout
                )

            size.text = "(${str_arr.size})"
        }
    }
}
package com.example.menuapp

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.MediaController
import android.widget.TextView
import android.widget.VideoView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageMetadata
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlin.random.Random

class explore_recycle_anim_adapter(var adapter_list : ArrayList<StorageReference>, val context : Context?, val inf_lay : View?, val recycle : RecyclerView?, val flag_layout : Int, val support_fragment : FragmentManager, val frame_info : FrameLayout, val flag_random_posts : Boolean, val flag_layout_unrandom : Int) : RecyclerView.Adapter<explore_recycle_anim_adapter.recycle_view>() {

    interface BackPressedListener{
        fun onBackPressed()
    }

    inner class recycle_view(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val username: TextView = itemView.findViewById(R.id.explore_username)
        val country: TextView = itemView.findViewById(R.id.explore_country)
        val description: TextView = itemView.findViewById(R.id.explore_description)
        val post_type_img: ImageView = itemView.findViewById(R.id.explore_post_type)
        val tags: TextView = itemView.findViewById(R.id.explore_tags)
        val btn1: ImageButton = itemView.findViewById(R.id.explore_btn1)
        val btn2: ImageButton = itemView.findViewById(R.id.explore_btn2)
        val btn3: ImageButton = itemView.findViewById(R.id.explore_btn3)
        val vid: VideoView = itemView.findViewById(R.id.explore_vid)
        val view: View = itemView.findViewById(R.id.view_aux)
        val like_number: TextView = itemView.findViewById(R.id.like_number)
        val save_number: TextView = itemView.findViewById(R.id.save_number)
        val btn_to_profile: Button = itemView.findViewById(R.id.explore_profile_btn)

        var btn1_flag = false
        var btn2_flag = false

        fun Bind(position: Int) {

            val animation = AlphaAnimation(0.5f, 1.0f)
            animation.duration = 500

            val id_stg = adapter_list[position]
            val db = FirebaseFirestore.getInstance()

            id_stg.metadata.addOnCompleteListener { metadata ->
                val element_id = metadata.result.getCustomMetadata("element_id")
                val user_post_id = element_id?.split("_")?.get(0)
                val document_type = metadata.result.getCustomMetadata("art_type")!!

                post_type_img.setImageResource(R.drawable.animation_icon)

                btn_to_profile.setOnClickListener() {

                    frame_info.visibility = FrameLayout.VISIBLE
                    recycle?.visibility = RecyclerView.INVISIBLE

                    if (user_post_id != null) {
                        context?.let { it1 ->
                            frag_profile(
                                R.layout.profile_lay,
                                it1, user_post_id, true
                            )
                        }?.let { it2 ->
                            support_fragment.beginTransaction()
                                .replace(
                                    R.id.explore_frame_info,
                                    it2
                                ).commit()
                        }
                    }
                }

                if (user_post_id != null) {
                    val ref_author_like =
                        db.collection("users").document(user_post_id)
                            .collection(document_type + "_LIKE").document(document_type + "_LIKE")

                    val ref_author_save =
                        db.collection("users").document(user_post_id)
                            .collection(document_type + "_SAVE").document(document_type + "_SAVE")

                    val ref_saved =
                        db.collection("users").document(Firebase.auth.currentUser?.uid.toString())
                            .collection("saved_liked_posts").document("saved_posts")
                    val ref_liked =
                        db.collection("users").document(Firebase.auth.currentUser?.uid.toString())
                            .collection("saved_liked_posts").document("liked_posts")

                    ref_liked.get()
                        .addOnSuccessListener { task ->

                            if (task.exists()) {

                                if (task.contains(element_id) && task.getBoolean(element_id) == true) {
                                    btn1.setImageResource(R.drawable.niced_icon_removebg_preview)
                                    btn1_flag = true
                                } else {
                                    btn1.setImageResource(R.drawable.peace_icon_removebg_preview)
                                    btn1_flag = false
                                }
                            }
                        }

                    ref_author_like.get()
                        .addOnSuccessListener { task ->

                            if (task.exists()) {
                                var num = task.getLong(element_id)

                                like_number.text =
                                    "$num NICE"

                                btn1.setOnClickListener() {      // like

                                    btn1.startAnimation(animation)

                                    if (btn1_flag == false) {
                                        btn1.setImageResource(R.drawable.niced_icon_removebg_preview)
                                        num = num!! + 1

                                        ref_author_like.update(
                                            mapOf(
                                                element_id to num
                                            )
                                        )

                                    } else {
                                        btn1.setImageResource(R.drawable.peace_icon_removebg_preview)
                                        num = num!! - 1

                                        ref_author_like.update(
                                            mapOf(
                                                element_id to num
                                            )
                                        )
                                    }

                                    like_number.setText("$num NICE")
                                    btn1_flag = !btn1_flag

                                    ref_liked.get()
                                        .addOnSuccessListener { task_new ->
                                            var nr: Int = task_new.data?.get("size")
                                                .toString().toInt()

                                            if (btn1_flag == true) {
                                                nr++
                                                ref_liked.update(
                                                    mapOf(
                                                        "size" to nr,
                                                        element_id to true
                                                    )
                                                )
                                            } else {
                                                nr--
                                                ref_liked.update(
                                                    mapOf(
                                                        "size" to nr,
                                                        element_id to false
                                                    )
                                                )
                                            }
                                        }
                                }
                            }
                        }

                        .addOnFailureListener {
                            Log.i("Tag", "fail")
                        }

                    ref_saved.get()
                        .addOnSuccessListener { task ->

                            if (task.exists()) {

                                if (task.contains(element_id) && task.getBoolean(element_id) == true) {
                                    btn2.setImageResource(R.drawable.saved_icon)
                                    btn2_flag = true
                                } else {
                                    btn2.setImageResource(R.drawable.save_icon)
                                    btn2_flag = false
                                }
                            }
                        }

                    ref_author_save.get()
                        .addOnSuccessListener { task ->

                            if (task.exists()) {
                                var num = task.getLong(element_id)

                                save_number.text =
                                    "$num SAVES"

                                btn2.setOnClickListener() {      // like

                                    btn2.startAnimation(animation)

                                    if (btn2_flag == false) {
                                        btn2.setImageResource(R.drawable.saved_icon)
                                        num = num!! + 1

                                        ref_author_save.update(
                                            mapOf(
                                                element_id to num
                                            )
                                        )

                                    } else {
                                        btn2.setImageResource(R.drawable.save_icon)
                                        num = num!! - 1

                                        ref_author_save.update(
                                            mapOf(
                                                element_id to num
                                            )
                                        )
                                    }

                                    save_number.setText("$num SAVES")
                                    btn2_flag = !btn2_flag

                                    ref_saved.get()
                                        .addOnSuccessListener { task_new ->
                                            var nr: Int = task_new.data?.get("size")
                                                .toString().toInt()

                                            if (btn2_flag == true) {
                                                nr++
                                                ref_saved.update(
                                                    mapOf(
                                                        "size" to nr,
                                                        element_id to true
                                                    )
                                                )
                                            } else {
                                                nr--
                                                ref_saved.update(
                                                    mapOf(
                                                        "size" to nr,
                                                        element_id to false
                                                    )
                                                )
                                            }
                                        }
                                }
                            }
                        }

                        .addOnFailureListener {
                            Log.i("Tag", "fail")
                        }
                }

                val uid = metadata.result.getCustomMetadata("uid").toString()

                val db = FirebaseFirestore.getInstance()
                db.collection("users").document(uid).get()
                    .addOnCompleteListener { querry ->
                        if (querry.isSuccessful) {
                            username.text = querry.result.getString("nickname")
                            country.text = "(${querry.result.getString("country")})"
                        }
                    }
                    .addOnFailureListener {
                        username.setText("(NO NAME)")
                        country.setText("(NO COUNTRY)")
                    }

                description.text = metadata.result.getCustomMetadata("description").toString()

                val str_arr =
                    Gson().fromJson(
                        metadata.result.getCustomMetadata("tags"),
                        ArrayList::class.java
                    ) as ArrayList<String>

                for (ind in 0 until str_arr.size)
                    str_arr[ind] = "#" + str_arr[ind]
                tags.text = str_arr.joinToString(separator = " ; ")

                if (description.text.toString() == "")
                    description.text = "(NO DESCRIPTION)"

                if (tags.text.toString() == "")
                    tags.text = "(NO TAGS)"

                id_stg.downloadUrl
                    .addOnSuccessListener { task_url ->

                        id_stg.metadata
                            .addOnSuccessListener { task_metadata ->

                                val video_height = task_metadata.getCustomMetadata("height")?.toInt()
                                val video_width = task_metadata.getCustomMetadata("width")?.toInt()

                                var height: Int = 0
                                val screen_width = context!!.resources.displayMetrics.widthPixels

                                if (video_width!! <= video_height!!) {                       // portrait
                                    height = (5 * screen_width) / 4

                                } else {                                                            // landscape
                                    height = (9 * screen_width) / 16
                                }

                                val vidParLay = vid.layoutParams as FrameLayout.LayoutParams

                                vidParLay.height = height
                                vidParLay.width = video_width

                                vid.scaleY = video_height.toFloat() / height.toFloat()
                                vid.scaleX = video_width.toFloat() / screen_width.toFloat()

                                vid.layoutParams = vidParLay

                                val media_control = MediaController(context)
                                media_control.setAnchorView(vid)

                                Log.i("tagg", task_url.toString())

                                vid.setMediaController(media_control)
                                vid.setVideoURI(task_url)
                                vid.requestFocus()

                                vid.pause()

                                btn3.setOnClickListener() {          // info

                                    btn3.startAnimation(animation)

                                    val intent = Intent(context, add_art_preview::class.java)
                                    //val fragment = add_fragment(R.layout.add_paint_frame, intent)

                                    intent.putExtra("author_id", metadata.result.getCustomMetadata("element_id")!!
                                        .split("_").get(0))

                                    intent.putExtra("uri", task_url.toString())
                                    intent.putExtra(
                                        "description",
                                        metadata.result.getCustomMetadata("description")
                                    )

                                    intent.putExtra("element_id", metadata.result.getCustomMetadata("element_id"))

                                    intent.putExtra("t1", metadata.result.getCustomMetadata("title"))
                                    intent.putExtra("t2", metadata.result.getCustomMetadata("genre"))
                                    intent.putExtra("t3", metadata.result.getCustomMetadata("animation_software"))
                                    intent.putExtra("t4", metadata.result.getCustomMetadata("storyboard"))
                                    intent.putExtra("t5", metadata.result.getCustomMetadata("other_facts"))

                                    intent.putExtra("frame_flag", 3)

                                    intent.putExtra("width", video_width)
                                    intent.putExtra("height", video_height)
                                    intent.putExtra("tags_with_hashtags", tags.text.toString())
                                    intent.putExtra("inv_post_flag", true)

                                    context?.startActivity(intent)
                                }
                            }
                    }
            }

            if (position == itemCount - 1) {

                FirebaseFirestore.getInstance().collection("adittional_info")
                    .document("total_uploads").get()
                    .addOnSuccessListener { task ->

                        var limit: Long = task.getLong("total_uploads_ANIMATION")!!

                        if (adapter_list.size < limit!!)
                            add_to_feed(
                                inf_lay,
                                recycle,
                                context,
                                false,
                                adapter_list,
                                flag_layout,
                                support_fragment,
                                limit,
                                frame_info
                            )
                        else {
                            val scaledDensity =
                                context?.resources?.displayMetrics?.scaledDensity
                            val margin = 100 * scaledDensity!!

                            val layoutPar =
                                itemView.layoutParams as RecyclerView.LayoutParams
                            layoutPar.bottomMargin = margin.toInt()
                            itemView.layoutParams = layoutPar
                            itemView.requestLayout()
                        }
                    }
            }
        }
    }

    override fun getItemCount(): Int {
        return adapter_list.size ?: 0
    }

    override fun onBindViewHolder(holder: recycle_view, position: Int) {
        holder.Bind(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): recycle_view {
        val inf_view = LayoutInflater.from(parent.context)
            .inflate(R.layout.explore_recycle_element_anim, parent, false)
        return recycle_view(inf_view)
    }
}
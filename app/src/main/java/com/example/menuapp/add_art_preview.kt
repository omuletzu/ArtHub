package com.example.menuapp

import android.content.Intent
import android.graphics.BitmapFactory
import android.media.MediaController2
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.textclassifier.ConversationActions
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.MediaController
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.google.firebase.messaging.*
import java.util.Random

class add_art_preview : AppCompatActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_art_preview)

        val username : TextView = findViewById(R.id.preview_username)
        val country : TextView = findViewById(R.id.preview_country)
        val description : TextView = findViewById(R.id.preview_description)
        val tags : TextView = findViewById(R.id.preview_tag)

        val db = FirebaseFirestore.getInstance()

        db.collection("users").document(intent.getStringExtra("author_id")!!).get()
            .addOnSuccessListener { query ->
                username.setText(query.getString("nickname").toString())
                country.setText("(${query.getString("country").toString()})")


            }
            .addOnFailureListener {
                username.setText("THERE WAS AN ERROR")
                country.setText("TRY REFRESHING")
            }

        description.setText(intent.getStringExtra("description"))

        if(intent.getStringExtra("tags_with_hashtags") != null){
            tags.setText(intent.getStringExtra("tags_with_hashtags"))
        }
        else {
            for (ind in intent.getStringArrayListExtra("tags")!!)
                tags.append("#$ind  ")
        }

        if(tags.text.toString() == "")
            tags.setText("(NO TAGS)")

        if(description.text.toString() == "")
            description.setText("(NO DESCRIPTION)")

        var original_height : Int = 0
        var original_width : Int = 0

        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true

        continue_after_width_height(intent.getIntExtra("width", 0), intent.getIntExtra("height", 0), db)

        val logo_doc_type : TextView = findViewById(R.id.logo_doc_type)
        val img_doc_type : ImageView = findViewById(R.id.art_preview_img_type)

        when(intent.getIntExtra("frame_flag", 0)){
            0 -> {
                logo_doc_type.setText("PAINTING")
                img_doc_type.setImageResource(R.drawable.painting_icon)
            }
            1 -> {
                logo_doc_type.setText("DRAWING")
                img_doc_type.setImageResource(R.drawable.sketch_icon)
            }
            2 -> {
                logo_doc_type.setText("PHOTO")
                img_doc_type.setImageResource(R.drawable.camera__icon)
            }
            3 -> {
                logo_doc_type.setText("ANIMATION")
                img_doc_type.setImageResource(R.drawable.animation_icon)
            }
        }
    }

    fun continue_after_width_height(original_width : Int, original_height : Int, db : FirebaseFirestore){

        val country : TextView = findViewById(R.id.preview_country)

        val custom_opt = RequestOptions()

        val screen_width = resources.displayMetrics.widthPixels
        var height : Int = 0

        if(original_width <= original_height){                       // landscape
            height = (5 * screen_width) / 4
        }
        else{                                                            // portrait
            height = (screen_width / 1.9).toInt()
        }

        custom_opt.override(screen_width, height)
        custom_opt.centerCrop()

        val img: ImageView = findViewById(R.id.preview_img)
        val vid : VideoView = findViewById(R.id.preview_vid)

        if(intent.getIntExtra("frame_flag", 0) != 3) {
            img.visibility = ImageView.VISIBLE
            vid.visibility = VideoView.INVISIBLE

            Glide.with(this)
                .load(Uri.parse(intent.getStringExtra("uri").toString()))
                .apply(custom_opt)
                .into(img)
        }
        else{
            img.visibility = ImageView.INVISIBLE
            vid.visibility = VideoView.VISIBLE

            vid.setVideoURI(Uri.parse(intent.getStringExtra("uri")))

            var video_height : Int = 0
            var video_width : Int = 0

            if(intent.getStringExtra("width") != null){
                video_height = intent.getStringExtra("height").toString().toInt()
                video_width = intent.getStringExtra("width").toString().toInt()
            }
            else {

                val retreiver = MediaMetadataRetriever()
                retreiver.setDataSource(this, Uri.parse(intent.getStringExtra("uri")))

                video_height =
                    retreiver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)!!
                        .toInt()

                video_width =
                    retreiver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)!!
                        .toInt()
            }

            if (video_width <= video_height) {                       // portrait
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

            val media_play = MediaController(this)
            media_play.setMediaPlayer(vid)
            vid.setMediaController(media_play)

            vid.start()
        }

        val firebase = FirebaseFirestore.getInstance()

        firebase.collection("nicknames").whereEqualTo("nickname", intent.getStringExtra("username")).get()
            .addOnSuccessListener { query ->
                if(!query.isEmpty){
                    country.setText("(${query.documents[0].getString("country").toString()})")
                }
            }

        var fragment : Fragment? = null

        when(intent.getIntExtra("frame_flag", 0)){
            0 -> {
                fragment = frame_fragment(R.layout.add_paint_frame, intent)
                supportFragmentManager.beginTransaction().replace(R.id.preview_frame, fragment).commit()
            }
            1 -> {
                fragment = frame_fragment(R.layout.add_drawing_frame, intent)
                supportFragmentManager.beginTransaction().replace(R.id.preview_frame, fragment).commit()
            }
            2 -> {
                fragment = frame_fragment(R.layout.add_photo_frame, intent)
                supportFragmentManager.beginTransaction().replace(R.id.preview_frame, fragment).commit()
            }
            3 -> {
                fragment = frame_fragment(R.layout.add_animation_frame, intent)
                supportFragmentManager.beginTransaction().replace(R.id.preview_frame, fragment).commit()
            }
        }

        val preview_frame : FrameLayout = findViewById(R.id.preview_frame)
        preview_frame.isEnabled = false
        preview_frame.isFocusable = false

        val animation = AlphaAnimation(0.5f, 1.0f)
        animation.duration = 1000

        val btn_back : ImageButton = findViewById(R.id.add_back)
        btn_back.setOnClickListener(){
            btn_back.startAnimation(animation)

            super.onBackPressed()
        }

        var img_vid : String = "img"

        val btn_next : Button = findViewById(R.id.add_next)

        btn_next.setOnClickListener(){

            btn_next.startAnimation(animation)

            btn_next.visibility = Button.INVISIBLE

            val metadata = StorageMetadata.Builder()
            var element_type : String = "PAINT"

            when(intent.getIntExtra("frame_flag", 0)){
                0 -> {
                    metadata
                        .setCustomMetadata("paint_medium", intent.getStringExtra("t1"))
                        .setCustomMetadata("dimensions", intent.getStringExtra("t2"))
                        .setCustomMetadata("other_facts", intent.getStringExtra("t3"))
                        .setCustomMetadata("art_type", "PAINT")
                }
                1 -> {
                    metadata
                        .setCustomMetadata("drawing_medium", intent.getStringExtra("t1"))
                        .setCustomMetadata("dimensions", intent.getStringExtra("t2"))
                        .setCustomMetadata("other_facts", intent.getStringExtra("t3"))
                        .setCustomMetadata("art_type", "DRAWING")
                    element_type = "DRAWING"
                }
                2 -> {
                    metadata
                        .setCustomMetadata("title", intent.getStringExtra("t1"))
                        .setCustomMetadata("location", intent.getStringExtra("t2"))
                        .setCustomMetadata("camera_used", intent.getStringExtra("t3"))
                        .setCustomMetadata("exposure", intent.getStringExtra("t4"))
                        .setCustomMetadata("other_facts", intent.getStringExtra("t5"))
                        .setCustomMetadata("art_type", "PHOTO")
                    element_type = "PHOTO"
                }
                3 -> {
                    metadata
                        .setCustomMetadata("title", intent.getStringExtra("t1"))
                        .setCustomMetadata("genre", intent.getStringExtra("t2"))
                        .setCustomMetadata("animation_software", intent.getStringExtra("t3"))
                        .setCustomMetadata("storyboard", intent.getStringExtra("t4"))
                        .setCustomMetadata("other_facts", intent.getStringExtra("t5"))
                        .setCustomMetadata("art_type", "ANIMATION")
                    element_type = "ANIMATION"
                    img_vid = "vid"
                }
            }

            metadata.setCustomMetadata("description", intent.getStringExtra("description").toString())
            metadata.setCustomMetadata("uid", Firebase.auth.currentUser?.uid.toString())
            metadata.setCustomMetadata("height", original_height.toString())
            metadata.setCustomMetadata("width", original_width.toString())
            metadata.setCustomMetadata("likes", "0")
            metadata.setCustomMetadata("shares", "0")
            metadata.setCustomMetadata("author", Firebase.auth.currentUser?.uid.toString())

            val gson = Gson()
            val gsonString = gson.toJson(intent.getStringArrayListExtra("tags"))
            metadata.setCustomMetadata("tags", gsonString)

            val reference = FirebaseStorage.getInstance().getReference("user_images/${Firebase.auth.currentUser?.uid.toString()}/$element_type")
            reference.listAll()
                .addOnSuccessListener { task ->

                    val random_id = Random().nextInt()
                    val element_id = "${Firebase.auth.currentUser?.uid.toString()}_$img_vid${random_id}_$element_type"
                    metadata.setCustomMetadata("element_id", element_id)

                    var reference_child = FirebaseStorage.getInstance().getReference("user_images/${Firebase.auth.currentUser?.uid.toString()}/$element_type/$img_vid${random_id}")

                    while(task.items.contains(reference_child)){
                        reference_child = FirebaseStorage.getInstance().getReference("user_images/${Firebase.auth.currentUser?.uid.toString()}/$element_type/$img_vid${Random().nextInt()}")
                    }

                    reference_child.putFile(Uri.parse(intent.getStringExtra("uri")), metadata.build())
                        .addOnSuccessListener {

                            Toast.makeText(this, "ART POSTED", Toast.LENGTH_SHORT).show()

                            db.collection("users").document(Firebase.auth.currentUser?.uid.toString()).collection(element_type + "_LIKE")
                                .document(element_type + "_LIKE")
                                    .update(
                                        mapOf(
                                            element_id to 0
                                        )
                            )

                            db.collection("users").document(Firebase.auth.currentUser?.uid.toString()).collection(element_type + "_SAVE")
                                .document(element_type + "_SAVE")
                                .update(
                                    mapOf(
                                        element_id to 0
                                    )
                                )

                            db.collection("users").document(Firebase.auth.currentUser?.uid.toString()).collection(element_type).document(element_id).set(
                                hashMapOf("size" to 0)
                            )

                            db.collection("adittional_info").document("total_uploads").get()
                                .addOnSuccessListener { task ->
                                    if(task.exists()){
                                        val map = hashMapOf("total_uploads_$element_type" to (task.getLong("total_uploads_$element_type")
                                            ?.plus(1)))

                                        db.collection("adittional_info").document("total_uploads").update(
                                            map as Map<String, Any>
                                        )
                                    }
                                }

                            val str_tags_array = intent.getStringArrayListExtra("tags")

                            if (str_tags_array != null) {
                                for(it in str_tags_array){

                                    val new_tag_ref = db.collection("tag_post").document(it)

                                    new_tag_ref.get()
                                        .addOnSuccessListener { task ->
                                            if(task.exists()){

                                                val size = task.getLong("size")
                                                val reference_child_field_name : String = "tag${size?.plus(1)}"

                                                if (size != null) {
                                                    new_tag_ref.update(
                                                        mapOf(
                                                            "size" to size + 1,
                                                            reference_child_field_name to reference_child.toString()
                                                        )
                                                    )
                                                }
                                            }
                                            else{
                                                new_tag_ref.set(
                                                    mapOf(
                                                        "size" to 1,
                                                        "tag1" to reference_child.toString()
                                                    )
                                                )
                                            }
                                        }
                                }
                            }

                            /*FirebaseFirestore.getInstance().collection("users").document(Firebase.auth.currentUser?.uid.toString()).collection("followers").document("follow").get()
                                .addOnSuccessListener { task_follow ->

                                    if(!task_follow.exists()){

                                        val data = task_follow.data

                                        if (data != null) {
                                            for((field, value) in data){

                                                if(value == true){

                                                    FirebaseFirestore.getInstance().collection("users").document(field).get()
                                                        .addOnSuccessListener{ task_follower ->

                                                        if(task_follower.exists()){

                                                            val token = task_follower.getString("token_fcm")

                                                            val message = Message.builder()
                                                                .setToken(field)
                                                                .setNotification(
                                                                    Notification.builder()
                                                                        .setTitle("Notification")
                                                                        .setBody("Mesaj")
                                                                        .build()
                                                                )
                                                                .build()

                                                            FirebaseMessaging.getInstance().send(message)
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }*/

                            finish()

                        }
                        .addOnFailureListener{
                            Log.i("tag", "FAIL")
                        }
                }
        }

        if(intent.getBooleanExtra("inv_post_flag", false) == true)
            btn_next.visibility = Button.INVISIBLE

        val report_btn : Button = findViewById(R.id.add_report)
        report_btn.setOnClickListener() {

            report_btn.startAnimation(animation)

            val intent_to_report = Intent(this, settings_report_users::class.java)
            intent_to_report.putExtra("post_id", intent.getStringExtra("post_id"))
            startActivity(intent_to_report)
        }

        if(intent.getBooleanExtra("inv_report_flag", false) == true)
            report_btn.visibility = Button.INVISIBLE

        val delete_post_btn : Button = findViewById(R.id.add_delete)
        val scroll_view_lay : ScrollView = findViewById(R.id.add_art_preview_scrollview)
        val delete_warning_layout : ConstraintLayout = findViewById(R.id.settings_delete_warning_lay)
        val warning_btn_yes : Button = findViewById(R.id.settings_delete_yes)
        val warning_btn_no : Button = findViewById(R.id.settings_delete_no)

        delete_post_btn.setOnClickListener(){

            delete_post_btn.startAnimation(animation)
            delete_warning_layout.visibility = ConstraintLayout.VISIBLE
            scroll_view_lay.alpha = 0.1f

            warning_btn_yes.setOnClickListener(){

                val total_uploads_ref = FirebaseFirestore.getInstance().collection("adittional_info").document("total_uploads")

                total_uploads_ref.get()
                    .addOnSuccessListener { task ->
                        if(task.exists()){

                            val post_uri = intent.getStringExtra("uri")

                            val storage_ref_to_post =
                                post_uri?.let { it1 ->
                                    FirebaseStorage.getInstance().getReferenceFromUrl(
                                        it1
                                    )
                                }

                            storage_ref_to_post?.delete()
                                ?.addOnSuccessListener {

                                    when(intent.getIntExtra("frame_flag", 0)){
                                        0 -> task.getLong("total_uploads_PAINT")
                                            ?.let { it1 -> update_total_uploads("total_uploads_PAINT", it1.minus(0), total_uploads_ref) }

                                        1 -> task.getLong("total_uploads_DRAWING")
                                            ?.let { it1 -> update_total_uploads("total_uploads_DRAWING", it1.minus(0), total_uploads_ref) }

                                        2 -> task.getLong("total_uploads_PHOTO")
                                            ?.let { it1 -> update_total_uploads("total_uploads_PHOTO", it1.minus(0), total_uploads_ref) }

                                        3 -> task.getLong("total_uploads_ANIMATION")
                                            ?.let { it1 -> update_total_uploads("total_uploads_ANIMATION", it1.minus(0), total_uploads_ref) }
                                    }

                                    for (ind in intent.getStringArrayListExtra("tags")!!) {

                                        val ref_to_specific_tag = FirebaseFirestore.getInstance().collection("tag_post").document(ind)
                                        ref_to_specific_tag.get()
                                            .addOnSuccessListener { task ->
                                                if(task.exists()){

                                                    for(it in task.data!!){

                                                        if(it.value == storage_ref_to_post.toString()){
                                                            ref_to_specific_tag.update(
                                                                mapOf(
                                                                    it.key to FieldValue.delete(),
                                                                    "size" to task.getLong("size")!! - 1
                                                                )
                                                            )

                                                            break
                                                        }
                                                    }
                                                }
                                            }
                                    }

                                    /*val element_id = intent.getStringExtra("element_id")

                                    db.collection("users").document(Firebase.auth.currentUser?.uid.toString()).collection("saved_liked_posts")
                                        .document("liked_posts").update(
                                            mapOf(
                                                element_id to FieldValue.delete()
                                            )
                                        )

                                    db.collection("users").document(Firebase.auth.currentUser?.uid.toString()).collection("saved_liked_posts")
                                        .document("saved_posts").update(
                                            mapOf(
                                                element_id to FieldValue.delete()
                                            )
                                        )*/

                                    /*FirebaseFirestore.getInstance().collection("users").get()
                                        .addOnSuccessListener { task1 ->
                                            if(!task1.isEmpty){

                                                for(ind in task1.documents){
                                                    ind.reference.collection("saved_liked_posts").document("liked_posts").
                                                }
                                            }
                                        }*/

                                    finish()
                                    Toast.makeText(this, "POST DELETED", Toast.LENGTH_SHORT).show()
                                }
                                ?.addOnFailureListener {
                                    warning_btn_no.performClick()
                                }
                        }
                    }
            }

            warning_btn_no.setOnClickListener(){
                delete_warning_layout.visibility = ConstraintLayout.INVISIBLE
                scroll_view_lay.alpha = 1.0f
            }
        }

        if(intent.getBooleanExtra("inv_delete_flag", true) == false){
            delete_post_btn.visibility = Button.VISIBLE
        }
    }

    private fun update_total_uploads(category : String, new_size : Long, total_uploads_ref : DocumentReference){
        total_uploads_ref.update(
            mapOf(
                category to new_size - 1
            )
        )
    }
}

class frame_fragment(val xml_layout : Int, val intent : Intent) : Fragment(){

    var t1 : TextInputEditText? = null
    var t2 : TextInputEditText? = null
    var t3 : TextInputEditText? = null
    var t4 : TextInputEditText? = null
    var t5 : TextInputEditText? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val inf_lay = inflater.inflate(xml_layout, container, false)

        when(xml_layout){
            R.layout.add_paint_frame -> {
                t1 = inf_lay.findViewById(R.id.paint_medium)
                t2 = inf_lay.findViewById(R.id.paint_size)
                t3 = inf_lay.findViewById(R.id.paint_other)
            }
            R.layout.add_drawing_frame -> {
                t1 = inf_lay.findViewById(R.id.drawing_medium)
                t2 = inf_lay.findViewById(R.id.drawing_size)
                t3 = inf_lay.findViewById(R.id.drawing_other)
            }
            R.layout.add_photo_frame -> {
                t1 = inf_lay.findViewById(R.id.photo_title)
                t2 = inf_lay.findViewById(R.id.photo_location)
                t3 = inf_lay.findViewById(R.id.photo_camera_used)
                t4 = inf_lay.findViewById(R.id.photo_exposure)
                t5 = inf_lay.findViewById(R.id.photo_others)
            }
            R.layout.add_animation_frame -> {
                t1 = inf_lay.findViewById(R.id.animation_title)
                t2 = inf_lay.findViewById(R.id.animation_location)
                t3 = inf_lay.findViewById(R.id.animation_camera_used)
                t4 = inf_lay.findViewById(R.id.animation_exposure)
                t5 = inf_lay.findViewById(R.id.animation_others)
            }
        }

        t1?.setText(intent.getStringExtra("t1"))
        t2?.setText(intent.getStringExtra("t2"))
        t3?.setText(intent.getStringExtra("t3"))

        if(xml_layout == R.layout.add_photo_frame || xml_layout == R.layout.add_animation_frame){
            t4?.setText(intent.getStringExtra("t4"))
            t5?.setText(intent.getStringExtra("t5"))
        }

        if(t1?.text.toString() == "")
            t1?.setText("-")

        if(t2?.text.toString() == "")
            t2?.setText("-")

        if(t3?.text.toString() == "")
            t3?.setText("-")

        if(t4?.text.toString() == "")
            t4?.setText("-")

        if(t5?.text.toString() == "")
            t5?.setText("-")

        t1?.setEnabled(false)
        t2?.setEnabled(false)
        t3?.setEnabled(false)
        t4?.setEnabled(false)
        t5?.setEnabled(false)

        return inf_lay
    }
}
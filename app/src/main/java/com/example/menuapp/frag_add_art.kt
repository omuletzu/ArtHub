package com.example.menuapp

import android.app.ActionBar.LayoutParams
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.media.Image
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
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
import android.widget.LinearLayout
import android.widget.MediaController
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toolbar
import android.widget.VideoView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContentResolverCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import java.io.InputStream

val IMAGE_CODE = 0
val VIDEO_CODE = 1

class frag_add_art (val xml_layout : Int, val supportFragmentManager: FragmentManager, val username : String?) : Fragment() {

    var img_uri :  ImageButton? = null
    lateinit var vid_for_result : VideoView
    lateinit var inf_lay_for_result : View
    lateinit var fragment : add_fragment
    var uri : String? = null
    var img_flag : Int = 0
    var global_height : Int = 0
    var global_width : Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        var frame_flag : Int = 0
        var flag_btn_inp_pressed : Boolean = false
        val inf_lay = inflater.inflate(xml_layout, container, false)
        inf_lay_for_result = inf_lay

        val change_btn : Button = inf_lay.findViewById(R.id.add_change_btn)
        val inp_desc : TextInputEditText = inf_lay.findViewById(R.id.add_input_desc)
        val inp_tag : TextInputEditText = inf_lay.findViewById(R.id.add_input_tag)
        val tag_btn : ImageButton = inf_lay.findViewById(R.id.add_tag_btn)
        val inp_img : ImageButton = inf_lay.findViewById(R.id.add_input_img)
        img_uri = inp_img

        val btn1 : Button = inf_lay.findViewById(R.id.add_btn1)
        val btn2 : Button = inf_lay.findViewById(R.id.add_btn2)
        val btn3 : Button = inf_lay.findViewById(R.id.add_btn3)
        //val btn4 : Button = inf_lay.findViewById(R.iadd_btn4)
        val add_next : Button = inf_lay.findViewById(R.id.add_next)
        val recycler : RecyclerView = inf_lay.findViewById(R.id.add_recycler)

        val arr_str = ArrayList<String>()
        recycler.adapter = add_recycler_adapter(arr_str, recycler, null, 0)
        recycler.layoutManager = LinearLayoutManager(inf_lay.context)
        recycler.visibility = RecyclerView.INVISIBLE

        val animation = AlphaAnimation(0.5f, 1.0f)
        animation.duration = 1000

        change_btn.setOnClickListener() {
            change_btn.startAnimation(animation)
            inp_img.performClick()
        }

        val intent = Intent(inf_lay.context, add_art_preview::class.java)

        add_next.setOnClickListener(){

            add_next.startAnimation(animation)

            if(img_flag == 1){
                if(uri != null){
                    intent.putExtra("uri", uri)
                    intent.putExtra("description", inp_desc.text.toString().trim().lowercase())
                    intent.putExtra("tags", arr_str)
                    intent.putExtra("frame_flag", frame_flag)
                    intent.putExtra("t1", fragment.get_t1()?.text.toString().trim().lowercase())
                    intent.putExtra("t2", fragment.get_t2()?.text.toString().trim().lowercase())
                    intent.putExtra("t3", fragment.get_t3()?.text.toString().trim().lowercase())
                    intent.putExtra("width", global_width)
                    intent.putExtra("height", global_height)
                    intent.putExtra("inv_post_flag", false)
                    intent.putExtra("inv_report_flag", true)
                    intent.putExtra("author_id", Firebase.auth.currentUser?.uid.toString())

                    if(frame_flag > 1){
                        intent.putExtra("t4", fragment.get_t4()?.text.toString().trim().lowercase())
                        intent.putExtra("t5", fragment.get_t5()?.text.toString().trim().lowercase())
                    }

                    val btn_anim = AnimationUtils.loadAnimation(context, R.anim.btn_clicked)
                    btn_anim.start()

                    startActivity(intent)
                }
            }
            else{
                inp_img.background = resources.getDrawable(R.drawable.dashed_border_back_red)
                val scroll : ScrollView = inf_lay.findViewById(R.id.add_scroll_view)
                scroll.smoothScrollTo(0, 0)
            }
        }

        tag_btn.setOnClickListener(){

            //tag_btn.animate().rotation(180.0F).duration = 250
            val btn_anim = AnimationUtils.loadAnimation(inf_lay.context, R.anim.btn_rotation)
            tag_btn.startAnimation(btn_anim)

            val check_spaces = Regex("[a-zA-Z0-9]").containsMatchIn(inp_tag.text.toString().trim().lowercase())

            if(!arr_str.contains(inp_tag.text.toString()) && inp_tag.text.toString().trim().lowercase() != "" && check_spaces) {
                arr_str.add("${inp_tag.text.toString().trim().lowercase()}")
                recycler.adapter = add_recycler_adapter(arr_str, recycler, null, 0)
                recycler.visibility = RecyclerView.VISIBLE

                inp_tag.setText("")
            }
        }

        fragment = add_fragment(R.layout.add_paint_frame, intent)

        supportFragmentManager.beginTransaction()
            .replace(R.id.add_frame, fragment).commit()

        val img_for_result : ImageView = inf_lay_for_result.findViewById(R.id.add_img)

        btn1.setOnClickListener(){
            if(frame_flag != 0) {
                colorOn(btn1)
                colorOff(btn2, btn3)

                fragment = add_fragment(R.layout.add_paint_frame, intent)
                supportFragmentManager.beginTransaction()
                    .replace(R.id.add_frame, fragment).commit()

                if(frame_flag == 3)
                    change_img_lay(img_for_result, false, true)
            }
            frame_flag = 0
        }

        btn2.setOnClickListener(){
            if(frame_flag != 1) {
                colorOn(btn2)
                colorOff(btn1, btn3)

                fragment = add_fragment(R.layout.add_drawing_frame, intent)
                supportFragmentManager.beginTransaction()
                    .replace(R.id.add_frame, fragment).commit()

                if(frame_flag == 3)
                    change_img_lay(img_for_result, false, true)
            }
            frame_flag = 1
        }

        btn3.setOnClickListener(){
            if(frame_flag != 2) {
                colorOn(btn3)
                colorOff(btn2, btn1)

                fragment = add_fragment(R.layout.add_photo_frame, intent)
                supportFragmentManager.beginTransaction()
                    .replace(R.id.add_frame, fragment).commit()

                if(frame_flag == 3)
                    change_img_lay(img_for_result, false, true)
            }
            frame_flag = 2
        }

        /*btn4.setOnClickListener(){
            if(frame_flag != 3) {
                colorOn(btn4)
                colorOff(btn2, btn3, btn1)

                fragment = add_fragment(R.layout.add_animation_frame, intent)
                supportFragmentManager.beginTransaction()
                    .replace(R.id.add_frame, fragment).commit()

                val img_for_result : ImageView = inf_lay.findViewById(R.id.add_img)
                change_img_lay(img_for_result, true, true)
            }
            frame_flag = 3
        }*/

        inp_img.setOnClickListener(){

            inp_img.startAnimation(animation)

            val intent = Intent(Intent.ACTION_PICK)

            if(frame_flag != 3) {
                intent.type = "image/*"
            }
            else{
                //intent.type = "video/*"
            }

            if(frame_flag == 3)
                startActivityForResult(intent, VIDEO_CODE)
            else
                startActivityForResult(intent, IMAGE_CODE)
        }

        return inf_lay
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(data != null){

            val img_for_result : ImageView = inf_lay_for_result.findViewById(R.id.add_img)

            img_uri?.visibility = ImageView.INVISIBLE
            img_flag = 1
            uri = data.data.toString()

            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true

            val input_stream = data.data?.let {
                inf_lay_for_result.context.contentResolver.openInputStream(it)
            }

            BitmapFactory.decodeStream(input_stream, null, options)

            val screen_width = resources.displayMetrics.widthPixels
            var height: Int = 0

            if (options.outWidth <= options.outHeight) {                       // portrait
                height = (5 * screen_width) / 4
            } else {                                                            // landscape
                height = (screen_width / 1.9).toInt()
            }

            global_height = height
            global_width = screen_width

            if(requestCode == IMAGE_CODE) {

                val custom_opt = RequestOptions()

                custom_opt.override(screen_width, height)
                custom_opt.centerCrop()

                Glide.with(inf_lay_for_result)
                    .load(data.data)
                    .apply(custom_opt)
                    .into(img_for_result)

                change_img_lay(img_for_result, false, false)
            }
            else {

                val vid : VideoView = inf_lay_for_result.findViewById(R.id.add_vid)
                vid.setVideoURI(Uri.parse(data.data.toString()))

                val retreiver = MediaMetadataRetriever()
                retreiver.setDataSource(context, Uri.parse(data.data.toString()))

                val video_height =
                    retreiver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)!!.toInt()

                val video_width =
                    retreiver.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)!!.toInt()

                global_height = video_height
                global_width = video_width

                if (video_width <= video_height) {                       // portrait
                    height = (5 * screen_width) / 4
                } else {                                                            // landscape
                    height = (9 * screen_width) / 16
                }

                val vidParLay = vid.layoutParams as FrameLayout.LayoutParams

                vidParLay.height = height
                vidParLay.width = screen_width

                vid.scaleY = video_height.toFloat() / height.toFloat()
                vid.scaleX = video_width.toFloat() / screen_width.toFloat()

                vid.layoutParams = vidParLay

                val media_play = MediaController(context)
                media_play.setMediaPlayer(vid)
                vid.setMediaController(media_play)

                vid.start()

                change_img_lay(img_for_result, true, false)
            }
        }
    }

    private fun change_img_lay(img_for_result : ImageView, flag_img_vid : Boolean, flag_change : Boolean){
        val view1 : View = inf_lay_for_result.findViewById(R.id.add_aux_view1)
        val view2 : View = inf_lay_for_result.findViewById(R.id.add_aux_view2)
        val bulb_img : ImageView = inf_lay_for_result.findViewById(R.id.bulb_img)
        val text_desc : TextView = inf_lay_for_result.findViewById(R.id.add_text1)
        val change_btn : Button = inf_lay_for_result.findViewById(R.id.add_change_btn)
        val toolbar : LinearLayout = inf_lay_for_result.findViewById(R.id.add_type_sel)
        val inp_img : ImageButton = inf_lay_for_result.findViewById(R.id.add_input_img)
        val vid_for_result : LinearLayout = inf_lay_for_result.findViewById(R.id.add_vid_frame)

        val layoutParToolbar = toolbar.layoutParams as ConstraintLayout.LayoutParams
        val view1LayPar = view1.layoutParams as ConstraintLayout.LayoutParams
        val view2LayPar = view2.layoutParams as ConstraintLayout.LayoutParams

        if(flag_change == false){
            view1.visibility = View.VISIBLE
            view2.visibility = View.VISIBLE
            bulb_img.visibility = ImageView.INVISIBLE
            text_desc.visibility = TextView.INVISIBLE
            change_btn.visibility = Button.VISIBLE
            inp_img.visibility = ImageButton.INVISIBLE

            view1LayPar.topToBottom = R.id.add_change_btn

            if(flag_img_vid == false) {
                layoutParToolbar.topToBottom = R.id.add_img
                toolbar.layoutParams = layoutParToolbar

                img_for_result.visibility = ImageView.VISIBLE
                vid_for_result.visibility = FrameLayout.INVISIBLE

                view2LayPar.topToBottom = R.id.add_img
            }
            else{
                layoutParToolbar.topToBottom = R.id.add_vid_frame
                toolbar.layoutParams = layoutParToolbar

                img_for_result.visibility = ImageView.INVISIBLE
                vid_for_result.visibility = FrameLayout.VISIBLE

                view2LayPar.topToBottom = R.id.add_vid_frame
            }
        }
        else{
            view1.visibility = View.INVISIBLE
            view2.visibility = View.INVISIBLE
            bulb_img.visibility = ImageView.VISIBLE
            text_desc.visibility = TextView.VISIBLE
            change_btn.visibility = Button.INVISIBLE
            inp_img.visibility = ImageButton.VISIBLE
            img_for_result.visibility = ImageView.INVISIBLE
            vid_for_result.visibility = FrameLayout.INVISIBLE

            layoutParToolbar.topToBottom = R.id.add_input_img
            toolbar.layoutParams = layoutParToolbar
        }

        inf_lay_for_result.findViewById<ScrollView>(R.id.add_scroll_view).scrollTo(0, 0)
    }

    private fun colorOn(btn : Button){
        btn.background = resources.getDrawable(R.drawable.add_input_text_back)
    }

    private fun colorOff(btn1 : Button, btn2 : Button){
        btn1.setBackgroundColor(resources.getColor(R.color.trans))
        btn2.setBackgroundColor(resources.getColor(R.color.trans))
        //btn3.setBackgroundColor(resources.getColor(R.color.trans))
    }
}

class add_fragment(val xml_layout: Int, intent : Intent) : Fragment(){

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

        return inf_lay
    }

    fun get_t1() : TextInputEditText?{
        return t1
    }

    fun get_t2() : TextInputEditText?{
        return t2
    }

    fun get_t3() : TextInputEditText?{
        return t3
    }

    fun get_t4() : TextInputEditText?{
        return t4
    }

    fun get_t5() : TextInputEditText?{
        return t5
    }
}
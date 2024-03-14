package com.example.menuapp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.GestureDetectorCompat
import androidx.core.view.setMargins
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.example.sql.WordNetHelper
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class explore_global(val xml_layout : Int, val support_fragment : FragmentManager, val btn_arr : ArrayList<ImageButton>) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val inf_lay = inflater.inflate(xml_layout, container, false)

        val recycle : RecyclerView = inf_lay.findViewById(R.id.explore_recycle)
        val search_input : TextInputEditText = inf_lay.findViewById(R.id.explore_search)
        val search_btn : ImageButton = inf_lay.findViewById(R.id.explore_search_btn)
        val btn_paint : Button = inf_lay.findViewById(R.id.explore_paint_btn)
        val btn_drawing : Button = inf_lay.findViewById(R.id.explore_drawing_btn)
        val btn_photo : Button = inf_lay.findViewById(R.id.explore_photo_btn)
        //val btn_animation : Button = inf_lay.findViewById(R.id.explore_animation_btn)
        val btn_random : Button = inf_lay.findViewById(R.id.explore_random_btn)
        val explore_scroll_lay : ScrollView = inf_lay.findViewById(R.id.explore_scroll_lay)
        val view_aux1 : View = inf_lay.findViewById(R.id.explore_view_aux1)

        val animation = AlphaAnimation(0.5f, 1.0f)
        animation.duration = 1000

        val adapter_list = ArrayList<StorageReference>()
        recycle.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)

        /*val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(recycle)*/

        val frame_info : FrameLayout = inf_lay.findViewById(R.id.explore_frame_info)

        val callback = object : OnBackPressedCallback(true){

            override fun handleOnBackPressed() {
                recycle.visibility = RecyclerView.VISIBLE
                frame_info.visibility = FrameLayout.INVISIBLE
            }
        }

        search_btn.setOnClickListener(){

            search_btn.startAnimation(animation)

            val input : String = search_input.text.toString().trim().lowercase()

            if(input != "") {
                explore_scroll_lay.visibility = ScrollView.INVISIBLE
                recycle.visibility = RecyclerView.VISIBLE
                view_aux1.visibility = View.VISIBLE

                val JWI_DICT = WordNetHelper().getWrd(requireContext())

                var tag_list = WordNetHelper().getText(JWI_DICT, input)

                if(tag_list == null)
                    tag_list = ArrayList<String>()

                tag_list?.add(0, input)

                val adapter_list = ArrayList<StorageReference>()
                val storage_instance = FirebaseStorage.getInstance()

                if (tag_list != null) {
                    for(ind in 0 until tag_list.size){

                        FirebaseFirestore.getInstance().collection("tag_post").document(tag_list[ind]).get()
                            .addOnSuccessListener { task ->
                                if(task.exists()){

                                    for((field, value) in task.data!!){
                                        if(field != "size"){

                                            val storageReference : StorageReference = storage_instance.getReferenceFromUrl(value.toString())
                                            adapter_list.add(storageReference)
                                        }
                                    }

                                    recycle.adapter = explore_recycle_adapter(
                                        adapter_list,
                                        context,
                                        inf_lay,
                                        recycle,
                                        5,
                                        support_fragment,
                                        frame_info,
                                        true,
                                        4
                                    )
                                }
                            }
                    }
                }
            }
            else{
                explore_scroll_lay.visibility = ScrollView.VISIBLE
                recycle.visibility = RecyclerView.INVISIBLE
                view_aux1.visibility = View.INVISIBLE
            }
        }

        if(activity != null) {
            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        }

        btn_random.setOnClickListener(){
            explore_scroll_lay.visibility = ScrollView.INVISIBLE
            recycle.visibility = RecyclerView.VISIBLE
            view_aux1.visibility = View.VISIBLE

            FirebaseFirestore.getInstance().collection("adittional_info")
                .document("total_uploads").get()
                .addOnSuccessListener { task ->
                    val limit = task.getLong("total_uploads_DRAWING")!! +
                            task.getLong("total_uploads_PHOTO")!! +
                            task.getLong("total_uploads_PAINT")!!

                    if(task.exists()) {
                        add_to_feed(
                            inf_lay,
                            recycle,
                            this.context,
                            true,
                            adapter_list,
                            4,
                            support_fragment,
                            limit,
                            frame_info
                        )
                    }
                }
        }

        btn_paint.setOnClickListener(){
            btn_paint.startAnimation(animation)
            btn_arr[0].performClick()
        }

        btn_drawing.setOnClickListener(){
            btn_drawing.startAnimation(animation)
            btn_arr[1].performClick()
        }

        btn_photo.setOnClickListener(){
            btn_photo.startAnimation(animation)
            btn_arr[2].performClick()
        }

        /*btn_animation.setOnClickListener(){
            btn_animation.startAnimation(animation)
            btn_arr[3].performClick()
        }*/

        val explore_relative_lay : RelativeLayout = inf_lay.findViewById(R.id.explore_relative_lay)
        val relativeParLay = explore_relative_lay.layoutParams as ConstraintLayout.LayoutParams
        val width = this.context?.resources?.displayMetrics?.widthPixels

        if (width != null) {
            relativeParLay.height = width + width / 4
        }
        else
            relativeParLay.height = 500

        explore_relative_lay.layoutParams = relativeParLay

        val linear_lay : LinearLayout = inf_lay.findViewById(R.id.explore_search_lay)
        val relative_lay : RelativeLayout = inf_lay.findViewById(R.id.explore_relative_lay)

        val doubleTapListener = DoubleTapListener(requireContext(), linear_lay, relative_lay, view_aux1)
        val gestureDetector : GestureDetector = GestureDetector(context, doubleTapListener)
        gestureDetector.setOnDoubleTapListener(doubleTapListener)

        recycle.setOnTouchListener(){ _, event ->
            gestureDetector.onTouchEvent(event)
        }

        return inf_lay
    }
}
class DoubleTapListener(val context : Context, val linear_lay : LinearLayout, val relative_lay : RelativeLayout, val view : View) : GestureDetector.SimpleOnGestureListener(){
    override fun onDoubleTap(e: MotionEvent): Boolean {

        val relativeParLay = relative_lay.layoutParams as ConstraintLayout.LayoutParams

        if(linear_lay.visibility == LinearLayout.INVISIBLE){
            linear_lay.visibility = LinearLayout.VISIBLE
            view.visibility = View.VISIBLE

            relativeParLay.topMargin = 30
            relativeParLay.height = ConstraintLayout.LayoutParams.WRAP_CONTENT
            relativeParLay.topToBottom = R.id.explore_search_lay
        }
        else{
            linear_lay.visibility = LinearLayout.INVISIBLE
            view.visibility = View.INVISIBLE

            relativeParLay.topMargin = 0
            relativeParLay.height = ConstraintLayout.LayoutParams.MATCH_PARENT
        }

        relative_lay.layoutParams = relativeParLay

        return true;
    }
}
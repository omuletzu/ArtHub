package com.example.menuapp

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlin.random.Random

public fun add_to_feed(inf_lay : View?, recycle : RecyclerView?, context : Context?, flag : Boolean, adapter_list : ArrayList<StorageReference>, flag_layout : Int, support_fragment : FragmentManager, limit : Long?, frame_info : FrameLayout) {

    val db = FirebaseFirestore.getInstance()

    var element_type : String = ""
    var img_vid : String = "img"
    when(flag_layout){
        0 -> element_type = "PAINT"
        1 -> element_type = "DRAWING"
        2 -> element_type = "PHOTO"
        3 -> {
            element_type = "ANIMATION"
            img_vid = "vid"
        }
        4 -> element_type = "RANDOM"
    }

    FirebaseFirestore.getInstance().collection("users").document(Firebase.auth.currentUser?.uid.toString())
        .collection("blocked_users").document("blocked_users").get()
        .addOnSuccessListener { task ->

            val blocked_users_id = ArrayList<String>()

            if(task.exists() && task.data != null){

                for(ind in task.data!!){
                    blocked_users_id.add(ind.value.toString())
                }
            }

            add_element(0, element_type, db, adapter_list, flag, flag_layout, recycle, context, inf_lay, support_fragment, img_vid, limit, frame_info, blocked_users_id)
        }
}

fun add_element(ind : Int, element_type : String, db : FirebaseFirestore, adapter_list : ArrayList<StorageReference>, flag : Boolean, flag_layout : Int, recycle: RecyclerView?, context : Context?, inf_lay: View?, support_fragment : FragmentManager, img_vid : String, limit : Long?, frame_info: FrameLayout, blocked_users_id : ArrayList<String>) {

    if (adapter_list.size < limit!! && ind < 3) {

        db.collection("users").get()
            .addOnSuccessListener { task1 ->
                if (!task1.isEmpty) {
                    val random_nr = Random.nextInt(0, task1.documents.size)
                    val user_id = task1.documents[random_nr].id

                    if(!blocked_users_id.contains(user_id)) {

                        var reference = FirebaseStorage.getInstance().getReference()
                            .child("user_images/$user_id/$element_type")

                        var random_element_type: String = "PAINT";
                        var img_vid_as_var = "img"
                        var post_type_img_id: Int = R.drawable.painting_icon
                        var flag_layout_as_var: Int = flag_layout
                        var flag_random_post_for_explore: Boolean = false

                        if (element_type == "RANDOM")
                            flag_layout_as_var = Random.nextInt(0, 3)

                        when (flag_layout_as_var) {
                            0 -> {
                                post_type_img_id = R.drawable.painting_icon
                            }

                            1 -> {
                                random_element_type = "DRAWING"
                                post_type_img_id = R.drawable.sketch_icon
                            }

                            2 -> {
                                random_element_type = "PHOTO"
                                post_type_img_id = R.drawable.camera__icon
                            }
                        }

                        if (element_type == "RANDOM") {
                            reference = FirebaseStorage.getInstance().getReference()
                                .child("user_images/$user_id/$random_element_type")

                            flag_random_post_for_explore = true;
                        }

                        reference.listAll()
                            .addOnSuccessListener { task2 ->

                                if (!task2.items.isEmpty()) {
                                    val random_file_nr = Random.nextInt(0, task2.items.size)
                                    val file_reference = task2.items[random_file_nr]
                                    //reference.child("$img_vid_as_var$random_file_nr")

                                    if (!adapter_list.contains(file_reference)) {
                                        adapter_list.add(file_reference)

                                        if (flag == true) {

                                            if (flag_layout == 3) {

                                                recycle?.adapter = explore_recycle_anim_adapter(
                                                    adapter_list,
                                                    context,
                                                    inf_lay,
                                                    recycle,
                                                    flag_layout,
                                                    support_fragment,
                                                    frame_info,
                                                    false,
                                                    3
                                                )
                                            } else {
                                                recycle?.adapter = explore_recycle_adapter(
                                                    adapter_list,
                                                    context,
                                                    inf_lay,
                                                    recycle,
                                                    flag_layout,
                                                    support_fragment,
                                                    frame_info,
                                                    flag_random_post_for_explore,
                                                    flag_layout_as_var
                                                )
                                            }
                                        } else {
                                            recycle?.adapter?.notifyDataSetChanged()
                                        }

                                        add_element(
                                            ind + 1,
                                            element_type,
                                            db,
                                            adapter_list,
                                            flag,
                                            flag_layout,
                                            recycle,
                                            context,
                                            inf_lay,
                                            support_fragment,
                                            img_vid,
                                            limit,
                                            frame_info,
                                            blocked_users_id
                                        )
                                    } else
                                        add_element(
                                            ind,
                                            element_type,
                                            db,
                                            adapter_list,
                                            flag,
                                            flag_layout,
                                            recycle,
                                            context,
                                            inf_lay,
                                            support_fragment,
                                            img_vid,
                                            limit,
                                            frame_info,
                                            blocked_users_id
                                        )
                                } else
                                    add_element(
                                        ind,
                                        element_type,
                                        db,
                                        adapter_list,
                                        flag,
                                        flag_layout,
                                        recycle,
                                        context,
                                        inf_lay,
                                        support_fragment,
                                        img_vid,
                                        limit,
                                        frame_info,
                                        blocked_users_id
                                    )
                            }
                    }
                }
            }
    }
}


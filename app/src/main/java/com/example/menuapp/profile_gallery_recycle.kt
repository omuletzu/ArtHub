package com.example.menuapp

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson

class profile_gallery_recycle(val str_arr : ArrayList<Pair<StorageReference, StorageReference?>>, val context : Context, var flag_layout: Int) : RecyclerView.Adapter<profile_gallery_recycle.recycle_class>() {

    inner class recycle_class(itemView : View) : RecyclerView.ViewHolder(itemView){

        val element1 : ImageButton = itemView.findViewById(R.id.gallery_element1)
        val element2 : ImageButton = itemView.findViewById(R.id.gallery_element2)
        val element_lay : LinearLayout = itemView.findViewById(R.id.gallery_element_lay)
        val height = context.resources.displayMetrics.widthPixels / 2                               // height = width

        fun Bind(position: Int){

            load_img(element1, str_arr[position].first, height, flag_layout)

            if(str_arr[position].second != null)
                str_arr[position].second?.let { load_img(element2, it, height, flag_layout) }
            else {
                val element2layPar = element2.layoutParams as LinearLayout.LayoutParams
                element2layPar.width = height
                element2.layoutParams = element2layPar
                element2.visibility = ImageButton.INVISIBLE
            }

            val elemLayPar = element_lay.layoutParams as ConstraintLayout.LayoutParams
            elemLayPar.height = height

            element_lay.layoutParams = elemLayPar
        }
    }

    fun load_img(element : ImageButton, str_arr_element : StorageReference, fixed_height : Int, flag_layout : Int){

        str_arr_element.downloadUrl
            .addOnCompleteListener { uri ->

                val custom_opt = RequestOptions()
                custom_opt.override(fixed_height, fixed_height)
                custom_opt.centerCrop()

                Glide
                    .with(context)
                    .load(Uri.parse(uri.result.toString()))
                    .apply(custom_opt)
                    .into(element)

                element.setOnClickListener(){

                    str_arr_element.metadata
                        .addOnCompleteListener { metadata ->

                            val intent = Intent(context, add_art_preview::class.java)

                            var flag_layout_var = flag_layout

                            intent.putExtra("uri", uri.result.toString())
                            intent.putExtra(
                                "description",
                                metadata.result.getCustomMetadata("description")
                            )

                            if(flag_layout > 3){
                                when (metadata.result.getCustomMetadata("art_type")) {
                                        "PAINT" -> flag_layout_var = 0
                                        "DRAWING" -> flag_layout_var = 1
                                        "PHOTO" -> flag_layout_var = 2
                                        "ANIMATION" -> flag_layout_var = 3
                                    }
                            }

                            when(flag_layout_var) {
                                0 -> {
                                    intent.putExtra("t1", metadata.result.getCustomMetadata("paint_medium"))
                                    intent.putExtra("t2", metadata.result.getCustomMetadata("dimensions"))
                                    intent.putExtra("t3", metadata.result.getCustomMetadata("other_facts"))
                                }
                                1 -> {
                                    intent.putExtra("t1", metadata.result.getCustomMetadata("drawing_medium"))
                                    intent.putExtra("t2", metadata.result.getCustomMetadata("dimensions"))
                                    intent.putExtra("t3", metadata.result.getCustomMetadata("other_facts"))
                                }
                                2 -> {
                                    intent.putExtra("t1", metadata.result.getCustomMetadata("title"))
                                    intent.putExtra("t2", metadata.result.getCustomMetadata("location"))
                                    intent.putExtra("t3", metadata.result.getCustomMetadata("camera_used"))
                                    intent.putExtra("t4", metadata.result.getCustomMetadata("exposure"))
                                    intent.putExtra("t5", metadata.result.getCustomMetadata("other_facts"))
                                }
                                3 -> {
                                    intent.putExtra("t1", metadata.result.getCustomMetadata("title"))
                                    intent.putExtra("t2", metadata.result.getCustomMetadata("genre"))
                                    intent.putExtra("t3", metadata.result.getCustomMetadata("animation_software"))
                                    intent.putExtra("t4", metadata.result.getCustomMetadata("storyboard"))
                                    intent.putExtra("t5", metadata.result.getCustomMetadata("other_facts"))
                                }
                            }

                            val original_height =
                                metadata.result.getCustomMetadata("height")?.toInt()
                            val original_width =
                                metadata.result.getCustomMetadata("width")?.toInt()

                            intent.putExtra("width", original_width)
                            intent.putExtra("height", original_height)

                            val arr_tags =
                                Gson().fromJson(
                                    metadata.result.getCustomMetadata("tags"),
                                    ArrayList::class.java
                                ) as ArrayList<String>

                            intent.putExtra("tags", arr_tags)
                            intent.putExtra("frame_flag", flag_layout_var)
                            intent.putExtra("inv_post_flag", true)
                            intent.putExtra("inv_delete_flag", false)
                            intent.putExtra("author_id", metadata.result.getCustomMetadata("element_id")?.split("_")?.get(0))
                            intent.putExtra("post_id", metadata.result.getCustomMetadata("element_id"))

                            context?.startActivity(intent)
                        }
                }
            }
    }

    override fun getItemCount(): Int {
        return str_arr.size
    }

    override fun onBindViewHolder(holder: recycle_class, position: Int) {
        holder.Bind(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): recycle_class {
        val inf_view = LayoutInflater.from(parent.context)
            .inflate(R.layout.profile_gallery_element, parent, false)
        return recycle_class(inf_view)
    }
}
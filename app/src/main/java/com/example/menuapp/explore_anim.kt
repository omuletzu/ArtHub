package com.example.menuapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.StorageReference

class explore_anim(val xml_layout : Int, val support_fragment : FragmentManager) : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val inf_lay = inflater.inflate(xml_layout, container, false)
        val recycle : RecyclerView = inf_lay.findViewById(R.id.explore_recycle)
        val frame_info : FrameLayout = inf_lay.findViewById(R.id.explore_frame_info)

        val adapter_list = ArrayList<StorageReference>()
        recycle.layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)

        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(recycle)

        val callback = object : OnBackPressedCallback(true){

            override fun handleOnBackPressed() {
                recycle.visibility = RecyclerView.VISIBLE
                frame_info.visibility = FrameLayout.INVISIBLE
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        FirebaseFirestore.getInstance().collection("adittional_info")
            .document("total_uploads").get()
            .addOnSuccessListener { task ->
                if(task.exists()) {
                    add_to_feed(
                        inf_lay,
                        recycle,
                        this.context,
                        true,
                        adapter_list,
                        3,
                        support_fragment,
                        task.getLong("total_uploads_ANIMATION"),
                        frame_info
                    )
                }
            }

        return inf_lay
    }
}
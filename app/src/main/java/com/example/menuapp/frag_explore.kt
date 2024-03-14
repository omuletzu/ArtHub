package com.example.menuapp

import android.content.Intent
import android.content.Intent.getIntent
import android.os.Bundle
import android.util.Log
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

class frag_explore(val xml_layout: Int, val supportFragmentManager : FragmentManager) : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val inf_lay = inflater.inflate(xml_layout, container, false)

        val btn1 : ImageButton = inf_lay.findViewById(R.id.btn1)
        val btn2 : ImageButton = inf_lay.findViewById(R.id.btn2)
        val btn3 : ImageButton = inf_lay.findViewById(R.id.btn3)
        val btn4 : ImageButton = inf_lay.findViewById(R.id.btn4)
        //val btn5 : ImageButton = inf_lay.findViewById(R.id.btn5)

        val btn_arr = ArrayList<ImageButton>()
        btn_arr.add(btn2)
        btn_arr.add(btn3)
        btn_arr.add(btn4)
        //btn_arr.add(btn5)

        btn1.setOnClickListener(){
            supportFragmentManager.beginTransaction().replace(R.id.explore_frame, explore_global(R.layout.activity_explore_global, childFragmentManager, btn_arr)).commit()

            alphaOn(btn1)
            alphaOff(btn2, btn3, btn4)
        }

        btn2.setOnClickListener(){
            supportFragmentManager.beginTransaction().replace(R.id.explore_frame, explore_paint(R.layout.activity_explore_paint, childFragmentManager)).commit()

            alphaOn(btn2)
            alphaOff(btn1, btn3, btn4)
        }

        btn3.setOnClickListener(){
            supportFragmentManager.beginTransaction().replace(R.id.explore_frame, explore_draw(R.layout.activity_explore_draw, childFragmentManager)).commit()

            alphaOn(btn3)
            alphaOff(btn2, btn1, btn4)
        }

        btn4.setOnClickListener(){
            supportFragmentManager.beginTransaction().replace(R.id.explore_frame, explore_photo(R.layout.activity_explore_photo, childFragmentManager)).commit()

            alphaOn(btn4)
            alphaOff(btn2, btn3, btn1)
        }

        /*btn5.setOnClickListener(){
            supportFragmentManager.beginTransaction().replace(R.id.explore_frame, explore_anim(R.layout.activity_explore_anim, childFragmentManager)).commit()

            alphaOn(btn5)
            alphaOff(btn2, btn3, btn1, btn4)
        }*/

        btn1.performClick()

        /*when(activity?.intent?.getIntExtra("frame_forced", 1)){
            0 -> btn2.performClick()
            1 -> btn3.performClick()
            2 -> btn4.performClick()
            3 -> btn5.performClick()
        }*/

        return inf_lay
    }

    private fun alphaOn(btn : ImageButton){
        btn.alpha = 0f
        btn.animate().alpha(0.5f).duration = 100
    }

    private fun alphaOff(btn1 : ImageButton, btn2 : ImageButton, btn3 : ImageButton){
        btn1.alpha = 1f
        btn2.alpha = 1f
        btn3.alpha = 1f
        //btn4.alpha = 1f
    }
}
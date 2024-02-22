package com.example.telegramclone.ui.fragments

import androidx.fragment.app.Fragment
import com.example.telegramclone.MainActivity
import com.example.telegramclone.utilits.APP_ACTIVITY

open class BaseFragment( layout:Int) : Fragment(layout) {

    override fun onStart() {
        super.onStart()
        APP_ACTIVITY.mAppDrawer.disableDrawer()
    }
}
package com.example.telegramclone.ui.fragments

import androidx.fragment.app.Fragment
import com.example.telegramclone.R
import com.example.telegramclone.utilits.APP_ACTIVITY
import com.example.telegramclone.utilits.hideKeyboard

class MainFragment : Fragment(R.layout.fragment_chat) {

    override fun onResume() {
        super.onResume()
        APP_ACTIVITY.title = "Telegram"
        APP_ACTIVITY.mAppDrawer.enableDrawer()
        hideKeyboard()
    }
}
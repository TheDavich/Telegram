package com.example.telegramclone.ui.fragments

import android.widget.EditText
import com.example.telegramclone.R
import com.example.telegramclone.database.CHILD_BIO
import com.example.telegramclone.database.CURRENT_UID
import com.example.telegramclone.database.NODE_USERS
import com.example.telegramclone.database.REF_DATABASE_ROOT
import com.example.telegramclone.database.USER
import com.example.telegramclone.database.setBioToDatabase
import com.example.telegramclone.utilits.showToast

class ChangeBioFragment : BaseChangeFragment(R.layout.fragment_change_bio) {

    override fun onResume() {
        super.onResume()
        val settingsInputBio = view?.findViewById<EditText>(R.id.settings_input_bio)
        settingsInputBio?.setText(USER.bio)
    }

    override fun change() {
        super.change()
        val settingsInputBio = view?.findViewById<EditText>(R.id.settings_input_bio)
        val newBio = settingsInputBio?.text.toString()
        setBioToDatabase(newBio)
    }

}

package com.example.telegramclone.ui.fragments

import android.widget.EditText
import com.example.telegramclone.R
import com.example.telegramclone.utilits.APP_ACTIVITY
import com.example.telegramclone.database.CHILD_FULLNAME
import com.example.telegramclone.database.CURRENT_UID
import com.example.telegramclone.database.NODE_USERS
import com.example.telegramclone.database.REF_DATABASE_ROOT
import com.example.telegramclone.database.USER
import com.example.telegramclone.database.setNameToDatabase
import com.example.telegramclone.utilits.showToast

class ChangeNameFragment : BaseChangeFragment(R.layout.fragment_change_name) {

    override fun onResume() {
        super.onResume()
        initFullNameList()
    }

    private fun initFullNameList() {
        val fullNameList = USER.fullName.split(" ")
        if (fullNameList.size > 1) {
            val settingsInputName = view?.findViewById<EditText>(R.id.settings_input_name)
            val settingsInputSurname = view?.findViewById<EditText>(R.id.settings_input_surname)
            settingsInputName?.setText(fullNameList[0])
            settingsInputSurname?.setText(fullNameList[1])
        } else {
            val settingsInputName = view?.findViewById<EditText>(R.id.settings_input_name)
            settingsInputName?.setText(fullNameList[0])
        }
    }

    override fun change() {
        val settingsInputName = view?.findViewById<EditText>(R.id.settings_input_name)
        val settingsInputSurname = view?.findViewById<EditText>(R.id.settings_input_surname)
        val name = settingsInputName?.text.toString()
        val surname = settingsInputSurname?.text.toString()
        if (name.isEmpty()) {
            showToast(getString(R.string.settings_toast_name_is_empty))
        } else {
            val fullName = "$name $surname"
            setNameToDatabase(fullName)
        }
    }
}

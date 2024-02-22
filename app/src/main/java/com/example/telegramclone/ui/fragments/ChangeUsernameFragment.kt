package com.example.telegramclone.ui.fragments

import android.widget.EditText
import com.example.telegramclone.R
import com.example.telegramclone.utilits.AppValueEventListener
import com.example.telegramclone.database.CHILD_USERNAME
import com.example.telegramclone.database.CURRENT_UID
import com.example.telegramclone.database.NODE_USERNAMES
import com.example.telegramclone.database.NODE_USERS
import com.example.telegramclone.database.REF_DATABASE_ROOT
import com.example.telegramclone.database.USER
import com.example.telegramclone.database.updateCurrentUsername
import com.example.telegramclone.utilits.showToast
import java.util.Locale

class ChangeUsernameFragment : BaseChangeFragment(R.layout.fragment_change_username) {

    private lateinit var mNewUsername: String

    override fun onResume() {
        super.onResume()
        val settingsInputUsername = view?.findViewById<EditText>(R.id.settings_input_username)
        settingsInputUsername?.setText(USER.username)
    }

    override fun change() {
        val settingsInputUsername = view?.findViewById<EditText>(R.id.settings_input_username)
        mNewUsername = settingsInputUsername?.text.toString().toLowerCase(Locale.getDefault())
        if (mNewUsername.isEmpty()) {
            showToast("Field cannot be empty")
        } else {
            REF_DATABASE_ROOT.child(NODE_USERNAMES)
                .addListenerForSingleValueEvent(AppValueEventListener { dataSnapshot ->
                    if (dataSnapshot.hasChild(mNewUsername)) {
                        showToast("This username has already been taken")
                    } else {
                        changeUsername()
                    }
                })
        }
    }

    private fun changeUsername() {
        REF_DATABASE_ROOT.child(NODE_USERNAMES).child(mNewUsername).setValue(CURRENT_UID)
            .addOnCompleteListener { usernameTask ->
                if (usernameTask.isSuccessful) {
                    updateCurrentUsername(mNewUsername)
                }
            }
    }

}


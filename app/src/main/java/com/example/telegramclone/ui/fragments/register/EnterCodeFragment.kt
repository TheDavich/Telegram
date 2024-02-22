package com.example.telegramclone.ui.fragments.register

import android.widget.EditText
import androidx.fragment.app.Fragment
import com.example.telegramclone.R
import com.example.telegramclone.utilits.APP_ACTIVITY
import com.example.telegramclone.database.AUTH
import com.example.telegramclone.utilits.AppTextWatcher
import com.example.telegramclone.database.CHILD_ID
import com.example.telegramclone.database.CHILD_PHONE
import com.example.telegramclone.database.CHILD_USERNAME
import com.example.telegramclone.database.NODE_PHONES
import com.example.telegramclone.database.NODE_USERS
import com.example.telegramclone.database.REF_DATABASE_ROOT
import com.example.telegramclone.utilits.restartActivity
import com.example.telegramclone.utilits.showToast
import com.google.firebase.auth.PhoneAuthProvider

class EnterCodeFragment(val phoneNumber: String, val id: String) :
    Fragment(R.layout.fragment_enter_code) {

    private lateinit var registerInputCode: EditText

    override fun onStart() {
        super.onStart()
        APP_ACTIVITY.title = phoneNumber
        registerInputCode = view?.findViewById(R.id.register_input_code) as EditText
        registerInputCode.addTextChangedListener(AppTextWatcher {
            val string = registerInputCode.text.toString()
            if (string.length == 6) {
                enterCode()
            }
        })
    }

    private fun enterCode() {
        val code = registerInputCode.text.toString()
        val credential = PhoneAuthProvider.getCredential(id, code)
        AUTH.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val uid = AUTH.currentUser?.uid.toString()
                val dateMap = mutableMapOf<String, Any>()
                dateMap[CHILD_ID] = uid
                dateMap[CHILD_PHONE] = phoneNumber
                dateMap[CHILD_USERNAME] = uid

                REF_DATABASE_ROOT.child(NODE_PHONES).child(phoneNumber).setValue(uid)
                    .addOnFailureListener { showToast(it.message.toString()) }
                    .addOnSuccessListener {
                        REF_DATABASE_ROOT.child(NODE_USERS).child(uid).updateChildren(dateMap)
                            .addOnSuccessListener {
                                showToast("Welcome!")
                                restartActivity()
                            }
                            .addOnFailureListener { showToast(it.message.toString()) }
                    }

            } else showToast(task.exception?.message.toString())
        }
    }
}

package com.example.telegramclone.utilits

import com.example.telegramclone.database.AUTH
import com.example.telegramclone.database.CHILD_STATE
import com.example.telegramclone.database.CURRENT_UID
import com.example.telegramclone.database.NODE_USERS
import com.example.telegramclone.database.REF_DATABASE_ROOT
import com.example.telegramclone.database.USER

enum class AppStates(val state:String) {

    ONLINE("Online"),
    OFFLINE("Offline"),
    TYPING("Typing...");

    companion object{
        fun updateState(appStates: AppStates){
            /*Функция принимает состояние и записывает в базу данных*/
            if (AUTH.currentUser!=null){
                REF_DATABASE_ROOT.child(NODE_USERS).child(CURRENT_UID).child(CHILD_STATE)
                    .setValue(appStates.state)
                    .addOnSuccessListener { USER.state = appStates.state }
                    .addOnFailureListener { showToast(it.message.toString()) }
            }
        }
    }
}
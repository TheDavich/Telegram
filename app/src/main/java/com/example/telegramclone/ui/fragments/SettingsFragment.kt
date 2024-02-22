package com.example.telegramclone.ui.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.telegramclone.R
import com.example.telegramclone.utilits.APP_ACTIVITY
import com.example.telegramclone.database.AUTH
import com.example.telegramclone.utilits.AppStates
import com.example.telegramclone.database.CURRENT_UID
import com.example.telegramclone.database.FOLDER_PROFILE_IMAGE
import com.example.telegramclone.database.REF_STORAGE_ROOT
import com.example.telegramclone.database.USER
import com.example.telegramclone.utilits.downloadAndSetImage
import com.example.telegramclone.database.getUrlFromStorage
import com.example.telegramclone.database.putImageToStorage
import com.example.telegramclone.database.putUrlToDatabase
import com.example.telegramclone.utilits.replaceFragment
import com.example.telegramclone.utilits.restartActivity
import com.example.telegramclone.utilits.showToast
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import de.hdodenhof.circleimageview.CircleImageView


class SettingsFragment : BaseFragment(R.layout.fragment_settings) {

    private lateinit var bioTextView: TextView
    private lateinit var fullNameTextView: TextView
    private lateinit var phoneNumberTextView: TextView
    private lateinit var statusTextView: TextView
    private lateinit var usernameTextView: TextView
    private lateinit var changeUsernameButton: ConstraintLayout
    private lateinit var changeBioButton: ConstraintLayout
    private lateinit var changePhotoButton: CircleImageView
    private lateinit var userPhotoImageView: CircleImageView

    override fun onResume() {
        super.onResume()
        setHasOptionsMenu(true)
        initFields()
    }

    private fun initFields() {
        bioTextView = requireView().findViewById(R.id.settings_bio)
        fullNameTextView = requireView().findViewById(R.id.settings_full_name)
        phoneNumberTextView = requireView().findViewById(R.id.settings_phone_number)
        statusTextView = requireView().findViewById(R.id.settings_status)
        usernameTextView = requireView().findViewById(R.id.settings_username)
        changeUsernameButton = requireView().findViewById(R.id.settings_btn_change_username)
        changeBioButton = requireView().findViewById(R.id.settings_btn_change_bio)
        changePhotoButton = requireView().findViewById(R.id.settings_change_photo)
        userPhotoImageView = requireView().findViewById(R.id.settings_user_photo)

        bioTextView.text = USER.bio
        fullNameTextView.text = USER.fullName
        phoneNumberTextView.text = USER.phone
        statusTextView.text = USER.state
        usernameTextView.text = USER.username
        changeUsernameButton.setOnClickListener { replaceFragment(ChangeUsernameFragment()) }
        changeBioButton.setOnClickListener { replaceFragment(ChangeBioFragment()) }
        changePhotoButton.setOnClickListener { changePhotoUser() }
        userPhotoImageView.downloadAndSetImage(USER.photoUrl)
    }

    private fun changePhotoUser() {
        /* Изменение фото пользователя */
        CropImage.activity()
            .setAspectRatio(1, 1)
            .setRequestedSize(600, 600)
            .setCropShape(CropImageView.CropShape.OVAL)
            .start(APP_ACTIVITY, this)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        /* Создание выпадающего меню */
        inflater.inflate(R.menu.settings_action_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        /* Слушатель выбора пунктов выпадающего меню */
        when (item.itemId) {
            R.id.settings_menu_exit -> {
                AppStates.updateState(AppStates.OFFLINE)
                AUTH.signOut()
                restartActivity()
            }
            R.id.settings_menu_change_name -> replaceFragment(ChangeNameFragment())
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        /* Активность, которая запускается для получения картинки для фото пользователя */
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE
            && resultCode == RESULT_OK && data != null
        ) {
            val uri = CropImage.getActivityResult(data).uri
            val path = REF_STORAGE_ROOT.child(FOLDER_PROFILE_IMAGE).child(CURRENT_UID)
            putImageToStorage(uri, path) {
                getUrlFromStorage(path) {
                    putUrlToDatabase(it) {
                        userPhotoImageView.downloadAndSetImage(it)
                        showToast(getString(R.string.toast_data_updated))
                        USER.photoUrl = it
                        APP_ACTIVITY.mAppDrawer.updateHeader()
                    }
                }
            }
        }
    }
}

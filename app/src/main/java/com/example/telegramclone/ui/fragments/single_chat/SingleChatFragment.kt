package com.example.telegramclone.ui.fragments.single_chat

import android.view.View
import android.widget.AbsListView
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.example.telegramclone.R
import com.example.telegramclone.database.CURRENT_UID
import com.example.telegramclone.database.NODE_MESSAGES
import com.example.telegramclone.database.NODE_USERS
import com.example.telegramclone.database.REF_DATABASE_ROOT
import com.example.telegramclone.database.TYPE_TEXT
import com.example.telegramclone.database.getCommonModel
import com.example.telegramclone.database.getUserModel
import com.example.telegramclone.database.sendMessage
import com.example.telegramclone.models.CommonModel
import com.example.telegramclone.models.UserModel
import com.example.telegramclone.ui.fragments.BaseFragment
import com.example.telegramclone.utilits.APP_ACTIVITY
import com.example.telegramclone.utilits.AppChildEventListener
import com.example.telegramclone.utilits.AppValueEventListener
import com.example.telegramclone.utilits.downloadAndSetImage
import com.example.telegramclone.utilits.showToast
import com.google.firebase.database.DatabaseReference
import de.hdodenhof.circleimageview.CircleImageView

class SingleChatFragment(private val contact: CommonModel) :
    BaseFragment(R.layout.fragment_single_chat) {

    private lateinit var mListenerInfoToolbar: AppValueEventListener
    private lateinit var mReceivingUser: UserModel
    private lateinit var mToolbarInfo: View
    private lateinit var mRefUser: DatabaseReference
    private lateinit var mRefMessages: DatabaseReference
    private lateinit var mAdapter: SingleChatAdapter
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mMessagesListener: AppChildEventListener
    private var mCountMessages = 15
    private var mIsScrolling = false
    private var mSmoothScrollToPosition = true
    private lateinit var mSwipeRefreshLayout: SwipeRefreshLayout
    private lateinit var mLayoutManager: LinearLayoutManager

    override fun onResume() {
        super.onResume()
        initFields()
        initFields()
        initToolbar()
        initRecyclerView()

    }

    private fun initFields() {
        val swipeRefresh = requireView().findViewById<SwipeRefreshLayout>(R.id.chat_swipe_refresh)
        mLayoutManager = LinearLayoutManager(this.context)
        mSwipeRefreshLayout = swipeRefresh
    }

    private fun initRecyclerView() {
        mRecyclerView = requireView().findViewById(R.id.chat_recycle_view)
        mAdapter = SingleChatAdapter()
        mRefMessages = REF_DATABASE_ROOT.child(NODE_MESSAGES).child(CURRENT_UID).child(contact.id)
        mRecyclerView.adapter = mAdapter
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.isNestedScrollingEnabled = false
        mRecyclerView.layoutManager = mLayoutManager
        mMessagesListener = AppChildEventListener {

            val message = it.getCommonModel()

            if (mSmoothScrollToPosition) {
                mAdapter.addItemToBottom(message) {
                    mRecyclerView.smoothScrollToPosition(mAdapter.itemCount)
                }
            } else {
                mAdapter.addItemToTop(message) {
                    mSwipeRefreshLayout.isRefreshing = false
                }
            }
        }

        mRefMessages.limitToLast(mCountMessages).addChildEventListener(mMessagesListener)

        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (mIsScrolling && dy < 0 && mLayoutManager.findFirstVisibleItemPosition() <= 3) {
                    updateData()
                }
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    mIsScrolling = true
                }

            }
        })

        mSwipeRefreshLayout.setOnRefreshListener { updateData() }
    }

    private fun updateData() {
        mSmoothScrollToPosition = false
        mIsScrolling = false
        mCountMessages += 10
        mRefMessages.removeEventListener(mMessagesListener)
        mRefMessages.limitToLast(mCountMessages).addChildEventListener(mMessagesListener)
    }

    private fun initToolbar() {
        mToolbarInfo = APP_ACTIVITY.mToolbar.findViewById(R.id.toolbar_info)
        mToolbarInfo.visibility = View.VISIBLE
        mListenerInfoToolbar = AppValueEventListener {
            mReceivingUser = it.getUserModel()
            initInfoToolbar()
        }

        mRefUser = REF_DATABASE_ROOT.child(NODE_USERS).child(contact.id)
        mRefUser.addValueEventListener(mListenerInfoToolbar)
        val btnChatSendMessage = requireView().findViewById<ImageView>(R.id.chat_btn_send_message)

        btnChatSendMessage.setOnClickListener {
            mSmoothScrollToPosition = true
            val messageEditText = requireView().findViewById<EditText>(R.id.chat_input_message)
            val message = messageEditText.text.toString()
            if (message.isEmpty()) {
                showToast("You can't send empty message")
            } else {
                sendMessage(message, contact.id, TYPE_TEXT) {
                    messageEditText.setText("")
                }
            }
        }
    }

    private fun initInfoToolbar() {
        if (mReceivingUser.fullName.isEmpty()) {
            val fullName = mToolbarInfo.findViewById<TextView>(R.id.toolbar_chat_fullname)
            fullName.text = contact.fullName
        } else {
            val fullName = mToolbarInfo.findViewById<TextView>(R.id.toolbar_chat_fullname)
            fullName.text = mReceivingUser.fullName
        }

        val image = mToolbarInfo.findViewById<CircleImageView>(R.id.toolbar_chat_image)
        image.downloadAndSetImage(mReceivingUser.photoUrl)

        val status = mToolbarInfo.findViewById<TextView>(R.id.toolbar_chat_status)
        status.text = mReceivingUser.state
    }

    override fun onPause() {
        super.onPause()
        mToolbarInfo.visibility = View.GONE
        mRefUser.removeEventListener(mListenerInfoToolbar)
        mRefMessages.removeEventListener(mMessagesListener)
    }
}


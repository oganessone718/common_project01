package com.example.common_project01.ui.friends

import FriendListAdapter
import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.common_project01.R
import com.example.common_project01.databinding.FragmentFriendListBinding
import com.example.common_project01.ui.DatabaseHelper
import com.example.common_project01.ui.UserProfile
import com.example.common_project01.ui.home.HomeFragment
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class FriendListFragment : Fragment() {

    private var _binding: FragmentFriendListBinding? = null
    lateinit var profileImage: ImageView
    lateinit var myImage:String

    // onDestroyView.
    private val binding get() = _binding!!
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ProfileEditFragment.IMAGE_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                profileImage.setImageURI(Uri.parse(myImage))
            } else {
                // 권한이 거부된 경우 사용자에게 설명을 제공하거나 다른 조치를 취할 수 있습니다.
            }
        }
    }
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        lateinit var recyclerView: RecyclerView
        lateinit var viewAdapter: RecyclerView.Adapter<*>
        lateinit var viewManager: RecyclerView.LayoutManager

        val view = inflater.inflate(R.layout.fragment_friend_list, container, false)

        val dbHelper = DatabaseHelper(requireContext())

        val userList = dbHelper.getUsers()

        val myProfile = userList.find { it.profile }
        val friendList = userList.filter { !it.profile }

        if (myProfile != null) {
            // 프로필 레이아웃을 findViewById 또는 inflater를 사용하여 생성
            val profileLayout = view.findViewById<View>(R.id.profile_layout)

            // 프로필 데이터를 UI 요소에 표시
            val profileId = profileLayout.findViewById<TextView>(R.id.profile_id)
            val profileName = profileLayout.findViewById<TextView>(R.id.profile_name)
            val profileIntro = profileLayout.findViewById<TextView>(R.id.profile_intro)
            myImage = myProfile.image
            profileImage = profileLayout.findViewById<ImageView>(R.id.profile_image)

            val permission = Manifest.permission.READ_EXTERNAL_STORAGE
            val granted = PackageManager.PERMISSION_GRANTED
            if (ContextCompat.checkSelfPermission(requireContext(), permission) != granted) {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(permission),
                    ProfileEditFragment.IMAGE_REQUEST_CODE
                )
            } else {
                profileImage.setImageURI(Uri.parse(myImage))
            }


            profileId.text = myProfile.id
            profileName.text = myProfile.name
            profileIntro.text = myProfile.intro
        }

        viewManager = LinearLayoutManager(activity)
        viewAdapter = FriendListAdapter(friendList){ friend ->
            val bundle = Bundle()
            bundle.putInt("userPrimaryKey", friend.primaryKey) // 전달할 데이터
            findNavController().navigate(R.id.navigation_home, bundle)
        }

        recyclerView = view.findViewById<RecyclerView>(R.id.friends_recycler_view).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
// FriendListFragment.kt

// 버튼 클릭 핸들러
        val editProfileButton = view.findViewById<Button>(R.id.edit_profile_button)
        editProfileButton.setOnClickListener {
            findNavController().navigate(R.id.navigation_profile_edit)
        }

        return view
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}
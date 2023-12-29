package com.example.common_project01.ui.friends

import FriendListAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.common_project01.R
import com.example.common_project01.databinding.FragmentFriendListBinding

class FriendListFragment : Fragment() {

    private var _binding: FragmentFriendListBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        lateinit var recyclerView: RecyclerView
        lateinit var viewAdapter: RecyclerView.Adapter<*>
        lateinit var viewManager: RecyclerView.LayoutManager

//        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
//        val root: View = binding.root
//
//        return root

        val view = inflater.inflate(R.layout.fragment_friend_list, container, false)

        val friends = listOf(
            Friend(R.drawable.ic_home_black_24dp, "박정민", "@oganessone718", "우짜노",true),
            Friend(R.drawable.ic_home_black_24dp, "영재", "@genius", "짱짱맨",false),
            Friend(R.drawable.ic_home_black_24dp, "영재", "@genius", "짱짱맨",false),
            Friend(R.drawable.ic_home_black_24dp, "영재", "@genius", "짱짱맨",false),
            Friend(R.drawable.ic_home_black_24dp, "영재", "@genius", "짱짱맨",false),
            Friend(R.drawable.ic_home_black_24dp, "영재", "@genius", "짱짱맨",false),
            Friend(R.drawable.ic_home_black_24dp, "영재", "@genius", "짱짱맨",false),
            Friend(R.drawable.ic_home_black_24dp, "영재", "@genius", "짱짱맨",false),
            Friend(R.drawable.ic_home_black_24dp, "영재", "@genius", "짱짱맨",false),
            Friend(R.drawable.ic_home_black_24dp, "영재", "@genius", "짱짱맨",false),
            Friend(R.drawable.ic_home_black_24dp, "영재", "@genius", "짱짱맨",false),
            Friend(R.drawable.ic_home_black_24dp, "영재", "@genius", "짱짱맨",false),
            Friend(R.drawable.ic_home_black_24dp, "영재", "@genius", "짱짱맨",false),
            Friend(R.drawable.ic_home_black_24dp, "영재", "@genius", "짱짱맨",false),
            Friend(R.drawable.ic_home_black_24dp, "영재", "@genius", "짱짱맨",false),
            // ... Add more friends here
        )
        val myProfile = friends.find { it.profile }
        val friendList = friends.filter { !it.profile }

        if (myProfile != null) {
            // 프로필 레이아웃을 findViewById 또는 inflater를 사용하여 생성
            val profileLayout = view.findViewById<View>(R.id.profile_layout)

            // 프로필 데이터를 UI 요소에 표시
            val profileImage = profileLayout.findViewById<ImageView>(R.id.profile_image)
            val profileName = profileLayout.findViewById<TextView>(R.id.profile_name)
            val profileId = profileLayout.findViewById<TextView>(R.id.profile_id)
            val profileIntro = profileLayout.findViewById<TextView>(R.id.profile_intro)

            profileImage.setImageResource(myProfile.image)
            profileName.text = myProfile.name
            profileId.text = myProfile.id
            profileIntro.text = myProfile.intro
        }

        viewManager = LinearLayoutManager(activity)
        viewAdapter = FriendListAdapter(friendList)

        recyclerView = view.findViewById<RecyclerView>(R.id.friends_recycler_view).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
// FriendListFragment.kt

// 버튼 클릭 핸들러
        val editProfileButton = view.findViewById<Button>(R.id.edit_profile_button)
        editProfileButton.setOnClickListener {
            // 프로필 수정 프래그먼트로 이동하는 네비게이션 액션 실행
            findNavController().navigate(R.id.navigation_profile_edit)
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
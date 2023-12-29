package com.example.common_project01.ui.friends

import FriendListAdapter
import android.content.ContentValues
import android.os.Bundle
import android.util.Log
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
import com.example.common_project01.ui.DatabaseHelper
import com.example.common_project01.ui.UserProfile

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

        val view = inflater.inflate(R.layout.fragment_friend_list, container, false)

        val dbHelper = DatabaseHelper(requireContext())
        val db = dbHelper.writableDatabase

        // 더미 데이터로 유저 프로필 추가
        fun addDummyUserProfile(i: Int) {

            val dummyUserProfile = UserProfile(
                primaryId = i,          // ID는 고유한 값으로 설정
                name = "John",   // 이름 설정
                id = "johnID",
                intro = "Hello, I'm John!",  // 자기 소개 설정
                image = "https://example.com/profile.jpg",  // 프로필 이미지 URL 또는 파일 경로 설정
                profile = false
            )

            // 데이터베이스에 유저 프로필 추가
            dbHelper.addProfile(dummyUserProfile)

            // 작업 완료 후 데이터베이스 연결 종료
//            db.close()
        }

        for (i:Int in 1..10)
            addDummyUserProfile(i)
            print("엥??")

        val userList = dbHelper.getUsers()

//        val friends = listOf(
//            Friend(R.drawable.ic_home_black_24dp, "박정민", "@oganessone718", "우짜노",true),
//            Friend(R.drawable.ic_home_black_24dp, "영재", "@genius", "짱짱맨",false),
//            Friend(R.drawable.ic_home_black_24dp, "영재", "@genius", "짱짱맨",false),
//            Friend(R.drawable.ic_home_black_24dp, "영재", "@genius", "짱짱맨",false),
//            Friend(R.drawable.ic_home_black_24dp, "영재", "@genius", "짱짱맨",false),
//            Friend(R.drawable.ic_home_black_24dp, "영재", "@genius", "짱짱맨",false),
//            Friend(R.drawable.ic_home_black_24dp, "영재", "@genius", "짱짱맨",false),
//            Friend(R.drawable.ic_home_black_24dp, "영재", "@genius", "짱짱맨",false),
//            Friend(R.drawable.ic_home_black_24dp, "영재", "@genius", "짱짱맨",false),
//            Friend(R.drawable.ic_home_black_24dp, "영재", "@genius", "짱짱맨",false),
//            Friend(R.drawable.ic_home_black_24dp, "영재", "@genius", "짱짱맨",false),
//            Friend(R.drawable.ic_home_black_24dp, "영재", "@genius", "짱짱맨",false),
//            Friend(R.drawable.ic_home_black_24dp, "영재", "@genius", "짱짱맨",false),
//            Friend(R.drawable.ic_home_black_24dp, "영재", "@genius", "짱짱맨",false),
//            Friend(R.drawable.ic_home_black_24dp, "영재", "@genius", "짱짱맨",false),
//            // ... Add more friends here
//        )
        val myProfile = userList.find { it.profile }
        val friendList = userList.filter { !it.profile }

        if (myProfile != null) {
            println("으아아")
            // 프로필 레이아웃을 findViewById 또는 inflater를 사용하여 생성
            val profileLayout = view.findViewById<View>(R.id.profile_layout)

            // 프로필 데이터를 UI 요소에 표시
            val profileName = profileLayout.findViewById<TextView>(R.id.profile_name)
            val profileId = profileLayout.findViewById<TextView>(R.id.profile_id)
            val profileIntro = profileLayout.findViewById<TextView>(R.id.profile_intro)
            val profileImage = profileLayout.findViewById<ImageView>(R.id.profile_image)

            profileImage.setImageResource(R.drawable.ic_home_black_24dp) //임시..
            profileName.text = myProfile.name
            profileId.text = myProfile.id
            profileIntro.text = myProfile.intro
        }

        viewManager = LinearLayoutManager(activity)
        viewAdapter = FriendListAdapter(friendList)
        print("으아아아악")

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
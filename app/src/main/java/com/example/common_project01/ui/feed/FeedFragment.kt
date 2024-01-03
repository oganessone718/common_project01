package com.example.common_project01.ui.feed
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.common_project01.R
import com.example.common_project01.databinding.FragmentFeedBinding
import com.example.common_project01.databinding.FragmentHomeBinding
import com.example.common_project01.ui.DatabaseHelper
import com.example.common_project01.ui.DiaryData
import com.example.common_project01.ui.UserProfile
import androidx.recyclerview.widget.GridLayoutManager
import java.text.SimpleDateFormat
import java.util.Date

class FeedFragment : Fragment() {

    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!
    private var isGridLayoutManager = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = FragmentFeedBinding.inflate(inflater, container, false)
        val view = binding.root

        // RecyclerView 설정
        binding.feedRecyclerView.layoutManager = LinearLayoutManager(context)
        val diaryData = loadDiaryData()
        val userData = loadUserData()
        binding.feedRecyclerView.adapter = DiaryDataAdapter(diaryData, userData, isGridLayoutManager)

        // 레이아웃 변경 버튼 리스너 설정
        binding.changeLayoutButton.setOnClickListener {
            isGridLayoutManager = !isGridLayoutManager
            binding.feedRecyclerView.layoutManager = if (isGridLayoutManager) {
                GridLayoutManager(context, 2) // 그리드 레이아웃으로 설정
            } else {
                LinearLayoutManager(context) // 리스트 레이아웃으로 설정
            }
            // Adapter를 새로 생성하거나 변경된 레이아웃 타입을 Adapter에 전달
            binding.feedRecyclerView.adapter =
                DiaryDataAdapter(diaryData, userData, isGridLayoutManager)
// =======

// class FeedFragment : Fragment() {

//     private var _binding: FragmentFeedBinding? = null
//     fun getCurrentFormattedDate(): String {
//         val dateFormat = SimpleDateFormat("yyyy.MM.dd")
//         return dateFormat.format(Date())
//     }
//     override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
//         // Inflate the layout for this fragment
//         val view = inflater.inflate(R.layout.fragment_feed, container, false)

//         val todayDate = view.findViewById<TextView>(R.id.seconddTabDate)
//         todayDate.text = getCurrentFormattedDate()

//         val recyclerView: RecyclerView = view.findViewById(R.id.feed_recyclerView)
//         recyclerView.layoutManager = LinearLayoutManager(context)
// >>>>>>> master

            updateButtonIcon()
        }

        return view
    }
    private fun updateButtonIcon() {
        val iconResId = if (isGridLayoutManager) {
            R.drawable.feed // 리스트 레이아웃 아이콘
        } else {
            R.drawable.ic_dashboard_black_24dp  // 그리드 레이아웃 아이콘
        }
        binding.changeLayoutButton.setImageResource(iconResId)
    }
    private fun loadDiaryData(): List<DiaryData> {
        val databaseHelper = DatabaseHelper(requireContext())
        return databaseHelper.getDiaryData()
    }

    private fun loadUserData(): List<UserProfile> {
        val databaseHelper = DatabaseHelper(requireContext())
        return databaseHelper.getUsers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
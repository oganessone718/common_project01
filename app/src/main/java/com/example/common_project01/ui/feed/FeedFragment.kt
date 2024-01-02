package com.example.common_project01.ui.feed
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.common_project01.R
import com.example.common_project01.databinding.FragmentFeedBinding
import com.example.common_project01.ui.DatabaseHelper
import com.example.common_project01.ui.DiaryData
import com.example.common_project01.ui.UserProfile

class FeedFragment : Fragment() {

    private var _binding: FragmentFeedBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment

        val view = inflater.inflate(R.layout.fragment_feed, container, false)

        val recyclerView: RecyclerView = view.findViewById(R.id.feed_recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // DiaryData 로드 로직
        val diaryData = loadDiaryData() // 데이터 로드 함수
        val userData = loadUserData()
        recyclerView.adapter = DiaryDataAdapter(diaryData, userData)

        return view
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
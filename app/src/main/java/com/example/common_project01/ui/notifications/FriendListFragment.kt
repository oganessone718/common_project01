package com.example.common_project01.ui.notifications

import FriendListAdapter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
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
            Friend(R.drawable.ic_home_black_24dp, "박정민", "@oganessone718", "우짜노"),
            Friend(R.drawable.ic_home_black_24dp, "영재", "@genius", "짱짱맨"),
            Friend(R.drawable.ic_home_black_24dp, "영재", "@genius", "짱짱맨"),
            Friend(R.drawable.ic_home_black_24dp, "영재", "@genius", "짱짱맨"),
            Friend(R.drawable.ic_home_black_24dp, "영재", "@genius", "짱짱맨"),
            Friend(R.drawable.ic_home_black_24dp, "영재", "@genius", "짱짱맨"),
            Friend(R.drawable.ic_home_black_24dp, "영재", "@genius", "짱짱맨"),
            Friend(R.drawable.ic_home_black_24dp, "영재", "@genius", "짱짱맨"),
            Friend(R.drawable.ic_home_black_24dp, "영재", "@genius", "짱짱맨"),
            Friend(R.drawable.ic_home_black_24dp, "영재", "@genius", "짱짱맨"),
            Friend(R.drawable.ic_home_black_24dp, "영재", "@genius", "짱짱맨"),
            Friend(R.drawable.ic_home_black_24dp, "영재", "@genius", "짱짱맨"),
            Friend(R.drawable.ic_home_black_24dp, "영재", "@genius", "짱짱맨"),
            Friend(R.drawable.ic_home_black_24dp, "영재", "@genius", "짱짱맨"),
            Friend(R.drawable.ic_home_black_24dp, "영재", "@genius", "짱짱맨"),
            Friend(R.drawable.ic_home_black_24dp, "영재", "@genius", "짱짱맨"),
            // ... Add more friends here
        )

        viewManager = LinearLayoutManager(activity)
        viewAdapter = FriendListAdapter(friends)

        recyclerView = view.findViewById<RecyclerView>(R.id.friends_recycler_view).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
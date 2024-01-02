package com.example.common_project01.ui.feed

import com.bumptech.glide.Glide
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.common_project01.R
import com.example.common_project01.ui.DiaryData
import com.example.common_project01.ui.UserProfile

class DiaryDataAdapter(private val diaryList: List<DiaryData>, private val userList: List<UserProfile>) : RecyclerView.Adapter<DiaryDataAdapter.DiaryViewHolder>() {

    class DiaryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val userIdView: TextView = view.findViewById(R.id.userId)
        val imageView: ImageView = view.findViewById(R.id.imageView)
        val profileView: ImageView = view.findViewById(R.id.profileImage)
        val feedView: TextView = view.findViewById(R.id.feedText)
        val likeButton: ImageButton = view.findViewById(R.id.likeButton)
        var isLiked = false // 좋아요 상태 추적
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_diary, parent, false)
        return DiaryViewHolder(view)
    }

    override fun onBindViewHolder(holder: DiaryViewHolder, position: Int) {
        val diary = diaryList[position]
        val user = userList.find { it.id == diary.userId }
        holder.userIdView.text = diary.userId
        holder.feedView.text = diary.feed
        // Glide를 사용하여 이미지 로드
        Glide.with(holder.itemView.context)
            .load(diary.image) // 여기에 이미지 URL 또는 URI
            .into(holder.imageView) // 이미지를 표시할 ImageView
        user?.let {
            Glide.with(holder.itemView.context)
                .load(it.image) // User 객체의 profileImage 사용
                .into(holder.profileView) // 프로필 이미지를 표시할 ImageView
        }


        holder.likeButton.setOnClickListener {
            if (holder.isLiked) {
                // 연한 회색빛 하얀색으로 변경
                holder.likeButton.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.light_grayish_white), PorterDuff.Mode.SRC_IN)
            } else {
                // 연분홍색으로 변경
                holder.likeButton.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.light_pink), PorterDuff.Mode.SRC_IN)
            }
            holder.isLiked = !holder.isLiked
        }
    }

    override fun getItemCount() = diaryList.size
}
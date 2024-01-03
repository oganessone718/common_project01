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

class DiaryDataAdapter(
    private val diaryList: List<DiaryData>,
    private val userList: List<UserProfile>,
    private val isGridLayoutManager: Boolean // 레이아웃 타입 변수 추가
) : RecyclerView.Adapter<DiaryDataAdapter.DiaryViewHolder>() {
    class DiaryViewHolder(view: View, isGridLayoutManager: Boolean) : RecyclerView.ViewHolder(view) {
        val userIdView: TextView = view.findViewById(R.id.userId)
        val imageView: ImageView? = view.findViewById(R.id.imageView)
        val profileView: ImageView? = view.findViewById(R.id.profileImage)
        val feedView: TextView? = view.findViewById(R.id.feedText)
        val likeButton: ImageButton? = view.findViewById(R.id.likeButton)
        var isLiked = false // 좋아요 상태 추적
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DiaryViewHolder {
        val layoutId = if (isGridLayoutManager) R.layout.item_diary_grid else R.layout.item_diary
        val view = LayoutInflater.from(parent.context).inflate(layoutId, parent, false)
        return DiaryViewHolder(view, isGridLayoutManager)
    }

    override fun onBindViewHolder(holder: DiaryViewHolder, position: Int) {
        val diary = diaryList[position]
        val user = userList.find { it.id == diary.userId }

        holder.userIdView.text = diary.userId
        holder.feedView?.text = diary.feed

        // Glide를 사용하여 이미지 로드 (imageView가 nullable이므로 safe call 사용)
        diary.image?.let { imageUrl ->
            Glide.with(holder.itemView.context)
                .load(imageUrl)
                .into(holder.imageView!!)
        }

        // 프로필 이미지 로드 (profileView가 nullable이므로 safe call 사용)
        user?.image?.let { userImageUrl ->
            Glide.with(holder.itemView.context)
                .load(userImageUrl)
                .into(holder.profileView!!)
        }

        // 좋아요 버튼 클릭 리스너 설정 (likeButton이 nullable이므로 safe call 사용)
        holder.likeButton?.setOnClickListener {
            if (holder.isLiked) {
                holder.likeButton.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.light_grayish_white), PorterDuff.Mode.SRC_IN)
            } else {
                holder.likeButton.setColorFilter(ContextCompat.getColor(holder.itemView.context, R.color.light_pink), PorterDuff.Mode.SRC_IN)
            }
            holder.isLiked = !holder.isLiked
        }
    }

    override fun getItemCount() = diaryList.size
}
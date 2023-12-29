import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.common_project01.R
import com.example.common_project01.ui.UserProfile

class FriendListAdapter(private val friendList: List<UserProfile>) : RecyclerView.Adapter<FriendListAdapter.FriendViewHolder>() {

    class FriendViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.friend_image)
        val nameView: TextView = view.findViewById(R.id.friend_name)
        val idView: TextView = view.findViewById(R.id.friend_id)
        val introView: TextView = view.findViewById(R.id.friend_intro)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_friend, parent, false)
        return FriendViewHolder(view)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        val friend = friendList[position]
        holder.imageView.setImageResource(R.drawable.ic_home_black_24dp) //  임시
        holder.nameView.text = friend.name
        holder.idView.text = friend.id
        holder.introView.text = friend.intro
    }

    override fun getItemCount() = friendList.size
}

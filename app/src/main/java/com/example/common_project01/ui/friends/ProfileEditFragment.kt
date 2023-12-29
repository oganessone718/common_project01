package com.example.common_project01.ui.friends
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.common_project01.R

//import kotlinx.android.synthetic.main.fragment_profile_edit.*

class ProfileEditFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 저장 버튼 클릭 이벤트 처리
//        save_button.setOnClickListener {
//            // 입력된 정보 가져오기
//            val updatedName = edit_name.text.toString()
//            val updatedId = edit_id.text.toString()
//            val updatedIntro = edit_intro.text.toString()
//
//            // 여기에서 수정된 정보를 사용하여 프로필 업데이트 로직을 구현할 수 있음
//            // 여기서는 가상의 업데이트 함수를 호출하는 것으로 대체
//
//            // 업데이트 후 프로필 화면으로 이동
//            findNavController().navigate(R.id.action_profileEditFragment_to_profileFragment)
//        }
    }
}
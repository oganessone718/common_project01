package com.example.common_project01.ui.friends
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.common_project01.R
import com.example.common_project01.ui.DatabaseHelper

//import kotlinx.android.synthetic.main.fragment_profile_edit.*

class ProfileEditFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile_edit, container, false)
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dbHelper = DatabaseHelper(requireContext())

        val saveButton = view.findViewById<View>(R.id.save_button)

        val myProfile = dbHelper.getProfile()

        val editId = view.findViewById<EditText>(R.id.edit_id)
        val editName = view.findViewById<EditText>(R.id.edit_name)
        val editIntro = view.findViewById<EditText>(R.id.edit_intro)
        var myPrimaryId = "tmp"

        if(myProfile.size!=1) {
            print("error")
            editId.setText(myProfile.size.toString())
        }
        else {
            myPrimaryId = myProfile[0].id
            editId.setText(myProfile[0].id)
            editName.setText(myProfile[0].name)
            editIntro.setText(myProfile[0].intro)
            // 수정필요(사진추가)
            }

        // 저장 버튼 클릭 이벤트 처리
        saveButton.setOnClickListener {
            // 입력된 정보 가져오기
            val updatedId = editId.text.toString()
            val updatedName = editName.text.toString()
            val updatedIntro = editIntro.text.toString()
            val updatedImage = "수정필요"
            dbHelper.updateProfile(myPrimaryId, updatedId, updatedName, updatedIntro, updatedImage)
        }
    }
}
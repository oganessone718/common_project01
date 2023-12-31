package com.example.common_project01.ui.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.content.Context
import android.icu.util.Calendar
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import com.example.common_project01.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var userID: String = "userID"
    private var currentYear: Int = 0
    private var currentMonth: Int = 0
    private var currentDay: Int = 0
    private lateinit var str: String
    private lateinit var diaryDatabaseHelper: DiaryDatabaseHelper
    companion object {
        const val IMAGE_REQUEST_CODE = 1000
    }


    // onCreateView: Fragment의 뷰를 생성할 때 호출하는 메서드
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root
        diaryDatabaseHelper = DiaryDatabaseHelper(requireContext())

        // 현재 날짜로 초기화
        val calendar = Calendar.getInstance()
        currentYear = calendar.get(Calendar.YEAR)
        currentMonth = calendar.get(Calendar.MONTH)
        currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        // 초기 날짜로 일기 데이터 로드
        loadDiaryData(currentYear, currentMonth, currentDay)

        // UI 초기화
        with(binding) {
            title.text = "시나브로"

            // CalendarView의 날짜 변경 리스너 설정
            calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
                currentYear = year
                currentMonth = month
                currentDay = dayOfMonth

                loadDiaryData(year, month, dayOfMonth)
            }

                saveBtn.setOnClickListener {
                    val date = formatDate(currentYear, currentMonth, currentDay)
                    val content = contextEditText.text.toString()
                    val diaryData = diaryDatabaseHelper.getDiary(date)
                    val imageUri = diaryData?.image ?: ""
                    diaryDatabaseHelper.insertOrUpdateDiary(userID, date, imageUri, content)
                    contextEditText.visibility = View.INVISIBLE
                    saveBtn.visibility = View.INVISIBLE
                    updateBtn.visibility = View.VISIBLE
                    deleteBtn.visibility = View.VISIBLE
                    str = contextEditText.text.toString()
                    diaryContent.text = str
                    diaryContent.visibility = View.VISIBLE
                }
                updateBtn.setOnClickListener {
                    contextEditText.visibility = View.VISIBLE
                    diaryContent.visibility = View.INVISIBLE
                    contextEditText.setText(str)
                    saveBtn.visibility = View.VISIBLE
                    updateBtn.visibility = View.INVISIBLE
                    deleteBtn.visibility = View.INVISIBLE
                    diaryContent.text = contextEditText.text
                }
                deleteBtn.setOnClickListener {
                    val date = formatDate(currentYear, currentMonth, currentDay)
                    diaryDatabaseHelper.deleteDiary(date)
                    diaryContent.visibility = View.INVISIBLE
                    updateBtn.visibility = View.INVISIBLE
                    deleteBtn.visibility = View.INVISIBLE
                    imageView.visibility = View.INVISIBLE
                    pickImageButton.visibility = View.VISIBLE
                    contextEditText.setText("")
                    contextEditText.visibility = View.VISIBLE
                    saveBtn.visibility = View.VISIBLE
                }

                pickImageButton.setOnClickListener {
                    pickImageFromGallery()
                    Handler(Looper.getMainLooper()).postDelayed({
                        binding.pickImageButton.visibility = View.INVISIBLE
                    }, 2000)
                }
        }

        return view
    }
    private fun formatDate(year: Int, month: Int, day: Int): String {
        return "$year-${month + 1}-$day"
    }

    private fun loadDiaryData(year: Int, month: Int, day: Int) {
        val selectedDate = formatDate(year, month, day)
        val diaryData = diaryDatabaseHelper.getDiary(selectedDate)

        if (diaryData != null && diaryData.date == selectedDate) {
            // diaryData가 존재하고, 선택된 날짜와 일치하는 경우 데이터 로딩
            binding.diaryContent.setText(diaryData.feed)
            binding.imageView.setImageURI(Uri.parse(diaryData.image))
            binding.pickImageButton.visibility = View.INVISIBLE
            // 이미지가 없으면 이미지 선택 버튼 보이게
            if (diaryData.image.isEmpty()) {
                binding.pickImageButton.visibility = View.VISIBLE
            } else {
                binding.pickImageButton.visibility = View.INVISIBLE
            }
            binding.contextEditText.visibility = View.INVISIBLE
            binding.diaryContent.visibility = View.VISIBLE
            binding.updateBtn.visibility = View.VISIBLE
            binding.deleteBtn.visibility = View.VISIBLE
            binding.saveBtn.visibility = View.INVISIBLE
        } else {
            // diaryData가 null이거나 선택된 날짜와 일치하지 않는 경우
            clearUI()
            binding.contextEditText.setText("")
            binding.contextEditText.visibility = View.VISIBLE
            binding.diaryContent.visibility = View.INVISIBLE  // diaryContent를 INVISIBLE로 설정
        }
    }

    private fun clearUI() {
        with(binding) {
            contextEditText.setText("")
            imageView.setImageDrawable(null)
            pickImageButton.visibility = View.VISIBLE
            diaryContent.visibility = View.VISIBLE
            saveBtn.visibility = View.VISIBLE
            updateBtn.visibility = View.INVISIBLE
            deleteBtn.visibility = View.INVISIBLE
        }
    }
    // 이미지 선택을 위한 인텐트를 시작하는 메서드
    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_REQUEST_CODE)
    }

    // 이미지 선택 결과를 처리하는 메서드
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.data?.let { imageUri ->
                binding.imageView.setImageURI(imageUri)

                val date = formatDate(currentYear, currentMonth, currentDay)
                val diaryData = diaryDatabaseHelper.getDiary(date)

                if (diaryData != null && diaryData.date == date) {
                    // diaryData가 존재하고, 선택된 날짜와 일치하는 경우 데이터 로딩
                    binding.diaryContent.setText(diaryData.feed)
                }
                var feed = ""
                if (diaryData != null) {
                    feed = diaryData.feed
                }
                diaryDatabaseHelper.insertOrUpdateDiary(userID, date, imageUri.toString(), feed)
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
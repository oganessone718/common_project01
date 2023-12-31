package com.example.common_project01.ui.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.content.Context
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
    private lateinit var fname: String
    private lateinit var str: String
    private lateinit var pickImageLauncher: ActivityResultLauncher<String>
    companion object {
        const val IMAGE_REQUEST_CODE = 1000
    }


    // onCreateView: Fragment의 뷰를 생성할 때 호출하는 메서드
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        // UI 초기화
        with(binding) {
            title.text = "시나브로"

            // CalendarView의 날짜 변경 리스너 설정
            calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
                currentYear = year
                currentMonth = month
                currentDay = dayOfMonth

                contextEditText.setText("")
                // 선택된 날짜에 해당하는 이미지 파일 이름
                val imgName = "$userID$year-${month + 1}-$dayOfMonth-image.txt"

                // 이미지 파일 존재 여부 확인
                val imgFile = context?.getFileStreamPath(imgName)
                if (imgFile != null && imgFile.exists()) {
                    // 이미지 파일이 존재하면 로드
                    val imgPath = imgFile.inputStream().bufferedReader().use { it.readText() }
                    loadImage(imgPath)
                    binding.pickImageButton.visibility = View.INVISIBLE
                } else {
                    // 이미지 파일이 없으면 이미지 뷰 초기화
                    binding.imageView.setImageDrawable(null)
                    // 이미지 선택 버튼 visible
                    binding.pickImageButton.visibility = View.VISIBLE
                }
                saveBtn.visibility = View.VISIBLE
                contextEditText.visibility = View.VISIBLE
                diaryContent.visibility = View.INVISIBLE
                updateBtn.visibility = View.INVISIBLE
                deleteBtn.visibility = View.INVISIBLE
                checkDay(year, month, dayOfMonth)
            }
            // Save 버튼 클릭 리스너
            saveBtn.setOnClickListener {
                saveDiary()
                contextEditText.visibility = View.INVISIBLE
                saveBtn.visibility = View.INVISIBLE
                updateBtn.visibility = View.VISIBLE
                deleteBtn.visibility = View.VISIBLE
                str = contextEditText.text.toString()
                diaryContent.text = str
                diaryContent.visibility = View.VISIBLE
            }

            // Update 버튼 클릭 리스너
            updateBtn.setOnClickListener {
                contextEditText.visibility = View.VISIBLE
                diaryContent.visibility = View.INVISIBLE
                contextEditText.setText(str)
                saveBtn.visibility = View.VISIBLE
                updateBtn.visibility = View.INVISIBLE
                deleteBtn.visibility = View.INVISIBLE
                diaryContent.text = contextEditText.text
            }
            //delete 버튼 클릭 리스너
            deleteBtn.setOnClickListener {
                diaryContent.visibility = View.INVISIBLE
                updateBtn.visibility = View.INVISIBLE
                deleteBtn.visibility = View.INVISIBLE
                contextEditText.setText("")
                contextEditText.visibility = View.VISIBLE
                saveBtn.visibility = View.VISIBLE
                removeDiary()
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

    private fun checkDay(year: Int, month: Int, day: Int) {
        fname = "$userID$year-${month + 1}-$day.txt"
        val imgName = "$userID$year-${month + 1}-$day-image.txt" // 이미지 파일 경로를 저장할 파일명
        try {
            context?.openFileInput(fname)?.use { fileInputStream ->
                val fileData = ByteArray(fileInputStream.available())
                fileInputStream.read(fileData)
                str = String(fileData)
                with(binding) {
                    contextEditText.visibility = View.INVISIBLE
                    diaryContent.visibility = View.VISIBLE
                    diaryContent.text = str
                    saveBtn.visibility = View.INVISIBLE
                    updateBtn.visibility = View.VISIBLE
                    deleteBtn.visibility = View.VISIBLE
                }
            }
            context?.openFileInput(imgName)?.use { fileInputStream -> // 이미지 경로 로드
                val imgPath = fileInputStream.bufferedReader().use { it.readText() }
                loadImage(imgPath) // 이미지 로드 함수 호출
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun saveDiary() {
        try {
            context?.openFileOutput(fname, Context.MODE_PRIVATE)?.use { fileOutputStream ->
                val content = binding.contextEditText.text.toString()
                fileOutputStream.write(content.toByteArray())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun removeDiary() {
        try {
            // 텍스트 파일 삭제
            context?.openFileOutput(fname, Context.MODE_PRIVATE)?.use { fileOutputStream ->
                fileOutputStream.write("".toByteArray())
            }
            // 이미지 파일 삭제
            val imgName = "$userID${currentYear}-${currentMonth + 1}-${currentDay}-image.txt"
            context?.deleteFile(imgName)

            with(binding) {
                // UI 업데이트
                diaryContent.text = "" // 텍스트 내용 삭제
                imageView.setImageDrawable(null) // 이미지 뷰 초기화
                pickImageButton.visibility = View.VISIBLE // 이미지 선택 버튼 보이게 설정
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_REQUEST_CODE)
    }

    // 이미지 로드 함수
    private fun loadImage(imagePath: String) {
        // imagePath를 사용하여 imageView에 이미지 로드
        val imgUri = Uri.parse(imagePath)
        binding.imageView.setImageURI(imgUri)
    }

    // 이미지 선택 후 onActivityResult에서 이미지 경로 저장
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            val imgUri = data?.data
            binding.imageView.setImageURI(imgUri)

            // 이미지 경로 저장
            val imgName = "$userID${currentYear}-${currentMonth + 1}-${currentDay}-image.txt"
            context?.openFileOutput(imgName, Context.MODE_PRIVATE)?.use { fileOutputStream ->
                val imgPath = imgUri.toString()
                fileOutputStream.write(imgPath.toByteArray())
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
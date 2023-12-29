package com.example.common_project01.ui.home

import android.os.Bundle
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.common_project01.databinding.FragmentHomeBinding
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private var userID: String = "userID"
    private lateinit var fname: String
    private lateinit var str: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        // UI 초기화
        with(binding) {
            title.text = "시나브로"
            calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
                saveBtn.visibility = View.VISIBLE
                contextEditText.visibility = View.VISIBLE
                diaryContent.visibility = View.INVISIBLE
                updateBtn.visibility = View.INVISIBLE
                deleteBtn.visibility = View.INVISIBLE
                contextEditText.setText("")
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
        }

        return view
    }


    private fun checkDay(year: Int, month: Int, day: Int) {
        fname = "$userID$year-${month + 1}-$day.txt"
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
            context?.openFileOutput(fname, Context.MODE_PRIVATE)?.use { fileOutputStream ->
                fileOutputStream.write("".toByteArray())
                with(binding) {
                    diaryContent.text = ""
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
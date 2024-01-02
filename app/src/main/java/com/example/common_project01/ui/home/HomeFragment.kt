package com.example.common_project01.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.content.Context
import android.content.CursorLoader
import android.icu.util.Calendar
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.common_project01.R
import com.example.common_project01.databinding.FragmentHomeBinding
import com.example.common_project01.ui.DatabaseHelper
import com.example.common_project01.ui.UserProfile
import com.example.common_project01.ui.friends.RealPathUtil
import kotlin.properties.Delegates
import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.provider.MediaStore
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.common_project01.ui.DiaryData

object RealPathUtil {

    @SuppressLint("NewApi")
    fun getRealPathFromURI_API19(context: Context, uri: Uri): String {
        var filePath = ""
        val wholeID = DocumentsContract.getDocumentId(uri)

        // Split at colon, use second item in the array
        val id = wholeID.split(":")[1]

        val column = arrayOf(MediaStore.Images.Media.DATA)

        // where id is equal to
        val sel = MediaStore.Images.Media._ID + "=?"

        context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            column, sel, arrayOf(id), null
        )?.use { cursor ->
            val columnIndex = cursor.getColumnIndex(column[0])

            if (cursor.moveToFirst()) {
                filePath = cursor.getString(columnIndex)
            }
        }

        return filePath
    }

    @SuppressLint("NewApi")
    fun getRealPathFromURI_API11to18(context: Context, contentUri: Uri): String? {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursorLoader = CursorLoader(
            context,
            contentUri, proj, null, null, null
        )
        val cursor = cursorLoader.loadInBackground()

        cursor?.use {
            val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            if (cursor.moveToFirst()) {
                return cursor.getString(column_index)
            }
        }
        return null
    }

    fun getRealPathFromURI_BelowAPI11(context: Context, contentUri: Uri): String? {
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        context.contentResolver.query(contentUri, proj, null, null, null)?.use { cursor ->
            val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            if (cursor.moveToFirst()) {
                return cursor.getString(column_index)
            }
        }
        return null
    }
}

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var profile: UserProfile
    private lateinit var selectedDate: String
    private lateinit var diaryDatabaseHelper: DatabaseHelper
    private var userPrimaryKey by Delegates.notNull<Int>()
    private lateinit var uploadImage: String
    private lateinit var user:UserProfile
    private lateinit var dialogView: View
    private lateinit var selectedDiary: DiaryData
    private var isEmpty by Delegates.notNull<Boolean>()
    companion object {
        const val IMAGE_REQUEST_CODE = 1000
    }

    // 추가하기 겸 수정하기 -> 나
    private fun showAddDialog() {
        (dialogView.parent as? ViewGroup)?.removeView(dialogView)

        val alertDialog = AlertDialog.Builder(requireContext())
        .setView(dialogView)
        .create()

        val imageView = dialogView.findViewById<ImageView>(R.id.editImage)
        val editFeed = dialogView.findViewById<EditText>(R.id.editFeed)

        //기록 없음 -> 저장
        if(isEmpty){
            editFeed.hint = "일기를 작성해보세요!"
            imageView.setImageURI(Uri.parse("android.resource://com.example.common_project01/drawable/empty_image"))
            editFeed.setText("")
        }
        //기록 있음 -> 수정
        else{
            imageView.setImageURI(Uri.parse(selectedDiary.image))
            editFeed.setText(selectedDiary.feed)
        }

        imageView.setOnClickListener {
            val photoPickerIntent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            photoPickerIntent.type = "image/*"
            startActivityForResult(photoPickerIntent, IMAGE_REQUEST_CODE)
        }

        val editBtn = dialogView.findViewById<Button>(R.id.editBtn)
        val deleteBtn = dialogView.findViewById<Button>(R.id.deleteBtn)
        val saveBtn = dialogView.findViewById<Button>(R.id.saveBtn)

        deleteBtn.visibility = View.GONE
        editBtn.visibility = View.GONE
        saveBtn.visibility = View.VISIBLE
        editFeed.isEnabled = true

        saveBtn.setOnClickListener{
            diaryDatabaseHelper.insertOrUpdateDiary(
                user.id,
                selectedDate,
                uploadImage,
                editFeed.text.toString()
            )
//            val bundle = Bundle()
//            bundle.putInt("userPrimaryKey", user.primaryKey) // 전달할 데이터
//            findNavController().navigate(R.id.navigation_home, bundle)
            alertDialog.dismiss()
        }

        alertDialog.show()
    }

    //자세히 보기 -> 나/남
    private fun showMoreDialog() {
        (dialogView.parent as? ViewGroup)?.removeView(dialogView)
        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        val editImage = dialogView.findViewById<ImageView>(R.id.editImage)
        val editFeed = dialogView.findViewById<EditText>(R.id.editFeed)

        val editBtn = dialogView.findViewById<Button>(R.id.editBtn)
        val deleteBtn = dialogView.findViewById<Button>(R.id.deleteBtn)
        val saveBtn = dialogView.findViewById<Button>(R.id.saveBtn)

        editImage.setImageURI(Uri.parse(selectedDiary.image))
        editFeed.setText(selectedDiary.feed)

        editFeed.isEnabled = false

        editBtn.setOnClickListener{
            showAddDialog()
            alertDialog.dismiss()
        }
        deleteBtn.setOnClickListener{
            diaryDatabaseHelper.deleteDiary(selectedDate, user.id)
            editImage.setImageURI(Uri.parse("android.resource://com.example.common_project01/drawable/empty_image"))

//            val bundle = Bundle()
//            bundle.putInt("userPrimaryKey", user.primaryKey) // 전달할 데이터
//            findNavController().navigate(R.id.navigation_home, bundle)
            alertDialog.dismiss()
        }


        //나이면
        if(user.profile){
            editBtn.visibility = View.VISIBLE
            deleteBtn.visibility = View.VISIBLE
            saveBtn.visibility = View.GONE
        }
        //남이면
        else{
            editBtn.visibility = View.GONE
            deleteBtn.visibility = View.GONE
            saveBtn.visibility = View.GONE
        }
        alertDialog.show()
    }

    // 이미지 선택 결과를 처리하는 메서드
    @SuppressLint("SuspiciousIndentation")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var realPath: String? = null

        if (requestCode == IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                realPath = if (Build.VERSION.SDK_INT < 11)
                    data.data?.let { RealPathUtil.getRealPathFromURI_BelowAPI11(requireContext(), it) }
                else if (Build.VERSION.SDK_INT < 19)
                    data.data?.let { RealPathUtil.getRealPathFromURI_API11to18(requireContext(), it) }
                else
                    data.data?.let { RealPathUtil.getRealPathFromURI_API19(requireContext(), it) }
            }
            dialogView.findViewById<ImageView>(R.id.editImage).setImageURI(Uri.parse(realPath.toString()))
            uploadImage = realPath.toString()
        }
    }
    // onCreateView: Fragment의 뷰를 생성할 때 호출하는 메서드
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View{
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root
        dialogView= LayoutInflater.from(requireContext()).inflate(R.layout.dialog_custom, null)
        diaryDatabaseHelper = DatabaseHelper(requireContext())

        // user와 자기자신 초기화
        profile = diaryDatabaseHelper.getProfile()[0]
        userPrimaryKey = arguments?.getInt("userPrimaryKey") ?: profile.primaryKey
        user = diaryDatabaseHelper.getUser(userPrimaryKey)!!

        // 현재 날짜로 초기화
        val calendar = Calendar.getInstance()
        selectedDate = formatDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

        if (userPrimaryKey != 1) {
            binding.gobackImageView.visibility = View.VISIBLE
        }
        binding.gobackImageView.setOnClickListener {
            // profile이 1인 사용자 데이터로 변경
            user = diaryDatabaseHelper.getUser(1)!!
            binding.gobackImageView.visibility = View.INVISIBLE
            // 프래그먼트 새로고침
            val bundle = Bundle()
            bundle.putInt("userPrimaryKey", user.primaryKey) // 전달할 데이터
            findNavController().navigate(R.id.navigation_home, bundle)
        }
        // 초기 날짜로 일기 데이터 로드
        loadDiaryData(selectedDate, user.id)
        val userName = user.name // 동적으로 설정할 사용자 이름
        String.format(resources.getString(R.string.title_home), userName)

        // UI 초기화
        with(binding) {
            addBtn.setOnClickListener{
                showAddDialog()
            }
            moreBtn.setOnClickListener{
                showMoreDialog()
            }

            if((!user.profile)&&(isEmpty)){
                addBtn.visibility = View.GONE
                moreBtn.visibility = View.GONE
            }
            // 남의 계정 + 기록 있음 -> 자세히 보기
            else if((!user.profile)&&(!isEmpty)){
                addBtn.visibility = View.GONE
                moreBtn.visibility = View.VISIBLE
            }
            // 나의 계정 + 기록 없음 -> 추가하기
            else if((user.profile)&&(isEmpty)){
                addBtn.visibility = View.VISIBLE
                moreBtn.visibility = View.GONE
            }
            // 나의 계정 + 기록 있음 -> 자세히보기
            else{
                addBtn.visibility = View.GONE
                moreBtn.visibility = View.VISIBLE
            }

            // CalendarView의 날짜 변경 리스너 설정
            calendarView.setOnDateChangeListener { _, year, month, dayOfMonth ->
                selectedDate = formatDate(year, month, dayOfMonth)
                loadDiaryData(selectedDate, user.id)
                // 남의 계정 + 기록 없음 -> nothing
                if((!user.profile)&&(isEmpty)){
                    addBtn.visibility = View.GONE
                    moreBtn.visibility = View.GONE
                }
                // 남의 계정 + 기록 있음 -> 자세히 보기
                else if((!user.profile)&&(!isEmpty)){
                    addBtn.visibility = View.GONE
                    moreBtn.visibility = View.VISIBLE
                }
                // 나의 계정 + 기록 없음 -> 추가하기
                else if((user.profile)&&(isEmpty)){
                    addBtn.visibility = View.VISIBLE
                    moreBtn.visibility = View.GONE
                }
                // 나의 계정 + 기록 있음 -> 자세히보기
                else{
                    addBtn.visibility = View.GONE
                    moreBtn.visibility = View.VISIBLE
                }
            }

        }

        return view
    }

    private fun formatDate(year: Int, month: Int, day: Int): String {
        return "$year-${month + 1}-$day"
    }

    private fun loadDiaryData(selectedDate:String, userID: String) {
        val diaryData = diaryDatabaseHelper.getDiary(selectedDate,userID)

        if (diaryData != null) {
            val previewText:String = if (diaryData?.feed?.length!! > 50) {
                diaryData.feed.substring(0, 50)+ "..." // 50글자 이상이면 첫 50글자와 줄임표를 추가
            } else {
                diaryData.feed // 50글자 미만이면 전체 문자열 사용
            }

            val previewImage: String = if ((diaryData?.image) == ""){
                "android.resource://com.example.common_project01/drawable/empty_image"
            }else{
                diaryData.image
            }

            binding.previewFeed.text = previewText
            binding.previewImage.setImageURI(Uri.parse(previewImage))

            // 명도를 조정하는 컬러 매트릭스 생성
            val colorMatrix = ColorMatrix().apply {
                setScale(0.5f, 0.5f, 0.5f, 1.0f) // R, G, B 채널의 명도를 낮춤 (0.5는 50% 명도)
            }

            // 컬러 필터 적용
            binding.previewImage.colorFilter = ColorMatrixColorFilter(colorMatrix)

            isEmpty = false
            selectedDiary = diaryData
        }else{
            isEmpty = true
            binding.previewFeed.text = "해당 날짜의 기록이 없어요."
            binding.previewImage.setImageURI(Uri.parse("android.resource://com.example.common_project01/drawable/empty_image"))
        }
    }

    fun refreshFragment(fragment: Fragment, fragmentManager: FragmentManager) {
        val ft = fragmentManager.beginTransaction()
        ft.detach(fragment).attach(fragment).commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
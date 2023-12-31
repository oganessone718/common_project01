package com.example.common_project01.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.content.Context
import android.content.CursorLoader
import android.icu.util.Calendar
import java.util.Date
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
import android.graphics.Typeface
import android.provider.MediaStore
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.view.LayoutInflater
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.common_project01.ui.DiaryData
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.MaterialCalendarView
import com.prolificinteractive.materialcalendarview.spans.DotSpan
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.Collections
import java.util.Locale

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
    fun formatDateString(inputDate: String): String {
        val inputFormat = SimpleDateFormat("yyyy-M-d", Locale.getDefault())
        val outputFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())

        return try {
            val date = inputFormat.parse(inputDate)
            if (date != null) outputFormat.format(date) else inputDate
        } catch (e: Exception) {
            inputDate  // 예외 발생 시 입력된 문자열을 그대로 반환
        }
    }

    // 추가하기 겸 수정하기 -> 나
    private fun showAddDialog() {
        // SimpleDateFormat을 사용하여 문자열 파싱
        (dialogView.parent as? ViewGroup)?.removeView(dialogView)

        val alertDialog = AlertDialog.Builder(requireContext())
        .setView(dialogView)
        .create()

        val background = ContextCompat.getDrawable(requireContext(), R.drawable.dialog_background)
        alertDialog.window?.setBackgroundDrawable(background)

        val dateTitle = dialogView.findViewById<TextView>(R.id.dateTitle)
        dateTitle.text = formatDateString(selectedDate)

        val editImage = dialogView.findViewById<ImageView>(R.id.editImage)
        val detailedFeed = dialogView.findViewById<TextView>(R.id.detailedFeed)
        val editFeed = dialogView.findViewById<EditText>(R.id.editFeed)

        val textView: TextView = dialogView.findViewById(R.id.myTextView)
        textView.visibility = View.VISIBLE
        val maxLength = editFeed.filters.filterIsInstance<InputFilter.LengthFilter>().firstOrNull()?.max ?: 0

        editFeed.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val currentLength = s?.length ?: 0
                textView.text = "$currentLength / $maxLength"
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        detailedFeed.visibility = View.GONE
        editFeed.visibility = View.VISIBLE

        editImage.setOnClickListener {
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
        //기록 없음 -> 저장
        if(isEmpty){
            saveBtn.isEnabled = false
            editImage.setImageURI(Uri.parse("android.resource://com.example.common_project01/drawable/upload_image"))
            editFeed.setText("")
            uploadImage = "android.resource://com.example.common_project01/drawable/empty_image"
            saveBtn.setOnClickListener{
                diaryDatabaseHelper.insertOrUpdateDiary(
                    user.id,
                    selectedDate,
                    uploadImage,
                    editFeed.text.toString()
                )
                val bundle = Bundle()
                bundle.putInt("userPrimaryKey", user.primaryKey) // 전달할 데이터
                bundle.putString("onDate",selectedDate)
                findNavController().navigate(R.id.navigation_home, bundle)
                alertDialog.dismiss()
            }
        }
        //기록 있음 -> 수정
        else{
            uploadImage = selectedDiary.image
            editImage.setImageURI(Uri.parse(selectedDiary.image))
            editFeed.setText(selectedDiary.feed)
            saveBtn.setOnClickListener{
                diaryDatabaseHelper.updateDiary(
                    selectedDiary.id,
                    user.id,
                    selectedDate,
                    uploadImage,
                    editFeed.text.toString()
                )
                val bundle = Bundle()
                bundle.putInt("userPrimaryKey", user.primaryKey) // 전달할 데이터
                bundle.putString("onDate",selectedDate)
                findNavController().navigate(R.id.navigation_home, bundle)
                alertDialog.dismiss()
            }
        }


        alertDialog.show()
    }

    //자세히 보기 -> 나/남
    private fun showMoreDialog() {
        (dialogView.parent as? ViewGroup)?.removeView(dialogView)

        val alertDialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        val dateTitle = dialogView.findViewById<TextView>(R.id.dateTitle)
        dateTitle.text = formatDateString(selectedDate)

        val background = ContextCompat.getDrawable(requireContext(), R.drawable.dialog_background)
        alertDialog.window?.setBackgroundDrawable(background)

        val editImage = dialogView.findViewById<ImageView>(R.id.editImage)
        val detailedFeed = dialogView.findViewById<TextView>(R.id.detailedFeed)
        val editFeed = dialogView.findViewById<EditText>(R.id.editFeed)

        val textView: TextView = dialogView.findViewById(R.id.myTextView)
        textView.visibility = View.GONE

        editFeed.visibility = View.GONE

        val editBtn = dialogView.findViewById<Button>(R.id.editBtn)
        val deleteBtn = dialogView.findViewById<Button>(R.id.deleteBtn)
        val saveBtn = dialogView.findViewById<Button>(R.id.saveBtn)

        editImage.setImageURI(Uri.parse(selectedDiary.image))
        detailedFeed.text = selectedDiary.feed
        if(detailedFeed.text==""){
            detailedFeed.visibility = View.GONE
        }else{
            detailedFeed.visibility = View.VISIBLE
        }

        editBtn.setOnClickListener{
            showAddDialog()
            val bundle = Bundle()
            bundle.putInt("userPrimaryKey", user.primaryKey) // 전달할 데이터
            bundle.putString("onDate",selectedDate)
            findNavController().navigate(R.id.navigation_home, bundle)
            alertDialog.dismiss()
        }
        deleteBtn.setOnClickListener{
            diaryDatabaseHelper.deleteDiary(selectedDate, user.id)
            editImage.setImageURI(Uri.parse("android.resource://com.example.common_project01/drawable/upload_image"))
            val bundle = Bundle()
            bundle.putInt("userPrimaryKey", user.primaryKey) // 전달할 데이터
            bundle.putString("onDate",selectedDate)
            findNavController().navigate(R.id.navigation_home, bundle)
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

            val saveBtn = dialogView.findViewById<Button>(R.id.saveBtn)

            saveBtn.isEnabled = true
        }
    }
    fun stringToDate(str: String): Date? {
        val format = SimpleDateFormat("yyyy-MM-dd")
        return try {
            format.parse(str)
        } catch (e: Exception) {
            e.printStackTrace()
            null
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

        lateinit var calendartoday: MaterialCalendarView

        // user와 자기자신 초기화
        profile = diaryDatabaseHelper.getProfile()[0]
        userPrimaryKey = arguments?.getInt("userPrimaryKey") ?: profile.primaryKey
        user = diaryDatabaseHelper.getUser(userPrimaryKey)!!
        binding.idReveal.text = user.name + "의 하루들"

        // 현재 날짜로 초기화
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) // 주의: month는 0부터 시작합니다.
        val userId = user.id

        // 이미지가 있는 날짜들을 담을 Set을 생성합니다.
        val datesWithImage = HashSet<CalendarDay>()

        // 달의 각 날짜에 대해 반복합니다.
        for (day in 1..31) {
            val selectedDate = formatDate(year, month, day)
            val diary = diaryDatabaseHelper.getDiary(selectedDate, userId)

            // 일기에 이미지가 있는 경우, 해당 날짜를 Set에 추가합니다.
            if (diary?.image != null) {
                val date = CalendarDay.from(year, month, day)
                datesWithImage.add(date)
            }
        }

        calendartoday = view.findViewById(R.id.mcalendarView)

        // 이미지가 있는 날짜에만 EventDecorator 적용
        if (datesWithImage.isNotEmpty()) {
            calendartoday.addDecorator(EventDecorator(datesWithImage))
        }

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
        selectedDate = arguments?.getString("onDate") ?: formatDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH))

        val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = format.parse(selectedDate) // Date 객체

        // Date 객체를 Calendar 객체로 변환
        if (date != null) {
            calendar.time = date
        }

        // 초기 날짜로 일기 데이터 로드
        loadDiaryData(selectedDate, user.id)
        // 명도를 조정하는 컬러 매트릭스 생성

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
            mcalendarView.setOnDateChangedListener { widget, date, selected ->
                // 선택된 날짜를 사용합니다.
                val year = date.year
                val month = date.month // 주의: month는 0부터 시작합니다.
                val dayOfMonth = date.day

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
                diaryData.feed.substring(0, 50) + "..." // 50글자 이상이면 첫 50글자와 줄임표를 추가
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
            binding.previewImage.colorFilter = null
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
class TodayDecorator: DayViewDecorator {
    private var date = CalendarDay.today()

    override fun shouldDecorate(day: CalendarDay?): Boolean {
        return day?.equals(date)!!
    }

    override fun decorate(view: DayViewFacade?) {
        view?.addSpan(StyleSpan(Typeface.BOLD))
        view?.addSpan(RelativeSizeSpan(1.4f))
        view?.addSpan(ForegroundColorSpan(Color.parseColor("#FFFFFF")))
    }
}
class EventDecorator(dates: Collection<CalendarDay>): DayViewDecorator {

    var dates: HashSet<CalendarDay> = HashSet(dates)

    override fun shouldDecorate(day: CalendarDay?): Boolean {
        return dates.contains(day)
    }

    override fun decorate(view: DayViewFacade?) {
        view?.addSpan(DotSpan(5F, Color.parseColor("#FFAAAA")))
    }
}
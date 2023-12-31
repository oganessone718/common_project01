package com.example.common_project01.ui.friends
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityCompat.startActivityForResult
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.common_project01.R
import com.example.common_project01.databinding.FragmentFriendListBinding
import com.example.common_project01.databinding.FragmentHomeBinding
import com.example.common_project01.ui.DatabaseHelper
import com.example.common_project01.ui.home.HomeFragment

//import kotlinx.android.synthetic.main.fragment_profile_edit.*
import android.annotation.SuppressLint
import android.content.Context
import android.content.CursorLoader
import android.database.Cursor
import android.net.Uri
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.os.Build

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

class ProfileEditFragment : Fragment() {

    private lateinit var editView: ImageView
    private lateinit var updatedImage: String

    //    private var _binding: FragmentFriendListBinding? = null
//    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile_edit, container, false)
    }
    private fun pickImageFromGallery() {
        val permission = Manifest.permission.READ_EXTERNAL_STORAGE
        val granted = PackageManager.PERMISSION_GRANTED
        if (ContextCompat.checkSelfPermission(requireContext(), permission) != granted) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(permission),
                IMAGE_REQUEST_CODE
            )
        } else {
            // 권한이 이미 부여되어 있는 경우 갤러리 열기
            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT )
            intent.type = "image/*"
            startActivityForResult(intent, IMAGE_REQUEST_CODE)
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == IMAGE_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 권한이 승인된 경우 갤러리 열기
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT )
                intent.type = "image/*"
                startActivityForResult(intent, IMAGE_REQUEST_CODE)
            } else {
                // 권한이 거부된 경우 사용자에게 설명을 제공하거나 다른 조치를 취할 수 있습니다.
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var realPath: String? = null

        if (requestCode == IMAGE_REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val imageUri: Uri = data.data!!

            if (Build.VERSION.SDK_INT < 11)
                realPath = data.data?.let { RealPathUtil.getRealPathFromURI_BelowAPI11(requireContext(), it) }
            else if (Build.VERSION.SDK_INT < 19)
                realPath = data.data?.let { RealPathUtil.getRealPathFromURI_API11to18(requireContext(), it) }
            else
                realPath = data.data?.let { RealPathUtil.getRealPathFromURI_API19(requireContext(), it) }
            editView.setImageURI(Uri.parse(realPath))
            updatedImage = realPath.toString()
        }
    }

    companion object {
        const val IMAGE_REQUEST_CODE = 1000
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val dbHelper = DatabaseHelper(requireContext())

        val saveButton = view.findViewById<View>(R.id.save_button)

        val myProfile = dbHelper.getProfile()

        val editName = view.findViewById<EditText>(R.id.edit_name)
        val editId = view.findViewById<EditText>(R.id.edit_id)
        val editIntro = view.findViewById<EditText>(R.id.edit_intro)
        editView = view.findViewById<ImageView>(R.id.edit_image)

        var myPrimaryId = -1

        if(myProfile.size!=1) {
            print("error")
            editId.setText(myProfile.size.toString()) // error 확인용
        }
        else {
            myPrimaryId = myProfile[0].primaryKey
            editName.setText(myProfile[0].name)
            editId.setText(myProfile[0].id)
            editIntro.setText(myProfile[0].intro)
            if(myProfile[0].image==="tmp"){
                editView.setImageResource(R.drawable.ic_launcher_background) //임시..
            }else{
                editView.setImageURI(Uri.parse(myProfile[0].image))
            }
        }

        val clickListener = View.OnClickListener { pickImageFromGallery() }


        editView.setOnClickListener(
            clickListener
        )
        // 저장 버튼 클릭 이벤트 처리
        saveButton.setOnClickListener {
            // 입력된 정보 가져오기
            val updatedName = editName.text.toString()
            val updatedId = editId.text.toString()
            val updatedIntro = editIntro.text.toString()
            dbHelper.updateProfile(myPrimaryId, updatedName, updatedId, updatedIntro, updatedImage)
        }
    }

}


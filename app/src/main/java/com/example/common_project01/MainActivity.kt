package com.example.common_project01

import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.common_project01.databinding.ActivityMainBinding
import com.example.common_project01.ui.DatabaseHelper
import com.example.common_project01.ui.DiaryData
import com.example.common_project01.ui.UserProfile
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class MainActivity : AppCompatActivity() {

    val MY_PERMISSION_ACCESS_ALL = 100

    private lateinit var binding: ActivityMainBinding
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment_activity_main).navigateUp() || super.onSupportNavigateUp()
    }

    private fun readJsonFile(context: Context, fileName: String): List<UserProfile> {
        val jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }

        val gson = Gson()
        val userListType = object : TypeToken<List<UserProfile>>() {}.type
        return gson.fromJson(jsonString, userListType)
    }
    private fun readdiaryJsonFile(context: Context, fileName: String): List<DiaryData> {
        val jsonString = context.assets.open(fileName).bufferedReader().use { it.readText() }

        val gson = Gson()
        val userListType = object : TypeToken<List<DiaryData>>() {}.type
        return gson.fromJson(jsonString, userListType)
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


        val firstUserList  = readJsonFile(this, "users.json")
        val diarytmpList  = readdiaryJsonFile(this, "diarytmp.json")

        val dbHelper = DatabaseHelper(this)
        if (dbHelper.getUserCount()==0){
            dbHelper.addAllProfilesToDatabase(firstUserList)
            dbHelper.addAlldiaryToDatabase(diarytmpList)
        }
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(setOf(
                R.id.navigation_home, R.id.navigation_feed, R.id.navigation_friends))

        // 권한 요청
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            var permissions = arrayOf(
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            )
            ActivityCompat.requestPermissions(this, permissions, MY_PERMISSION_ACCESS_ALL)
        }

    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode === MY_PERMISSION_ACCESS_ALL) {
            if (grantResults.size > 0) {
                for (grant in grantResults) {
                    if (grant != PackageManager.PERMISSION_GRANTED) System.exit(0)
                }
            }
        }
    }
}
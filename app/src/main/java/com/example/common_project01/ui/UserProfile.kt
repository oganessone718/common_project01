package com.example.common_project01.ui


import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


data class UserProfile(
    val primaryId: Int,            // 유저 ID (고유 식별자)
    val name: String,       // 이름
    val id: String,   // 사용자 이름 또는 아이디
    val intro: String,        // 자기 소개
    val profileImage: String // 프로필 이미지 경로 또는 URL
)

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "UserProfileDatabase.db"
        private const val DATABASE_VERSION = 1
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(
            "CREATE TABLE user_profiles (" +
                    "primaryId INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT," +
                    "id TEXT," +
                    "intro TEXT," +
                    "profile_image TEXT" +
                    ");"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        // 데이터베이스 업그레이드 로직 추가 (필요한 경우)
    }
    fun addProfile(profile: UserProfile) {
        val db = writableDatabase
        val values = ContentValues()
        values.put("name", profile.name)
        values.put("intro", profile.intro)
        values.put("image", profile.image)
        val newRowId = db.insert("user_profiles", null, values)
        db.close()
    }

    fun updateProfile(profile: UserProfile) {
        val db = writableDatabase
        val values = ContentValues()
        values.put("name", profile.name)
        values.put("intro", profile.intro)
        values.put("image", profile.image)
        db.update("user_profiles", values, "id = ?", arrayOf(profile.id.toString()))
        db.close()
    }


}

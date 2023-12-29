package com.example.common_project01.ui


import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper


data class UserProfile(
    val primaryId: Int,            // 유저 ID (고유 식별자)
    val name: String,       // 이름
    val id: String,   // 사용자 이름 또는 아이디
    val intro: String,        // 자기 소개
    val image: String, // 프로필 이미지 경로 또는 URL
    val profile: Boolean // 프로필 or 친구목록
)

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "UserProfileDatabase.db"
        private const val DATABASE_VERSION = 2
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(
            "CREATE TABLE user_profiles (" +
                    "primaryId INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "name TEXT," +
                    "id TEXT," +
                    "intro TEXT," +
                    "image TEXT," +
                    "profile BOOLEAN" +
                    ");"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            // 버전 1에서 버전 2로 업그레이드하는 경우
            db?.execSQL("ALTER TABLE user_profiles ADD COLUMN image TEXT;")
        }
    }


    fun addProfile(profile: UserProfile) {
        val db = writableDatabase
        val values = ContentValues()
        values.put("primaryId", profile.primaryId)
        values.put("name", profile.name)
        values.put("id", profile.id)
        values.put("intro", profile.intro)
        values.put("image", profile.image)
        values.put("profile", profile.profile)
        db.insert("user_profiles", null, values)
//        db.close()
    }

    fun updateProfile(profile: UserProfile) {
        val db = writableDatabase
        val values = ContentValues()
        values.put("primaryId", profile.primaryId)
        values.put("name", profile.name)
        values.put("id", profile.id)
        values.put("intro", profile.intro)
        values.put("image", profile.image)
        values.put("profile", profile.profile)
        db.update("user_profiles", values, "primaryId = ?", arrayOf(profile.primaryId.toString()))
//        db.close()
    }

    // DatabaseHelper.kt


    private val TABLE_NAME = "user_profiles"

    // 유저 목록 가져오기
    @SuppressLint("Range")
    fun getUsers(): List<UserProfile> {
        val userList = mutableListOf<UserProfile>()
        val db = this.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)

        while (cursor.moveToNext()) {
            val primaryId = cursor.getInt(cursor.getColumnIndex("primaryId"))
            val name = cursor.getString(cursor.getColumnIndex("name"))
            val id = cursor.getString(cursor.getColumnIndex("id"))
            val intro = cursor.getString(cursor.getColumnIndex("intro"))
            val image = cursor.getString(cursor.getColumnIndex("image"))
//            val profile = cursor.getString(cursor.getColumnIndex(profile))
            val profile = false

            val userProfile = UserProfile(primaryId, name, id, intro, image, profile)
            userList.add(userProfile)
        }

        cursor.close()
//        db.close()

        return userList
    }


}

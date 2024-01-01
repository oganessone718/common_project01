package com.example.common_project01.ui

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import android.icu.util.Calendar


data class UserProfile(
    val primaryKey: Int, // PK ^^
    val name: String, // 이름
    val id: String, // 유저 ID (고유 식별자)
    val intro: String, // 자기 소개
    val image: String, // 프로필 이미지 경로 또는 URL
    val profile: Boolean // 프로필 or 친구목록
)

data class DiaryData(
    val userId: String,
    val date: String,
    val image: String,
    val feed: String
)

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "Database.db"
        private const val DATABASE_VERSION = 2
    }
    private var currentYear: Int = 0
    private var currentMonth: Int = 0
    private var currentDay: Int = 0

    private fun formatDate(year: Int, month: Int, day: Int): String {
        return "$year-${month + 1}-$day"
    }

    fun getUserCount(): Int {
        val db = this.readableDatabase
        val query = "SELECT COUNT(*) FROM user_profiles"
        val cursor = db.rawQuery(query, null)
        var userCount = 0

        if (cursor.moveToFirst()) {
            userCount = cursor.getInt(0)
        }

        cursor.close()
        return userCount
    }

    @SuppressLint("Range")
    fun getProfileUserId(): String? {
        val db = this.readableDatabase
        val cursor = db.query(
            "user_profiles", // 테이블 이름
            arrayOf("id"), // 가져올 컬럼
            "profile = ?", // 조건
            arrayOf("1"), // 조건에 맞는 값
            null,
            null,
            null
        )

        var userId: String? = null
        if (cursor.moveToFirst()) {
            userId = cursor.getString(cursor.getColumnIndex("id"))
        }
        cursor.close()
        return userId
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(
            "CREATE TABLE user_profiles (" +
                    "primaryKey INTEGER PRIMARY KEY AUTOINCREMENT,"+
                    "id TEXT," +
                    "name TEXT," +
                    "intro TEXT," +
                    "image TEXT," +
                    "profile BOOLEAN" +
                    ");"
        )
        db?.execSQL(
            "CREATE TABLE DiaryData (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT,"+
                    "userId TEXT," +
                    "date TEXT," +
                    "image TEXT," +
                    "feed TEXT" +
                    ");"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS tableName");
        onCreate(db);
    }


    fun addProfile(newName: String, newId: String, newIntro: String, newImage: String, newProfile: Boolean) {
        val db = writableDatabase
        val values = ContentValues()

        values.put("name", newName)
        values.put("id", newId)
        values.put("intro", newIntro)
        values.put("image", newImage)
        values.put("profile", newProfile)
        db.insert("user_profiles", null, values)
        db.close()
    }
    fun addAllProfilesToDatabase(userList: List<UserProfile>) {
        userList.forEach { userProfile ->
            this.addProfile(
                newName = userProfile.name,
                newId = userProfile.id,
                newIntro = userProfile.intro,
                newImage = userProfile.image,
                newProfile = userProfile.profile
            )
        }
    }
    fun updateProfile(primaryKey: Int, updatedName:String, updatedId:String, updatedIntro:String, updatedImage: String) {
        val db = writableDatabase
        val values = ContentValues()

        values.put("name", updatedName)
        values.put("id", updatedId)
        values.put("intro", updatedIntro)
        values.put("image", updatedImage)
        values.put("profile", true)
        db.update("user_profiles", values, "primaryKey = ?", arrayOf(primaryKey.toString()))
        db.close()
    }

    // 유저 목록 가져오기
    @SuppressLint("Range")
    fun getUsers(): List<UserProfile> {
        val userList = mutableListOf<UserProfile>()
        val db = this.readableDatabase

        val cursor = db.rawQuery("SELECT * FROM user_profiles", null)

        while (cursor.moveToNext()) {
            val primaryKey = cursor.getInt(cursor.getColumnIndex("primaryKey"))
            val name = cursor.getString(cursor.getColumnIndex("name"))
            val id = cursor.getString(cursor.getColumnIndex("id"))
            val intro = cursor.getString(cursor.getColumnIndex("intro"))
            val image = cursor.getString(cursor.getColumnIndex("image"))
            val profileInt = cursor.getInt(cursor.getColumnIndex("profile"))
            val profile = profileInt == 1

            val userProfile = UserProfile(primaryKey, name, id, intro, image, profile)
            userList.add(userProfile)
        }

        cursor.close()
        db.close()

        return userList
    }

    @SuppressLint("Range")
    fun getUser(userPrimaryKey:Int): UserProfile? {
        val userList = mutableListOf<UserProfile>()
        val db = this.readableDatabase

        val cursor = db.query(
            "user_profiles",
            arrayOf("id", "name", "intro","image","profile"),
            "primaryKey=?",
            arrayOf(userPrimaryKey.toString()),
            null, null, null, null
        )

        var user:UserProfile?= null

        if (cursor != null && cursor.moveToFirst()) {
            user = UserProfile(
                primaryKey = userPrimaryKey,
                id = cursor.getString(cursor.getColumnIndex("id")),
                name= cursor.getString(cursor.getColumnIndex("name")),
                intro = cursor.getString(cursor.getColumnIndex("intro")),
                image = cursor.getString(cursor.getColumnIndex("intro")),
                profile = cursor.getInt(cursor.getColumnIndex("profile"))==1,
            )
        }

        cursor.close()
        db.close()

        return user
    }

    @SuppressLint("Range")
    fun getProfile(): List<UserProfile> {
        val userList = mutableListOf<UserProfile>()
        val db = this.readableDatabase

        // profile 컬럼이 true인 row만 가져옴
        val cursor = db.rawQuery("SELECT * FROM user_profiles WHERE profile = 1", null)

        while (cursor.moveToNext()) {
            val primaryKey = cursor.getInt(cursor.getColumnIndex("primaryKey"))
            val name = cursor.getString(cursor.getColumnIndex("name"))
            val id = cursor.getString(cursor.getColumnIndex("id"))
            val intro = cursor.getString(cursor.getColumnIndex("intro"))
            val image = cursor.getString(cursor.getColumnIndex("image"))
            val profile = cursor.getInt(cursor.getColumnIndex("profile")) == 1 // 1일 때 true, 0일 때 false

            val userProfile = UserProfile(primaryKey, name, id, intro, image, profile)
            userList.add(userProfile)
        }

        cursor.close()
        return userList
    }

    fun insertOrUpdateDiary(userId: String, date: String, image: String, feed: String) {
        val db = writableDatabase
        val contentValues = ContentValues()
        contentValues.put("userId", userId)
        contentValues.put("date", date)
        contentValues.put("image", image)
        contentValues.put("feed", feed)
        db.insertWithOnConflict("DiaryData", null, contentValues, SQLiteDatabase.CONFLICT_REPLACE)
        db.close()
    }

    @SuppressLint("Range")
    fun getDiary(date: String, searchUserId:String): DiaryData? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM DiaryData WHERE date = ? AND userId = ? ORDER BY id DESC", arrayOf(date,searchUserId)) // index 큰 순서로 정렬하여 최신 수정본 load
        var diaryData: DiaryData? = null
        Log.d("myTag",searchUserId+"getget")

        if (cursor.moveToFirst()) {
            val userId = cursor.getString(cursor.getColumnIndex("userId"))
            val image = cursor.getString(cursor.getColumnIndex("image"))
            val feed = cursor.getString(cursor.getColumnIndex("feed"))
            diaryData = DiaryData(userId, date, image, feed)
        }
        cursor.close()
        db.close()
        return diaryData
    }

    fun deleteDiary(date: String) {
        val db = this.writableDatabase
        db.delete("DiaryData", "date = ?", arrayOf(date))
        db.close()
    }

    @SuppressLint("Range")
    fun getDiaryData(): List<DiaryData> {

        val calendar = Calendar.getInstance()
        currentYear = calendar.get(Calendar.YEAR)
        currentMonth = calendar.get(Calendar.MONTH)
        currentDay = calendar.get(Calendar.DAY_OF_MONTH)

        val date = formatDate(currentYear, currentMonth, currentDay)

        val diaryList = mutableListOf<DiaryData>()
        val db = this.readableDatabase

        // 오늘 날짜에 해당하는 데이터만 조회
        val cursor = db.rawQuery("SELECT * FROM DiaryData WHERE date = ? ORDER BY id DESC", arrayOf(date))

        while (cursor.moveToNext()) {
            val userId = cursor.getString(cursor.getColumnIndex("userId"))
            val date = cursor.getString(cursor.getColumnIndex("date"))
            val image = cursor.getString(cursor.getColumnIndex("image"))
            val feed = cursor.getString(cursor.getColumnIndex("feed"))

            diaryList.add(DiaryData(userId, date, image, feed))
        }
        cursor.close()
        db.close()

        return diaryList
    }

}

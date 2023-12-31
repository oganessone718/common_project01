package com.example.common_project01.ui

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.icu.util.Calendar
import android.util.Log


data class UserProfile(
    val primaryKey: Int, // PK ^^
    val name: String, // 이름
    val id: String, // 유저 ID (고유 식별자)
    val intro: String, // 자기 소개
    val image: String, // 프로필 이미지 경로 또는 URL
    val profile: Boolean // 프로필 or 친구목록
)

data class DiaryData(
    val id: Int, // PK ^^
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
                    "id TEXT UNIQUE," +
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
                    "feed TEXT," +
                    "FOREIGN KEY (userId) REFERENCES user_profiles(id) ON UPDATE CASCADE" +
                    ");"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS tableName");
        onCreate(db);
    }

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.setForeignKeyConstraintsEnabled(true) // 외래 키 제약 조건 활성화
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

    fun addAlldiaryToDatabase(diaryList: List<DiaryData>) {
        Log.d("okokok", "dbquery-alldiarytodatabase")
        Log.d("okokok", "Diary list size: ${diaryList.size}")

        if (diaryList.isNotEmpty()) {
            try {
                val firstDiary = diaryList[0]
                Log.d("okokok", "First diary userId: ${firstDiary.userId}")
                Log.d("okokok", "First diary date: ${firstDiary.date}")
            } catch (e: Exception) {
                Log.e("okokok", "Error accessing first diary data: ${e.message}")
            }
        }

        diaryList.forEach { diaryData ->
            try {
                Log.d("okokok", "Processing diary for user: ${diaryData.userId}")
                this.insertOrUpdateDiary(
                    userId = diaryData.userId,
                    date = diaryData.date,
                    image = diaryData.image,
                    feed = diaryData.feed
                )
            } catch (e: Exception) {
                Log.e("okokok", "Error processing diary data for user: ${diaryData.userId}, Error: ${e.message}")
            }
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

    fun updateDiary(primaryKey: Int, updatedUserId: String, updatedDate: String, updatedImage: String, updatedFeed: String) {
        val db = writableDatabase
        val values = ContentValues()

        values.put("userId", updatedUserId)
        values.put("date", updatedDate)
        values.put("image", updatedImage)
        values.put("feed", updatedFeed)
        db.update("DiaryData", values, "id = ?", arrayOf(primaryKey.toString()))
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

    fun getUserProfileImage(userId: String): String {
        // 데이터베이스에 연결
        val db = this.readableDatabase

        // 쿼리를 실행하여 사용자 프로필 이미지 가져오기
        val cursor = db.query(
            "user_profiles", // 테이블 이름
            arrayOf("profileImage"), // 가져올 컬럼
            "userId = ?", // 조건
            arrayOf(userId), // 조건에 대한 값
            null,
            null,
            null
        )

        var imageUrl = ""
        if (cursor.moveToFirst()) {
            imageUrl = cursor.getString(cursor.getColumnIndexOrThrow("profileImage"))
        }

        cursor.close()
        db.close()

        return imageUrl
    }

    fun insertOrUpdateDiary(userId: String, date: String, image: String, feed: String) {
        val db = writableDatabase
        val contentValues = ContentValues()
        Log.d("okokok",userId)
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
        if (cursor.moveToFirst()) {
            val id = cursor.getInt(cursor.getColumnIndex("id"))
            val userId = cursor.getString(cursor.getColumnIndex("userId"))
            val image = cursor.getString(cursor.getColumnIndex("image"))
            val feed = cursor.getString(cursor.getColumnIndex("feed"))
            diaryData = DiaryData(id, userId, date, image, feed)
        }
        cursor.close()
        db.close()
        return diaryData
    }

    // 수정하고싶은 욕심.. primarykey 입력하는 편이 DB적인 측면에서 더 낫지 않나... 시간상 참는다.
    fun deleteDiary(date: String, userId: String) {
        val db = this.writableDatabase
        db.delete("DiaryData", "date = ? AND userID = ?", arrayOf(date,userId))
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
            val id = cursor.getInt(cursor.getColumnIndex("id"))
            val userId = cursor.getString(cursor.getColumnIndex("userId"))
            val date = cursor.getString(cursor.getColumnIndex("date"))
            val image = cursor.getString(cursor.getColumnIndex("image"))
            val feed = cursor.getString(cursor.getColumnIndex("feed"))

            diaryList.add(DiaryData(id, userId, date, image, feed))
        }
        cursor.close()
        db.close()

        return diaryList
    }

}

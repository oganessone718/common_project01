package com.example.common_project01.ui

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

data class UserProfile(
    val primaryKey: Int, // PK ^^
    val name: String, // 이름
    val id: String, // 유저 ID (고유 식별자)
    val intro: String, // 자기 소개
    val image: String, // 프로필 이미지 경로 또는 URL
    val profile: Boolean // 프로필 or 친구목록
)

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "UserProfileDatabase.db"
        private const val DATABASE_VERSION = 1
    }

    private val TABLE_NAME = "user_profiles"

    fun getUserCount(): Int {
        val db = this.readableDatabase
        val query = "SELECT COUNT(*) FROM $TABLE_NAME"
        val cursor = db.rawQuery(query, null)
        var userCount = 0

        if (cursor.moveToFirst()) {
            userCount = cursor.getInt(0)
        }

        cursor.close()
        return userCount
    }

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(
            "CREATE TABLE $TABLE_NAME (" +
                    "primaryKey INTEGER PRIMARY KEY AUTOINCREMENT,"+
                    "id TEXT," +
                    "name TEXT," +
                    "intro TEXT," +
                    "image TEXT," +
                    "profile BOOLEAN" +
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

        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME", null)

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
    fun getProfile(): List<UserProfile> {
        val userList = mutableListOf<UserProfile>()
        val db = this.readableDatabase

        // profile 컬럼이 true인 row만 가져옴
        val cursor = db.rawQuery("SELECT * FROM $TABLE_NAME WHERE profile = 1", null)

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

}

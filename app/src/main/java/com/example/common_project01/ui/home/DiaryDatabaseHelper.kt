package com.example.common_project01.ui.home

import android.annotation.SuppressLint
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues

class DiaryDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    override fun onCreate(db: SQLiteDatabase) {
        val createTableStatement = """
            CREATE TABLE DiaryData (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                userId TEXT,
                date TEXT,
                image TEXT,
                feed TEXT
            )
        """.trimIndent()
        db.execSQL(createTableStatement)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS tableName");
        if (db != null) {
            onCreate(db)
        };
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
    fun getDiary(date: String): DiaryData? {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM DiaryData WHERE date = ? ORDER BY id DESC", arrayOf(date)) // index 큰 순서로 정렬하여 최신 수정본 load
        var diaryData: DiaryData? = null

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

    companion object {
        private const val DATABASE_NAME = "DiaryDatabase.db"
        private const val DATABASE_VERSION = 1
    }
}
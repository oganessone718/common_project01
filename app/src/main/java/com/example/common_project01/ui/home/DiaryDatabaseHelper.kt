package com.example.common_project01.ui.home

class DiaryDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    // onCreate, onUpgrade 등 구현...

    fun insertOrUpdateDiary(userId: String, date: String, image: String, feed: String) {
        // 데이터베이스에 데이터 삽입 또는 업데이트
    }

    fun getDiary(date: String): DiaryData? {
        // 데이터베이스에서 해당 날짜의 데이터 조회
    }

    fun deleteDiary(date: String) {
        // 데이터베이스에서 해당 날짜의 데이터 삭제
    }
}
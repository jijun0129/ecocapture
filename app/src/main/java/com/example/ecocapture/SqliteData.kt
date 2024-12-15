package com.example.ecocapture

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.graphics.Bitmap
import android.widget.Toast
import java.io.ByteArrayOutputStream

class MyDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "EcoCapture.db"  // 데이터베이스 이름
        private const val DATABASE_VERSION = 1  // 데이터베이스 버전
        private const val TABLE_NAME = "search_results"  // 테이블 이름
        private const val COLUMN_ID = "id"  // 컬럼 이름
        private const val COLUMN_IMAGE = "image"  // 이미지 컬럼
        private const val COLUMN_SEARCH_TEXT = "search_text"  // 검색 텍스트 컬럼
        private const val COLUMN_RESULT_TEXT = "result_text"  // 결과 텍스트 컬럼
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_NAME (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_IMAGE BLOB,
                $COLUMN_SEARCH_TEXT TEXT,
                $COLUMN_RESULT_TEXT TEXT
            )
        """
        db.execSQL(createTableQuery)
    }

    fun insertResult(image: ByteArray?, searchText: String, resultText: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_IMAGE, image)
            put(COLUMN_SEARCH_TEXT, searchText)
            put(COLUMN_RESULT_TEXT, resultText)
        }
        db.insert(TABLE_NAME, null, values)
        db.close()
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun getAllResults(): List<Triple<ByteArray?, String, String>> {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_NAME,
            arrayOf(COLUMN_IMAGE, COLUMN_SEARCH_TEXT, COLUMN_RESULT_TEXT),
            null, null, null, null, "$COLUMN_ID DESC", "20"
        )

        val results = mutableListOf<Triple<ByteArray?, String, String>>()
        with(cursor) {
            while (moveToNext()) {
                val image = getBlob(getColumnIndexOrThrow(COLUMN_IMAGE))
                val searchText = getString(getColumnIndexOrThrow(COLUMN_SEARCH_TEXT)) ?: "N/A"
                val resultText = getString(getColumnIndexOrThrow(COLUMN_RESULT_TEXT))
                results.add(Triple(image, searchText, resultText))
            }
            close()
        }
        return results
    }

}

package com.example.laba8

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.laba8.Tag


class TagRepository(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    fun getAllTags(): List<Tag> {
        val tags = mutableListOf<Tag>()
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_TAGS,
            null, null, null, null, null,
            DatabaseHelper.COLUMN_TAG_NAME
        )

        with(cursor) {
            while (moveToNext()) {
                val tag = Tag(
                    id = getLong(getColumnIndexOrThrow(DatabaseHelper.COLUMN_TAG_ID)),
                    name = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_TAG_NAME)),
                )
                tags.add(tag)
            }
        }
        cursor.close()
        return tags
    }

    fun getTagById(id: Long): Tag? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_TAGS,
            null,
            "${DatabaseHelper.COLUMN_TAG_ID} = ?",
            arrayOf(id.toString()),
            null, null, null
        )

        return if (cursor.moveToFirst()) {
            val tag = Tag(
                id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TAG_ID)),
                name = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TAG_NAME)),
            )
            cursor.close()
            tag
        } else {
            cursor.close()
            null
        }
    }

    fun updateTag(tag: Tag): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_TAG_NAME, tag.name)
        }

        val rowsAffected = db.update(
            DatabaseHelper.TABLE_TAGS,
            values,
            "${DatabaseHelper.COLUMN_TAG_ID} = ?",
            arrayOf(tag.id.toString())
        )

        return rowsAffected > 0
    }

    fun deleteTag(id: Long): Boolean {
        val db = dbHelper.writableDatabase
        val rowsAffected = db.delete(
            DatabaseHelper.TABLE_TAGS,
            "${DatabaseHelper.COLUMN_TAG_ID} = ?",
            arrayOf(id.toString())
        )
        return rowsAffected > 0
    }
    fun insertTag(tag: Tag): Boolean {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_TAG_NAME, tag.name)
        }

        val newId = db.insert(DatabaseHelper.TABLE_TAGS, null, values)
        return newId != -1L
    }
    fun close() {
        dbHelper.close()
    }
}

































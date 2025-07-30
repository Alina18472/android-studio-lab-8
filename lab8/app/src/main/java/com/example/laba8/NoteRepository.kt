package com.example.laba8

import android.content.ContentValues
import android.content.Context
import com.example.laba8.DatabaseHelper.Companion.COLUMN_CONTENT
import com.example.laba8.DatabaseHelper.Companion.COLUMN_CREATED_AT
import com.example.laba8.DatabaseHelper.Companion.COLUMN_NOTE_ID
import com.example.laba8.DatabaseHelper.Companion.COLUMN_TAG_ID_FK
import com.example.laba8.DatabaseHelper.Companion.COLUMN_TITLE
import com.example.laba8.DatabaseHelper.Companion.TABLE_NOTES
import com.example.laba8.DatabaseHelper.Companion.TABLE_NOTE_TAGS
import com.example.laba8.Note

class NoteRepository(context: Context) {
    private val dbHelper = DatabaseHelper(context)

    fun addNote(note: Note): Long {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_CREATED_AT, note.createdAt)
            put(COLUMN_TITLE, note.title)
            put(COLUMN_CONTENT, note.content)
        }


        val noteId = db.insert(TABLE_NOTES, null, values)


        if (noteId != -1L) {
            addTagsToNote(noteId, note.tags)
        }

        return noteId
    }

    private fun addTagsToNote(noteId: Long, tags: List<String>) {
        val db = dbHelper.writableDatabase

        tags.forEach { tagName ->

            val tagId = getTagIdByName(tagName)
            if (tagId != -1L) {
                val values = ContentValues().apply {
                    put(COLUMN_NOTE_ID, noteId)
                    put(COLUMN_TAG_ID_FK, tagId)
                }
                db.insert(TABLE_NOTE_TAGS, null, values)
            }
        }
    }

    fun getAllNotes(): List<Note> {
        val notes = mutableListOf<Note>()
        val db = dbHelper.readableDatabase

        val cursor = db.query(
            DatabaseHelper.TABLE_NOTES,
            null, null, null, null, null,
            "${DatabaseHelper.COLUMN_CREATED_AT} DESC"
        )

        with(cursor) {
            while (moveToNext()) {
                val noteId = getLong(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID))
                val note = Note(
                    id = noteId,
                    createdAt = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_CREATED_AT)),
                    title = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_TITLE)),
                    content = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_CONTENT)),
                    tags = getTagsForNote(noteId)
                )
                notes.add(note)
            }
        }
        cursor.close()
        return notes
    }

    fun close() {
        dbHelper.close()
    }
    fun getNoteById(id: Long): Note? {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_NOTES,
            null,
            "${DatabaseHelper.COLUMN_ID} = ?",
            arrayOf(id.toString()),
            null, null, null
        )

        return if (cursor.moveToFirst()) {
            val note = Note(
                id = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)),
                createdAt = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CREATED_AT)),
                title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TITLE)),
                content = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_CONTENT)),
                tags = getTagsForNote(cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID)))
            )
            cursor.close()
            note
        } else {
            cursor.close()
            null
        }
    }

    fun updateNote(note: Note): Int {
        val db = dbHelper.writableDatabase
        val values = ContentValues().apply {
            put(DatabaseHelper.COLUMN_CREATED_AT, note.createdAt)
            put(DatabaseHelper.COLUMN_TITLE, note.title)
            put(DatabaseHelper.COLUMN_CONTENT, note.content)
        }

        updateNoteTags(note.id, note.tags)

        return db.update(
            DatabaseHelper.TABLE_NOTES,
            values,
            "${DatabaseHelper.COLUMN_ID} = ?",
            arrayOf(note.id.toString())
        )
    }

    fun deleteNote(id: Long): Int {
        val db = dbHelper.writableDatabase

        db.delete(
            DatabaseHelper.TABLE_NOTE_TAGS,
            "${DatabaseHelper.COLUMN_NOTE_ID} = ?",
            arrayOf(id.toString())
        )

        return db.delete(
            DatabaseHelper.TABLE_NOTES,
            "${DatabaseHelper.COLUMN_ID} = ?",
            arrayOf(id.toString())
        )
    }

    private fun getTagsForNote(noteId: Long): List<String> {
        val tags = mutableListOf<String>()
        val db = dbHelper.readableDatabase

        val query = """
        SELECT ${DatabaseHelper.COLUMN_TAG_NAME} 
        FROM ${DatabaseHelper.TABLE_TAGS} 
        INNER JOIN ${DatabaseHelper.TABLE_NOTE_TAGS} 
        ON ${DatabaseHelper.TABLE_TAGS}.${DatabaseHelper.COLUMN_TAG_ID} = 
           ${DatabaseHelper.TABLE_NOTE_TAGS}.${DatabaseHelper.COLUMN_TAG_ID_FK}
        WHERE ${DatabaseHelper.TABLE_NOTE_TAGS}.${DatabaseHelper.COLUMN_NOTE_ID} = ?
    """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(noteId.toString()))
        while (cursor.moveToNext()) {
            tags.add(cursor.getString(0))
        }
        cursor.close()
        return tags
    }

    private fun updateNoteTags(noteId: Long, tags: List<String>) {
        val db = dbHelper.writableDatabase


        db.delete(
            DatabaseHelper.TABLE_NOTE_TAGS,
            "${DatabaseHelper.COLUMN_NOTE_ID} = ?",
            arrayOf(noteId.toString())
        )


        tags.forEach { tagName ->
            val tagId = getTagIdByName(tagName)
            if (tagId != -1L) {
                val values = ContentValues().apply {
                    put(DatabaseHelper.COLUMN_NOTE_ID, noteId)
                    put(DatabaseHelper.COLUMN_TAG_ID_FK, tagId)
                }
                db.insert(DatabaseHelper.TABLE_NOTE_TAGS, null, values)
            }
        }
    }

    private fun getTagIdByName(tagName: String): Long {
        val db = dbHelper.readableDatabase
        val cursor = db.query(
            DatabaseHelper.TABLE_TAGS,
            arrayOf(DatabaseHelper.COLUMN_TAG_ID),
            "${DatabaseHelper.COLUMN_TAG_NAME} = ?",
            arrayOf(tagName),
            null, null, null
        )

        return if (cursor.moveToFirst()) {
            val id = cursor.getLong(0)
            cursor.close()
            id
        } else {
            cursor.close()
            -1
        }
    }
    fun getNotesByTagId(tagId: Long): List<Note> {
        val notes = mutableListOf<Note>()
        val db = dbHelper.readableDatabase

        val query = """
        SELECT n.* 
        FROM ${DatabaseHelper.TABLE_NOTES} n
        INNER JOIN ${DatabaseHelper.TABLE_NOTE_TAGS} nt 
        ON n.${DatabaseHelper.COLUMN_ID} = nt.${DatabaseHelper.COLUMN_NOTE_ID}
        WHERE nt.${DatabaseHelper.COLUMN_TAG_ID_FK} = ?
        ORDER BY n.${DatabaseHelper.COLUMN_CREATED_AT} DESC
    """.trimIndent()

        val cursor = db.rawQuery(query, arrayOf(tagId.toString()))

        with(cursor) {
            while (moveToNext()) {
                val noteId = getLong(getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID))
                val note = Note(
                    id = noteId,
                    createdAt = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_CREATED_AT)),
                    title = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_TITLE)),
                    content = getString(getColumnIndexOrThrow(DatabaseHelper.COLUMN_CONTENT)),
                    tags = getTagsForNote(noteId)
                )
                notes.add(note)
            }
        }
        cursor.close()
        return notes
    }

}
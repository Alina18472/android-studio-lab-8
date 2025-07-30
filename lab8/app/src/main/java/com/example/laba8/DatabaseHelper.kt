package com.example.laba8
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues
class DatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME,
    null,
    DATABASE_VERSION
) {
    companion object {
        private const val DATABASE_NAME = "notes_app.db"
        private const val DATABASE_VERSION = 4


        const val TABLE_NOTES = "notes"
        const val COLUMN_ID = "id"
        const val COLUMN_CREATED_AT = "created_at"
        const val COLUMN_TITLE = "title"
        const val COLUMN_CONTENT = "content"


        const val CREATE_NOTES_TABLE = """
            CREATE TABLE $TABLE_NOTES (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_CREATED_AT TEXT NOT NULL,
                $COLUMN_TITLE TEXT NOT NULL,
                $COLUMN_CONTENT TEXT NOT NULL
            )
        """
        const val TABLE_TAGS = "tags"
        const val COLUMN_TAG_ID = "tag_id"
        const val COLUMN_TAG_NAME = "name"



        const val TABLE_NOTE_TAGS = "note_tags"
        const val COLUMN_NOTE_ID = "note_id"
        const val COLUMN_TAG_ID_FK = "tag_id"


        const val CREATE_TAGS_TABLE = """
            CREATE TABLE $TABLE_TAGS (
                $COLUMN_TAG_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_TAG_NAME TEXT NOT NULL UNIQUE
                
            )
        """


        const val CREATE_NOTE_TAGS_TABLE = """
            CREATE TABLE $TABLE_NOTE_TAGS (
                $COLUMN_NOTE_ID INTEGER,
                $COLUMN_TAG_ID_FK INTEGER,
                PRIMARY KEY ($COLUMN_NOTE_ID, $COLUMN_TAG_ID_FK),
                FOREIGN KEY ($COLUMN_NOTE_ID) REFERENCES $TABLE_NOTES($COLUMN_ID),
                FOREIGN KEY ($COLUMN_TAG_ID_FK) REFERENCES $TABLE_TAGS($COLUMN_TAG_ID)
            )
        """
    }
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(CREATE_NOTES_TABLE)
        db.execSQL(CREATE_TAGS_TABLE)
        db.execSQL(CREATE_NOTE_TAGS_TABLE)



    }



    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NOTE_TAGS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TAGS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NOTES")
        onCreate(db)
    }


}
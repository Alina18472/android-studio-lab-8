
package com.example.laba8

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class EditTagActivity : AppCompatActivity() {
    private lateinit var tagRepository: TagRepository
    private lateinit var noteRepository: NoteRepository
    private var currentTag: Tag? = null
    private var isEditMode = false

    private lateinit var etTagName: EditText
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button
    private lateinit var btnDelete: ImageButton
    private lateinit var tvNotesWithTag: TextView
    private lateinit var notesRecyclerView: RecyclerView
    private lateinit var notesAdapter: NotesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_tag)

        tagRepository = TagRepository(this)
        noteRepository = NoteRepository(this)


        val tagId = intent.getLongExtra("TAG_ID", -1)
        isEditMode = tagId != -1L


        initUI()

        if (isEditMode) {

            currentTag = tagRepository.getTagById(tagId) ?: run {
                showToast("Тег не найден")
                finish()
                return
            }
            etTagName.setText(currentTag?.name)
            btnDelete.visibility = View.VISIBLE


            loadNotesWithTag(tagId)
        }
        else {

            btnDelete.visibility = View.GONE
            tvNotesWithTag.visibility = View.GONE
        }


        setupClickListeners()
    }

    private fun initUI() {

        etTagName = findViewById(R.id.et_tag_name)
        btnSave = findViewById(R.id.btn_save)
        btnCancel = findViewById(R.id.btn_cancel)
        btnDelete = findViewById(R.id.btn_delete)
        tvNotesWithTag = findViewById(R.id.tv_notes_with_tag)

        notesRecyclerView = findViewById(R.id.notes_recycler_view)
        notesRecyclerView.layoutManager = LinearLayoutManager(this)
        notesAdapter = NotesAdapter(emptyList()) { note ->
            openNoteForEditing(note.id)
        }
        notesRecyclerView.adapter = notesAdapter


        findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar).title =
            if (isEditMode) "Редактирование тега" else "Создание тега"
    }

    private fun setupClickListeners() {
        btnSave.setOnClickListener {
            if (validateInput()) {
                saveTag()
            }
        }

        btnCancel.setOnClickListener {
            finish()
        }

        btnDelete.setOnClickListener {
            deleteTag()
        }
    }

    private fun validateInput(): Boolean {
        val name = etTagName.text.toString().trim()
        if (name.isEmpty()) {
            showToast("Название тега не может быть пустым")
            return false
        }
        // Проверка уникальности
        val allTags = tagRepository.getAllTags()
        val tagExists = allTags.any {
            it.name.equals(name, ignoreCase = true) &&
                    (!isEditMode || it.id != currentTag?.id)
        }

        if (tagExists) {
            showToast("Тег с таким именем уже существует")
            return false
        }
        return true
    }

    private fun saveTag() {
        val name = etTagName.text.toString().trim()

        if (isEditMode) {
            currentTag?.let { tag ->
                if (name != tag.name) {
                    val updatedTag = tag.copy(name = name)
                    if (tagRepository.updateTag(updatedTag)) {
                        showToast("Тег обновлен")
                        setResult(RESULT_OK)
                        finish()
                    } else {
                        showToast("Ошибка при обновлении тега")
                    }
                } else {
                    finish()
                }
            }
        } else {
            val newTag = Tag(id = 0, name = name)
            if (tagRepository.insertTag(newTag)) {
                showToast("Тег создан")
                setResult(RESULT_OK)
                finish()
            } else {
                showToast("Ошибка при создании тега")
            }
        }
    }

    private fun deleteTag() {
        currentTag?.let { tag ->

            if (tagRepository.deleteTag(tag.id)) {
                showToast("Тег удален")
                setResult(RESULT_OK)
                finish()
            } else {
                showToast("Ошибка при удалении тега")
            }
        }
    }

    private fun loadNotesWithTag(tagId: Long) {
        val notes = noteRepository.getNotesByTagId(tagId)
        notesAdapter.updateNotes(notes)
        tvNotesWithTag.visibility = if (notes.isNotEmpty()) View.VISIBLE else View.GONE
    }

    private fun openNoteForEditing(noteId: Long) {
        val intent = Intent(this, EditNoteActivity::class.java).apply {
            putExtra("note_id", noteId)
        }
        startActivity(intent)
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        tagRepository.close()
        noteRepository.close()
        super.onDestroy()
    }
}
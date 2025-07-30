//package com.example.laba8
//
//import android.app.AlertDialog
//import android.content.Intent
//import android.os.Bundle
//import android.view.LayoutInflater
//import android.view.View
//import android.widget.*
//import androidx.appcompat.app.AppCompatActivity
//import java.text.SimpleDateFormat
//import java.util.*
//
//
//class EditNoteActivity : AppCompatActivity() {
//
//    private lateinit var noteRepository: NoteRepository
//    private lateinit var tagRepository: TagRepository
//    private var currentNoteId: Long = -1
//    private var isEditMode = false
//    private val selectedTags = mutableSetOf<String>()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_edit_note)
//
//        noteRepository = NoteRepository(this)
//        tagRepository = TagRepository(this)
//
//        currentNoteId = intent.getLongExtra("note_id", -1)
//        isEditMode = currentNoteId != -1L
//        initUI()
//
//        val deleteButton = findViewById<ImageButton>(R.id.btn_delete)
//        deleteButton.visibility = if (isEditMode) View.VISIBLE else View.GONE
//        deleteButton.setOnClickListener {
//            showDeleteConfirmationDialog()
//        }
//
//
//        findViewById<Button>(R.id.btn_add_tag).setOnClickListener {
//            showAddTagDialog()
//        }
//
//
//        findViewById<Button>(R.id.btn_cancel).setOnClickListener {
//            finish()
//        }
//
//
//        findViewById<Button>(R.id.btn_save).setOnClickListener {
//            if (isEditMode) {
//                updateNote()
//            } else {
//                createNote()
//            }
//        }
//    }
//
//    private fun initUI() {
//        if (isEditMode) {
//
//            val note = noteRepository.getNoteById(currentNoteId) ?: run {
//                finish()
//                return
//            }
//
//
//            findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar).title = "Редактирование заметки"
//
//            findViewById<TextView>(R.id.tv_date).text = "Дата создания: ${note.createdAt}"
//            findViewById<EditText>(R.id.et_title).setText(note.title)
//            findViewById<EditText>(R.id.et_content).setText(note.content)
//
//
//            selectedTags.addAll(note.tags)
//        } else {
//
//            findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar).title = "Создание заметки"
//            findViewById<TextView>(R.id.tv_date).text = "Дата создания: ${getCurrentDateTime()}"
//        }
//
//        updateTagsUI()
//    }
//
//    private fun getCurrentDateTime(): String {
//        val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
//        return dateFormat.format(Date())
//    }
//
//    private fun updateTagsUI() {
//        val tagsContainer = findViewById<LinearLayout>(R.id.tags_container)
//        tagsContainer.removeAllViews()
//
//        if (selectedTags.isEmpty()) {
//            val textView = TextView(this)
//            textView.text = "Нет тегов"
//            textView.setTextColor(resources.getColor(android.R.color.darker_gray))
//            tagsContainer.addView(textView)
//        } else {
//            for (tag in selectedTags) {
//                val tagView = LayoutInflater.from(this).inflate(R.layout.item_tag_edit, tagsContainer, false)
//                val tagName = tagView.findViewById<TextView>(R.id.tv_tag_name)
//                val removeBtn = tagView.findViewById<ImageButton>(R.id.btn_remove_tag)
//
//                tagName.text = tag
//                removeBtn.setOnClickListener {
//                    selectedTags.remove(tag)
//                    updateTagsUI()
//                }
//
//                tagsContainer.addView(tagView)
//            }
//        }
//    }
//
//    private fun showAddTagDialog() {
//        val allTags = tagRepository.getAllTags().map { it.name }
//        val availableTags = allTags.filter { !selectedTags.contains(it) }.toTypedArray()
//
//        if (availableTags.isEmpty()) {
//            Toast.makeText(this, "Все доступные теги уже добавлены", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        val builder = AlertDialog.Builder(this)
//        builder.setTitle("Выберите тег для добавления")
//            .setItems(availableTags) { _, which ->
//                selectedTags.add(availableTags[which])
//                updateTagsUI()
//            }
//            .setNegativeButton("Отмена", null)
//            .show()
//    }
//
//    private fun showDeleteConfirmationDialog() {
//        AlertDialog.Builder(this)
//            .setTitle("Удаление заметки")
//            .setMessage("Вы уверены, что хотите удалить эту заметку?")
//            .setPositiveButton("Удалить") { _, _ ->
//                noteRepository.deleteNote(currentNoteId)
//                finish()
//            }
//            .setNegativeButton("Отмена", null)
//            .show()
//    }
//
//    private fun updateNote() {
//        val title = findViewById<EditText>(R.id.et_title).text.toString().trim()
//        val content = findViewById<EditText>(R.id.et_content).text.toString().trim()
//
//        if (title.isEmpty()) {
//            Toast.makeText(this, "Заголовок не может быть пустым", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        val updatedNote = noteRepository.getNoteById(currentNoteId)?.copy(
//            title = title,
//            content = content,
//            tags = selectedTags.toList()
//        ) ?: return
//
//        noteRepository.updateNote(updatedNote)
//        finish()
//    }
//
//    private fun createNote() {
//        val title = findViewById<EditText>(R.id.et_title).text.toString().trim()
//        val content = findViewById<EditText>(R.id.et_content).text.toString().trim()
//
//        if (title.isEmpty()) {
//            Toast.makeText(this, "Заголовок не может быть пустым", Toast.LENGTH_SHORT).show()
//            return
//        }
//
//        val newNote = Note(
//            id = 0,
//            createdAt = getCurrentDateTime(),
//            title = title,
//            content = content,
//            tags = selectedTags.toList()
//        )
//
//        noteRepository.addNote(newNote)
//        finish()
//    }
//
//    override fun onDestroy() {
//        noteRepository.close()
//        tagRepository.close()
//        super.onDestroy()
//    }
//}
package com.example.laba8

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*

class EditNoteActivity : AppCompatActivity() {

    private lateinit var noteRepository: NoteRepository
    private lateinit var tagRepository: TagRepository
    private var currentNoteId: Long = -1
    private var isEditMode = false
    private val selectedTags = mutableSetOf<String>()

    private lateinit var etDate: EditText
    private lateinit var datePickerDialog: DatePickerDialog
    private lateinit var timePickerDialog: TimePickerDialog
    private val calendar = Calendar.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_note)

        noteRepository = NoteRepository(this)
        tagRepository = TagRepository(this)

        currentNoteId = intent.getLongExtra("note_id", -1)
        isEditMode = currentNoteId != -1L
        etDate = findViewById(R.id.et_date)
        initUI()
        initDatePicker()
        initTimePicker()
        setupDatePicker()

        val deleteButton = findViewById<ImageButton>(R.id.btn_delete)
        deleteButton.visibility = if (isEditMode) View.VISIBLE else View.GONE
        deleteButton.setOnClickListener {
            showDeleteConfirmationDialog()
        }

        findViewById<Button>(R.id.btn_add_tag).setOnClickListener {
            showAddTagDialog()
        }

        findViewById<Button>(R.id.btn_cancel).setOnClickListener {
            finish()
        }

        findViewById<Button>(R.id.btn_save).setOnClickListener {
            if (isEditMode) {
                updateNote()
            } else {
                createNote()
            }
        }
    }

    private fun initUI() {
        if (isEditMode) {
            val note = noteRepository.getNoteById(currentNoteId) ?: run {
                finish()
                return
            }

            findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar).title = "Редактирование заметки"
            findViewById<EditText>(R.id.et_title).setText(note.title)
            findViewById<EditText>(R.id.et_content).setText(note.content)
            etDate.setText(note.createdAt)
            selectedTags.addAll(note.tags)
        } else {
            findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar).title = "Создание заметки"
            etDate.setText(getCurrentDateTime())
        }

        updateTagsUI()
    }

    private fun initDatePicker() {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
            updateDateInView()
            timePickerDialog.show()
        }

        datePickerDialog = DatePickerDialog(
            this,
            dateSetListener,
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
    }

    private fun initTimePicker() {
        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
            calendar.set(Calendar.MINUTE, minute)
            updateDateInView()
        }

        timePickerDialog = TimePickerDialog(
            this,
            timeSetListener,
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )
    }

    private fun updateDateInView() {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        etDate.setText(dateFormat.format(calendar.time))
    }

    private fun setupDatePicker() {
        etDate = findViewById(R.id.et_date)
        etDate.setOnClickListener {
            datePickerDialog.show()
        }


    }

    private fun getCurrentDateTime(): String {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        return dateFormat.format(Date())
    }

    private fun updateTagsUI() {
        val tagsContainer = findViewById<LinearLayout>(R.id.tags_container)
        tagsContainer.removeAllViews()

        if (selectedTags.isEmpty()) {
            val textView = TextView(this)
            textView.text = "Нет тегов"
            textView.setTextColor(resources.getColor(android.R.color.darker_gray))
            tagsContainer.addView(textView)
        } else {
            for (tag in selectedTags) {
                val tagView = LayoutInflater.from(this).inflate(R.layout.item_tag_edit, tagsContainer, false)
                val tagName = tagView.findViewById<TextView>(R.id.tv_tag_name)
                val removeBtn = tagView.findViewById<ImageButton>(R.id.btn_remove_tag)

                tagName.text = tag
                removeBtn.setOnClickListener {
                    selectedTags.remove(tag)
                    updateTagsUI()
                }

                tagsContainer.addView(tagView)
            }
        }
    }

    private fun showAddTagDialog() {
        val allTags = tagRepository.getAllTags().map { it.name }
        val availableTags = allTags.filter { !selectedTags.contains(it) }.toTypedArray()

        if (availableTags.isEmpty()) {
            Toast.makeText(this, "Все доступные теги уже добавлены", Toast.LENGTH_SHORT).show()
            return
        }

        AlertDialog.Builder(this)
            .setTitle("Выберите тег для добавления")
            .setItems(availableTags) { _, which ->
                selectedTags.add(availableTags[which])
                updateTagsUI()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun showDeleteConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("Удаление заметки")
            .setMessage("Вы уверены, что хотите удалить эту заметку?")
            .setPositiveButton("Удалить") { _, _ ->
                noteRepository.deleteNote(currentNoteId)
                finish()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun updateNote() {
        val title = findViewById<EditText>(R.id.et_title).text.toString().trim()
        val content = findViewById<EditText>(R.id.et_content).text.toString().trim()
        val date = etDate.text.toString()

        if (title.isEmpty()) {
            Toast.makeText(this, "Заголовок не может быть пустым", Toast.LENGTH_SHORT).show()
            return
        }

        if (date.isEmpty()) {
            Toast.makeText(this, "Дата не может быть пустой", Toast.LENGTH_SHORT).show()
            return
        }

        val updatedNote = noteRepository.getNoteById(currentNoteId)?.copy(
            createdAt = date,
            title = title,
            content = content,
            tags = selectedTags.toList()
        ) ?: return

        noteRepository.updateNote(updatedNote)
        finish()
    }

    private fun createNote() {
        val title = findViewById<EditText>(R.id.et_title).text.toString().trim()
        val content = findViewById<EditText>(R.id.et_content).text.toString().trim()
        val date = etDate.text.toString()

        if (title.isEmpty()) {
            Toast.makeText(this, "Заголовок не может быть пустым", Toast.LENGTH_SHORT).show()
            return
        }

        if (date.isEmpty()) {
            Toast.makeText(this, "Дата не может быть пустой", Toast.LENGTH_SHORT).show()
            return
        }

        val newNote = Note(
            id = 0,
            createdAt = date,
            title = title,
            content = content,
            tags = selectedTags.toList()
        )

        noteRepository.addNote(newNote)
        finish()
    }

    override fun onDestroy() {
        noteRepository.close()
        tagRepository.close()
        super.onDestroy()
    }
}
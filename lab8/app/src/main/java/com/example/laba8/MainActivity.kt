package com.example.laba8
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.laba8.NoteRepository
import com.google.android.material.floatingactionbutton.FloatingActionButton
import android.content.Intent
import java.text.SimpleDateFormat
import java.util.*
import android.widget.Toast
class MainActivity : AppCompatActivity() {

    private lateinit var notesRecyclerView: RecyclerView
    private lateinit var notesAdapter: NotesAdapter
    private lateinit var noteRepository: NoteRepository
    private lateinit var tagRepository: TagRepository


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        noteRepository = NoteRepository(this)

        tagRepository = TagRepository(this)


        if (noteRepository.getAllNotes().isEmpty()) {
            addSampleNotes()
        }

        notesRecyclerView = findViewById(R.id.notes_recycler_view)
        setupRecyclerView()


        findViewById<Button>(R.id.btn_tags).setOnClickListener {
            try {
                val intent = Intent(this, TagsActivity::class.java)
                startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this, "Error opening tags: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
        findViewById<FloatingActionButton>(R.id.fab_add_note).setOnClickListener {
            val intent = Intent(this, EditNoteActivity::class.java)
            startActivity(intent)
        }
    }

    private fun addSampleNotes() {
        val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
        val currentDate = dateFormat.format(Date())

        val availableTags = tagRepository.getAllTags().map { it.name }

        val sampleNotes = listOf(
            Note(
                id = 0,
                createdAt = currentDate,
                title = "Первая заметка",
                content = "Это пример первой заметки в приложении",
                tags = listOf("Важное", "Работа").filter { availableTags.contains(it) }
            ),
            Note(
                id = 0,
                createdAt = currentDate,
                title = "Вторая заметка",
                content = "Это вторая тестовая заметка с более длинным текстом",
                tags = listOf("Личное").filter { availableTags.contains(it) }
            ),
            Note(
                id = 0,
                createdAt = currentDate,
                title = "Третья заметка",
                content = "Еще один пример заметки",
                tags = emptyList()
            )
        )

        sampleNotes.forEach { noteRepository.addNote(it) }
    }

    private fun setupRecyclerView() {
        notesAdapter = NotesAdapter(noteRepository.getAllNotes()) { note ->
            val intent = Intent(this, EditNoteActivity::class.java).apply {
                putExtra("note_id", note.id)
            }
            startActivity(intent)
        }
        notesRecyclerView.layoutManager = LinearLayoutManager(this)
        notesRecyclerView.adapter = notesAdapter
    }

    override fun onResume() {
        super.onResume()
        notesAdapter.updateNotes(noteRepository.getAllNotes())
    }

    override fun onDestroy() {
        noteRepository.close()
        super.onDestroy()
    }
}


class NotesAdapter(private var notes: List<Note>, private val onItemClick: (Note) -> Unit) : RecyclerView.Adapter<NotesAdapter.NoteViewHolder>() {

    fun updateNotes(newNotes: List<Note>) {
        notes = newNotes
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_note, parent, false)
        return NoteViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteViewHolder, position: Int) {
        holder.bind(notes[position])
    }

    override fun getItemCount(): Int = notes.size

    inner class NoteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvDate: TextView = itemView.findViewById(R.id.tv_note_date)
        private val tvTitle: TextView = itemView.findViewById(R.id.tv_note_title)
        private val tvContent: TextView = itemView.findViewById(R.id.tv_note_content)
        private val tvTags: TextView = itemView.findViewById(R.id.tv_note_tags)

        fun bind(note: Note) {
            tvDate.text = note.createdAt
            tvTitle.text = note.title
            tvContent.text = note.content
            tvTags.text = if (note.tags.isNotEmpty()) "Теги: ${note.tags.joinToString(", ")}" else ""

            itemView.setOnClickListener {
                onItemClick(note)
            }
        }
    }
}
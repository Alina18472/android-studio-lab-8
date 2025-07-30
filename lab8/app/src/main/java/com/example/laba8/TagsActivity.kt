
package com.example.laba8

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.graphics.Rect
import android.util.DisplayMetrics
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlin.math.max

class TagsActivity : AppCompatActivity() {

    private lateinit var tagsRecyclerView: RecyclerView
    private lateinit var tagsAdapter: TagsAdapter
    private lateinit var tagRepository: TagRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tags)



        tagRepository = TagRepository(this)


        tagsRecyclerView = findViewById(R.id.tags_recycler_view)
        setupRecyclerView()


        findViewById<Button>(R.id.btn_notes).apply {
            setOnClickListener { finish() }

        }

        findViewById<FloatingActionButton>(R.id.fab_create_tag).setOnClickListener {
            val intent = Intent(this, EditTagActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupRecyclerView() {
        tagsAdapter = TagsAdapter(tagRepository.getAllTags())

        tagsRecyclerView.layoutManager = LinearLayoutManager(this)
        tagsRecyclerView.adapter = tagsAdapter
    }
    override fun onResume() {
        super.onResume()

        tagsAdapter.updateData(tagRepository.getAllTags())
    }
}

class TagsAdapter(
    private var tags: List<Tag>
) : RecyclerView.Adapter<TagsAdapter.TagViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_tag, parent, false)
        return TagViewHolder(view)
    }

    override fun onBindViewHolder(holder: TagViewHolder, position: Int) {
        holder.bind(tags[position])
    }

    override fun getItemCount(): Int = tags.size
    fun updateData(newTags: List<Tag>) {
        tags = newTags
        notifyDataSetChanged()
    }
    inner class TagViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTag: TextView = itemView.findViewById(R.id.tv_tag)

        fun bind(tag: Tag) {
            tvTag.text = tag.name

            itemView.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, EditTagActivity::class.java).apply {
                    putExtra("TAG_ID", tag.id)
                }
                context.startActivity(intent)
            }
        }
    }
}
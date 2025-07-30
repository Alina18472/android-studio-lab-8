package com.example.laba8

data class Note(
    val id: Long,
    val createdAt: String,
    val title: String,
    val content: String,
    val tags: List<String>
)
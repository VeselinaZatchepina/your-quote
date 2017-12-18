package com.github.veselinazatchepina.myquotes.data.local.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey


@Entity(foreignKeys = arrayOf(
        ForeignKey(
                entity = Book::class,
                parentColumns = arrayOf("bookId"),
                childColumns = arrayOf("bookIdJoin")),
        ForeignKey(
                entity = BookReleaseYear::class,
                parentColumns = arrayOf("yearId"),
                childColumns = arrayOf("yearIdJoin"))
))
data class BookAndBookReleaseYear(@PrimaryKey(autoGenerate = true) val bookAndBookReleaseYearId: Long,
                                  val yearIdJoin: Long,
                                  val bookIdJoin: Long)
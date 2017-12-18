package com.github.veselinazatchepina.myquotes.data.local.entity

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey


@Entity
data class BookReleaseYear(@PrimaryKey(autoGenerate = true) val yearId: Long,
                           val year: Long)
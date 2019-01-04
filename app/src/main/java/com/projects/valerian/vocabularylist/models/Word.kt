package com.projects.valerian.vocabularylist.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class SimplifiedLexicalEntry(
    val partOfSpeech: String,
    val definitions: List<String>,
    val examples: List<String>,
    val synonyms: List<String>
): Parcelable

@Parcelize
class Word(
    val id: String,
    val lexicalEntries: List<SimplifiedLexicalEntry>
): Parcelable {

    fun getSummary() =
        lexicalEntries
            .flatMap { it.definitions }
            .mapIndexed { index, string -> "${index + 1}. $string" }
            .joinToString(separator = "; ")

    fun getDescription() =
            lexicalEntries
                .flatMap { it.definitions }
                .mapIndexed { index, string -> "${index + 1}. $string" }
                .joinToString(separator = "\n")
}
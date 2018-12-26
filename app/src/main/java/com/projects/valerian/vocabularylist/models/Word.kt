package com.projects.valerian.vocabularylist.models

class SimplifiedLexicalEntry(
    val partOfSpeech: String,
    val definitions: List<String>,
    val examples: List<String>,
    val synonyms: List<String>
)

class Word(
    val id: String,
    val lexicalEntries: List<SimplifiedLexicalEntry>
) {

    fun getSummary() =
        lexicalEntries
            .flatMap { it.definitions }
            .mapIndexed { index, string -> "${index + 1}. $string" }
            .joinToString(separator = "; ")
}
package com.example.wordsapp.networking.responsemodels

import com.orm.SugarRecord

class Word : SugarRecord<Word?> {
    var word: String? = null
    var meaning: String? = null
    var selected:Boolean?=false

    constructor() {}
    constructor(word: String?, meaning: String?,selected:Boolean?) {
        this.word = word
        this.meaning = meaning
        this.selected=selected
    }
}
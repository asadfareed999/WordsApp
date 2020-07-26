package com.example.wordsapp.networking.responsemodels

import com.orm.SugarRecord

class Word : SugarRecord<Word?> {
    var word: String? = null
    var meaning: String? = null

    constructor() {}
    constructor(word: String?, meaning: String?) {
        this.word = word
        this.meaning = meaning
    }
}
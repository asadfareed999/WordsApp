package com.example.wordsapp.networking.responsemodels

import com.orm.SugarRecord

class AllWords : SugarRecord<AllWords?> {
    var word: String? = null
    var meaning: String? = null

    constructor() {}
    constructor(word: String?, meaning: String?) {
        this.word = word
        this.meaning = meaning
    }
}
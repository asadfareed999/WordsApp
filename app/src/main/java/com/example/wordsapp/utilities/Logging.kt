package com.example.wordsapp.utilities

import timber.log.Timber

class Logging {
    companion object {
        fun debug(message: String, tag: String = "") {
            if (tag.isEmpty())
                Timber.d(message)
            else
                Timber.tag(tag).d(message)
        }

        fun error(message: String, tag: String = "") {
            if (tag.isEmpty())
                Timber.e(message)
            else
                Timber.tag(tag).e(message)
        }

        fun info(message: String, tag: String = "") {
            if (tag.isEmpty())
                Timber.i(message)
            else
                Timber.tag(tag).i(message)
        }

        fun warning(message: String, tag: String = "") {
            if (tag.isEmpty())
                Timber.w(message)
            else
                Timber.tag(tag).w(message)
        }
    }
}
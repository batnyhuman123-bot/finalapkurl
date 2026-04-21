package com.example.finalapkurl.data

import android.util.Base64

object UrlCodec {
    fun urlToVtId(url: String): String {
        return Base64.encodeToString(url.toByteArray(Charsets.UTF_8), Base64.URL_SAFE or Base64.NO_WRAP)
            .trimEnd('=')
    }
}

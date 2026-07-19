package com.example.data.local

import androidx.room.TypeConverter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class Converters {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        if (value == null) return null
        val type = Types.newParameterizedType(List::class.java, String::class.java)
        val adapter = moshi.adapter<List<String>>(type)
        return adapter.toJson(value)
    }

    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        if (value == null) return null
        val type = Types.newParameterizedType(List::class.java, String::class.java)
        val adapter = moshi.adapter<List<String>>(type)
        return adapter.fromJson(value)
    }

    @TypeConverter
    fun fromMapList(value: List<Map<String, String>>?): String? {
        if (value == null) return null
        val type = Types.newParameterizedType(
            List::class.java,
            Types.newParameterizedType(Map::class.java, String::class.java, String::class.java)
        )
        val adapter = moshi.adapter<List<Map<String, String>>>(type)
        return adapter.toJson(value)
    }

    @TypeConverter
    fun toMapList(value: String?): List<Map<String, String>>? {
        if (value == null) return null
        val type = Types.newParameterizedType(
            List::class.java,
            Types.newParameterizedType(Map::class.java, String::class.java, String::class.java)
        )
        val adapter = moshi.adapter<List<Map<String, String>>>(type)
        return adapter.fromJson(value)
    }
}

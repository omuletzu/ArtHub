package com.example.menuapp

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class user_database (val context : Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object{
        val DATABASE_NAME = "user_database"
        val DATABASE_VERSION = 1
    }

    override fun onCreate(db : SQLiteDatabase) {

        db.execSQL("CREATE TABLE $DATABASE_NAME " +
                "(ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "NAME VARCHAR(20), " +
                "PASSWORD VARCHAR(20), " +
                "LOGGED INTEGER )")
    }

    override fun onUpgrade(p0: SQLiteDatabase?, p1: Int, p2: Int) {
        TODO("Not yet implemented")
    }
}
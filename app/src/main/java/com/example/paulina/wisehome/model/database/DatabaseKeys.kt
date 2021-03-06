package com.example.paulina.wisehome.model.database


enum class DatabaseKeys constructor(private val key: String) {
    ACCOUNT_TYPE("account type"),
    USERNAME("username");

    override fun toString(): String {
        return key
    }
}
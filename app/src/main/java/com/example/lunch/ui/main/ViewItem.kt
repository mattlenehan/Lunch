package com.example.lunch.ui.main

interface ViewItem<T>: Comparable<T> {
    fun areContentsTheSame(other: T): Boolean
    fun areItemsTheSame(other: T): Boolean
}
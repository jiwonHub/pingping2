package com.example.pingpinge.community

data class CommunityDB(
    val title: String,
    val content: String,
    val uri: String,
    val key: Long
){
    constructor() : this("", "", "", 0)
}

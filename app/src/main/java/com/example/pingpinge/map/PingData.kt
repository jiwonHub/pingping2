package com.example.pingpinge.map

data class PingData (
    val id : Long,
    val title : String,
    val content : String,
    val uri : String,
    val lat : Double,
    val lng : Double
) {
    constructor() : this(0, "", "", "", 0.0, 0.0)
}
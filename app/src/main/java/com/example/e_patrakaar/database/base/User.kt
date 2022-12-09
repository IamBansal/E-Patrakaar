package com.example.e_patrakaar.database.base


data class User(
    val name:String,
    val imageUrl:String,
    val email:String,
    val number:String,
    val uid:String,
    val deviceToken:String
){
    constructor(): this("","","","","","")

    constructor(name: String,imageUrl: String,email: String,number: String,uid: String): this(
        name,
        imageUrl,
        email,
        number,
        uid,
        "")
}
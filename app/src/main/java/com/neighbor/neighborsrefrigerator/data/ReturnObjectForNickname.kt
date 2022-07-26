package com.neighbor.neighborsrefrigerator.data

import com.google.gson.annotations.SerializedName

data class ReturnObjectForNickname(

    @SerializedName("isConnect")
    val isConnect: Boolean,
    @SerializedName("resultCode")
    val resultCode: Int,
    @SerializedName("msg")
    val msg: String,
    @SerializedName("isExist")
    val isExist: Boolean

)

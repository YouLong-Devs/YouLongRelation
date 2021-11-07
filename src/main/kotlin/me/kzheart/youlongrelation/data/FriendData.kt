package me.kzheart.youlongrelation.data

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

/**
 * @author kzheart
 * @date 2021/11/1 20:08
 */
data class FriendData(val friendName: String, val date: Date)


inline fun <reified T> Gson.fromJson(json: String) = this.fromJson<T>(json, object : TypeToken<T>() {}.type)

/**
 * Map序列化
 */
fun <K, V> MutableMap<K, V>.serializeString(): String {
    return Gson().toJson(this)
}

/**
 * 好友列表反序列化
 */
fun String?.deserializeFriends(): MutableMap<String, FriendData> {
    return if (this != null) Gson().fromJson<MutableMap<String, FriendData>>(this) else mutableMapOf()
}

/**
 * 亲密度反序列化
 */
fun String?.deserializeIntimacy(): MutableMap<String, Int> {
    return if (this != null) Gson().fromJson<MutableMap<String, Int>>(this) else mutableMapOf()
}


/*fun MutableMap<String, FriendData>.serializeString(): String {
    return Gson().toJson(this)
}*/


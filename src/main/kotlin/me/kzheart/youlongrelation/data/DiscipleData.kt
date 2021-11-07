package me.kzheart.youlongrelation.data

import com.google.gson.Gson
import java.util.*

/**
 * @author kzheart
 * @date 2021/11/1 20:13
 */
data class DiscipleData(val name: String, val data: Date)

/*fun Player.addDisciple(name: String): Boolean {
    val disciplesData = getDataContainer()["disciples"].deserializeDisciples()
    if (disciplesData.containsKey(name) || disciplesData.size >= 9) return false
    disciplesData[name] = DiscipleData(name, Date())
    getDataContainer()["disciples"] = disciplesData.serializeString()
    return true
}

fun Player.removeDisciple(name: String): Boolean {
    val disciplesData = getDataContainer()["disciples"].deserializeDisciples()
    if (!disciplesData.containsKey(name)) return false
    val removedData = disciplesData.remove(name)
    getDataContainer()["disciples"] = disciplesData.serializeString()
    return removedData != null
}*/

fun String?.deserializeDisciples(): MutableMap<String, DiscipleData> {
    return if (this != null) Gson().fromJson<MutableMap<String, DiscipleData>>(this) else mutableMapOf()
}

/*
fun MutableMap<String, DiscipleData>.serializeString(): String {
    return Gson().toJson(this)
}
*/

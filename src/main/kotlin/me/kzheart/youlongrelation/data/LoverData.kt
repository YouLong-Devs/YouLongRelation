package me.kzheart.youlongrelation.data

import com.google.gson.Gson
import java.util.*

/**
 * @author kzheart
 * @date 2021/11/1 20:09
 */
class LoverData(val name: String, val date: Date) {
    fun serializeString(): String {
        return Gson().toJson(this)
    }


}


/*fun ProxyPlayer.addLover(loverName: String): Boolean {
    val playerLoverData = getDataContainer()["lover"].deserializeLover()
    val loverData = loverName.getDataContainerByName()["lover"].deserializeLover()
    if (playerLoverData == null && loverData == null) {
        val date = Date()
        getDataContainer()["lover"] = LoverData(loverName, date).serializeString()
        loverName.getDataContainerByName()["lover"] = LoverData(this.name, date).serializeString()
        return true
    }
    return false
}

fun ProxyPlayer.removeLover(): Boolean {
    val playerLoverData = getDataContainer()["lover"].deserializeLover() ?: return false
    playerLoverData.name.getDataContainerByName()["lover"] = ""
    getDataContainer()["lover"] = ""
    return true
}*/

fun String?.deserializeLover(): LoverData? {
    return if (this != null) Gson().fromJson<LoverData>(this) else null
}


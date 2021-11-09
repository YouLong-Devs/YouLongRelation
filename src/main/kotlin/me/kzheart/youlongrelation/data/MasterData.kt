package me.kzheart.youlongrelation.data

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import java.util.*

/**
 * @author kzheart
 * @date 2021/11/1 20:13
 */
class MasterData(val master: String, val date: Date) {
    fun serializeString(): String {
        return GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create().toJson(this)
    }
}

/*fun Player.addMaster(name: String): Boolean {
    val masterData = getDataContainer()["master"].deserializeMaster()
    if (masterData == null) {
        getDataContainer()["master"] = MasterData(name, Date()).serializeString()
        return true
    }
    return false
}

fun Player.removeMaster(): Boolean {
    player.getDataContainer()["master"].deserializeMaster() ?: return false
    getDataContainer()["master"] = ""
    return true
}*/

fun String?.deserializeMaster(): MasterData? {
    return if (this != null) GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create().fromJson<MasterData>(this) else null
}
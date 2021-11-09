package me.kzheart.youlongrelation.api

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import me.kzheart.youlongrelation.common.Relation
import me.kzheart.youlongrelation.common.bukkit.conf.BukkitFriendConfManager
import me.kzheart.youlongrelation.common.bukkit.conf.BukkitLoverConfManager
import me.kzheart.youlongrelation.common.bukkit.conf.BukkitMasterDiscipleConfManager
import me.kzheart.youlongrelation.data.*
import me.kzheart.youlongrelation.database.getDataContainerByName
import org.apache.commons.lang3.time.DateUtils
import java.util.*

/**
 * @author kzheart
 * @date 2021/11/5 17:29
 */
object YouLongRelationApi {


    @JvmStatic
    fun getFriends(name: String): MutableMap<String, FriendData> {
        return name.getDataContainerByName()["friends"].deserializeFriends()
    }

    @JvmStatic
    fun addFriend(playerName: String, friendName: String): Boolean {
        if (playerName == friendName)
            return false
        val playerFriendsData = playerName.getDataContainerByName()["friends"].deserializeFriends()
        val friendsData = friendName.getDataContainerByName()["friends"].deserializeFriends()
        val date = Date()

        if (playerFriendsData.containsKey(friendName) || friendsData.containsKey(playerName)) return false
        playerFriendsData[friendName] = FriendData(friendName, date)
        friendsData[playerName] = FriendData(playerName, date)


        playerName.getDataContainerByName()["friends"] = playerFriendsData.serializeString()
        friendName.getDataContainerByName()["friends"] = friendsData.serializeString()
        return true
    }

    @JvmStatic
    fun removeFriend(playerName: String, friendName: String) {
        val playerFriendsData = playerName.getDataContainerByName()["friends"].deserializeFriends()
        val friendsData = friendName.getDataContainerByName()["friends"].deserializeFriends()


        playerFriendsData.remove(friendName)
        friendsData.remove(playerName)


        playerName.getDataContainerByName()["friends"] = playerFriendsData.serializeString()
        friendName.getDataContainerByName()["friends"] = friendsData.serializeString()
    }

    @JvmStatic
    fun getLover(playerName: String): LoverData? {
        return playerName.getDataContainerByName()["lover"].deserializeLover()
    }

    @JvmStatic
    fun addLover(playerName: String, loverName: String): Boolean {
        if (playerName == loverName)
            return false
        val playerLoverData = getLover(playerName)
        val loverData = getLover(loverName)

        if (playerLoverData != null || loverData != null)
            return false

        val date = Date()

        playerName.getDataContainerByName()["lover"] = LoverData(loverName, date).serializeString()
        loverName.getDataContainerByName()["lover"] = LoverData(playerName, date).serializeString()
        setIntimacy(playerName, loverName, 0)
        return true
    }

    @JvmStatic
    fun removeLover(playerName: String) {
        val playerLoverData = playerName.getDataContainerByName()["lover"].deserializeLover()
        playerLoverData?.name?.getDataContainerByName()?.set("lover", "")
        playerName.getDataContainerByName()["lover"] = ""
    }

    @JvmStatic
    fun getIntimacy(playerName: String, friendName: String): Int {
        return playerName.getDataContainerByName()["intimacys"].deserializeIntimacy()[friendName] ?: return 0
    }


    @JvmStatic
    fun setIntimacy(playerName: String, friendName: String, intimacy: Int) {
        val playerIntimacyData = playerName.getDataContainerByName()["intimacys"].deserializeIntimacy()
        val intimacyData = friendName.getDataContainerByName()["intimacys"].deserializeIntimacy()

        playerIntimacyData[friendName] = intimacy
        intimacyData[playerName] = intimacy

        playerName.getDataContainerByName()["intimacys"] = playerIntimacyData.serializeString()
        friendName.getDataContainerByName()["intimacys"] = intimacyData.serializeString()
    }


    @JvmStatic
    fun getMaster(playerName: String): MasterData? {
        return playerName.getDataContainerByName()["master"].deserializeMaster()
    }

    @JvmStatic
    fun addMaster(playerName: String, masterName: String): Boolean {
        if (playerName == masterName)
            return false

        val playerMasterData = getMaster(playerName)
        val masterDiscipleData = getDisciples(masterName).toMutableMap()
        if (playerMasterData != null || masterDiscipleData.containsKey(playerName)) return false


        val date = Date()
        masterDiscipleData[playerName] = DiscipleData(playerName, date)

        playerName.getDataContainerByName()["master"] = MasterData(masterName, date).serializeString()
        masterName.getDataContainerByName()["disciples"] = masterDiscipleData.serializeString()
        return true
    }

    @JvmStatic
    fun removeMaster(playerName: String) {
        val masterData = getMaster(playerName) ?: return
        val deserializeDisciples = getDisciples(masterData.master).toMutableMap()
        deserializeDisciples.remove(playerName)
        playerName.getDataContainerByName()["master"] = ""
        masterData.master.getDataContainerByName()["disciples"] = deserializeDisciples.serializeString()
    }


    @JvmStatic
    fun getDisciples(playerName: String): Map<String, DiscipleData> {
        return playerName.getDataContainerByName()["disciples"].deserializeDisciples()
    }

    @JvmStatic
    fun addDisciple(playerName: String, discipleName: String): Boolean {
        return addMaster(discipleName, playerName)
    }

    @JvmStatic
    fun removeDisciple(discipleName: String) {
        removeMaster(discipleName)
    }

    @JvmStatic
    fun getRelation(playerName: String, targetName: String): List<Relation> {
        val relations = mutableListOf<Relation>()
        if (isFriend(playerName, targetName))
            relations.add(Relation.FRIEND)
        if (isMentoring(playerName, targetName))
            relations.add(Relation.MENTORING)
        if (isLover(playerName, targetName))
            relations.add(Relation.LOVER)
        return if (relations.size == 0)
            listOf(Relation.NONE)
        else
            relations
    }

    @JvmStatic
    fun isFriend(playerName: String, targetName: String): Boolean {
        return getFriends(playerName).containsKey(targetName)
    }

    @JvmStatic
    fun isLover(playerName: String, targetName: String): Boolean {
        return getLover(playerName)?.name == targetName
    }

    @JvmStatic
    fun isMentoring(playerName: String, targetName: String): Boolean {
        return getMaster(playerName)?.master == targetName || getDisciples(playerName).containsKey(targetName)
    }

    @JvmStatic
    fun hasLover(playerName: String): Boolean {
        return getLover(playerName) != null
    }


    @JvmStatic
    fun updateFriendUpgradeDate(playerName: String) {
        playerName.getDataContainerByName()["lastFriendUpgradeDate"] = Gson().toJson(Date())
    }

    @JvmStatic
    fun getFriendLastUpgradeDate(playerName: String): Date? {
        val dateString =
            playerName.getDataContainerByName()["lastFriendUpgradeDate"] ?: return null
        return Gson().fromJson(dateString, Date::class.java)
    }


    @JvmStatic
    fun getFriendUpgradeRemainTime(playerName: String): Int {
        val date = getFriendLastUpgradeDate(playerName)
        if (date == null || !DateUtils.isSameDay(date, Date()))
            setFriendUpgradeRemainTime(playerName, BukkitFriendConfManager.time)
        return playerName.getDataContainerByName()["friendUpgradeRemainTime"]?.toInt()
            ?: return BukkitFriendConfManager.time
    }

    @JvmStatic
    fun setFriendUpgradeRemainTime(playerName: String, remainingTime: Int) {
        if (remainingTime >= 0 && remainingTime <= BukkitFriendConfManager.time) {
            playerName.getDataContainerByName()["friendUpgradeRemainTime"] = remainingTime
        } else
            throw RuntimeException("friend remainTime data not in range ")
    }


    @JvmStatic
    fun getLoverLastUpgradeDate(playerName: String): Date? {
        val dateString =
            playerName.getDataContainerByName()["lastLoverUpgradeDate"] ?: return null
        return Gson().fromJson(dateString, Date::class.java)
    }

    @JvmStatic
    fun updateLoverUpgradeDate(playerName: String) {
        playerName.getDataContainerByName()["lastLoverUpgradeDate"] = Gson().toJson(Date())
    }

    @JvmStatic
    fun getLoverUpgradeRemainTime(playerName: String): Int {
        val date = getLoverLastUpgradeDate(playerName)
        if (date == null || !DateUtils.isSameDay(date, Date()))
            setLoverUpgradeRemainTime(playerName, BukkitLoverConfManager.time)
        return playerName.getDataContainerByName()["loverUpgradeRemainTime"]?.toInt()
            ?: return BukkitLoverConfManager.time
    }

    @JvmStatic
    fun getMasterLastUpgradeDate(playerName: String): Date? {
        val dateString =
            playerName.getDataContainerByName()["lastMasterUpgradeDate"] ?: return null
        return Gson().fromJson(dateString, Date::class.java)
    }

    @JvmStatic
    fun updateMasterUpgradeDate(playerName: String) {
        playerName.getDataContainerByName()["lastMasterUpgradeDate"] = Gson().toJson(Date())
    }


    @JvmStatic
    fun setLoverUpgradeRemainTime(playerName: String, remainingTime: Int) {
        if (remainingTime >= 0 && remainingTime <= BukkitLoverConfManager.time) {
            playerName.getDataContainerByName()["loverUpgradeRemainTime"] = remainingTime
        } else
            throw RuntimeException("lover remainTime data not in range ")
    }

    @JvmStatic
    fun getMasterUpgradeRemainTime(playerName: String): Int {
        val date = getMasterLastUpgradeDate(playerName)
        if (date == null || !DateUtils.isSameDay(date, Date()))
            setLoverUpgradeRemainTime(playerName, BukkitLoverConfManager.time)
        return playerName.getDataContainerByName()["masterUpgradeRemainTime"]?.toInt()
            ?: return BukkitMasterDiscipleConfManager.time
    }

    @JvmStatic
    fun setMasterUpgradeRemainTime(playerName: String, remainingTime: Int) {
        if (remainingTime >= 0 && remainingTime <= BukkitMasterDiscipleConfManager.time) {
            playerName.getDataContainerByName()["masterUpgradeRemainTime"] = remainingTime
        } else
            throw RuntimeException("master remainTime data not in range ")
    }

    fun setPlayerLevel(playerName: String, level: Int) {
        if (level >= 0) {
            playerName.getDataContainerByName()["level"] = level
        } else
            throw RuntimeException("level data not in range ")
    }

    fun getPlayerLevel(playerName: String): Int {
        return playerName.getDataContainerByName()["level"]?.toInt() ?: return 0
    }

    inline fun <reified T> Gson.fromJson(json: String) = this.fromJson<T>(json, object : TypeToken<T>() {}.type)


}


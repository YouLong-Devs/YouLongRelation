package me.kzheart.youlongrelation.api

import me.kzheart.youlongrelation.data.*
import net.md_5.bungee.api.connection.ProxiedPlayer
import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide
import taboolib.common.platform.function.getProxyPlayer
import taboolib.module.lang.sendLang

/**
 * @author kzheart
 * @date 2021/11/4 14:23
 */
@PlatformSide([Platform.BUNGEE])
object YouLongRelationBungeeApi {

    @JvmStatic
    fun getFriends(proxiedPlayer: ProxiedPlayer): MutableMap<String, FriendData> {
        return YouLongRelationApi.getFriends(proxiedPlayer.name)
    }

    @JvmStatic
    fun getFriends(proxiedPlayerName: String): MutableMap<String, FriendData> {
        return YouLongRelationApi.getFriends(proxiedPlayerName)
    }

    @JvmStatic
    fun setPlayerIntimacy(proxiedPlayer: ProxiedPlayer, friend: ProxiedPlayer, intimacy: Int) {
        YouLongRelationApi.setIntimacy(proxiedPlayer.name, friend.name, intimacy)
    }


    @JvmStatic
    fun addFriend(proxiedPlayer: ProxiedPlayer, friend: ProxiedPlayer): Boolean {
        return YouLongRelationApi.addFriend(proxiedPlayer.name, friend.name)
    }

    @JvmStatic
    fun addFriend(proxiedPlayer: ProxiedPlayer, friendName: String): Boolean {
        return YouLongRelationApi.addFriend(proxiedPlayer.name, friendName)
    }

    @JvmStatic
    fun removeFriend(proxiedPlayer: ProxiedPlayer, friend: ProxiedPlayer) {
        YouLongRelationApi.removeFriend(proxiedPlayer.name, friend.name)
    }

    @JvmStatic
    fun removeFriend(proxiedPlayer: ProxiedPlayer, friendName: String) {
        YouLongRelationApi.removeFriend(proxiedPlayer.name, friendName)
    }


    @JvmStatic
    fun getLover(proxiedPlayer: ProxiedPlayer): LoverData? {
        return YouLongRelationApi.getLover(proxiedPlayer.name)
    }

    fun getLover(proxiedPlayerName: String): LoverData? {
        return YouLongRelationApi.getLover(proxiedPlayerName)
    }

    @JvmStatic
    fun addLover(proxiedPlayer: ProxiedPlayer, lover: ProxiedPlayer): Boolean {
        return YouLongRelationApi.addLover(proxiedPlayer.name, lover.name)
    }

    @JvmStatic
    fun addLover(proxiedPlayer: ProxiedPlayer, loverName: String): Boolean {
        return YouLongRelationApi.addLover(proxiedPlayer.name, loverName)
    }

    @JvmStatic
    fun removeLover(proxiedPlayer: ProxiedPlayer) {
        YouLongRelationApi.removeLover(proxiedPlayer.name)
    }


    @JvmStatic
    fun getIntimacy(proxiedPlayer: ProxiedPlayer, friend: ProxiedPlayer): Int {
        return YouLongRelationApi.getIntimacy(proxiedPlayer.name, friend.name)
    }

    @JvmStatic
    fun getIntimacy(proxiedPlayer: ProxiedPlayer, friendName: String): Int {
        return YouLongRelationApi.getIntimacy(proxiedPlayer.name, friendName)
    }

    @JvmStatic
    fun getIntimacy(playerName: String, friendName: String): Int {
        return YouLongRelationApi.getIntimacy(playerName, friendName)
    }

    @JvmStatic
    fun getMaster(proxiedPlayer: ProxiedPlayer): MasterData? {
        return YouLongRelationApi.getMaster(proxiedPlayer.name)
    }

    @JvmStatic
    fun getMaster(proxiedPlayerName: String): MasterData? {
        return YouLongRelationApi.getMaster(proxiedPlayerName)
    }

    @JvmStatic
    fun addMaster(proxiedPlayer: ProxiedPlayer, master: ProxiedPlayer): Boolean {
        return YouLongRelationApi.addMaster(proxiedPlayer.name, master.name)
    }

    @JvmStatic
    fun addMaster(playerName: String, master: ProxiedPlayer): Boolean {
        return YouLongRelationApi.addMaster(playerName, master.name)
    }

    @JvmStatic
    fun addMaster(proxiedPlayer: ProxiedPlayer, masterName: String): Boolean {
        return YouLongRelationApi.addMaster(proxiedPlayer.name, masterName)
    }

    @JvmStatic
    fun removeMaster(proxiedPlayer: ProxiedPlayer) {
        YouLongRelationApi.removeMaster(proxiedPlayer.name)
    }

    @JvmStatic
    fun getDisciples(proxiedPlayer: ProxiedPlayer): Map<String, DiscipleData> {
        return YouLongRelationApi.getDisciples(proxiedPlayer.name)
    }

    @JvmStatic
    fun getDisciples(proxiedPlayerName: String): Map<String, DiscipleData> {
        return YouLongRelationApi.getDisciples(proxiedPlayerName)
    }


    @JvmStatic
    fun addDisciple(proxiedPlayer: ProxiedPlayer, discipleName: String): Boolean {
        return YouLongRelationApi.addMaster(proxiedPlayer.name, discipleName)
    }

    @JvmStatic
    fun addDisciple(playerName: String, disciple: ProxiedPlayer): Boolean {
        return YouLongRelationApi.addMaster(playerName, disciple.name)
    }

    @JvmStatic
    fun addDisciple(proxiedPlayer: ProxiedPlayer, disciple: ProxiedPlayer): Boolean {
        return YouLongRelationApi.addMaster(proxiedPlayer.name, disciple.name)
    }

    @JvmStatic
    fun removeDisciple(discipleName: String) {
        YouLongRelationApi.removeMaster(discipleName)
    }

    @JvmStatic
    fun getPlayerLevel(proxiedPlayer: ProxiedPlayer): Int {
        return YouLongRelationApi.getPlayerLevel(proxiedPlayer.name)
    }

    @JvmStatic
    fun getPlayerLevel(playerName: String): Int {
        return YouLongRelationApi.getPlayerLevel(playerName)
    }


}


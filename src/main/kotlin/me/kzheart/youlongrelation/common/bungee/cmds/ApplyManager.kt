package me.kzheart.youlongrelation.common.bungee.cmds

import net.md_5.bungee.api.event.ServerConnectedEvent
import net.md_5.bungee.api.event.ServerDisconnectEvent
import taboolib.common.platform.ProxyPlayer
import taboolib.common.platform.event.SubscribeEvent

/**
 * @author kzheart
 * @date 2021/11/9 10:39
 */
object ApplyManager {
    private val discipleApplyList = hashMapOf<String, MutableList<String>>()
    private val friendApplyList = hashMapOf<String, MutableList<String>>()
    private val loverApplyList = hashMapOf<String, MutableList<String>>()
    private val masterApplyList = hashMapOf<String, MutableList<String>>()

    @SubscribeEvent
    fun playerLogin(e: ServerConnectedEvent) {
        discipleApplyList[e.player.name] = mutableListOf()
        friendApplyList[e.player.name] = mutableListOf()
        loverApplyList[e.player.name] = mutableListOf()
        masterApplyList[e.player.name] = mutableListOf()
    }


    @SubscribeEvent
    fun playerDisconnect(e: ServerDisconnectEvent) {
        discipleApplyList.remove(e.player.name)
        friendApplyList.remove(e.player.name)
        loverApplyList.remove(e.player.name)
        masterApplyList.remove(e.player.name)
    }

    fun addLoverApply(player: ProxyPlayer, lover: ProxyPlayer) {
        if (loverApplyList.containsKey(player.name)) {
            loverApplyList[player.name]!!.add(lover.name)
        } else
            loverApplyList[player.name] = mutableListOf(lover.name)
    }

    fun addLoverApply(playerName: String, lover: ProxyPlayer) {
        if (loverApplyList.containsKey(playerName)) {
            loverApplyList[playerName]!!.add(lover.name)
        } else
            loverApplyList[playerName] = mutableListOf(lover.name)
    }

    fun addFriendApply(player: ProxyPlayer, friend: ProxyPlayer) {
        if (friendApplyList.containsKey(friend.name)) {
            friendApplyList[player.name]!!.add(friend.name)
        } else
            friendApplyList[player.name] = mutableListOf(friend.name)
    }

    fun addFriendApply(playerName: String, friend: ProxyPlayer) {
        if (friendApplyList.containsKey(playerName)) {
            friendApplyList[playerName]!!.add(friend.name)
        } else
            friendApplyList[playerName] = mutableListOf(friend.name)
    }

    fun addDiscipleApply(player: ProxyPlayer, master: ProxyPlayer) {
        if (discipleApplyList.containsKey(player.name)) {
            discipleApplyList[player.name]!!.add(master.name)
        } else
            discipleApplyList[player.name] = mutableListOf(master.name)
    }

    fun addDiscipleApply(playerName: String, master: ProxyPlayer) {
        if (discipleApplyList.containsKey(playerName)) {
            discipleApplyList[playerName]!!.add(master.name)
        } else
            discipleApplyList[playerName] = mutableListOf(master.name)
    }

    fun addMasterApply(player: ProxyPlayer, disciple: ProxyPlayer) {
        if (masterApplyList.containsKey(player.name)) {
            masterApplyList[player.name]!!.add(disciple.name)
        } else
            masterApplyList[player.name] = mutableListOf(disciple.name)
    }

    fun addMasterApply(playerName: String, disciple: ProxyPlayer) {
        if (masterApplyList.containsKey(playerName)) {
            masterApplyList[playerName]!!.add(disciple.name)
        } else
            masterApplyList[playerName] = mutableListOf(disciple.name)
    }

    fun removeFriendApply(player: ProxyPlayer, friendName: String) {
        friendApplyList[player.name]?.remove(friendName)
    }

    fun removeDiscipleApply(player: ProxyPlayer, disciple: String) {
        discipleApplyList[player.name]?.remove(disciple)
    }

    fun removeLoverApply(player: ProxyPlayer, loverName: String) {
        loverApplyList[player.name]?.remove(loverName)
    }

    fun removeMasterApply(player: ProxyPlayer, masterName: String) {
        masterApplyList[player.name]?.remove(masterName)
    }

    fun getFriendApply(friend: ProxyPlayer): MutableList<String> {
        return friendApplyList[friend.name] ?: mutableListOf()
    }

    fun getLoverApply(lover: ProxyPlayer): MutableList<String> {
        return loverApplyList[lover.name] ?: mutableListOf()
    }

    fun getMasterApply(master: ProxyPlayer): MutableList<String> {
        return masterApplyList[master.name] ?: mutableListOf()
    }

    fun getDiscipleApply(disciple: ProxyPlayer): MutableList<String> {
        return discipleApplyList[disciple.name] ?: mutableListOf()
    }

    fun hasFriendApply(playerName: String, friend: ProxyPlayer): Boolean {
        return friendApplyList[playerName]?.contains(friend.name) == true
    }

    fun hasMasterApply(playerName: String, master: ProxyPlayer): Boolean {
        return friendApplyList[playerName]?.contains(master.name) == true
    }

    fun hasLoverApply(playerName: String, lover: ProxyPlayer): Boolean {
        return friendApplyList[playerName]?.contains(lover.name) == true
    }

    fun hasDiscipleApply(playerName: String, disciple: ProxyPlayer): Boolean {
        return friendApplyList[playerName]?.contains(disciple.name) == true
    }


}
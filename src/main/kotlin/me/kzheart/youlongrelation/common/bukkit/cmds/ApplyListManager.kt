package me.kzheart.youlongrelation.common.bukkit.cmds

import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerKickEvent
import org.bukkit.event.player.PlayerQuitEvent
import taboolib.common.platform.event.SubscribeEvent

/**
 * @author kzheart
 * @date 2021/11/7 11:57
 */
object ApplyListManager {
    private val friendUpgrade = hashMapOf<String, MutableList<String>>()
    private val loverUpgradeApplyList = hashMapOf<String, String?>()
    private val discipleUpgradeApplyList = hashMapOf<String, MutableList<String>>()
    private val masterUpgradeApplyList = hashMapOf<String, String>()
    private val masterUpgradeReadyList = hashMapOf<String, MutableList<String>>()
    fun getFriendUpgradeList(player: Player): MutableList<String> {
        return friendUpgrade[player.name] ?: return mutableListOf()
    }

    fun getLoverUpgradeApply(player: Player): String? {
        return loverUpgradeApplyList[player.name]
    }

    fun getDiscipleUpgradeApply(player: Player): MutableList<String> {
        return discipleUpgradeApplyList[player.name] ?: return mutableListOf()
    }

    fun getMasterUpgradeApply(player: Player): String? {
        return masterUpgradeApplyList[player.name]
    }

    fun addFriendUpgradeApply(player: Player, friend: Player) {
        friendUpgrade[player.name]?.add(friend.name)
    }

    fun addDiscipleToReady(master: Player, disciple: Player) {
        masterUpgradeReadyList[master.name]?.add(disciple.name)
    }

    fun clearMasterUpgradeApplyList(master: Player) {
        discipleUpgradeApplyList[master.name]?.clear()
    }


    fun addLoverUpgradeApply(player: Player, lover: Player) {
        loverUpgradeApplyList[player.name] = lover.name
    }


    fun addDiscipleUpgradeApply(master: Player, disciple: Player) {
        discipleUpgradeApplyList[master.name]?.add(disciple.name)
    }


    fun addMasterUpgradeApply(disciple: Player, master: Player) {
        masterUpgradeApplyList[disciple.name] = master.name
    }


    fun removeFriendUpgradeApply(player: Player, friend: OfflinePlayer) {
        friendUpgrade[player.name]?.remove(friend.name)
    }


    fun removeLoverUpgradeApply(player: Player) {
        loverUpgradeApplyList.remove(player.name)
    }


    fun removeDiscipleUpgradeApply(master: Player, disciple: OfflinePlayer) {
        discipleUpgradeApplyList[master.name]?.remove(disciple.name)
    }


    fun removeMasterUpgradeApply(disciple: Player) {
        masterUpgradeApplyList.remove(disciple.name)
    }

    fun isFriendApply(player: Player, friend: Player): Boolean {
        return friendUpgrade[player.name]?.contains(friend.name) == true
    }

    fun isLoverApply(player: Player, lover: Player): Boolean {
        return loverUpgradeApplyList[player.name] == lover.name
    }

    fun isMasterApply(player: Player, master: Player): Boolean {
        return masterUpgradeApplyList[player.name] == master.name
    }

    fun isDiscipleApply(player: Player, disciple: Player): Boolean {
        return discipleUpgradeApplyList[player.name]?.contains(disciple.name) == true
    }

    fun getMasterReadyList(master: Player): MutableList<String> {
        return masterUpgradeReadyList[master.name] ?: mutableListOf()
    }


    @SubscribeEvent
    fun onPlayerJoin(e: PlayerJoinEvent) {
        friendUpgrade[e.player.name] = mutableListOf()
        discipleUpgradeApplyList[e.player.name] = mutableListOf()
        masterUpgradeReadyList[e.player.name] = mutableListOf()
    }

    @SubscribeEvent
    fun onPlayerKick(e: PlayerKickEvent) {
        friendUpgrade.remove(e.player.name)
        loverUpgradeApplyList.remove(e.player.name)
        discipleUpgradeApplyList.remove(e.player.name)
        discipleUpgradeApplyList.remove(e.player.name)
        masterUpgradeReadyList.remove(e.player.name)
    }

    @SubscribeEvent
    fun onPlayerQuit(e: PlayerQuitEvent) {
        friendUpgrade.remove(e.player.name)
        loverUpgradeApplyList.remove(e.player.name)
        discipleUpgradeApplyList.remove(e.player.name)
        discipleUpgradeApplyList.remove(e.player.name)
        masterUpgradeReadyList.remove(e.player.name)
    }
}
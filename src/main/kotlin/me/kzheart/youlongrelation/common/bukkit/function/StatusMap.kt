package me.kzheart.youlongrelation.common.bukkit.function

import io.netty.util.internal.ConcurrentSet
import me.kzheart.youlongrelation.api.YouLongRelationBukkitApi
import me.kzheart.youlongrelation.api.event.bukkit.DisturbedCause
import me.kzheart.youlongrelation.api.event.bukkit.PlayerDisturbedEvent
import me.kzheart.youlongrelation.api.event.bukkit.Status
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.*
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.getProxyPlayer
import taboolib.module.lang.sendLang
import java.util.concurrent.ConcurrentHashMap

/**
 * @author kzheart
 * @date 2021/11/7 1:37
 */
object StatusMap {
    private val statusPlayerList = hashSetOf<String>()
    private val friendUpgradingMap = ConcurrentHashMap<String, String>()
    private val loverUpgradingMap = ConcurrentHashMap<String, String>()
    private val masterUpgradingMap = ConcurrentHashMap<String, MutableList<String>>()
    private val discipleUpgradingMap = ConcurrentHashMap<String, String>()
    fun playerIsInStatus(player: Player): Boolean {
        return getPlayerStatus(player) != Status.NONE
    }

    fun getPlayerStatus(player: Player): Status {
        if (statusPlayerList.contains(player.name)) {
            if (friendUpgradingMap.containsKey(player.name)) return Status.FRIEND_UPGRADING
            if (loverUpgradingMap.containsKey(player.name)) return Status.LOVER_UPGRADING
            if (masterUpgradingMap.containsKey(player.name)) return Status.MASTER_UPGRADING
            if (discipleUpgradingMap.containsKey(player.name)) return Status.DISCIPLE_UPGRADING
        }
        return Status.NONE
    }

    fun setPlayerInFriendUpgrading(player: Player, friend: Player) {
        statusPlayerList.add(player.name)
        statusPlayerList.add(friend.name)
        friendUpgradingMap[player.name] = friend.name
        friendUpgradingMap[friend.name] = player.name
    }

    fun setPlayerLoverUpgrading(player: Player, friend: Player) {
        statusPlayerList.add(player.name)
        statusPlayerList.add(friend.name)
        loverUpgradingMap[player.name] = friend.name
        loverUpgradingMap[friend.name] = player.name
    }

    fun setPlayerMasterUpgrading(player: Player, disciples: List<Player>) {
        statusPlayerList.add(player.name)
        disciples.forEach {
            statusPlayerList.add(it.name)
            discipleUpgradingMap[it.name] = player.name
        }
        masterUpgradingMap[player.name] = disciples.map { it.name }.toMutableList()
    }


/*    fun setPlayerDiscipleUpgrading(player: Player, master: Player) {
        statusPlayerList.add(player.name)
        statusPlayerList.add(master.name)
        discipleUpgradingMap[player.name] = master.name
        masterUpgradingMap[master.name] = mutableListOf(player.name)
    }*/


    fun removePlayerFromStatus(player: Player): Status {
        return when (getPlayerStatus(player)) {
            Status.FRIEND_UPGRADING -> {
                val friendName = friendUpgradingMap[player.name]
                statusPlayerList.remove(player.name)
                statusPlayerList.remove(friendName)
                friendUpgradingMap.remove(player.name)
                friendUpgradingMap.remove(friendName)
                Status.FRIEND_UPGRADING
            }
            Status.LOVER_UPGRADING -> {
                val loverName = loverUpgradingMap[player.name]
                loverUpgradingMap.remove(player.name)
                loverUpgradingMap.remove(loverName)
                statusPlayerList.remove(player.name)
                statusPlayerList.remove(loverName)
                Status.LOVER_UPGRADING
            }
            Status.MASTER_UPGRADING -> {
                val discipleList = masterUpgradingMap[player.name]
                discipleList?.forEach {
                    discipleUpgradingMap.remove(it)
                }
                discipleList?.remove(player.name)
                Status.MASTER_UPGRADING
            }
            Status.DISCIPLE_UPGRADING -> {
                val masterName = discipleUpgradingMap[player.name]
                masterUpgradingMap[masterName]?.remove(player.name)
                discipleUpgradingMap.remove(player.name)
                Status.DISCIPLE_UPGRADING
            }
            Status.NONE -> Status.NONE
        }
    }

    fun getPlayerStatusTarget(player: Player): List<String> {
        return when (getPlayerStatus(player)) {
            Status.FRIEND_UPGRADING -> {
                listOf(friendUpgradingMap[player.name]!!)
            }
            Status.LOVER_UPGRADING -> {
                listOf(loverUpgradingMap[player.name]!!)
            }
            Status.MASTER_UPGRADING -> {
                masterUpgradingMap[player.name]!!.toList()
            }
            Status.DISCIPLE_UPGRADING -> {
                listOf(discipleUpgradingMap[player.name]!!)
            }
            Status.NONE -> listOf()
        }
    }


    @SubscribeEvent(ignoreCancelled = true)
    fun onPlayerQuit(e: PlayerQuitEvent) {
        if (playerIsInStatus(e.player)) {
            PlayerDisturbedEvent(
                e.player.name,
                getPlayerStatusTarget(e.player),
                DisturbedCause.QUIT,
                getPlayerStatus(e.player)
            ).call()
            removePlayerFromStatus(e.player)
        }
    }

    @SubscribeEvent(ignoreCancelled = true)
    fun onPlayerKick(e: PlayerQuitEvent) {
        if (playerIsInStatus(e.player)) {
            PlayerDisturbedEvent(
                e.player.name,
                getPlayerStatusTarget(e.player),
                DisturbedCause.QUIT,
                getPlayerStatus(e.player)
            ).call()
            removePlayerFromStatus(e.player)
        }
    }

    @SubscribeEvent(ignoreCancelled = true)
    fun onPlayerTeleport(e: PlayerTeleportEvent) {
        if (playerIsInStatus(e.player)) {
            PlayerDisturbedEvent(
                e.player.name,
                getPlayerStatusTarget(e.player),
                DisturbedCause.QUIT,
                getPlayerStatus(e.player)
            ).call()
            removePlayerFromStatus(e.player)
        }
    }

    @SubscribeEvent(ignoreCancelled = true)
    fun onPlayerCommand(e: PlayerCommandPreprocessEvent) {
        if (playerIsInStatus(e.player)) {
            PlayerDisturbedEvent(
                e.player.name,
                getPlayerStatusTarget(e.player),
                DisturbedCause.QUIT,
                getPlayerStatus(e.player)
            ).call()
            removePlayerFromStatus(e.player)
        }
    }

    @SubscribeEvent(ignoreCancelled = true)
    fun onPlayerChat(e: AsyncPlayerChatEvent) {
        if (playerIsInStatus(e.player)) {
            PlayerDisturbedEvent(
                e.player.name,
                getPlayerStatusTarget(e.player),
                DisturbedCause.QUIT,
                getPlayerStatus(e.player)
            ).call()
            removePlayerFromStatus(e.player)
        }
    }

    @SubscribeEvent(ignoreCancelled = true)
    fun onPlayerDamage(e: EntityDamageEvent) {
        if (e.entity is Player) {
            val player = e.entity as Player
            if (playerIsInStatus(player)) {
                PlayerDisturbedEvent(
                    player.name,
                    getPlayerStatusTarget(player),
                    DisturbedCause.QUIT,
                    getPlayerStatus(player)
                ).call()
                removePlayerFromStatus(player)
            }
        }
    }

    @SubscribeEvent(ignoreCancelled = true)
    fun onPlayerMove(e: PlayerMoveEvent) {
        if (e.from.x == e.to.x && e.from.z == e.to.z && e.from.y == e.to.y) return
        if (playerIsInStatus(e.player)) {
            PlayerDisturbedEvent(
                e.player.name,
                getPlayerStatusTarget(e.player),
                DisturbedCause.QUIT,
                getPlayerStatus(e.player)
            ).call()
            removePlayerFromStatus(e.player)
        }
    }

    @SubscribeEvent(ignoreCancelled = true)
    fun onPlayerDeath(e: PlayerDeathEvent) {
        if (playerIsInStatus(e.entity)) {
            PlayerDisturbedEvent(
                e.entity.name,
                getPlayerStatusTarget(e.entity),
                DisturbedCause.QUIT,
                getPlayerStatus(e.entity)
            ).call()
            removePlayerFromStatus(e.entity)
        }
    }


    @SubscribeEvent(ignoreCancelled = true)
    fun onPlayerInteract(e: PlayerInteractEvent) {
        if (playerIsInStatus(e.player)) {
            if (e.hasItem() || e.hasBlock()) {
                PlayerDisturbedEvent(
                    e.player.name,
                    getPlayerStatusTarget(e.player),
                    DisturbedCause.QUIT,
                    getPlayerStatus(e.player)
                ).call()
                removePlayerFromStatus(e.player)
            }
        }
    }

    @SubscribeEvent
    fun onDisturbedCause(e: PlayerDisturbedEvent) {
        val causeString = when (e.cause) {
            DisturbedCause.DEATH -> "死亡"
            DisturbedCause.DAMAGE -> "攻击或被攻击"
            DisturbedCause.TIME_OVER -> "时间结束"
            DisturbedCause.USE_ITEM -> "与物品交互"
            DisturbedCause.MOVE -> "移动"
            DisturbedCause.CHAT -> "聊天"
            DisturbedCause.COMMAND -> "使用命令"
            DisturbedCause.TELEPORT -> "传送"
            DisturbedCause.QUIT -> "离开"
        }

        val statusString = when (e.status) {
            Status.DISCIPLE_UPGRADING -> "师徒传功"
            Status.LOVER_UPGRADING -> "道侣双修"
            Status.MASTER_UPGRADING -> "师徒传功"
            Status.FRIEND_UPGRADING -> "好友传功"
            else -> "null"
        }
        getProxyPlayer(e.playerName)?.sendLang("status-disturbed", e.playerName, causeString, statusString)
        e.targetName.forEach {
            getProxyPlayer(it)?.sendLang("status-disturbed", e.playerName, causeString, statusString)
        }
    }
}
package me.kzheart.youlongrelation.api.event.bukkit

import org.bukkit.entity.Player
import taboolib.platform.type.BukkitProxyEvent

/**
 * @author kzheart
 * @date 2021/11/6 22:11
 * 朋友之间传功事件 每秒触发一次
 */
class PlayerFriendUpgradeEvent(
    val player: Player,
    val friend: Player,
    val playerAddExp: Double,
    val friendAddExp: Double
) :
    BukkitProxyEvent() {

}
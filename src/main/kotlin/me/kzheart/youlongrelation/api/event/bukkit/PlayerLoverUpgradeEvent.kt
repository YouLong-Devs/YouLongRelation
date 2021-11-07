package me.kzheart.youlongrelation.api.event.bukkit

import org.bukkit.entity.Player
import taboolib.platform.type.BukkitProxyEvent

/**
 * @author kzheart
 * @date 2021/11/7 11:37
 */
class PlayerLoverUpgradeEvent(
    val player: Player,
    val lover: Player,
    val playerAddExp: Double,
    val loverAddExp: Double
) : BukkitProxyEvent() {

}
package me.kzheart.youlongrelation.api.event.bukkit

import taboolib.platform.type.BukkitProxyEvent

/**
 * @author kzheart
 * @date 2021/11/6 12:06
 */
class PlayerDisturbedEvent(
    val playerName: String,
    val targetName: List<String>,
    val cause: DisturbedCause,
    val status: Status
) :
    BukkitProxyEvent() {
}
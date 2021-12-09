package me.kzheart.youlongrelation.api.event.bukkit

import org.bukkit.entity.Player
import taboolib.platform.type.BukkitProxyEvent

/**
 * @author kzheart
 * @date 2021/11/7 16:35
 */
class PlayerMasterUpgradeEvent(val master: Player, val disciples: List<Player>) : BukkitProxyEvent() {

}
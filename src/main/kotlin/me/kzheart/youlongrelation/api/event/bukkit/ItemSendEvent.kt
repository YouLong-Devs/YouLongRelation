package me.kzheart.youlongrelation.api.event.bukkit

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.platform.type.BukkitProxyEvent

/**
 * @author kzheart
 * @date 2021/11/5 18:11
 */
class ItemSendEvent(val sender: Player, val receiver: String, val item: ItemStack) : BukkitProxyEvent() {
}
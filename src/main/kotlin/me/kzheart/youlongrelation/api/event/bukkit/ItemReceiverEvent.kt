package me.kzheart.youlongrelation.api.event.bukkit

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.platform.type.BukkitProxyEvent

/**
 * @author kzheart
 * @date 2021/11/5 18:22
 */
class ItemReceiverEvent(val sender: String, val receiver: Player, val item: ItemStack) : BukkitProxyEvent() {
}
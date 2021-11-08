package me.kzheart.youlongrelation.common.bukkit.gui

import me.kzheart.youlongrelation.api.YouLongRelationBukkitApi
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerKickEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.Inventory
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.submit
import taboolib.library.xseries.XMaterial
import taboolib.module.ui.buildMenu
import taboolib.module.ui.type.Basic
import taboolib.module.ui.type.Linked
import taboolib.module.ui.type.Stored
import taboolib.platform.util.buildItem
import taboolib.platform.util.hasLore
import taboolib.platform.util.inventoryCenterSlots
import taboolib.platform.util.isNotAir

/**
 * @author kzheart
 * @date 2021/11/8 23:02
 */
object SendItemGui {
    @SubscribeEvent
    fun onPlayerJoin(e: PlayerJoinEvent) {
        guiCache[e.player.name] = mutableMapOf()
    }

    @SubscribeEvent
    fun onPlayerQuit(e: PlayerQuitEvent) {
        guiCache.remove(e.player.name)
    }

    @SubscribeEvent
    fun onPlayerKick(e: PlayerKickEvent) {
        guiCache.remove(e.player.name)
    }

    private val guiCache = mutableMapOf<String, MutableMap<String, Inventory>>()
    fun open(player: Player, receiver: String) {
        val inventory = guiCache[player.name]!!.computeIfAbsent(receiver) {
            buildMenu<Basic>("§a赠送物品 §8To: §b$receiver   &cTips: 请及时将物品拿走 否则物品可能消失") {
                rows(1)
                map("Y")
                set('Y', XMaterial.BARRIER) {
                    name = "§a发送物品给 $receiver"
                }
                onClick('Y') { it ->
                    it.inventory.contents.filter { it.isNotAir() }
                        .filter { it.type != Material.BARRIER }
                        .forEach { item ->
                            YouLongRelationBukkitApi.sendGift(player, receiver, item)
                        }
                }
                onClose {
                    if (it.inventory.contents.filter { it.isNotAir() }
                            .filter { it.type != Material.BARRIER }.isNotEmpty()) {
                        submit(delay = 6000) {
                            guiCache[player.name]?.remove(receiver)
                        }
                    } else {
                            guiCache[player.name]?.remove(receiver)
                    }
                }
            }
        }
        player.openInventory(inventory)

    }
}
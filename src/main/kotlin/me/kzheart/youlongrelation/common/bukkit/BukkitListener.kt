package me.kzheart.youlongrelation.common.bukkit

import com.sucy.skill.api.event.PlayerLevelUpEvent
import me.kzheart.youlongrelation.api.YouLongRelationApi
import me.kzheart.youlongrelation.api.YouLongRelationBukkitApi
import me.kzheart.youlongrelation.api.event.bukkit.ItemReceiverEvent
import me.kzheart.youlongrelation.api.event.bukkit.ItemSendEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerLevelChangeEvent
import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide
import taboolib.common.platform.event.SubscribeEvent
import taboolib.module.nms.getName
import taboolib.platform.util.sendLang

/**
 * @author kzheart
 * @date 2021/11/7 14:35
 */
@PlatformSide([Platform.BUKKIT])
object BukkitListener {
    @SubscribeEvent
    fun onPlayerUpLevel(e: PlayerLevelUpEvent) {
        YouLongRelationBukkitApi.updatePlayerLevel(e.playerData.player)
    }

    @SubscribeEvent
    fun onPlayerJoin(e: PlayerJoinEvent) {
        YouLongRelationBukkitApi.updatePlayerLevel(e.player)
    }

    @SubscribeEvent
    fun onItemSend(e: ItemSendEvent) {
        e.sender.sendLang("item-sender", e.receiver, e.item.getName(), e.item.amount)
    }

    @SubscribeEvent
    fun onItemReceive(e: ItemReceiverEvent) {
        e.receiver.sendLang("item-receiver", e.item.getName(), e.item.amount)
    }
}
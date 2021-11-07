package me.kzheart.youlongrelation.api

import com.sucy.skill.SkillAPI
import me.kzheart.youlongrelation.YouLongRelation
import me.kzheart.youlongrelation.api.event.bukkit.ItemReceiverEvent
import me.kzheart.youlongrelation.api.event.bukkit.ItemSendEvent
import me.kzheart.youlongrelation.common.util.Players
import me.kzheart.youlongrelation.data.DiscipleData
import me.kzheart.youlongrelation.data.FriendData
import org.bukkit.Bukkit
import org.bukkit.OfflinePlayer
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide
import taboolib.common.platform.function.submit
import taboolib.platform.util.giveItem
import taboolib.platform.util.isAir
import taboolib.platform.util.serializeToByteArray
import java.util.*

/**
 * @author kzheart
 * @date 2021/11/4 13:21
 */
@PlatformSide([Platform.BUKKIT])
object YouLongRelationBukkitApi {

    @JvmStatic
    fun getFriends(player: OfflinePlayer): Map<String, FriendData> {
        return YouLongRelationApi.getFriends(player.name)
    }

    @JvmStatic
    fun getDisciples(player: OfflinePlayer): Map<String, DiscipleData> {
        return YouLongRelationApi.getDisciples(player.name)
    }

    @JvmStatic
    fun setPlayerIntimacy(player: OfflinePlayer, friend: OfflinePlayer, intimacy: Int) {
        YouLongRelationApi.setIntimacy(player.name, friend.name, intimacy)
    }


    @JvmStatic
    fun isFriend(player: OfflinePlayer, target: OfflinePlayer): Boolean {
        return YouLongRelationApi.isFriend(player.name, target.name)
    }

    @JvmStatic
    fun isLover(player: OfflinePlayer, target: OfflinePlayer): Boolean {
        return YouLongRelationApi.isLover(player.name, target.name)
    }

    @JvmStatic
    fun isMentoring(player: OfflinePlayer, target: OfflinePlayer): Boolean {
        return YouLongRelationApi.isMentoring(player.name, target.name)
    }

    @JvmStatic
    fun updateFriendUpgradeDate(player: OfflinePlayer) {
        YouLongRelationApi.updateFriendUpgradeDate(player.name)
    }


    @JvmStatic
    fun getFriendLastUpgradeDate(player: OfflinePlayer): Date? {
        return YouLongRelationApi.getFriendLastUpgradeDate(player.name)
    }


    @JvmStatic
    fun getFriendUpgradeRemainTime(player: OfflinePlayer): Int {
        return YouLongRelationApi.getFriendUpgradeRemainTime(player.name)
    }


    @JvmStatic
    fun setFriendUpgradeRemainTime(player: Player, remainingTime: Int) {
        YouLongRelationApi.setFriendUpgradeRemainTime(player.name, remainingTime)
    }


    @JvmStatic
    fun updateLoverUpgradeDate(player: OfflinePlayer) {
        YouLongRelationApi.updateLoverUpgradeDate(player.name)
    }

    @JvmStatic
    fun getLoverUpgradeRemainTime(player: OfflinePlayer): Int {
        return YouLongRelationApi.getLoverUpgradeRemainTime(player.name)
    }

    @JvmStatic
    fun setLoverUpgradeRemainTime(player: OfflinePlayer, remainingTime: Int) {
        YouLongRelationApi.setLoverUpgradeRemainTime(player.name, remainingTime)
    }

    @JvmStatic
    fun updatePlayerLevel(player: OfflinePlayer) {
        val level = SkillAPI.getPlayerData(player).mainClass?.level ?: 0
        YouLongRelationApi.setPlayerLevel(player.name, level)
    }

    @JvmStatic
    fun getMasterUpgradeRemainTime(player: OfflinePlayer): Int {
        return YouLongRelationApi.getLoverUpgradeRemainTime(player.name)
    }

    @JvmStatic
    fun setMasterUpgradeRemainTime(player: OfflinePlayer, remainingTime: Int) {
        return YouLongRelationApi.setMasterUpgradeRemainTime(player.name, remainingTime)
    }

    @JvmStatic
    fun updateMasterUpgradeDate(player: OfflinePlayer) {
        return YouLongRelationApi.updateMasterUpgradeDate(player.name)
    }

    @JvmStatic
    fun getPlayerLevel(player: OfflinePlayer): Int {
        return YouLongRelationApi.getPlayerLevel(player.name)
    }

    /**
     * 一个跨服赠送物品的API
     * 该api使用会触发SendItemEvent
     * 有多个物品则会触发多次SendItemEvent
     */
    @JvmStatic
    fun sendGift(sender: Player, receiver: String, vararg itemStacks: ItemStack?) {
        if (sender.name == receiver) {
            return
        }
        if (!Players.isPlayerOnline(receiver))
            return
        submit(async = true) {
            itemStacks.forEach {
                if (it == null || it.isAir())
                    return@forEach
                val event = ItemSendEvent(sender, receiver, it)
                //判断是否在同一个服务器
                if (Bukkit.getPlayer(receiver) != null) {
                    Bukkit.getPlayer(receiver).run {
                        if (event.call()) {
                            giveItem(it.clone())
                            it.amount = 0
                            ItemReceiverEvent(sender.name, this, it.clone())
                        }
                    }
                } else {
                    val itemBytes = it.serializeToByteArray()
                    val receiverBytes = receiver.toByteArray()
                    val senderBytes = sender.name.toByteArray()
                    val bytes = mutableListOf<Byte>()
                    //bytes 第一个Byte用于记录receiver的Bytes的大小
                    bytes.add(0, receiverBytes.size.toByte())


                    //bytes 第二个Byte用于记录sender的Bytes大小
                    bytes.add(1, senderBytes.size.toByte())

                    bytes.addAll(receiverBytes.toMutableList())

                    bytes.addAll(senderBytes.toMutableList())

                    bytes.addAll(itemBytes.toMutableList())

                    //再次进行判断 害怕.jpg
                    if (Players.isPlayerOnline(receiver)) {
                        if (event.call()) {
                            sender.sendPluginMessage(YouLongRelation.plugin, "YLRelation:item", bytes.toByteArray())
                            it.amount = 0
                        }
                    }
                }
            }
        }
    }


}
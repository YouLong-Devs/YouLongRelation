package me.kzheart.youlongrelation.common.bukkit.cmds

import me.kzheart.youlongrelation.YouLongRelation
import me.kzheart.youlongrelation.api.YouLongRelationApi
import me.kzheart.youlongrelation.api.YouLongRelationBukkitApi
import me.kzheart.youlongrelation.common.bukkit.function.FriendEachUpgrade
import me.kzheart.youlongrelation.common.bukkit.gui.FriendGui
import me.kzheart.youlongrelation.common.bukkit.gui.SendItemGui
import me.kzheart.youlongrelation.common.util.Players
import org.apache.commons.lang3.time.DateUtils
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide
import taboolib.common.platform.command.*
import taboolib.common.platform.function.getProxyPlayer
import taboolib.common.platform.function.submit
import taboolib.expansion.createHelper
import taboolib.module.lang.sendLang
import taboolib.module.nms.getName
import taboolib.platform.util.*
import taboolib.type.BukkitEquipment
import java.util.*

/**
 * @author kzheart
 * @date 2021/11/4 19:41
 */
@PlatformSide([Platform.BUKKIT])
@CommandHeader("friendbukkit", permission = "youlongrelation.use.friendbukkit")
object FriendBukkitCommand {
    @CommandBody
    val main = mainCommand {
        createHelper()
    }

    @CommandBody(permission = "youlongrelation.use.friendbukkit.gui")
    val gui = subCommand {
        execute<Player> { sender, context, argument ->
            FriendGui.open(sender)
        }
    }

    @CommandBody(permission = "youlongrelation.use.friend.gift")
    val gift = subCommand {
        literal("send") {
            dynamic {
                suggestion<Player> { sender, _ ->
                    Players.getPlayers().filter { it != sender.name }
                        .filter { YouLongRelationBukkitApi.getFriends(sender).containsKey(it) }
                }
                execute<Player> { sender, context, argument ->
                    if (Players.isPlayerOnline(argument))
                        return@execute sender.sendLang("player-not-online", argument)
                    YouLongRelationBukkitApi.sendGift(sender, argument)
                    //sendGift(sender, argument, BukkitEquipment.getItems(sender)[BukkitEquipment.HAND])
                }
            }
        }
        literal("gui") {
            dynamic {
                suggestion<Player> { sender, context ->
                    Players.getPlayers().filter { YouLongRelationBukkitApi.isFriend(sender, it) }
                }
                execute<Player> { sender, context, argument ->
                    SendItemGui.open(sender, argument)
                }
            }
        }
    }

    @CommandBody(permission = "youlongrelation.use.friend.upgrade")
    val upgrade = subCommand {

        literal("with") {
            dynamic {
                suggestion<Player> { sender, context ->
                    sender.getNearbyEntities(10.0, 10.0, 10.0).filterIsInstance<Player>().map { it }
                        .filter { YouLongRelationBukkitApi.isFriend(sender, it) }.map { it.name }
                }
                execute<Player> { sender, _, argument ->
                    val friend =
                        Bukkit.getPlayer(argument) ?: return@execute sender.sendLang("player-not-online", argument)
                    val friendAllow = YouLongRelationBukkitApi.getFriendUpgradeRemainTime(friend) > 0
/*                YouLongRelationBukkitApi.getFriendLastUpgradeDate(friend) == null || !DateUtils.isSameDay(
                    YouLongRelationBukkitApi.getFriendLastUpgradeDate(friend),
                    Date()
                ) &&*/

                    val playerAllow = YouLongRelationBukkitApi.getFriendUpgradeRemainTime(sender) > 0
/*                    (YouLongRelationBukkitApi.getFriendLastUpgradeDate(sender) == null || !DateUtils.isSameDay(
                        YouLongRelationBukkitApi.getFriendLastUpgradeDate(sender),
                        Date()
                    )) && */

                    if (!friendAllow) {
                        sender.sendLang("friend-no-time-upgrade", friend.name)
                        friend.sendLang("friend-no-time-upgrade", friend.name)
                        return@execute
                    }

                    if (!playerAllow) {
                        sender.sendLang("friend-no-time-upgrade", sender.name)
                        friend.sendLang("friend-no-time-upgrade", sender.name)
                        return@execute
                    }

                    friend.sendLang("friend-upgrade-receiver", sender.name)
                    sender.sendLang("friend-upgrade-sender", friend.name)
                    ApplyListManager.addFriendUpgradeApply(friend, sender)

                }
            }
        }
        literal("accept") {
            dynamic {
                suggestion<Player> { sender, context ->
                    sender.getNearbyEntities(10.0, 10.0, 10.0).asSequence().filterIsInstance<Player>().map { it }
                        .filter { YouLongRelationBukkitApi.isFriend(sender, it) }
                        .filter { ApplyListManager.isFriendApply(sender, it) }.map { it.name }.toList()
                }
                execute<Player> { sender, _, argument ->
                    val friend =
                        Bukkit.getPlayer(argument) ?: return@execute sender.sendLang("player-not-online", argument)
                    ApplyListManager.removeFriendUpgradeApply(sender, friend)
                    val friendAllow = YouLongRelationBukkitApi.getFriendUpgradeRemainTime(friend) > 0
/*                YouLongRelationBukkitApi.getFriendLastUpgradeDate(friend) == null || !DateUtils.isSameDay(
                    YouLongRelationBukkitApi.getFriendLastUpgradeDate(friend),
                    Date()
                ) &&*/

                    val playerAllow = YouLongRelationBukkitApi.getFriendUpgradeRemainTime(sender) > 0
/*                    (YouLongRelationBukkitApi.getFriendLastUpgradeDate(sender) == null || !DateUtils.isSameDay(
                        YouLongRelationBukkitApi.getFriendLastUpgradeDate(sender),
                        Date()
                    )) && */

                    if (!friendAllow) {
                        sender.sendLang("friend-no-time-upgrade", friend.name)
                        friend.sendLang("friend-no-time-upgrade", friend.name)
                        return@execute
                    }

                    if (!playerAllow) {
                        sender.sendLang("friend-no-time-upgrade", sender.name)
                        friend.sendLang("friend-no-time-upgrade", sender.name)
                        return@execute
                    }
                    FriendEachUpgrade.start(sender, friend)
                }
            }
        }
        literal("deny") {
            dynamic {
                suggestion<Player> { sender, context ->
                    ApplyListManager.getFriendUpgradeList(sender)
                }
                execute<Player> { sender, _, argument ->
                    ApplyListManager.removeFriendUpgradeApply(sender, Bukkit.getOfflinePlayer(argument))
                    getProxyPlayer(argument)?.sendLang("friend-upgrade-deny-receiver", sender.name)
                    sender.sendLang("friend-upgrade-deny-sender", argument)
                }
            }
        }
    }
}


private fun sendGift(sender: Player, receiver: String, vararg itemStacks: ItemStack?) {
    if (sender.name == receiver) {
        return sender.sendLang("receiver-is-self")
    }
    if (!Players.isPlayerOnline(receiver))
        return sender.sendLang("player-not-online", receiver)
    submit(async = true) {
        itemStacks.forEach {
            if (it == null || it.isAir())
                return@forEach
            //判断是否在同一个服务器
            if (Bukkit.getPlayer(receiver) != null) {
                Bukkit.getPlayer(receiver).run {
                    giveItem(it.clone())
                    sendLang("item-receiver", sender.name, it.getName(), it.amount)
                    sender.sendLang("item-sender", receiver, it.getName(), it.amount)
                    it.amount = 0
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
                    sender.sendPluginMessage(YouLongRelation.plugin, "YLRelation:item", bytes.toByteArray())
                    sender.sendLang("item-sender", receiver, it.getName(), it.amount)
                    it.amount = 0

                } else {
                    sender.sendLang("player-not-online", receiver)
                }
            }
        }
    }
}


/*        literal("receive") {
            dynamic {
                suggestion<ProxyPlayer> { sender, context ->
                    val giftsData = sender.getDataContainer()["gifts"].deserializeGifts()
                    giftsData.filter { it.value.itemsJson.size != 0 }.map { it.key }
                }
                execute<ProxyPlayer> { sender, _, argument ->
                    val playersGiftsData = sender.getDataContainer()["gifts"].deserializeGifts()
                    val giftData = playersGiftsData[argument]
                    submit(async = true) {
                        giftData!!.itemsJson.forEach {
                            val item = ItemHelper.fromJson(it) ?: return@forEach
                            sender.cast<Player>().giveItem(item)
                        }

                        giftData.itemsJson.clear()
                        playersGiftsData[argument] = giftData
                        sender.getDataContainer()["gifts"] = playersGiftsData.serializeString()
                    }
                }
            }
        }*/






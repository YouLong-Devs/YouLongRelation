package me.kzheart.youlongrelation.common.bungee

import com.google.common.io.ByteStreams
import me.kzheart.youlongrelation.YouLongRelation
import me.kzheart.youlongrelation.api.event.bukkit.ItemReceiverEvent
import me.kzheart.youlongrelation.common.util.Players.setPlayers
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import org.bukkit.plugin.messaging.PluginMessageListener
import taboolib.common.platform.function.submit
import taboolib.module.nms.getName
import taboolib.module.porticus.common.MessageBuilder
import taboolib.module.porticus.common.MessageReader
import taboolib.platform.util.deserializeToItemStack
import taboolib.platform.util.giveItem
import taboolib.platform.util.sendLang
import java.io.IOException
import java.util.*

/**
 * @author kzheart
 * @date 2021/11/2 20:25
 */
class Bungee : PluginMessageListener {
    override fun onPluginMessageReceived(channel: String, player: Player, message: ByteArray) {
        val data = ByteStreams.newDataInput(message)

        if (channel == "BungeeCord") {
            try {
                val subChannel = data.readUTF()
                if (subChannel == "PlayerList") {
                    data.readUTF() // server
                    setPlayers(data.readUTF().split(", "))
                }
            } catch (ignored: IOException) {
            }
        }
        if (channel == "YLRelation:reply") {
            try {
                val data = MessageReader.read(message).build()
                handle(player, data)
            } catch (ignored: IOException) {

            }
        }

        if (channel == "YLRelation:item") {
            try {
                val bytes = message.toMutableList()
                val receiverByteSize = bytes[0]
                val senderByteSize = bytes[1]
                val receiverName =
                    ByteStreams.newDataInput(bytes.subList(2, receiverByteSize.toInt() + 2).toByteArray()).readLine()

                val sendName =
                    ByteStreams.newDataInput(
                        bytes.subList(
                            receiverByteSize.toInt() + 2,
                            receiverByteSize.toInt() + 2 + senderByteSize
                        ).toByteArray()
                    ).readLine()
                val itemStack = bytes.subList(senderByteSize.toInt() + 2 + receiverByteSize, bytes.size).toByteArray()
                    .deserializeToItemStack()
                //此时说明接收的玩家离线了 直接发送物品给自己
                if (sendName == receiverName) {
                    Bukkit.getPlayer(sendName)?.run {
                        giveItem(itemStack)
                        this.sendLang("receiver-not-online")
                    }
                    return
                }

                Bukkit.getPlayer(receiverName)?.run {
                    ItemReceiverEvent(sendName, this, itemStack)
                    giveItem(itemStack)
                    this.sendLang("item-receiver", sendName, itemStack.getName(), itemStack.amount)
                }

            } catch (ignored: IOException) {

            }
        }
    }

    private fun handle(player: Player, data: Array<String>) {
        when (data[0]) {
            "apply" -> {
            }
        }
    }


    companion object {

        fun init() {
            if (!Bukkit.getMessenger().isOutgoingChannelRegistered(YouLongRelation.plugin, "BungeeCord")) {
                Bukkit.getMessenger().registerOutgoingPluginChannel(YouLongRelation.plugin, "BungeeCord")
                Bukkit.getMessenger().registerIncomingPluginChannel(YouLongRelation.plugin, "BungeeCord", Bungee())
            }
            //注册发送信息通道
            if (!Bukkit.getMessenger().isOutgoingChannelRegistered(YouLongRelation.plugin, "YLRelation:action")) {
                Bukkit.getMessenger().registerOutgoingPluginChannel(YouLongRelation.plugin, "YLRelation:action")
            }
            //注册返回信息通道
            if (!Bukkit.getMessenger().isIncomingChannelRegistered(YouLongRelation.plugin, "YLRelation:reply")) {
                Bukkit.getMessenger()
                    .registerIncomingPluginChannel(YouLongRelation.plugin, "YLRelation:reply", Bungee())
            }

            //注册物品传输通道
            if (!Bukkit.getMessenger().isIncomingChannelRegistered(YouLongRelation.plugin, "YLRelation:item")) {
                Bukkit.getMessenger()
                    .registerIncomingPluginChannel(YouLongRelation.plugin, "YLRelation:item", Bungee())
                Bukkit.getMessenger()
                    .registerOutgoingPluginChannel(YouLongRelation.plugin, "YLRelation:item")
            }
        }

        fun sendActionMessage(player: Player, vararg args: String) {
            submit(async = true) {
                try {
                    for (bytes in MessageBuilder.create(arrayOf(UUID.randomUUID().toString(), *args))) {
                        player.sendPluginMessage(YouLongRelation.plugin, "YLRelation:action", bytes)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

        fun sendBungeeData(player: Player, vararg args: String) {
            val out = ByteStreams.newDataOutput()
            for (arg in args) {
                try {
                    out.writeUTF(arg)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            player.sendPluginMessage(YouLongRelation.plugin, "BungeeCord", out.toByteArray())
        }
    }


}


package me.kzheart.youlongrelation.common.bungee

import com.google.common.io.ByteStreams
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.event.PluginMessageEvent
import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.getProxyPlayer
import taboolib.common.platform.function.submit
import taboolib.module.lang.sendLang
import taboolib.module.porticus.common.MessageBuilder
import taboolib.module.porticus.common.MessageReader
import java.io.IOException
import java.lang.reflect.Proxy
import java.util.*

/**
 * @author kzheart
 * @date 2021/11/3 13:33
 */
@PlatformSide([Platform.BUNGEE])
object ListenerBungeeTransfer {

    @SubscribeEvent
    fun onTransfer(e: PluginMessageEvent) {
        if (e.isCancelled) {
            return
        }
        if (e.tag == "YLRelation:action") {
            try {
                val message = MessageReader.read(e.data)
                if (message.isCompleted) {
                    val data = message.build()
                    handle(data)
                }
            } catch (ignored: IOException) {
            }
        }

        if (e.tag == "YLRelation:item") {
            try {
                val bytes = e.data.toMutableList()
                //名字所占byte的大小
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
                val itemStackByte = bytes.subList(senderByteSize.toInt() + 2 + receiverByteSize, bytes.size)

                ProxyServer.getInstance().getPlayer(receiverName).run {
                    if (this != null)
                        server.info.sendData("YLRelation:item", e.data)
                    //如果此时玩家突然离线了 将物品重新发送给发送者 如果发送者也寄了 就拉到把 我懒得管了 判断这一堆我已经想吐了
                    else {

                        ProxyServer.getInstance().getPlayer(sendName)?.run {
                            val newData = mutableListOf<Byte>()
                            //这段信息代表 发送者和接收者都是发送者自己
                            newData.add(0, senderByteSize)
                            newData.add(1, senderByteSize)
                            newData.addAll(
                                bytes.subList(
                                    receiverByteSize.toInt() + 2,
                                    receiverByteSize.toInt() + 2 + senderByteSize
                                )
                            )
                            newData.addAll(
                                bytes.subList(
                                    receiverByteSize.toInt() + 2,
                                    receiverByteSize.toInt() + 2 + senderByteSize
                                )
                            )
                            newData.addAll(itemStackByte)
                            server.info.sendData("YLRelation:item", newData.toByteArray())
                        }
                    }

                }

            } catch (ignored: IOException) {
            }
        }
    }

    private fun handle(data: Array<String>) {
        when (data[0]) {
            "apply" -> {
                when (data[1]) {
                    "friend" -> {
                        val target = data[2]
                        val applyName = data[3]
                        sendReply(ProxyServer.getInstance().getPlayer(target), "apply", "friend", applyName)
                    }
                }
            }
            "sendLang" -> {
                val target = data[1]
                val node = data[2]
                val args = data.copyOfRange(3, data.size)
                getProxyPlayer(target)?.sendLang(node, *args)
            }
            "accept" -> {
                when (data[1]) {
                    "friend" -> {
                        val target = data[2]
                        val applyName = data[3]
                        sendReply(ProxyServer.getInstance().getPlayer(target), "accept", "friend", applyName)
                        sendReply(ProxyServer.getInstance().getPlayer(applyName), "accept", "friend", target)
                    }
                }
            }
        }


    }

    private fun sendReply(proxiedPlayer: ProxiedPlayer, vararg args: String) {
        submit(async = true) {
            try {
                for (bytes in MessageBuilder.create(arrayOf(UUID.randomUUID().toString(), *args))) {
                    proxiedPlayer.server.info.sendData("YLRelation:reply", bytes)
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}
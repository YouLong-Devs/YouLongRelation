package me.kzheart.youlongrelation.common.bungee.cmds

import me.kzheart.youlongrelation.api.YouLongRelationBungeeApi
import me.kzheart.youlongrelation.common.bungee.conf.BungeeLoverConfManager
import me.kzheart.youlongrelation.data.deserializeLover
import me.kzheart.youlongrelation.database.getDataContainer
import me.kzheart.youlongrelation.database.getDataContainerByName
import net.md_5.bungee.api.connection.ProxiedPlayer
import net.md_5.bungee.api.event.ServerConnectedEvent
import net.md_5.bungee.api.event.ServerDisconnectEvent
import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide
import taboolib.common.platform.ProxyPlayer
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.getProxyPlayer
import taboolib.common.platform.function.onlinePlayers
import taboolib.expansion.createHelper
import taboolib.module.lang.sendLang

/**
 * @author kzheart
 * @date 2021/11/4 15:32
 */
@PlatformSide([Platform.BUNGEE])
@CommandHeader("lover", permission = "youlongrelation.lover.use")
object LoverCommand {


    @CommandBody(permission = "youlongrelation.lover.use")
    val main = mainCommand {
        createHelper()
    }

    @CommandBody(permission = "youlongrelation.lover.use.marry")
    val marry = subCommand {
        dynamic {
            suggestion<ProxyPlayer> { sender, _ ->
                onlinePlayers().map { it.name }
                    .filter {
                        YouLongRelationBungeeApi.getIntimacy(
                            sender.cast<ProxiedPlayer>(),
                            it
                        ) >= BungeeLoverConfManager.needIntimacy
                    }
            }
            execute<ProxyPlayer> { sender, _, argument ->
                val loverData = getProxyPlayer(argument)!!.getDataContainer()["lover"].deserializeLover()
                if (loverData != null) {
                    return@execute sender.sendLang("lover-already-marry", argument)
                }
                if (ApplyManager.hasLoverApply(argument, sender))
                    return@execute sender.sendLang("lover-already-apply-sender", argument)
                ApplyManager.addLoverApply(argument, sender)
                getProxyPlayer(argument)?.sendLang("lover-apply-receiver", sender.name)
                return@execute sender.sendLang("lover-apply-success", argument)
            }
        }
    }

    @CommandBody(permission = "youlongrelation.lover.use.marry")
    val check = subCommand {
        execute<ProxyPlayer> { sender, context, argument ->
            val loverData = YouLongRelationBungeeApi.getLover(sender.cast<ProxiedPlayer>())
                ?: return@execute sender.sendLang("lover-not-exist")
            val proxyPlayer = getProxyPlayer(loverData.name)
            val onlineStats = if (proxyPlayer != null) "§a在线" else "§8离线"
            sender.sendLang("lover-check", loverData.name, onlineStats)
        }
    }

    @CommandBody(permission = "youlongrelation.lover.use.marry")
    val accept = subCommand {
        dynamic {
            suggestion<ProxyPlayer> { sender, context ->
                return@suggestion ApplyManager.getLoverApply(sender)
            }
            execute<ProxyPlayer> { sender, context, argument ->
                ApplyManager.removeLoverApply(sender, argument)
                val loverData = YouLongRelationBungeeApi.getLover(argument)
                argument.getDataContainerByName()["lover"].deserializeLover()
                //申请人已经有道侣
                if (loverData != null) {
                    return@execute sender.sendLang("lover-already-marry", argument)
                }
                val senderData = YouLongRelationBungeeApi.getLover(sender.cast<ProxiedPlayer>())
                //玩家自己已经有道侣
                if (senderData != null) {
                    return@execute sender.sendLang("lover-already-marry", sender.name)
                }
                runCatching {
                    YouLongRelationBungeeApi.addLover(sender.cast(), argument)
                }.onSuccess {
                    sender.sendLang("lover-accept", argument)
                    getProxyPlayer(argument)?.sendLang("lover-accept", sender.name)
                }.onFailure {
                    it.printStackTrace()
                    sender.sendLang("unknown-error")
                }
            }
        }
    }

    @CommandBody(permission = "youlongrelation.lover.use.marry")
    val deny = subCommand {
        dynamic {
            suggestion<ProxyPlayer> { sender, context ->
                return@suggestion ApplyManager.getLoverApply(sender)
            }
            execute<ProxyPlayer> { sender, context, argument ->
                ApplyManager.removeLoverApply(sender, argument)
                sender.sendLang("lover-deny-apply-sender", argument)
                getProxyPlayer(argument)?.sendLang("lover-deny-apply-receiver", sender.name)
            }
        }
    }

    @CommandBody(permission = "youlongrelation.lover.use.marry")
    val divorce = subCommand {
        execute<ProxyPlayer> { sender, context, argument ->
            val loverData = YouLongRelationBungeeApi.getLover(sender.cast<ProxiedPlayer>())
                ?: return@execute sender.sendLang("lover-not-exist")
            sender.sendLang("lover-divorce-confirm", loverData.name)
        }
    }


    @CommandBody(permission = "youlongrelation.lover.use.marry")
    val divorceConfirm = subCommand {
        execute<ProxyPlayer> { sender, context, argument ->
            //判断是否有道侣
            val loverData = YouLongRelationBungeeApi.getLover(sender.cast<ProxiedPlayer>())
                ?: return@execute sender.sendLang("lover-not-exist")

            //删除道侣
            runCatching {
                YouLongRelationBungeeApi.removeLover(sender.cast())
            }.onSuccess {
                sender.sendLang("lover-divorce", loverData.name)
                getProxyPlayer(loverData.name)?.sendLang("lover-divorce", sender.name)
            }.onFailure {
                it.printStackTrace()
                return@execute sender.sendLang("unknown-error")
            }
        }
    }

}




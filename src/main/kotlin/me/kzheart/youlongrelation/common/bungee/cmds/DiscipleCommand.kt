package me.kzheart.youlongrelation.common.bungee.cmds

import me.kzheart.youlongrelation.api.YouLongRelationBungeeApi
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
 * @date 2021/11/6 0:50
 */
@PlatformSide([Platform.BUNGEE])
@CommandHeader("disciple", permission = "youlongrelation.disciple.use")
object DiscipleCommand {

    @CommandBody(permission = "youlongrelation.disciple.use")
    val main = mainCommand {
        createHelper()
    }

    @CommandBody(permission = "youlongrelation.disciple.use.common")
    val check = subCommand {
        execute<ProxyPlayer> { sender, context, argument ->
            val disciples = YouLongRelationBungeeApi.getDisciples(sender.cast<ProxiedPlayer>())
            if (disciples.isEmpty())
                return@execute sender.sendLang("disciple-not-exist")
            sender.sendLang("disciple-check", disciples.keys)
        }
    }

    @CommandBody(permission = "youlongrelation.disciple.use.common")
    val add = subCommand {
        dynamic {
            suggestion<ProxyPlayer> { sender, _ ->
                onlinePlayers().map { it.name }.filter { it != sender.name }
            }
            execute<ProxyPlayer> { sender, _, argument ->

                val disciples = YouLongRelationBungeeApi.getDisciples(sender.cast<ProxiedPlayer>())
                if (disciples.size >= MasterCommand.maxDiscipleCount)
                    return@execute sender.sendLang("master-already-maxcount", sender.name)

                val masterData = YouLongRelationBungeeApi.getMaster(sender.cast<ProxiedPlayer>())
                if (masterData != null)
                    return@execute sender.sendLang("master-already-exist", argument)

                if (ApplyManager.hasDiscipleApply(argument, sender))
                    return@execute sender.sendLang("disciple-already-apply-sender", argument)

                ApplyManager.addDiscipleApply(argument, sender)
                getProxyPlayer(argument)?.sendLang("disciple-apply-receiver", sender.name)
                return@execute sender.sendLang("disciple-apply-success", argument)
            }
        }
    }


    @CommandBody(permission = "youlongrelation.disciple.use.common")
    val accept = subCommand {
        dynamic {
            suggestion<ProxyPlayer> { sender, _ ->
                return@suggestion ApplyManager.getDiscipleApply(sender)
            }
            execute<ProxyPlayer> { sender, _, argument ->
                ApplyManager.removeDiscipleApply(sender, argument)
                val disciples = YouLongRelationBungeeApi.getDisciples(sender.cast<ProxiedPlayer>())
                if (disciples.size >= MasterCommand.maxDiscipleCount)
                    return@execute sender.sendLang("master-already-maxcount", sender.name)

                val masterData = YouLongRelationBungeeApi.getMaster(sender.cast<ProxiedPlayer>())
                if (masterData != null)
                    return@execute sender.sendLang("master-already-exist", argument)

                runCatching {
                    if (YouLongRelationBungeeApi.addDisciple(argument, sender.cast())) {
                        getProxyPlayer(argument)?.sendLang("master-accept", sender.name)
                        return@execute sender.sendLang("master-accept", argument)
                    } else
                        return@execute sender.sendLang("unknown-error")
                }.onFailure {
                    return@execute sender.sendLang("unknown-error")
                }
            }
        }
    }

    @CommandBody(permission = "youlongrelation.disciple.use.common")
    val deny = subCommand {
        dynamic {
            suggestion<ProxyPlayer> { sender, context ->
                return@suggestion ApplyManager.getDiscipleApply(sender)
            }
            execute<ProxyPlayer> { sender, context, argument ->
                ApplyManager.removeDiscipleApply(sender, argument)
                sender.sendLang("disciple-deny-apply-sender", argument)
                getProxyPlayer(argument)?.sendLang("disciple-deny-apply-receiver", sender.name)
            }
        }
    }

    @CommandBody(permission = "youlongrelation.disciple.use.common")
    val remove = subCommand {
        dynamic {
            suggestion<ProxyPlayer> { sender, context ->
                YouLongRelationBungeeApi.getDisciples(sender.cast<ProxiedPlayer>()).map { it.key }
            }
            execute<ProxyPlayer> { sender, context, argument ->
                sender.sendLang("disciple-leave-confirm", argument)
            }
        }
    }

    @CommandBody(permission = "youlongrelation.disciple.use.common")
    val removeConfirm = subCommand {
        dynamic {
            suggestion<ProxyPlayer> { sender, context ->
                YouLongRelationBungeeApi.getDisciples(sender.cast<ProxiedPlayer>()).map { it.key }
            }
            execute<ProxyPlayer> { sender, context, argument ->
                runCatching {
                    YouLongRelationBungeeApi.removeDisciple(argument)
                }.onSuccess {
                    getProxyPlayer(argument)?.sendLang("master-leave", sender.name)
                    sender.sendLang("master-leave", argument)
                }.onFailure {
                    sender.sendLang("unknown-error", argument)
                }
            }
        }
    }
}
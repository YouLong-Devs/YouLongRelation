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
 * @date 2021/11/4 13:30
 */
@PlatformSide([Platform.BUNGEE])
@CommandHeader("friend", permission = "youlongrelation.friend.use")
object FriendCommand {

    @CommandBody(permission = "youlongrelation.friend.use")
    val main = mainCommand {
        createHelper()
    }

    @CommandBody(permission = "youlongrelation.friend.use")
    val intimacy = subCommand {
        dynamic {
            suggestion<ProxyPlayer> { sender, context ->
                YouLongRelationBungeeApi.getFriends(sender.cast<ProxiedPlayer>()).keys.toList()
            }
            execute<ProxyPlayer> { sender, _, argument ->
                sender.sendLang(
                    "friend-intimacy",
                    argument,
                    YouLongRelationBungeeApi.getIntimacy(sender.name, argument)
                )
            }
        }
    }

    @CommandBody(permission = "youlongrelation.friend.use.common")
    val add = subCommand {
        dynamic {
            suggestion<ProxyPlayer> { sender, _ ->
                onlinePlayers().map { it.name }.filter { it != sender.name }
            }
            execute<ProxyPlayer> { sender, _, argument ->
                val friends = YouLongRelationBungeeApi.getFriends(sender.cast<ProxiedPlayer>())
                //判断是否已经有这个好友了
                if (friends.containsKey(argument))
                    return@execute sender.sendLang("friend-already", argument)
                //判断是否已经申请过
                if (ApplyManager.hasFriendApply(argument, sender))
                    return@execute sender.sendLang("friend-already-apply", argument)
                //如果添加成功 两边都发信息
                ApplyManager.addFriendApply(argument, sender)
                getProxyPlayer(argument)?.sendLang("friend-apply-receiver", sender.name)
                return@execute sender.sendLang("friend-apply-success", argument)
            }
        }
    }

    @CommandBody(permission = "youlongrelation.friend.use.common")
    val accept = subCommand {
        dynamic {
            suggestion<ProxyPlayer> { sender, _ ->
                return@suggestion ApplyManager.getFriendApply(sender)
            }
            execute<ProxyPlayer> { sender, _, argument ->
                ApplyManager.removeFriendApply(sender, argument)
                val friends = YouLongRelationBungeeApi.getFriends(sender.cast<ProxiedPlayer>())
                //判断是否已经是好友
                if (friends.containsKey(argument)) {
                    return@execute sender.sendLang("friend-already", argument)
                }
                runCatching {
                    YouLongRelationBungeeApi.addFriend(sender.cast<ProxiedPlayer>(), argument)
                }.onSuccess {
                    sender.sendLang("friend-accept", argument)
                    getProxyPlayer(argument)?.sendLang("friend-accept", sender.name)
                }.onFailure {
                    it.printStackTrace()
                    sender.sendLang("unknown-error")
                }
            }
        }
    }

    @CommandBody(permission = "youlongrelation.friend.use.common")
    val deny = subCommand {
        dynamic {
            suggestion<ProxyPlayer> { sender, _ ->
                return@suggestion ApplyManager.getFriendApply(sender)
            }
            execute<ProxyPlayer> { sender, _, argument ->
                ApplyManager.removeFriendApply(sender, argument)
                sender.sendLang("friend-deny-apply-sender", argument)
                getProxyPlayer(argument)?.sendLang("friend-deny-apply-receiver", sender.name)
            }
        }
    }

    @CommandBody(permission = "youlongrelation.friend.use.common")
    val remove = subCommand {
        dynamic {
            suggestion<ProxyPlayer> { sender, _ ->
                YouLongRelationBungeeApi.getFriends(sender.cast<ProxiedPlayer>()).map { it.key }
            }
            execute<ProxyPlayer> { sender, _, argument ->
                runCatching {
                    YouLongRelationBungeeApi.removeFriend(sender.cast(), argument)
                }
                    .onSuccess {
                        YouLongRelationBungeeApi.setPlayerIntimacy(sender.cast(), argument, 0)
                        sender.sendLang("friend-remove", argument)
                        getProxyPlayer(argument)?.sendLang("friend-remove", sender.name)
                    }
                    .onFailure {
                        it.printStackTrace()
                        sender.sendLang("unknown-error")
                    }
            }
        }
    }


}
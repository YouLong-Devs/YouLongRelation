package me.kzheart.youlongrelation.common.bungee.cmds

import me.kzheart.youlongrelation.api.YouLongRelationBungeeApi
import me.kzheart.youlongrelation.common.bungee.conf.BungeeMasterDiscipleConfManager
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
 * @date 2021/11/4 16:24
 */
@PlatformSide([Platform.BUNGEE])
@CommandHeader("master", permission = "youlongrelation.master.use")
object MasterCommand {

    val maxDiscipleCount = BungeeMasterDiscipleConfManager.maxDiscipleCount
    private val applyList = hashMapOf<String, MutableList<String>>()

    @CommandBody(permission = "youlongrelation.master.use")
    val main = mainCommand {
        createHelper()
    }

    @CommandBody(permission = "youlongrelation.master.use.apprentice")
    val add = subCommand {
        dynamic {
            suggestion<ProxyPlayer> { sender, context ->
                onlinePlayers().map { it.name }.filter { it != sender.name }
            }
            execute<ProxyPlayer> { sender, _, argument ->
                val masterData = YouLongRelationBungeeApi.getMaster(sender.cast<ProxiedPlayer>())
                if (masterData != null)
                    return@execute sender.sendLang("master-already-exist", sender.name)

                if (YouLongRelationBungeeApi.getDisciples(argument).size >= maxDiscipleCount)
                    return@execute sender.sendLang("master-already-maxcount", argument)
                if (isLevelAllowAddMaster(sender.cast<ProxiedPlayer>(), getProxyPlayer(argument)!!.cast())) {
                    return@execute sender.sendLang("master-disciple-level-not-allow")
                }
                if (applyList[argument]!!.contains(sender.name))
                    return@execute sender.sendLang("master-already-apply-sender", argument)

                if (applyList[argument]?.add(sender.name) == true) {
                    getProxyPlayer(argument)?.sendLang("master-apply-receiver", sender.name)
                    return@execute sender.sendLang("master-apply-success", argument)
                } else return@execute sender.sendLang("unknown-error")
            }
        }
    }

    @CommandBody(permission = "youlongrelation.master.use.apprentice")
    val accept = subCommand {
        dynamic {
            suggestion<ProxyPlayer> { sender, context ->
                return@suggestion applyList[sender.name]
            }
            execute<ProxyPlayer> { sender, context, argument ->
                val masterData = YouLongRelationBungeeApi.getMaster(argument)
                applyList[sender.name]?.remove(argument)
                //已经有师傅了
                if (masterData != null) {
                    return@execute sender.sendLang("master-already-exist", argument)
                }
                //判断师傅数量弟子数量是否超
                if (YouLongRelationBungeeApi.getDisciples(sender.cast<ProxiedPlayer>()).size >= maxDiscipleCount)
                    return@execute sender.sendLang("master-already-maxcount", sender.name)
                if (isLevelAllowAddMaster(argument, sender.cast())) {
                    return@execute sender.sendLang("master-disciple-level-not-allow")
                }
                runCatching {
                    if (YouLongRelationBungeeApi.addMaster(argument, sender.cast())) {
                        sender.sendLang("master-accept", argument)
                        getProxyPlayer(argument)?.sendLang("master-accept", sender.name)
                    } else {
                        sender.sendLang("unknown-error")
                    }
                }.onFailure {
                    it.printStackTrace()
                    sender.sendLang("unknown-error")
                }
            }
        }
    }

    @CommandBody(permission = "youlongrelation.master.use.apprentice")
    val deny = subCommand {
        dynamic {
            suggestion<ProxyPlayer> { sender, context ->
                return@suggestion applyList[sender.name]
            }
            execute<ProxyPlayer> { sender, context, argument ->
                applyList[sender.name]?.remove(argument)
                sender.sendLang("disciple-deny-apply-receiver", argument)
                getProxyPlayer(argument)?.sendLang("master-deny-apply-receiver", sender.name)
            }
        }
    }

    @CommandBody(permission = "youlongrelation.master.use.apprentice")
    val leave = subCommand {
        execute<ProxyPlayer> { sender, context, argument ->
            val masterData = YouLongRelationBungeeApi.getMaster(sender.cast<ProxiedPlayer>())
                ?: return@execute sender.sendLang("master-not-exist")
            sender.sendLang("master-leave-confirm", masterData.master)
        }
    }

    @CommandBody(permission = "youlongrelation.master.use.apprentice")
    val leaveConfirm = subCommand {
        execute<ProxyPlayer> { sender, context, argument ->
            val masterData = YouLongRelationBungeeApi.getMaster(sender.cast<ProxiedPlayer>())
                ?: return@execute sender.sendLang("master-not-exist")
            runCatching {
                YouLongRelationBungeeApi.removeMaster(sender.cast())
            }.onSuccess {
                sender.sendLang("master-leave", masterData.master)
                getProxyPlayer(masterData.master)?.sendLang("master-leave", sender.name)
            }.onFailure {
                it.printStackTrace()
                return@execute sender.sendLang("unknown-error")
            }
        }
    }


    @SubscribeEvent
    fun playerLogin(e: ServerConnectedEvent) {
        applyList[e.player.name] = mutableListOf()
    }


    @SubscribeEvent
    fun playerDisconnect(e: ServerDisconnectEvent) {
        applyList.remove(e.player.name)
    }

    private fun isLevelAllowAddMaster(player: ProxiedPlayer, master: ProxiedPlayer): Boolean {
        return YouLongRelationBungeeApi.getPlayerLevel(master) > YouLongRelationBungeeApi.getPlayerLevel(player)
    }

    private fun isLevelAllowAddMaster(playerName: String, master: ProxiedPlayer): Boolean {
        return YouLongRelationBungeeApi.getPlayerLevel(master) > YouLongRelationBungeeApi.getPlayerLevel(playerName)
    }
}
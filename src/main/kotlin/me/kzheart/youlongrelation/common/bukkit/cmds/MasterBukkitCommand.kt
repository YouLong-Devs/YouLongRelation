package me.kzheart.youlongrelation.common.bukkit.cmds

import me.kzheart.youlongrelation.api.YouLongRelationBukkitApi
import me.kzheart.youlongrelation.common.bukkit.conf.BukkitMasterDiscipleConfManager
import me.kzheart.youlongrelation.common.bukkit.function.MasterDiscipleEachUpgrade
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.function.getProxyPlayer
import taboolib.expansion.createHelper
import taboolib.module.lang.sendLang
import taboolib.platform.util.sendLang

/**
 * @author kzheart
 * @date 2021/11/7 14:58
 */
@PlatformSide([Platform.BUKKIT])
@CommandHeader("masterbukkit", permission = "youlongrelation.use.masterbukkit")
object MasterBukkitCommand {
    val readyStartList = mutableListOf<String>()

    @CommandBody
    val main = mainCommand {
        createHelper()
    }

    @CommandBody(permission = "youlongrelation.use.masterbukkit.upgrade")
    val upgrade = subCommand {
        literal("with") {
            dynamic {
                suggestion<Player> { sender, context ->
                    sender.getNearbyEntities(10.0, 10.0, 10.0).asSequence().filterIsInstance<Player>().map { it }
                        .filter { YouLongRelationBukkitApi.isDisciple(sender, it) }.map { it.name }.toList()
                }
                execute<Player> { sender, _, argument ->
                    val disciple =
                        Bukkit.getPlayer(argument) ?: return@execute sender.sendLang("player-not-online", argument)

                    val playerAllow = YouLongRelationBukkitApi.getMasterUpgradeRemainTime(sender) > 0


                    if (!playerAllow) {
                        sender.sendLang("master-no-time-upgrade", sender.name)
                        disciple.sendLang("master-no-time-upgrade", sender.name)
                        return@execute
                    }
                    if (ApplyListManager.getMasterReadyList(sender).size >= BukkitMasterDiscipleConfManager.count)
                        return@execute sender.sendLang("master-ready-is-full")

                    disciple.sendLang("master-upgrade-receiver", sender.name)
                    sender.sendLang("master-upgrade-sender", disciple.name)
                    ApplyListManager.addMasterUpgradeApply(disciple, sender)
                }
            }
        }
        literal("accept") {
            dynamic {
                suggestion<Player> { sender, context ->
                    sender.getNearbyEntities(10.0, 10.0, 10.0).asSequence().filterIsInstance<Player>().map { it }
                        .filter { YouLongRelationBukkitApi.isDisciple(sender, it) }
                        .filter { ApplyListManager.isDiscipleApply(sender, it) }.map { it.name }.toList()
                }
                execute<Player> { sender, _, argument ->
                    val disciple =
                        Bukkit.getPlayer(argument) ?: return@execute sender.sendLang("player-not-online", argument)
                    ApplyListManager.removeDiscipleUpgradeApply(sender, disciple)
                    val masterAllow = YouLongRelationBukkitApi.getMasterUpgradeRemainTime(sender) > 0

                    if (!masterAllow) {
                        sender.sendLang("master-no-time-upgrade", sender.name)
                        disciple.sendLang("master-no-time-upgrade", sender.name)
                        return@execute
                    }

                    if (ApplyListManager.getMasterReadyList(sender).size >= BukkitMasterDiscipleConfManager.count)
                        return@execute sender.sendLang("master-ready-is-full")
                    ApplyListManager.addDiscipleToReady(sender, disciple)
                    disciple.sendLang("disciple-join-upgrade", sender.name)
                    sender.sendLang("master-join-upgrade", disciple.name)
                }
            }
        }



        literal("deny") {
            dynamic {
                suggestion<Player> { sender, context ->
                    ApplyListManager.getDiscipleUpgradeApply(sender)
                }
                execute<Player> { sender, _, argument ->
                    ApplyListManager.removeDiscipleUpgradeApply(sender, Bukkit.getOfflinePlayer(argument))
                    getProxyPlayer(argument)?.sendLang("master-upgrade-deny-receiver")
                    sender.sendLang("master-upgrade-deny-sender", argument)
                }
            }
        }


        literal("start") {
            execute<Player> { sender, context, argument ->
                if (YouLongRelationBukkitApi.getDisciples(sender).isEmpty())
                    return@execute sender.sendLang("disciple-not-exist")
                if (ApplyListManager.getMasterReadyList(sender).isEmpty())
                    return@execute sender.sendLang("master-ready-is-empty")
                val readyList = ApplyListManager.getMasterReadyList(sender)
                val disciples =
                    sender.getNearbyEntities(10.0, 10.0, 10.0).asSequence().filterIsInstance<Player>().map { it }
                        .filter {
                            YouLongRelationBukkitApi.isDisciple(sender, it)
                        }.filter { readyList.contains(it.name) }.toList()
                ApplyListManager.clearMasterUpgradeReadyList(sender)
                MasterDiscipleEachUpgrade.start(sender, disciples)
            }
        }

        literal("clear") {
            execute<Player> { sender, context, argument ->
                ApplyListManager.clearMasterUpgradeReadyList(sender)
                sender.sendLang("master-ready-clear")
            }
        }
    }
}
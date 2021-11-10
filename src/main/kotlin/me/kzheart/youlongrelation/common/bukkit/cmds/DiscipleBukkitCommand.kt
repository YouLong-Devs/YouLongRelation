package me.kzheart.youlongrelation.common.bukkit.cmds

import me.kzheart.youlongrelation.api.YouLongRelationBukkitApi
import me.kzheart.youlongrelation.common.bukkit.conf.BukkitMasterDiscipleConfManager
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
 * @date 2021/11/7 15:13
 */
@PlatformSide([Platform.BUKKIT])
@CommandHeader("disciplebukkit", permission = "youlongrelation.use.disciplebukkit")
object DiscipleBukkitCommand {
    @CommandBody
    val main = mainCommand {
        createHelper()
    }

    @CommandBody(permission = "youlongrelation.use.disciplebukkit.upgrade")
    val upgrade = subCommand {
        literal("with") {
            dynamic {
                suggestion<Player> { sender, context ->
                    sender.getNearbyEntities(10.0, 10.0, 10.0).asSequence().filterIsInstance<Player>().map { it }
                        .filter { YouLongRelationBukkitApi.isMaster(sender, it) }.map { it.name }.toList()
                }
                execute<Player> { sender, _, argument ->
                    val master =
                        Bukkit.getPlayer(argument) ?: return@execute sender.sendLang("player-not-online", argument)
                    val masterAllow = YouLongRelationBukkitApi.getMasterUpgradeRemainTime(master) > 0
                    if (!masterAllow) {
                        sender.sendLang("master-no-time-upgrade", master.name)
                        return@execute
                    }
                    if (ApplyListManager.getMasterReadyList(sender).size >= BukkitMasterDiscipleConfManager.count)
                        return@execute sender.sendLang("master-ready-is-full")
                    master.sendLang("master-upgrade-apply", sender.name)
                    sender.sendLang("disciple-upgrade-apply", master.name)
                    ApplyListManager.addDiscipleUpgradeApply(master, sender)
                }
            }
        }
        literal("accept") {
            dynamic {
                suggestion<Player> { sender, context ->
                    sender.getNearbyEntities(10.0, 10.0, 10.0).asSequence().filterIsInstance<Player>().map { it }
                        .filter { YouLongRelationBukkitApi.isMaster(sender, it) }
                        .filter { ApplyListManager.isMasterApply(sender, it) }.map { it.name }.toList()
                }
                execute<Player> { sender, _, argument ->

                    val master =
                        Bukkit.getPlayer(argument) ?: return@execute sender.sendLang("player-not-online", argument)
                    ApplyListManager.removeMasterUpgradeApply(sender)
                    val masterAllow = YouLongRelationBukkitApi.getMasterUpgradeRemainTime(master) > 0

                    if (!masterAllow) {
                        sender.sendLang("master-no-time-upgrade", master.name)
                        return@execute
                    }
                    if (ApplyListManager.getMasterReadyList(sender).size >= BukkitMasterDiscipleConfManager.count)
                        return@execute sender.sendLang("master-ready-is-full")

                    ApplyListManager.addDiscipleToReady(master, sender)
                    sender.sendLang("disciple-join-upgrade", master.name)
                    master.sendLang("master-join-upgrade", sender.name)
                }
            }
        }
        literal("deny") {
            dynamic {
                suggestion<Player> { sender, context ->
                    if (ApplyListManager.getMasterUpgradeApply(sender) == null) return@suggestion emptyList<String>()
                    else return@suggestion listOf(ApplyListManager.getMasterUpgradeApply(sender)!!)
                }
                execute<Player> { sender, _, argument ->
                    ApplyListManager.removeMasterUpgradeApply(sender)
                    sender.sendLang("disciple-upgrade-deny-sender", argument)
                    getProxyPlayer(argument)?.sendLang("disciple-upgrade-deny-receiver", sender.name)

                }
            }
        }
    }
}
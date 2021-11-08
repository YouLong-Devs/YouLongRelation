package me.kzheart.youlongrelation.common.bukkit.cmds

import me.kzheart.youlongrelation.api.YouLongRelationBukkitApi
import me.kzheart.youlongrelation.common.bukkit.function.LoverEachUpgrade
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
 * @date 2021/11/7 0:33
 */
@PlatformSide([Platform.BUKKIT])
@CommandHeader("loverbukkit", permission = "youlongrelation.use.loverbukkit")
object LoverBukkitCommand {

    @CommandBody
    val main = mainCommand {
        createHelper()
    }

    @CommandBody(permission = "youlongrelation.use.loverbukkit.upgrade")
    val upgrade = subCommand {
        literal("with") {
            dynamic {
                suggestion<Player> { sender, context ->
                    sender.getNearbyEntities(10.0, 10.0, 10.0).asSequence().filterIsInstance<Player>().map { it }
                        .filter { YouLongRelationBukkitApi.isLover(sender, it) }.map { it.name }.toList()
                }
                execute<Player> { sender, _, argument ->
                    val lover =
                        Bukkit.getPlayer(argument) ?: return@execute sender.sendLang("player-not-online", argument)

                    val loverAllow = YouLongRelationBukkitApi.getFriendUpgradeRemainTime(lover) > 0
                    val playerAllow = YouLongRelationBukkitApi.getFriendUpgradeRemainTime(sender) > 0

                    if (!loverAllow) {
                        sender.sendLang("lover-no-time-upgrade", lover.name)
                        lover.sendLang("lover-no-time-upgrade", lover.name)
                        return@execute
                    }

                    if (!playerAllow) {
                        sender.sendLang("lover-no-time-upgrade", sender.name)
                        lover.sendLang("lover-no-time-upgrade", sender.name)
                        return@execute
                    }

                    lover.sendLang("lover-upgrade-receiver", sender.name)
                    sender.sendLang("lover-upgrade-sender", lover.name)
                    ApplyListManager.addLoverUpgradeApply(lover, sender)
                }
            }
        }
        literal("accept") {
            dynamic {
                suggestion<Player> { sender, context ->
                    sender.getNearbyEntities(10.0, 10.0, 10.0).asSequence().filterIsInstance<Player>().map { it }
                        .filter { YouLongRelationBukkitApi.isLover(sender, it) }
                        .filter { ApplyListManager.isLoverApply(sender, it) }.map { it.name }.toList()
                }
                execute<Player> { sender, _, argument ->
                    val lover =
                        Bukkit.getPlayer(argument) ?: return@execute sender.sendLang("player-not-online", argument)
                    ApplyListManager.removeLoverUpgradeApply(sender)
                    val loverAllow = YouLongRelationBukkitApi.getLoverUpgradeRemainTime(lover) > 0
/*                YouLongRelationBukkitApi.getFriendLastUpgradeDate(friend) == null || !DateUtils.isSameDay(
                    YouLongRelationBukkitApi.getFriendLastUpgradeDate(friend),
                    Date()
                ) &&*/

                    val playerAllow = YouLongRelationBukkitApi.getLoverUpgradeRemainTime(sender) > 0
/*                    (YouLongRelationBukkitApi.getFriendLastUpgradeDate(sender) == null || !DateUtils.isSameDay(
                        YouLongRelationBukkitApi.getFriendLastUpgradeDate(sender),
                        Date()
                    )) && */

                    if (!loverAllow) {
                        sender.sendLang("lover-no-time-upgrade", lover.name)
                        lover.sendLang("lover-no-time-upgrade", lover.name)
                        return@execute
                    }

                    if (!playerAllow) {
                        sender.sendLang("lover-no-time-upgrade", sender.name)
                        lover.sendLang("lover-no-time-upgrade", sender.name)
                        return@execute
                    }
                    LoverEachUpgrade.start(sender, lover)
                }
            }
        }
        literal("deny") {
            dynamic {
                suggestion<Player> { sender, context ->
                    if (ApplyListManager.getLoverUpgradeApply(sender) == null)
                        return@suggestion emptyList<String>()
                    else
                        return@suggestion listOf(ApplyListManager.getLoverUpgradeApply(sender)!!)
                }
                execute<Player> { sender, _, argument ->
                    ApplyListManager.removeLoverUpgradeApply(sender)
                    getProxyPlayer(argument)?.sendLang("lover-upgrade-deny-receiver", sender.name)
                    sender.sendLang("lover-upgrade-deny-sender", argument)
                }
            }
        }
    }
}
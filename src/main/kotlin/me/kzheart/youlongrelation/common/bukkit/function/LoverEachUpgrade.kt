package me.kzheart.youlongrelation.common.bukkit.function

import com.sucy.skill.SkillAPI
import com.sucy.skill.api.enums.ExpSource
import me.kzheart.youlongrelation.api.YouLongRelationBukkitApi
import me.kzheart.youlongrelation.api.event.bukkit.*
import me.kzheart.youlongrelation.common.bukkit.conf.BukkitFriendConfManager
import me.kzheart.youlongrelation.common.bukkit.conf.BukkitLoverConfManager
import org.bukkit.entity.Player
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.console
import taboolib.common.platform.function.submit
import taboolib.platform.compat.replacePlaceholder
import taboolib.platform.util.sendLang
import javax.script.ScriptEngineManager
import kotlin.math.roundToInt

/**
 * @author kzheart
 * @date 2021/11/6 22:08
 */
object LoverEachUpgrade {
    /*    private val engine = ScriptEngineManager().getEngineByName("javascript")
        private val time = BukkitLoverConfManager.time
        private val exp = BukkitLoverConfManager.exp*/
    @SubscribeEvent(ignoreCancelled = true)
    fun onLoverTick(e: PlayerLoverUpgradeEvent) {
        BukkitLoverConfManager.commands.forEach {
            console().run {
                performCommand(it.replacePlaceholder(e.player))
                performCommand(it.replacePlaceholder(e.lover))
            }
        }
    }

    fun start(player: Player, lover: Player) {
        if (StatusMap.playerIsInStatus(player) || StatusMap.playerIsInStatus(lover)) {
            return
        }

        StatusMap.setPlayerLoverUpgrading(player, lover)
        submit(async = true, period = 20) {
            val playerRemainTime = YouLongRelationBukkitApi.getLoverUpgradeRemainTime(player)
            val loverRemainTime = YouLongRelationBukkitApi.getLoverUpgradeRemainTime(lover)
            if (StatusMap.getPlayerStatus(player) == Status.LOVER_UPGRADING && StatusMap.getPlayerStatus(lover) == Status.LOVER_UPGRADING) {
                if (playerRemainTime > 0 && loverRemainTime > 0) {
/*                    val playerCurrentExp =
                        if (SkillAPI.getPlayerAccountData(player).activeData.classes.firstOrNull() == null) 0.0 else SkillAPI.getPlayerAccountData(
                            player
                        ).activeData.classes.firstOrNull()!!.exp
                    val loverCurrentExp =
                        if (SkillAPI.getPlayerAccountData(lover).activeData.classes.firstOrNull() == null) 0.0 else SkillAPI.getPlayerAccountData(
                            player
                        ).activeData.classes.firstOrNull()!!.exp

                    val playerAddExp =
                        engine.eval(exp.replace("{current_exp}", playerCurrentExp.toString())).toString().toDouble()
                    val loverAddExp =
                        engine.eval(exp.replace("{current_exp}", loverCurrentExp.toString())).toString().toDouble()*/


/*                    player.sendLang("lover-upgrade-get-exp", playerAddExp.roundToInt(), playerRemainTime - 1)
                    lover.sendLang("lover-upgrade-get-exp", loverAddExp.roundToInt(), loverRemainTime - 1)*/

/*                    SkillAPI.getPlayerData(player).giveExp(playerAddExp, ExpSource.SPECIAL)
                    SkillAPI.getPlayerData(player).giveExp(playerAddExp, ExpSource.SPECIAL)*/


                    if (!PlayerLoverUpgradeEvent(player, lover).call())
                        return@submit cancel()


                    YouLongRelationBukkitApi.setLoverUpgradeRemainTime(player, playerRemainTime - 1)
                    YouLongRelationBukkitApi.setLoverUpgradeRemainTime(lover, loverRemainTime - 1)

                    YouLongRelationBukkitApi.updateLoverUpgradeDate(player)
                    YouLongRelationBukkitApi.updateLoverUpgradeDate(lover)

                } else {
                    if (playerRemainTime == 0) {
                        PlayerDisturbedEvent(
                            player.name,
                            listOf(lover.name),
                            DisturbedCause.TIME_OVER,
                            Status.LOVER_UPGRADING
                        ).call()
                        StatusMap.removePlayerFromStatus(player)
                    } else {
                        PlayerDisturbedEvent(
                            lover.name,
                            listOf(player.name),
                            DisturbedCause.TIME_OVER,
                            Status.LOVER_UPGRADING
                        ).call()
                        StatusMap.removePlayerFromStatus(lover)
                    }
                    cancel()
                }
            } else {
                cancel()
            }
        }
    }
}
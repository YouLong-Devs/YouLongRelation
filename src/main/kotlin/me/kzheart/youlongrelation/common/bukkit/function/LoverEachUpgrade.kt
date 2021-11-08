package me.kzheart.youlongrelation.common.bukkit.function

import com.sucy.skill.SkillAPI
import com.sucy.skill.api.enums.ExpSource
import me.kzheart.youlongrelation.api.YouLongRelationBukkitApi
import me.kzheart.youlongrelation.api.event.bukkit.*
import me.kzheart.youlongrelation.common.bukkit.conf.BukkitLoverConfManager
import org.bukkit.entity.Player
import taboolib.common.platform.function.submit
import taboolib.platform.util.sendLang
import javax.script.ScriptEngineManager
import kotlin.math.roundToInt

/**
 * @author kzheart
 * @date 2021/11/6 22:08
 */
object LoverEachUpgrade {
    private val engine = ScriptEngineManager().getEngineByName("javascript")
    private val time = BukkitLoverConfManager.time
    private val exp = BukkitLoverConfManager.exp
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
                    val playerCurrentExp = SkillAPI.getPlayerData(player).classes.run {
                        var exp: Double = 0.0
                        forEach {
                            exp += it.exp
                        }
                        return@run exp
                    }

                    val loverCurrentExp = SkillAPI.getPlayerData(lover).classes.run {
                        var exp: Double = 0.0
                        forEach {
                            exp += it.exp
                        }
                        return@run exp
                    }

                    val playerAddExp =
                        engine.eval(exp.replace("{current_exp}", playerCurrentExp.toString())).toString().toDouble()
                    val loverAddExp =
                        engine.eval(exp.replace("{current_exp}", loverCurrentExp.toString())).toString().toDouble()


                    YouLongRelationBukkitApi.setLoverUpgradeRemainTime(player, playerRemainTime - 1)
                    YouLongRelationBukkitApi.setLoverUpgradeRemainTime(lover, loverRemainTime - 1)


                    player.sendLang("lover-upgrade-get-exp", playerAddExp.roundToInt(), playerRemainTime - 1)
                    lover.sendLang("lover-upgrade-get-exp", loverAddExp.roundToInt(), loverRemainTime - 1)

                    SkillAPI.getPlayerData(player).giveExp(playerAddExp, ExpSource.SPECIAL)
                    SkillAPI.getPlayerData(lover).giveExp(loverAddExp, ExpSource.SPECIAL)

                    PlayerLoverUpgradeEvent(player, lover, playerAddExp, loverAddExp).call()
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
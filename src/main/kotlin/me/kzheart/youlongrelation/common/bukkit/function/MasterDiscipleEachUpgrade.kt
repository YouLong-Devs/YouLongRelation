package me.kzheart.youlongrelation.common.bukkit.function

import com.sucy.skill.SkillAPI
import com.sucy.skill.api.enums.ExpSource
import me.kzheart.youlongrelation.api.YouLongRelationBukkitApi
import me.kzheart.youlongrelation.api.event.bukkit.*
import me.kzheart.youlongrelation.common.bukkit.conf.BukkitFriendConfManager
import me.kzheart.youlongrelation.common.bukkit.conf.BukkitMasterDiscipleConfManager
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
 * @date 2021/11/7 14:49
 */
object MasterDiscipleEachUpgrade {
    @SubscribeEvent
    fun masterDiscipleOnTick(e: PlayerMasterUpgradeEvent) {
        BukkitMasterDiscipleConfManager.masterCommands.forEach {
            console().run {
                performCommand(it.replacePlaceholder(e.master))
            }
        }
        BukkitMasterDiscipleConfManager.discipleCommands.forEach { command ->
            console().run {
                e.disciples.forEach {
                    performCommand(command.replacePlaceholder(it))
                }
            }
        }
    }

    /*    private val engine = ScriptEngineManager().getEngineByName("javascript")
        private val time = BukkitMasterDiscipleConfManager.time
        private val discipleexp = BukkitMasterDiscipleConfManager.discipleexp
        private val masterexp = BukkitMasterDiscipleConfManager.masterexp*/
    fun start(player: Player, disciples: List<Player>) {
        if (StatusMap.playerIsInStatus(player)) {
            return
        }
        disciples.forEach {
            if (StatusMap.playerIsInStatus(it)) return

        }

        StatusMap.setPlayerMasterUpgrading(player, disciples)


        submit(async = true, period = 20) {
            val masterRemainTime = YouLongRelationBukkitApi.getMasterUpgradeRemainTime(player)
            if (StatusMap.getPlayerStatus(player) == Status.MASTER_UPGRADING) {
                disciples.forEach {
                    if (StatusMap.getPlayerStatus(it) != Status.DISCIPLE_UPGRADING) {
                        cancel()
                        return@submit
                    }
                }
                if (masterRemainTime > 0) {
/*                    val masterCurrentExp =
                        if (SkillAPI.getPlayerData(player).classes.firstOrNull() == null) 0.0 else SkillAPI.getPlayerData(
                            player
                        ).classes.firstOrNull()!!.exp
                    var disciplesExp = 0.0

                    disciples.forEach {
                        val discipleExpString = discipleexp.replace("{master_exp}", masterCurrentExp.toString())
                        val discipleAddExp = engine.eval(discipleExpString).toString().toDouble()
                        disciplesExp += if (SkillAPI.getPlayerData(it).classes.firstOrNull() == null) 0.0 else SkillAPI.getPlayerData(
                            it
                        ).classes.firstOrNull()!!.exp

                        it.sendLang("disciple-upgrade-get-exp", discipleAddExp.roundToInt(), masterRemainTime - 1)
                        SkillAPI.getPlayerData(it).giveExp(discipleAddExp, ExpSource.SPECIAL)
                    }

                    val masterExpString = masterexp.replace("{disciple_exp}", disciplesExp.toString())
                    val masterAddExp = engine.eval(masterExpString).toString().toDouble()

                    player.sendLang("disciple-upgrade-get-exp", masterAddExp.roundToInt(), masterRemainTime - 1)
                    SkillAPI.getPlayerData(player).giveExp(masterAddExp, ExpSource.SPECIAL)*/


                    if (!PlayerMasterUpgradeEvent(player, disciples).call())
                        return@submit cancel()

                    YouLongRelationBukkitApi.setMasterUpgradeRemainTime(player, masterRemainTime - 1)
                    YouLongRelationBukkitApi.updateMasterUpgradeDate(player)
                } else {
                    disciples.forEach { _ ->
                        PlayerDisturbedEvent(
                            player.name,
                            disciples.map { it.name },
                            DisturbedCause.TIME_OVER,
                            Status.MASTER_UPGRADING
                        ).call()
                    }
                    StatusMap.removePlayerFromStatus(player)
                    cancel()
                }
            } else cancel()
        }
    }
}


package me.kzheart.youlongrelation.common.bukkit.function

import com.sucy.skill.SkillAPI
import com.sucy.skill.api.enums.ExpSource
import me.kzheart.youlongrelation.api.YouLongRelationBukkitApi
import me.kzheart.youlongrelation.api.event.bukkit.DisturbedCause
import me.kzheart.youlongrelation.api.event.bukkit.PlayerDisturbedEvent
import me.kzheart.youlongrelation.api.event.bukkit.PlayerFriendUpgradeEvent
import me.kzheart.youlongrelation.api.event.bukkit.Status
import me.kzheart.youlongrelation.common.bukkit.conf.BukkitFriendConfManager
import org.bukkit.entity.Player
import taboolib.common.platform.function.submit
import taboolib.platform.util.sendLang
import javax.script.ScriptEngineManager

/**
 * @author kzheart
 * @date 2021/11/6 1:36
 */
object FriendEachUpgrade {
    private val engine = ScriptEngineManager().getEngineByName("javascript")
    private val time = BukkitFriendConfManager.time
    private val exp = BukkitFriendConfManager.exp


    fun start(player: Player, friend: Player) {
/*        if (!YouLongRelationBukkitApi.isFriend(player, friend)) {
            player.sendLang("firend-no-each", friend.name)
            friend.sendLang("firend-no-each", player.name)
            return
        }*/
        if (StatusMap.playerIsInStatus(player) || StatusMap.playerIsInStatus(friend)) {
            return
        }
        if (YouLongRelationBukkitApi.getPlayerLevel(player) - YouLongRelationBukkitApi.getPlayerLevel(friend) >= 2) {
            player.health = 0.0
            friend.health = 0.0
            player.sendLang("")
        }
        StatusMap.setPlayerInFriendUpgrading(player, friend)

        submit(async = true, period = 20) {
            val playerRemainTime = YouLongRelationBukkitApi.getFriendUpgradeRemainTime(player)
            val friendRemainTime = YouLongRelationBukkitApi.getFriendUpgradeRemainTime(friend)
            if (StatusMap.getPlayerStatus(player) == Status.FRIEND_UPGRADING && StatusMap.getPlayerStatus(friend) == Status.FRIEND_UPGRADING) {
                if (playerRemainTime > 0 && friendRemainTime > 0) {
                    val playerCurrentExp = SkillAPI.getPlayerData(player).mainClass.exp

                    val friendCurrentExp = SkillAPI.getPlayerData(friend).mainClass.exp

                    val playerAddExp =
                        engine.eval(exp.replace("{current_exp}", playerCurrentExp.toString())).toString().toDouble()
                    val friendAddExp =
                        engine.eval(exp.replace("{current_exp}", friendCurrentExp.toString())).toString().toDouble()


                    YouLongRelationBukkitApi.setFriendUpgradeRemainTime(player, playerRemainTime - 1)
                    YouLongRelationBukkitApi.setFriendUpgradeRemainTime(friend, friendRemainTime - 1)


                    player.sendLang("friend-upgrade-get-exp", playerAddExp, playerRemainTime - 1)
                    friend.sendLang("friend-upgrade-get-exp", friendAddExp, friendRemainTime - 1)

                    SkillAPI.getPlayerData(player).giveExp(playerAddExp, ExpSource.SPECIAL)
                    SkillAPI.getPlayerData(friend).giveExp(friendAddExp, ExpSource.SPECIAL)

                    PlayerFriendUpgradeEvent(player, friend, playerAddExp, friendAddExp).call()
                    YouLongRelationBukkitApi.updateFriendUpgradeDate(player)
                    YouLongRelationBukkitApi.updateFriendUpgradeDate(friend)
                } else {
                    if (playerRemainTime == 0) {
                        PlayerDisturbedEvent(
                            player.name,
                            listOf(friend.name),
                            DisturbedCause.TIME_OVER,
                            Status.FRIEND_UPGRADING
                        ).call()
                        StatusMap.removePlayerFromStatus(player)
                    } else {
                        PlayerDisturbedEvent(
                            friend.name,
                            listOf(player.name),
                            DisturbedCause.TIME_OVER,
                            Status.FRIEND_UPGRADING
                        ).call()
                        StatusMap.removePlayerFromStatus(friend)
                    }
                    cancel()
                }
            } else {
                cancel()
            }
        }
    }

}


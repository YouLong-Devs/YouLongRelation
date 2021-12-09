package me.kzheart.youlongrelation.common.bukkit.function

import com.sucy.skill.SkillAPI
import com.sucy.skill.api.enums.ExpSource
import me.kzheart.youlongrelation.api.YouLongRelationBukkitApi
import me.kzheart.youlongrelation.api.event.bukkit.DisturbedCause
import me.kzheart.youlongrelation.api.event.bukkit.PlayerDisturbedEvent
import me.kzheart.youlongrelation.api.event.bukkit.PlayerFriendUpgradeEvent
import me.kzheart.youlongrelation.api.event.bukkit.Status
import me.kzheart.youlongrelation.common.bukkit.conf.BukkitFriendConfManager
import org.bukkit.Bukkit
import org.bukkit.entity.Player
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.console
import taboolib.common.platform.function.submit
import taboolib.platform.compat.PlaceholderExpansion
import taboolib.platform.compat.replacePlaceholder
import taboolib.platform.util.sendLang
import javax.script.ScriptEngineManager
import kotlin.math.roundToInt

/**
 * @author kzheart
 * @date 2021/11/6 1:36
 */
object FriendEachUpgrade {
/*    private val engine = ScriptEngineManager().getEngineByName("javascript")
    private val time = BukkitFriendConfManager.time
    private val exp = BukkitFriendConfManager.exp*/

    @SubscribeEvent(ignoreCancelled = true)
    fun onFriendTick(e: PlayerFriendUpgradeEvent) {
        BukkitFriendConfManager.commands.forEach {
            console().run {
                performCommand(it.replacePlaceholder(e.player))
                performCommand(it.replacePlaceholder(e.friend))
            }
        }
//        e.player.sendLang(
//            "friend-upgrade-get-exp",
//            e.playerAddExp.roundToInt(),
//            YouLongRelationBukkitApi.getFriendUpgradeRemainTime(e.player)
//        )
//        e.friend.sendLang(
//            "friend-upgrade-get-exp",
//            e.friendAddExp.roundToInt(),
//            YouLongRelationBukkitApi.getFriendUpgradeRemainTime(e.friend)
//        )
/*        //skillapi给的傻逼方法不好使啊 我还是直接用命令吧。。。。
        console().performCommand("class exp ${e.player.name} ${e.playerAddExp}")
        console().performCommand("class exp ${e.friend.name} ${e.friendAddExp}")*/

/*        SkillAPI.getPlayerAccountData(e.player).activeData.mainClass.giveExp(playerAddExp, ExpSource.SPECIAL)
        SkillAPI.getPlayerAccountData(e.friend).activeData.mainClass.giveExp(playerAddExp, ExpSource.SPECIAL)*/

    }

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
            player.sendLang("friend-upgrade-death")
            friend.sendLang("friend-upgrade-death")
            return
        }
        StatusMap.setPlayerInFriendUpgrading(player, friend)

        submit(async = true, period = 20) {
            val playerRemainTime = YouLongRelationBukkitApi.getFriendUpgradeRemainTime(player)
            val friendRemainTime = YouLongRelationBukkitApi.getFriendUpgradeRemainTime(friend)
            if (StatusMap.getPlayerStatus(player) == Status.FRIEND_UPGRADING && StatusMap.getPlayerStatus(friend) == Status.FRIEND_UPGRADING) {
                if (playerRemainTime > 0 && friendRemainTime > 0) {
/*                    val playerCurrentExp =
                        if (SkillAPI.getPlayerAccountData(player).activeData.classes.firstOrNull() == null) 0.0 else SkillAPI.getPlayerAccountData(
                            player
                        ).activeData.classes.firstOrNull()!!.exp
                    val friendCurrentExp =
                        if (SkillAPI.getPlayerAccountData(friend).activeData.classes.firstOrNull() == null) 0.0 else SkillAPI.getPlayerAccountData(
                            player
                        ).activeData.classes.firstOrNull()!!.exp


                    val playerAddExp =
                        engine.eval(exp.replace("{current_exp}", playerCurrentExp.toString())).toString().toDouble()
                    val friendAddExp =
                        engine.eval(exp.replace("{current_exp}", friendCurrentExp.toString())).toString().toDouble()*/




                    if (!PlayerFriendUpgradeEvent(player, friend).call())
                        return@submit cancel()

                    YouLongRelationBukkitApi.setFriendUpgradeRemainTime(player, playerRemainTime - 1)
                    YouLongRelationBukkitApi.setFriendUpgradeRemainTime(friend, friendRemainTime - 1)

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
            } else cancel()

        }
    }

}


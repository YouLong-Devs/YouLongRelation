package me.kzheart.youlongrelation.common.bukkit.conf

import me.kzheart.youlongrelation.YouLongRelation

/**
 * @author kzheart
 * @date 2021/11/6 22:55
 */
object BukkitLoverConfManager {
    val time by lazy { YouLongRelation.config.getInt("lover.upgrade.time", 3600) }

    //    val exp by lazy { YouLongRelation.config.getString("lover.upgrade.exp", "{current_exp} * 0.2") }
    val commands by lazy { YouLongRelation.config.getStringList("friend.upgrade.commands") ?: listOf() }
}
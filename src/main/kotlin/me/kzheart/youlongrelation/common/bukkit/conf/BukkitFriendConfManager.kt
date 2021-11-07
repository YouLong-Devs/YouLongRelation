package me.kzheart.youlongrelation.common.bukkit.conf

import me.kzheart.youlongrelation.YouLongRelation

/**
 * @author kzheart
 * @date 2021/11/6 22:52
 */
object BukkitFriendConfManager {
    val exp by lazy { YouLongRelation.config.getString("friend.upgrade.exp", "{current_exp} * 0.2") }
    val level by lazy { YouLongRelation.config.getInt("friend.upgrade.level", 2) }
    val time by lazy { YouLongRelation.config.getInt("friend.upgrade.time", 300) }
}
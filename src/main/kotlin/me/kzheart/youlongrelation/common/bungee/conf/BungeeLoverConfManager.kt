package me.kzheart.youlongrelation.common.bungee.conf

import me.kzheart.youlongrelation.YouLongRelation
import me.kzheart.youlongrelation.YouLongRelationBungee

/**
 * @author kzheart
 * @date 2021/11/7 12:47
 */
object BungeeLoverConfManager {
    val allowSameSex by lazy { YouLongRelationBungee.config.getBoolean("lover.homosexual", false) }
    val needIntimacy by lazy { YouLongRelationBungee.config.getInt("lover.intimacy", 80) }
}
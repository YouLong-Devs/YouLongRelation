package me.kzheart.youlongrelation.common.bungee.conf

import me.kzheart.youlongrelation.YouLongRelation
import me.kzheart.youlongrelation.YouLongRelationBungee

/**
 * @author kzheart
 * @date 2021/11/7 12:49
 */
object BungeeMasterDiscipleConfManager {
    val maxDiscipleCount by lazy { YouLongRelationBungee.config.getInt("disciple.max-count", 9) }
}
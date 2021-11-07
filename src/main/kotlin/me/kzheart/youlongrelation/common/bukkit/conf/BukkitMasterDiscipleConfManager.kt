package me.kzheart.youlongrelation.common.bukkit.conf

import me.kzheart.youlongrelation.YouLongRelation

/**
 * @author kzheart
 * @date 2021/11/6 23:02
 */
object BukkitMasterDiscipleConfManager {
    val time by lazy { YouLongRelation.config.getInt("master.time", 3600) }
    val count by lazy { YouLongRelation.config.getInt("master.count", 3) }
    val masterexp by lazy { YouLongRelation.config.getString("master.masterexp") }
    val discipleexp by lazy { YouLongRelation.config.getString("master.discipleexp") }
}
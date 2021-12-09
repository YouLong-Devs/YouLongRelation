package me.kzheart.youlongrelation.database

import org.bukkit.OfflinePlayer
import taboolib.common.platform.ProxyPlayer
import taboolib.common.platform.function.pluginId
import taboolib.library.configuration.ConfigurationSection
import taboolib.module.database.HostSQL
import java.io.File
import java.util.*
import java.util.concurrent.ConcurrentHashMap

private var playerDatabase: Database? = null

private val playerDataContainer = ConcurrentHashMap<UUID, DataContainer>()

fun setupPlayerDatabase(conf: ConfigurationSection, table: String = conf.getString("table")!!) {
    playerDatabase = Database(TypeSQL(HostSQL(conf), table))
}

fun setupPlayerDatabase(
    host: String = "localhost",
    port: Int = 3306,
    user: String = "root",
    password: String = "root",
    database: String = "minecraft",
    table: String = "${pluginId.lowercase()}_database",
) {
    playerDatabase = Database(TypeSQL(HostSQL(host, port.toString(), user, password, database), table))
}

fun setupPlayerDatabase(file: File) {
    playerDatabase = Database(TypeSQLite(file))
}

fun ProxyPlayer.getDataContainer(): DataContainer {
    return DataContainer(this.name, playerDatabase!!)
}

fun ProxyPlayer.setupDataContainer(usernameMode: Boolean = false) {
    val user = if (usernameMode) name else uniqueId.toString()
    playerDataContainer[uniqueId] = DataContainer(user, playerDatabase!!)
}

fun ProxyPlayer.releaseDataContainer() {
    playerDataContainer.remove(uniqueId)
}

fun OfflinePlayer.getDataContainer(): DataContainer {
    return DataContainer(uniqueId.toString(), playerDatabase!!)
}

fun String.getDataContainerByName(): DataContainer {
    return DataContainer(this, playerDatabase!!)
}
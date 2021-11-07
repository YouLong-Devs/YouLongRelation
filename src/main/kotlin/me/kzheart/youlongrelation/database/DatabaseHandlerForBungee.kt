package me.kzheart.youlongrelation.database

import net.md_5.bungee.api.connection.ProxiedPlayer
import taboolib.common.platform.function.adaptPlayer

/**
 * @author kzheart
 * @date 2021/11/4 13:46
 */
fun ProxiedPlayer.getDataContainer(): DataContainer {
    return adaptPlayer(this).getDataContainer()
}

fun ProxiedPlayer.setupDataContainer(usernameMode: Boolean = false) {
    adaptPlayer(this).setupDataContainer(usernameMode)
}

fun ProxiedPlayer.releaseDataContainer() {
    adaptPlayer(this).releaseDataContainer()
}
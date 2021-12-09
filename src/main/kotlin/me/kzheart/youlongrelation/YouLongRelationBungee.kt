package me.kzheart.youlongrelation

import me.kzheart.youlongrelation.database.releaseDataContainer
import me.kzheart.youlongrelation.database.setupDataContainer
import me.kzheart.youlongrelation.database.setupPlayerDatabase
import net.md_5.bungee.api.ProxyServer
import net.md_5.bungee.api.event.ServerConnectedEvent
import net.md_5.bungee.api.event.ServerDisconnectEvent
import taboolib.common.LifeCycle
import taboolib.common.platform.*
import taboolib.common.platform.event.SubscribeEvent
import taboolib.module.configuration.Config
import taboolib.module.configuration.SecuredFile

/**
 * @author kzheart
 * @date 2021/11/3 11:43
 */
@PlatformSide([Platform.BUNGEE])
object YouLongRelationBungee : Plugin() {
    @Config("bungeeconf.yml")
    lateinit var config: SecuredFile




    override fun onEnable() {
        ProxyServer.getInstance().registerChannel("YLRelation:action")
        ProxyServer.getInstance().registerChannel("YLRelation:item")
    }


    @Awake(LifeCycle.ENABLE)
    private fun loadDatabase() {
        setupPlayerDatabase(config.getConfigurationSection("database")!!)
    }

    @SubscribeEvent
    fun playerLogin(e: ServerConnectedEvent) {
        e.player.setupDataContainer(true)
    }


    @SubscribeEvent
    fun playerDisconnect(e: ServerDisconnectEvent) {
        e.player.releaseDataContainer()
    }


}
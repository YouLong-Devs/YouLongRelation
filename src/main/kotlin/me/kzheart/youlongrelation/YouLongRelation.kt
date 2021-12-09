package me.kzheart.youlongrelation

import me.kzheart.youlongrelation.common.bungee.Bungee
import me.kzheart.youlongrelation.data.deserializeFriends
import me.kzheart.youlongrelation.database.getDataContainerByName
import me.kzheart.youlongrelation.database.releaseDataContainer
import me.kzheart.youlongrelation.database.setupDataContainer
import me.kzheart.youlongrelation.database.setupPlayerDatabase
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.craftbukkit.v1_12_R1.inventory.CraftItemStack
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import taboolib.common.LifeCycle
import taboolib.common.platform.Awake
import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide
import taboolib.common.platform.Plugin
import taboolib.common.platform.event.SubscribeEvent
import taboolib.common.platform.function.info
import taboolib.common.platform.function.submit
import taboolib.module.configuration.Config
import taboolib.module.configuration.Configuration
import taboolib.platform.BukkitPlugin
import kotlin.properties.Delegates

/**
 * @author kzheart
 * @date 2021/11/1 19:28
 */
@PlatformSide([Platform.BUKKIT])
object YouLongRelation : Plugin() {
    @Config("config.yml")
    lateinit var config: Configuration


    val plugin by lazy { BukkitPlugin.getInstance() }
    override fun onEnable() {
/*        Bungee.init()*/
        info("YouLongRelation enabled")
        Bungee.init()

        //加载每个玩家的好友头颅
        submit(async = true) {
            Bukkit.getOfflinePlayers().forEach {
                it.name.getDataContainerByName()["friends"].deserializeFriends().forEach { (t, _) ->
                    submit(async = true) {
                        beforeLoadPlayerHead(t)
                    }
                }
            }
        }
    }

    override fun onDisable() {
    }

    override fun onActive() {
/*        Bukkit.getServer().messenger.registerOutgoingPluginChannel(plugin, "BungeeCord")
        Bukkit.getServer().messenger.registerIncomingPluginChannel(plugin, "BungeeCord", Bungee());*/
    }


    @Awake(LifeCycle.ENABLE)
    private fun loadDatabase() {
        setupPlayerDatabase(config.getConfigurationSection("database")!!)
    }


    @SubscribeEvent
    fun e(e: PlayerJoinEvent) {
        e.player.setupDataContainer(true)


/*        submit(async = true) {
            e.player.getDataContainer()["friends"].deserializeFriends().forEach { (t, u) ->
                beforeLoadPlayerHead(t)
            }
        }*/
    }

    @SubscribeEvent
    fun e(e: PlayerQuitEvent) {
        e.player.releaseDataContainer()
    }

    private fun beforeLoadPlayerHead(name: String) {
        val item = ItemStack(Material.SKULL_ITEM)
        val meta = (item.itemMeta) as SkullMeta
        meta.owner = name
        item.itemMeta = meta
        CraftItemStack.asNMSCopy(item)
    }
}
package me.kzheart.youlongrelation.common.bukkit.gui

import me.kzheart.youlongrelation.common.util.Players
import me.kzheart.youlongrelation.data.FriendData
import me.kzheart.youlongrelation.data.deserializeFriends
import me.kzheart.youlongrelation.database.getDataContainer
import org.bukkit.entity.Player
import taboolib.common.platform.function.console
import taboolib.library.xseries.XMaterial
import taboolib.module.lang.asLangText
import taboolib.module.lang.asLangTextList
import taboolib.module.ui.buildMenu
import taboolib.module.ui.type.Linked
import taboolib.platform.util.buildItem
import taboolib.platform.util.inventoryCenterSlots
import taboolib.platform.util.modifyLore
import java.text.SimpleDateFormat

/**
 * @author kzheart
 * @date 2021/11/1 20:14
 */
object FriendGui {

    fun open(player: Player) {
        player.openInventory(buildMenu<Linked<FriendData>>(console().asLangText("friend-gui-title", player.name)) {
            rows(6)
            slots(inventoryCenterSlots)
            elements {
                player.getDataContainer()["friends"].deserializeFriends().map { it.value }
            }
            onGenerate(true) { player, element, index, slot ->
                buildItem(XMaterial.PLAYER_HEAD) {
                    skullOwner = console().asLangText("friend-icon-name", element.friendName)
                }.modifyLore {
                    console().asLangTextList(
                        "friend-icon-lore", SimpleDateFormat("yyyy-MM-dd HH:mm").format(element.date),
                    ).forEach {
                        add(it)
                    }
                }
            }
            setNextPage(51) { _, hasNextPage ->
                if (hasNextPage) {
                    buildItem(XMaterial.SPECTRAL_ARROW) { name = console().asLangText("next-page") }
                } else {
                    buildItem(XMaterial.ARROW) { name = console().asLangText("next-page") }
                }
            }
            setPreviousPage(47) { _, hasPreviousPage ->
                if (hasPreviousPage) {
                    buildItem(XMaterial.SPECTRAL_ARROW) { name = console().asLangText("previous-page") }
                } else {
                    buildItem(XMaterial.ARROW) { name = console().asLangText("previous-page") }
                }
            }
        })

    }
}

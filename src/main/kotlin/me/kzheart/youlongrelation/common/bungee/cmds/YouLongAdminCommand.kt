package me.kzheart.youlongrelation.common.bungee.cmds

import me.kzheart.youlongrelation.api.YouLongRelationApi
import me.kzheart.youlongrelation.api.YouLongRelationBungeeApi
import taboolib.common.platform.Platform
import taboolib.common.platform.PlatformSide
import taboolib.common.platform.ProxyCommandSender
import taboolib.common.platform.command.CommandBody
import taboolib.common.platform.command.CommandHeader
import taboolib.common.platform.command.mainCommand
import taboolib.common.platform.command.subCommand
import taboolib.common.platform.function.onlinePlayers
import taboolib.common5.Coerce
import taboolib.expansion.createHelper
import taboolib.module.lang.sendLang

/**
 * @author kzheart
 * @date 2021/11/5 17:23
 */
@PlatformSide([Platform.BUNGEE])
@CommandHeader("youlongadmin", ["yladmin"], permission = "youlongrelation.admin")
object YouLongAdminCommand {
    @CommandBody(permission = "youlongrelation.admin")
    val main = mainCommand {
        createHelper()
    }

    @CommandBody(permission = "youlongrelation.admin")
    val intimacy = subCommand {
        literal("set") {
            dynamic {
                //设置某个玩家的与某个好友亲密度
                suggestion<ProxyCommandSender> { sender, _ ->
                    onlinePlayers().map { it.name }
                }
                dynamic {
                    suggestion<ProxyCommandSender> { sender, context ->
                        YouLongRelationApi.getFriends(context.argument(-1)).map { it.key }
                    }
                    dynamic {
                        suggestion<ProxyCommandSender>(true) { sender, context ->
                            listOf("number")
                        }
                        restrict<ProxyCommandSender> { sender, context, argument ->
                            // 只允许使用数字类型
                            Coerce.asInteger(argument).isPresent
                        }
                        execute<ProxyCommandSender> { sender, context, argument ->
                            YouLongRelationApi.setIntimacy(
                                context.argument(-1),
                                context.argument(-2),
                                argument.toInt()
                            )
                            sender.sendLang(
                                "intimacy-set",
                                context.argument(-1),
                                context.argument(-2),
                                YouLongRelationBungeeApi.getIntimacy(context.argument(-1), context.argument(-2))
                            )
                        }
                    }
                }
            }
        }
    }

}
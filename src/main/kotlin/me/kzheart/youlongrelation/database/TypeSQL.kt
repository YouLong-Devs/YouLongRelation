package me.kzheart.youlongrelation.database

import taboolib.module.database.*

class TypeSQL(val host: Host<SQL>, table: String) : Type() {

    val tableVar = Table(table, host) {
        add { id() }
        add("user") {
            type(ColumnTypeSQL.VARCHAR, 36) {
                options(ColumnOptionSQL.KEY)
            }
        }
        add("key") {
            type(ColumnTypeSQL.VARCHAR, 64) {
                options(ColumnOptionSQL.KEY)
            }
        }
        add("value") {
            type(ColumnTypeSQL.TEXT)
        }
    }

    override fun host(): Host<*> {
        return host
    }

    override fun tableVar(): Table<*, *> {
        return tableVar
    }
}
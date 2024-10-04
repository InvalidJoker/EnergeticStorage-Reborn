package com.liamxsage.energeticstorage.data.database

import com.liamxsage.energeticstorage.data.cache.SystemCache
import com.liamxsage.energeticstorage.data.*
import com.liamxsage.energeticstorage.data.enums.NetworkItemType
import com.liamxsage.energeticstorage.data.enums.NetworkItemType.CONTAINER
import com.liamxsage.energeticstorage.data.interfaces.NetworkItem
import com.liamxsage.energeticstorage.utils.serialization.customJson
import kotlinx.serialization.encodeToString
import org.bukkit.block.Block
import org.jetbrains.exposed.dao.id.UUIDTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.api.ExposedBlob
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

object CoreTable: UUIDTable("storage_cores") {
    val coreUUID = uuid("core_uuid")
    val block = blob("block")
}

object NetworkItemTable: UUIDTable("network_items") {
    val block = blob("block")
    val connectedCoreUUID = uuid("connected_core_uuid").nullable()
    val type = enumeration("type", NetworkItemType::class)
}



fun NetworkItem.toType(): NetworkItemType = when(this) {
    is Container -> CONTAINER
    is HopperImporter -> NetworkItemType.HOPPER_IMPORT
    is Cable -> NetworkItemType.CABLE
    else -> throw IllegalArgumentException(this::class.simpleName + " is not a valid network item type")
}

fun NetworkItem.save() = transaction {
    if (this@save is Core) {
        saveCore()
        return@transaction
    }
    NetworkItemTable.replace {
        val jsonBlock = customJson.encodeToString(this@save.block)
        it[block] = ExposedBlob(jsonBlock.toByteArray())
        it[connectedCoreUUID] = this@save.connectedCoreUUID
        it[type] = toType()
    }
}

fun Core.saveCore() = transaction {
    CoreTable.replace {
        val jsonBlock = customJson.encodeToString(this@saveCore.block)
        it[block] = ExposedBlob(jsonBlock.toByteArray())
        it[coreUUID] = uuid
    }
}

fun loadNetworkItems(coreUUID: UUID): List<NetworkItem> = transaction {
    NetworkItemTable.selectAll().where { NetworkItemTable.connectedCoreUUID eq coreUUID }.map {
        val block = customJson.decodeFromString<Block>(it[NetworkItemTable.block].bytes.decodeToString())
        val type = it[NetworkItemTable.type]
        val item = type.toNetworkItem(block)
        item.connectedCoreUUID = it[NetworkItemTable.connectedCoreUUID]
        item
    }
}

fun loadFromDatabase() = transaction {
    CoreTable.selectAll().forEach {
        val block = customJson.decodeFromString<Block>(it[CoreTable.block].bytes.decodeToString())
        val uuid = it[CoreTable.coreUUID]
        val core = Core(uuid, block)
        val items = loadNetworkItems(uuid)

        items.forEach { item ->
            core.addItem(item)
        }

    }
}
fun saveToDatabase() {
    SystemCache.getSystems().forEach { system ->
        system.saveCore()
        system.connectedItems.forEach { item ->
            item.save()
        }
    }
}
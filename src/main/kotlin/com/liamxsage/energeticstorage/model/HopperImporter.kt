package com.liamxsage.energeticstorage.model

import com.liamxsage.energeticstorage.NETWORK_INTERFACE_ID_NAMESPACE
import com.liamxsage.energeticstorage.NETWORK_INTERFACE_NAMESPACE
import com.liamxsage.energeticstorage.TEXT_GRAY
import com.liamxsage.energeticstorage.extensions.persistentDataContainer
import com.liamxsage.energeticstorage.extensions.toItemBuilder
import org.bukkit.block.Block
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.util.*

class HopperImporter(
    override val block: Block
): NetworkItem(block) {
    override var connectedCoreUUID: UUID? = null

    override fun setBlockUUID(): HopperImporter {
        block.persistentDataContainer[NETWORK_INTERFACE_ID_NAMESPACE, PersistentDataType.STRING] = connectedCoreUUID.toString()
        return this
    }


    companion object {
        fun createHopperImportItem(): ItemStack = NetworkInterfaceType.HOPPER_IMPORT.material.toItemBuilder {
            display("${TEXT_GRAY}Hopper Importer")
            lore(
                "${TEXT_GRAY}Import items from a hopper",
            )
            setGlinting(true)
            customModelData(1)
            addPersistentData(NETWORK_INTERFACE_NAMESPACE, PersistentDataType.BOOLEAN, true)
            flag(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ADDITIONAL_TOOLTIP, ItemFlag.HIDE_ENCHANTS)
        }.build()
    }

}
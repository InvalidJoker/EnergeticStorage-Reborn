package com.liamxsage.energeticstorage.data

import com.liamxsage.energeticstorage.NETWORK_INTERFACE_ID_NAMESPACE
import com.liamxsage.energeticstorage.NETWORK_INTERFACE_NAMESPACE
import com.liamxsage.energeticstorage.TEXT_GRAY
import com.liamxsage.energeticstorage.data.enums.NetworkInterfaceType
import com.liamxsage.energeticstorage.data.interfaces.NetworkItem
import com.liamxsage.energeticstorage.extensions.persistentDataContainer
import com.liamxsage.energeticstorage.extensions.toItemBuilder
import org.bukkit.block.Block
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.util.*

class Cable(
    override val block: Block
): NetworkItem(block) {
    override var connectedCoreUUID: UUID? = null

    override fun setBlockUUID(): Cable {
        block.persistentDataContainer[NETWORK_INTERFACE_ID_NAMESPACE, PersistentDataType.STRING] = connectedCoreUUID.toString()
        return this
    }


    companion object {
        /**
         * Creates an ItemStack representing a pipe item for the Network.
         *
         * @return The created pipe item.
         */
        fun createCableItem(): ItemStack = NetworkInterfaceType.CABLE.material.toItemBuilder {
            display("${TEXT_GRAY}Cable")
            lore(
                "${TEXT_GRAY}Connects the system",
            )
            setGlinting(true)
            customModelData(1)
            addPersistentData(NETWORK_INTERFACE_NAMESPACE, PersistentDataType.BOOLEAN, true)
            flag(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ADDITIONAL_TOOLTIP, ItemFlag.HIDE_ENCHANTS)
        }.build()
    }

}
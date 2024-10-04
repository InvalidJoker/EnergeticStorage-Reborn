package com.liamxsage.energeticstorage.data

import com.liamxsage.energeticstorage.NETWORK_INTERFACE_ID_NAMESPACE
import com.liamxsage.energeticstorage.NETWORK_INTERFACE_NAMESPACE
import com.liamxsage.energeticstorage.TEXT_GRAY
import com.liamxsage.energeticstorage.data.enums.NetworkInterfaceType
import com.liamxsage.energeticstorage.data.interfaces.NetworkItem
import com.liamxsage.energeticstorage.extensions.persistentDataContainer
import com.liamxsage.energeticstorage.extensions.toItemBuilder
import org.bukkit.block.Block
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.util.*

class Container(
    override val block: Block
): NetworkItem(block) {
    override var connectedCoreUUID: UUID? = null

    override fun setBlockUUID(): Container {
        block.persistentDataContainer[NETWORK_INTERFACE_ID_NAMESPACE, PersistentDataType.STRING] = connectedCoreUUID.toString()
        return this
    }

    fun getBlocks(): List<ItemStack> {
        // Ensure the block is an instance of a container (e.g., Chest, Barrel)
        val state = block.state
        if (state is org.bukkit.block.Container) {
            return state.inventory.contents.filterNotNull()
        } else {
            return emptyList()
        }
    }

    fun getInventory(): Inventory? {
        // Ensure the block is an instance of a container (e.g., Chest, Barrel)
        val state = block.state
        if (state is org.bukkit.block.Container) {
            return state.inventory
        } else {
           return null
        }
    }

    companion object {
        fun createContainerItem(): ItemStack = NetworkInterfaceType.CONTAINER.material.toItemBuilder {
            display("${TEXT_GRAY}Storage Container")
            lore(
                "${TEXT_GRAY}Store your items",
            )
            setGlinting(true)
            customModelData(1)
            addPersistentData(NETWORK_INTERFACE_NAMESPACE, PersistentDataType.BOOLEAN, true)
            flag(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ADDITIONAL_TOOLTIP, ItemFlag.HIDE_ENCHANTS)
        }.build()
    }

}
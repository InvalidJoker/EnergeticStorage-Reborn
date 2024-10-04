package com.liamxsage.energeticstorage.model

import com.liamxsage.energeticstorage.NETWORK_INTERFACE_ID_NAMESPACE
import com.liamxsage.energeticstorage.NETWORK_INTERFACE_NAMESPACE
import com.liamxsage.energeticstorage.TEXT_GRAY
import com.liamxsage.energeticstorage.cache.SystemCache
import com.liamxsage.energeticstorage.extensions.persistentDataContainer
import com.liamxsage.energeticstorage.extensions.toItemBuilder
import org.bukkit.block.Block
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import java.util.*


class Core(
    val uuid: UUID = UUID.randomUUID(),
    override val block: Block
): NetworkItem(block) {
    override var connectedCoreUUID: UUID? = uuid

    val connectedItems = mutableSetOf<NetworkItem>()

    val connectedContainers
        get() = connectedItems.filterIsInstance<Container>()

    val connectedCables
        get() = connectedItems.filterIsInstance<Cable>()

    init {
        SystemCache.addSystem(this)
    }

    override fun setBlockUUID(): Core {
        block.persistentDataContainer[NETWORK_INTERFACE_ID_NAMESPACE, PersistentDataType.STRING] = connectedCoreUUID.toString()
        return this
    }

    companion object {

        fun createCoreItem(): ItemStack = NetworkInterfaceType.CORE.material.toItemBuilder {
            display("${TEXT_GRAY}Core")
            lore(
                "${TEXT_GRAY}Heart of the system",
                "${TEXT_GRAY}Needs to be inserted into a system to function.",
                "${TEXT_GRAY}Maximum of 1 per system."
            )
            setGlinting(true)
            customModelData(1)
            addPersistentData(NETWORK_INTERFACE_NAMESPACE, PersistentDataType.BOOLEAN, true)
            flag(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ADDITIONAL_TOOLTIP, ItemFlag.HIDE_ENCHANTS)
        }.build()
    }

    val totalItems: Long
        get() = connectedContainers.sumOf { it.getBlocks().sumOf { it.amount.toLong() } }

    fun addItem(item: NetworkItem) {
        connectedItems.add(item)
    }


    fun removeItem(block: Block) {
        connectedItems.removeIf { it.block.location == block.location }
    }

    fun removeItem(item: NetworkItem) {
        connectedItems.remove(item)
    }



    fun storeItem(item: ItemStack): Boolean {
        // get the container with the least amount of items
        if (connectedContainers.isEmpty()) return false
        val container = connectedContainers.minByOrNull { it.getBlocks().sumOf { it.amount } } ?: return false

        // add the item to the container

        val inventory = container.getInventory() ?: run {
            removeItem(container)
            return storeItem(item)
        }
        inventory.addItem(item)

        return true
    }

    fun removeItem(item: ItemStack) {
        // get the container with this item

        val container = connectedContainers.firstOrNull { it.getBlocks().any { it.isSimilar(item) } } ?: return

        // remove the item from the container
        val inventory = container.getInventory() ?: return run {
            removeItem(container)
           removeItem(item)
        }
        inventory.removeItem(item)
    }


}

package com.liamxsage.energeticstorage.listeners

import com.liamxsage.energeticstorage.cache.SystemCache
import com.liamxsage.energeticstorage.extensions.isNetworkInterface
import com.liamxsage.energeticstorage.managers.getConnectedSystemItems
import com.liamxsage.energeticstorage.model.Container
import com.liamxsage.energeticstorage.model.NetworkInterfaceType
import com.liamxsage.energeticstorage.model.NetworkItem

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent

class BlockBreakListener : Listener {

    @EventHandler(ignoreCancelled = true)
    fun onBlockBreak(event: BlockBreakEvent): Unit = with(event) {
        if (!block.isNetworkInterface) return println("Block is not a network interface")

        isDropItems = false

        println("Block broken: ${block.type}")

        if (block.type == NetworkInterfaceType.CORE.material) {

            val system = SystemCache.getSystemByBlock(block) ?: return
            SystemCache.removeSystem(system)

            system.connectedItems.forEach { item ->
                item.connectedCoreUUID = null
            }
        } else {
            println("Block broken: $block")
            val system = SystemCache.getSystemByItemBlock(block) ?: return

            system.removeItem(block)

            println("System: $system")

            val connectedItems = getConnectedSystemItems(system.block)

            // compare system.connectedItems to connectedItems and remove any that aren't connected to another core
            val itemsToRemove = mutableListOf<NetworkItem>()

            system.connectedItems.forEach { item ->
                if (connectedItems.none { it.value == item }) {
                    println("Marking item for removal: $item")
                    itemsToRemove.add(item) // Add the item to the list instead of removing it directly
                }
            }

            itemsToRemove.forEach { item ->
                system.removeItem(item)
                item.connectedCoreUUID = null
            }

            println("Connected items: ${connectedItems.filter { it.value is Container }.size}")


        }


    }



}
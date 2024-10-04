package com.liamxsage.energeticstorage.listeners

import com.liamxsage.energeticstorage.cache.SystemCache
import com.liamxsage.energeticstorage.extensions.isNetworkInterface
import com.liamxsage.energeticstorage.managers.getConnectedSystemItems
import com.liamxsage.energeticstorage.model.NetworkInterfaceType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent

class BlockBreakListener : Listener {

    @EventHandler(ignoreCancelled = true)
    fun onBlockBreak(event: BlockBreakEvent): Unit = with(event) {
        isDropItems = false

        println("Block broken: ${block.type}")

        if (block.type == NetworkInterfaceType.CORE.material) {

            val system = SystemCache.getSystemByBlock(block) ?: return
            SystemCache.removeSystem(system)

            system.connectedItems.forEach { item ->
                item.connectedCoreUUID = null
            }
        } else if (block.type == NetworkInterfaceType.CABLE.material || block.type == NetworkInterfaceType.CONTAINER.material || block.type == NetworkInterfaceType.HOPPER_IMPORT.material) {
            println("Block broken: $block")
            val system = SystemCache.getSystemByItemBlock(block) ?: return

            system.removeItem(block)

            println("System: $system")

            if (system.connectedCables.isEmpty()) {
                SystemCache.removeSystem(system)
            }

            val connectedItems = getConnectedSystemItems(system.block)


            // compare system.connectedItems to connectedItems and remove any that aren't connected to another core

            println("Connected items: $connectedItems")


        }


    }



}
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
        if (!block.isNetworkInterface) return@with
        isDropItems = false

        if (block.type == NetworkInterfaceType.CORE.material) {

            val system = SystemCache.getSystemByBlock(block) ?: return
            SystemCache.removeSystem(system)

            system.connectedItems.forEach { item ->
                item.connectedCoreUUID = null
            }
        }

        if (block.type == NetworkInterfaceType.CABLE.material || block.type == NetworkInterfaceType.CONTAINER.material) {
            val system = SystemCache.getSystemByBlock(block) ?: return

            system.removeItem(block)

            if (system.connectedCables.isEmpty()) {
                SystemCache.removeSystem(system)
            }

            val connectedItems = getConnectedSystemItems(block)


            // compare system.connectedItems to connectedItems and remove any that aren't connected to another core

            system.connectedItems.forEach { item ->
                if (connectedItems.none { it.value.block.location == item.block.location }) {
                    system.removeItem(item.block)
                    item.connectedCoreUUID = null
                }
            }


        }


    }



}
package com.liamxsage.energeticstorage.listeners

import com.liamxsage.energeticstorage.cache.SystemCache
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryMoveItemEvent
import org.bukkit.event.inventory.InventoryType

class HopperEvent: Listener {
    @EventHandler
    fun onItemMoveIntoHopper(event: InventoryMoveItemEvent) {
        if (event.destination.type != InventoryType.HOPPER) return
        if (event.destination.location == null) return
        val itemStack = event.item

        println("Item moved into hopper: ${itemStack.type}")

        val system = SystemCache.getSystemByItemBlock(event.destination.location!!.block) ?: return

        println("System found: ${system.block.location}")

        system.storeItem(itemStack)

        // remove item from hopper
        event.isCancelled = true
        event.item.amount = 0
    }
}
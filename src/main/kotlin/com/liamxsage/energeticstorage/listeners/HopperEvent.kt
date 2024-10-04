package com.liamxsage.energeticstorage.listeners

import com.liamxsage.energeticstorage.data.cache.SystemCache
import com.liamxsage.energeticstorage.extensions.isNetworkInterface
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryMoveItemEvent
import org.bukkit.event.inventory.InventoryPickupItemEvent
import org.bukkit.event.inventory.InventoryType

class HopperEvent: Listener {
    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    fun onItemMoveIntoHopper(event: InventoryMoveItemEvent) {
        println("Item moved into hopper")
        if (event.destination.type != InventoryType.HOPPER) return println("Inventory type is not hopper")
        if (event.destination.location == null) return println("Hopper location is null")

        if (!event.destination.location!!.block.isNetworkInterface) return println("Hopper is not a network interface")

        val itemStack = event.item

        println("Item moved into hopper: ${itemStack.type}")

        val system = SystemCache.getSystemByItemBlock(event.destination.location!!.block) ?: return

        println("System found: ${system.block.location}")

        val success = system.storeItem(itemStack)

        if (!success) {
            event.isCancelled = true
            return
        }

        // remove item from hopper
        event.item.amount = 0
    }


    @EventHandler(ignoreCancelled = true, priority = EventPriority.HIGHEST)
    fun onHopperPickupItem(event: InventoryPickupItemEvent) {
        println("Hopper pickup item event")
        if (event.inventory.type != InventoryType.HOPPER) return println("Inventory type is not hopper")
        if (event.inventory.location == null) return println("Hopper location is null")

        if (!event.inventory.location!!.block.isNetworkInterface) return println("Hopper is not a network interface")

        val itemStack = event.item.itemStack
        println("Item picked up by hopper: ${itemStack.type}")

        val system = SystemCache.getSystemByItemBlock(event.inventory.location!!.block) ?: return

        println("System found: ${system.block.location}")

        val success = system.storeItem(itemStack)

        if (!success) {

            event.isCancelled = true
            return
        }

        // remove item from hopper
        event.item.remove()
        event.isCancelled = true

    }
}
package com.liamxsage.energeticstorage.listeners

import com.liamxsage.energeticstorage.cache.SystemCache
import com.liamxsage.energeticstorage.cache.SystemCache.getSystemByBlock
import com.liamxsage.energeticstorage.cache.SystemCache.getSystemByItemBlock
import com.liamxsage.energeticstorage.extensions.*
import com.liamxsage.energeticstorage.managers.getConnectedSystemItems
import com.liamxsage.energeticstorage.model.Cable
import com.liamxsage.energeticstorage.model.Container
import com.liamxsage.energeticstorage.model.Core
import com.liamxsage.energeticstorage.model.NetworkInterfaceType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.inventory.ItemStack

class BlockPlaceListener : Listener {

    @EventHandler(ignoreCancelled = true)
    fun onBlockPlace(event: BlockPlaceEvent): Unit = with(event) {
        if (!itemInHand.isNetworkInterface) return@with
        val networkInterfaceType = getNetworkInterfaceType(itemInHand) ?: return@with

        when (networkInterfaceType) {
            NetworkInterfaceType.CABLE -> placeItem()
            NetworkInterfaceType.CORE -> placeCore()
            NetworkInterfaceType.CONTAINER -> placeItem()
        }

    }


    private fun BlockPlaceEvent.placeItem() {
        val block = blockPlaced
        val player = player

        val nearbyBlocks = sequenceOf(
            block.getRelative(1, 0, 0),
            block.getRelative(-1, 0, 0),
            block.getRelative(0, 1, 0),
            block.getRelative(0, -1, 0),
            block.getRelative(0, 0, 1),
            block.getRelative(0, 0, -1)
        ).filter { it.type == NetworkInterfaceType.CABLE.material || it.type == NetworkInterfaceType.CORE.material || it.type == NetworkInterfaceType.CONTAINER.material }

        if (nearbyBlocks.none()) {
            player.sendMessage("You must place this item next to a cable, core, or container.")
            isCancelled = true
            return
        }

        val nearbyCore = nearbyBlocks.firstOrNull { it.type == NetworkInterfaceType.CORE.material }
        val nearbyItem = nearbyBlocks.firstOrNull { it.type == NetworkInterfaceType.CABLE.material || it.type == NetworkInterfaceType.CONTAINER.material }

        // check if connected to a core if so check core location if any other check item location

        val networkItem = when (block.type) {
            NetworkInterfaceType.CABLE.material -> Cable(block)
            NetworkInterfaceType.CONTAINER.material -> Container(block)
            else -> return
        }

        player.sendMessage("Connected to a ${block.type}")


        val system: Core = if (nearbyCore != null) {
            getSystemByBlock(nearbyCore) ?: return
        } else {
            if (nearbyItem == null) return
            getSystemByItemBlock(nearbyItem) ?: return
        }

        player.sendMessage("Connected to a2 ${system.block.type}")

        networkItem.connectedCoreUUID = system.uuid
        networkItem.setBlockUUID()

        system.addItem(networkItem)

        player.sendMessage("Successfully placed the ${block.type}.")
        player.soundExecution()
    }

    private fun BlockPlaceEvent.placeCore() {
        val block = blockPlaced
        val player = player

        val core = Core( block = block )

        core.setBlockUUID()

        SystemCache.addSystem(core)

        val connectedItems = getConnectedSystemItems(block)

        connectedItems.forEach { item ->
            item.value.connectedCoreUUID = core.uuid
            item.value.setBlockUUID()
            core.addItem(item.value)
        }

        player.sendMessage("Successfully placed the core.")
        player.soundExecution()

    }

    private fun getNetworkInterfaceType(itemStack: ItemStack): NetworkInterfaceType? {
        return NetworkInterfaceType.entries.find { it.material == itemStack.type }
    }
}

// IMPORTANT: when a player is in an gui and gets an item we need to check if the item is in the inv of the system if not update the inv and don't give the item to the player
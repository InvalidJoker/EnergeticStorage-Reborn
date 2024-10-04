package com.liamxsage.energeticstorage.listeners

import com.liamxsage.energeticstorage.cache.SystemCache
import com.liamxsage.energeticstorage.extensions.toItemBuilder
import com.liamxsage.energeticstorage.model.Cable
import com.liamxsage.energeticstorage.model.Container
import com.liamxsage.energeticstorage.model.Core
import com.liamxsage.energeticstorage.model.HopperImporter
import org.bukkit.block.Chest
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryOpenEvent
import org.bukkit.event.inventory.InventoryType
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.inventory.ItemStack

class ItemClickListener : Listener {

    companion object {
        val itemClickEvents: MutableMap<ItemStack, (event: InventoryClickEvent) -> Unit> = mutableMapOf()
    }

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val item = event.currentItem ?: return
        val action = itemClickEvents[item] ?: return
        action(event)
    }

    @EventHandler
    fun onBlockInteract(event: InventoryOpenEvent) {
        val player = event.player

        player.sendMessage(SystemCache.getSystems().size.toString())
        player.sendMessage(SystemCache.getSystems()[0].connectedItems.size.toString())
        SystemCache.getSystems()[0].connectedItems.forEach {
            player.sendMessage(it.block.location.toString())
        }
        if (event.inventory.type != InventoryType.CHEST) return
        val holder: Chest = event.inventory.holder as? Chest ?: return


        val system = SystemCache.getSystemByItemBlock(holder.block) ?: return

        player.sendMessage("System found")
        player.sendMessage("Total items: ${system.totalItems}")
        player.sendMessage("Connected cables: ${system.connectedCables.size}")

    }

    @EventHandler
    fun playerJoin(event: PlayerJoinEvent) {
        val cable = Cable.createCableItem()
        // make size of cable 12

        val core = Core.createCoreItem()

        val storage = Container.createContainerItem()
        val hopper = HopperImporter.createHopperImportItem()

        event.player.inventory.addItem(cable.toItemBuilder {
            asAmount(12)
        }.build(), core, storage.toItemBuilder {
            asAmount(2)
        }.build(), hopper.toItemBuilder {
            asAmount(2)
        }.build())

    }
}
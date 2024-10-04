package com.liamxsage.energeticstorage.managers

import com.liamxsage.energeticstorage.NETWORK_INTERFACE_ID_NAMESPACE
import com.liamxsage.energeticstorage.cache.SystemCache
import com.liamxsage.energeticstorage.extensions.getKey
import com.liamxsage.energeticstorage.extensions.isNetworkInterface
import com.liamxsage.energeticstorage.model.NetworkInterfaceType
import com.liamxsage.energeticstorage.model.NetworkItem
import dev.fruxz.ascend.extension.forceCastOrNull
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.inventory.ItemStack
import java.util.*

private fun getFacesExcluding(face: BlockFace): List<BlockFace> {
    return BlockFace.entries.filter { it != face }
}

fun getConnectedSystemItems(
    block: Block,
    sourceFace: BlockFace? = null,
    iteration: Int = 0,
    visitedBlocks: MutableSet<Block> = mutableSetOf()
): Map<Block, NetworkItem> {
    if (!visitedBlocks.add(block)) return emptyMap()

    val faces = sourceFace?.let { getFacesExcluding(it) } ?: BlockFace.entries
    val connectedInterfaces = mutableMapOf<Block, NetworkItem>()

    for (face in faces) {
        val relativeBlock = block.getRelative(face)
        if (!relativeBlock.isNetworkInterface) continue

        val networkInterface = getNetwork(relativeBlock) ?: continue
        connectedInterfaces[relativeBlock] = networkInterface

        val iteratedInterfaces =
            getConnectedSystemItems(relativeBlock, face.oppositeFace, iteration + 1, visitedBlocks)

        // Merge iteratedInterfaces into connectedInterfaces
        iteratedInterfaces.forEach { (key, value) ->
            connectedInterfaces.putIfAbsent(key, value)
        }
    }

    return connectedInterfaces
}

fun getNetwork(block: Block): NetworkItem? {
    return when (block.type) {
        NetworkInterfaceType.CABLE.material -> SystemCache.getItemByBlock(block)
        NetworkInterfaceType.CORE.material -> SystemCache.getSystemByBlock(block)
        NetworkInterfaceType.CONTAINER.material -> SystemCache.getItemByBlock(block)
        else -> null
    }
}
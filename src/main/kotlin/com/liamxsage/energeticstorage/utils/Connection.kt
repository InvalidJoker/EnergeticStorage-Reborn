package com.liamxsage.energeticstorage.utils

import com.liamxsage.energeticstorage.data.cache.SystemCache
import com.liamxsage.energeticstorage.extensions.isNetworkInterface
import com.liamxsage.energeticstorage.data.enums.NetworkInterfaceType
import com.liamxsage.energeticstorage.data.interfaces.NetworkItem
import org.bukkit.block.Block
import org.bukkit.block.BlockFace

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
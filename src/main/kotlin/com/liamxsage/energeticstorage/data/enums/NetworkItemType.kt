package com.liamxsage.energeticstorage.data.enums

import com.liamxsage.energeticstorage.data.Cable
import com.liamxsage.energeticstorage.data.Container
import com.liamxsage.energeticstorage.data.HopperImporter
import com.liamxsage.energeticstorage.data.interfaces.NetworkItem
import org.bukkit.block.Block

enum class NetworkItemType {
    CONTAINER,
    HOPPER_IMPORT,
    CABLE;

    fun toNetworkItem(block: Block): NetworkItem = when(this) {
        CONTAINER -> Container(block)
        HOPPER_IMPORT -> HopperImporter(block)
        CABLE -> Cable(block)
    }
}
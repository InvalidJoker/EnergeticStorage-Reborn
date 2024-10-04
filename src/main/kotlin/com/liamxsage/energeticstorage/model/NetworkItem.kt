package com.liamxsage.energeticstorage.model

import org.bukkit.block.Block
import java.util.*

abstract class NetworkItem(open val block: Block) {
    abstract var connectedCoreUUID: UUID?

    abstract fun setBlockUUID(): NetworkItem
}
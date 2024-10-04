package com.liamxsage.energeticstorage.extensions

import com.liamxsage.energeticstorage.EnergeticStorage
import com.liamxsage.energeticstorage.NETWORK_INTERFACE_NAMESPACE
import com.liamxsage.energeticstorage.utils.customblockdata.CustomBlockData
import org.bukkit.block.Block

val Block.persistentDataContainer
    get() = CustomBlockData(this, EnergeticStorage.instance)

val Block.isNetworkInterface: Boolean
    get() = persistentDataContainer.has(NETWORK_INTERFACE_NAMESPACE)

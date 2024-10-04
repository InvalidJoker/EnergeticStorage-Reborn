package com.liamxsage.energeticstorage.model

import org.bukkit.Material

enum class NetworkInterfaceType(val material: Material) {
    CORE(Material.LODESTONE),
    CABLE(Material.YELLOW_CONCRETE),
    CONTAINER(Material.CHEST)
}
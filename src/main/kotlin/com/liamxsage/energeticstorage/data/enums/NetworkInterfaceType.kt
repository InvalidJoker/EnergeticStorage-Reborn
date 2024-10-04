package com.liamxsage.energeticstorage.data.enums

import org.bukkit.Material

enum class NetworkInterfaceType(val material: Material) {
    CORE(Material.LODESTONE),
    CABLE(Material.YELLOW_CONCRETE),
    CONTAINER(Material.CHEST),
    HOPPER_IMPORT(Material.HOPPER),
}
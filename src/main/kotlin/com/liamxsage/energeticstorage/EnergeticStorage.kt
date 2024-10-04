package com.liamxsage.energeticstorage

import com.liamxsage.energeticstorage.data.cache.SystemCache
import com.liamxsage.energeticstorage.data.database.DatabaseConnection
import org.bukkit.plugin.java.JavaPlugin
import kotlin.system.measureTimeMillis

class EnergeticStorage : JavaPlugin() {

    companion object {
        lateinit var instance: EnergeticStorage
            private set
    }



    init {
        instance = this
    }

    override fun onEnable() {
        // Plugin startup logic
        saveDefaultConfig()
        DatabaseConnection.connect()

        SystemCache.load()

        val time = measureTimeMillis {
            RegisterManager.registerListeners()
        }
        logger.info("Plugin enabled in $time ms")
        logger.info("EnergeticStorage is now tweaking your item storage behavior!")
    }

    override fun onDisable() {
        // Plugin shutdown logic
        SystemCache.save()
        DatabaseConnection.disconnect()

        logger.info("EnergeticStorage is now shutting down.")
    }
}
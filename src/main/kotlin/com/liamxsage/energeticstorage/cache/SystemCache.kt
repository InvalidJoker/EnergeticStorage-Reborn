package com.liamxsage.energeticstorage.cache

import com.liamxsage.energeticstorage.database.loadFromDatabase
import com.liamxsage.energeticstorage.database.save
import com.liamxsage.energeticstorage.database.saveToDatabase
import com.liamxsage.energeticstorage.model.Core
import com.liamxsage.energeticstorage.model.NetworkItem
import org.bukkit.block.Block
import java.util.*
import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantReadWriteLock

object SystemCache {

    private val cacheLock: ReadWriteLock = ReentrantReadWriteLock()
    private var cache = mapOf<UUID, Core>()

    fun addSystem(core: Core) {
        cacheLock.writeLock().lock()
        cache = cache + (core.uuid to core)
        cacheLock.writeLock().unlock()
    }

    fun removeSystem(core: Core) {
        cacheLock.writeLock().lock()
        cache = cache - core.uuid
        cacheLock.writeLock().unlock()
    }

    fun getSystems(): List<Core> {
        cacheLock.readLock().lock()
        val cores = cache.values.toList()
        cacheLock.readLock().unlock()
        return cores
    }


    fun getItemByBlock(block: Block): NetworkItem? {
        cacheLock.readLock().lock()
        val container = cache.values.flatMap { it.connectedItems }.find { it.block.location == block.location }
        cacheLock.readLock().unlock()
        return container
    }

    fun getSystemByItemBlock(block: Block): Core? {
        cacheLock.readLock().lock()
        val core = cache.values.find { it.connectedItems.any { it.block.location == block.location } }
        cacheLock.readLock().unlock()
        return core
    }

    fun getSystemByBlock(block: Block): Core? {
        cacheLock.readLock().lock()
        val core = cache.values.find { it.block == block }
        cacheLock.readLock().unlock()
        return core
    }

    fun save() {
        cacheLock.readLock().lock()

        saveToDatabase()

        cacheLock.readLock().unlock()
    }

    fun load() {
        cacheLock.writeLock().lock()
        loadFromDatabase()
        cacheLock.writeLock().unlock()
    }
}
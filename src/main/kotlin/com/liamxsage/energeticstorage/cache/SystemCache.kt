package com.liamxsage.energeticstorage.cache

import com.liamxsage.energeticstorage.model.Cable
import com.liamxsage.energeticstorage.model.Container
import com.liamxsage.energeticstorage.model.Core
import com.liamxsage.energeticstorage.model.NetworkItem
import org.bukkit.Location
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

    fun getSystem(uuid: UUID): Core? {
        cacheLock.readLock().lock()
        val core = cache[uuid]
        cacheLock.readLock().unlock()
        return core
    }

    fun getSystems(): List<Core> {
        cacheLock.readLock().lock()
        val cores = cache.values.toList()
        cacheLock.readLock().unlock()
        return cores
    }


    fun getSystemByItem(item: NetworkItem): Core? {
        cacheLock.readLock().lock()
        val core = cache.values.find { it.connectedItems.contains(item) }
        cacheLock.readLock().unlock()
        return core
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



}
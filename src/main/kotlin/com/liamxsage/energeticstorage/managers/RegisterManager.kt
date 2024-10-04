package com.liamxsage.energeticstorage.managers

import com.google.common.reflect.ClassPath
import com.liamxsage.energeticstorage.EnergeticStorage
import com.liamxsage.energeticstorage.PACKAGE_NAME
import com.liamxsage.energeticstorage.customblockdata.BlockDataListener
import com.liamxsage.energeticstorage.extensions.getLogger
import com.liamxsage.energeticstorage.listeners.BlockBreakListener
import com.liamxsage.energeticstorage.listeners.BlockPlaceListener
import com.liamxsage.energeticstorage.listeners.HopperEvent
import com.liamxsage.energeticstorage.listeners.ItemClickListener
import dev.fruxz.ascend.extension.forceCastOrNull
import org.bukkit.Bukkit
import kotlin.reflect.KClass

object RegisterManager {

    private val logger = getLogger()

    private fun <T : Any> loadClassesInPackage(packageName: String, clazzType: KClass<T>): List<KClass<out T>> {
        try {
            val classLoader = EnergeticStorage.instance.javaClass.classLoader
            val allClasses = ClassPath.from(classLoader).allClasses
            val classes = mutableListOf<KClass<out T>>()
            for (classInfo in allClasses) {
                if (!classInfo.name.startsWith(PACKAGE_NAME)) continue
                if (classInfo.packageName.startsWith(packageName) && !classInfo.name.contains('$')) {
                    try {
                        val loadedClass = classInfo.load().kotlin
                        if (clazzType.isInstance(loadedClass.javaObjectType.getDeclaredConstructor().newInstance())) {
                            classes.add(loadedClass.forceCastOrNull<KClass<out T>>() ?: continue)
                        }
                    } catch (_: Exception) {
                        // Ignore
                    }
                }
            }
            return classes
        } catch (exception: Exception) {
            logger.error("Failed to load classes", exception)
            return emptyList()
        }
    }

    /**
     * Loads all classes in a given package that are annotated with the specified annotation.
     *
     * @param packageName The name of the package.
     * @param annotation The annotation class.
     * @return A list of loaded classes annotated with the specified annotation.
     */
    private fun loadClassesInPackageWithAnnotation(
        packageName: String,
        annotation: KClass<out Annotation>
    ): List<KClass<out Any>> {
        try {
            val classLoader = EnergeticStorage.instance.javaClass.classLoader
            val allClasses = ClassPath.from(classLoader).allClasses
            val classes = mutableListOf<KClass<out Any>>()
            for (classInfo in allClasses) {
                if (!classInfo.name.startsWith(PACKAGE_NAME)) continue
                if (classInfo.packageName.startsWith(packageName) && !classInfo.name.contains('$')) {
                    try {
                        val loadedClass = classInfo.load().kotlin
                        if (loadedClass.annotations.any { it.annotationClass == annotation }) {
                            classes.add(loadedClass)
                        }
                    } catch (_: Exception) {
                        // Ignore
                    }
                }
            }
            return classes
        } catch (exception: Exception) {
            logger.error("Failed to load classes", exception)
            return emptyList()
        }
    }

    /**
     * Registers listeners by iterating through a list of listener classes and registering them
     * with the Bukkit plugin manager.
     */
    fun registerListeners() {
        val listenerClasses = listOf(
            ItemClickListener(),
            BlockBreakListener(),
            BlockPlaceListener(),
            BlockDataListener(),
            HopperEvent()
        )
        var amountListeners = 0
        for (listener in listenerClasses) {
            try {
                Bukkit.getPluginManager().registerEvents(listener, EnergeticStorage.instance)
                amountListeners++
            } catch (e: Exception) {
                logger.error("Exception while registering listener: ${listener.javaClass.simpleName}")
                e.printStackTrace()
            }
        }
        logger.info("Registered $amountListeners listeners")
    }
}
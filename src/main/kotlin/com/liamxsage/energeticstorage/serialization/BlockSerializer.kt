package com.liamxsage.energeticstorage.serialization


import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import org.bukkit.Bukkit
import org.bukkit.block.Block

val customJson = Json {
    serializersModule = SerializersModule {
        contextual(Block::class, BlockSerializer)
    }
}

object BlockSerializer : KSerializer<Block> {
    private const val BLOCK_SEPARATOR = ";"

    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("org.bukkit.block.Block", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Block {
        val raw = decoder.decodeString()

        val slices = raw.split(BLOCK_SEPARATOR)
        val worldName = slices[0]
        val world = Bukkit.getWorld(worldName) ?: throw IllegalArgumentException("World $worldName not found")

        return world.getBlockAt(
            slices[1].toInt(),
            slices[2].toInt(),
            slices[3].toInt(),
        )
    }

    override fun serialize(encoder: Encoder, value: Block) {
        encoder.encodeString("${value.world.name}$BLOCK_SEPARATOR${value.x}$BLOCK_SEPARATOR${value.y}$BLOCK_SEPARATOR${value.z}")
    }
}
package com.liamxsage.energeticstorage.serialization

import com.google.gson.TypeAdapter
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import org.bukkit.Bukkit
import org.bukkit.block.Block

class BlockAdapter : TypeAdapter<Block>() {
    override fun write(output: JsonWriter, block: Block?) {
        if (block == null) {
            output.nullValue()
            return
        }
        output.beginObject()
        output.value(convertFromStringBlock(block))
        output.endObject()
    }

    override fun read(input: JsonReader): Block {
        if (input.peek() == null) {
            input.nextNull()
            return Bukkit.getWorlds()[0].getBlockAt(0, 0, 0)
        }
        input.beginObject()
        val block = convertToBlock(input.nextString())
        input.endObject()
        return block
    }


    private fun convertFromStringBlock(block: Block) =
        "${block.world},${block.x},${block.y},${block.z}"

    private fun convertToBlock(string: String): Block {
        val split = string.split(",")

        val world = Bukkit.getWorld(split[0]) ?: Bukkit.getWorlds()[0]

        val block = world.getBlockAt(
            split[1].toInt(),
            split[2].toInt(),
            split[3].toInt()
        )

        return block
    }
}
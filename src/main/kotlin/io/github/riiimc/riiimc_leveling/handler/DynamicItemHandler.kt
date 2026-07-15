package io.github.riiimc.riiimc_leveling.handler

import net.minecraft.core.NonNullList
import net.minecraft.world.item.ItemStack
import net.neoforged.neoforge.items.ItemStackHandler

class DynamicItemHandler(size: Int) : ItemStackHandler(size) {

    fun resize(newSize: Int) {
        if (newSize == stacks.size) return

        val oldStacks = stacks.toList()

        stacks = NonNullList.withSize(newSize, ItemStack.EMPTY)

        oldStacks.take(newSize).forEachIndexed { index, stack ->
            stacks[index] = stack
        }

        onContentsChanged(-1)
    }
    override fun onContentsChanged(slot: Int) {
        // BlockEntity側でdirty
    }
}
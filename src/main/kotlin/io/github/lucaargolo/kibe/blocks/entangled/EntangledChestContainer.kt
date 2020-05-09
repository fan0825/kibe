package io.github.lucaargolo.kibe.blocks.entangled

import io.github.lucaargolo.kibe.blocks.ENTANGLED_CHEST
import net.minecraft.container.BlockContext
import net.minecraft.container.Container
import net.minecraft.container.Slot
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.PlayerInventory
import net.minecraft.inventory.Inventory
import net.minecraft.item.ItemStack
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

class EntangledChestContainer(syncId: Int, playerInventory: PlayerInventory, entity: EntangledChestEntity, val blockContext: BlockContext): Container(null, syncId) {

    var inventory: Inventory = object: Inventory {
        override fun getInvSize(): Int {
            return entity.invSize
        }

        override fun isInvEmpty(): Boolean {
            return entity.isInvEmpty
        }

        override fun getInvStack(slot: Int): ItemStack? {
            return entity.getInvStack(slot)
        }

        override fun removeInvStack(slot: Int): ItemStack? {
            val stack: ItemStack = entity.removeInvStack(slot)
            onContentChanged(this)
            return stack
        }

        override fun takeInvStack(slot: Int, amount: Int): ItemStack? {
            val stack: ItemStack = entity.takeInvStack(slot, amount)
            onContentChanged(this)
            return stack
        }

        override fun setInvStack(slot: Int, stack: ItemStack?) {
            entity.setInvStack(slot, stack)
            onContentChanged(this)
        }

        override fun markDirty() {
            entity.markDirty()
        }

        override fun canPlayerUseInv(player: PlayerEntity?): Boolean {
            return entity.canPlayerUseInv(player)
        }

        override fun clear() {
            entity.clear()
        }
    }

    init {
        checkContainerSize(inventory, 27)
        inventory.onInvOpen(playerInventory.player)
        val i: Int = (3 - 4) * 18

        var n: Int
        var m: Int

        n = 0
        while (n < 3) {
            m = 0
            while (m < 9) {
                addSlot(Slot(inventory, m + n * 9, 8 + m * 18, 18 + n * 18))
                ++m
            }
            ++n
        }

        n = 0
        while (n < 3) {
            m = 0
            while (m < 9) {
                addSlot(
                    Slot(
                        playerInventory,
                        m + n * 9 + 9,
                        8 + m * 18,
                        103 + n * 18 + i
                    )
                )
                ++m
            }
            ++n
        }

        n = 0
        while (n < 9) {
            addSlot(Slot(playerInventory, n, 8 + n * 18, 161 + i))
            ++n
        }

    }

    override fun canUse(player: PlayerEntity): Boolean {
        return blockContext.run({ world: World, blockPos: BlockPos ->
            if (world.getBlockState(
                    blockPos
                ).block != ENTANGLED_CHEST
            ) false else player.squaredDistanceTo(
                blockPos.x + .5,
                blockPos.y + .5,
                blockPos.z + .5
            ) < 64.0
        }, true)
    }

    override fun transferSlot(player: PlayerEntity?, invSlot: Int): ItemStack? {
        var itemStack = ItemStack.EMPTY
        val slot = this.slots[invSlot]
        if (slot != null && slot.hasStack()) {
            val itemStack2 = slot.stack
            itemStack = itemStack2.copy()
            if (invSlot < 27) {
                if (!insertItem(itemStack2, 27, this.slots.size, true)) {
                    return ItemStack.EMPTY
                }
            } else if (!insertItem(itemStack2, 0, 27, false)) {
                return ItemStack.EMPTY
            }
            if (itemStack2.isEmpty) {
                slot.stack = ItemStack.EMPTY
            } else {
                slot.markDirty()
            }
        }
        return itemStack
    }
}
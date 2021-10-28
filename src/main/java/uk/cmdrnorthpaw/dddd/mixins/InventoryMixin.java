package uk.cmdrnorthpaw.dddd.mixins;

import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(PlayerInventory.class)
public abstract class InventoryMixin {
    @Shadow @Final private List<DefaultedList<ItemStack>> combinedInventory;

    @Shadow @Final public PlayerEntity player;

    public void dropAll() {
        this.combinedInventory.forEach((inventory) -> {
            inventory.forEach(itemStack -> {
                ItemEntity droppedItem = this.player.dropItem(itemStack, true, false);

                NbtCompound nbt = new NbtCompound();
                if (droppedItem != null) {
                    droppedItem.writeCustomDataToNbt(nbt);
                    if (this.player.isDead()) nbt.putInt("Age", -32768);
                    droppedItem.readCustomDataFromNbt(nbt);
                }

            });
        });
    }
}

package uk.cmdrnorthpaw.dddd;

import com.google.common.collect.ImmutableList;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.stat.Stat;
import net.minecraft.util.collection.DefaultedList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(PlayerEntity.class)
public abstract class PlayerMixin {

    @Shadow public abstract void increaseStat(Stat<?> stat, int amount);

    @Redirect(method = "dropInventory", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;dropAll()V"))
    public void dropAll(PlayerInventory instance) {

        ((CombinedInventoryAccessor)instance).getCombinedInventory().forEach((inventory) -> {
            inventory.forEach(itemStack -> {
                ItemEntity droppedItem = instance.player.dropItem(itemStack, true, false);

                NbtCompound nbt = new NbtCompound();
                if (droppedItem != null) {
                    droppedItem.writeCustomDataToNbt(nbt);
                    if (instance.player.isDead()) nbt.putInt("Age", -32768);
                    droppedItem.readCustomDataFromNbt(nbt);
                }

            });
        });
    }

    @Mixin(PlayerInventory.class)
    private interface CombinedInventoryAccessor {
        @Accessor
        List<DefaultedList<ItemStack>> getCombinedInventory();
    }
}

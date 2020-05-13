package io.github.lucaargolo.kibe.mixin;

import io.github.lucaargolo.kibe.blocks.miscellaneous.Elevator;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class EntityMixin {

    @Shadow public abstract BlockPos getBlockPos();

    @Shadow public World world;

    @Shadow public abstract void teleport(double destX, double destY, double destZ);

    @Shadow public abstract void playSound(SoundEvent sound, float volume, float pitch);

    @Inject(at = @At("HEAD"), method = "setSneaking", cancellable = true)
    private void setSneaking(boolean sneaking, CallbackInfo info){
        if(sneaking) {
            BlockPos pos = getBlockPos();
            Block block = world.getBlockState(pos.down()).getBlock();
            if (block instanceof Elevator) {
                pos = pos.down();
                while(pos.getY() > 0) {
                    if(world.getBlockState(pos.down()).getBlock().equals(block) && Elevator.Companion.isElevatorValid(world, pos.down())) {
                        world.playSound(null, pos, SoundEvents.BLOCK_PISTON_CONTRACT, SoundCategory.BLOCKS, 0.5F, world.random.nextFloat() * 0.15F + 0.6F);
                        teleport(pos.down().getX()+0.5, pos.down().getY()+1.15, pos.down().getZ()+0.5);
                        break;
                    }else{
                        pos = pos.down();
                    }
                }
            }
        }

    }

}

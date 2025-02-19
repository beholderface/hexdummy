package net.beholderface.oneironaut.item;

import at.petrak.hexcasting.api.item.MediaHolderItem;
import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.api.utils.HexUtils;
import net.beholderface.oneironaut.Oneironaut;
import net.minecraft.block.Block;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.AliasedBlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class MonkfruitItem extends AliasedBlockItem {
    public MonkfruitItem(Block block, Settings settings) {
        super(block, settings);
    }

    @Override
    public ItemStack finishUsing(ItemStack stack, World world, LivingEntity user) {
        if (!world.isClient){
            double maxGaussian = 7.0;
            double rawGaussian = world.random.nextGaussian();
            double gaussian = Math.min(Math.max((rawGaussian * (maxGaussian / 2)) + (maxGaussian / 2), 0), maxGaussian);
            double overallReleased = gaussian + 3;
            //Oneironaut.LOGGER.info("Monkfruit releasing " + overallReleased + " dust, raw gaussian is " + rawGaussian);
            List<ItemStack> mediaHolders = new ArrayList<>();
            if (user instanceof PlayerEntity player){
                for (ItemStack checkedStack : player.getInventory().main){
                    if (checkedStack.getItem() instanceof MediaHolderItem battery){
                        if (battery.canRecharge(checkedStack) && battery.getMaxMedia(checkedStack) != battery.getMedia(checkedStack)){
                            mediaHolders.add(checkedStack);
                        }
                    }
                }
                if (player.getStackInHand(Hand.OFF_HAND).getItem() instanceof MediaHolderItem battery){
                    if (battery.canRecharge(player.getStackInHand(Hand.OFF_HAND))){
                        mediaHolders.add(player.getStackInHand(Hand.OFF_HAND));
                    }
                }
                int quantity = mediaHolders.size();
                for (ItemStack battery : mediaHolders){
                    MediaHolderItem type = (MediaHolderItem) battery.getItem();
                    double inserted = overallReleased / quantity;
                    type.insertMedia(battery, (int) (inserted * MediaConstants.DUST_UNIT), false);
                }
            }
        }
        return user.eatFood(world, stack);
    }
}

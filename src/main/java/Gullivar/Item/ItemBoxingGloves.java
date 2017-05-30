package Gullivar.Item;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemBoxingGloves extends Item {
	
	public ItemBoxingGloves() {
		this.setMaxDamage(80);
	}
	
	@Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 72000;
    }
	
	@Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack itemStackIn, World worldIn, EntityPlayer playerIn, EnumHand hand) {
		playerIn.setActiveHand(hand);
        return new ActionResult(EnumActionResult.SUCCESS, itemStackIn);
    }
	
	@Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.BOW;
    }
	
	@Override
    public void onPlayerStoppedUsing(ItemStack stack, World worldIn, EntityLivingBase entityLiving, int timeLeft) {
    	//Punch Something
    }

}

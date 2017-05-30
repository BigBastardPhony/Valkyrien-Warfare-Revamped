package Gullivar.Item;

import Gullivar.GullivarMod;
import Gullivar.Capability.ISizeCapability;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;
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
		if(!worldIn.isRemote){
			ISizeCapability sizeCapability = entityLiving.getCapability(GullivarMod.entitySize, null);
			double scale = 1D;
			if(sizeCapability != null) {
				scale = sizeCapability.getScaleValue();
			}
			
			Vec3d playerLook = entityLiving.getLookVec();
			double distance = scale * 4.5D;
			Vec3d startPos = entityLiving.getPositionVector().addVector(0,entityLiving.getEyeHeight() * scale,0);
			
			Vec3d endPos = startPos.add(new Vec3d(playerLook.xCoord * distance, playerLook.yCoord * distance, playerLook.zCoord * distance));
			
			RayTraceResult result = worldIn.rayTraceBlocks(startPos, endPos);
			
			if(result != null && result.typeOfHit != Type.MISS && result.hitVec != null){
				Vec3d hitLocation = result.hitVec;
				
				float explosionSize = 1F * (float) scale;
				
				worldIn.createExplosion(entityLiving, hitLocation.xCoord, hitLocation.yCoord, hitLocation.zCoord, explosionSize, true);
			}
		}
    	//Punch Something
    }

}

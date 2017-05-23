package Gullivar.Capability;

import Gullivar.GullivarMod;
import Gullivar.Network.EntityScaleMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

public class ImplISizeCapability implements ISizeCapability {

	protected double scale;
	protected double originalHeight;
	protected double originalEyeHeight;
	protected double originalWidth;

	@Override
	public double getScaleValue() {
		return scale;
	}

	@Override
	public double getOriginalHeight() {
		return originalHeight;
	}

	@Override
	public double getOriginalEyeHeight() {
		return originalEyeHeight;
	}

	@Override
	public double getOriginalWidth() {
		return originalWidth;
	}

	@Override
	public void setScaleValue(double scale) {
		this.scale = scale;
	}

	@Override
	public void setOriginalHeight(double originalHeight) {
		this.originalHeight = originalHeight;
	}

	@Override
	public void setOriginalEyeHeight(double originalEyeHeight) {
		this.originalEyeHeight = originalEyeHeight;
	}

	@Override
	public void setOriginalWidth(double originalWidth) {
		this.originalWidth = originalWidth;
	}

	@Override
	public int loadEntityDefaultsIfNone(EntityLivingBase toLoad) {
		if(scale == 0 && originalHeight == 0 && originalEyeHeight == 0 && originalWidth == 0){
			scale = 1.0D;
			originalHeight = toLoad.height;
			originalEyeHeight = toLoad.getEyeHeight();
			originalWidth = toLoad.width;
		}
		return -1;
	}

	@Override
	public void updateEntityScaleServer(EntityLivingBase toUpdate) {
		double newHeight = toUpdate.height * scale;
		double newEyeHeight = toUpdate.getEyeHeight() * scale;
		double newWidth = toUpdate.width * scale;

		if(toUpdate instanceof EntityPlayer){
			EntityPlayer player = (EntityPlayer)toUpdate;
			
			Minecraft.getMinecraft().playerController.getBlockReachDistance();
		}
		
		float f = toUpdate.width;
		toUpdate.width = (float) newWidth;
		toUpdate.height = (float) newHeight;
        AxisAlignedBB axisalignedbb = toUpdate.getEntityBoundingBox();
        toUpdate.setEntityBoundingBox(new AxisAlignedBB(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ, axisalignedbb.minX + (double)toUpdate.width, axisalignedbb.minY + (double)toUpdate.height, axisalignedbb.minZ + (double)toUpdate.width));

        if (toUpdate.width > f && !toUpdate.firstUpdate && !toUpdate.worldObj.isRemote)
        {
//        	toUpdate.moveEntity((double)(f - toUpdate.width), 0.0D, (double)(f - toUpdate.width));
        }
        
        if(!toUpdate.worldObj.isRemote){
        	EntityScaleMessage message = new EntityScaleMessage(toUpdate, (float) getScaleValue());
        	GullivarMod.GulliverSizeNetwork.sendToAllAround(message, new TargetPoint(toUpdate.dimension, toUpdate.posX, toUpdate.posY, toUpdate.posZ, 150D));
        	
        }
	}

}

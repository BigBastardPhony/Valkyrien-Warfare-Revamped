package Gullivar.Capability;

import java.lang.reflect.Method;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.AxisAlignedBB;

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
	public void updateEntityScale(EntityLivingBase toUpdate) {
		double newHeight = toUpdate.height * scale;
		double newEyeHeight = toUpdate.getEyeHeight() * scale;
		double newWidth = toUpdate.width * scale;

		float f = toUpdate.width;
		toUpdate.width = (float) newWidth;
		toUpdate.height = (float) newHeight;
        AxisAlignedBB axisalignedbb = toUpdate.getEntityBoundingBox();
        toUpdate.setEntityBoundingBox(new AxisAlignedBB(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ, axisalignedbb.minX + (double)toUpdate.width, axisalignedbb.minY + (double)toUpdate.height, axisalignedbb.minZ + (double)toUpdate.width));

        if (toUpdate.width > f && !toUpdate.firstUpdate && !toUpdate.worldObj.isRemote)
        {
//        	toUpdate.moveEntity((double)(f - toUpdate.width), 0.0D, (double)(f - toUpdate.width));
        }
	}

}

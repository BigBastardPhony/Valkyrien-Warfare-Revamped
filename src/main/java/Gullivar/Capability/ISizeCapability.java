package Gullivar.Capability;

import net.minecraft.entity.EntityLivingBase;

public interface ISizeCapability {

	public double getScaleValue();

	public double getOriginalHeight();

	public double getOriginalEyeHeight();

	public double getOriginalWidth();

	public void setScaleValue(double scale);

	public void setOriginalHeight(double originalHeight);

	public void setOriginalEyeHeight(double originalEyeHeight);

	public void setOriginalWidth(double originalWidth);

	public int loadEntityDefaultsIfNone(EntityLivingBase toLoad);

	public void updateEntityScale(EntityLivingBase toUpdate);

}

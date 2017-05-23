package ValkyrienWarfareControl;

import Gullivar.GullivarMod;
import Gullivar.Capability.ISizeCapability;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;

public class SmallPotion extends Potion {

	public SmallPotion(){
		super(false, 0);
	}
		
	@Override
	public void performEffect(EntityLivingBase base, int p_76394_2_) {
		ISizeCapability sizeCapability = base.getCapability(GullivarMod.entitySize, null);
	    if(base != null){
	    	float NewScale = (float) (sizeCapability.getScaleValue() * .3D);
	    	
	    	if(NewScale < .2)
	    	{
	    		NewScale = (float) .2;
	    	}
	    	sizeCapability.setScaleValue(NewScale);
	    	sizeCapability.updateEntityScaleServer(base);
	    }
			//entityLivingBaseIn.storePropety("MakeMeBig")
	 }
	
	public Potion setIconIndexVisible(int p_76399_1_, int p_76399_2_) {
        return this.setIconIndex(p_76399_1_, p_76399_2_);
    }
	
	@Override
    public boolean isReady(int duration, int amplifier) {
		return true;
    }
}

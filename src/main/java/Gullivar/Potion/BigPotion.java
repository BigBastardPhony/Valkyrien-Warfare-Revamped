package Gullivar.Potion;

import javax.annotation.Nullable;

import Gullivar.GullivarMod;
import Gullivar.Capability.ISizeCapability;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;

public class BigPotion extends Potion {

	public BigPotion() {
		super(false,0);
	}

	@Override
	public void performEffect(EntityLivingBase base, int p_76394_2_) {
			
		ISizeCapability sizeCapability = base.getCapability(GullivarMod.entitySize, null);
    	if(base != null){
    		float NewScale = (float) (sizeCapability.getScaleValue() * 3D);
    		
    		if(NewScale > 15)
    		{
    			NewScale = 15;
    		}
    		
    		sizeCapability.setScaleValue(NewScale);
    		sizeCapability.updateEntityScaleServer(base, true);
    		
    	}
		//entityLivingBaseIn.storePropety("MakeMeBig")
	}
	
	@Override
	public void affectEntity(@Nullable Entity source, @Nullable Entity indirectSource, EntityLivingBase entityLivingBaseIn, int amplifier, double health) {
		performEffect(entityLivingBaseIn, 1);
	}

	public Potion setIconIndexVisible(int p_76399_1_, int p_76399_2_) {
        return this.setIconIndex(p_76399_1_, p_76399_2_);
    }

	@Override
    public boolean isReady(int duration, int amplifier) {
		return true;
    }

}

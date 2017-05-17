package ValkyrienWarfareControl;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.Potion;

public class BigPotion extends Potion {

	public BigPotion()
	{
		super(false,0);
	}
	
	@Override
	 public void performEffect(EntityLivingBase entityLivingBaseIn, int p_76394_2_)
	 {
		//entityLivingBaseIn.storePropety("MakeMeBig")
	 }
	
	public Potion setIconIndexVisible(int p_76399_1_, int p_76399_2_)
    {
        return this.setIconIndex(p_76399_1_, p_76399_2_);
    }
	
}

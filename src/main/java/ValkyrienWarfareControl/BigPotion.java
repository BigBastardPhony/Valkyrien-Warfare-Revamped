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
	
}

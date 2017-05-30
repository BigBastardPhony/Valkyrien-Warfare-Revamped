package Gullivar;

import Gullivar.Item.ItemBoxingGloves;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class GulliverItems {

	public static void registerItems(GullivarMod instance){
		instance.boxingGloves = new ItemBoxingGloves().setUnlocalizedName("boxinggloves").setRegistryName(instance.MODID, "boxinggloves").setCreativeTab(CreativeTabs.COMBAT).setMaxStackSize(1);
		
		GameRegistry.register(instance.boxingGloves);
	}
}

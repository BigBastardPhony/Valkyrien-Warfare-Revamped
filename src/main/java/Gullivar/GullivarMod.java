package Gullivar;

import Gullivar.Capability.CapabilitiesRegistry;
import Gullivar.Capability.ISizeCapability;
import Gullivar.Network.EntityScaleMessage;
import Gullivar.Network.EntityScaleMessageHandler;
import Gullivar.Potion.BigPotion;
import Gullivar.Potion.SmallPotion;
import Gullivar.Proxy.CommonProxy;
import ValkyrienWarfareBase.ValkyrienWarfareMod;
import net.minecraft.item.Item;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLStateEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = GullivarMod.MODID, name = GullivarMod.MODNAME, version = GullivarMod.MODVER)
public class GullivarMod {

	public static final String MODID = "gullivar";
	public static final String MODNAME = "Gullivar Mod";
	public static final String MODVER = "1.0";

	@Instance(MODID)
	public static GullivarMod instance = new GullivarMod();
	
	@SidedProxy(clientSide = "Gullivar.Proxy.ClientProxy", serverSide = "Gullivar.Proxy.CommonProxy")
	public static CommonProxy proxy;

	@CapabilityInject(ISizeCapability.class)
	public static final Capability<ISizeCapability> entitySize = null;

	public static SimpleNetworkWrapper GulliverSizeNetwork;
	
	public Item boxingGloves;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		for(int i = 0; i < 100; i++){
//			System.out.println("OBAMA!");
		}
		CapabilitiesRegistry.registerCapabilities();
		registerNetworks(event);
		GulliverItems.registerItems(instance);
		proxy.preInit(event);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init(event);
		
		Potion.REGISTRY.register(28, new ResourceLocation("embiggening"), (new BigPotion()).setIconIndexVisible(6, 3).setPotionName("effect.embiggen").setBeneficial());
		PotionType.REGISTRY.register(911, new ResourceLocation("embiggening"), new PotionType("embiggening", new PotionEffect[]{new PotionEffect(Potion.REGISTRY.getObject(new ResourceLocation("embiggening")),1,1)}));
		
		Potion.REGISTRY.register(29, new ResourceLocation("ensmallening"), (new SmallPotion()).setIconIndexVisible(6 , 3).setPotionName("effect.ensmallen").setBeneficial());
		PotionType.REGISTRY.register(912, new ResourceLocation("ensmallening"), new PotionType("ensmallening", new PotionEffect[]{new PotionEffect(Potion.REGISTRY.getObject(new ResourceLocation("ensmallening")),1,1)}));


	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit(event);
	}

	private void registerNetworks(FMLStateEvent event) {
		GulliverSizeNetwork = NetworkRegistry.INSTANCE.newSimpleChannel("scalech");
		GulliverSizeNetwork.registerMessage(EntityScaleMessageHandler.class, EntityScaleMessage.class, 0, Side.CLIENT);
	}
	
}

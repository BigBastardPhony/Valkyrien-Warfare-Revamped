package Gullivar;

import Gullivar.Capability.CapabilitiesRegistry;
import Gullivar.Capability.ISizeCapability;
import Gullivar.Proxy.CommonProxy;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = GullivarMod.MODID, name = GullivarMod.MODNAME, version = GullivarMod.MODVER)
public class GullivarMod {

	public static final String MODID = "gullivarmod";
	public static final String MODNAME = "Gullivar Mod";
	public static final String MODVER = "1.0";

	@SidedProxy(clientSide = "Gullivar.Proxy.ClientProxy", serverSide = "Gullivar.Proxy.CommonProxy")
	public static CommonProxy proxy;

	@CapabilityInject(ISizeCapability.class)
	public static final Capability<ISizeCapability> entitySize = null;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		for(int i = 0; i < 100; i++){
//			System.out.println("OBAMA!");
		}
		CapabilitiesRegistry.registerCapabilities();
		proxy.preInit(event);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		proxy.init(event);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit(event);
	}

}

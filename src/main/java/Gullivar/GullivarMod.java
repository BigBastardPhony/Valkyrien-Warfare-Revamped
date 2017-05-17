package Gullivar;

import ValkyrienWarfareBase.PhysicsManagement.PhysicsWrapperEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.EntityRegistry;

@Mod(modid = GullivarMod.MODID, name = GullivarMod.MODNAME, version = GullivarMod.MODVER)
public class GullivarMod {

	public static final String MODID = "gullivarmod";
	public static final String MODNAME = "Gullivar Mod";
	public static final String MODVER = "1.0";
	
	@CapabilityInject(ISizeCapability.class)
	public static final Capability<ISizeCapability> entitySize = null;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		for(int i = 0; i < 100; i++){
//			System.out.println("OBAMA!");
		}
	}
	
	@EventHandler
	public void init(FMLInitializationEvent event) {

	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		
	}
	
}

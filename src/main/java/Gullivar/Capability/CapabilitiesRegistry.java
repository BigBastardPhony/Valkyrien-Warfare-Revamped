package Gullivar.Capability;

import net.minecraftforge.common.capabilities.CapabilityManager;

public class CapabilitiesRegistry {

	public static void registerCapabilities(){
		CapabilityManager.INSTANCE.register(ISizeCapability.class, new StorageScale(), ImplISizeCapability.class);
	}
}

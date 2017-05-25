package Gullivar.Network;

import Gullivar.GullivarMod;
import Gullivar.Capability.ISizeCapability;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.IThreadListener;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class EntityScaleMessageHandler implements IMessageHandler<EntityScaleMessage, IMessage> {

	@Override
	public IMessage onMessage(EntityScaleMessage message, MessageContext ctx) {
		
		if (Minecraft.getMinecraft().thePlayer == null) {
			return null;
		}

		IThreadListener mainThread = Minecraft.getMinecraft();
		mainThread.addScheduledTask(new Runnable() {
			@Override
			public void run() {
				Entity ent = Minecraft.getMinecraft().theWorld.getEntityByID(message.entityID);
				if (ent != null && ent instanceof EntityLivingBase) {
					ISizeCapability sizeCapability = ent.getCapability(GullivarMod.entitySize, null);
			    	if(sizeCapability != null){
			    		sizeCapability.setScaleValue(message.scale);
			    		sizeCapability.updateEntityScaleServer((EntityLivingBase) ent, true);
			    	}else{
			    		System.err.println("WHAT THE FUCK JUST HAPPENED HERE!!!");
			    	}
				}
			}
		});
		return null;
	}

}

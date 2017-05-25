package Gullivar;

import Gullivar.Capability.ISizeCapability;
import Gullivar.Network.EntityScaleMessage;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerRespawnEvent;

public class EventsCommon {

	@SubscribeEvent
	public void onEntityConstruct(AttachCapabilitiesEvent evt) {
		if(evt.getObject() instanceof EntityLivingBase){
			evt.addCapability(new ResourceLocation(GullivarMod.MODID, "ScaleCapability"), new ICapabilitySerializable<NBTTagByteArray>() {
				ISizeCapability inst = GullivarMod.entitySize.getDefaultInstance();
				int dummy = inst.loadEntityDefaultsIfNone((EntityLivingBase) evt.getObject());

				@Override
				public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
					return capability == GullivarMod.entitySize;
				}

				@Override
				public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
					return capability == GullivarMod.entitySize ? GullivarMod.entitySize.<T>cast(inst) : null;
				}

				@Override
				public NBTTagByteArray serializeNBT() {
					return (NBTTagByteArray) GullivarMod.entitySize.getStorage().writeNBT(GullivarMod.entitySize, inst, null);
				}

				@Override
				public void deserializeNBT(NBTTagByteArray nbt) {
					GullivarMod.entitySize.getStorage().readNBT(GullivarMod.entitySize, inst, null, nbt);
				}
			});
		}
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onEntityTrack(PlayerEvent.StartTracking event) {
		if (!event.getEntityPlayer().worldObj.isRemote) {
			Entity entity = event.getTarget();
			if(entity instanceof EntityLivingBase){
				ISizeCapability sizeCapability = entity.getCapability(GullivarMod.entitySize, null);
				if(sizeCapability != null){
					EntityScaleMessage message = new EntityScaleMessage(entity, (float) sizeCapability.getScaleValue());
					GullivarMod.GulliverSizeNetwork.sendTo(message, (EntityPlayerMP) event.getEntityPlayer());
				}
			}
		}
	}
	
	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onPlayerLoginEvent(PlayerLoggedInEvent event) {
		ISizeCapability sizeCapability = event.player.getCapability(GullivarMod.entitySize, null);
		if(sizeCapability != null){
			EntityScaleMessage message = new EntityScaleMessage(event.player, (float) sizeCapability.getScaleValue());
			GullivarMod.GulliverSizeNetwork.sendTo(message, (EntityPlayerMP) event.player);
			sizeCapability.updateEntityScaleServer(event.player, false);
		}
	}

	@SubscribeEvent(priority = EventPriority.HIGHEST)
	public void onPlayerRespawnEvent(PlayerRespawnEvent event) {
		ISizeCapability sizeCapability = event.player.getCapability(GullivarMod.entitySize, null);
		if(sizeCapability != null){
			EntityScaleMessage message = new EntityScaleMessage(event.player, (float) sizeCapability.getScaleValue());
			GullivarMod.GulliverSizeNetwork.sendTo(message, (EntityPlayerMP) event.player);
			sizeCapability.updateEntityScaleServer(event.player, false);
		}
	}

}

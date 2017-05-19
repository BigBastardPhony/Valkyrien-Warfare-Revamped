package Gullivar;

import Gullivar.Capability.ISizeCapability;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

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
			if(evt.getObject() instanceof EntityPlayer){
				System.out.println("SHIT!");
			}
		}
	}

}

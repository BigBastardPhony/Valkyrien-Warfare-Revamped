package Gullivar.Capability;

import Gullivar.DataTypes;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagByteArray;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class StorageScale implements IStorage<ISizeCapability>{

	@Override
	public NBTBase writeNBT(Capability<ISizeCapability> capability, ISizeCapability instance, EnumFacing side) {

		float[] values = new float[4];
		values[0] = (float) instance.getScaleValue();
		values[1] = (float) instance.getOriginalHeight();
		values[2] = (float) instance.getOriginalEyeHeight();
		values[3] = (float) instance.getOriginalWidth();

		byte[] toStore = DataTypes.toByteArray(values);

		NBTTagByteArray byteTag = new NBTTagByteArray(toStore);

		return byteTag;
	}

	@Override
	public void readNBT(Capability<ISizeCapability> capability, ISizeCapability instance, EnumFacing side, NBTBase nbt) {
		NBTTagByteArray byteTag = (NBTTagByteArray) nbt;

		byte[] readBytes = byteTag.getByteArray();
		float[] values = DataTypes.toFloatArray(readBytes);

		instance.setScaleValue(values[0]);
		instance.setOriginalHeight(values[1]);
		instance.setOriginalEyeHeight(values[2]);
		instance.setOriginalWidth(values[3]);
	}

}

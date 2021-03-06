package Gullivar.Network;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class EntityScaleMessage implements IMessage {

	Entity toApply;
	int entityID;
	float scale;
	
	public EntityScaleMessage(){}
	
	public EntityScaleMessage(Entity entityToScale, float newScale){
		toApply = entityToScale;
		entityID = entityToScale.entityId;
		scale = newScale;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		entityID = buf.readInt();
		scale = buf.readFloat();
	}
	
	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(entityID);
		buf.writeFloat(scale);
	}

}

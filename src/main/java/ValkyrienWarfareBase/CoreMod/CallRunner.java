package ValkyrienWarfareBase.CoreMod;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.base.Predicate;

import Gullivar.GullivarMod;
import Gullivar.Capability.ISizeCapability;
import ValkyrienWarfareBase.ValkyrienWarfareMod;
import ValkyrienWarfareBase.API.RotationMatrices;
import ValkyrienWarfareBase.API.Vector;
import ValkyrienWarfareBase.ChunkManagement.PhysicsChunkManager;
import ValkyrienWarfareBase.Collision.EntityCollisionInjector;
import ValkyrienWarfareBase.Collision.EntityPolygon;
import ValkyrienWarfareBase.Collision.Polygon;
import ValkyrienWarfareBase.Interaction.ShipUUIDToPosData.ShipPositionData;
import ValkyrienWarfareBase.Physics.BlockMass;
import ValkyrienWarfareBase.Physics.PhysicsQueuedForce;
import ValkyrienWarfareBase.PhysicsManagement.PhysicsWrapperEntity;
import ValkyrienWarfareBase.PhysicsManagement.WorldPhysObjectManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketEffect;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.server.management.PlayerList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.IChunkGenerator;
import net.minecraftforge.common.DimensionManager;

public class CallRunner {

	//TEMP; Removeme later
	public static boolean isServerInOnlineMode() {
		return false;
	}
	
	public static float onGetJumpUpwardsMotion(float original, EntityLivingBase livingbase) {
		ISizeCapability sizeCapability = livingbase.getCapability(GullivarMod.entitySize, null);
		if(sizeCapability != null){
			double scale = sizeCapability.getScaleValue();

			return (float) (original * Math.max(1D, Math.pow(scale, 1D/2D)));
		}
        return original;
    }

    public static void updateSize(EntityPlayer player) {
    	if(true){
//    		return;
    	}
    	float f;
        float f1;

//        System.out.print("GET IT!");

        if (player.isElytraFlying())
        {
            f = 0.6F;
            f1 = 0.6F;
        }
        else if (player.isPlayerSleeping())
        {
            f = 0.2F;
            f1 = 0.2F;
        }
        else if (player.isSneaking())
        {
            f = 0.6F;
            f1 = 1.65F;
        }
        else
        {
            f = 0.6F;
            f1 = 1.8F;
        }

        if (f != player.width || f1 != player.height)
        {
            AxisAlignedBB axisalignedbb = player.getEntityBoundingBox();
            axisalignedbb = new AxisAlignedBB(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ, axisalignedbb.minX + (double)f, axisalignedbb.minY + (double)f1, axisalignedbb.minZ + (double)f);

            if (!player.worldObj.collidesWithAnyBlock(axisalignedbb))
            {
            	float width = f;
            	float height = f1;
            	if (width != player.width || height != player.height)
                {

            		ISizeCapability sizeCapability = player.getCapability(GullivarMod.entitySize, null);
                	float scale = 1;
            		if(sizeCapability != null){
            			scale = (float) sizeCapability.getScaleValue();
                	}
                    f = player.width;
                    player.width = width * scale;
                    player.height = height * scale;
                    AxisAlignedBB axisalignedbb2 = player.getEntityBoundingBox();
                    player.setEntityBoundingBox(new AxisAlignedBB(axisalignedbb2.minX, axisalignedbb2.minY, axisalignedbb2.minZ, axisalignedbb2.minX + (double)player.width, axisalignedbb2.minY + (double)player.height, axisalignedbb2.minZ + (double)player.width));

                    if (player.width > f && !player.firstUpdate && !player.worldObj.isRemote)
                    {
                        player.moveEntity((double)(f - player.width), 0.0D, (double)(f - player.width));
                    }
                }
            }
        }
        net.minecraftforge.fml.common.FMLCommonHandler.instance().onPlayerPostTick(player);
    }

    public static float getEyeHeight(Entity e) {
//    	System.out.println("I WOrk!");
    	float originalHeight = e.getEyeHeight();
    	if(e instanceof EntityLivingBase){
    		ISizeCapability sizeCapability = e.getCapability(GullivarMod.entitySize, null);
    		if(sizeCapability != null){
    			double scale = sizeCapability.getScaleValue();
    			return (float)(originalHeight * scale);
    		}
    	}
        return originalHeight;
    }

	/**
	 * Fixes the chair dismount position
	 * @param entity
	 * @param potentialShip
	 */
	public static void onEntityDismountEntity(EntityLivingBase entity, Entity potentialShip){
		if(potentialShip instanceof PhysicsWrapperEntity){
			entity.ridingEntity = null;
			entity.posY += 1.45D;
			return;
		}
		entity.dismountEntity(potentialShip);
	}

	public static Iterator<Chunk> rebuildChunkIterator(Iterator<Chunk> chunkIterator){
		ArrayList<Chunk> newBackingArray = new ArrayList<Chunk>();
		while(chunkIterator.hasNext()){
			newBackingArray.add(chunkIterator.next());
		}
		return newBackingArray.iterator();
	}

    public static BlockPos getBedSpawnLocation(BlockPos toReturn, World worldIn, BlockPos bedLocation, boolean forceSpawn){
//    	System.out.println("Keep their heads ringing");

		int chunkX = bedLocation.getX() >> 4;
		int chunkZ = bedLocation.getZ() >> 4;
		long chunkPos = ChunkPos.chunkXZ2Int(chunkX, chunkZ);

		UUID shipManagingID = ValkyrienWarfareMod.chunkManager.getShipIDManagingPos_Persistant(worldIn, chunkX, chunkZ);
		if(shipManagingID != null){
			ShipPositionData positionData = ValkyrienWarfareMod.chunkManager.getShipPosition_Persistant(worldIn, shipManagingID);

			if(positionData != null){
				double[] lToWTransform = RotationMatrices.convertToDouble(positionData.lToWTransform);

				Vector bedPositionInWorld = new Vector(bedLocation.getX() + .5D, bedLocation.getY() + .5D, bedLocation.getZ() + .5D);
				RotationMatrices.applyTransform(lToWTransform, bedPositionInWorld);

				bedPositionInWorld.Y += 1D;

				bedLocation = new BlockPos(bedPositionInWorld.X, bedPositionInWorld.Y, bedPositionInWorld.Z);

				return bedLocation;
			}else{
				System.err.println("A ship just had Chunks claimed persistant, but not any position data persistant");
			}
		}

    	return toReturn;
    }

    private boolean[] alreadyTryingToSleep = new boolean[2];

    //Ported to event
//    public SleepResult trySleep(SleepResult result, EntityPlayer player, BlockPos bedLocation){
//    	PhysicsWrapperEntity wrapper = ValkyrienWarfareMod.physicsManager.getObjectManagingPos(player.worldObj, bedLocation);
//    	if(wrapper != null){
//    		player.addChatMessage(new TextComponentString("Spawn Point Set!"));
//    		player.playerLocation = bedLocation;
//    		return SleepResult.NOT_POSSIBLE_HERE;
//    	}
//    	return result;
//    }

	public static double getDistanceSq(TileEntity tile, double x, double y, double z){
		World tileWorld = tile.getWorld();
		double toReturn = tile.getDistanceSq(x, y, z);

//		System.out.println("test");

		if(tileWorld != null){

			//Assume on Ship
			if(tileWorld.isRemote && toReturn > 9999999D){
				BlockPos pos = tile.getPos();
				PhysicsWrapperEntity wrapper = ValkyrienWarfareMod.physicsManager.getObjectManagingPos(tile.getWorld(), pos);

				if(wrapper != null){
					Vector tilePos = new Vector(pos.getX() + .5D, pos.getY() + .5D, pos.getZ() + .5D);
					wrapper.wrapping.coordTransform.fromLocalToGlobal(tilePos);

					tilePos.X -= x;
					tilePos.Y -= y;
					tilePos.Z -= z;

					return tilePos.lengthSq();
				}
			}

		}
		return toReturn;
	}

	public static void markBlockRangeForRenderUpdate(World world, int x1, int y1, int z1, int x2, int y2, int z2){
//		System.out.println((x2-x1)*(y2-y1)*(z2-z1));
//		System.out.println(x1+":"+x2+":"+y1+":"+y2+":"+z1+":"+z2);

		//Stupid OpenComputers fix, blame those assholes
		if(x2 == 1 && y1 == 0 && z2 == 1){
			x2 = x1 + 1;
			x1--;

			y1 = y2 - 1;
			y2++;

			z2 = z1 + 1;
			z2--;
		}

		int midX = (x1 + x2) / 2;
		int midY = (y1 + y2) / 2;
		int midZ = (z1 + z2) / 2;
		BlockPos newPos = new BlockPos(midX, midY, midZ);
		PhysicsWrapperEntity wrapper = ValkyrienWarfareMod.physicsManager.getObjectManagingPos(world, newPos);
		if (wrapper != null && wrapper.wrapping.renderer != null) {
			wrapper.wrapping.renderer.updateRange(x1-1, y1-1, z1-1, x2+1, y2+1, z2+1);
		}

		if(wrapper == null){
			world.markBlockRangeForRenderUpdate(x1, y1, z1, x2, y2, z2);
		}
	}

	//Prevent random villages and shit from popping up on ships
    public static void onPopulateChunk(Chunk chunk, IChunkGenerator generator){
    	if(PhysicsChunkManager.isLikelyShipChunk(chunk.xPosition, chunk.zPosition)){
//        	System.out.println("Tried populating a Ship Chunk, but failed!");
    		return;
    	}
    	chunk.populateChunk(generator);
    }

    public static void onAddEntity(Chunk chunk, Entity entityIn){
    	World world = chunk.worldObj;

    	int i = MathHelper.floor_double(entityIn.posX / 16.0D);
        int j = MathHelper.floor_double(entityIn.posZ / 16.0D);

        if(i == chunk.xPosition && j == chunk.zPosition){
        	chunk.addEntity(entityIn);
        }else{
        	Chunk realChunkFor = world.getChunkFromChunkCoords(i, j);
        	if(!realChunkFor.isEmpty() && realChunkFor.isChunkLoaded){
        		realChunkFor.addEntity(entityIn);
        	}
        }
    }

	public static BlockPos onGetPrecipitationHeight(World world, BlockPos posToCheck) {
		BlockPos pos = world.getPrecipitationHeight(posToCheck);
		if(!world.isRemote || !ValkyrienWarfareMod.accurateRain){
			return pos;
		}else{
			return CallRunnerClient.onGetPrecipitationHeightClient(world, posToCheck);
		}
	}

	public static boolean onIsOnLadder(EntityLivingBase base) {
		boolean vanilla = base.isOnLadder();
		if (vanilla) {
			return true;
		}
		if (base instanceof EntityPlayer && ((EntityPlayer) base).isSpectator()) {
			return false;
		}
		List<PhysicsWrapperEntity> nearbyPhys = ValkyrienWarfareMod.physicsManager.getManagerForWorld(base.worldObj).getNearbyPhysObjects(base.getEntityBoundingBox());
		for (PhysicsWrapperEntity physWrapper : nearbyPhys) {
			Vector playerPos = new Vector(base);
			physWrapper.wrapping.coordTransform.fromGlobalToLocal(playerPos);
			int i = MathHelper.floor_double(playerPos.X);
			int j = MathHelper.floor_double(playerPos.Y);
			int k = MathHelper.floor_double(playerPos.Z);

			BlockPos blockpos = new BlockPos(i, j, k);
			IBlockState iblockstate = base.worldObj.getBlockState(blockpos);
			Block block = iblockstate.getBlock();

			boolean isSpectator = (base instanceof EntityPlayer && ((EntityPlayer) base).isSpectator());
			if (isSpectator)
				return false;

			EntityPolygon playerPoly = new EntityPolygon(base.getEntityBoundingBox(), physWrapper.wrapping.coordTransform.wToLTransform, base);
			AxisAlignedBB bb = playerPoly.getEnclosedAABB();
			for (int x = MathHelper.floor_double(bb.minX); x < bb.maxX; x++) {
				for (int y = Math.min(MathHelper.floor_double(bb.minY), 255); y < Math.min(bb.maxY, 255); y++) {
					for (int z = MathHelper.floor_double(bb.minZ); z < bb.maxZ; z++) {
						BlockPos pos = new BlockPos(x, y, z);
						IBlockState checkState = base.worldObj.getBlockState(pos);
						if (checkState.getBlock().isLadder(checkState, base.worldObj, pos, base)) {
							return true;
							// AxisAlignedBB ladderBB = checkState.getBlock().getBoundingBox(checkState, base.worldObj, pos).offset(pos).expandXyz(.1D);
							// Polygon checkBlock = new Polygon(ladderBB);
							// EntityPolygonCollider collider = new EntityPolygonCollider(playerPoly, checkBlock, physWrapper.wrapping.coordTransform.normals, new Vector(base.motionX,base.motionY,base.motionZ));
							//// System.out.println(!collider.seperated);
							// if(!collider.seperated){
							// return true;
							// }

						}
					}
				}
			}

			// return net.minecraftforge.common.ForgeHooks.isLivingOnLadder(iblockstate, base.worldObj, new BlockPos(i, j, k), base);
		}
		return false;
	}

	public static void onExplosionA(Explosion e) {
		Vector center = new Vector(e.explosionX, e.explosionY, e.explosionZ);
		World worldIn = e.worldObj;
		float radius = e.explosionSize;

		AxisAlignedBB toCheck = new AxisAlignedBB(center.X - radius, center.Y - radius, center.Z - radius, center.X + radius, center.Y + radius, center.Z + radius);
		List<PhysicsWrapperEntity> shipsNear = ValkyrienWarfareMod.physicsManager.getManagerForWorld(e.worldObj).getNearbyPhysObjects(toCheck);
		e.doExplosionA();
		// TODO: Make this compatible and shit!
		for (PhysicsWrapperEntity ship : shipsNear) {
			Vector inLocal = new Vector(center);
			RotationMatrices.applyTransform(ship.wrapping.coordTransform.wToLTransform, inLocal);
			// inLocal.roundToWhole();
			Explosion expl = new Explosion(ship.worldObj, null, inLocal.X, inLocal.Y, inLocal.Z, radius, false, false);

			double waterRange = .6D;

			boolean cancelDueToWater = false;

			for (int x = (int) Math.floor(expl.explosionX - waterRange); x <= Math.ceil(expl.explosionX + waterRange); x++) {
				for (int y = (int) Math.floor(expl.explosionY - waterRange); y <= Math.ceil(expl.explosionY + waterRange); y++) {
					for (int z = (int) Math.floor(expl.explosionZ - waterRange); z <= Math.ceil(expl.explosionZ + waterRange); z++) {
						if (!cancelDueToWater) {
							IBlockState state = e.worldObj.getBlockState(new BlockPos(x, y, z));
							if (state.getBlock() instanceof BlockLiquid) {
								cancelDueToWater = true;
							}
						}
					}
				}
			}

			expl.doExplosionA();

			double affectedPositions = 0D;

			for (Object o : expl.affectedBlockPositions) {
				BlockPos pos = (BlockPos) o;
				IBlockState state = ship.worldObj.getBlockState(pos);
				Block block = state.getBlock();
				if (!block.isAir(state, worldIn, (BlockPos) o) || ship.wrapping.explodedPositionsThisTick.contains((BlockPos) o)) {
					affectedPositions++;
				}
			}

			if (!cancelDueToWater) {
				for (Object o : expl.affectedBlockPositions) {
					BlockPos pos = (BlockPos) o;

					IBlockState state = ship.worldObj.getBlockState(pos);
					Block block = state.getBlock();
					if (!block.isAir(state, worldIn, (BlockPos) o) || ship.wrapping.explodedPositionsThisTick.contains((BlockPos) o)) {
						if (block.canDropFromExplosion(expl)) {
							block.dropBlockAsItemWithChance(ship.worldObj, pos, state, 1.0F / expl.explosionSize, 0);
						}
						block.onBlockExploded(ship.worldObj, pos, expl);
						if (!worldIn.isRemote) {
							Vector posVector = new Vector(pos.getX() + .5, pos.getY() + .5, pos.getZ() + .5);

							ship.wrapping.coordTransform.fromLocalToGlobal(posVector);

							double mass = BlockMass.basicMass.getMassFromState(state, pos, ship.worldObj);

							double explosionForce = Math.sqrt(e.explosionSize) * 1000D * mass;

							Vector forceVector = new Vector(pos.getX() + .5 - expl.explosionX, pos.getY() + .5 - expl.explosionY, pos.getZ() + .5 - expl.explosionZ);

							double vectorDist = forceVector.length();

							forceVector.normalize();

							forceVector.multiply(explosionForce / vectorDist);

							RotationMatrices.doRotationOnly(ship.wrapping.coordTransform.lToWRotation, forceVector);

							PhysicsQueuedForce queuedForce = new PhysicsQueuedForce(forceVector, posVector, false, 1);

							if (!ship.wrapping.explodedPositionsThisTick.contains(pos)) {
								ship.wrapping.explodedPositionsThisTick.add(pos);
							}

							ship.wrapping.queueForce(queuedForce);
						}
					}
				}
			}

		}
	}

	public static <T extends Entity> List<T> onGetEntitiesWithinAABB(World world, Class<? extends T> clazz, AxisAlignedBB aabb, @Nullable Predicate<? super T> filter) {
		List toReturn = world.getEntitiesWithinAABB(clazz, aabb, filter);

		BlockPos pos = new BlockPos((aabb.minX + aabb.maxX) / 2D, (aabb.minY + aabb.maxY) / 2D, (aabb.minZ + aabb.maxZ) / 2D);
		PhysicsWrapperEntity wrapper = ValkyrienWarfareMod.physicsManager.getObjectManagingPos(world, pos);
		if (wrapper != null) {
			Polygon poly = new Polygon(aabb, wrapper.wrapping.coordTransform.lToWTransform);
			aabb = poly.getEnclosedAABB();//.contract(.3D);
			toReturn.addAll(world.getEntitiesWithinAABB(clazz, aabb, filter));
		}
		return toReturn;
	}

	public static List<Entity> onGetEntitiesInAABBexcluding(World world, @Nullable Entity entityIn, AxisAlignedBB boundingBox, @Nullable Predicate<? super Entity> predicate) {
		if((boundingBox.maxX-boundingBox.minX)*(boundingBox.maxZ-boundingBox.minZ) > 1000000D){
			return new ArrayList();
		}

		//Prevents the players item pickup AABB from merging with a PhysicsWrapperEntity AABB
		if(entityIn instanceof EntityPlayer){
			EntityPlayer player = (EntityPlayer)entityIn;
			if (player.isRiding() && !player.getRidingEntity().isDead && player.getRidingEntity() instanceof PhysicsWrapperEntity){
                AxisAlignedBB axisalignedbb = player.getEntityBoundingBox().union(player.getRidingEntity().getEntityBoundingBox()).expand(1.0D, 0.0D, 1.0D);

                if(boundingBox.equals(axisalignedbb)){
                	boundingBox = player.getEntityBoundingBox().expand(1.0D, 0.5D, 1.0D);
                }
            }
		}

		List toReturn = world.getEntitiesInAABBexcluding(entityIn, boundingBox, predicate);

		BlockPos pos = new BlockPos((boundingBox.minX + boundingBox.maxX) / 2D, (boundingBox.minY + boundingBox.maxY) / 2D, (boundingBox.minZ + boundingBox.maxZ) / 2D);
		PhysicsWrapperEntity wrapper = ValkyrienWarfareMod.physicsManager.getObjectManagingPos(world, pos);
		if (wrapper != null) {
			Polygon poly = new Polygon(boundingBox, wrapper.wrapping.coordTransform.lToWTransform);
			boundingBox = poly.getEnclosedAABB().contract(.3D);
			toReturn.addAll(world.getEntitiesInAABBexcluding(entityIn, boundingBox, predicate));
		}
		return toReturn;
	}

//	public static Iterator<Chunk> onGetPersistentChunkIterable(World world, Iterator<Chunk> chunkIterator) {
//		Iterator<Chunk> vanillaResult = world.getPersistentChunkIterable(chunkIterator);
//		ArrayList<Chunk> newResultArray = new ArrayList<Chunk>();
//		while (vanillaResult.hasNext()) {
//			newResultArray.add(vanillaResult.next());
//		}
//		WorldPhysObjectManager manager = ValkyrienWarfareMod.physicsManager.getManagerForWorld(world);
//		ArrayList<PhysicsWrapperEntity> physEntities = (ArrayList<PhysicsWrapperEntity>) manager.physicsEntities.clone();
//		for (PhysicsWrapperEntity wrapper : physEntities) {
//			for (Chunk[] chunkArray : wrapper.wrapping.claimedChunks) {
//				for (Chunk chunk : chunkArray) {
//					newResultArray.add(chunk);
//				}
//			}
//		}
//		return newResultArray.iterator();
//	}

	public static boolean onCanInteractWith(Container con, EntityPlayer player) {
		boolean vanilla = con.canInteractWith(player);
		return true;
	}

	public static double getDistanceSq(double vanilla, Entity entity, double x, double y, double z) {
		if (vanilla < 64.0D) {
			return vanilla;
		} else {
			BlockPos pos = new BlockPos(x, y, z);
			PhysicsWrapperEntity wrapper = ValkyrienWarfareMod.physicsManager.getObjectManagingPos(entity.worldObj, pos);
			if (wrapper != null) {
				Vector posVec = new Vector(x, y, z);
				wrapper.wrapping.coordTransform.fromLocalToGlobal(posVec);
				posVec.X -= entity.posX;
				posVec.Y -= entity.posY;
				posVec.Z -= entity.posZ;
				if (vanilla > posVec.lengthSq()) {
					return posVec.lengthSq();
				}
			}
		}
		return vanilla;
	}

	public static double onGetDistanceSq(Entity entity, BlockPos pos) {
		double vanilla = entity.getDistanceSq(pos);
		if (vanilla < 64.0D) {
			return vanilla;
		} else {
			PhysicsWrapperEntity wrapper = ValkyrienWarfareMod.physicsManager.getObjectManagingPos(entity.worldObj, pos);
			if (wrapper != null) {
				Vector posVec = new Vector(pos.getX() + .5D, pos.getY() + .5D, pos.getZ() + .5D);
				wrapper.wrapping.coordTransform.fromLocalToGlobal(posVec);
				posVec.X -= entity.posX;
				posVec.Y -= entity.posY;
				posVec.Z -= entity.posZ;
				if (vanilla > posVec.lengthSq()) {
					return posVec.lengthSq();
				}
			}
		}
		return vanilla;
	}

	public static void onPlaySound(World world, double x, double y, double z, SoundEvent soundIn, SoundCategory category, float volume, float pitch, boolean distanceDelay) {
		BlockPos pos = new BlockPos(x, y, z);
		PhysicsWrapperEntity wrapper = ValkyrienWarfareMod.physicsManager.getObjectManagingPos(world, pos);
		if (wrapper != null) {
			Vector posVec = new Vector(x, y, z);
			wrapper.wrapping.coordTransform.fromLocalToGlobal(posVec);
			x = posVec.X;
			y = posVec.Y;
			z = posVec.Z;
		}
		world.playSound(x, y, z, soundIn, category, volume, pitch, distanceDelay);
	}

	public static double getDistanceSq(double ret, TileEntity ent, double x, double y, double z) {
		PhysicsWrapperEntity wrapper = ValkyrienWarfareMod.physicsManager.getObjectManagingPos(ent.getWorld(), ent.getPos());
		if (wrapper != null) {
			Vector vec = new Vector(x, y, z);
			wrapper.wrapping.coordTransform.fromGlobalToLocal(vec);
			double d0 = ent.getPos().getX() - vec.X;
	        double d1 = ent.getPos().getY() - vec.Y;
	        double d2 = ent.getPos().getZ() - vec.Z;
	        return d0 * d0 + d1 * d1 + d2 * d2;
		}
		return ret;
	}

/*	public static boolean onSpawnEntityInWorld(World world, Entity entity) {
		BlockPos posAt = new BlockPos(entity);
		PhysicsWrapperEntity wrapper = ValkyrienWarfareMod.physicsManager.getObjectManagingPos(world, posAt);
		if (!(entity instanceof EntityFallingBlock) && wrapper != null && wrapper.wrapping.coordTransform != null) {
			if (entity instanceof EntityMountingWeaponBase || entity instanceof EntityArmorStand || entity instanceof EntityPig || entity instanceof EntityBoat) {
//				entity.startRiding(wrapper);
				wrapper.wrapping.fixEntity(entity, new Vector(entity));
				wrapper.wrapping.queueEntityForMounting(entity);
			}
			RotationMatrices.applyTransform(wrapper.wrapping.coordTransform.lToWTransform, wrapper.wrapping.coordTransform.lToWRotation, entity);
		}
		return world.spawnEntityInWorld(entity);
	}*/

	public static void onSendToAllNearExcept(PlayerList list, @Nullable EntityPlayer except, double x, double y, double z, double radius, int dimension, Packet<?> packetIn) {
		BlockPos pos = new BlockPos(x, y, z);
		World worldIn = null;
		if (except == null) {
			worldIn = DimensionManager.getWorld(dimension);
		} else {
			worldIn = except.worldObj;
		}
		PhysicsWrapperEntity wrapper = ValkyrienWarfareMod.physicsManager.getObjectManagingPos(worldIn, pos);
		Vector packetPosition = new Vector(x, y, z);
		if (wrapper != null && wrapper.wrapping.coordTransform != null) {
			wrapper.wrapping.coordTransform.fromLocalToGlobal(packetPosition);

			if (packetIn instanceof SPacketSoundEffect) {
				SPacketSoundEffect soundEffect = (SPacketSoundEffect) packetIn;
				packetIn = new SPacketSoundEffect(soundEffect.sound, soundEffect.category, packetPosition.X, packetPosition.Y, packetPosition.Z, soundEffect.soundVolume, soundEffect.soundPitch);
			}
			//
			if (packetIn instanceof SPacketEffect) {
				SPacketEffect effect = (SPacketEffect) packetIn;
				BlockPos blockpos = new BlockPos(packetPosition.X, packetPosition.Y, packetPosition.Z);
				packetIn = new SPacketEffect(effect.soundType, blockpos, effect.soundData, effect.serverWide);
			}
		}

		x = packetPosition.X;
		y = packetPosition.Y;
		z = packetPosition.Z;

		// list.sendToAllNearExcept(except, packetPosition.X, packetPosition.Y, packetPosition.Z, radius, dimension, packetIn);

		for (int i = 0; i < list.playerEntityList.size(); ++i) {
			EntityPlayerMP entityplayermp = (EntityPlayerMP) list.playerEntityList.get(i);

			if (entityplayermp != except && entityplayermp.dimension == dimension) {
				// NOTE: These are set to use the last variables for a good reason; dont change them
				double d0 = x - entityplayermp.posX;
				double d1 = y - entityplayermp.posY;
				double d2 = z - entityplayermp.posZ;

				if (d0 * d0 + d1 * d1 + d2 * d2 < radius * radius) {
					entityplayermp.connection.sendPacket(packetIn);
				} else {
					d0 = x - entityplayermp.lastTickPosX;
					d1 = y - entityplayermp.lastTickPosY;
					d2 = z - entityplayermp.lastTickPosZ;
					if (d0 * d0 + d1 * d1 + d2 * d2 < radius * radius) {
						entityplayermp.connection.sendPacket(packetIn);
					}
				}
			}
		}
	}

	public static void onSetBlockState(IBlockState newState, int flags, World world, BlockPos pos){
		IBlockState oldState = world.getBlockState(pos);
		PhysicsWrapperEntity wrapper = ValkyrienWarfareMod.physicsManager.getObjectManagingPos(world, pos);
		if (wrapper != null) {
			wrapper.wrapping.onSetBlockState(oldState, newState, pos);
		}
    }

	public static RayTraceResult onRayTraceBlocks(World world, Vec3d vec31, Vec3d vec32, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox, boolean returnLastUncollidableBlock) {
		return rayTraceBlocksIgnoreShip(world, vec31, vec32, stopOnLiquid, ignoreBlockWithoutBoundingBox, returnLastUncollidableBlock, null);
	}

	public static RayTraceResult rayTraceBlocksIgnoreShip(World world, Vec3d vec31, Vec3d vec32, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox, boolean returnLastUncollidableBlock, PhysicsWrapperEntity toIgnore) {
		RayTraceResult vanillaTrace = world.rayTraceBlocks(vec31, vec32, stopOnLiquid, ignoreBlockWithoutBoundingBox, returnLastUncollidableBlock);
		WorldPhysObjectManager physManager = ValkyrienWarfareMod.physicsManager.getManagerForWorld(world);
		if (physManager == null) {
			return vanillaTrace;
		}

		Vec3d playerEyesPos = vec31;
		Vec3d playerReachVector = vec32.subtract(vec31);

		AxisAlignedBB playerRangeBB = new AxisAlignedBB(vec31.xCoord, vec31.yCoord, vec31.zCoord, vec32.xCoord, vec32.yCoord, vec32.zCoord);

		List<PhysicsWrapperEntity> nearbyShips = physManager.getNearbyPhysObjects(playerRangeBB);
		//Get rid of the Ship that we're not supposed to be RayTracing for
		nearbyShips.remove(toIgnore);

		boolean changed = false;

		double reachDistance = playerReachVector.lengthVector();
		double worldResultDistFromPlayer = 420000000D;
		if (vanillaTrace != null && vanillaTrace.hitVec != null) {
			worldResultDistFromPlayer = vanillaTrace.hitVec.distanceTo(vec31);
		}
		for (PhysicsWrapperEntity wrapper : nearbyShips) {
			playerEyesPos = vec31;
			playerReachVector = vec32.subtract(vec31);
			// TODO: Re-enable
			if (world.isRemote) {
				// ValkyrienWarfareMod.proxy.updateShipPartialTicks(wrapper);
			}
			// Transform the coordinate system for the player eye pos
			playerEyesPos = RotationMatrices.applyTransform(wrapper.wrapping.coordTransform.RwToLTransform, playerEyesPos);
			playerReachVector = RotationMatrices.applyTransform(wrapper.wrapping.coordTransform.RwToLRotation, playerReachVector);
			Vec3d playerEyesReachAdded = playerEyesPos.addVector(playerReachVector.xCoord * reachDistance, playerReachVector.yCoord * reachDistance, playerReachVector.zCoord * reachDistance);
			RayTraceResult resultInShip = world.rayTraceBlocks(playerEyesPos, playerEyesReachAdded, stopOnLiquid, ignoreBlockWithoutBoundingBox, returnLastUncollidableBlock);
			if (resultInShip != null && resultInShip.hitVec != null && resultInShip.typeOfHit == Type.BLOCK) {
				double shipResultDistFromPlayer = resultInShip.hitVec.distanceTo(playerEyesPos);
				if (shipResultDistFromPlayer < worldResultDistFromPlayer) {
					worldResultDistFromPlayer = shipResultDistFromPlayer;
					resultInShip.hitVec = RotationMatrices.applyTransform(wrapper.wrapping.coordTransform.RlToWTransform, resultInShip.hitVec);
					vanillaTrace = resultInShip;
				}
			}
		}

		return vanillaTrace;
	}

	public static void onEntityMove(Entity entity, double dx, double dy, double dz) {
		double movDistSq = (dx*dx) + (dy*dy) + (dz*dz);
		if(movDistSq > 1000000){
			//Assume this will take us to Ship coordinates
			double newX = entity.posX + dx;
			double newY = entity.posY + dy;
			double newZ = entity.posZ + dz;
			BlockPos newPosInBlock = new BlockPos(newX,newY,newZ);

			PhysicsWrapperEntity wrapper = ValkyrienWarfareMod.physicsManager.getObjectManagingPos(entity.worldObj, newPosInBlock);

			if(wrapper == null){
//				Just forget this even happened
//				System.err.println("An entity just tried moving like a millions miles an hour. Probably VW's fault, sorry about that.");
				return;
			}

			Vector endPos = new Vector(newX,newY,newZ);
			RotationMatrices.applyTransform(wrapper.wrapping.coordTransform.wToLTransform, endPos);

			dx = endPos.X - entity.posX;
			dy = endPos.Y - entity.posY;
			dz = endPos.Z - entity.posZ;
		}
		if (!EntityCollisionInjector.alterEntityMovement(entity, dx, dy, dz)) {
			entity.moveEntity(dx, dy, dz);
		}
	}
    public static Vec3d onGetLook(Entity entityFor, float partialTicks){
    	Vec3d defaultOutput = entityFor.getLook(partialTicks);

    	PhysicsWrapperEntity wrapper = ValkyrienWarfareMod.physicsManager.getShipFixedOnto(entityFor);
		if(wrapper != null){
			Vector newOutput = new Vector(defaultOutput);
			RotationMatrices.applyTransform(wrapper.wrapping.coordTransform.RlToWRotation, newOutput);
			return newOutput.toVec3d();
		}

    	return defaultOutput;
    }

}

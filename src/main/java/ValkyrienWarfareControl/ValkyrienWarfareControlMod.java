package ValkyrienWarfareControl;

import java.io.File;

import ValkyrienWarfareBase.ValkyrienWarfareMod;
import ValkyrienWarfareBase.Capability.IAirshipCounterCapability;
import ValkyrienWarfareControl.Block.BlockBalloonBurner;
import ValkyrienWarfareControl.Block.BlockDopedEtherium;
import ValkyrienWarfareControl.Block.BlockHovercraftController;
import ValkyrienWarfareControl.Block.BlockShipPassengerChair;
import ValkyrienWarfareControl.Block.BlockShipPilotsChair;
import ValkyrienWarfareControl.Block.BlockThrustModulator;
import ValkyrienWarfareControl.Block.BlockThrustRelay;
import ValkyrienWarfareControl.Block.Engine.BlockNormalEngine;
import ValkyrienWarfareControl.Block.Engine.BlockRedstoneEngine;
import ValkyrienWarfareControl.Block.EtherCompressor.BlockCreativeEtherCompressor;
import ValkyrienWarfareControl.Block.EtherCompressor.BlockNormalEtherCompressor;
import ValkyrienWarfareControl.Capability.ICapabilityLastRelay;
import ValkyrienWarfareControl.Capability.ImplCapabilityLastRelay;
import ValkyrienWarfareControl.Capability.StorageLastRelay;
import ValkyrienWarfareControl.GUI.ControlGUIHandler;
import ValkyrienWarfareControl.Item.ItemRelayWire;
import ValkyrienWarfareControl.Item.ItemShipStealer;
import ValkyrienWarfareControl.Item.ItemSystemLinker;
import ValkyrienWarfareControl.Network.EntityFixMessage;
import ValkyrienWarfareControl.Network.EntityFixMessageHandler;
import ValkyrienWarfareControl.Network.HovercraftControllerGUIInputHandler;
import ValkyrienWarfareControl.Network.HovercraftControllerGUIInputMessage;
import ValkyrienWarfareControl.Piloting.PilotControlsMessage;
import ValkyrienWarfareControl.Piloting.PilotControlsMessageHandler;
import ValkyrienWarfareControl.Piloting.SetShipPilotMessage;
import ValkyrienWarfareControl.Piloting.SetShipPilotMessageHandler;
import ValkyrienWarfareControl.Proxy.CommonProxyControl;
import ValkyrienWarfareControl.TileEntity.BalloonBurnerTileEntity;
import ValkyrienWarfareControl.TileEntity.PilotsChairTileEntity;
import ValkyrienWarfareControl.TileEntity.TileEntityHoverController;
import ValkyrienWarfareControl.TileEntity.TileEntityNormalEtherCompressor;
import ValkyrienWarfareWorld.ValkyrienWarfareWorldMod;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.config.Configuration;
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
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = ValkyrienWarfareControlMod.MODID, name = ValkyrienWarfareControlMod.MODNAME, version = ValkyrienWarfareControlMod.MODVER)
public class ValkyrienWarfareControlMod {

	public static Configuration config;

	public static final String MODID = "valkyrienwarfarecontrol";
	public static final String MODNAME = "Valkyrien Warfare Control";
	public static final String MODVER = "0.9_alpha";

    @Instance(MODID)
	public static ValkyrienWarfareControlMod instance = new ValkyrienWarfareControlMod();

	public static SimpleNetworkWrapper controlNetwork;

	public Block basicEngine;
	public Block advancedEngine;
	public Block eliteEngine;
	public Block ultimateEngine; // Couldn't think of what to name these, so I went with the Mekanism naming style
	public Block redstoneEngine;

	public Block basicHoverController;
	public Block dopedEtherium;
	public Block balloonBurner;
	public Block pilotsChair;
	public Block passengerChair;

	public Block antigravityEngine; //leaving it with the old name to prevent blocks dissapearing
	public Block advancedEtherCompressor;
	public Block eliteEtherCompressor;
	public Block ultimateEtherCompressor;
	public Block creativeEtherCompressor;

	public Block thrustRelay;
	public Block thrustModulator;

	public Item systemLinker;
	public Item airshipStealer;
	public Item relayWire;

	@SidedProxy(clientSide = "ValkyrienWarfareControl.Proxy.ClientProxyControl", serverSide = "ValkyrienWarfareControl.Proxy.CommonProxyControl")
	public static CommonProxyControl proxy;

	@CapabilityInject(ICapabilityLastRelay.class)
	public static final Capability<ICapabilityLastRelay> lastRelayCapability = null;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		config = new Configuration(new File(ValkyrienWarfareMod.getWorkingFolder() + "/config/valkyrienwarfarecontrol.cfg"));
		config.load();
		proxy.preInit(event);
		config.save();

		registerBlocks(event);
		registerItems(event);
		registerCapibilities(event);

		proxy.preInit(event);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		registerTileEntities(event);

		registerRecipies(event);
		registerNetworks(event);
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new ControlGUIHandler());

		proxy.init(event);
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		proxy.postInit(event);
	}

	private void registerBlocks(FMLStateEvent event) {
		double basicEnginePower = config.get(Configuration.CATEGORY_GENERAL, "basicEnginePower", 4000D, "Engine power for the basic Engine").getDouble();
		double advancedEnginePower = config.get(Configuration.CATEGORY_GENERAL, "advancedEnginePower", 6000D, "Engine power for the advanced Engine").getDouble();
		double eliteEnginePower = config.get(Configuration.CATEGORY_GENERAL, "eliteEnginePower", 8000D, "Engine power for the elite Engine").getDouble();
		double ultimateEnginePower = config.get(Configuration.CATEGORY_GENERAL, "ultimateEnginePower", 16000D, "Engine power for the ultimate Engine").getDouble();
		double redstoneEnginePower = config.get(Configuration.CATEGORY_GENERAL, "redstoneEnginePower", 500D, "Multiplied by the redstone power (0-15) to the Redstone Engine").getDouble();

		double basicEtherCompressorPower = config.get(Configuration.CATEGORY_GENERAL, "basicEtherCompressorPower", 25000D, "Engine power for the basic Ether Compressor").getDouble();
		double advancedEtherCompressorPower = config.get(Configuration.CATEGORY_GENERAL, "advancedEtherCompressorPower", 45000D, "Engine power for the advanced Ether Compressor").getDouble();
		double eliteEtherCompressorPower = config.get(Configuration.CATEGORY_GENERAL, "eliteEtherCompressorPower", 80000D, "Engine power for the elite Ether Compressor").getDouble();
		double ultimateEtherCompressorPower = config.get(Configuration.CATEGORY_GENERAL, "ultimateEtherCompressorPower", 100000D, "Engine power for the ultimate Ether Compressor").getDouble();

		basicEngine = new BlockNormalEngine(Material.WOOD, basicEnginePower).setHardness(5f).setUnlocalizedName("basicEngine").setRegistryName(MODID, "basicEngine").setCreativeTab(CreativeTabs.TRANSPORTATION);
		advancedEngine = new BlockNormalEngine(Material.ROCK, advancedEnginePower).setHardness(6f).setUnlocalizedName("advancedEngine").setRegistryName(MODID, "advancedEngine").setCreativeTab(CreativeTabs.TRANSPORTATION);
		eliteEngine = new BlockNormalEngine(Material.IRON, eliteEnginePower).setHardness(8f).setUnlocalizedName("eliteEngine").setRegistryName(MODID, "eliteEngine").setCreativeTab(CreativeTabs.TRANSPORTATION);
		ultimateEngine = new BlockNormalEngine(Material.GROUND, ultimateEnginePower).setHardness(10f).setUnlocalizedName("ultimateEngine").setRegistryName(MODID, "ultimateEngine").setCreativeTab(CreativeTabs.TRANSPORTATION);
		redstoneEngine = new BlockRedstoneEngine(Material.REDSTONE_LIGHT, redstoneEnginePower).setHardness(7.0f).setUnlocalizedName("redstoneEngine").setRegistryName(MODID, "redstoneEngine").setCreativeTab(CreativeTabs.TRANSPORTATION);

		antigravityEngine = new BlockNormalEtherCompressor(Material.WOOD, basicEtherCompressorPower).setHardness(8f).setUnlocalizedName("antigravengine").setRegistryName(MODID, "antigravengine").setCreativeTab(CreativeTabs.TRANSPORTATION);
		advancedEtherCompressor = new BlockNormalEtherCompressor(Material.ROCK, advancedEtherCompressorPower).setHardness(8f).setUnlocalizedName("advancedEtherCompressor").setRegistryName(MODID, "advancedEtherCompressor").setCreativeTab(CreativeTabs.TRANSPORTATION);
		eliteEtherCompressor = new BlockNormalEtherCompressor(Material.IRON, eliteEtherCompressorPower).setHardness(8f).setUnlocalizedName("eliteEtherCompressor").setRegistryName(MODID, "eliteEtherCompressor").setCreativeTab(CreativeTabs.TRANSPORTATION);
		ultimateEtherCompressor = new BlockNormalEtherCompressor(Material.GROUND, ultimateEtherCompressorPower).setHardness(8f).setUnlocalizedName("ultimateEtherCompressor").setRegistryName(MODID, "ultimateEtherCompressor").setCreativeTab(CreativeTabs.TRANSPORTATION);
		creativeEtherCompressor = new BlockCreativeEtherCompressor(Material.BARRIER, Double.MAX_VALUE / 4).setHardness(0.0f).setUnlocalizedName("creativeEtherCompressor").setRegistryName(MODID, "creativeEtherCompressor").setCreativeTab(CreativeTabs.TRANSPORTATION);

		basicHoverController = new BlockHovercraftController(Material.IRON).setHardness(10f).setUnlocalizedName("basichovercraftcontroller").setRegistryName(MODID, "basichovercraftcontroller").setCreativeTab(CreativeTabs.TRANSPORTATION);
		dopedEtherium = new BlockDopedEtherium(Material.GLASS).setHardness(4f).setUnlocalizedName("dopedetherium").setRegistryName(MODID, "dopedetherium").setCreativeTab(CreativeTabs.TRANSPORTATION);
		balloonBurner = new BlockBalloonBurner(Material.IRON).setHardness(4f).setUnlocalizedName("balloonburner").setRegistryName(MODID, "balloonburner").setCreativeTab(CreativeTabs.TRANSPORTATION);
		pilotsChair = new BlockShipPilotsChair(Material.IRON).setHardness(4f).setUnlocalizedName("shippilotschair").setRegistryName(MODID, "shippilotschair").setCreativeTab(CreativeTabs.TRANSPORTATION);
		passengerChair = new BlockShipPassengerChair(Material.IRON).setHardness(4f).setUnlocalizedName("shippassengerchair").setRegistryName(MODID, "shippassengerchair").setCreativeTab(CreativeTabs.TRANSPORTATION);

		thrustRelay = new BlockThrustRelay(Material.IRON).setHardness(5f).setUnlocalizedName("thrustrelay").setRegistryName(MODID, "thrustrelay").setCreativeTab(CreativeTabs.TRANSPORTATION);
		thrustModulator = new BlockThrustModulator(Material.IRON).setHardness(8f).setUnlocalizedName("thrustmodulator").setRegistryName(MODID, "thrustmodulator").setCreativeTab(CreativeTabs.TRANSPORTATION);

		registerBlock(basicEngine);
		registerBlock(advancedEngine);
		registerBlock(eliteEngine);
		registerBlock(ultimateEngine);
		registerBlock(redstoneEngine);

		registerBlock(antigravityEngine);
		registerBlock(advancedEtherCompressor);
		registerBlock(eliteEtherCompressor);
		registerBlock(ultimateEtherCompressor);
		registerBlock(creativeEtherCompressor);

		registerBlock(basicHoverController);
		registerBlock(dopedEtherium);
		registerBlock(balloonBurner);
		registerBlock(pilotsChair);
		registerBlock(passengerChair);

		registerBlock(thrustRelay);
		registerBlock(thrustModulator);
	}

	private void registerTileEntities(FMLStateEvent event) {
		TileEntity.addMapping(TileEntityHoverController.class, "tilehovercontroller");
		TileEntity.addMapping(TileEntityNormalEtherCompressor.class, "tileantigravengine");
		TileEntity.addMapping(BalloonBurnerTileEntity.class, "tileballoonburner");
		TileEntity.addMapping(PilotsChairTileEntity.class, "tilemanualshipcontroller");
	}

	private void registerItems(FMLStateEvent event) {
		systemLinker = new ItemSystemLinker().setUnlocalizedName("systemlinker").setRegistryName(MODID, "systemlinker").setCreativeTab(CreativeTabs.TRANSPORTATION).setMaxStackSize(1);
		airshipStealer = new ItemShipStealer().setUnlocalizedName("airshipStealer").setRegistryName(MODID, "airshipStealer").setCreativeTab(CreativeTabs.TOOLS).setMaxStackSize(1);
		relayWire = new ItemRelayWire().setUnlocalizedName("relaywire").setRegistryName(MODID, "relaywire").setCreativeTab(CreativeTabs.TRANSPORTATION).setMaxStackSize(1);

		GameRegistry.register(systemLinker);
		GameRegistry.register(airshipStealer);
		GameRegistry.register(relayWire);
	}

	private void registerRecipies(FMLStateEvent event) {
		GameRegistry.addRecipe(new ItemStack(pilotsChair), new Object[] { "SLS", "EWE", " S ", 'S', Items.STICK, 'L', Items.LEATHER, 'W', Item.getItemFromBlock(Blocks.LOG), 'E', ValkyrienWarfareWorldMod.etheriumCrystal });
		GameRegistry.addRecipe(new ItemStack(systemLinker), new Object[] { "IR ", " DR", "I I", 'I', Items.IRON_INGOT, 'D', Items.DIAMOND, 'R', Items.REDSTONE });
		GameRegistry.addRecipe(new ItemStack(balloonBurner), new Object[]{ "IFI", "WIW", "PPP", 'I', Items.IRON_INGOT, 'F', Items.FLINT_AND_STEEL, 'W', Item.getItemFromBlock(Blocks.LOG), 'P', Item.getItemFromBlock(Blocks.PLANKS) });

		GameRegistry.addRecipe(new ItemStack(basicHoverController), new Object[] { "III", "TCT", "III", 'I', Item.getItemFromBlock(Blocks.IRON_BLOCK), 'C', Items.COMPASS, 'T', Item.getItemFromBlock(Blocks.CRAFTING_TABLE) });

		GameRegistry.addRecipe(new ItemStack(antigravityEngine, 4), new Object[] { "#I#", "#E#", "WEW", '#', Item.getItemFromBlock(Blocks.PLANKS), 'I', Items.IRON_INGOT, 'E', ValkyrienWarfareWorldMod.etheriumCrystal, 'W', Item.getItemFromBlock(Blocks.LOG) });
		GameRegistry.addRecipe(new ItemStack(advancedEtherCompressor, 4), new Object[] { "#I#", "#E#", "WEW", '#', Item.getItemFromBlock(Blocks.STONE), 'I', Items.IRON_INGOT, 'E', ValkyrienWarfareWorldMod.etheriumCrystal, 'W', Item.getItemFromBlock(Blocks.LOG) });
		GameRegistry.addRecipe(new ItemStack(advancedEtherCompressor, 2), new Object[] { "#I#", "#E#", "WEW", '#', Item.getItemFromBlock(Blocks.COBBLESTONE), 'I', Items.IRON_INGOT, 'E', ValkyrienWarfareWorldMod.etheriumCrystal, 'W', Item.getItemFromBlock(Blocks.LOG) });
		GameRegistry.addRecipe(new ItemStack(eliteEtherCompressor, 4), new Object[] { "III", "IEI", "WEW", 'I', Items.IRON_INGOT, 'E', ValkyrienWarfareWorldMod.etheriumCrystal, 'W', Item.getItemFromBlock(Blocks.LOG) });
		GameRegistry.addRecipe(new ItemStack(ultimateEtherCompressor, 4), new Object[] { "#I#", "#E#", "WEW", '#', Item.getItemFromBlock(Blocks.OBSIDIAN), 'I', Items.IRON_INGOT, 'E', ValkyrienWarfareWorldMod.etheriumCrystal, 'W', Item.getItemFromBlock(Blocks.LOG) });

		GameRegistry.addRecipe(new ItemStack(basicEngine, 4), new Object[] { "I##", "IPP", "I##", '#', Item.getItemFromBlock(Blocks.PLANKS), 'P', Item.getItemFromBlock(Blocks.PISTON), 'I', Items.IRON_INGOT });
		GameRegistry.addRecipe(new ItemStack(advancedEngine, 4), new Object[] { "I##", "IPP", "I##", '#', Item.getItemFromBlock(Blocks.STONE), 'P', Item.getItemFromBlock(Blocks.PISTON), 'I', Items.IRON_INGOT });
		GameRegistry.addRecipe(new ItemStack(advancedEngine, 2), new Object[] { "I##", "IPP", "I##", '#', Item.getItemFromBlock(Blocks.COBBLESTONE), 'P', Item.getItemFromBlock(Blocks.PISTON), 'I', Items.IRON_INGOT });
		GameRegistry.addRecipe(new ItemStack(eliteEngine, 4), new Object[] { "III", "IPP", "III", 'P', Item.getItemFromBlock(Blocks.PISTON), 'I', Items.IRON_INGOT });
		GameRegistry.addRecipe(new ItemStack(ultimateEngine, 4), new Object[] { "I##", "IPP", "I##", '#', Item.getItemFromBlock(Blocks.OBSIDIAN), 'P', Item.getItemFromBlock(Blocks.PISTON), 'I', Items.IRON_INGOT });

	}

	private void registerNetworks(FMLStateEvent event) {
		controlNetwork = NetworkRegistry.INSTANCE.newSimpleChannel("controlNetwork");
		controlNetwork.registerMessage(HovercraftControllerGUIInputHandler.class, HovercraftControllerGUIInputMessage.class, 0, Side.SERVER);
		controlNetwork.registerMessage(PilotControlsMessageHandler.class, PilotControlsMessage.class, 1, Side.SERVER);
		controlNetwork.registerMessage(EntityFixMessageHandler.class, EntityFixMessage.class, 2, Side.CLIENT);
		controlNetwork.registerMessage(SetShipPilotMessageHandler.class, SetShipPilotMessage.class, 3, Side.CLIENT);
	}

	public void registerCapibilities(FMLStateEvent event)	{
		CapabilityManager.INSTANCE.register(ICapabilityLastRelay.class, new StorageLastRelay(), ImplCapabilityLastRelay.class);
	}

	private static void registerBlock(Block block){
		GameRegistry.register(block);
		registerItemBlock(block);
	}

	private static void registerItemBlock(Block block){
		GameRegistry.register(new ItemBlock(block).setRegistryName(block.getRegistryName()));
	}
}

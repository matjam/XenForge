package net.stupendous.xf;
import java.net.URL;

import net.minecraftforge.common.Configuration;

import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;

@Mod(modid = "XenForge", name = "XenForge", version = "1.0.0")
@NetworkMod(clientSideRequired = false, serverSideRequired = false)

public class XenForgePlugin {
	private static XenForgeConnectionHandler connectionHandler = null;
	private static XenForgeLogger log = null;
	private static Configuration config = null;
	private static XenForgeDatabase db = null;
	public static XenForgePlugin instance = null;
	
	@PreInit
	public void preInit(FMLPreInitializationEvent event) {
		if (instance == null) {
			instance = this;
		}
		
		if (log == null) {
			log = new XenForgeLogger(event.getModLog());
		}
		
		log.info("Initializing. Path: %s", System.getProperty("user.dir"));
		
		
		
		if (config == null) {
			config = new Configuration(event.getSuggestedConfigurationFile());
		}
		
		config.load();

		db = getDb();
		
		if (connectionHandler == null) {
			connectionHandler = new XenForgeConnectionHandler();
		}
	}
	
	@Init
	public void init(FMLInitializationEvent event) {
		NetworkRegistry.instance().registerConnectionHandler(connectionHandler);

		db.connect();
		
		config.save();
		log.info("Forge mod loaded.");
	}
	
	public static XenForgeLogger getLogger() {
		return log;
	}
	
	public Configuration getConfig() {
		return config;
	}
	
	public static XenForgePlugin getInstance() {
		return instance;
	}
	
	public static XenForgeDatabase getDb() {
		if (db == null) {
			db = new XenForgeDatabase(getInstance());
		}

		return db;
	}
}

package net.stupendous.xf;

import net.minecraft.server.MinecraftServer;
import net.minecraft.src.INetworkManager;
import net.minecraft.src.NetHandler;
import net.minecraft.src.NetLoginHandler;
import net.minecraft.src.Packet1Login;
import cpw.mods.fml.common.network.IConnectionHandler;
import cpw.mods.fml.common.network.Player;

public class XenForgeConnectionHandler implements IConnectionHandler {
	private XenForgeLogger log = null;
	private String unregisteredMessage = null;
	private String bannedMessage = null;
	private String wrongGroupMessage = null;
	private XenForgePlugin plugin = null;
	private XenForgeDatabase db = null;
	
	XenForgeConnectionHandler() {
		if (log == null) {
			log = XenForgePlugin.getLogger();
		}
		
		if (plugin == null) {
			plugin = XenForgePlugin.getInstance();
		}
		
		if (db == null) {
			db = XenForgePlugin.getDb();
		}
		
		unregisteredMessage = plugin.getConfig().get("msg", "unregistered", "Please register an account at SITE_NAME.").value;
		bannedMessage = plugin.getConfig().get("msg", "banned", "You have been banned.").value;
		wrongGroupMessage = plugin.getConfig().get("msg", "group", "You do not have access, sorry.").value;
	}
	
	@Override
	public void playerLoggedIn(Player player, NetHandler netHandler,
			INetworkManager manager) {
	}

	@Override
	public String connectionReceived(NetLoginHandler netHandler,
			INetworkManager manager) {
		int forumId = db.getForumId(netHandler.clientUsername); 
				
		if (forumId == -1) {
			log.info("Player %s rejected: Unregistered.", netHandler.clientUsername);
			return unregisteredMessage;
		}
		
		if (db.isBanned(forumId)) {
			log.info("Player %s rejected: Banned.", netHandler.clientUsername);
			return bannedMessage;
		}
		
		if (!db.isAllowed(forumId)) {
			log.info("Player %s rejected: Not allowed.", netHandler.clientUsername);
			return wrongGroupMessage;
		}
		
		log.info("Player %s accepted.", netHandler.clientUsername);

		return null;
	}

	@Override
	public void connectionOpened(NetHandler netClientHandler, String server,
			int port, INetworkManager manager) {
	}

	@Override
	public void connectionOpened(NetHandler netClientHandler,
			MinecraftServer server, INetworkManager manager) {
	}

	@Override
	public void connectionClosed(INetworkManager manager) {
	}

	@Override
	public void clientLoggedIn(NetHandler clientHandler,
			INetworkManager manager, Packet1Login login) {
	}

}

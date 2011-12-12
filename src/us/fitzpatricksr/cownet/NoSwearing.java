package us.fitzpatricksr.cownet;

import java.util.logging.Logger;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class NoSwearing extends PlayerListener {
    private Logger logger = Logger.getLogger("Minecraft");
	private String permissionNode;
	private String[] bannedPhrases;
	private int fireTicks;
	
	public NoSwearing(JavaPlugin plugin, String permissionRoot, String trigger) {
        FileConfiguration config = plugin.getConfig();
        if (config.getBoolean(trigger+".enable")) {
            this.permissionNode = permissionRoot+"."+trigger+".allowed";
            this.bannedPhrases = config.getString(trigger+".bannedphrases").split("-");
            this.fireTicks = config.getInt(trigger+".fire_ticks", 5) * 20; // 5 seconds
    		PluginManager pm = plugin.getServer().getPluginManager();
    		pm.registerEvent(Event.Type.PLAYER_CHAT, this, Event.Priority.Normal, plugin);
            logger.info(trigger+" enable");
        } else {
            logger.info("CowNet - "+trigger+".enable: false");
        }
    }

	public void onPlayerChat(PlayerChatEvent event) {
		String text = " " + event.getMessage().toLowerCase().replaceAll("[^a-z]", "") + " ";
		Player player = event.getPlayer();
		if (player.hasPermission(permissionNode)) {
			logger.info(player.getName()+" said a bad word, but has permissions.");
			return;
		}
		
		if (bannedPhrases != null) {
			for (int i = 0; i < bannedPhrases.length; i++) {
				String bannedPhrase = " " + bannedPhrases[i].toLowerCase() + " ";				
				if (text.indexOf(bannedPhrase) != -1) {
					player.setFireTicks(fireTicks);
					player.sendMessage("You've been very naughty!  Saying words like "+bannedPhrase+".");
					event.setCancelled(true);
					return;
				} else {
					logger.info("Didn't see word '"+bannedPhrase+"'");
				}
			}
		}
	}
}

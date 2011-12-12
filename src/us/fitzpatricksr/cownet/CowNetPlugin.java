package us.fitzpatricksr.cownet;

import java.util.logging.Logger;
import org.bukkit.plugin.java.JavaPlugin;

public class CowNetPlugin extends JavaPlugin {
	private final Logger logger = Logger.getLogger("Minecraft");
	
	@Override
	public void onDisable() {
		logger.info("CowNetPlugin is now disabled!");
		getServer().getScheduler().cancelAllTasks();
	}

	@Override
	public void onEnable() {
		this.getConfig().options().copyDefaults(true);
		if (getConfig().getBoolean("cownet.enable", false)) {
			logger.info("CowNetPlugin enabled.");
            new StarveCommand(this, "cownet", "starve");
            new BounceCommand(this, "cownet", "bounce");
            new NoSwearing(this, "cownet", "noswearing");
		} else {
			logger.info("CowNetPlugin disabled");
			getPluginLoader().disablePlugin(this);
		}
		this.saveConfig();
	}
}



package us.fitzpatricksr.cownet;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.logging.Logger;

public class BounceCommand implements CommandExecutor {
    private static final int MAX_RADIUS = 100;
    private static final int MAX_VELOCITY = 5;
    private static final int DEFAULT_RADIUS = 5;
    private static final int DEFAULT_VELOCITY = 1;
    private Logger logger = Logger.getLogger("Minecraft");
    private int standardRadius;
    private int standardVelocity;
    private String permissionNode;

	public BounceCommand(JavaPlugin plugin, String permissionRoot, String trigger) {
        FileConfiguration config = plugin.getConfig();
        if (config.getBoolean(trigger+".enable")) {
            this.permissionNode = permissionRoot+"."+trigger;
            this.standardRadius = config.getInt(trigger+".radius", DEFAULT_RADIUS);
            this.standardVelocity = config.getInt(trigger+".velocity", DEFAULT_VELOCITY);
            plugin.getCommand(trigger).setExecutor(this);
            logger.info(trigger+" enable: true, radius: "+standardRadius+", velocity: "+ standardVelocity);
        } else {
            logger.info("CowNet - "+trigger+".enable: false");
        }
    }

	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		if (sender instanceof Player) {
            final Player player = (Player) sender;

            if (!player.hasPermission(permissionNode) && !player.isOp()) {
                player.sendMessage("Sorry, you don't have permissions: "+ permissionNode);
                return false;
            }

            if (args.length > 2) {
                player.sendMessage("usage: /"+command.getName()+" [radius:"+ standardRadius +"] [velocity:"+ standardVelocity +"]");
                return false;
            }
            if (args.length == 1 && "help".equalsIgnoreCase(args[0])) {
                player.sendMessage("usage: bounce [radius] [velocity]");
            } else {
                int radius = standardRadius;
                if (args.length >= 1) {
                    try {
                        radius = Integer.parseInt(args[0]);
                    } catch (Exception e) {
                        player.sendMessage("usage: radius must be a number between 1 and "+MAX_RADIUS);
                        return false;
                    }
                    if (radius < 1 || radius > MAX_RADIUS) {
                        player.sendMessage("usage: radius must be a number between 1 and "+MAX_RADIUS);
                        return false;
                    }
                }
                int velocity = standardVelocity;
                if (args.length >= 2) {
                    try {
                        velocity = Integer.parseInt(args[1]);
                    } catch (Exception e) {
                        player.sendMessage("usage: velocity must be a number between 1 and "+ MAX_VELOCITY);
                        return false;
                    }
                    if (velocity < 1 || velocity > MAX_RADIUS) {
                        player.sendMessage("usage: velocity must be a number between 1 and "+ MAX_VELOCITY);
                        return false;
                    }
                }
                final Vector vector = new Vector(0, velocity, 0);
                List<Entity> entities = player.getNearbyEntities(radius, radius, radius);
                if (entities.size() == 0) {
                    player.sendMessage("Nothing to bounce within a radius of "+radius);
                } else {
                    player.sendMessage("Bouncing creatures within a radius of "+radius);
                    for (Entity entity : entities) {
                        if (entity instanceof LivingEntity) {
                            LivingEntity livingEntity = (LivingEntity) entity;
                            livingEntity.setVelocity(vector);
                        }
                    }
                }
            }
            return true;
        } else {
            return false;
        }

	}
}


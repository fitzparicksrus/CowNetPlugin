package us.fitzpatricksr.cownet;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.logging.Logger;

public class StarveCommand implements CommandExecutor {
    private static final int MAX_RADIUS = 100;
    private static final int MAX_DAMAGE = 100;
    private static int DEFAULT_RADIUS = 5;
    private static int DEFAULT_DAMAGE = 20;
    private Logger logger = Logger.getLogger("Minecraft");
    private int standardRadius;
    private int standardDamage;
    private String permissionNode;

	public StarveCommand(JavaPlugin plugin, String permissionRoot, String trigger) {
        FileConfiguration config = plugin.getConfig();
        if (config.getBoolean(trigger+".enable")) {
            this.permissionNode = permissionRoot+"."+trigger;
            this.standardRadius = config.getInt(trigger+".radius", DEFAULT_RADIUS);
            this.standardDamage = config.getInt(trigger+".damage", DEFAULT_DAMAGE);
            plugin.getCommand(trigger).setExecutor(this);
            logger.info(trigger+" enable: true, radius: "+standardRadius+", damage: "+standardDamage);
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
                player.sendMessage("usage: /"+command.getName()+" [standardRadius:"+ standardRadius +"] [standardDamage:"+ standardDamage +"]");
                return false;
            }
            int range = standardRadius;
            if (args.length >= 1) {
                try {
                    range = Integer.parseInt(args[0]);
                } catch (Exception e) {
                    player.sendMessage("usage: standardRadius must be a number between 1 and "+MAX_RADIUS);
                    return false;
                }
                if (range < 1 || range > MAX_RADIUS) {
                    player.sendMessage("usage: standardRadius must be a number between 1 and "+MAX_RADIUS);
                    return false;
                }
            }
            int damage = standardDamage;
            if (args.length >= 2) {
                try {
                    damage = Integer.parseInt(args[1]);
                } catch (Exception e) {
                    player.sendMessage("usage: standardDamage must be a number between 1 and "+MAX_DAMAGE);
                    return false;
                }
                if (damage < 1 || damage > MAX_RADIUS) {
                    player.sendMessage("usage: standardDamage must be a number between 1 and "+MAX_DAMAGE);
                    return false;
                }
            }
            player.sendMessage("Starving creatures within a standardRadius of: "+range);
            List<Entity> entities = player.getNearbyEntities(range, range, range);
            int deathToll = 0;
            int wounded = 0;
            for (Entity entity : entities) {
                if (entity instanceof LivingEntity) {
                    LivingEntity livingEntity = (LivingEntity) entity;
                    livingEntity.damage(damage, player);
                    if (livingEntity.getHealth() < 1) {
                        deathToll++;
                    } else {
                        wounded++;
                    }
                }
            }
            if (deathToll == 0 && wounded == 0) {
                player.sendMessage("No dead.  No wounded.  Happy day.");
            } else {
                if (deathToll > 0) {
                    player.sendMessage(""+deathToll+" creatures died.");
                }
                if (wounded > 0) {
                    player.sendMessage(""+deathToll+" creatures wounded.");
                }
            }
            return true;
        } else {
            return false;
        }

	}
}


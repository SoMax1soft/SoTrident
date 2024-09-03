package so.max1soft.sotrident;

import com.Zrips.CMI.Containers.CMIUser;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class Listeners implements Listener {
    private final HashMap<UUID, TridentAttraction> tridentAttractions = new HashMap<>();
    private final HashMap<UUID, Trident> tridents = new HashMap<>();
    private final HashMap<UUID, Long> lastTridentUsage = new HashMap<>();
    private final Main plugin;

    public Listeners(Main plugin) {
        this.plugin = plugin;
    }

    private boolean isValidTrident(ItemStack trident) {
        if (trident == null || trident.getType() != Material.TRIDENT) {
            return false;
        }
        ItemMeta meta = trident.getItemMeta();
        if (meta == null || !meta.hasLore()) {
            return false;
        }
        List<String> tridentLore = meta.getLore();
        List<String> expectedLore = plugin.getConfig().getStringList("trident-lore");

        if (tridentLore.size() != expectedLore.size()) {
            return false;
        }

        for (int i = 0; i < tridentLore.size(); i++) {
            if (!tridentLore.get(i).equalsIgnoreCase(expectedLore.get(i))) {
                return false;
            }
        }
        return true;
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        if (event.getEntity() instanceof Trident) {
            Trident trident = (Trident) event.getEntity();
            if (trident.getShooter() instanceof Player) {
                Player player = (Player) trident.getShooter();
                if (!isValidTrident(trident.getItem())) {
                    return;
                }



                Entity hitEntity = event.getHitEntity();
                if (hitEntity != null) {
                    tridentAttractions.put(hitEntity.getUniqueId(), new TridentAttraction(trident));

                    if (isGodMode(CMIUser.getUser(player))) {
                        ActionBarUtil.sendActionBar(player, plugin.getConfig().getString("trident-actionbar-disable-god"));
                        return;
                    }
                    if (player.getGameMode() == GameMode.SPECTATOR || player.getGameMode() == GameMode.CREATIVE) {
                        ActionBarUtil.sendActionBar(player, plugin.getConfig().getString("trident-actionbar-disable-gm"));
                        return;
                    }
                    if (player.isFlying()) {
                        ActionBarUtil.sendActionBar(player, plugin.getConfig().getString("trident-actionbar-disable-fly"));
                        return;
                    }

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            if (hitEntity.isDead() || !tridentAttractions.containsKey(hitEntity.getUniqueId())) {
                                this.cancel();
                                return;
                            }

                            TridentAttraction attraction = tridentAttractions.get(hitEntity.getUniqueId());
                            if (attraction.hasExpired()) {
                                tridentAttractions.remove(hitEntity.getUniqueId());
                                this.cancel();
                                return;
                            }

                            Location playerLocation = player.getLocation();
                            Location entityLocation = hitEntity.getLocation();

                            double distance = playerLocation.distance(entityLocation);
                            if (distance < 1.5) {
                                hitEntity.teleport(playerLocation);
                                tridentAttractions.remove(hitEntity.getUniqueId());
                                this.cancel();
                                return;
                            }

                            Vector direction = playerLocation.toVector().subtract(entityLocation.toVector()).normalize();
                            hitEntity.setVelocity(direction.multiply(2));

                            spawnChainBetween(player, hitEntity);
                        }
                    }.runTaskTimer(plugin, 0L, 1L);
                }
            }
        }
    }

    private void spawnChainBetween(Player player, Entity target) {
        Location playerLocation = player.getLocation().clone().add(0, 1, 0);
        Location targetLocation = target.getLocation().clone().add(0, 1, 0);

        Vector direction = targetLocation.toVector().subtract(playerLocation.toVector()).normalize();
        double distance = playerLocation.distance(targetLocation);
        double step = 0.5;

        for (double i = 0; i < distance; i += step) {
            Location chainLocation = playerLocation.clone().add(direction.clone().multiply(i));
            player.getWorld().spawnParticle(Particle.REDSTONE, chainLocation, 1, 0.1, 0.1, 0.1, new Particle.DustOptions(Color.GRAY, 1));
        }
    }

    private boolean isGodMode(CMIUser user) {
        try {
            Method isGodMethod = user.getClass().getMethod("isGod");
            return (boolean) isGodMethod.invoke(user);
        } catch (NoSuchMethodException | IllegalAccessException | java.lang.reflect.InvocationTargetException e) {
            e.printStackTrace();
            return false;
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Trident) {
            Trident trident = (Trident) event.getDamager();
            if (trident.getShooter() instanceof Player) {
                Player player = (Player) trident.getShooter();
                if (!isValidTrident(trident.getItem())) {
                    return;
                }


                Entity hitEntity = event.getEntity();
                if (isGodMode(CMIUser.getUser(player))) {
                    ActionBarUtil.sendActionBar(player, plugin.getConfig().getString("trident-actionbar-disable-god"));
                    return;
                }
                if (player.getGameMode() == GameMode.SPECTATOR || player.getGameMode() == GameMode.CREATIVE) {
                    ActionBarUtil.sendActionBar(player, plugin.getConfig().getString("trident-actionbar-disable-gm"));
                    return;
                }
                if (player.isFlying()) {
                    ActionBarUtil.sendActionBar(player, plugin.getConfig().getString("trident-actionbar-disable-fly"));
                    return;
                }

                tridentAttractions.put(hitEntity.getUniqueId(), new TridentAttraction(trident));

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (hitEntity.isDead() || !tridentAttractions.containsKey(hitEntity.getUniqueId())) {
                            ActionBarUtil.sendActionBar(player, plugin.getConfig().getString("trident-actionbar-neuspex"));
                            this.cancel();
                            return;
                        }

                        TridentAttraction attraction = tridentAttractions.get(hitEntity.getUniqueId());
                        if (attraction.hasExpired()) {
                            tridentAttractions.remove(hitEntity.getUniqueId());
                            ActionBarUtil.sendActionBar(player, plugin.getConfig().getString("trident-actionbar-neuspex"));
                            this.cancel();
                            return;
                        }

                        Location playerLocation = player.getLocation();
                        Location entityLocation = hitEntity.getLocation();

                        double distance = playerLocation.distance(entityLocation);

                        if (distance < 1.5) {
                            hitEntity.teleport(playerLocation);
                            tridentAttractions.remove(hitEntity.getUniqueId());
                            this.cancel();
                            return;
                        }
                        hitEntity.getWorld().playSound(hitEntity.getLocation(), Sound.BLOCK_CHAIN_PLACE, 1.0F, 1.0F);
                        Vector direction = playerLocation.toVector().subtract(entityLocation.toVector()).normalize();
                        hitEntity.setVelocity(direction.multiply(0.5));
                        ActionBarUtil.sendActionBar(player, plugin.getConfig().getString("trident-actionbar-todo"));

                        spawnChainBetween(player, hitEntity);
                    }
                }.runTaskTimer(plugin, 0L, 1L);
            }
        }
    }
}

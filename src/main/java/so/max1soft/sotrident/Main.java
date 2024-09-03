package so.max1soft.sotrident;

import com.Zrips.CMI.CMI;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class Main extends JavaPlugin {
    private static Main instance;
    private CMI cmi;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        this.cmi = (CMI) getServer().getPluginManager().getPlugin("CMI");
        getCommand("sotrident").setExecutor(new SCommandExecutor(this));
        getServer().getPluginManager().registerEvents(new Listeners(this), this);
        getLogger().info("");
        getLogger().info("§fПлагин: §aЗапущен");
        getLogger().info("§fСоздатель: §b@max1soft");
        getLogger().info("§fСайт: §dMax1soft.pw");
        getLogger().info("§fВерсия: §c1.0");
        getLogger().info("");
        getLogger().info("§fИнформация: §dMax1soft.pw");
        getLogger().info("");
    }

    public static Main getInstance() {
        return instance;
    }

    public CMI getCMI() {
        return this.cmi;
    }

}

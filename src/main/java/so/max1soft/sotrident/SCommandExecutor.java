package so.max1soft.sotrident;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class SCommandExecutor implements CommandExecutor {
    private final Main plugin;

    public SCommandExecutor(Main plugin) {
        this.plugin = plugin;
    }

    public void giveSoTrident(Player player) {
        ItemStack trident = new ItemStack(Material.TRIDENT);
        ItemMeta meta = trident.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(Main.getInstance().getConfig().getString("trident-name").replace("&", "§"));
            List<String> stringList = Main.getInstance().getConfig().getStringList("trident-lore");
            meta.setLore(stringList);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
            meta.addEnchant(Enchantment.LOYALTY, 3, true);
            trident.setItemMeta(meta);
        }
        player.getInventory().addItem(trident);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
            if (!sender.hasPermission("SoTrident.Command")){
                sender.sendMessage(Main.getInstance().getConfig().getString("permission-message"));
                return true;
            }
            Player player = (Player) sender;
            if (args.length == 1) {
                Player target = Bukkit.getPlayer(args[0]);
                if (target != null) {
                    giveSoTrident(target);
                    sender.sendMessage("Успешно выдано!");
                } else {
                    sender.sendMessage("Успешно выдано!");
                }
                return true;
            } else {
                sender.sendMessage(ChatColor.RED + "Неверное количество аргументов!");
                return false;
            }

    }

}

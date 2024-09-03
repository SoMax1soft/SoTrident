package so.max1soft.sotrident;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class ActionBarUtil {

    private static BukkitRunnable task;


    public static void sendActionBar(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(message));
    }


    public static void sendPersistentActionBar(Player player, String message) {
        stopActionBar();
        task = new BukkitRunnable() {
            @Override
            public void run() {
                sendActionBar(player, message);
            }
        };
        task.runTaskTimer(Main.getPlugin(Main.class), 0, 20);
    }


    public static void stopActionBar() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }
}

package com.wenkrang.UuidHelper;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;

public class GUI implements Listener {
    @EventHandler
    public static void GUIevent(InventoryClickEvent event) throws IOException, InvalidConfigurationException {
        if (event.getView().getTitle().equalsIgnoreCase("映射你的账户唯一标识码")){

            if(event.getRawSlot() == 12){
                List<String> lore = event.getView().getItem(12).getItemMeta().getLore();

                if (lore.get(lore.size() - 1).equalsIgnoreCase("§6立即修改！！！")){
                    event.getView().getPlayer().sendMessage("c");
                    Player player = (Player) event.getView().getPlayer();
                    String name = player.getName();
                    player.kickPlayer("正在映射你的Uuid，你可以等待10秒后进入服务器");
                    File file = new File("./plugins/UuidHelper/config.yaml");
                    YamlConfiguration yamlConfiguration = new YamlConfiguration();
                    if (file.exists()) {
                        yamlConfiguration.load(file);
                        yamlConfiguration.set(name, event.getView().getItem(0).getItemMeta().getDisplayName());
                        yamlConfiguration.set(event.getView().getItem(0).getItemMeta().getDisplayName(), event.getView().getPlayer().getUniqueId().toString());
                        yamlConfiguration.save("./plugins/UuidHelper/config.yaml");

                    } else {
                        yamlConfiguration.set(name, event.getView().getItem(0).getItemMeta().getDisplayName());
                        yamlConfiguration.set(event.getView().getItem(0).getItemMeta().getDisplayName(), name);
                        yamlConfiguration.save("./plugins/UuidHelper/config.yaml");
                    }

                    String Uuid = event.getView().getItem(0).getItemMeta().getDisplayName();
                    File PlayerData = new File("./world/playerdata/" + player.getPlayerProfile().getUniqueId() + ".dat");
                    File PlayerDataTo = new File("./world/playerdata/" + Uuid + ".dat");
                    File OPlayerData = new File("./world/playerdata/" + player.getPlayerProfile().getUniqueId() + ".dat.old");
                    File OPlayerDataTo = new File("./world/playerdata/" + Uuid + ".dat.old");

                    PlayerDataTo.delete();
                    OPlayerDataTo.delete();

                    Files.copy(PlayerData.toPath(), PlayerDataTo.toPath());

                }
            }
            if(event.getRawSlot() == 13){
                event.getView().getPlayer().closeInventory();
            }
            event.setCancelled(true);
        }
    }
}

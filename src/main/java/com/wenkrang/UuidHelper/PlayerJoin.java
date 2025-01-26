package com.wenkrang.UuidHelper;

import com.mojang.authlib.GameProfile;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.Objects;
import java.util.UUID;

import static org.bukkit.Bukkit.*;

public class PlayerJoin implements Listener {
    @EventHandler(priority = EventPriority.HIGHEST)
    public static void playerjoin (PlayerLoginEvent event) throws IOException, InvalidConfigurationException {

        File file = new File("./plugins/UuidHelper/config.yaml");
        if (file.exists()) {

            YamlConfiguration yamlConfiguration = new YamlConfiguration();
            yamlConfiguration.load(file);
            if (yamlConfiguration.getString(event.getPlayer().getName()) != null) {
                if (yamlConfiguration.getString(event.getPlayer().getName()).equalsIgnoreCase(event.getPlayer().getUniqueId().toString())) return;
                try {
                    Objects.requireNonNull(getPlayer(UUID.fromString(Objects.requireNonNull(yamlConfiguration.getString(event.getPlayer().getName()))))).kickPlayer("您已经在异地登录");
                }catch (Exception e){}
                getServer().getConsoleSender().sendMessage(yamlConfiguration.getString(event.getPlayer().getName()));
                getServer().getConsoleSender().sendMessage(event.getPlayer().getUniqueId().toString());
                String Uuid = yamlConfiguration.getString(event.getPlayer().getName());
                getServer().getConsoleSender().sendMessage("[§9*§r] 检测到需要映射的玩家 " + event.getPlayer().getName());
                String string = event.getPlayer().getUniqueId().toString();
                getServer().getConsoleSender().sendMessage("[§9*§r] 正在将玩家映射到指定的Uuid ： " + Uuid);
                Player player = event.getPlayer();
                CraftPlayer craftPlayer = (CraftPlayer) player;
                GameProfile gameProfile = craftPlayer.getHandle().getGameProfile();
                try {
                    // 创建新的Uuid
                    UUID newUuid = null;
                    if (Uuid != null) {
                        newUuid = UUID.fromString(Uuid);
                    }

                    Field idField = gameProfile.getClass().getDeclaredField("id");
                    idField.setAccessible(true);
                    idField.set(gameProfile, newUuid);
                    // 输出新旧Uuid供验证
                    getServer().getConsoleSender().sendMessage("[§9*§r] 已将玩家从 " + player.getUniqueId() + " 映射至 " + Uuid);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                new BukkitRunnable(){

                    @Override
                    public void run() {
                        try {
                            YamlConfiguration Load = new YamlConfiguration();
                            Load.load("./plugins/UuidHelper/player/" + yamlConfiguration.getString(event.getPlayer().getName()));
                            event.getPlayer().teleport(Load.getLocation("location"));
                            getLogger().info(Load.getLocation("location").toString());
                            event.getPlayer().setFoodLevel(Load.getInt("FoodLevel"));
                            for (int i = 0;i < event.getPlayer().getInventory().getSize();i++) {
                                event.getPlayer().getInventory().setItem(i,Load.getItemStack("inv-" + String.valueOf(i)));
                            }
                            event.getPlayer().setLevel(Load.getInt("Level"));
                            event.getPlayer().setHealth(Load.getDouble("Health"));
                            event.getPlayer().sendMessage("[§9*§r] 已将你的Uuid从 " + player.getUniqueId() + " 映射至 " + Uuid);
                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.runTaskLater(UuidHelper.getPlugin(UuidHelper.class), 20);

//                YamlConfiguration Save = new YamlConfiguration();
//                Save.set("location",event.getPlayer().getLocation());
//                for (int i = 0;i < event.getPlayer().getInventory().getSize();i++) {
//                    Save.set("inv-"+String.valueOf(i),event.getPlayer().getInventory().getItem(i));
//                }
//                Save.set("FoodLevel",event.getPlayer().getFoodLevel());
//                Save.set("Level",event.getPlayer().getLevel());
//                Save.set("Health",event.getPlayer().getHealth());
//                try {
//                    Save.save("./plugins/UuidHelper/player/" + event.getPlayer().getUniqueId());
//                } catch (IOException e) {
//                    throw new RuntimeException(e);
//                }

                new BukkitRunnable() {

                    @Override
                    public void run() {

                        if (!event.getPlayer().isOnline()) cancel();
                        event.getPlayer().saveData();

                        File PlayerData = new File("./world/playerdata/" + string + ".dat");
                        File PlayerDataTo = new File("./world/playerdata/" + Uuid + ".dat");
                        File OPlayerData = new File("./world/playerdata/" + string + ".dat.old");
                        File OPlayerDataTo = new File("./world/playerdata/" + Uuid + ".dat.old");

                        PlayerData.delete();

                        OPlayerData.delete();

                        try {
                            Files.copy(PlayerDataTo.toPath(),PlayerData.toPath());
                            if (OPlayerData.exists()){
                                Files.copy(OPlayerDataTo.toPath(),OPlayerData.toPath());
                            }

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        YamlConfiguration Save = new YamlConfiguration();
                        Save.set("location",event.getPlayer().getLocation());
                        for (int i = 0;i < event.getPlayer().getInventory().getSize();i++) {
                            Save.set("inv-"+String.valueOf(i),event.getPlayer().getInventory().getItem(i));
                        }
                        Save.set("FoodLevel",event.getPlayer().getFoodLevel());
                        Save.set("Level",event.getPlayer().getLevel());
                        Save.set("Health",event.getPlayer().getHealth());
                        try {
                            Save.save("./plugins/UuidHelper/player/" + yamlConfiguration.getString(event.getPlayer().getName()));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }.runTaskTimer(com.wenkrang.UuidHelper.UuidHelper.getPlugin(com.wenkrang.UuidHelper.UuidHelper.class), 80, 5);


            }
            if (yamlConfiguration.get(event.getPlayer().getUniqueId().toString()) != null) {
                new BukkitRunnable(){

                    @Override
                    public void run() {
                        try {
                            YamlConfiguration Load = new YamlConfiguration();
                            Load.load("./plugins/UuidHelper/player/" + event.getPlayer().getUniqueId());
                            event.getPlayer().teleport(Load.getLocation("location"));
                            getLogger().info(Load.getLocation("location").toString());
                            event.getPlayer().setFoodLevel(Load.getInt("FoodLevel"));
                            for (int i = 0;i < event.getPlayer().getInventory().getSize();i++) {
                                event.getPlayer().getInventory().setItem(i,Load.getItemStack("inv-" + String.valueOf(i)));
                            }
                            event.getPlayer().setLevel(Load.getInt("Level"));
                            event.getPlayer().setHealth(Load.getDouble("Health"));
                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.runTaskLater(UuidHelper.getPlugin(UuidHelper.class), 20);



                try {
                    Objects.requireNonNull(getPlayer(UUID.fromString(Objects.requireNonNull(yamlConfiguration.getString(event.getPlayer().getUniqueId().toString()))))).kickPlayer("您已经在异地登录");
                }catch (Exception e){}

                String UuidTo = event.getPlayer().getUniqueId().toString();
                String Uuid = yamlConfiguration.getString(event.getPlayer().getUniqueId().toString());



                new BukkitRunnable() {
                    @Override
                    public void run() {

                        if (!event.getPlayer().isOnline()) cancel();
                        event.getPlayer().saveData();
                        File PlayerData = new File("./world/playerdata/" + Uuid + ".dat");
                        File PlayerDataTo = new File("./world/playerdata/" + UuidTo + ".dat");
                        File OPlayerData = new File("./world/playerdata/" + Uuid + ".dat.old");
                        File OPlayerDataTo = new File("./world/playerdata/" + UuidTo + ".dat.old");

                        try {
                            PlayerData.delete();
                            OPlayerData.delete();

                        }catch (Exception e){
                            e.printStackTrace();
                        }


                        try {
                            if (OPlayerDataTo.exists()) {
                                Files.copy(OPlayerDataTo.toPath(),OPlayerData.toPath());
                            }
                            Files.copy(PlayerDataTo.toPath(),PlayerData.toPath());

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

                        YamlConfiguration Save = new YamlConfiguration();
                        Save.set("location",event.getPlayer().getLocation());
                        for (int i = 0;i < event.getPlayer().getInventory().getSize();i++) {
                            Save.set("inv-"+String.valueOf(i),event.getPlayer().getInventory().getItem(i));
                        }
                        Save.set("FoodLevel",event.getPlayer().getFoodLevel());
                        Save.set("Level",event.getPlayer().getLevel());
                        Save.set("Health",event.getPlayer().getHealth());
                        try {
                            Save.save("./plugins/UuidHelper/player/" + event.getPlayer().getUniqueId());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }


                    }
                }.runTaskTimer(UuidHelper.getPlugin(UuidHelper.class), 80, 5);

            }

        }



    }

//    @EventHandler(priority = EventPriority.HIGHEST)
//    public void playerjoin(AsyncPlayerPreLoginEvent event) throws IOException, InvalidConfigurationException {
//
//
//        System.out.println(event.getHandlers().toString());
//        File file = new File("./plugins/UuidHelper/config.yaml");
//        if (file.exists()) {
//            YamlConfiguration yamlConfiguration = new YamlConfiguration();
//            yamlConfiguration.load(file);
//            if (yamlConfiguration.getString(event.getName()) != null) {
//                getLogger().info("[§9*§r] 检测到需要映射的玩家 " + event.getName());
//                String string = yamlConfiguration.getString(event.getName());
//                getLogger().info("[§9*§r] 正在将玩家映射到指定的Uuid ： " + string);
//                CraftPlayer craftPlayer = (CraftPlayer) Bukkit.getPlayerExact(event.getName());
//
//                ServerPlayer serverPlayer = craftPlayer.getHandle();
//                GameProfile gameProfile = craftPlayer.getProfile();
//
//                Uuid newUuid = null;
//                if (string != null) {
//                    newUuid = Uuid.fromString(string);
//                }
//
//                try {
//                    // 修改 GameProfile 的 Uuid
//                    Field idField = gameProfile.getClass().getDeclaredField("id");
//                    idField.setAccessible(true);
//                    idField.set(gameProfile, newUuid);
//
//
//
//                    // 输出新旧Uuid供验证
//                    getLogger().info("[§9*§r] 已将玩家从 " + craftPlayer.getUniqueId() + " 映射至 " + string);
//                    craftPlayer.sendMessage("[§9*§r] 已将你的Uuid从 " + craftPlayer.getUniqueId() + " 映射至 " + string);
//                } catch (Exception ex) {
//                    ex.printStackTrace();
//                    getLogger().severe("无法修改玩家 " + craftPlayer.getName() + " 的 Uuid: " + ex.getMessage());
//                }
//            }
//        }
//    }
}
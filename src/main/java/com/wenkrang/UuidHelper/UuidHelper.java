package com.wenkrang.UuidHelper;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class UuidHelper extends JavaPlugin {
    public static File nameToUuidFile;
    public static ConcurrentHashMap<String, UUID> nameToUuidMap = new ConcurrentHashMap<>();



    @Override
    public void onEnable() {
        // Plugin startup logic
        try {
            new File("./plugins/UuidHelper/player/").mkdirs();
            File file = new File("./plugins/Uuidhelper/");
            file.mkdirs();
            this.getCommand("uh").setExecutor(new command());
            getServer().getPluginManager().registerEvents(new PlayerJoin(), this);
            getServer().getPluginManager().registerEvents(new GUI(), this);
            nameToUuidFile = new File("./usermap.bin");
            try (final DataInputStream dis = new DataInputStream(new FileInputStream(nameToUuidFile))) {
                while (dis.available() > 0) {
                    String username = dis.readUTF();
                    UUID Uuid = new UUID(dis.readLong(), dis.readLong());
                    UUID previous = nameToUuidMap.put(username, Uuid);
                    getLogger().info(Uuid.toString() + "," + username);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            File yaml = new File("./plugins/UuidHelper/");
            if (!yaml.exists()) {
                yaml.mkdirs();
            }
        }catch (Exception e) {

        }


    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}

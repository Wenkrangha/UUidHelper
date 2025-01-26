package com.wenkrang.UuidHelper;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class command implements CommandExecutor {
    public static ItemStack setitem (ItemStack itemStack,int i) {
        ItemStack clone = itemStack.clone();
        ItemMeta itemMeta = clone.getItemMeta();
        itemMeta.setDisplayName("§f确认§9修改§r ( " + String.valueOf(i) + "秒 )");
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
    public static ItemStack updateitem (ItemStack itemStack) {
        ItemStack clone = itemStack.clone();
        String displayName = clone.getItemMeta().getDisplayName();
        if(displayName.equalsIgnoreCase("§f确认§9修改§r§f ( 5秒 )")){
            clone = setitem(clone, 4);
        }
        if(displayName.equalsIgnoreCase("§f确认§9修改§r§f ( 4秒 )")){
            clone = setitem(clone, 3);
        }
        if(displayName.equalsIgnoreCase("§f确认§9修改§r§f ( 3秒 )")){
            clone = setitem(clone, 2);
        }
        if(displayName.equalsIgnoreCase("§f确认§9修改§r§f ( 2秒 )")){
            clone = setitem(clone, 1);
        }
        if(displayName.equalsIgnoreCase("§f确认§9修改§r§f ( 1秒 )")){
            clone.setType(Material.ANVIL);
            ItemMeta itemMeta = clone.getItemMeta();
            itemMeta.setDisplayName("§f确认§9修改§r");
            clone.setItemMeta(itemMeta);
        }

        return clone;
    }
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (commandSender.isOp()) {
            if (strings[0].equalsIgnoreCase("search")) {
                commandSender.sendMessage("[§9*§r] 正在查询玩家 §l" + strings[1]);
                UUID Uuid = UuidHelper.nameToUuidMap.get(strings[1]);
                try {
                    if (Uuid == null) {
                        commandSender.sendMessage("[§4!§r] 失败");
                    } else {
                        commandSender.sendMessage("[§9*§r] 查询成功，Uuid为"+ Uuid.toString());
                    }
                }catch (Exception e) {
                    commandSender.sendMessage("[§4!§r] 查询失败");
                }
            }
            if (strings[0].equalsIgnoreCase("set")) {
                Player player = Bukkit.getPlayer(strings[1]);
                if (!player.isOnline()) {
                    commandSender.sendMessage("[§4*§r] 玩家不在线!");
                    return true;
                }
                commandSender.sendMessage("[§9*§r] 正在映射玩家 §l" + strings[1] + "§r 至 " + strings[2]);
                commandSender.sendMessage("[§9*§r] 等待玩家回复中...");
                Inventory inventory = Bukkit.createInventory(null, 27, "映射你的账户唯一标识码");
                ItemStack itemStack = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
                ItemMeta itemMeta = itemStack.getItemMeta();
                itemMeta.setDisplayName("");
                itemStack.setItemMeta(itemMeta);

                ItemStack itemStack1 = new ItemStack(Material.PLAYER_HEAD);
                ItemMeta itemMeta1 = itemStack1.getItemMeta();
                itemMeta1.setDisplayName("§f确认§9你的修改");
                ArrayList<String> arrayList = new ArrayList<>();
                String sender = commandSender.getName();
                arrayList.add("§7来自 §l" + sender + "§r§f 的请求");
                arrayList.add("§7我们即将§l映射§r§f你的Uuid");
                arrayList.add("§7请确认你是否同意该§l操作");
                itemMeta1.setLore(arrayList);
                itemStack1.setItemMeta(itemMeta1);

                ItemStack itemStack2 = new ItemStack(Material.BARRIER);
                ItemMeta itemMeta2 = itemStack2.getItemMeta();
                itemMeta2.setDisplayName("§f确认§9修改§r§f ( 5秒 )");
                ArrayList<String> arrayList1 = new ArrayList<>();
                arrayList1.add("§7我确保我已经将该账号下的物品已经全部清空，");
                arrayList1.add("§7我自愿承担映射后造成的一切不可预测的后果");
                arrayList1.add("§7");
                arrayList1.add("§6§l右键 §f立即退出并修改");
                itemMeta2.setLore(arrayList1);
                itemStack2.setItemMeta(itemMeta2);

                ItemStack itemStack3 = new ItemStack(Material.IRON_DOOR);
                ItemMeta itemMeta3 = itemStack3.getItemMeta();
                itemMeta3.setDisplayName("§f拒绝§4映射");
                ArrayList<String> arrayList2 = new ArrayList<>();
                arrayList2.add("§7我不同意管理组映射我的Uuid");
                arrayList2.add("§7立即退出映射，关闭该界面");
                itemMeta3.setLore(arrayList2);
                itemStack3.setItemMeta(itemMeta3);

                ItemStack itemStack4 = new ItemStack(Material.LIGHT_BLUE_STAINED_GLASS_PANE);
                ItemMeta itemMeta4 = itemStack4.getItemMeta();
                itemMeta4.setDisplayName(strings[2]);
                itemStack4.setItemMeta(itemMeta4);


                inventory.setItem(0, itemStack4);
                inventory.setItem(1, itemStack);
                inventory.setItem(2, itemStack);
                inventory.setItem(3, itemStack);
                inventory.setItem(4, itemStack);
                inventory.setItem(5, itemStack);
                inventory.setItem(6, itemStack);
                inventory.setItem(7, itemStack);
                inventory.setItem(8, itemStack);
                inventory.setItem(9, itemStack);
                inventory.setItem(10, itemStack1);
                inventory.setItem(11, itemStack);
                inventory.setItem(12, itemStack2);
                inventory.setItem(13, itemStack3);
                inventory.setItem(17, itemStack);
                inventory.setItem(18, itemStack);
                inventory.setItem(19, itemStack);
                inventory.setItem(20, itemStack);
                inventory.setItem(21, itemStack);
                inventory.setItem(22, itemStack);
                inventory.setItem(23, itemStack);
                inventory.setItem(24, itemStack);
                inventory.setItem(25, itemStack);
                inventory.setItem(26, itemStack);

                player.openInventory(inventory);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        inventory.setItem(12, setitem(itemStack2, 5));
                    }
                }.runTaskLater(UuidHelper.getPlugin(UuidHelper.class), 20);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        inventory.setItem(12, setitem(itemStack2, 4));
                    }
                }.runTaskLater(UuidHelper.getPlugin(UuidHelper.class), 40);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        inventory.setItem(12, setitem(itemStack2, 3));
                    }
                }.runTaskLater(UuidHelper.getPlugin(UuidHelper.class), 60);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        inventory.setItem(12, setitem(itemStack2, 2));
                    }
                }.runTaskLater(UuidHelper.getPlugin(UuidHelper.class), 80);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        inventory.setItem(12, setitem(itemStack2, 1));
                    }
                }.runTaskLater(UuidHelper.getPlugin(UuidHelper.class), 100);
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        ItemStack clone = inventory.getItem(12).clone();
                        clone.setType(Material.ANVIL);
                        ItemMeta itemMeta = clone.getItemMeta();
                        itemMeta.setDisplayName("§f确认§9修改§r");
                        List<String> lore = itemMeta.getLore();
                        lore.add("§6立即修改！！！");
                        itemMeta.setLore(lore);
                        clone.setItemMeta(itemMeta);
                        inventory.setItem(12, clone);
                    }
                }.runTaskLater(UuidHelper.getPlugin(UuidHelper.class), 120);
            }
        }else {
            commandSender.sendMessage("[§4!§r] 该命令只能由管理员执行！");
        }

        return true;
    }
}

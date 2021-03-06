package io.github.thomdare.adminmode;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class Mode {
    private List<String> enableCommands = new ArrayList<>();
    private List<String> disableCommands = new ArrayList<>();
    private Map<UUID, StaffData> inMode = new HashMap<>();
    private String name;

    public Mode(YamlConfiguration config, File file) throws IOException {
        config.addDefault("enableCommands", enableCommands);
        config.addDefault("disableCommands", disableCommands);
        config.options().copyDefaults(true);
        config.save(file);
        enableCommands = config.getStringList("enableCommands");
        disableCommands = config.getStringList("disableCommands");
        name = file.getName().substring(0,file.getName().length()-4);
        Bukkit.getLogger().info("Loading mode: " + name);


    }

    public Set<UUID> getPlayers() {
        return inMode.keySet();
    }

    public void enableMode(Player player) {
        for (String command : enableCommands) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replaceAll("@p", player.getName()));

        }
        inMode.put(player.getUniqueId(), new StaffData(player.getLocation(), player.getLevel(), player.getExp(), player.getInventory().getContents()));
        player.setExp(0);
        player.setLevel(0);
        player.getInventory().clear();
        player.updateInventory();
        Bukkit.getLogger().info(player.getName() + " entered " +name+" mode");
    }

    public void disableMode(Player player) {
        for (String command : disableCommands) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replaceAll("@p", player.getName()));
        }
        StaffData playerData = inMode.get(player.getUniqueId());
        inMode.remove(player.getUniqueId());
        player.teleport(playerData.getLoc());
        player.getInventory().clear();
        for (int i = 0; i < playerData.getInv().length; i++) {
            if (playerData.getInv()[i] == null)
                continue;
            player.getInventory().setItem(i, playerData.getInv()[i]);
        }
        player.setExp(playerData.getXp());
        player.setLevel(playerData.getXpLevel());
        player.updateInventory();
        Bukkit.getLogger().info(player.getName() + " disabled " +name+" mode");
    }

    public String getName() {
        return name;
    }

    private class StaffData {
        private final Location loc;
        private final int xpLevel;
        private final float xp;
        private final ItemStack[] inv;

        public StaffData(Location loc, int xpLevel, float xp, ItemStack... inv) {

            this.loc = loc;
            this.xpLevel = xpLevel;
            this.xp = xp;
            this.inv = inv;
        }

        public Location getLoc() {
            return loc;
        }

        public int getXpLevel() {
            return xpLevel;
        }

        public float getXp() {
            return xp;
        }

        public ItemStack[] getInv() {
            return inv;
        }
    }


}

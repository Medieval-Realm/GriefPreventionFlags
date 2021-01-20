package me.ryanhamshire.GPFlags.flags;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.WeakHashMap;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Container;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

import me.ryanhamshire.GPFlags.Flag;
import me.ryanhamshire.GPFlags.FlagManager;
import me.ryanhamshire.GPFlags.GPFlags;
import me.ryanhamshire.GPFlags.MessageSpecifier;
import me.ryanhamshire.GPFlags.Messages;
import me.ryanhamshire.GriefPrevention.Claim;
import me.ryanhamshire.GriefPrevention.ClaimPermission;
import me.ryanhamshire.GriefPrevention.GriefPrevention;
import me.ryanhamshire.GriefPrevention.PlayerData;

/**
 * Allow players to view (but not manipulate) containers (chests, dispensers, hoppers etc) within a claim.
 * Example use case: Player casino's to verify odds.
 * 
 * @author Lewys Davies (Lew_)
 */
public class FlagDef_ViewContainers extends FlagDefinition {
    
    // WeakHashSet
    private final static Set<Inventory> viewing = Collections.newSetFromMap(new WeakHashMap<Inventory, Boolean>()); 
    
    public FlagDef_ViewContainers(FlagManager manager, GPFlags plugin) {
        super(manager, plugin);
    }
    
    @EventHandler
    public void onInvOpen(PlayerInteractEvent event) {
        if(event.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if(event.getClickedBlock() == null) return; // Just in case
        
        Block block = event.getClickedBlock();
        BlockState state = block.getState();
        if(!(state instanceof Container)) return;
        
        Flag flag = this.getFlagInstanceAtLocation(block.getLocation(), null);
        if (flag == null) return;
        
        Player player = event.getPlayer();
        
        PlayerData playerData = GriefPrevention.instance.dataStore.getPlayerData(player.getUniqueId());
        Claim claim = GriefPrevention.instance.dataStore.getClaimAt(block.getLocation(), true, playerData.lastClaim);
        if(claim == null) return;
        
        if(claim.ownerID.equals(player.getUniqueId()) || claim.hasExplicitPermission(player, ClaimPermission.Inventory)) {
            return; // Player Already has Inventory Access to this claim
        }
        
        Container cont = (Container) state;
        Inventory original = cont.getInventory();
        
        Inventory copy = Bukkit.createInventory(original.getHolder(), original.getType());
        copy.setContents(original.getContents());
        viewing.add(copy);
        
        player.openInventory(copy);
    }
    
    @EventHandler
    public void onInvClick(InventoryClickEvent event) {
        if(event.isCancelled()) return;
        if(viewing.contains(event.getInventory())) event.setCancelled(true);
    }
    
    @EventHandler
    public void onInvClose(InventoryCloseEvent event) {
        viewing.remove(event.getInventory());
    }

    @Override
    public String getName() {
        return "ViewContainers";
    }

    @Override
    public MessageSpecifier getSetMessage(String parameters) {
        return new MessageSpecifier(Messages.EnableViewContainers);
    }

    @Override
    public MessageSpecifier getUnSetMessage() {
        return new MessageSpecifier(Messages.DisableViewContainers);
    }

    @Override
    public List<FlagType> getFlagType() {
        return Arrays.asList(FlagType.CLAIM);
    }
}

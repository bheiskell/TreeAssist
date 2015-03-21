package me.itsatacoshop247.TreeAssist.blocklists;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * Implementation of {@link BlockList} which retains no history. Instead it evaluates nearby blocks. If the blocks
 * appear to be tree like, it will report that a player placed the block. This blocklist will error on the side of
 * caution.
 */
public class BestGuessBlocklist implements BlockList {

    private static final Logger LOG = Logger.getLogger("Minecraft.TreeAssist");

    final private int MAX_DEPTH = 30;
    
    final private List<BlockFace> DIRECTIONS = Lists.newArrayList(
            BlockFace.NORTH,
            BlockFace.NORTH_EAST,
            BlockFace.EAST,
            BlockFace.SOUTH_EAST,
            BlockFace.SOUTH,
            BlockFace.SOUTH_WEST,
            BlockFace.WEST,
            BlockFace.NORTH_WEST,
            BlockFace.UP,
            BlockFace.DOWN);
    
    final private Set<Material> LOGS = Sets.newHashSet(
            Material.LOG,
            Material.LOG_2);
    
    final private Set<Material> NATURAL_NEIGHBORS = Sets.newHashSet(
            Material.LOG,
            Material.LOG_2,
            Material.AIR,
            Material.DIRT,
            Material.LEAVES,
            Material.LEAVES_2,
            Material.GRASS,
            Material.LONG_GRASS,
            Material.DOUBLE_PLANT);
    
    @Override
    public void initiate() {
    }

    @Override
    public boolean isPlayerPlaced(Block block) {
        for (Block log : getLogRelatives(block)) {
            for (BlockFace direction : DIRECTIONS) {
                if (!NATURAL_NEIGHBORS.contains(log.getRelative(direction).getType())) {
                    
                    LOG.info("Player placed relative: " + log.getRelative(direction).getType());
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Obtain the Set of log blocks which comprise a tree. Traversal stops after exceeding a distance of
     * {@link #MAX_DEPTH}.
     * @param block the starting block
     * @return the list of adjacent logs which comprise the tree
     */
    private Set<Block> getLogRelatives(Block block) {
        LinkedHashSet<Block> entries = new LinkedHashSet<Block>();
        
        getLogRelatives(block, entries, 0);
        
        return entries;
    }
    
    private void getLogRelatives(Block block, LinkedHashSet<Block> entries, int depth) {
        // Prevent revisiting
        if (entries.contains(block)) {
            return;
        }
        
        // Avoid too much recursion
        if (depth > MAX_DEPTH) {
            LOG.warning("Exceeded max depth while walking tree: " + MAX_DEPTH);
            return;
        }
        
        entries.add(block);
        
        for (BlockFace direction : DIRECTIONS) {
            Block relative = block.getRelative(direction);
            if (LOGS.contains(relative.getType())) {
                getLogRelatives(relative, entries, depth + 1);
            }
        }
    }

    @Override
    public void addBlock(Block block) {
    }

    @Override
    public void removeBlock(Block block) {
    }

    @Override
    public void save() {
    }

    @Override
    public void logBreak(Block block, Player player) {
    }

}

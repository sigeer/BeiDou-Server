package org.gms.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.gms.client.Character;
import org.gms.client.inventory.InventoryType;
import org.gms.client.inventory.Item;
import org.gms.client.inventory.ItemFactory;
import org.gms.constants.game.GameConstants;
import org.gms.util.DatabaseConnection;
import org.gms.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StorageFactory {
    protected static final Logger log = LoggerFactory.getLogger(StorageFactory.class);

    private static AbstractStorage create(StorageType storageType, Character chr) throws SQLException {
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("INSERT INTO storages (ownerId, world, slots, meso, type) VALUES (?, ?, 4, 0, ?)")) {
            ps.setInt(1, storageType.getItemFactory().isAccount() ? chr.getAccountId() : chr.getId());
            ps.setInt(2, chr.getWorld());
            ps.setInt(3, storageType.getValue());

            ps.executeUpdate();
        }

        return loadOrCreateFromDB(storageType, chr);
    }

    public static AbstractStorage loadOrCreateFromDB(StorageType storageType, Character chr) {
        int ownerId = storageType.getItemFactory().isAccount() ? chr.getAccountId() : chr.getId();
        AbstractStorage ret = null;
        try (Connection con = DatabaseConnection.getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT slots, meso FROM storages WHERE ownerId = ? AND world = ? AND type = ?")) {
            ps.setInt(1, ownerId);
            ps.setInt(2, chr.getWorld());
            ps.setInt(3, storageType.getValue());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    if (storageType == StorageType.GachaponStorage) {
                        ret = new GachaponStorage(chr, rs.getInt("meso"));
                    }
                    if (storageType == StorageType.AccountStorage) {
                        ret = new AccountStorage(chr, (byte)rs.getInt("slots"), rs.getInt("meso"));
                    }
                    if (ret != null) {
                        for (Pair<Item, InventoryType> item : storageType.getItemFactory().loadItems(ownerId, false)) {
                            ret.items.add(item.getLeft());
                        }
                    }

                } else {
                    ret = create(storageType, chr);
                }
            }

            return ret;
        } catch (SQLException ex) { // exceptions leading to deploy null storages found thanks to Jefe
            log.error("SQL error occurred when trying to load storage for accId {}, world {}", chr, GameConstants.WORLD_NAMES[chr.getWorld()], ex);
            throw new RuntimeException(ex);
        }
    }
}

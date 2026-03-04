package org.gms.server;

import org.gms.client.Client;
import org.gms.client.Character;
import org.gms.client.inventory.InventoryType;
import org.gms.client.inventory.Item;
import org.gms.client.inventory.manipulator.InventoryManipulator;
import org.gms.config.GameConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gms.util.PacketCreator;
import org.gms.util.Pair;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public abstract class AbstractStorage {
    protected static final Logger log = LoggerFactory.getLogger(AbstractStorage.class);

    protected int currentNpcid;
    protected int meso;
    protected byte slots;
    protected final Map<InventoryType, List<Item>> typeItems = new HashMap<>();
    protected List<Item> items = new LinkedList<>();
    protected final Lock lock = new ReentrantLock(true);

    protected final Character owner;
    protected boolean changed;
    protected StorageType storageType;

    protected AbstractStorage(Character owner, byte slots, int meso) {
        this.owner = owner;
        this.slots = slots;
        this.meso = meso;
    }

    public int getOwnerId() {
        return storageType.getItemFactory().isAccount() ? owner.getAccountId() : owner.getId();
    }

    public byte getSlots() {
        return slots;
    }

    public boolean canGainSlots(int slots) {
        slots += this.slots;
        return slots <= 48;
    }

    public boolean canGainItems(int count) {
        return this.items.size() + count <= this.slots;
    }

    public boolean gainSlots(int slots) {
        lock.lock();
        try {
            if (canGainSlots(slots)) {
                slots += this.slots;
                this.slots = (byte) slots;
                this.changed = true;
                return true;
            }

            return false;
        } finally {
            lock.unlock();
        }
    }

    public void saveToDB(Connection con) {
        if (!this.changed) {
            return;
        }
        
        try {
            try (PreparedStatement ps = con.prepareStatement("UPDATE storages SET slots = ?, meso = ? WHERE ownerId = ? AND type = ?")) {
                ps.setInt(1, slots);
                ps.setInt(2, meso);
                ps.setInt(3, getOwnerId());
                ps.setInt(4, storageType.getValue());
                ps.executeUpdate();
            }
            List<Pair<Item, InventoryType>> itemsWithType = new ArrayList<>();

            List<Item> list = getItems();
            for (Item item : list) {
                itemsWithType.add(new Pair<>(item, item.getInventoryType()));
            }

            storageType.getItemFactory().saveItems(itemsWithType, getOwnerId(), con);
            this.changed = false;
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public Item getItem(byte slot) {
        lock.lock();
        try {
            return items.get(slot);
        } finally {
            lock.unlock();
        }
    }

    protected boolean baseCheck() {
        return true;
    }

    /**
     * 取出物品检查
     * @param item
     * @return
     */
    public boolean takeOutItemCheck(Item item) {
        if (!baseCheck()) {
            return false;
        }

        if (ItemInformationProvider.getInstance().isPickupRestricted(item.getItemId()) && owner.haveItemWithId(item.getItemId(), true)) {
            owner.sendPacket(PacketCreator.getStorageError((byte) 0x0C));
            return false;
        }

        if (!InventoryManipulator.checkSpace(owner.getClient(), item.getItemId(), item.getQuantity(), item.getOwner())) {
            owner.sendPacket(PacketCreator.getStorageError((byte) 0x0A));
            return false;
        }

        return true;
    }

    public boolean takeOut(Item item) {
        lock.lock();
        try {
            boolean ret = items.remove(item);

            InventoryType type = item.getInventoryType();
            typeItems.put(type, new ArrayList<>(filterItems(type)));

            this.changed = true;
            return ret;
        } finally {
            lock.unlock();
        }
    }

    /**
     * 存入物品检查
     * @param slot
     * @param itemId
     * @param quantity
     * @return
     */
    public boolean storeItemCheck(short slot, int itemId, short quantity)  {
        if (!baseCheck()) {
            return false;
        }

        if (quantity < 1) {
            owner.sendPacket(PacketCreator.enableActions());
            return false;
        }
        if (isFull()) {
            owner.sendPacket(PacketCreator.getStorageError((byte) 0x11));
            return false;
        }

        return true;
    }

    public boolean store(Item item) {
        lock.lock();
        try {
            if (isFull()) { // thanks Optimist for noticing unrestricted amount of insertions here
                return false;
            }

            items.add(item);

            InventoryType type = item.getInventoryType();
            typeItems.put(type, new ArrayList<>(filterItems(type)));

            this.changed = true;
            return true;
        } finally {
            lock.unlock();
        }
    }

    public List<Item> getItems() {
        lock.lock();
        try {
            return Collections.unmodifiableList(items);
        } finally {
            lock.unlock();
        }
    }

    private List<Item> filterItems(InventoryType type) {
        List<Item> storageItems = getItems();
        List<Item> ret = new LinkedList<>();

        for (Item item : storageItems) {
            if (item.getInventoryType() == type) {
                ret.add(item);
            }
        }
        return ret;
    }

    public byte getSlot(InventoryType type, byte slot) {
        lock.lock();
        try {
            byte ret = 0;
            List<Item> storageItems = getItems();
            for (Item item : storageItems) {
                if (item == typeItems.get(type).get(slot)) {
                    return ret;
                }
                ret++;
            }
            return -1;
        } finally {
            lock.unlock();
        }
    }

    public void sendStorage(Client c, int npcId) {
        lock.lock();
        try {
            items.sort((o1, o2) -> {
                if (o1.getInventoryType().getType() < o2.getInventoryType().getType()) {
                    return -1;
                } else if (o1.getInventoryType() == o2.getInventoryType()) {
                    return 0;
                }
                return 1;
            });

            List<Item> storageItems = getItems();
            for (InventoryType type : InventoryType.values()) {
                typeItems.put(type, new ArrayList<>(storageItems));
            }

            currentNpcid = npcId;
            owner.setCurrentStorage(this);
            owner.sendPacket(PacketCreator.getStorage(npcId, slots, storageItems, meso));
        } finally {
            lock.unlock();
        }
    }

    public void sendStored(Client c, InventoryType type) {
        lock.lock();
        try {
            c.sendPacket(PacketCreator.storeStorage(slots, type, typeItems.get(type)));
        } finally {
            lock.unlock();
        }
    }

    public void sendTakenOut(Client c, InventoryType type) {
        lock.lock();
        try {
            c.sendPacket(PacketCreator.takeOutStorage(slots, type, typeItems.get(type)));
        } finally {
            lock.unlock();
        }
    }

    public void arrangeItems(Client c) {
        if (!baseCheck()) {
            return;
        }

        lock.lock();
        try {
            StorageInventory msi = new StorageInventory(c, items);
            msi.mergeItems();
            items = msi.sortItems();

            for (InventoryType type : InventoryType.values()) {
                typeItems.put(type, new ArrayList<>(items));
            }

            c.sendPacket(PacketCreator.arrangeStorage(slots, items));
        } finally {
            lock.unlock();
        }
    }

    public int getMeso() {
        return meso;
    }

    public void setMeso(int meso) {
        if (meso < 0) {
            throw new RuntimeException();
        }
        this.meso = meso;
        this.changed = true;
    }

    /**
     * 存入金币检查
     * @param meso
     * @return
     */
    public boolean storeMesoCheck(int meso) {
        if (!baseCheck()) {
            return false;
        }

        return this.owner.getMeso() >= meso;
    }

    /**
     * 取出金币检查
     * @param meso
     * @return
     */
    public boolean takeOutMesoCheck(int meso) {
        if (!baseCheck()) {
            return false;
        }

        return this.meso >= meso;
    }

    public void sendMeso(Client c) {
        c.sendPacket(PacketCreator.mesoStorage(slots, meso));
    }

    public void onTakeOutSuccess () {

    }

    public void OnStoreSuccess () {
    }

    public boolean isFull() {
        lock.lock();
        try {
            return items.size() >= slots;
        } finally {
            lock.unlock();
        }
    }

    public void close() {
        lock.lock();
        try {
            typeItems.clear();
            owner.setCurrentStorage(null);
        } finally {
            lock.unlock();
        }
    }

}

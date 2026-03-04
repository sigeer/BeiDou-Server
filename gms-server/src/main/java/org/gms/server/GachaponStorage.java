package org.gms.server;

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.client.inventory.Item;

public class GachaponStorage extends AbstractStorage {

    public GachaponStorage(Character owner, int meso) {
        super(owner, (byte)48, meso);

        storageType = StorageType.GachaponStorage;
    }

    @Override
    public boolean canGainSlots(int slots) {
        return false;
    }

    @Override
    public boolean gainSlots(int slots) {
        return false;
    }

    @Override
    public boolean store(Item item) {
        owner.dropMessage(1, "奖品仓库只能取出，无法存放/排列。");
        sendMeso(owner.getClient());
        return false;
    }

    public boolean addItem(Item item) {
        return super.store(item);
    }

    @Override
    public boolean storeMesoCheck(int meso) {
        owner.dropMessage(1, "奖品仓库只能取出，无法存放/排列。");
        sendMeso(owner.getClient());
        return false;
    }

    @Override
    public boolean takeOutMesoCheck(int meso) {
        return this.meso >= meso;
    }

    @Override
    public boolean storeItemCheck(short slot, int itemId, short quantity) {
        owner.dropMessage(1, "奖品仓库只能取出，无法存放/排列。");
        sendMeso(owner.getClient());
        return false;
    }

    @Override
    public void arrangeItems(Client c) {
        owner.dropMessage(1, "奖品仓库只能取出，无法存放/排列。");
        sendMeso(owner.getClient());
        return;
    }

}

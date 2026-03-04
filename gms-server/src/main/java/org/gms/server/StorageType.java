package org.gms.server;

import org.gms.client.inventory.ItemFactory;

import lombok.Getter;

public enum StorageType {
    AccountStorage(0, ItemFactory.STORAGE),
    GachaponStorage(1, ItemFactory.GACHAPON_STORAGE);


    @Getter
    private final int value;
    @Getter
    private final ItemFactory itemFactory;

    private StorageType(int value, ItemFactory itemFactory) {
        this.value = value;
        this.itemFactory = itemFactory;
    }
}

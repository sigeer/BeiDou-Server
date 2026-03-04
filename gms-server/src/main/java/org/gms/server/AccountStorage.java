package org.gms.server;

import org.gms.client.Character;
import org.gms.client.inventory.Item;
import org.gms.config.GameConfig;
import org.gms.provider.Data;
import org.gms.provider.DataProvider;
import org.gms.provider.DataProviderFactory;
import org.gms.provider.DataTool;
import org.gms.provider.wz.WZFiles;
import org.gms.util.PacketCreator;
import java.util.HashMap;
import java.util.Map;

public class AccountStorage extends AbstractStorage {
    private static final Map<Integer, Integer> trunkGetCache = new HashMap<>();
    private static final Map<Integer, Integer> trunkPutCache = new HashMap<>();

    public AccountStorage(Character owner, byte slot, int meso) {
        super(owner, slot, meso);

        storageType = StorageType.AccountStorage;
    }

    @Override
    protected boolean baseCheck() {
        if (owner.getLevel() < 15) {
            owner.dropMessage(1, "15级以后才可以使用仓库服务");
            sendMeso(owner.getClient());
            return false;
        }

        if (owner.isGM() && owner.gmLevel() < GameConfig.getServerInt("minimum_gm_level_to_use_storage")) {
            owner.dropMessage(1, "当前GM级别禁止使用仓库");
            log.info(String.format("GM %s blocked from using storage", owner.getName()));
            sendMeso(owner.getClient());
            return false;
        }

        return true;
    }

    private int getStoreFee() {  // thanks to GabrielSin
        int npcId = currentNpcid;
        Integer fee = trunkPutCache.get(npcId);
        if (fee == null) {
            fee = 100;

            DataProvider npc = DataProviderFactory.getDataProvider(WZFiles.NPC);
            Data npcData = npc.getData(npcId + ".img");
            if (npcData != null) {
                fee = DataTool.getIntConvert("info/trunkPut", npcData, 100);
            }

            trunkPutCache.put(npcId, fee);
        }

        return fee;
    }

    private int getTakeOutFee() {
        int npcId = currentNpcid;
        Integer fee = trunkGetCache.get(npcId);
        if (fee == null) {
            fee = 0;

            DataProvider npc = DataProviderFactory.getDataProvider(WZFiles.NPC);
            Data npcData = npc.getData(npcId + ".img");
            if (npcData != null) {
                fee = DataTool.getIntConvert("info/trunkGet", npcData, 0);
            }

            trunkGetCache.put(npcId, fee);
        }

        return fee;
    }

    @Override
    public boolean takeOutItemCheck(Item item) {
        int fee = getTakeOutFee();
        if (owner.getMeso() < fee) {
            owner.sendPacket(PacketCreator.getStorageError((byte) 0x0B));
            return false;
        }

        return super.takeOutItemCheck(item);
    }

    @Override
    public boolean storeItemCheck(short slot, int itemId, short quantity)  {
        int fee = getStoreFee();
        if (owner.getMeso() < fee) {
            owner.sendPacket(PacketCreator.getStorageError((byte) 0x0B));
            return false;
        }

        return super.storeItemCheck(slot, itemId, quantity);
    }

    @Override
    public void onTakeOutSuccess () {
        int fee = getTakeOutFee();
        owner.gainMeso(-fee, false);
    }

    @Override
    public void OnStoreSuccess () {
        int fee = getStoreFee();
        owner.gainMeso(-fee, false, true, false);
    }
}

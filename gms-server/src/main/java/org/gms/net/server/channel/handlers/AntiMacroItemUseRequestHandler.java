package org.gms.net.server.channel.handlers;

import org.gms.client.Client;
import org.gms.client.Character;
import org.gms.client.antiMacro.AntiMacroPackets;
import org.gms.client.antiMacro.AntiMacroType;
import org.gms.client.antiMacro.AntiMacroService;
import org.gms.client.inventory.InventoryType;
import org.gms.client.inventory.manipulator.InventoryManipulator;
import org.gms.constants.id.ItemId;
import org.gms.net.AbstractPacketHandler;
import org.gms.net.packet.InPacket;
import org.gms.util.PacketCreator;

public class AntiMacroItemUseRequestHandler extends AbstractPacketHandler {

    @Override
    public void handlePacket(InPacket p, Client c) {
        Character chr = c.getPlayer();

        if (!chr.isAlive()) {
            c.sendPacket(PacketCreator.enableActions());
            return;
        }

        // ---- 读取客户端数据包 ----
        String targetName = p.readString();
        short slot = p.readShort();
        int itemId = p.readInt();

        // ---- 验证物品 ----
        if (itemId != ItemId.AntiMacroItem) {
            c.sendPacket(PacketCreator.enableActions());
            return;
        }

        var inv = chr.getInventory(InventoryType.USE);
        var item = inv.getItem(slot);
        if (item == null || item.getItemId() != itemId || item.getQuantity() <= 0) {
            c.sendPacket(PacketCreator.enableActions());
            return;
        }

        // ---- 验证目标 ----
        var target = chr.getMap().getCharacterByName(targetName);
        if (target == null || target == chr) {
            c.sendPacket(AntiMacroPackets.PlayerNotFound());
            return;
        }

        // ---- 发包 + 超时检测 ----
        chr.getWorldServer().getAntiMacroService().sendAntiMacro(chr, target, AntiMacroType.Item, () ->
        {
            InventoryManipulator.removeFromSlot(c, InventoryType.USE, slot, (short)1, false);
        });

        c.sendPacket(PacketCreator.enableActions());
    }

    
}

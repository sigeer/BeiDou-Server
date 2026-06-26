package org.gms.net.server.channel.handlers;

import org.gms.client.Client;
import org.gms.client.Character;
import org.gms.client.antiMacro.AntiMacroPackets;
import org.gms.client.antiMacro.AntiMacroType;
import org.gms.net.AbstractPacketHandler;
import org.gms.net.packet.InPacket;
import org.gms.util.PacketCreator;

public class AntiMacroSkillUseRequestHandler extends AbstractPacketHandler {

    @Override
    public void handlePacket(InPacket p, Client c) {
        Character chr = c.getPlayer();

        if (!chr.isAlive() || !chr.isGM()) {
            c.sendPacket(PacketCreator.enableActions());
            return;
        }

        String targetName = p.readString();

        Character target = chr.getMap().getCharacterByName(targetName);
        if (target == null || target == chr) {
            c.sendPacket(AntiMacroPackets.PlayerNotFound());
            return;
        }

        chr.getWorldServer().getAntiMacroService().sendAntiMacro(chr, target, AntiMacroType.AdminSkill, null);
    }

}

package org.gms.net.server.channel.handlers;

import org.gms.client.Client;
import org.gms.client.Character;

import org.gms.net.AbstractPacketHandler;
import org.gms.net.packet.InPacket;

public class AntiMacroResponseHandler extends AbstractPacketHandler {
    @Override
    public void handlePacket(InPacket p, Client c) {
        Character chr = c.getPlayer();
        chr.getWorldServer().getAntiMacroService().handleAnswer(chr, p.readString());
    }
}

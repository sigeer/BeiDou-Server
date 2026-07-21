/*
    This file is part of the HeavenMS MapleStory Server, commands OdinMS-based
    Copyleft (L) 2016 - 2019 RonanLana

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as
    published by the Free Software Foundation version 3 as published by
    the Free Software Foundation. You may not use, modify or distribute
    this program under any other version of the GNU Affero General Public
    License.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

/*
   @Author: Arthur L - Refactored command content into modules
*/
package org.gms.client.command.commands.gm2;

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.client.command.Command;
import org.gms.constants.id.NpcId;
import org.gms.server.ItemInformationProvider;
import org.gms.server.life.Monster;
import org.gms.server.life.NPC;
import org.gms.server.life.PlayerNPC;
import org.gms.server.maps.Door;
import org.gms.server.maps.HiredMerchant;
import org.gms.server.maps.MapItem;
import org.gms.server.maps.MapObject;
import org.gms.server.maps.MapObjectType;
import org.gms.server.maps.PlayerShop;
import org.gms.server.maps.Portal;
import org.gms.server.maps.Reactor;
import org.gms.server.maps.Summon;
import org.gms.util.I18nUtil;
import org.gms.util.PacketCreator;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

public class WhereaMiCommand extends Command {
    {
        setDescription(I18nUtil.getMessage("WhereaMiCommand.message1"));
    }

    @Override
    public void execute(Client c, String[] params) {
        Character player = c.getPlayer();

        StringBuilder sb = new StringBuilder();
        sb.append(I18nUtil.getMessage("WhereaMiCommand.message1")).append("\r\n");
        sb.append(I18nUtil.getMessage("WhereaMiCommand.message2")).append(player.getMap().getId()).append("\r\n");
        sb.append(I18nUtil.getMessage("WhereaMiCommand.message3")).append(player.getEventInstance().getName()).append("\r\n");
        sb.append(I18nUtil.getMessage("WhereaMiCommand.message4")).append(player.getMap().getEventInstance().getName()).append("\r\n");
        sb.append(I18nUtil.getMessage("WhereaMiCommand.message5")).append(player.getPosition()).append("\r\n");
        sb.append(I18nUtil.getMessage("WhereaMiCommand.message6")).append(player.getMap().getFootholds().findBelow(player.getPosition()).getId()).append("\r\n");
        sb.append(I18nUtil.getMessage("WhereaMiCommand.message7")).append(player.getStance()).append("\r\n");

        Portal closestPortal = player.getMap().findClosestPortal(player.getPosition());

        sb
            .append("Id: ").append(closestPortal.getId()).append("\r\n")
            .append("PortalType(pt): ").append(closestPortal.getType()).append("\r\n")
            .append("PortalName(pn): ").append(closestPortal.getName()).append("\r\n")
            .append("TargetName(tn): ").append(closestPortal.getTarget()).append("\r\n")
            .append("TargetMap(tm): ").append(closestPortal.getTargetMapId()).append("\r\n")
            .append("Script: ").append(closestPortal.getScriptName()).append("\r\n");
            
        Map<MapObjectType, List<MapObject>> allMapObjects = player.getMap().getMapObjects().stream()
            .sorted(Comparator.comparingDouble(x -> x.getPosition().distanceSq(player.getPosition())))
            .collect(Collectors.groupingBy(x -> x.getType(), LinkedHashMap::new, Collectors.toList()));
        sb.append("=========MapObject=========\r\n\r\n");


        for (Entry<MapObjectType, List<MapObject>> group : allMapObjects.entrySet()) {
            sb.append(group.getKey()).append("===>\r\n");

            for (MapObject mapObj : group.getValue()) {
                int sourceId = mapObj.getObjectId();
                String displayName = group.getKey().toString();

                if (mapObj instanceof Monster mob) {
                    displayName = mob.getName();
                    sourceId = mob.getId();
                } else if (mapObj instanceof NPC npc) {
                    displayName = npc.getName();
                    sourceId = npc.getId();
                } else if (mapObj instanceof PlayerNPC playerNPC) {
                    displayName = playerNPC.getName();
                    sourceId = playerNPC.getScriptId();
                } else if (mapObj instanceof Character mapChr) {
                    displayName = mapChr.getName();
                    sourceId = mapChr.getId();
                } else if (mapObj instanceof MapItem mapitem) {
                    displayName = ItemInformationProvider.getInstance().getName(mapitem.getItemId()); 
                    sourceId = mapitem.getItemId();
                } else if (mapObj instanceof HiredMerchant hm) {
                    displayName = hm.getDescription();
                    sourceId = hm.getItemId();
                } else if (mapObj instanceof PlayerShop ps) {
                    displayName = ps.getDescription();
                    sourceId = ps.getItemId();
                } else if (mapObj instanceof Reactor reactor) {
                    sourceId = reactor.getId();
                } else if (mapObj instanceof Door door) {
                    sourceId = door.getOwnerId();
                } else if (mapObj instanceof Summon summon) {
                    sourceId = summon.getOwner().getId();
                }

                sb.append(">> ")
                    .append(displayName)
                    .append(" - ")
                    .append("ID: ").append(sourceId)
                    .append(" - ")
                    .append(I18nUtil.getMessage("WhereaMiCommand.message5")).append(mapObj.getObjectId())
                    .append(" - ")
                    .append(I18nUtil.getMessage("WhereaMiCommand.message7")).append(mapObj.getPosition()).append("\r\n");
            }
        }

        c.sendPacket(PacketCreator.getNPCTalk(NpcId.MAPLE_ADMINISTRATOR, (byte) 0, sb.toString(), "00 00", (byte) 0));
    }
}

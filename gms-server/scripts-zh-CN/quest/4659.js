/*
	This file is part of the OdinMS Maple Story Server
    Copyright (C) 2008 Patrick Huy <patrick.huy@frz.cc>
		       Matthias Butz <matze@odinms.de>
		       Jan Christian Meyer <vimes@odinms.de>

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
/* 	Author: Moogra
	NPC Name: 		?????????????
	Map(s): 		New Leaf City
	Description: 		Quest - Pet Evolution
*/

var status = -1;

function start(mode, type, selection) {
//nothing here?
}

function end(mode, type, selection) {
    status++;
    if (mode != 1) {
        if (type == 1 && mode == 0) {
            status -= 2;
        } else {
            qm.dispose();
            return;
        }
    }
    if (status == 0) {
        qm.sendNext("找到进化材料做得很好。现在我会给你一个机器人");
    } else if (status == 1) {
        if (qm.isQuestCompleted(4659)) {
            qm.dropMessage(1, "这是怎么回事?");
            qm.dispose();
        } else if (qm.canHold(5000048)) {
            var petIds = qm.getPlayer().getPets()
                .filter(x => x != null && x.getItemId() == 5000048 && x.getLevel() >= 15)
                .map(x => x.getUniqueId());
            if (petIds.length === 0) {
                qm.sendOk("看来你的宠物还没有成长到可以进化的程度。再训练它一段时间，直到它达到 #b15级#k.");
                qm.dispose();
                return;
            }
            qm.askPetLevel("SelectPet", "选择要进化哪只宠物？", petIds);
        } else {
            qm.dropMessage(1, "你的背包已满");
            qm.dispose();
        }
    }
}

function levelSelectPet(petId) {
    var petSlot = qm.getPlayer().getPetIndex(petId);
    if (petSlot < 0) {
        qm.sendOk("宠物无法进化.");
        qm.dispose();
        return;
    }

    var newPet = qm.evolvePet(petSlot);
    if (newPet == null) {
        qm.sendOk("出了点问题。请重试。");
        qm.dispose();
        return;
    }

    qm.gainItem(5380000, -1);
    qm.gainItem(4000111, -50);
    qm.completeQuest();

    qm.dispose();
}
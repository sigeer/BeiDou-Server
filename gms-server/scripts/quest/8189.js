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
/* 	Author: 		Blue
	Name:	 		Garnox
	Map(s): 		New Leaf City : Town Center
	Description: 		Quest - Pet Re-Evolution
*/

var status = -1;

function end(mode, type, selection) {
    if (mode == -1) {
        qm.dispose();
    } else {
        if (mode == 1) {
            status++;
        } else {
            status--;
        }
        if (status == 0) {
            if (qm.getMeso() < 10000) {
                qm.sendOk("Hey! I need #b10,000 mesos#k to do your pet's re-evolution!");
                qm.dispose();
                return;
            }

            qm.sendYesNo("Alright then, let's do this again, shall we? As usual, it's going to be random, and I'm going to take away one of your Rock of Evolutions. \r\n\r #r#eReady?#n#k");
        } else if (status == 1) {
            qm.sendNextPrev("Then here we go...! #rHYAHH!#k");
        } else if (status == 2) {
            var petIds = qm.getPlayer().getPets()
                .filter(x => x != null && ((x.getItemId() >= 5000030 && x.getItemId() <= 5000033) || (x.getItemId() >= 5000049 && x.getItemId() <= 5000052)) && x.getLevel() >= 15)
                .map(x => x.getUniqueId());
            if (petIds.length === 0) {
                qm.sendOk("It looks like your pet is not grown enough to be evolved yet. Train it a bit more, util it reaches #blevel 15#k.");
                qm.dispose();
                return;
            }

            qm.askPetLevel("SelectPet", "Which pet do you want to evolve?", petIds);
        }
    }
}

function levelSelectPet(petId) {
    var petSlot = qm.getPlayer().getPetIndex(petId);
    if (petSlot < 0) {
        qm.sendOk("Pet could not be evolved.");
        qm.dispose();
        return;
    }

    var oldPet = qm.getPlayer().getPet(petSlot);
    var newPet = qm.evolvePet(petSlot);
    if (newPet == null) {
        qm.sendOk("Something wrong, try again.");
        qm.dispose();
        return;
    }

    qm.gainItem(5380000, -1);
    qm.gainMeso(-10000);
    qm.completeQuest();

    qm.sendOk("Woo! It worked again! #rYou may find your new pet under your 'CASH' inventory.\r #kIt used to be a #b#i" + oldPet.getItemId() + "##t" + oldPet.getItemId() + "##k, and now it's \r a#b #i" + newPet.getItemId() + "##t" + newPet.getItemId() + "##k! \r\n Come back with 10,000 mesos and another Rock of Evolution if you don't like it!\r\n\r\n#fUI/UIWindow.img/QuestIcon/4/0#\r\n#v" + newPet.getItemId() + "# #t" + newPet.getItemId() + "#");
    qm.dispose();
}
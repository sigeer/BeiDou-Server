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
        if (qm.getMeso() < 10000) {
            qm.sendOk("Hey! I need #b10,000 mesos#k to do your pet's evolution!");
            qm.dispose();
            return;
        }
        qm.sendNext("Great job on finding your evolution materials. I will now give you a robot.");
    } else if (status == 1) {
        if (qm.isQuestCompleted(4659)) {
            qm.dropMessage(1, "how did this get here?");
            qm.dispose();
        } else if (qm.canHold(5000048)) {
            var petIds = qm.getPlayer().getPets()
                .filter(x => x != null && x.getItemId() == 5000048 && x.getLevel() >= 15)
                .map(x => x.getUniqueId());
            if (petIds.length === 0) {
                qm.sendOk("It looks like your pet is not grown enough to be evolved yet. Train it a bit more, util it reaches #blevel 15#k.");
                qm.dispose();
                return;
            }
            qm.askPetLevel("SelectPet", "Which pet do you want to evolve?", petIds);
        } else {
            qm.dropMessage(1, "Your inventory is full");
            qm.dispose();
        }
    }
}

function levelSelectPet(petId) {
    var petSlot = qm.getPlayer().getPetIndex(petId);
    if (petSlot < 0) {
        qm.getPlayer().message("Pet could not be evolved.");
        qm.dispose();
        return;
    }

    var newPet = qm.evolvePet(petSlot);
    if (newPet == null) {
        qm.sendOk("Something wrong, try again.");
        qm.dispose();
        return;
    }

    qm.gainItem(5380000, -1);
    qm.gainItem(4000111, -50);
    qm.completeQuest();

    qm.dispose();
}
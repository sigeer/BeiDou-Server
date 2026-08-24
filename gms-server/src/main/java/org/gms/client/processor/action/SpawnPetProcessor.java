/*
    This file is part of the HeavenMS MapleStory Server
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
package org.gms.client.processor.action;

import org.gms.client.Character;
import org.gms.client.Client;
import org.gms.client.SkillFactory;
import org.gms.client.inventory.InventoryType;
import org.gms.client.inventory.Item;
import org.gms.client.inventory.Pet;
import org.gms.client.inventory.PetDataFactory;
import org.gms.client.inventory.manipulator.InventoryManipulator;
import org.gms.constants.id.ItemId;

/**
 * @author RonanLana - just added locking on OdinMS' SpawnPetHandler method body
 */
public class SpawnPetProcessor {
    public static void processSpawnPet(Client c, byte slot, boolean lead) {
        if (c.tryacquireClient()) {
            try {
                Character chr = c.getPlayer();

                Item petItem = chr.getInventory(InventoryType.CASH).getItem(slot);
                if (petItem == null) {
                    return;
                }

                Pet pet = petItem.getPet();
                if (pet == null) {
                    return;
                }

                if (chr.getPetIndex(pet) != -1) {
                    chr.unEquipPet(pet, true);
                } else {
                    if (chr.getSkillLevel(SkillFactory.getSkill(chr.getJob().getMultiPetSkillId())) == 0 && chr.getPet(0) != null) {
                        chr.unEquipPet(chr.getPet(0), false);
                    }

                    int petItemId = pet.getItemId();
                    if (petItemId == ItemId.DRAGON_PET || petItemId == ItemId.ROBO_PET) {
                        int evolveid = PetDataFactory.getEvolvedPetItemId(petItemId);
                        if (evolveid == 0) {
                            return;
                        }
                        int petId = Pet.createPet(evolveid);
                        if (petId == -1) {
                            return;
                        }
                        
                        long expiration = petItem.getExpiration();
                        InventoryManipulator.removeFromSlot(c, InventoryType.CASH, petItem.getPosition(), (short)1, false);
                        InventoryManipulator.addById(c, evolveid, (short) 1, null, petId, expiration);

                        pet = chr.getInventory(InventoryType.CASH).findByCashId(petId).getPet();
                        if (pet == null) {
                            return;
                        }
                    }

                    if (lead) {
                        chr.shiftPetsRight();
                    }

                    chr.summonPet(pet, -1, false);
                }
            } finally {
                c.releaseClient();
            }
        }
    }
}

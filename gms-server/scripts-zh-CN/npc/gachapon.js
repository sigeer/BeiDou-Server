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
/* NPC Base
	Map Name (Map ID)
	Extra NPC info.
 */

var status;
var ticketId = 5220000;
var mapName = ["Henesys", "Ellinia", "Perion", "Kerning City", "Sleepywood", "Mushroom Shrine", "Showa Spa (M)", "Showa Spa (F)", "Ludibrium", "New Leaf City", "El Nath", "Nautilus"];
var curMapName = "";

function start() {
    status = -1;
    curMapName = mapName[(cm.getNpc() != 9100117 && cm.getNpc() != 9100109) ? (cm.getNpc() - 9100100) : cm.getNpc() == 9100109 ? 9 : 11];

    if (cm.haveItem(ticketId)) {
        cm.sendSelectLevel(`欢迎来到${curMapName}扭蛋机。我可以为您做些什么呢？\r\n\r\n
            #L0#什么是扭蛋机？#l\r\n
            #L1#在哪里可以购买#t${ticketId}#？#l\r\n
            #L2#使用1张#t${ticketId}#。#l\r\n
            #L3#查看我的#r奖品仓库#k。#l`);
    } else {
        cm.sendSelectLevel(`欢迎来到${curMapName}扭蛋机。我可以为您做些什么呢？\r\n\r\n
            #L0#什么是扭蛋机？#l\r\n
            #L1#在哪里可以购买#t${ticketId}#？#l\r\n
            #L3#查看我的#r奖品仓库#k。#l`);
    }
}

function level0 () {
    cm.sendNextLevel("0More", `玩转扭蛋机，赢得稀有卷轴、装备、椅子、熟练书和其他酷炫物品！你只需要一张 #i${ticketId}##b#t${ticketId}##k 就有机会成为随机物品的幸运获得者。`);
}

function level0More() {
     cm.sendLastNextLevel("0", "dispose", "你会在" + curMapName + "的扭蛋机中找到各种物品，但最有可能找到与" + curMapName + "相关的物品和卷轴。");
}

function level1() {
    cm.sendNextLevel("dispose", `#i${ticketId}##b#t${ticketId}##k 可以在#r现金商店#k使用NX或枫叶点购买。点击屏幕右下角的红色商店图标访问#r现金商店#k。`);
}

function level2() {
    if (cm.getPlayer().getGachaponStorage().canGainItems(1)) { // One free slot in every inventory.
        cm.gainItem(ticketId, -1);
        cm.doGachapon();
        cm.dispose();
    } else {
        cm.sendOkLevel("dispose", "请确保你的#r装备、消耗、设置#k和#r其他#k物品栏中至少有一个空位。");
    }
}

function level3() {
    cm.openGachaponStorage();
    cm.dispose();
}

function leveldispose() {
    cm.dispose();
}
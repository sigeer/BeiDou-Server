const dataSource = [
    { name: "Deo", displayName: "Deo", mapId: 260010201, mobId: 3220001, pos: [{ minX: 645, maxX: 645, y: 275 }], mobTime: 3 * 60 * 60, msg: "Deo slowly appeared out of the sand dust." },
    { name: "Bamboo", displayName: "Bamboo Warrior", mapId: 800020120, mobId: 6090002, pos: [{ minX: 600, maxX: 700, y: 50 }], mobTime: 3 * 60 * 60, msg: "From amongst the ruins shrouded by the mists, Bamboo Warrior appears." },
    { name: "Centipede", displayName: "Giant Centipede", mapId: 251010102, mobId: 5220004, pos: [{ minX: 600, maxX: 700, y: 50 }], mobTime: 3 * 60 * 60, msg: "From the mists surrounding the herb garden, the gargantuous Giant Centipede appears." },
    { name: "Kimera", displayName: "Chimera", mapId: 261030000, mobId: 8220002, pos: [{ minX: -900, maxX: 0, y: 180 }], mobTime: 3 * 60 * 60, msg: "Kimera has appeared out of the darkness of the underground with a glitter in her eyes." },
    { name: "KingClang", displayName: "King Clang", mapId: 110040000, mobId: 5220001, pos: [{ minX: -1600, maxX: 800, y: 140 }], mobTime: 3 * 60 * 60, msg: "A strange turban shell has appeared on the beach." },
    { name: "Faust1", displayName: "Faust", mapId: 100040105, mobId: 5220002, pos: [{ minX: 456, maxX: 456, y: 278 }], mobTime: 3 * 60 * 60, msg: "Faust appeared amidst the blue fog." },
    { name: "Faust2", displayName: "Faust", mapId: 100040106, mobId: 5220002, pos: [{ minX: 474, maxX: 474, y: 278 }], mobTime: 3 * 60 * 60, msg: "Faust appeared amidst the blue fog." },
    { name: "Eliza", displayName: "Eliza", mapId: 200010300, mobId: 8220000, pos: [{ minX: 208, maxX: 208, y: 83 }], mobTime: 3 * 60 * 60, msg: "Eliza has appeared with a black whirlwind." },
    { name: "Dyle", displayName: "Dyle", mapId: 107000300, mobId: 6220000, pos: [{ minX: 90, maxX: 90, y: 119 }], mobTime: 3 * 60 * 60, msg: "The huge crocodile Dyle has come out from the swamp." },
    { name: "Mano", displayName: "Mano", mapId: 104000400, mobId: 2220000, pos: [{ minX: 279, maxX: 279, y: -496 }], mobTime: 3 * 60 * 60, msg: "A cool breeze was felt when Mano appeared." },
    { name: "Zeno", displayName: "Zeno", mapId: 221040301, mobId: 6220001, pos: [{ minX: -4224, maxX: -4224, y: 776 }], mobTime: 3 * 60 * 60, msg: "Zeno has appeared with a heavy sound of machinery." },
    { name: "TaeRoon", displayName: "Tae Roon", mapId: 250010304, mobId: 7220000, pos: [{ minX: -800, maxX: -100, y: 390 }], mobTime: 3 * 60 * 60, msg: "Tae Roon has appeared with a soft whistling sound." },
    { name: "Stumpy", displayName: "Stumpy", mapId: 101030404, mobId: 3220000, pos: [{ minX: 400, maxX: 1200, y: 1280 }], mobTime: 3 * 60 * 60, msg: "Stumpy has appeared with a stumping sound that rings the Stone Mountain." },
    { name: "KingSageCat", displayName: "King Sage Cat", mapId: 250010504, mobId: 7220002, pos: [{ minX: -500, maxX: 800, y: 540 }], mobTime: 3 * 60 * 60, msg: "The ghostly air around here has become stronger. The unpleasant sound of a cat crying can be heard." },
    { name: "NineTailedFox", displayName: "Nine-Tailed Fox", mapId: 222010310, mobId: 7220001, pos: [{ minX: -800, maxX: 500, y: 33 }], mobTime: 3 * 60 * 60, msg: "As the moon light dims, a long fox cry can be heard and the presence of the old fox can be felt" },
    { name: "Seruf", displayName: "Seruf", mapId: 230020100, mobId: 4220001, pos: [{ minX: -1500, maxX: 800, y: 520 }], mobTime: 3 * 60 * 60, msg: "A strange shell has appeared from a grove of seaweed" },
    { name: "Leviathan", displayName: "Leviathan", mapId: 240040401, mobId: 8220003, pos: [{ minX: -300, maxX: 300, y: 1125 }], mobTime: 3 * 60 * 60, msg: "Leviathan emerges from the canyon and the cold icy wind blows.‌" },
    { name: "SnackBar", displayName: "Unknow Snack Bar", mapId: 105090310, mobId: 8220008, pos: [{ minX: -626, maxX: -626, y: -604 }, { minX: 735, maxX: 735, y: -600 }], mobTime: 3 * 60 * 60, msg: "Slowly, a suspicious food stand opens up on a strangely remote place." },
    { name: "Timer1", displayName: "Timer", mapId: 220050100, mobId: 5220003, pos: [{ minX: -770, maxX: 0, y: 1030 }], mobTime: 3 * 60 * 60, msg: "Tick-Tock Tick-Tock! Timer makes it's presence known." },
    { name: "Timer2", displayName: "Timer", mapId: 220050000, mobId: 5220003, pos: [{ minX: -1000, maxX: 400, y: 1030 }], mobTime: 3 * 60 * 60, msg: "Tick-Tock Tick-Tock! Timer makes it's presence known." },
    { name: "Timer3", displayName: "Timer", mapId: 220050200, mobId: 5220003, pos: [{ minX: -700, maxX: 700, y: 1030 }], mobTime: 3 * 60 * 60, msg: "Tick-Tock Tick-Tock! Timer makes it's presence known." },
];

function init() {
    for (const item of dataSource) {
        const map = em.getChannelServer().getMapFactory().getMap(item.mapId);
        map.setupAreaBoss(item.name, item.mobId, em.getBossTime(item.mobTime), item.pos, item.msg);
    }
}

// ---------- FILLER FUNCTIONS ----------

function dispose() {}

function setup(eim, leaderid) {}

function monsterValue(eim, mobid) {return 0;}

function disbandParty(eim, player) {}

function playerDisconnected(eim, player) {}

function playerEntry(eim, player) {}

function monsterKilled(mob, eim) {}

function scheduledTimeout(eim) {}

function afterSetup(eim) {}

function changedLeader(eim, leader) {}

function playerExit(eim, player) {}

function leftParty(eim, player) {}

function clearPQ(eim) {}

function allMonstersDead(eim) {}

function playerUnregistered(eim, player) {}

function cancelSchedule() {}

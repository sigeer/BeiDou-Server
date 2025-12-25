const dataSource = [
    { name: "Deo", displayName: "大宇", mapId: 260010201, mobId: 3220001, pos: [{ minX: 645, maxX: 645, y: 275 }], mobTime: 3 * 60 * 60, msg: "自沙暴中缓缓显形，黄沙如时光之纱寸寸剥落‌" },
    { name: "Bamboo", displayName: "青竹武士", mapId: 800020120, mobId: 6090002, pos: [{ minX: 600, maxX: 700, y: 50 }], mobTime: 3 * 60 * 60, msg: "青岚自氤氲雾气笼罩的断垣残壁间，骤然现形！" },
    { name: "Centipede", displayName: "巨型蜈蚣", mapId: 251010102, mobId: 5220004, pos: [{ minX: 600, maxX: 700, y: 50 }], mobTime: 3 * 60 * 60, msg: "从药圃生瘴处破雾而出，千足划空如戈戟森然‌" },
    { name: "Kimera", displayName: "吉米拉", mapId: 261030000, mobId: 8220002, pos: [{ minX: -900, maxX: 0, y: 180 }], mobTime: 3 * 60 * 60, msg: "自地底幽暗中现身的刹那，眼瞳里跃动的碎光如未淬火的星屑‌" },
    { name: "KingClang", displayName: "巨居蟹", mapId: 110040000, mobId: 5220001, pos: [{ minX: -1600, maxX: 800, y: 140 }], mobTime: 3 * 60 * 60, msg: "出现在海岸线上，一顶形似螺壳的奇异头巾随潮水起伏，其螺旋纹路在夕照下泛着妖异的磷光" },
    { name: "Faust1", displayName: "浮士德", mapId: 100040105, mobId: 5220002, pos: [{ minX: 456, maxX: 456, y: 278 }], mobTime: 3 * 60 * 60, msg: "自靛蓝雾霭中显形时，整片天空呈现出光谱折射的奇异蓝调，仿佛歌德手稿里未完成的魔法阵正在现世具象化" },
    { name: "Faust2", displayName: "浮士德", mapId: 100040106, mobId: 5220002, pos: [{ minX: 474, maxX: 474, y: 278 }], mobTime: 3 * 60 * 60, msg: "自靛蓝雾霭中显形时，整片天空呈现出光谱折射的奇异蓝调，仿佛歌德手稿里未完成的魔法阵正在现世具象化" },
    { name: "Eliza", displayName: "艾利杰", mapId: 200010300, mobId: 8220000, pos: [{ minX: 208, maxX: 208, y: 83 }], mobTime: 3 * 60 * 60, msg: "降临之时，黑色旋风如巨蟒盘绕，将天光绞碎成纷扬的鸦羽" },
    { name: "Dyle", displayName: "多尔", mapId: 107000300, mobId: 6220000, pos: [{ minX: 90, maxX: 90, y: 119 }], mobTime: 3 * 60 * 60, msg: "破开沼泽浮出时，腐殖质的气味与沼气在它嶙峋的背甲上凝结成绿色雾霭" },
    { name: "Mano", displayName: "红蜗牛王", mapId: 104000400, mobId: 2220000, pos: [{ minX: 279, maxX: 279, y: -496 }], mobTime: 3 * 60 * 60, msg: "玛诺的身影伴随着凉风乍起时悄然显现，落叶在她脚边打着旋儿静止" },
    { name: "Zeno", displayName: "朱诺", mapId: 221040301, mobId: 6220001, pos: [{ minX: -4224, maxX: -4224, y: 776 }], mobTime: 3 * 60 * 60, msg: "伴随重型机械轰鸣声现世！" },
    { name: "TaeRoon", displayName: "肯德熊", mapId: 250010304, mobId: 7220000, pos: [{ minX: -800, maxX: -100, y: 390 }], mobTime: 3 * 60 * 60, msg: "伴着一声柔和的哨音翩然而至‌" },
    { name: "Stumpy", displayName: "树妖王", mapId: 101030404, mobId: 3220000, pos: [{ minX: 400, maxX: 1200, y: 1280 }], mobTime: 3 * 60 * 60, msg: "伴着沉闷的撞击声现身于石山之间，余音在山谷中回荡‌" },
    { name: "KingSageCat", displayName: "妖怪禅师", mapId: 250010504, mobId: 7220002, pos: [{ minX: -500, maxX: 800, y: 540 }], mobTime: 3 * 60 * 60, msg: "的出现使幽魅之气愈发浓稠，野猫的泣鸣如锈蚀的刀片划破凝滞的夜雾‌" },
    { name: "NineTailedFox", displayName: "九尾狐", mapId: 222010310, mobId: 7220001, pos: [{ minX: -800, maxX: 500, y: 33 }], mobTime: 3 * 60 * 60, msg: "伴随着一声悠长的狐啸划破夜色，那老狐的气息如雾霭般在林间弥散‌" },
    { name: "Seruf", displayName: "歇尔夫", mapId: 230020100, mobId: 4220001, pos: [{ minX: -1500, maxX: 800, y: 520 }], mobTime: 3 * 60 * 60, msg: "，一枚泛着诡异磷光的贝壳悄然浮现在幽暗的海藻林中，其螺旋纹路在流动的藻须间若隐若现‌" },
    { name: "Leviathan", displayName: "大海兽", mapId: 240040401, mobId: 8220003, pos: [{ minX: -300, maxX: 300, y: 1125 }], mobTime: 3 * 60 * 60, msg: "昂首而起的刹那，裹挟着冰晶的寒风如远古战吼般撕裂云层‌" },
    { name: "SnackBar", displayName: "小吃店", mapId: 105090310, mobId: 8220008, pos: [{ minX: -626, maxX: -626, y: -604 }, { minX: 735, maxX: 735, y: -600 }], mobTime: 3 * 60 * 60, msg: "正缓缓在幽僻的荒郊野径旁支起招牌，蒸腾的热气在空荡的山路上显得格外扎眼。‌" },
    { name: "Timer1", displayName: "提莫", mapId: 220050100, mobId: 5220003, pos: [{ minX: -770, maxX: 0, y: 1030 }], mobTime: 3 * 60 * 60, msg: "嘀嗒...嘀嗒...！时间精灵提莫在轻声提醒。" },
    { name: "Timer2", displayName: "提莫", mapId: 220050000, mobId: 5220003, pos: [{ minX: -1000, maxX: 400, y: 1030 }], mobTime: 3 * 60 * 60, msg: "嘀嗒...嘀嗒...！时间精灵提莫在轻声提醒。" },
    { name: "Timer3", displayName: "提莫", mapId: 220050200, mobId: 5220003, pos: [{ minX: -700, maxX: 700, y: 1030 }], mobTime: 3 * 60 * 60, msg: "嘀嗒...嘀嗒...！时间精灵提莫在轻声提醒。" },
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

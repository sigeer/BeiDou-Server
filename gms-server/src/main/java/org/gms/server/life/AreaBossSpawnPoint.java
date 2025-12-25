package org.gms.server.life;

import org.gms.server.maps.MapleMap;
import org.gms.util.RandomPoint;
import org.gms.util.Randomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gms.net.server.Server;
import org.gms.client.Character;
import static java.util.concurrent.TimeUnit.SECONDS;

import java.awt.Point;
import java.util.List;


public class AreaBossSpawnPoint extends SpawnPoint {
    private static final Logger log = LoggerFactory.getLogger(AreaBossSpawnPoint.class);
    private static final Point emptyPoint = new Point();
    
    String spawnMessage;
    List<RandomPoint> points;
    final MapleMap map;

    public AreaBossSpawnPoint(MapleMap map, int monsterId, List<RandomPoint> pos, int mobTime, int mobInterval, int team, String spawnMessage) {
        super(monsterId, 0, 0, emptyPoint, mobTime, mobInterval, team);
        this.map = map;
        this.spawnMessage = spawnMessage;
        this.points = pos;
    }

    @Override
    protected void setMonsterPosition(Monster mob) {
        mob.setPosition(points.get(Randomizer.nextInt(points.size())).getPoint());
    }

    @Override
    protected MonsterListener getMonsterListener(Monster mob){
        return new MonsterListener() {
            @Override
            public void monsterSpawned() {
                log.info("[野外BOSS] 已在频道 {} 的 {}({}) {} 生成 {}({})，检测间隔：{} 分钟",
                    map.getChannelServer().getId(),
                    map.getMapName(),
                    map.getId(),
                    mob.getPosition(),
                    mob.getName(),
                    mob.getId(),
                    mobTime / 60
                );
                map.broadcastStringMessage(6, String.format("[野外BOSS] %s  %s", mob.getName(), spawnMessage));
            }

            @Override
            public void monsterKilled(Character killer, int aniTime) { }

            @Override
            public void monsterDamaged(Character from, int trueDmg) {}

            @Override
            public void monsterHealed(int trueHeal) {}

            @Override
            public void monsterCleared() {
                nextPossibleSpawn = Server.getInstance().getCurrentTime();
                if (mobTime > 0){
                    nextPossibleSpawn += SECONDS.toMillis(mobTime);
                }
                else {
                    nextPossibleSpawn += mobInterval;
                }
                spawnedMonsters.decrementAndGet();
            }
        };
    }
}

package org.gms.server.life;

import org.gms.client.Character;

public interface MonsterListener {

    void monsterSpawned();
    void monsterKilled(Character killer, int aniTime);
    void monsterDamaged(Character from, int trueDmg);
    void monsterHealed(int trueHeal);
    void monsterCleared();
}

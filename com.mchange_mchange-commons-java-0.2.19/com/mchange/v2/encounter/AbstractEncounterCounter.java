/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.encounter;

import com.mchange.v2.encounter.EncounterCounter;
import java.util.Map;

class AbstractEncounterCounter
implements EncounterCounter {
    static final Long ONE = new Long(1L);
    Map m;

    AbstractEncounterCounter(Map map) {
        this.m = map;
    }

    @Override
    public long encounter(Object object) {
        Long l;
        long l2;
        Long l3 = (Long)this.m.get(object);
        if (l3 == null) {
            l2 = 0L;
            l = ONE;
        } else {
            l2 = l3;
            l = new Long(l2 + 1L);
        }
        this.m.put(object, l);
        return l2;
    }

    @Override
    public long reset(Object object) {
        long l = this.encounter(object);
        this.m.remove(object);
        return l;
    }

    @Override
    public void resetAll() {
        this.m.clear();
    }
}


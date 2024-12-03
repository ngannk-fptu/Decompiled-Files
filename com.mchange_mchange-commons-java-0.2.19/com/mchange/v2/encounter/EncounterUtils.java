/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.encounter;

import com.mchange.v1.identicator.IdHashMap;
import com.mchange.v1.identicator.IdWeakHashMap;
import com.mchange.v1.identicator.Identicator;
import com.mchange.v2.encounter.EncounterCounter;
import com.mchange.v2.encounter.GenericEncounterCounter;

public final class EncounterUtils {
    public static EncounterCounter createStrong(Identicator identicator) {
        return new GenericEncounterCounter(new IdHashMap(identicator));
    }

    public static EncounterCounter createWeak(Identicator identicator) {
        return new GenericEncounterCounter(new IdWeakHashMap(identicator));
    }

    public static EncounterCounter syncWrap(final EncounterCounter encounterCounter) {
        return new EncounterCounter(){

            @Override
            public synchronized long encounter(Object object) {
                return encounterCounter.encounter(object);
            }

            @Override
            public synchronized long reset(Object object) {
                return encounterCounter.reset(object);
            }

            @Override
            public synchronized void resetAll() {
                encounterCounter.resetAll();
            }
        };
    }

    private EncounterUtils() {
    }
}


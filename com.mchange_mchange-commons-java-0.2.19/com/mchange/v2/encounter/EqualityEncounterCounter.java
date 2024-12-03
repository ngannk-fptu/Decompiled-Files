/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v2.encounter;

import com.mchange.v2.encounter.AbstractEncounterCounter;
import java.util.WeakHashMap;

public class EqualityEncounterCounter
extends AbstractEncounterCounter {
    public EqualityEncounterCounter() {
        super(new WeakHashMap());
    }
}


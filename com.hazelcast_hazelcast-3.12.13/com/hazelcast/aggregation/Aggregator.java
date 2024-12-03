/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.aggregation;

import java.io.Serializable;

public abstract class Aggregator<I, R>
implements Serializable {
    public abstract void accumulate(I var1);

    public void onAccumulationFinished() {
    }

    public abstract void combine(Aggregator var1);

    public void onCombinationFinished() {
    }

    public abstract R aggregate();
}


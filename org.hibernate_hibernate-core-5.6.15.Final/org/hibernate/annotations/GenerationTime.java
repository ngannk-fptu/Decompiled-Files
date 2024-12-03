/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.annotations;

import org.hibernate.tuple.GenerationTiming;

public enum GenerationTime {
    NEVER(GenerationTiming.NEVER),
    INSERT(GenerationTiming.INSERT),
    ALWAYS(GenerationTiming.ALWAYS);

    private final GenerationTiming equivalent;

    private GenerationTime(GenerationTiming equivalent) {
        this.equivalent = equivalent;
    }

    public GenerationTiming getEquivalent() {
        return this.equivalent;
    }
}


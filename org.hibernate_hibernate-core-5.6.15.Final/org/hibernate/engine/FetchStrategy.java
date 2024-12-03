/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine;

import org.hibernate.engine.FetchStyle;
import org.hibernate.engine.FetchTiming;

public class FetchStrategy {
    private final FetchTiming timing;
    private final FetchStyle style;

    public FetchStrategy(FetchTiming timing, FetchStyle style) {
        this.timing = timing;
        this.style = style;
    }

    public FetchTiming getTiming() {
        return this.timing;
    }

    public FetchStyle getStyle() {
        return this.style;
    }
}


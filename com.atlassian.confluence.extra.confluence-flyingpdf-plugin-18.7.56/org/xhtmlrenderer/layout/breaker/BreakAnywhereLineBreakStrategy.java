/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.layout.breaker;

import org.xhtmlrenderer.layout.breaker.BreakPoint;
import org.xhtmlrenderer.layout.breaker.BreakPointsProvider;

public class BreakAnywhereLineBreakStrategy
implements BreakPointsProvider {
    private String currentString;
    int position = 0;

    public BreakAnywhereLineBreakStrategy(String currentString) {
        this.currentString = currentString;
    }

    @Override
    public BreakPoint next() {
        if (this.position + 1 > this.currentString.length()) {
            return BreakPoint.getDonePoint();
        }
        return new BreakPoint(this.position++);
    }
}


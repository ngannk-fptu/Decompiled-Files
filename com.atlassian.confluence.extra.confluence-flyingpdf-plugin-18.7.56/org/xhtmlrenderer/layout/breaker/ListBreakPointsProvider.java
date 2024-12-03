/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.layout.breaker;

import java.util.Iterator;
import java.util.List;
import org.xhtmlrenderer.layout.breaker.BreakPoint;
import org.xhtmlrenderer.layout.breaker.BreakPointsProvider;

public class ListBreakPointsProvider
implements BreakPointsProvider {
    private Iterator<BreakPoint> breakPoints;

    public ListBreakPointsProvider(List<BreakPoint> breakPoints) {
        this.breakPoints = breakPoints.iterator();
    }

    @Override
    public BreakPoint next() {
        if (!this.breakPoints.hasNext()) {
            return BreakPoint.getDonePoint();
        }
        return this.breakPoints.next();
    }
}


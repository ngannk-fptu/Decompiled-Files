/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.sl.draw.geom;

import org.apache.poi.sl.draw.geom.Context;
import org.apache.poi.sl.draw.geom.GuideIf;

public interface AdjustValueIf
extends GuideIf {
    @Override
    default public double evaluate(Context ctx) {
        return this.evaluateAdjustValue(ctx);
    }

    default public double evaluateAdjustValue(Context ctx) {
        String name = this.getName();
        GuideIf adj = ctx.getAdjustValue(name);
        return adj != null ? adj.evaluate(ctx) : this.evaluateGuide(ctx);
    }
}


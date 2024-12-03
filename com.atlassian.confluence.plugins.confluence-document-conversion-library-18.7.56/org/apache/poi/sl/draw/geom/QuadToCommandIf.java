/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.sl.draw.geom;

import java.awt.geom.Path2D;
import org.apache.poi.sl.draw.geom.AdjustPointIf;
import org.apache.poi.sl.draw.geom.Context;
import org.apache.poi.sl.draw.geom.PathCommand;

public interface QuadToCommandIf
extends PathCommand {
    public AdjustPointIf getPt1();

    public void setPt1(AdjustPointIf var1);

    public AdjustPointIf getPt2();

    public void setPt2(AdjustPointIf var1);

    @Override
    default public void execute(Path2D.Double path, Context ctx) {
        AdjustPointIf pt1 = this.getPt1();
        double x1 = ctx.getValue(pt1.getX());
        double y1 = ctx.getValue(pt1.getY());
        AdjustPointIf pt2 = this.getPt2();
        double x2 = ctx.getValue(pt2.getX());
        double y2 = ctx.getValue(pt2.getY());
        path.quadTo(x1, y1, x2, y2);
    }
}


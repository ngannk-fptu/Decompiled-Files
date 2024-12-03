/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.sl.draw.geom;

import java.awt.geom.Path2D;
import org.apache.poi.sl.draw.geom.AdjustPointIf;
import org.apache.poi.sl.draw.geom.Context;
import org.apache.poi.sl.draw.geom.PathCommand;

public interface LineToCommandIf
extends PathCommand {
    public AdjustPointIf getPt();

    public void setPt(AdjustPointIf var1);

    @Override
    default public void execute(Path2D.Double path, Context ctx) {
        AdjustPointIf pt = this.getPt();
        double x = ctx.getValue(pt.getX());
        double y = ctx.getValue(pt.getY());
        path.lineTo(x, y);
    }
}


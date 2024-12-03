/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.sl.draw.geom;

import java.awt.geom.Arc2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import org.apache.poi.sl.draw.geom.ArcToCommand;
import org.apache.poi.sl.draw.geom.Context;
import org.apache.poi.sl.draw.geom.PathCommand;

public interface ArcToCommandIf
extends PathCommand {
    public void setHR(String var1);

    public void setWR(String var1);

    public void setStAng(String var1);

    public void setSwAng(String var1);

    public String getHR();

    public String getWR();

    public String getStAng();

    public String getSwAng();

    @Override
    default public void execute(Path2D.Double path, Context ctx) {
        double rx = ctx.getValue(this.getWR());
        double ry = ctx.getValue(this.getHR());
        double ooStart = ctx.getValue(this.getStAng()) / 60000.0;
        double ooExtent = ctx.getValue(this.getSwAng()) / 60000.0;
        double awtStart = ArcToCommand.convertOoxml2AwtAngle(ooStart, rx, ry);
        double awtSweep = ArcToCommand.convertOoxml2AwtAngle(ooStart + ooExtent, rx, ry) - awtStart;
        double radStart = Math.toRadians(ooStart);
        double invStart = Math.atan2(rx * Math.sin(radStart), ry * Math.cos(radStart));
        Point2D pt = path.getCurrentPoint();
        double x0 = pt.getX() - rx * Math.cos(invStart) - rx;
        double y0 = pt.getY() - ry * Math.sin(invStart) - ry;
        Arc2D.Double arc = new Arc2D.Double(x0, y0, 2.0 * rx, 2.0 * ry, awtStart, awtSweep, 0);
        path.append(arc, true);
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.sl.draw.geom;

import java.awt.geom.Path2D;
import org.apache.poi.sl.draw.geom.Context;
import org.apache.poi.sl.draw.geom.PathCommand;
import org.apache.poi.sl.usermodel.PaintStyle;

public interface PathIf {
    public void addCommand(PathCommand var1);

    public Path2D.Double getPath(Context var1);

    public boolean isStroked();

    public void setStroke(boolean var1);

    public boolean isFilled();

    public PaintStyle.PaintModifier getFill();

    public void setFill(PaintStyle.PaintModifier var1);

    public long getW();

    public void setW(long var1);

    public long getH();

    public void setH(long var1);

    public boolean isExtrusionOk();

    public void setExtrusionOk(boolean var1);
}


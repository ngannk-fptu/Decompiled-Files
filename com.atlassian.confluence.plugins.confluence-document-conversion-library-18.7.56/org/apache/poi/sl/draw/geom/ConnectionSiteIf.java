/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.sl.draw.geom;

import org.apache.poi.sl.draw.geom.AdjustPointIf;

public interface ConnectionSiteIf {
    public AdjustPointIf getPos();

    public void setPos(AdjustPointIf var1);

    public String getAng();

    public void setAng(String var1);

    public boolean isSetAng();
}


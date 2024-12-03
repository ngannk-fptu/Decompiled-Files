/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xslf.draw.geom;

import org.apache.poi.sl.draw.geom.AdjustPointIf;
import org.openxmlformats.schemas.drawingml.x2006.main.CTAdjPoint2D;

public class XSLFAdjustPoint
implements AdjustPointIf {
    private final CTAdjPoint2D pnt;

    public XSLFAdjustPoint(CTAdjPoint2D pnt) {
        this.pnt = pnt;
    }

    @Override
    public String getX() {
        return this.pnt.xgetX().getStringValue();
    }

    @Override
    public void setX(String value) {
        this.pnt.setX(value);
    }

    @Override
    public boolean isSetX() {
        return this.pnt.xgetX() != null;
    }

    @Override
    public String getY() {
        return this.pnt.xgetY().getStringValue();
    }

    @Override
    public void setY(String value) {
        this.pnt.setY(value);
    }

    @Override
    public boolean isSetY() {
        return this.pnt.xgetY() != null;
    }
}


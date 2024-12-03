/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xslf.draw.geom;

import org.apache.poi.sl.draw.geom.AdjustPointIf;
import org.apache.poi.sl.draw.geom.LineToCommandIf;
import org.apache.poi.xslf.draw.geom.XSLFAdjustPoint;
import org.openxmlformats.schemas.drawingml.x2006.main.CTAdjPoint2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPath2DLineTo;

public class XSLFLineTo
implements LineToCommandIf {
    private final CTPath2DLineTo lineTo;

    public XSLFLineTo(CTPath2DLineTo lineTo) {
        this.lineTo = lineTo;
    }

    @Override
    public AdjustPointIf getPt() {
        return new XSLFAdjustPoint(this.lineTo.getPt());
    }

    @Override
    public void setPt(AdjustPointIf pt) {
        CTAdjPoint2D xpt = this.lineTo.getPt();
        if (xpt == null) {
            xpt = this.lineTo.addNewPt();
        }
        xpt.setX(pt.getX());
        xpt.setY(pt.getY());
    }
}


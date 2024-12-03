/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xslf.draw.geom;

import org.apache.poi.sl.draw.geom.AdjustPointIf;
import org.apache.poi.sl.draw.geom.MoveToCommandIf;
import org.apache.poi.xslf.draw.geom.XSLFAdjustPoint;
import org.openxmlformats.schemas.drawingml.x2006.main.CTAdjPoint2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPath2DMoveTo;

public class XSLFMoveTo
implements MoveToCommandIf {
    private final CTPath2DMoveTo moveTo;

    public XSLFMoveTo(CTPath2DMoveTo moveTo) {
        this.moveTo = moveTo;
    }

    @Override
    public XSLFAdjustPoint getPt() {
        return new XSLFAdjustPoint(this.moveTo.getPt());
    }

    @Override
    public void setPt(AdjustPointIf pt) {
        CTAdjPoint2D xpt = this.moveTo.getPt();
        if (xpt == null) {
            xpt = this.moveTo.addNewPt();
        }
        xpt.setX(pt.getX());
        xpt.setY(pt.getY());
    }
}


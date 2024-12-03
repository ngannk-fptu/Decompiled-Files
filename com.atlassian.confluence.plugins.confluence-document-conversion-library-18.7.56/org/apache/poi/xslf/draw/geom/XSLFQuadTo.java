/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xslf.draw.geom;

import org.apache.poi.sl.draw.geom.AdjustPointIf;
import org.apache.poi.sl.draw.geom.QuadToCommandIf;
import org.apache.poi.xslf.draw.geom.XSLFAdjustPoint;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPath2DQuadBezierTo;

public class XSLFQuadTo
implements QuadToCommandIf {
    private final CTPath2DQuadBezierTo bezier;

    public XSLFQuadTo(CTPath2DQuadBezierTo bezier) {
        this.bezier = bezier;
    }

    @Override
    public AdjustPointIf getPt1() {
        return new XSLFAdjustPoint(this.bezier.getPtArray(0));
    }

    @Override
    public void setPt1(AdjustPointIf pt1) {
    }

    @Override
    public AdjustPointIf getPt2() {
        return new XSLFAdjustPoint(this.bezier.getPtArray(1));
    }

    @Override
    public void setPt2(AdjustPointIf pt2) {
    }
}


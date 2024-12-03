/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xslf.draw.geom;

import org.apache.poi.sl.draw.geom.AdjustValueIf;
import org.apache.poi.xslf.draw.geom.XSLFGuide;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGeomGuide;

public class XSLFAdjustValue
extends XSLFGuide
implements AdjustValueIf {
    public XSLFAdjustValue(CTGeomGuide guide) {
        super(guide);
    }
}


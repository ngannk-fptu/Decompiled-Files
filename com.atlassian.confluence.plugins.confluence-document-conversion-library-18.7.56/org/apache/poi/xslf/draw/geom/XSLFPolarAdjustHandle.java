/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xslf.draw.geom;

import org.apache.poi.sl.draw.geom.AdjustHandle;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPolarAdjustHandle;

public class XSLFPolarAdjustHandle
implements AdjustHandle {
    final CTPolarAdjustHandle xobj;

    public XSLFPolarAdjustHandle(CTPolarAdjustHandle xobj) {
        this.xobj = xobj;
    }
}


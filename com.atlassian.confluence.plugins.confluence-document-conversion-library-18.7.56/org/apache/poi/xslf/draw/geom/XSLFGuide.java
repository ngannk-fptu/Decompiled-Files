/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xslf.draw.geom;

import org.apache.poi.sl.draw.geom.GuideIf;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGeomGuide;

public class XSLFGuide
implements GuideIf {
    final CTGeomGuide guide;

    public XSLFGuide(CTGeomGuide guide) {
        this.guide = guide;
    }

    @Override
    public String getName() {
        return this.guide.getName();
    }

    @Override
    public void setName(String name) {
        this.guide.setName(name);
    }

    @Override
    public String getFmla() {
        return this.guide.getFmla();
    }

    @Override
    public void setFmla(String fmla) {
        this.guide.setFmla(fmla);
    }
}


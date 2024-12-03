/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xslf.draw.geom;

import org.apache.poi.sl.draw.geom.ArcToCommandIf;
import org.openxmlformats.schemas.drawingml.x2006.main.CTPath2DArcTo;

public class XSLFArcTo
implements ArcToCommandIf {
    private final CTPath2DArcTo arc;

    public XSLFArcTo(CTPath2DArcTo arc) {
        this.arc = arc;
    }

    @Override
    public String getHR() {
        return this.arc.xgetHR().getStringValue();
    }

    @Override
    public void setHR(String hr) {
        this.arc.setHR(hr);
    }

    @Override
    public String getWR() {
        return this.arc.xgetHR().getStringValue();
    }

    @Override
    public void setWR(String wr) {
        this.arc.setWR(wr);
    }

    @Override
    public String getStAng() {
        return this.arc.xgetStAng().getStringValue();
    }

    @Override
    public void setStAng(String stAng) {
        this.arc.setStAng(stAng);
    }

    @Override
    public String getSwAng() {
        return this.arc.xgetSwAng().getStringValue();
    }

    @Override
    public void setSwAng(String swAng) {
        this.arc.setSwAng(swAng);
    }
}


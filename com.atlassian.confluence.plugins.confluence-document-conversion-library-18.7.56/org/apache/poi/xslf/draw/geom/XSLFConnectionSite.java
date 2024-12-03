/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xslf.draw.geom;

import org.apache.poi.sl.draw.geom.AdjustPointIf;
import org.apache.poi.sl.draw.geom.ConnectionSiteIf;
import org.apache.poi.xslf.draw.geom.XSLFAdjustPoint;
import org.openxmlformats.schemas.drawingml.x2006.main.CTAdjPoint2D;
import org.openxmlformats.schemas.drawingml.x2006.main.CTConnectionSite;

public class XSLFConnectionSite
implements ConnectionSiteIf {
    final CTConnectionSite cxn;

    public XSLFConnectionSite(CTConnectionSite cxn) {
        this.cxn = cxn;
    }

    @Override
    public AdjustPointIf getPos() {
        return new XSLFAdjustPoint(this.cxn.getPos());
    }

    @Override
    public void setPos(AdjustPointIf pos) {
        CTAdjPoint2D p = this.cxn.getPos();
        if (p == null) {
            p = this.cxn.addNewPos();
        }
        p.setX(pos.getX());
        p.setY(pos.getY());
    }

    @Override
    public String getAng() {
        return this.cxn.xgetAng().getStringValue();
    }

    @Override
    public void setAng(String value) {
        this.cxn.setAng(value);
    }

    @Override
    public boolean isSetAng() {
        return this.cxn.xgetAng() == null;
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xddf.usermodel.text;

import org.apache.poi.ooxml.util.POIXMLUnits;
import org.apache.poi.util.Internal;
import org.apache.poi.xddf.usermodel.text.XDDFBulletSize;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBulletSizePercent;

public class XDDFBulletSizePercent
implements XDDFBulletSize {
    private CTTextBulletSizePercent percent;
    private Double scale;

    public XDDFBulletSizePercent(double value) {
        this(CTTextBulletSizePercent.Factory.newInstance(), null);
        this.setPercent(value);
    }

    @Internal
    protected XDDFBulletSizePercent(CTTextBulletSizePercent percent, Double scale) {
        this.percent = percent;
        this.scale = scale == null ? 0.001 : scale * 0.001;
    }

    @Internal
    protected CTTextBulletSizePercent getXmlObject() {
        return this.percent;
    }

    public double getPercent() {
        return (double)POIXMLUnits.parsePercent(this.percent.xgetVal()) * this.scale;
    }

    public void setPercent(double value) {
        this.percent.setVal(Long.toString(Math.round(1000.0 * value)));
    }
}


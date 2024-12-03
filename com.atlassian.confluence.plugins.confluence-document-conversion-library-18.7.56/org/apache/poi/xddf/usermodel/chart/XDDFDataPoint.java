/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xddf.usermodel.chart;

import org.apache.poi.util.Internal;
import org.apache.poi.xddf.usermodel.XDDFFillProperties;
import org.apache.poi.xddf.usermodel.XDDFLineProperties;
import org.apache.poi.xddf.usermodel.XDDFShapeProperties;
import org.apache.poi.xddf.usermodel.chart.MarkerStyle;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDPt;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTMarker;

public class XDDFDataPoint {
    private final CTDPt point;

    @Internal
    protected XDDFDataPoint(CTDPt point) {
        this.point = point;
    }

    public long getIndex() {
        return this.point.getIdx().getVal();
    }

    public void setFillProperties(XDDFFillProperties fill) {
        XDDFShapeProperties properties = this.getShapeProperties();
        if (properties == null) {
            properties = new XDDFShapeProperties();
        }
        properties.setFillProperties(fill);
        this.setShapeProperties(properties);
    }

    public void setLineProperties(XDDFLineProperties line) {
        XDDFShapeProperties properties = this.getShapeProperties();
        if (properties == null) {
            properties = new XDDFShapeProperties();
        }
        properties.setLineProperties(line);
        this.setShapeProperties(properties);
    }

    public XDDFShapeProperties getShapeProperties() {
        if (this.point.isSetSpPr()) {
            return new XDDFShapeProperties(this.point.getSpPr());
        }
        return null;
    }

    public void setShapeProperties(XDDFShapeProperties properties) {
        if (properties == null) {
            if (this.point.isSetSpPr()) {
                this.point.unsetSpPr();
            }
        } else if (this.point.isSetSpPr()) {
            this.point.setSpPr(properties.getXmlObject());
        } else {
            this.point.addNewSpPr().set(properties.getXmlObject());
        }
    }

    public Long getExplosion() {
        if (this.point.isSetExplosion()) {
            return this.point.getExplosion().getVal();
        }
        return null;
    }

    public void setExplosion(Long explosion) {
        if (explosion == null) {
            if (this.point.isSetExplosion()) {
                this.point.unsetExplosion();
            }
        } else if (this.point.isSetExplosion()) {
            this.point.getExplosion().setVal(explosion);
        } else {
            this.point.addNewExplosion().setVal(explosion);
        }
    }

    public boolean getInvertIfNegative() {
        if (this.point.isSetInvertIfNegative()) {
            return this.point.getInvertIfNegative().getVal();
        }
        return false;
    }

    public void setInvertIfNegative(boolean invertIfNegative) {
        if (this.point.isSetInvertIfNegative()) {
            this.point.getInvertIfNegative().setVal(invertIfNegative);
        } else {
            this.point.addNewInvertIfNegative().setVal(invertIfNegative);
        }
    }

    public void setMarkerSize(short size) {
        if (size < 2 || 72 < size) {
            throw new IllegalArgumentException("Minimum inclusive: 2; Maximum inclusive: 72");
        }
        CTMarker marker = this.getMarker();
        if (marker.isSetSize()) {
            marker.getSize().setVal(size);
        } else {
            marker.addNewSize().setVal(size);
        }
    }

    public void setMarkerStyle(MarkerStyle style) {
        CTMarker marker = this.getMarker();
        if (marker.isSetSymbol()) {
            marker.getSymbol().setVal(style.underlying);
        } else {
            marker.addNewSymbol().setVal(style.underlying);
        }
    }

    private CTMarker getMarker() {
        if (this.point.isSetMarker()) {
            return this.point.getMarker();
        }
        return this.point.addNewMarker();
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xddf.usermodel;

import java.util.Locale;
import org.apache.poi.ooxml.util.POIXMLUnits;
import org.apache.poi.util.Internal;
import org.apache.poi.xddf.usermodel.XDDFColor;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.main.CTColor;
import org.openxmlformats.schemas.drawingml.x2006.main.CTScRgbColor;
import org.openxmlformats.schemas.drawingml.x2006.main.STPercentage;

public class XDDFColorRgbPercent
extends XDDFColor {
    private final CTScRgbColor color;

    public XDDFColorRgbPercent(int red, int green, int blue) {
        this(CTScRgbColor.Factory.newInstance(), CTColor.Factory.newInstance());
        this.setRed(red);
        this.setGreen(green);
        this.setBlue(blue);
    }

    @Internal
    protected XDDFColorRgbPercent(CTScRgbColor color) {
        this(color, null);
    }

    @Internal
    protected XDDFColorRgbPercent(CTScRgbColor color, CTColor container) {
        super(container);
        this.color = color;
    }

    @Override
    @Internal
    protected XmlObject getXmlObject() {
        return this.color;
    }

    public int getRed() {
        return POIXMLUnits.parsePercent(this.color.xgetR());
    }

    public void setRed(int red) {
        this.color.setR(this.normalize(red));
    }

    public int getGreen() {
        return POIXMLUnits.parsePercent(this.color.xgetG());
    }

    public void setGreen(int green) {
        this.color.setG(this.normalize(green));
    }

    public int getBlue() {
        return POIXMLUnits.parsePercent(this.color.xgetB());
    }

    public void setBlue(int blue) {
        this.color.setB(this.normalize(blue));
    }

    private int normalize(int value) {
        return value < 0 ? 0 : Math.min(100000, value);
    }

    public String toRGBHex() {
        int c = 0;
        for (STPercentage pct : new STPercentage[]{this.color.xgetR(), this.color.xgetG(), this.color.xgetB()}) {
            c = c << 8 | POIXMLUnits.parsePercent(pct) * 255 / 100000 & 0xFF;
        }
        return String.format(Locale.ROOT, "%06X", c);
    }
}


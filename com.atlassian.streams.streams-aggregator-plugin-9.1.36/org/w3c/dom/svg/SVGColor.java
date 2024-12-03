/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.svg;

import org.w3c.dom.css.CSSValue;
import org.w3c.dom.css.RGBColor;
import org.w3c.dom.svg.SVGException;
import org.w3c.dom.svg.SVGICCColor;

public interface SVGColor
extends CSSValue {
    public static final short SVG_COLORTYPE_UNKNOWN = 0;
    public static final short SVG_COLORTYPE_RGBCOLOR = 1;
    public static final short SVG_COLORTYPE_RGBCOLOR_ICCCOLOR = 2;
    public static final short SVG_COLORTYPE_CURRENTCOLOR = 3;

    public short getColorType();

    public RGBColor getRGBColor();

    public SVGICCColor getICCColor();

    public void setRGBColor(String var1) throws SVGException;

    public void setRGBColorICCColor(String var1, String var2) throws SVGException;

    public void setColor(short var1, String var2, String var3) throws SVGException;
}


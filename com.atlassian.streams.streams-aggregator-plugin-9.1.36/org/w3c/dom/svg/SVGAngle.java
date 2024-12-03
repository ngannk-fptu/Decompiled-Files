/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.svg;

import org.w3c.dom.DOMException;

public interface SVGAngle {
    public static final short SVG_ANGLETYPE_UNKNOWN = 0;
    public static final short SVG_ANGLETYPE_UNSPECIFIED = 1;
    public static final short SVG_ANGLETYPE_DEG = 2;
    public static final short SVG_ANGLETYPE_RAD = 3;
    public static final short SVG_ANGLETYPE_GRAD = 4;

    public short getUnitType();

    public float getValue();

    public void setValue(float var1) throws DOMException;

    public float getValueInSpecifiedUnits();

    public void setValueInSpecifiedUnits(float var1) throws DOMException;

    public String getValueAsString();

    public void setValueAsString(String var1) throws DOMException;

    public void newValueSpecifiedUnits(short var1, float var2);

    public void convertToSpecifiedUnits(short var1);
}


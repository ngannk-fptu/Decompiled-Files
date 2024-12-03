/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.dom.svg;

import org.w3c.dom.DOMException;

public interface SVGLength {
    public static final short SVG_LENGTHTYPE_UNKNOWN = 0;
    public static final short SVG_LENGTHTYPE_NUMBER = 1;
    public static final short SVG_LENGTHTYPE_PERCENTAGE = 2;
    public static final short SVG_LENGTHTYPE_EMS = 3;
    public static final short SVG_LENGTHTYPE_EXS = 4;
    public static final short SVG_LENGTHTYPE_PX = 5;
    public static final short SVG_LENGTHTYPE_CM = 6;
    public static final short SVG_LENGTHTYPE_MM = 7;
    public static final short SVG_LENGTHTYPE_IN = 8;
    public static final short SVG_LENGTHTYPE_PT = 9;
    public static final short SVG_LENGTHTYPE_PC = 10;

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


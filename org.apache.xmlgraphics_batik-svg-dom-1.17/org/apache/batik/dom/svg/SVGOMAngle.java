/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.parser.AngleHandler
 *  org.apache.batik.parser.AngleParser
 *  org.apache.batik.parser.DefaultAngleHandler
 *  org.apache.batik.parser.ParseException
 *  org.w3c.dom.svg.SVGAngle
 */
package org.apache.batik.dom.svg;

import org.apache.batik.parser.AngleHandler;
import org.apache.batik.parser.AngleParser;
import org.apache.batik.parser.DefaultAngleHandler;
import org.apache.batik.parser.ParseException;
import org.w3c.dom.DOMException;
import org.w3c.dom.svg.SVGAngle;

public class SVGOMAngle
implements SVGAngle {
    private short unitType;
    protected float value;
    protected static final String[] UNITS = new String[]{"", "", "deg", "rad", "grad"};
    protected static double[][] K = new double[][]{{1.0, Math.PI / 180, 0.015707963267948967}, {57.29577951308232, 1.0, 63.66197723675813}, {0.9, 0.015707963267948967, 1.0}};

    public short getUnitType() {
        this.revalidate();
        return this.unitType;
    }

    public float getValue() {
        this.revalidate();
        return SVGOMAngle.toUnit(this.getUnitType(), this.value, (short)2);
    }

    public void setValue(float value) throws DOMException {
        this.revalidate();
        this.setUnitType((short)2);
        this.value = value;
        this.reset();
    }

    public float getValueInSpecifiedUnits() {
        this.revalidate();
        return this.value;
    }

    public void setValueInSpecifiedUnits(float value) throws DOMException {
        this.revalidate();
        this.value = value;
        this.reset();
    }

    public String getValueAsString() {
        this.revalidate();
        return Float.toString(this.value) + UNITS[this.getUnitType()];
    }

    public void setValueAsString(String value) throws DOMException {
        this.parse(value);
        this.reset();
    }

    public void newValueSpecifiedUnits(short unit, float value) {
        this.setUnitType(unit);
        this.value = value;
        this.reset();
    }

    public void convertToSpecifiedUnits(short unit) {
        this.value = SVGOMAngle.toUnit(this.getUnitType(), this.value, unit);
        this.setUnitType(unit);
    }

    protected void reset() {
    }

    protected void revalidate() {
    }

    protected void parse(String s) {
        try {
            AngleParser angleParser = new AngleParser();
            angleParser.setAngleHandler((AngleHandler)new DefaultAngleHandler(){

                public void angleValue(float v) throws ParseException {
                    SVGOMAngle.this.value = v;
                }

                public void deg() throws ParseException {
                    SVGOMAngle.this.setUnitType((short)2);
                }

                public void rad() throws ParseException {
                    SVGOMAngle.this.setUnitType((short)3);
                }

                public void grad() throws ParseException {
                    SVGOMAngle.this.setUnitType((short)4);
                }
            });
            this.setUnitType((short)1);
            angleParser.parse(s);
        }
        catch (ParseException e) {
            this.setUnitType((short)0);
            this.value = 0.0f;
        }
    }

    public static float toUnit(short fromUnit, float value, short toUnit) {
        if (fromUnit == 1) {
            fromUnit = (short)2;
        }
        if (toUnit == 1) {
            toUnit = (short)2;
        }
        return (float)(K[fromUnit - 2][toUnit - 2] * (double)value);
    }

    public void setUnitType(short unitType) {
        this.unitType = unitType;
    }
}


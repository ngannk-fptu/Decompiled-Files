/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.batik.parser.LengthHandler
 *  org.apache.batik.parser.LengthParser
 *  org.apache.batik.parser.ParseException
 *  org.apache.batik.parser.UnitProcessor
 *  org.apache.batik.parser.UnitProcessor$Context
 *  org.apache.batik.parser.UnitProcessor$UnitResolver
 *  org.w3c.dom.svg.SVGLength
 */
package org.apache.batik.anim.dom;

import org.apache.batik.anim.dom.SVGOMElement;
import org.apache.batik.parser.LengthHandler;
import org.apache.batik.parser.LengthParser;
import org.apache.batik.parser.ParseException;
import org.apache.batik.parser.UnitProcessor;
import org.w3c.dom.DOMException;
import org.w3c.dom.Element;
import org.w3c.dom.svg.SVGLength;

public abstract class AbstractSVGLength
implements SVGLength {
    public static final short HORIZONTAL_LENGTH = 2;
    public static final short VERTICAL_LENGTH = 1;
    public static final short OTHER_LENGTH = 0;
    protected short unitType;
    protected float value;
    protected short direction;
    protected UnitProcessor.Context context = new DefaultContext();
    protected static final String[] UNITS = new String[]{"", "", "%", "em", "ex", "px", "cm", "mm", "in", "pt", "pc"};

    protected abstract SVGOMElement getAssociatedElement();

    public AbstractSVGLength(short direction) {
        this.direction = direction;
        this.value = 0.0f;
        this.unitType = 1;
    }

    public short getUnitType() {
        this.revalidate();
        return this.unitType;
    }

    public float getValue() {
        this.revalidate();
        try {
            return UnitProcessor.svgToUserSpace((float)this.value, (short)this.unitType, (short)this.direction, (UnitProcessor.Context)this.context);
        }
        catch (IllegalArgumentException ex) {
            return 0.0f;
        }
    }

    public void setValue(float value) throws DOMException {
        this.value = UnitProcessor.userSpaceToSVG((float)value, (short)this.unitType, (short)this.direction, (UnitProcessor.Context)this.context);
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
        if (this.unitType == 0) {
            return "";
        }
        return Float.toString(this.value) + UNITS[this.unitType];
    }

    public void setValueAsString(String value) throws DOMException {
        this.parse(value);
        this.reset();
    }

    public void newValueSpecifiedUnits(short unit, float value) {
        this.unitType = unit;
        this.value = value;
        this.reset();
    }

    public void convertToSpecifiedUnits(short unit) {
        float v = this.getValue();
        this.unitType = unit;
        this.setValue(v);
    }

    protected void reset() {
    }

    protected void revalidate() {
    }

    protected void parse(String s) {
        try {
            LengthParser lengthParser = new LengthParser();
            UnitProcessor.UnitResolver ur = new UnitProcessor.UnitResolver();
            lengthParser.setLengthHandler((LengthHandler)ur);
            lengthParser.parse(s);
            this.unitType = ur.unit;
            this.value = ur.value;
        }
        catch (ParseException e) {
            this.unitType = 0;
            this.value = 0.0f;
        }
    }

    protected class DefaultContext
    implements UnitProcessor.Context {
        protected DefaultContext() {
        }

        public Element getElement() {
            return AbstractSVGLength.this.getAssociatedElement();
        }

        public float getPixelUnitToMillimeter() {
            return AbstractSVGLength.this.getAssociatedElement().getSVGContext().getPixelUnitToMillimeter();
        }

        public float getPixelToMM() {
            return this.getPixelUnitToMillimeter();
        }

        public float getFontSize() {
            return AbstractSVGLength.this.getAssociatedElement().getSVGContext().getFontSize();
        }

        public float getXHeight() {
            return 0.5f;
        }

        public float getViewportWidth() {
            return AbstractSVGLength.this.getAssociatedElement().getSVGContext().getViewportWidth();
        }

        public float getViewportHeight() {
            return AbstractSVGLength.this.getAssociatedElement().getSVGContext().getViewportHeight();
        }
    }
}


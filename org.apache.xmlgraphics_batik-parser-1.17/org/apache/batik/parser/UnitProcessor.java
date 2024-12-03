/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.parser;

import org.apache.batik.parser.LengthHandler;
import org.apache.batik.parser.LengthParser;
import org.apache.batik.parser.ParseException;
import org.w3c.dom.Element;

public abstract class UnitProcessor {
    public static final short HORIZONTAL_LENGTH = 2;
    public static final short VERTICAL_LENGTH = 1;
    public static final short OTHER_LENGTH = 0;
    static final double SQRT2 = Math.sqrt(2.0);

    protected UnitProcessor() {
    }

    public static float svgToObjectBoundingBox(String s, String attr, short d, Context ctx) throws ParseException {
        LengthParser lengthParser = new LengthParser();
        UnitResolver ur = new UnitResolver();
        lengthParser.setLengthHandler(ur);
        lengthParser.parse(s);
        return UnitProcessor.svgToObjectBoundingBox(ur.value, ur.unit, d, ctx);
    }

    public static float svgToObjectBoundingBox(float value, short type, short d, Context ctx) {
        switch (type) {
            case 1: {
                return value;
            }
            case 2: {
                return value / 100.0f;
            }
            case 3: 
            case 4: 
            case 5: 
            case 6: 
            case 7: 
            case 8: 
            case 9: 
            case 10: {
                return UnitProcessor.svgToUserSpace(value, type, d, ctx);
            }
        }
        throw new IllegalArgumentException("Length has unknown type");
    }

    public static float svgToUserSpace(String s, String attr, short d, Context ctx) throws ParseException {
        LengthParser lengthParser = new LengthParser();
        UnitResolver ur = new UnitResolver();
        lengthParser.setLengthHandler(ur);
        lengthParser.parse(s);
        return UnitProcessor.svgToUserSpace(ur.value, ur.unit, d, ctx);
    }

    public static float svgToUserSpace(float v, short type, short d, Context ctx) {
        switch (type) {
            case 1: 
            case 5: {
                return v;
            }
            case 7: {
                return v / ctx.getPixelUnitToMillimeter();
            }
            case 6: {
                return v * 10.0f / ctx.getPixelUnitToMillimeter();
            }
            case 8: {
                return v * 25.4f / ctx.getPixelUnitToMillimeter();
            }
            case 9: {
                return v * 25.4f / (72.0f * ctx.getPixelUnitToMillimeter());
            }
            case 10: {
                return v * 25.4f / (6.0f * ctx.getPixelUnitToMillimeter());
            }
            case 3: {
                return UnitProcessor.emsToPixels(v, d, ctx);
            }
            case 4: {
                return UnitProcessor.exsToPixels(v, d, ctx);
            }
            case 2: {
                return UnitProcessor.percentagesToPixels(v, d, ctx);
            }
        }
        throw new IllegalArgumentException("Length has unknown type");
    }

    public static float userSpaceToSVG(float v, short type, short d, Context ctx) {
        switch (type) {
            case 1: 
            case 5: {
                return v;
            }
            case 7: {
                return v * ctx.getPixelUnitToMillimeter();
            }
            case 6: {
                return v * ctx.getPixelUnitToMillimeter() / 10.0f;
            }
            case 8: {
                return v * ctx.getPixelUnitToMillimeter() / 25.4f;
            }
            case 9: {
                return v * (72.0f * ctx.getPixelUnitToMillimeter()) / 25.4f;
            }
            case 10: {
                return v * (6.0f * ctx.getPixelUnitToMillimeter()) / 25.4f;
            }
            case 3: {
                return UnitProcessor.pixelsToEms(v, d, ctx);
            }
            case 4: {
                return UnitProcessor.pixelsToExs(v, d, ctx);
            }
            case 2: {
                return UnitProcessor.pixelsToPercentages(v, d, ctx);
            }
        }
        throw new IllegalArgumentException("Length has unknown type");
    }

    protected static float percentagesToPixels(float v, short d, Context ctx) {
        if (d == 2) {
            float w = ctx.getViewportWidth();
            return w * v / 100.0f;
        }
        if (d == 1) {
            float h = ctx.getViewportHeight();
            return h * v / 100.0f;
        }
        double w = ctx.getViewportWidth();
        double h = ctx.getViewportHeight();
        double vpp = Math.sqrt(w * w + h * h) / SQRT2;
        return (float)(vpp * (double)v / 100.0);
    }

    protected static float pixelsToPercentages(float v, short d, Context ctx) {
        if (d == 2) {
            float w = ctx.getViewportWidth();
            return v * 100.0f / w;
        }
        if (d == 1) {
            float h = ctx.getViewportHeight();
            return v * 100.0f / h;
        }
        double w = ctx.getViewportWidth();
        double h = ctx.getViewportHeight();
        double vpp = Math.sqrt(w * w + h * h) / SQRT2;
        return (float)((double)v * 100.0 / vpp);
    }

    protected static float pixelsToEms(float v, short d, Context ctx) {
        return v / ctx.getFontSize();
    }

    protected static float emsToPixels(float v, short d, Context ctx) {
        return v * ctx.getFontSize();
    }

    protected static float pixelsToExs(float v, short d, Context ctx) {
        float xh = ctx.getXHeight();
        return v / xh / ctx.getFontSize();
    }

    protected static float exsToPixels(float v, short d, Context ctx) {
        float xh = ctx.getXHeight();
        return v * xh * ctx.getFontSize();
    }

    public static interface Context {
        public Element getElement();

        public float getPixelUnitToMillimeter();

        public float getPixelToMM();

        public float getFontSize();

        public float getXHeight();

        public float getViewportWidth();

        public float getViewportHeight();
    }

    public static class UnitResolver
    implements LengthHandler {
        public float value;
        public short unit = 1;

        @Override
        public void startLength() throws ParseException {
        }

        @Override
        public void lengthValue(float v) throws ParseException {
            this.value = v;
        }

        @Override
        public void em() throws ParseException {
            this.unit = (short)3;
        }

        @Override
        public void ex() throws ParseException {
            this.unit = (short)4;
        }

        @Override
        public void in() throws ParseException {
            this.unit = (short)8;
        }

        @Override
        public void cm() throws ParseException {
            this.unit = (short)6;
        }

        @Override
        public void mm() throws ParseException {
            this.unit = (short)7;
        }

        @Override
        public void pc() throws ParseException {
            this.unit = (short)10;
        }

        @Override
        public void pt() throws ParseException {
            this.unit = (short)9;
        }

        @Override
        public void px() throws ParseException {
            this.unit = (short)5;
        }

        @Override
        public void percentage() throws ParseException {
            this.unit = (short)2;
        }

        @Override
        public void endLength() throws ParseException {
        }
    }
}


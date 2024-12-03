/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.w3c.css.sac.LexicalUnit
 */
package org.apache.batik.css.parser;

import org.w3c.css.sac.LexicalUnit;

public abstract class CSSLexicalUnit
implements LexicalUnit {
    public static final String UNIT_TEXT_CENTIMETER = "cm";
    public static final String UNIT_TEXT_DEGREE = "deg";
    public static final String UNIT_TEXT_EM = "em";
    public static final String UNIT_TEXT_EX = "ex";
    public static final String UNIT_TEXT_GRADIAN = "grad";
    public static final String UNIT_TEXT_HERTZ = "Hz";
    public static final String UNIT_TEXT_INCH = "in";
    public static final String UNIT_TEXT_KILOHERTZ = "kHz";
    public static final String UNIT_TEXT_MILLIMETER = "mm";
    public static final String UNIT_TEXT_MILLISECOND = "ms";
    public static final String UNIT_TEXT_PERCENTAGE = "%";
    public static final String UNIT_TEXT_PICA = "pc";
    public static final String UNIT_TEXT_PIXEL = "px";
    public static final String UNIT_TEXT_POINT = "pt";
    public static final String UNIT_TEXT_RADIAN = "rad";
    public static final String UNIT_TEXT_REAL = "";
    public static final String UNIT_TEXT_SECOND = "s";
    public static final String TEXT_RGBCOLOR = "rgb";
    public static final String TEXT_RECT_FUNCTION = "rect";
    public static final String TEXT_COUNTER_FUNCTION = "counter";
    public static final String TEXT_COUNTERS_FUNCTION = "counters";
    protected short lexicalUnitType;
    protected LexicalUnit nextLexicalUnit;
    protected LexicalUnit previousLexicalUnit;

    protected CSSLexicalUnit(short t, LexicalUnit prev) {
        this.lexicalUnitType = t;
        this.previousLexicalUnit = prev;
        if (prev != null) {
            ((CSSLexicalUnit)prev).nextLexicalUnit = this;
        }
    }

    public short getLexicalUnitType() {
        return this.lexicalUnitType;
    }

    public LexicalUnit getNextLexicalUnit() {
        return this.nextLexicalUnit;
    }

    public void setNextLexicalUnit(LexicalUnit lu) {
        this.nextLexicalUnit = lu;
    }

    public LexicalUnit getPreviousLexicalUnit() {
        return this.previousLexicalUnit;
    }

    public void setPreviousLexicalUnit(LexicalUnit lu) {
        this.previousLexicalUnit = lu;
    }

    public int getIntegerValue() {
        throw new IllegalStateException();
    }

    public float getFloatValue() {
        throw new IllegalStateException();
    }

    public String getDimensionUnitText() {
        switch (this.lexicalUnitType) {
            case 19: {
                return UNIT_TEXT_CENTIMETER;
            }
            case 28: {
                return UNIT_TEXT_DEGREE;
            }
            case 15: {
                return UNIT_TEXT_EM;
            }
            case 16: {
                return UNIT_TEXT_EX;
            }
            case 29: {
                return UNIT_TEXT_GRADIAN;
            }
            case 33: {
                return UNIT_TEXT_HERTZ;
            }
            case 18: {
                return UNIT_TEXT_INCH;
            }
            case 34: {
                return UNIT_TEXT_KILOHERTZ;
            }
            case 20: {
                return UNIT_TEXT_MILLIMETER;
            }
            case 31: {
                return UNIT_TEXT_MILLISECOND;
            }
            case 23: {
                return UNIT_TEXT_PERCENTAGE;
            }
            case 22: {
                return UNIT_TEXT_PICA;
            }
            case 17: {
                return UNIT_TEXT_PIXEL;
            }
            case 21: {
                return UNIT_TEXT_POINT;
            }
            case 30: {
                return UNIT_TEXT_RADIAN;
            }
            case 14: {
                return UNIT_TEXT_REAL;
            }
            case 32: {
                return UNIT_TEXT_SECOND;
            }
        }
        throw new IllegalStateException("No Unit Text for type: " + this.lexicalUnitType);
    }

    public String getFunctionName() {
        throw new IllegalStateException();
    }

    public LexicalUnit getParameters() {
        throw new IllegalStateException();
    }

    public String getStringValue() {
        throw new IllegalStateException();
    }

    public LexicalUnit getSubValues() {
        throw new IllegalStateException();
    }

    public static CSSLexicalUnit createSimple(short t, LexicalUnit prev) {
        return new SimpleLexicalUnit(t, prev);
    }

    public static CSSLexicalUnit createInteger(int val, LexicalUnit prev) {
        return new IntegerLexicalUnit(val, prev);
    }

    public static CSSLexicalUnit createFloat(short t, float val, LexicalUnit prev) {
        return new FloatLexicalUnit(t, val, prev);
    }

    public static CSSLexicalUnit createDimension(float val, String dim, LexicalUnit prev) {
        return new DimensionLexicalUnit(val, dim, prev);
    }

    public static CSSLexicalUnit createFunction(String f, LexicalUnit params, LexicalUnit prev) {
        return new FunctionLexicalUnit(f, params, prev);
    }

    public static CSSLexicalUnit createPredefinedFunction(short t, LexicalUnit params, LexicalUnit prev) {
        return new PredefinedFunctionLexicalUnit(t, params, prev);
    }

    public static CSSLexicalUnit createString(short t, String val, LexicalUnit prev) {
        return new StringLexicalUnit(t, val, prev);
    }

    protected static class StringLexicalUnit
    extends CSSLexicalUnit {
        protected String value;

        public StringLexicalUnit(short t, String val, LexicalUnit prev) {
            super(t, prev);
            this.value = val;
        }

        @Override
        public String getStringValue() {
            return this.value;
        }
    }

    protected static class PredefinedFunctionLexicalUnit
    extends CSSLexicalUnit {
        protected LexicalUnit parameters;

        public PredefinedFunctionLexicalUnit(short t, LexicalUnit params, LexicalUnit prev) {
            super(t, prev);
            this.parameters = params;
        }

        @Override
        public String getFunctionName() {
            switch (this.lexicalUnitType) {
                case 27: {
                    return CSSLexicalUnit.TEXT_RGBCOLOR;
                }
                case 38: {
                    return CSSLexicalUnit.TEXT_RECT_FUNCTION;
                }
                case 25: {
                    return CSSLexicalUnit.TEXT_COUNTER_FUNCTION;
                }
                case 26: {
                    return CSSLexicalUnit.TEXT_COUNTERS_FUNCTION;
                }
            }
            return super.getFunctionName();
        }

        @Override
        public LexicalUnit getParameters() {
            return this.parameters;
        }
    }

    protected static class FunctionLexicalUnit
    extends CSSLexicalUnit {
        protected String name;
        protected LexicalUnit parameters;

        public FunctionLexicalUnit(String f, LexicalUnit params, LexicalUnit prev) {
            super((short)41, prev);
            this.name = f;
            this.parameters = params;
        }

        @Override
        public String getFunctionName() {
            return this.name;
        }

        @Override
        public LexicalUnit getParameters() {
            return this.parameters;
        }
    }

    protected static class DimensionLexicalUnit
    extends CSSLexicalUnit {
        protected float value;
        protected String dimension;

        public DimensionLexicalUnit(float val, String dim, LexicalUnit prev) {
            super((short)42, prev);
            this.value = val;
            this.dimension = dim;
        }

        @Override
        public float getFloatValue() {
            return this.value;
        }

        @Override
        public String getDimensionUnitText() {
            return this.dimension;
        }
    }

    protected static class FloatLexicalUnit
    extends CSSLexicalUnit {
        protected float value;

        public FloatLexicalUnit(short t, float val, LexicalUnit prev) {
            super(t, prev);
            this.value = val;
        }

        @Override
        public float getFloatValue() {
            return this.value;
        }
    }

    protected static class IntegerLexicalUnit
    extends CSSLexicalUnit {
        protected int value;

        public IntegerLexicalUnit(int val, LexicalUnit prev) {
            super((short)13, prev);
            this.value = val;
        }

        @Override
        public int getIntegerValue() {
            return this.value;
        }
    }

    protected static class SimpleLexicalUnit
    extends CSSLexicalUnit {
        public SimpleLexicalUnit(short t, LexicalUnit prev) {
            super(t, prev);
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.css.style;

public class Length {
    public static final int MAX_WIDTH = 0x3FFFFFFF;
    public static final int VARIABLE = 1;
    public static final int FIXED = 2;
    public static final int PERCENT = 3;
    private int _type = 1;
    private long _value = 0L;

    public Length() {
    }

    public Length(long value, int type) {
        this._value = value;
        this._type = type;
    }

    public void setValue(long value) {
        this._value = value;
    }

    public long value() {
        return this._value;
    }

    public void setType(int type) {
        this._type = type;
    }

    public int type() {
        return this._type;
    }

    public boolean isVariable() {
        return this._type == 1;
    }

    public boolean isFixed() {
        return this._type == 2;
    }

    public boolean isPercent() {
        return this._type == 3;
    }

    public long width(int maxWidth) {
        switch (this._type) {
            case 2: {
                return this._value;
            }
            case 3: {
                return (long)maxWidth * this._value / 100L;
            }
            case 1: {
                return maxWidth;
            }
        }
        return -1L;
    }

    public long minWidth(int maxWidth) {
        switch (this._type) {
            case 2: {
                return this._value;
            }
            case 3: {
                return (long)maxWidth * this._value / 100L;
            }
        }
        return 0L;
    }

    public String toString() {
        StringBuffer result = new StringBuffer();
        result.append("(type=");
        switch (this._type) {
            case 2: {
                result.append("fixed");
                break;
            }
            case 3: {
                result.append("percent");
                break;
            }
            case 1: {
                result.append("variable");
                break;
            }
            default: {
                result.append("unknown");
            }
        }
        result.append(", value=");
        result.append(this._value);
        result.append(")");
        return result.toString();
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.dv.xs;

import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.ValidationContext;
import org.apache.xerces.impl.dv.xs.DoubleDV;
import org.apache.xerces.impl.dv.xs.TypeValidator;
import org.apache.xerces.xs.datatypes.XSFloat;

public class FloatDV
extends TypeValidator {
    @Override
    public short getAllowedFacets() {
        return 2552;
    }

    @Override
    public Object getActualValue(String string, ValidationContext validationContext) throws InvalidDatatypeValueException {
        try {
            return new XFloat(string);
        }
        catch (NumberFormatException numberFormatException) {
            throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[]{string, "float"});
        }
    }

    @Override
    public int compare(Object object, Object object2) {
        return ((XFloat)object).compareTo((XFloat)object2);
    }

    @Override
    public boolean isIdentical(Object object, Object object2) {
        if (object2 instanceof XFloat) {
            return ((XFloat)object).isIdentical((XFloat)object2);
        }
        return false;
    }

    private static final class XFloat
    implements XSFloat {
        private final float value;
        private String canonical;

        public XFloat(String string) throws NumberFormatException {
            if (DoubleDV.isPossibleFP(string)) {
                this.value = Float.parseFloat(string);
            } else if (string.equals("INF")) {
                this.value = Float.POSITIVE_INFINITY;
            } else if (string.equals("-INF")) {
                this.value = Float.NEGATIVE_INFINITY;
            } else if (string.equals("NaN")) {
                this.value = Float.NaN;
            } else {
                throw new NumberFormatException(string);
            }
        }

        public boolean equals(Object object) {
            if (object == this) {
                return true;
            }
            if (!(object instanceof XFloat)) {
                return false;
            }
            XFloat xFloat = (XFloat)object;
            if (this.value == xFloat.value) {
                return true;
            }
            return this.value != this.value && xFloat.value != xFloat.value;
        }

        public int hashCode() {
            return this.value == 0.0f ? 0 : Float.floatToIntBits(this.value);
        }

        public boolean isIdentical(XFloat xFloat) {
            if (xFloat == this) {
                return true;
            }
            if (this.value == xFloat.value) {
                return this.value != 0.0f || Float.floatToIntBits(this.value) == Float.floatToIntBits(xFloat.value);
            }
            return this.value != this.value && xFloat.value != xFloat.value;
        }

        private int compareTo(XFloat xFloat) {
            float f = xFloat.value;
            if (this.value < f) {
                return -1;
            }
            if (this.value > f) {
                return 1;
            }
            if (this.value == f) {
                return 0;
            }
            if (this.value != this.value) {
                if (f != f) {
                    return 0;
                }
                return 2;
            }
            return 2;
        }

        public synchronized String toString() {
            if (this.canonical == null) {
                if (this.value == Float.POSITIVE_INFINITY) {
                    this.canonical = "INF";
                } else if (this.value == Float.NEGATIVE_INFINITY) {
                    this.canonical = "-INF";
                } else if (this.value != this.value) {
                    this.canonical = "NaN";
                } else if (this.value == 0.0f) {
                    this.canonical = "0.0E1";
                } else {
                    this.canonical = Float.toString(this.value);
                    if (this.canonical.indexOf(69) == -1) {
                        int n;
                        int n2 = this.canonical.length();
                        char[] cArray = new char[n2 + 3];
                        this.canonical.getChars(0, n2, cArray, 0);
                        int n3 = n = cArray[0] == '-' ? 2 : 1;
                        if (this.value >= 1.0f || this.value <= -1.0f) {
                            int n4;
                            int n5;
                            for (n5 = n4 = this.canonical.indexOf(46); n5 > n; --n5) {
                                cArray[n5] = cArray[n5 - 1];
                            }
                            cArray[n] = 46;
                            while (cArray[n2 - 1] == '0') {
                                --n2;
                            }
                            if (cArray[n2 - 1] == '.') {
                                // empty if block
                            }
                            int n6 = ++n2;
                            cArray[n6] = 69;
                            n5 = n4 - n;
                            int n7 = ++n2;
                            ++n2;
                            cArray[n7] = (char)(n5 + 48);
                        } else {
                            int n8 = n + 1;
                            while (cArray[n8] == '0') {
                                ++n8;
                            }
                            cArray[n - 1] = cArray[n8];
                            cArray[n] = 46;
                            int n9 = n8 + 1;
                            int n10 = n + 1;
                            while (n9 < n2) {
                                cArray[n10] = cArray[n9];
                                ++n9;
                                ++n10;
                            }
                            if ((n2 -= n8 - n) == n + 1) {
                                cArray[n2++] = 48;
                            }
                            cArray[n2++] = 69;
                            cArray[n2++] = 45;
                            n9 = n8 - n;
                            cArray[n2++] = (char)(n9 + 48);
                        }
                        this.canonical = new String(cArray, 0, n2);
                    }
                }
            }
            return this.canonical;
        }

        @Override
        public float getValue() {
            return this.value;
        }
    }
}


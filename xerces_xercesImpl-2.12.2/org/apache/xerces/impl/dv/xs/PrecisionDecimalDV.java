/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.dv.xs;

import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.ValidationContext;
import org.apache.xerces.impl.dv.xs.TypeValidator;

class PrecisionDecimalDV
extends TypeValidator {
    PrecisionDecimalDV() {
    }

    @Override
    public short getAllowedFacets() {
        return 4088;
    }

    @Override
    public Object getActualValue(String string, ValidationContext validationContext) throws InvalidDatatypeValueException {
        try {
            return new XPrecisionDecimal(string);
        }
        catch (NumberFormatException numberFormatException) {
            throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[]{string, "precisionDecimal"});
        }
    }

    @Override
    public int compare(Object object, Object object2) {
        return ((XPrecisionDecimal)object).compareTo((XPrecisionDecimal)object2);
    }

    @Override
    public int getFractionDigits(Object object) {
        return ((XPrecisionDecimal)object).fracDigits;
    }

    @Override
    public int getTotalDigits(Object object) {
        return ((XPrecisionDecimal)object).totalDigits;
    }

    @Override
    public boolean isIdentical(Object object, Object object2) {
        if (!(object2 instanceof XPrecisionDecimal) || !(object instanceof XPrecisionDecimal)) {
            return false;
        }
        return ((XPrecisionDecimal)object).isIdentical((XPrecisionDecimal)object2);
    }

    static class XPrecisionDecimal {
        int sign = 1;
        int totalDigits = 0;
        int intDigits = 0;
        int fracDigits = 0;
        String ivalue = "";
        String fvalue = "";
        int pvalue = 0;
        private String canonical;

        XPrecisionDecimal(String string) throws NumberFormatException {
            if (string.equals("NaN")) {
                this.ivalue = string;
                this.sign = 0;
            }
            if (string.equals("+INF") || string.equals("INF") || string.equals("-INF")) {
                this.ivalue = string.charAt(0) == '+' ? string.substring(1) : string;
                return;
            }
            this.initD(string);
        }

        void initD(String string) throws NumberFormatException {
            int n;
            int n2 = string.length();
            if (n2 == 0) {
                throw new NumberFormatException();
            }
            int n3 = 0;
            int n4 = 0;
            int n5 = 0;
            int n6 = 0;
            if (string.charAt(0) == '+') {
                n3 = 1;
            } else if (string.charAt(0) == '-') {
                n3 = 1;
                this.sign = -1;
            }
            for (n = n3; n < n2 && string.charAt(n) == '0'; ++n) {
            }
            for (n4 = n; n4 < n2 && TypeValidator.isDigit(string.charAt(n4)); ++n4) {
            }
            if (n4 < n2) {
                if (string.charAt(n4) != '.' && string.charAt(n4) != 'E' && string.charAt(n4) != 'e') {
                    throw new NumberFormatException();
                }
                if (string.charAt(n4) == '.') {
                    for (n6 = n5 = n4 + 1; n6 < n2 && TypeValidator.isDigit(string.charAt(n6)); ++n6) {
                    }
                } else {
                    this.pvalue = Integer.parseInt(string.substring(n4 + 1, n2));
                }
            }
            if (n3 == n4 && n5 == n6) {
                throw new NumberFormatException();
            }
            for (int i = n5; i < n6; ++i) {
                if (TypeValidator.isDigit(string.charAt(i))) continue;
                throw new NumberFormatException();
            }
            this.intDigits = n4 - n;
            this.fracDigits = n6 - n5;
            if (this.intDigits > 0) {
                this.ivalue = string.substring(n, n4);
            }
            if (this.fracDigits > 0) {
                this.fvalue = string.substring(n5, n6);
                if (n6 < n2) {
                    this.pvalue = Integer.parseInt(string.substring(n6 + 1, n2));
                }
            }
            this.totalDigits = this.intDigits + this.fracDigits;
        }

        public boolean equals(Object object) {
            if (object == this) {
                return true;
            }
            if (!(object instanceof XPrecisionDecimal)) {
                return false;
            }
            XPrecisionDecimal xPrecisionDecimal = (XPrecisionDecimal)object;
            return this.compareTo(xPrecisionDecimal) == 0;
        }

        private int compareFractionalPart(XPrecisionDecimal xPrecisionDecimal) {
            if (this.fvalue.equals(xPrecisionDecimal.fvalue)) {
                return 0;
            }
            StringBuffer stringBuffer = new StringBuffer(this.fvalue);
            StringBuffer stringBuffer2 = new StringBuffer(xPrecisionDecimal.fvalue);
            this.truncateTrailingZeros(stringBuffer, stringBuffer2);
            return stringBuffer.toString().compareTo(stringBuffer2.toString());
        }

        private void truncateTrailingZeros(StringBuffer stringBuffer, StringBuffer stringBuffer2) {
            int n;
            for (n = stringBuffer.length() - 1; n >= 0 && stringBuffer.charAt(n) == '0'; --n) {
                stringBuffer.deleteCharAt(n);
            }
            for (n = stringBuffer2.length() - 1; n >= 0 && stringBuffer2.charAt(n) == '0'; --n) {
                stringBuffer2.deleteCharAt(n);
            }
        }

        public int compareTo(XPrecisionDecimal xPrecisionDecimal) {
            if (this.sign == 0) {
                return 2;
            }
            if (this.ivalue.equals("INF") || xPrecisionDecimal.ivalue.equals("INF")) {
                if (this.ivalue.equals(xPrecisionDecimal.ivalue)) {
                    return 0;
                }
                if (this.ivalue.equals("INF")) {
                    return 1;
                }
                return -1;
            }
            if (this.ivalue.equals("-INF") || xPrecisionDecimal.ivalue.equals("-INF")) {
                if (this.ivalue.equals(xPrecisionDecimal.ivalue)) {
                    return 0;
                }
                if (this.ivalue.equals("-INF")) {
                    return -1;
                }
                return 1;
            }
            if (this.sign != xPrecisionDecimal.sign) {
                return this.sign > xPrecisionDecimal.sign ? 1 : -1;
            }
            return this.sign * this.compare(xPrecisionDecimal);
        }

        private int compare(XPrecisionDecimal xPrecisionDecimal) {
            if (this.pvalue != 0 || xPrecisionDecimal.pvalue != 0) {
                if (this.pvalue == xPrecisionDecimal.pvalue) {
                    return this.intComp(xPrecisionDecimal);
                }
                if (this.intDigits + this.pvalue != xPrecisionDecimal.intDigits + xPrecisionDecimal.pvalue) {
                    return this.intDigits + this.pvalue > xPrecisionDecimal.intDigits + xPrecisionDecimal.pvalue ? 1 : -1;
                }
                if (this.pvalue > xPrecisionDecimal.pvalue) {
                    int n = this.pvalue - xPrecisionDecimal.pvalue;
                    StringBuffer stringBuffer = new StringBuffer(this.ivalue);
                    StringBuffer stringBuffer2 = new StringBuffer(this.fvalue);
                    for (int i = 0; i < n; ++i) {
                        if (i < this.fracDigits) {
                            stringBuffer.append(this.fvalue.charAt(i));
                            stringBuffer2.deleteCharAt(i);
                            continue;
                        }
                        stringBuffer.append('0');
                    }
                    return this.compareDecimal(stringBuffer.toString(), xPrecisionDecimal.ivalue, stringBuffer2.toString(), xPrecisionDecimal.fvalue);
                }
                int n = xPrecisionDecimal.pvalue - this.pvalue;
                StringBuffer stringBuffer = new StringBuffer(xPrecisionDecimal.ivalue);
                StringBuffer stringBuffer3 = new StringBuffer(xPrecisionDecimal.fvalue);
                for (int i = 0; i < n; ++i) {
                    if (i < xPrecisionDecimal.fracDigits) {
                        stringBuffer.append(xPrecisionDecimal.fvalue.charAt(i));
                        stringBuffer3.deleteCharAt(i);
                        continue;
                    }
                    stringBuffer.append('0');
                }
                return this.compareDecimal(this.ivalue, stringBuffer.toString(), this.fvalue, stringBuffer3.toString());
            }
            return this.intComp(xPrecisionDecimal);
        }

        private int intComp(XPrecisionDecimal xPrecisionDecimal) {
            if (this.intDigits != xPrecisionDecimal.intDigits) {
                return this.intDigits > xPrecisionDecimal.intDigits ? 1 : -1;
            }
            return this.compareDecimal(this.ivalue, xPrecisionDecimal.ivalue, this.fvalue, xPrecisionDecimal.fvalue);
        }

        private int compareDecimal(String string, String string2, String string3, String string4) {
            int n = string.compareTo(string3);
            if (n != 0) {
                return n > 0 ? 1 : -1;
            }
            if (string2.equals(string4)) {
                return 0;
            }
            StringBuffer stringBuffer = new StringBuffer(string2);
            StringBuffer stringBuffer2 = new StringBuffer(string4);
            this.truncateTrailingZeros(stringBuffer, stringBuffer2);
            n = stringBuffer.toString().compareTo(stringBuffer2.toString());
            return n == 0 ? 0 : (n > 0 ? 1 : -1);
        }

        public synchronized String toString() {
            if (this.canonical == null) {
                this.makeCanonical();
            }
            return this.canonical;
        }

        private void makeCanonical() {
            this.canonical = "TBD by Working Group";
        }

        public boolean isIdentical(XPrecisionDecimal xPrecisionDecimal) {
            if (this.ivalue.equals(xPrecisionDecimal.ivalue) && (this.ivalue.equals("INF") || this.ivalue.equals("-INF") || this.ivalue.equals("NaN"))) {
                return true;
            }
            return this.sign == xPrecisionDecimal.sign && this.intDigits == xPrecisionDecimal.intDigits && this.fracDigits == xPrecisionDecimal.fracDigits && this.pvalue == xPrecisionDecimal.pvalue && this.ivalue.equals(xPrecisionDecimal.ivalue) && this.fvalue.equals(xPrecisionDecimal.fvalue);
        }
    }
}


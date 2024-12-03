/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.impl.dv.xs;

import java.math.BigDecimal;
import java.math.BigInteger;
import org.apache.xerces.impl.dv.InvalidDatatypeValueException;
import org.apache.xerces.impl.dv.ValidationContext;
import org.apache.xerces.impl.dv.xs.TypeValidator;
import org.apache.xerces.xs.datatypes.XSDecimal;

public class DecimalDV
extends TypeValidator {
    @Override
    public final short getAllowedFacets() {
        return 4088;
    }

    @Override
    public Object getActualValue(String string, ValidationContext validationContext) throws InvalidDatatypeValueException {
        try {
            return new XDecimal(string);
        }
        catch (NumberFormatException numberFormatException) {
            throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[]{string, "decimal"});
        }
    }

    @Override
    public final int compare(Object object, Object object2) {
        return ((XDecimal)object).compareTo((XDecimal)object2);
    }

    @Override
    public final int getTotalDigits(Object object) {
        return ((XDecimal)object).totalDigits;
    }

    @Override
    public final int getFractionDigits(Object object) {
        return ((XDecimal)object).fracDigits;
    }

    static class XDecimal
    implements XSDecimal {
        int sign = 1;
        int totalDigits = 0;
        int intDigits = 0;
        int fracDigits = 0;
        String ivalue = "";
        String fvalue = "";
        boolean integer = false;
        private String canonical;

        XDecimal(String string) throws NumberFormatException {
            this.initD(string);
        }

        XDecimal(String string, boolean bl) throws NumberFormatException {
            if (bl) {
                this.initI(string);
            } else {
                this.initD(string);
            }
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
                if (string.charAt(n4) != '.') {
                    throw new NumberFormatException();
                }
                n5 = n4 + 1;
                n6 = n2;
            }
            if (n3 == n4 && n5 == n6) {
                throw new NumberFormatException();
            }
            while (n6 > n5 && string.charAt(n6 - 1) == '0') {
                --n6;
            }
            for (int i = n5; i < n6; ++i) {
                if (TypeValidator.isDigit(string.charAt(i))) continue;
                throw new NumberFormatException();
            }
            this.intDigits = n4 - n;
            this.fracDigits = n6 - n5;
            this.totalDigits = this.intDigits + this.fracDigits;
            if (this.intDigits > 0) {
                this.ivalue = string.substring(n, n4);
                if (this.fracDigits > 0) {
                    this.fvalue = string.substring(n5, n6);
                }
            } else if (this.fracDigits > 0) {
                this.fvalue = string.substring(n5, n6);
            } else {
                this.sign = 0;
            }
        }

        void initI(String string) throws NumberFormatException {
            int n;
            int n2 = string.length();
            if (n2 == 0) {
                throw new NumberFormatException();
            }
            int n3 = 0;
            int n4 = 0;
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
                throw new NumberFormatException();
            }
            if (n3 == n4) {
                throw new NumberFormatException();
            }
            this.intDigits = n4 - n;
            this.fracDigits = 0;
            this.totalDigits = this.intDigits;
            if (this.intDigits > 0) {
                this.ivalue = string.substring(n, n4);
            } else {
                this.sign = 0;
            }
            this.integer = true;
        }

        public boolean equals(Object object) {
            if (object == this) {
                return true;
            }
            if (!(object instanceof XDecimal)) {
                return false;
            }
            XDecimal xDecimal = (XDecimal)object;
            if (this.sign != xDecimal.sign) {
                return false;
            }
            if (this.sign == 0) {
                return true;
            }
            return this.intDigits == xDecimal.intDigits && this.fracDigits == xDecimal.fracDigits && this.ivalue.equals(xDecimal.ivalue) && this.fvalue.equals(xDecimal.fvalue);
        }

        public int compareTo(XDecimal xDecimal) {
            if (this.sign != xDecimal.sign) {
                return this.sign > xDecimal.sign ? 1 : -1;
            }
            if (this.sign == 0) {
                return 0;
            }
            return this.sign * this.intComp(xDecimal);
        }

        private int intComp(XDecimal xDecimal) {
            if (this.intDigits != xDecimal.intDigits) {
                return this.intDigits > xDecimal.intDigits ? 1 : -1;
            }
            int n = this.ivalue.compareTo(xDecimal.ivalue);
            if (n != 0) {
                return n > 0 ? 1 : -1;
            }
            n = this.fvalue.compareTo(xDecimal.fvalue);
            return n == 0 ? 0 : (n > 0 ? 1 : -1);
        }

        public synchronized String toString() {
            if (this.canonical == null) {
                this.makeCanonical();
            }
            return this.canonical;
        }

        private void makeCanonical() {
            if (this.sign == 0) {
                this.canonical = this.integer ? "0" : "0.0";
                return;
            }
            if (this.integer && this.sign > 0) {
                this.canonical = this.ivalue;
                return;
            }
            StringBuffer stringBuffer = new StringBuffer(this.totalDigits + 3);
            if (this.sign == -1) {
                stringBuffer.append('-');
            }
            if (this.intDigits != 0) {
                stringBuffer.append(this.ivalue);
            } else {
                stringBuffer.append('0');
            }
            if (!this.integer) {
                stringBuffer.append('.');
                if (this.fracDigits != 0) {
                    stringBuffer.append(this.fvalue);
                } else {
                    stringBuffer.append('0');
                }
            }
            this.canonical = stringBuffer.toString();
        }

        @Override
        public BigDecimal getBigDecimal() {
            if (this.sign == 0) {
                return new BigDecimal(BigInteger.ZERO);
            }
            return new BigDecimal(this.toString());
        }

        @Override
        public BigInteger getBigInteger() throws NumberFormatException {
            if (this.fracDigits != 0) {
                throw new NumberFormatException();
            }
            if (this.sign == 0) {
                return BigInteger.ZERO;
            }
            if (this.sign == 1) {
                return new BigInteger(this.ivalue);
            }
            return new BigInteger("-" + this.ivalue);
        }

        @Override
        public long getLong() throws NumberFormatException {
            if (this.fracDigits != 0) {
                throw new NumberFormatException();
            }
            if (this.sign == 0) {
                return 0L;
            }
            if (this.sign == 1) {
                return Long.parseLong(this.ivalue);
            }
            return Long.parseLong("-" + this.ivalue);
        }

        @Override
        public int getInt() throws NumberFormatException {
            if (this.fracDigits != 0) {
                throw new NumberFormatException();
            }
            if (this.sign == 0) {
                return 0;
            }
            if (this.sign == 1) {
                return Integer.parseInt(this.ivalue);
            }
            return Integer.parseInt("-" + this.ivalue);
        }

        @Override
        public short getShort() throws NumberFormatException {
            if (this.fracDigits != 0) {
                throw new NumberFormatException();
            }
            if (this.sign == 0) {
                return 0;
            }
            if (this.sign == 1) {
                return Short.parseShort(this.ivalue);
            }
            return Short.parseShort("-" + this.ivalue);
        }

        @Override
        public byte getByte() throws NumberFormatException {
            if (this.fracDigits != 0) {
                throw new NumberFormatException();
            }
            if (this.sign == 0) {
                return 0;
            }
            if (this.sign == 1) {
                return Byte.parseByte(this.ivalue);
            }
            return Byte.parseByte("-" + this.ivalue);
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package com.ctc.wstx.shaded.msv_core.datatype.xsd;

import com.ctc.wstx.shaded.msv.relaxng_datatype.ValidationContext;
import com.ctc.wstx.shaded.msv_core.datatype.SerializationContext;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.BuiltinAtomicType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.Comparator;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.SimpleURType;
import com.ctc.wstx.shaded.msv_core.datatype.xsd.XSDatatype;
import java.math.BigDecimal;
import java.math.BigInteger;

public class NumberType
extends BuiltinAtomicType
implements Comparator {
    public static final NumberType theInstance = new NumberType();
    private static final BigInteger the10 = new BigInteger("10");
    private static final long serialVersionUID = 1L;

    private NumberType() {
        super("decimal");
    }

    public final XSDatatype getBaseType() {
        return SimpleURType.theInstance;
    }

    protected boolean checkFormat(String content, ValidationContext context) {
        int len = content.length();
        int i = 0;
        boolean atLeastOneDigit = false;
        if (len == 0) {
            return false;
        }
        char ch = content.charAt(0);
        if (ch == '-' || ch == '+') {
            ++i;
        }
        while (i < len) {
            if ('0' <= (ch = content.charAt(i++)) && ch <= '9') {
                atLeastOneDigit = true;
                continue;
            }
            if (ch == '.') break;
            return false;
        }
        while (i < len) {
            if ('0' <= (ch = content.charAt(i++)) && ch <= '9') {
                atLeastOneDigit = true;
                continue;
            }
            return false;
        }
        return atLeastOneDigit;
    }

    public Object _createValue(String content, ValidationContext context) {
        if (!this.checkFormat(content, context)) {
            return null;
        }
        return NumberType.load(content);
    }

    public static BigDecimal load(String content) {
        try {
            BigInteger[] q_r;
            if (content.length() == 0) {
                return null;
            }
            if (content.charAt(0) == '+') {
                content = content.substring(1);
            }
            BigDecimal r = new BigDecimal(content);
            while (r.scale() > 0 && (q_r = r.unscaledValue().divideAndRemainder(the10))[1].equals(BigInteger.ZERO)) {
                r = new BigDecimal(q_r[0], r.scale() - 1);
            }
            return r;
        }
        catch (NumberFormatException e) {
            return null;
        }
    }

    public static String save(Object o) {
        return ((BigDecimal)o).toString();
    }

    public Class getJavaObjectType() {
        return BigDecimal.class;
    }

    public String convertToLexicalValue(Object o, SerializationContext context) {
        if (o instanceof BigDecimal) {
            return o.toString();
        }
        throw new IllegalArgumentException();
    }

    public final int isFacetApplicable(String facetName) {
        if (facetName.equals("totalDigits") || facetName.equals("fractionDigits") || facetName.equals("pattern") || facetName.equals("enumeration") || facetName.equals("whiteSpace") || facetName.equals("maxInclusive") || facetName.equals("minInclusive") || facetName.equals("maxExclusive") || facetName.equals("minExclusive")) {
            return 0;
        }
        return -2;
    }

    public final int compare(Object o1, Object o2) {
        int r = ((Comparable)o1).compareTo(o2);
        if (r < 0) {
            return -1;
        }
        if (r > 0) {
            return 1;
        }
        return 0;
    }
}


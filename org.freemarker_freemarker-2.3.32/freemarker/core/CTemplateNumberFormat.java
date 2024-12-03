/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.TemplateFormatUtil;
import freemarker.core.TemplateNumberFormat;
import freemarker.core.TemplateValueFormatException;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateNumberModel;
import java.math.BigDecimal;
import java.math.BigInteger;

final class CTemplateNumberFormat
extends TemplateNumberFormat {
    private static final float MAX_INCREMENT_1_FLOAT = 1.6777216E7f;
    private static final double MAX_INCREMENT_1_DOUBLE = 9.007199254740992E15;
    private final String doublePositiveInfinity;
    private final String doubleNegativeInfinity;
    private final String doubleNaN;
    private final String floatPositiveInfinity;
    private final String floatNegativeInfinity;
    private final String floatNaN;

    CTemplateNumberFormat(String doublePositiveInfinity, String doubleNegativeInfinity, String doubleNaN, String floatPositiveInfinity, String floatNegativeInfinity, String floatNaN) {
        this.doublePositiveInfinity = doublePositiveInfinity;
        this.doubleNegativeInfinity = doubleNegativeInfinity;
        this.doubleNaN = doubleNaN;
        this.floatPositiveInfinity = floatPositiveInfinity;
        this.floatNegativeInfinity = floatNegativeInfinity;
        this.floatNaN = floatNaN;
    }

    @Override
    public String formatToPlainText(TemplateNumberModel numberModel) throws TemplateValueFormatException, TemplateModelException {
        Number num = TemplateFormatUtil.getNonNullNumber(numberModel);
        if (num instanceof Integer || num instanceof Long) {
            return num.toString();
        }
        if (num instanceof Double) {
            double n = num.doubleValue();
            if (n == Double.POSITIVE_INFINITY) {
                return this.doublePositiveInfinity;
            }
            if (n == Double.NEGATIVE_INFINITY) {
                return this.doubleNegativeInfinity;
            }
            if (Double.isNaN(n)) {
                return this.doubleNaN;
            }
            if (Math.floor(n) == n) {
                if (Math.abs(n) <= 9.007199254740992E15) {
                    return Long.toString((long)n);
                }
            } else {
                double absN = Math.abs(n);
                if (absN < 0.001 && absN > 1.0E-7) {
                    return BigDecimal.valueOf(n).toString();
                }
                if (absN >= 1.0E7) {
                    return BigDecimal.valueOf(n).toPlainString();
                }
            }
            return CTemplateNumberFormat.removeRedundantDot0(Double.toString(n));
        }
        if (num instanceof Float) {
            float n = num.floatValue();
            if (n == Float.POSITIVE_INFINITY) {
                return this.floatPositiveInfinity;
            }
            if (n == Float.NEGATIVE_INFINITY) {
                return this.floatNegativeInfinity;
            }
            if (Float.isNaN(n)) {
                return this.floatNaN;
            }
            if (Math.floor(n) == (double)n) {
                if (Math.abs(n) <= 1.6777216E7f) {
                    return Long.toString((long)n);
                }
            } else {
                float absN = Math.abs(n);
                if (absN < 0.001f && absN > 1.0E-7f) {
                    return new BigDecimal(num.toString()).toString();
                }
            }
            return CTemplateNumberFormat.removeRedundantDot0(Float.toString(n));
        }
        if (num instanceof BigInteger) {
            return num.toString();
        }
        if (num instanceof BigDecimal) {
            BigDecimal bd = ((BigDecimal)num).stripTrailingZeros();
            int scale = bd.scale();
            if (scale <= 0) {
                if (scale <= -100) {
                    return bd.toString();
                }
                return bd.toPlainString();
            }
            return bd.toString();
        }
        return num.toString();
    }

    private static String removeRedundantDot0(String s) {
        int len = s.length();
        for (int i = 0; i < len; ++i) {
            int src;
            char c = s.charAt(i);
            if (c != '.') continue;
            if (s.charAt(++i) != '0') break;
            if (++i == len) {
                return s.substring(0, len - 2);
            }
            if (s.charAt(i) != 'E') break;
            char[] result = new char[s.length() - 2];
            int dst = 0;
            for (src = 0; src < i - 2; ++src) {
                result[dst++] = s.charAt(src);
            }
            for (src = i; src < len; ++src) {
                result[dst++] = s.charAt(src);
            }
            return String.valueOf(result);
        }
        return s;
    }

    @Override
    public boolean isLocaleBound() {
        return false;
    }

    @Override
    public String getDescription() {
        return "c";
    }
}


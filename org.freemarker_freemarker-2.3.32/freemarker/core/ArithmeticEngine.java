/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.BugException;
import freemarker.core._MiscTemplateException;
import freemarker.template.TemplateException;
import freemarker.template.utility.NumberUtil;
import freemarker.template.utility.OptimizerUtil;
import freemarker.template.utility.StringUtil;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

public abstract class ArithmeticEngine {
    public static final BigDecimalEngine BIGDECIMAL_ENGINE = new BigDecimalEngine();
    public static final ConservativeEngine CONSERVATIVE_ENGINE = new ConservativeEngine();
    protected int minScale = 12;
    protected int maxScale = 12;
    protected int roundingPolicy = 4;

    public abstract int compareNumbers(Number var1, Number var2) throws TemplateException;

    public abstract Number add(Number var1, Number var2) throws TemplateException;

    public abstract Number subtract(Number var1, Number var2) throws TemplateException;

    public abstract Number multiply(Number var1, Number var2) throws TemplateException;

    public abstract Number divide(Number var1, Number var2) throws TemplateException;

    public abstract Number modulus(Number var1, Number var2) throws TemplateException;

    public abstract Number toNumber(String var1);

    public void setMinScale(int minScale) {
        if (minScale < 0) {
            throw new IllegalArgumentException("minScale < 0");
        }
        this.minScale = minScale;
    }

    public void setMaxScale(int maxScale) {
        if (maxScale < this.minScale) {
            throw new IllegalArgumentException("maxScale < minScale");
        }
        this.maxScale = maxScale;
    }

    public void setRoundingPolicy(int roundingPolicy) {
        if (roundingPolicy != 2 && roundingPolicy != 1 && roundingPolicy != 3 && roundingPolicy != 5 && roundingPolicy != 6 && roundingPolicy != 4 && roundingPolicy != 7 && roundingPolicy != 0) {
            throw new IllegalArgumentException("invalid rounding policy");
        }
        this.roundingPolicy = roundingPolicy;
    }

    private static BigDecimal toBigDecimal(Number num) {
        if (num instanceof BigDecimal) {
            return (BigDecimal)num;
        }
        if (num instanceof Integer || num instanceof Long || num instanceof Byte || num instanceof Short) {
            return BigDecimal.valueOf(num.longValue());
        }
        if (num instanceof BigInteger) {
            return new BigDecimal((BigInteger)num);
        }
        try {
            return new BigDecimal(num.toString());
        }
        catch (NumberFormatException e) {
            if (NumberUtil.isInfinite(num)) {
                throw new NumberFormatException("It's impossible to convert an infinite value (" + num.getClass().getSimpleName() + " " + num + ") to BigDecimal.");
            }
            throw new NumberFormatException("Can't parse this as BigDecimal number: " + StringUtil.jQuote(num));
        }
    }

    private static Number toBigDecimalOrDouble(String s) {
        if (s.length() > 2) {
            char c = s.charAt(0);
            if (c == 'I' && (s.equals("INF") || s.equals("Infinity"))) {
                return Double.POSITIVE_INFINITY;
            }
            if (c == 'N' && s.equals("NaN")) {
                return Double.NaN;
            }
            if (c == '-' && s.charAt(1) == 'I' && (s.equals("-INF") || s.equals("-Infinity"))) {
                return Double.NEGATIVE_INFINITY;
            }
        }
        return new BigDecimal(s);
    }

    public static class ConservativeEngine
    extends ArithmeticEngine {
        private static final int INTEGER = 0;
        private static final int LONG = 1;
        private static final int FLOAT = 2;
        private static final int DOUBLE = 3;
        private static final int BIGINTEGER = 4;
        private static final int BIGDECIMAL = 5;
        private static final Map classCodes = ConservativeEngine.createClassCodesMap();

        @Override
        public int compareNumbers(Number first, Number second) throws TemplateException {
            switch (ConservativeEngine.getCommonClassCode(first, second)) {
                case 0: {
                    int n1 = first.intValue();
                    int n2 = second.intValue();
                    return n1 < n2 ? -1 : (n1 == n2 ? 0 : 1);
                }
                case 1: {
                    long n1 = first.longValue();
                    long n2 = second.longValue();
                    return n1 < n2 ? -1 : (n1 == n2 ? 0 : 1);
                }
                case 2: {
                    float n1 = first.floatValue();
                    float n2 = second.floatValue();
                    return n1 < n2 ? -1 : (n1 == n2 ? 0 : 1);
                }
                case 3: {
                    double n1 = first.doubleValue();
                    double n2 = second.doubleValue();
                    return n1 < n2 ? -1 : (n1 == n2 ? 0 : 1);
                }
                case 4: {
                    BigInteger n1 = ConservativeEngine.toBigInteger(first);
                    BigInteger n2 = ConservativeEngine.toBigInteger(second);
                    return n1.compareTo(n2);
                }
                case 5: {
                    BigDecimal n1 = ArithmeticEngine.toBigDecimal(first);
                    BigDecimal n2 = ArithmeticEngine.toBigDecimal(second);
                    return n1.compareTo(n2);
                }
            }
            throw new Error();
        }

        @Override
        public Number add(Number first, Number second) throws TemplateException {
            switch (ConservativeEngine.getCommonClassCode(first, second)) {
                case 0: {
                    int n1 = first.intValue();
                    int n2 = second.intValue();
                    int n = n1 + n2;
                    return (n ^ n1) < 0 && (n ^ n2) < 0 ? Long.valueOf((long)n1 + (long)n2) : (long)Integer.valueOf(n).intValue();
                }
                case 1: {
                    long n1 = first.longValue();
                    long n2 = second.longValue();
                    long n = n1 + n2;
                    return (n ^ n1) < 0L && (n ^ n2) < 0L ? ConservativeEngine.toBigInteger(first).add(ConservativeEngine.toBigInteger(second)) : Long.valueOf(n);
                }
                case 2: {
                    return Float.valueOf(first.floatValue() + second.floatValue());
                }
                case 3: {
                    return first.doubleValue() + second.doubleValue();
                }
                case 4: {
                    BigInteger n1 = ConservativeEngine.toBigInteger(first);
                    BigInteger n2 = ConservativeEngine.toBigInteger(second);
                    return n1.add(n2);
                }
                case 5: {
                    BigDecimal n1 = ArithmeticEngine.toBigDecimal(first);
                    BigDecimal n2 = ArithmeticEngine.toBigDecimal(second);
                    return n1.add(n2);
                }
            }
            throw new Error();
        }

        @Override
        public Number subtract(Number first, Number second) throws TemplateException {
            switch (ConservativeEngine.getCommonClassCode(first, second)) {
                case 0: {
                    int n1 = first.intValue();
                    int n2 = second.intValue();
                    int n = n1 - n2;
                    return (n ^ n1) < 0 && (n ^ ~n2) < 0 ? Long.valueOf((long)n1 - (long)n2) : (long)Integer.valueOf(n).intValue();
                }
                case 1: {
                    long n1 = first.longValue();
                    long n2 = second.longValue();
                    long n = n1 - n2;
                    return (n ^ n1) < 0L && (n ^ (n2 ^ 0xFFFFFFFFFFFFFFFFL)) < 0L ? ConservativeEngine.toBigInteger(first).subtract(ConservativeEngine.toBigInteger(second)) : Long.valueOf(n);
                }
                case 2: {
                    return Float.valueOf(first.floatValue() - second.floatValue());
                }
                case 3: {
                    return first.doubleValue() - second.doubleValue();
                }
                case 4: {
                    BigInteger n1 = ConservativeEngine.toBigInteger(first);
                    BigInteger n2 = ConservativeEngine.toBigInteger(second);
                    return n1.subtract(n2);
                }
                case 5: {
                    BigDecimal n1 = ArithmeticEngine.toBigDecimal(first);
                    BigDecimal n2 = ArithmeticEngine.toBigDecimal(second);
                    return n1.subtract(n2);
                }
            }
            throw new Error();
        }

        @Override
        public Number multiply(Number first, Number second) throws TemplateException {
            switch (ConservativeEngine.getCommonClassCode(first, second)) {
                case 0: {
                    int n1 = first.intValue();
                    int n2 = second.intValue();
                    int n = n1 * n2;
                    return n1 == 0 || n / n1 == n2 ? (long)Integer.valueOf(n).intValue() : Long.valueOf((long)n1 * (long)n2);
                }
                case 1: {
                    long n1 = first.longValue();
                    long n2 = second.longValue();
                    long n = n1 * n2;
                    return n1 == 0L || n / n1 == n2 ? Long.valueOf(n) : ConservativeEngine.toBigInteger(first).multiply(ConservativeEngine.toBigInteger(second));
                }
                case 2: {
                    return Float.valueOf(first.floatValue() * second.floatValue());
                }
                case 3: {
                    return first.doubleValue() * second.doubleValue();
                }
                case 4: {
                    BigInteger n1 = ConservativeEngine.toBigInteger(first);
                    BigInteger n2 = ConservativeEngine.toBigInteger(second);
                    return n1.multiply(n2);
                }
                case 5: {
                    BigDecimal n1 = ArithmeticEngine.toBigDecimal(first);
                    BigDecimal n2 = ArithmeticEngine.toBigDecimal(second);
                    BigDecimal r = n1.multiply(n2);
                    return r.scale() > this.maxScale ? r.setScale(this.maxScale, this.roundingPolicy) : r;
                }
            }
            throw new Error();
        }

        @Override
        public Number divide(Number first, Number second) throws TemplateException {
            switch (ConservativeEngine.getCommonClassCode(first, second)) {
                case 0: {
                    int n1 = first.intValue();
                    int n2 = second.intValue();
                    if (n1 % n2 == 0) {
                        return n1 / n2;
                    }
                    return (double)n1 / (double)n2;
                }
                case 1: {
                    long n1 = first.longValue();
                    long n2 = second.longValue();
                    if (n1 % n2 == 0L) {
                        return n1 / n2;
                    }
                    return (double)n1 / (double)n2;
                }
                case 2: {
                    return Float.valueOf(first.floatValue() / second.floatValue());
                }
                case 3: {
                    return first.doubleValue() / second.doubleValue();
                }
                case 4: {
                    BigInteger n1 = ConservativeEngine.toBigInteger(first);
                    BigInteger n2 = ConservativeEngine.toBigInteger(second);
                    BigInteger[] divmod = n1.divideAndRemainder(n2);
                    if (divmod[1].equals(BigInteger.ZERO)) {
                        return divmod[0];
                    }
                    BigDecimal bd1 = new BigDecimal(n1);
                    BigDecimal bd2 = new BigDecimal(n2);
                    return bd1.divide(bd2, this.minScale, this.roundingPolicy);
                }
                case 5: {
                    BigDecimal n1 = ArithmeticEngine.toBigDecimal(first);
                    BigDecimal n2 = ArithmeticEngine.toBigDecimal(second);
                    int scale1 = n1.scale();
                    int scale2 = n2.scale();
                    int scale = Math.max(scale1, scale2);
                    scale = Math.max(this.minScale, scale);
                    return n1.divide(n2, scale, this.roundingPolicy);
                }
            }
            throw new Error();
        }

        @Override
        public Number modulus(Number first, Number second) throws TemplateException {
            switch (ConservativeEngine.getCommonClassCode(first, second)) {
                case 0: {
                    return first.intValue() % second.intValue();
                }
                case 1: {
                    return first.longValue() % second.longValue();
                }
                case 2: {
                    return Float.valueOf(first.floatValue() % second.floatValue());
                }
                case 3: {
                    return first.doubleValue() % second.doubleValue();
                }
                case 4: {
                    BigInteger n1 = ConservativeEngine.toBigInteger(first);
                    BigInteger n2 = ConservativeEngine.toBigInteger(second);
                    return n1.mod(n2);
                }
                case 5: {
                    throw new _MiscTemplateException("Can't calculate remainder on BigDecimals");
                }
            }
            throw new BugException();
        }

        @Override
        public Number toNumber(String s) {
            Number n = ArithmeticEngine.toBigDecimalOrDouble(s);
            return n instanceof BigDecimal ? (Number)OptimizerUtil.optimizeNumberRepresentation(n) : (Number)n;
        }

        private static Map createClassCodesMap() {
            HashMap<Class, Integer> map = new HashMap<Class, Integer>(17);
            Integer intcode = 0;
            map.put(Byte.class, intcode);
            map.put(Short.class, intcode);
            map.put(Integer.class, intcode);
            map.put(Long.class, 1);
            map.put(Float.class, 2);
            map.put(Double.class, 3);
            map.put(BigInteger.class, 4);
            map.put(BigDecimal.class, 5);
            return map;
        }

        private static int getClassCode(Number num) throws TemplateException {
            try {
                return (Integer)classCodes.get(num.getClass());
            }
            catch (NullPointerException e) {
                if (num == null) {
                    throw new _MiscTemplateException("The Number object was null.");
                }
                throw new _MiscTemplateException("Unknown number type ", num.getClass().getName());
            }
        }

        private static int getCommonClassCode(Number num1, Number num2) throws TemplateException {
            int c2;
            int c1 = ConservativeEngine.getClassCode(num1);
            int c = c1 > (c2 = ConservativeEngine.getClassCode(num2)) ? c1 : c2;
            switch (c) {
                case 2: {
                    if ((c1 < c2 ? c1 : c2) != 1) break;
                    return 3;
                }
                case 4: {
                    int min;
                    int n = min = c1 < c2 ? c1 : c2;
                    if (min != 3 && min != 2) break;
                    return 5;
                }
            }
            return c;
        }

        private static BigInteger toBigInteger(Number num) {
            return num instanceof BigInteger ? (BigInteger)num : new BigInteger(num.toString());
        }
    }

    public static class BigDecimalEngine
    extends ArithmeticEngine {
        @Override
        public int compareNumbers(Number first, Number second) {
            float secondF;
            double secondD;
            float firstF;
            double firstD;
            int secondSignum;
            int firstSignum = NumberUtil.getSignum(first);
            if (firstSignum != (secondSignum = NumberUtil.getSignum(second))) {
                return firstSignum < secondSignum ? -1 : (firstSignum > secondSignum ? 1 : 0);
            }
            if (firstSignum == 0 && secondSignum == 0) {
                return 0;
            }
            if (first.getClass() == second.getClass()) {
                if (first instanceof BigDecimal) {
                    return ((BigDecimal)first).compareTo((BigDecimal)second);
                }
                if (first instanceof Integer) {
                    return ((Integer)first).compareTo((Integer)second);
                }
                if (first instanceof Long) {
                    return ((Long)first).compareTo((Long)second);
                }
                if (first instanceof Double) {
                    return ((Double)first).compareTo((Double)second);
                }
                if (first instanceof Float) {
                    return ((Float)first).compareTo((Float)second);
                }
                if (first instanceof Byte) {
                    return ((Byte)first).compareTo((Byte)second);
                }
                if (first instanceof Short) {
                    return ((Short)first).compareTo((Short)second);
                }
            }
            if (first instanceof Double && Double.isInfinite(firstD = first.doubleValue())) {
                if (NumberUtil.hasTypeThatIsKnownToNotSupportInfiniteAndNaN(second)) {
                    return firstD == Double.NEGATIVE_INFINITY ? -1 : 1;
                }
                if (second instanceof Float) {
                    return Double.compare(firstD, second.doubleValue());
                }
            }
            if (first instanceof Float && Float.isInfinite(firstF = first.floatValue())) {
                if (NumberUtil.hasTypeThatIsKnownToNotSupportInfiniteAndNaN(second)) {
                    return firstF == Float.NEGATIVE_INFINITY ? -1 : 1;
                }
                if (second instanceof Double) {
                    return Double.compare(firstF, second.doubleValue());
                }
            }
            if (second instanceof Double && Double.isInfinite(secondD = second.doubleValue())) {
                if (NumberUtil.hasTypeThatIsKnownToNotSupportInfiniteAndNaN(first)) {
                    return secondD == Double.NEGATIVE_INFINITY ? 1 : -1;
                }
                if (first instanceof Float) {
                    return Double.compare(first.doubleValue(), secondD);
                }
            }
            if (second instanceof Float && Float.isInfinite(secondF = second.floatValue())) {
                if (NumberUtil.hasTypeThatIsKnownToNotSupportInfiniteAndNaN(first)) {
                    return secondF == Float.NEGATIVE_INFINITY ? 1 : -1;
                }
                if (first instanceof Double) {
                    return Double.compare(first.doubleValue(), secondF);
                }
            }
            return ArithmeticEngine.toBigDecimal(first).compareTo(ArithmeticEngine.toBigDecimal(second));
        }

        @Override
        public Number add(Number first, Number second) {
            BigDecimal left = ArithmeticEngine.toBigDecimal(first);
            BigDecimal right = ArithmeticEngine.toBigDecimal(second);
            return left.add(right);
        }

        @Override
        public Number subtract(Number first, Number second) {
            BigDecimal left = ArithmeticEngine.toBigDecimal(first);
            BigDecimal right = ArithmeticEngine.toBigDecimal(second);
            return left.subtract(right);
        }

        @Override
        public Number multiply(Number first, Number second) {
            BigDecimal right;
            BigDecimal left = ArithmeticEngine.toBigDecimal(first);
            BigDecimal result = left.multiply(right = ArithmeticEngine.toBigDecimal(second));
            if (result.scale() > this.maxScale) {
                result = result.setScale(this.maxScale, this.roundingPolicy);
            }
            return result;
        }

        @Override
        public Number divide(Number first, Number second) {
            BigDecimal left = ArithmeticEngine.toBigDecimal(first);
            BigDecimal right = ArithmeticEngine.toBigDecimal(second);
            return this.divide(left, right);
        }

        @Override
        public Number modulus(Number first, Number second) {
            long left = first.longValue();
            long right = second.longValue();
            return left % right;
        }

        @Override
        public Number toNumber(String s) {
            return ArithmeticEngine.toBigDecimalOrDouble(s);
        }

        private BigDecimal divide(BigDecimal left, BigDecimal right) {
            int scale1 = left.scale();
            int scale2 = right.scale();
            int scale = Math.max(scale1, scale2);
            scale = Math.max(this.minScale, scale);
            return left.divide(right, scale, this.roundingPolicy);
        }
    }
}


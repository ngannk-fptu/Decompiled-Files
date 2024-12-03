/*
 * Decompiled with CFR 0.152.
 */
package freemarker.template.utility;

import freemarker.template.utility.ClassUtil;
import freemarker.template.utility.UnsupportedNumberClassException;
import java.math.BigDecimal;
import java.math.BigInteger;

public class NumberUtil {
    private static final BigDecimal BIG_DECIMAL_INT_MIN = BigDecimal.valueOf(Integer.MIN_VALUE);
    private static final BigDecimal BIG_DECIMAL_INT_MAX = BigDecimal.valueOf(Integer.MAX_VALUE);
    private static final BigInteger BIG_INTEGER_INT_MIN = BIG_DECIMAL_INT_MIN.toBigInteger();
    private static final BigInteger BIG_INTEGER_INT_MAX = BIG_DECIMAL_INT_MAX.toBigInteger();

    private NumberUtil() {
    }

    public static boolean isInfinite(Number num) {
        if (num instanceof Double) {
            return ((Double)num).isInfinite();
        }
        if (num instanceof Float) {
            return ((Float)num).isInfinite();
        }
        if (NumberUtil.hasTypeThatIsKnownToNotSupportInfiniteAndNaN(num)) {
            return false;
        }
        throw new UnsupportedNumberClassException(num.getClass());
    }

    public static boolean isNaN(Number num) {
        if (num instanceof Double) {
            return ((Double)num).isNaN();
        }
        if (num instanceof Float) {
            return ((Float)num).isNaN();
        }
        if (NumberUtil.hasTypeThatIsKnownToNotSupportInfiniteAndNaN(num)) {
            return false;
        }
        throw new UnsupportedNumberClassException(num.getClass());
    }

    public static int getSignum(Number num) throws ArithmeticException {
        if (num instanceof Integer) {
            int n = num.intValue();
            return n > 0 ? 1 : (n == 0 ? 0 : -1);
        }
        if (num instanceof BigDecimal) {
            BigDecimal n = (BigDecimal)num;
            return n.signum();
        }
        if (num instanceof Double) {
            double n = num.doubleValue();
            if (n > 0.0) {
                return 1;
            }
            if (n == 0.0) {
                return 0;
            }
            if (n < 0.0) {
                return -1;
            }
            throw new ArithmeticException("The signum of " + n + " is not defined.");
        }
        if (num instanceof Float) {
            float n = num.floatValue();
            if (n > 0.0f) {
                return 1;
            }
            if (n == 0.0f) {
                return 0;
            }
            if (n < 0.0f) {
                return -1;
            }
            throw new ArithmeticException("The signum of " + n + " is not defined.");
        }
        if (num instanceof Long) {
            long n = num.longValue();
            return n > 0L ? 1 : (n == 0L ? 0 : -1);
        }
        if (num instanceof Short) {
            short n = num.shortValue();
            return n > 0 ? 1 : (n == 0 ? 0 : -1);
        }
        if (num instanceof Byte) {
            byte n = num.byteValue();
            return n > 0 ? 1 : (n == 0 ? 0 : -1);
        }
        if (num instanceof BigInteger) {
            BigInteger n = (BigInteger)num;
            return n.signum();
        }
        throw new UnsupportedNumberClassException(num.getClass());
    }

    public static boolean isIntegerBigDecimal(BigDecimal bd) {
        return bd.scale() <= 0 || bd.setScale(0, 1).compareTo(bd) == 0;
    }

    public static boolean hasTypeThatIsKnownToNotSupportInfiniteAndNaN(Number num) {
        return num instanceof Integer || num instanceof BigDecimal || num instanceof Long || num instanceof Short || num instanceof Byte || num instanceof BigInteger;
    }

    public static int toIntExact(Number num) {
        if (num instanceof Integer || num instanceof Short || num instanceof Byte) {
            return num.intValue();
        }
        if (num instanceof Long) {
            int result;
            long n = num.longValue();
            if (n != (long)(result = (int)n)) {
                throw NumberUtil.newLossyConverionException(num, Integer.class);
            }
            return result;
        }
        if (num instanceof Double || num instanceof Float) {
            double n = num.doubleValue();
            if (n % 1.0 != 0.0 || n < -2.147483648E9 || n > 2.147483647E9) {
                throw NumberUtil.newLossyConverionException(num, Integer.class);
            }
            return (int)n;
        }
        if (num instanceof BigDecimal) {
            BigDecimal n = (BigDecimal)num;
            if (!NumberUtil.isIntegerBigDecimal(n) || n.compareTo(BIG_DECIMAL_INT_MAX) > 0 || n.compareTo(BIG_DECIMAL_INT_MIN) < 0) {
                throw NumberUtil.newLossyConverionException(num, Integer.class);
            }
            return n.intValue();
        }
        if (num instanceof BigInteger) {
            BigInteger n = (BigInteger)num;
            if (n.compareTo(BIG_INTEGER_INT_MAX) > 0 || n.compareTo(BIG_INTEGER_INT_MIN) < 0) {
                throw NumberUtil.newLossyConverionException(num, Integer.class);
            }
            return n.intValue();
        }
        throw new UnsupportedNumberClassException(num.getClass());
    }

    private static ArithmeticException newLossyConverionException(Number fromValue, Class toType) {
        return new ArithmeticException("Can't convert " + fromValue + " to type " + ClassUtil.getShortClassName(toType) + " without loss.");
    }
}


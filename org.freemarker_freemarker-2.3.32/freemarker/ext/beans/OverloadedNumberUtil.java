/*
 * Decompiled with CFR 0.152.
 */
package freemarker.ext.beans;

import freemarker.template.utility.ClassUtil;
import freemarker.template.utility.NumberUtil;
import java.math.BigDecimal;
import java.math.BigInteger;

class OverloadedNumberUtil {
    static final int BIG_MANTISSA_LOSS_PRICE = 40000;
    private static final long MAX_DOUBLE_OR_LONG = 0x20000000000000L;
    private static final long MIN_DOUBLE_OR_LONG = -9007199254740992L;
    private static final int MAX_DOUBLE_OR_LONG_LOG_2 = 53;
    private static final int MAX_FLOAT_OR_INT = 0x1000000;
    private static final int MIN_FLOAT_OR_INT = -16777216;
    private static final int MAX_FLOAT_OR_INT_LOG_2 = 24;
    private static final double LOWEST_ABOVE_ZERO = 1.0E-6;
    private static final double HIGHEST_BELOW_ONE = 0.999999;

    private OverloadedNumberUtil() {
    }

    static Number addFallbackType(Number num, int typeFlags) {
        Class<?> numClass;
        block44: {
            block45: {
                boolean exact;
                int intN;
                block47: {
                    double diff;
                    block50: {
                        block48: {
                            block49: {
                                block46: {
                                    block37: {
                                        double doubleN;
                                        block38: {
                                            boolean exact2;
                                            long longN;
                                            block40: {
                                                double diff2;
                                                block43: {
                                                    block41: {
                                                        block42: {
                                                            block39: {
                                                                numClass = num.getClass();
                                                                if (numClass == BigDecimal.class) {
                                                                    BigDecimal n = (BigDecimal)num;
                                                                    if ((typeFlags & 0x13C) != 0 && (typeFlags & 0x2C0) != 0 && NumberUtil.isIntegerBigDecimal(n)) {
                                                                        return new IntegerBigDecimal(n);
                                                                    }
                                                                    return n;
                                                                }
                                                                if (numClass == Integer.class) {
                                                                    int pn = num.intValue();
                                                                    if ((typeFlags & 4) != 0 && pn <= 127 && pn >= -128) {
                                                                        return new IntegerOrByte((Integer)num, (byte)pn);
                                                                    }
                                                                    if ((typeFlags & 8) != 0 && pn <= Short.MAX_VALUE && pn >= Short.MIN_VALUE) {
                                                                        return new IntegerOrShort((Integer)num, (short)pn);
                                                                    }
                                                                    return num;
                                                                }
                                                                if (numClass == Long.class) {
                                                                    long pn = num.longValue();
                                                                    if ((typeFlags & 4) != 0 && pn <= 127L && pn >= -128L) {
                                                                        return new LongOrByte((Long)num, (byte)pn);
                                                                    }
                                                                    if ((typeFlags & 8) != 0 && pn <= 32767L && pn >= -32768L) {
                                                                        return new LongOrShort((Long)num, (short)pn);
                                                                    }
                                                                    if ((typeFlags & 0x10) != 0 && pn <= Integer.MAX_VALUE && pn >= Integer.MIN_VALUE) {
                                                                        return new LongOrInteger((Long)num, (int)pn);
                                                                    }
                                                                    return num;
                                                                }
                                                                if (numClass != Double.class) break block37;
                                                                doubleN = num.doubleValue();
                                                                if ((typeFlags & 0x13C) == 0 || doubleN > 9.007199254740992E15 || doubleN < -9.007199254740992E15) break block38;
                                                                longN = num.longValue();
                                                                diff2 = doubleN - (double)longN;
                                                                if (diff2 != 0.0) break block39;
                                                                exact2 = true;
                                                                break block40;
                                                            }
                                                            if (!(diff2 > 0.0)) break block41;
                                                            if (!(diff2 < 1.0E-6)) break block42;
                                                            exact2 = false;
                                                            break block40;
                                                        }
                                                        if (!(diff2 > 0.999999)) break block38;
                                                        exact2 = false;
                                                        ++longN;
                                                        break block40;
                                                    }
                                                    if (!(diff2 > -1.0E-6)) break block43;
                                                    exact2 = false;
                                                    break block40;
                                                }
                                                if (!(diff2 < -0.999999)) break block38;
                                                exact2 = false;
                                                --longN;
                                            }
                                            if ((typeFlags & 4) != 0 && longN <= 127L && longN >= -128L) {
                                                return new DoubleOrByte((Double)num, (byte)longN);
                                            }
                                            if ((typeFlags & 8) != 0 && longN <= 32767L && longN >= -32768L) {
                                                return new DoubleOrShort((Double)num, (short)longN);
                                            }
                                            if ((typeFlags & 0x10) != 0 && longN <= Integer.MAX_VALUE && longN >= Integer.MIN_VALUE) {
                                                int intN2 = (int)longN;
                                                return (typeFlags & 0x40) != 0 && intN2 >= -16777216 && intN2 <= 0x1000000 ? new DoubleOrIntegerOrFloat((Double)num, intN2) : new DoubleOrInteger((Double)num, intN2);
                                            }
                                            if ((typeFlags & 0x20) != 0) {
                                                if (exact2) {
                                                    return new DoubleOrLong((Double)num, longN);
                                                }
                                                if (longN >= Integer.MIN_VALUE && longN <= Integer.MAX_VALUE) {
                                                    return new DoubleOrLong((Double)num, longN);
                                                }
                                            }
                                        }
                                        if ((typeFlags & 0x40) != 0 && doubleN >= -3.4028234663852886E38 && doubleN <= 3.4028234663852886E38) {
                                            return new DoubleOrFloat((Double)num);
                                        }
                                        return num;
                                    }
                                    if (numClass != Float.class) break block44;
                                    float floatN = num.floatValue();
                                    if ((typeFlags & 0x13C) == 0 || floatN > 1.6777216E7f || floatN < -1.6777216E7f) break block45;
                                    intN = num.intValue();
                                    diff = floatN - (float)intN;
                                    if (diff != 0.0) break block46;
                                    exact = true;
                                    break block47;
                                }
                                if (intN < -128 || intN > 127) break block45;
                                if (!(diff > 0.0)) break block48;
                                if (!(diff < 1.0E-5)) break block49;
                                exact = false;
                                break block47;
                            }
                            if (!(diff > 0.99999)) break block45;
                            exact = false;
                            ++intN;
                            break block47;
                        }
                        if (!(diff > -1.0E-5)) break block50;
                        exact = false;
                        break block47;
                    }
                    if (!(diff < -0.99999)) break block45;
                    exact = false;
                    --intN;
                }
                if ((typeFlags & 4) != 0 && intN <= 127 && intN >= -128) {
                    return new FloatOrByte((Float)num, (byte)intN);
                }
                if ((typeFlags & 8) != 0 && intN <= Short.MAX_VALUE && intN >= Short.MIN_VALUE) {
                    return new FloatOrShort((Float)num, (short)intN);
                }
                if ((typeFlags & 0x10) != 0) {
                    return new FloatOrInteger((Float)num, intN);
                }
                if ((typeFlags & 0x20) != 0) {
                    return exact ? new FloatOrInteger((Float)num, intN) : new FloatOrByte((Float)num, (byte)intN);
                }
            }
            return num;
        }
        if (numClass == Byte.class) {
            return num;
        }
        if (numClass == Short.class) {
            short pn = num.shortValue();
            if ((typeFlags & 4) != 0 && pn <= 127 && pn >= -128) {
                return new ShortOrByte((Short)num, (byte)pn);
            }
            return num;
        }
        if (numClass == BigInteger.class) {
            if ((typeFlags & 0xFC) != 0) {
                BigInteger biNum = (BigInteger)num;
                int bitLength = biNum.bitLength();
                if ((typeFlags & 4) != 0 && bitLength <= 7) {
                    return new BigIntegerOrByte(biNum);
                }
                if ((typeFlags & 8) != 0 && bitLength <= 15) {
                    return new BigIntegerOrShort(biNum);
                }
                if ((typeFlags & 0x10) != 0 && bitLength <= 31) {
                    return new BigIntegerOrInteger(biNum);
                }
                if ((typeFlags & 0x20) != 0 && bitLength <= 63) {
                    return new BigIntegerOrLong(biNum);
                }
                if ((typeFlags & 0x40) != 0 && (bitLength <= 24 || bitLength == 25 && biNum.getLowestSetBit() >= 24)) {
                    return new BigIntegerOrFloat(biNum);
                }
                if ((typeFlags & 0x80) != 0 && (bitLength <= 53 || bitLength == 54 && biNum.getLowestSetBit() >= 53)) {
                    return new BigIntegerOrDouble(biNum);
                }
                return num;
            }
            return num;
        }
        return num;
    }

    static int getArgumentConversionPrice(Class fromC, Class toC) {
        if (toC == fromC) {
            return 0;
        }
        if (toC == Integer.class) {
            if (fromC == IntegerBigDecimal.class) {
                return 31003;
            }
            if (fromC == BigDecimal.class) {
                return 41003;
            }
            if (fromC == Long.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == Double.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == Float.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == Byte.class) {
                return 10003;
            }
            if (fromC == BigInteger.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == LongOrInteger.class) {
                return 21003;
            }
            if (fromC == DoubleOrFloat.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == DoubleOrIntegerOrFloat.class) {
                return 22003;
            }
            if (fromC == DoubleOrInteger.class) {
                return 22003;
            }
            if (fromC == DoubleOrLong.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == IntegerOrByte.class) {
                return 0;
            }
            if (fromC == DoubleOrByte.class) {
                return 22003;
            }
            if (fromC == LongOrByte.class) {
                return 21003;
            }
            if (fromC == Short.class) {
                return 10003;
            }
            if (fromC == LongOrShort.class) {
                return 21003;
            }
            if (fromC == ShortOrByte.class) {
                return 10003;
            }
            if (fromC == FloatOrInteger.class) {
                return 21003;
            }
            if (fromC == FloatOrByte.class) {
                return 21003;
            }
            if (fromC == FloatOrShort.class) {
                return 21003;
            }
            if (fromC == BigIntegerOrInteger.class) {
                return 16003;
            }
            if (fromC == BigIntegerOrLong.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == BigIntegerOrDouble.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == BigIntegerOrFloat.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == BigIntegerOrByte.class) {
                return 16003;
            }
            if (fromC == IntegerOrShort.class) {
                return 0;
            }
            if (fromC == DoubleOrShort.class) {
                return 22003;
            }
            if (fromC == BigIntegerOrShort.class) {
                return 16003;
            }
            return Integer.MAX_VALUE;
        }
        if (toC == Long.class) {
            if (fromC == Integer.class) {
                return 10004;
            }
            if (fromC == IntegerBigDecimal.class) {
                return 31004;
            }
            if (fromC == BigDecimal.class) {
                return 41004;
            }
            if (fromC == Double.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == Float.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == Byte.class) {
                return 10004;
            }
            if (fromC == BigInteger.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == LongOrInteger.class) {
                return 0;
            }
            if (fromC == DoubleOrFloat.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == DoubleOrIntegerOrFloat.class) {
                return 21004;
            }
            if (fromC == DoubleOrInteger.class) {
                return 21004;
            }
            if (fromC == DoubleOrLong.class) {
                return 21004;
            }
            if (fromC == IntegerOrByte.class) {
                return 10004;
            }
            if (fromC == DoubleOrByte.class) {
                return 21004;
            }
            if (fromC == LongOrByte.class) {
                return 0;
            }
            if (fromC == Short.class) {
                return 10004;
            }
            if (fromC == LongOrShort.class) {
                return 0;
            }
            if (fromC == ShortOrByte.class) {
                return 10004;
            }
            if (fromC == FloatOrInteger.class) {
                return 21004;
            }
            if (fromC == FloatOrByte.class) {
                return 21004;
            }
            if (fromC == FloatOrShort.class) {
                return 21004;
            }
            if (fromC == BigIntegerOrInteger.class) {
                return 15004;
            }
            if (fromC == BigIntegerOrLong.class) {
                return 15004;
            }
            if (fromC == BigIntegerOrDouble.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == BigIntegerOrFloat.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == BigIntegerOrByte.class) {
                return 15004;
            }
            if (fromC == IntegerOrShort.class) {
                return 10004;
            }
            if (fromC == DoubleOrShort.class) {
                return 21004;
            }
            if (fromC == BigIntegerOrShort.class) {
                return 15004;
            }
            return Integer.MAX_VALUE;
        }
        if (toC == Double.class) {
            if (fromC == Integer.class) {
                return 20007;
            }
            if (fromC == IntegerBigDecimal.class) {
                return 32007;
            }
            if (fromC == BigDecimal.class) {
                return 32007;
            }
            if (fromC == Long.class) {
                return 30007;
            }
            if (fromC == Float.class) {
                return 10007;
            }
            if (fromC == Byte.class) {
                return 20007;
            }
            if (fromC == BigInteger.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == LongOrInteger.class) {
                return 21007;
            }
            if (fromC == DoubleOrFloat.class) {
                return 0;
            }
            if (fromC == DoubleOrIntegerOrFloat.class) {
                return 0;
            }
            if (fromC == DoubleOrInteger.class) {
                return 0;
            }
            if (fromC == DoubleOrLong.class) {
                return 0;
            }
            if (fromC == IntegerOrByte.class) {
                return 20007;
            }
            if (fromC == DoubleOrByte.class) {
                return 0;
            }
            if (fromC == LongOrByte.class) {
                return 21007;
            }
            if (fromC == Short.class) {
                return 20007;
            }
            if (fromC == LongOrShort.class) {
                return 21007;
            }
            if (fromC == ShortOrByte.class) {
                return 20007;
            }
            if (fromC == FloatOrInteger.class) {
                return 10007;
            }
            if (fromC == FloatOrByte.class) {
                return 10007;
            }
            if (fromC == FloatOrShort.class) {
                return 10007;
            }
            if (fromC == BigIntegerOrInteger.class) {
                return 20007;
            }
            if (fromC == BigIntegerOrLong.class) {
                return 30007;
            }
            if (fromC == BigIntegerOrDouble.class) {
                return 20007;
            }
            if (fromC == BigIntegerOrFloat.class) {
                return 20007;
            }
            if (fromC == BigIntegerOrByte.class) {
                return 20007;
            }
            if (fromC == IntegerOrShort.class) {
                return 20007;
            }
            if (fromC == DoubleOrShort.class) {
                return 0;
            }
            if (fromC == BigIntegerOrShort.class) {
                return 20007;
            }
            return Integer.MAX_VALUE;
        }
        if (toC == Float.class) {
            if (fromC == Integer.class) {
                return 30006;
            }
            if (fromC == IntegerBigDecimal.class) {
                return 33006;
            }
            if (fromC == BigDecimal.class) {
                return 33006;
            }
            if (fromC == Long.class) {
                return 40006;
            }
            if (fromC == Double.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == Byte.class) {
                return 20006;
            }
            if (fromC == BigInteger.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == LongOrInteger.class) {
                return 30006;
            }
            if (fromC == DoubleOrFloat.class) {
                return 30006;
            }
            if (fromC == DoubleOrIntegerOrFloat.class) {
                return 23006;
            }
            if (fromC == DoubleOrInteger.class) {
                return 30006;
            }
            if (fromC == DoubleOrLong.class) {
                return 40006;
            }
            if (fromC == IntegerOrByte.class) {
                return 24006;
            }
            if (fromC == DoubleOrByte.class) {
                return 23006;
            }
            if (fromC == LongOrByte.class) {
                return 24006;
            }
            if (fromC == Short.class) {
                return 20006;
            }
            if (fromC == LongOrShort.class) {
                return 24006;
            }
            if (fromC == ShortOrByte.class) {
                return 20006;
            }
            if (fromC == FloatOrInteger.class) {
                return 0;
            }
            if (fromC == FloatOrByte.class) {
                return 0;
            }
            if (fromC == FloatOrShort.class) {
                return 0;
            }
            if (fromC == BigIntegerOrInteger.class) {
                return 30006;
            }
            if (fromC == BigIntegerOrLong.class) {
                return 40006;
            }
            if (fromC == BigIntegerOrDouble.class) {
                return 40006;
            }
            if (fromC == BigIntegerOrFloat.class) {
                return 24006;
            }
            if (fromC == BigIntegerOrByte.class) {
                return 24006;
            }
            if (fromC == IntegerOrShort.class) {
                return 24006;
            }
            if (fromC == DoubleOrShort.class) {
                return 23006;
            }
            if (fromC == BigIntegerOrShort.class) {
                return 24006;
            }
            return Integer.MAX_VALUE;
        }
        if (toC == Byte.class) {
            if (fromC == Integer.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == IntegerBigDecimal.class) {
                return 35001;
            }
            if (fromC == BigDecimal.class) {
                return 45001;
            }
            if (fromC == Long.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == Double.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == Float.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == BigInteger.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == LongOrInteger.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == DoubleOrFloat.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == DoubleOrIntegerOrFloat.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == DoubleOrInteger.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == DoubleOrLong.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == IntegerOrByte.class) {
                return 22001;
            }
            if (fromC == DoubleOrByte.class) {
                return 25001;
            }
            if (fromC == LongOrByte.class) {
                return 23001;
            }
            if (fromC == Short.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == LongOrShort.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == ShortOrByte.class) {
                return 21001;
            }
            if (fromC == FloatOrInteger.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == FloatOrByte.class) {
                return 23001;
            }
            if (fromC == FloatOrShort.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == BigIntegerOrInteger.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == BigIntegerOrLong.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == BigIntegerOrDouble.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == BigIntegerOrFloat.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == BigIntegerOrByte.class) {
                return 18001;
            }
            if (fromC == IntegerOrShort.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == DoubleOrShort.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == BigIntegerOrShort.class) {
                return Integer.MAX_VALUE;
            }
            return Integer.MAX_VALUE;
        }
        if (toC == Short.class) {
            if (fromC == Integer.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == IntegerBigDecimal.class) {
                return 34002;
            }
            if (fromC == BigDecimal.class) {
                return 44002;
            }
            if (fromC == Long.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == Double.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == Float.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == Byte.class) {
                return 10002;
            }
            if (fromC == BigInteger.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == LongOrInteger.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == DoubleOrFloat.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == DoubleOrIntegerOrFloat.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == DoubleOrInteger.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == DoubleOrLong.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == IntegerOrByte.class) {
                return 21002;
            }
            if (fromC == DoubleOrByte.class) {
                return 24002;
            }
            if (fromC == LongOrByte.class) {
                return 22002;
            }
            if (fromC == LongOrShort.class) {
                return 22002;
            }
            if (fromC == ShortOrByte.class) {
                return 0;
            }
            if (fromC == FloatOrInteger.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == FloatOrByte.class) {
                return 22002;
            }
            if (fromC == FloatOrShort.class) {
                return 22002;
            }
            if (fromC == BigIntegerOrInteger.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == BigIntegerOrLong.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == BigIntegerOrDouble.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == BigIntegerOrFloat.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == BigIntegerOrByte.class) {
                return 17002;
            }
            if (fromC == IntegerOrShort.class) {
                return 21002;
            }
            if (fromC == DoubleOrShort.class) {
                return 24002;
            }
            if (fromC == BigIntegerOrShort.class) {
                return 17002;
            }
            return Integer.MAX_VALUE;
        }
        if (toC == BigDecimal.class) {
            if (fromC == Integer.class) {
                return 20008;
            }
            if (fromC == IntegerBigDecimal.class) {
                return 0;
            }
            if (fromC == Long.class) {
                return 20008;
            }
            if (fromC == Double.class) {
                return 20008;
            }
            if (fromC == Float.class) {
                return 20008;
            }
            if (fromC == Byte.class) {
                return 20008;
            }
            if (fromC == BigInteger.class) {
                return 10008;
            }
            if (fromC == LongOrInteger.class) {
                return 20008;
            }
            if (fromC == DoubleOrFloat.class) {
                return 20008;
            }
            if (fromC == DoubleOrIntegerOrFloat.class) {
                return 20008;
            }
            if (fromC == DoubleOrInteger.class) {
                return 20008;
            }
            if (fromC == DoubleOrLong.class) {
                return 20008;
            }
            if (fromC == IntegerOrByte.class) {
                return 20008;
            }
            if (fromC == DoubleOrByte.class) {
                return 20008;
            }
            if (fromC == LongOrByte.class) {
                return 20008;
            }
            if (fromC == Short.class) {
                return 20008;
            }
            if (fromC == LongOrShort.class) {
                return 20008;
            }
            if (fromC == ShortOrByte.class) {
                return 20008;
            }
            if (fromC == FloatOrInteger.class) {
                return 20008;
            }
            if (fromC == FloatOrByte.class) {
                return 20008;
            }
            if (fromC == FloatOrShort.class) {
                return 20008;
            }
            if (fromC == BigIntegerOrInteger.class) {
                return 10008;
            }
            if (fromC == BigIntegerOrLong.class) {
                return 10008;
            }
            if (fromC == BigIntegerOrDouble.class) {
                return 10008;
            }
            if (fromC == BigIntegerOrFloat.class) {
                return 10008;
            }
            if (fromC == BigIntegerOrByte.class) {
                return 10008;
            }
            if (fromC == IntegerOrShort.class) {
                return 20008;
            }
            if (fromC == DoubleOrShort.class) {
                return 20008;
            }
            if (fromC == BigIntegerOrShort.class) {
                return 10008;
            }
            return Integer.MAX_VALUE;
        }
        if (toC == BigInteger.class) {
            if (fromC == Integer.class) {
                return 10005;
            }
            if (fromC == IntegerBigDecimal.class) {
                return 10005;
            }
            if (fromC == BigDecimal.class) {
                return 40005;
            }
            if (fromC == Long.class) {
                return 10005;
            }
            if (fromC == Double.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == Float.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == Byte.class) {
                return 10005;
            }
            if (fromC == LongOrInteger.class) {
                return 10005;
            }
            if (fromC == DoubleOrFloat.class) {
                return Integer.MAX_VALUE;
            }
            if (fromC == DoubleOrIntegerOrFloat.class) {
                return 21005;
            }
            if (fromC == DoubleOrInteger.class) {
                return 21005;
            }
            if (fromC == DoubleOrLong.class) {
                return 21005;
            }
            if (fromC == IntegerOrByte.class) {
                return 10005;
            }
            if (fromC == DoubleOrByte.class) {
                return 21005;
            }
            if (fromC == LongOrByte.class) {
                return 10005;
            }
            if (fromC == Short.class) {
                return 10005;
            }
            if (fromC == LongOrShort.class) {
                return 10005;
            }
            if (fromC == ShortOrByte.class) {
                return 10005;
            }
            if (fromC == FloatOrInteger.class) {
                return 25005;
            }
            if (fromC == FloatOrByte.class) {
                return 25005;
            }
            if (fromC == FloatOrShort.class) {
                return 25005;
            }
            if (fromC == BigIntegerOrInteger.class) {
                return 0;
            }
            if (fromC == BigIntegerOrLong.class) {
                return 0;
            }
            if (fromC == BigIntegerOrDouble.class) {
                return 0;
            }
            if (fromC == BigIntegerOrFloat.class) {
                return 0;
            }
            if (fromC == BigIntegerOrByte.class) {
                return 0;
            }
            if (fromC == IntegerOrShort.class) {
                return 10005;
            }
            if (fromC == DoubleOrShort.class) {
                return 21005;
            }
            if (fromC == BigIntegerOrShort.class) {
                return 0;
            }
            return Integer.MAX_VALUE;
        }
        return Integer.MAX_VALUE;
    }

    static int compareNumberTypeSpecificity(Class c1, Class c2) {
        if ((c1 = ClassUtil.primitiveClassToBoxingClass(c1)) == (c2 = ClassUtil.primitiveClassToBoxingClass(c2))) {
            return 0;
        }
        if (c1 == Integer.class) {
            if (c2 == Long.class) {
                return 1;
            }
            if (c2 == Double.class) {
                return 4;
            }
            if (c2 == Float.class) {
                return 3;
            }
            if (c2 == Byte.class) {
                return -2;
            }
            if (c2 == Short.class) {
                return -1;
            }
            if (c2 == BigDecimal.class) {
                return 5;
            }
            if (c2 == BigInteger.class) {
                return 2;
            }
            return 0;
        }
        if (c1 == Long.class) {
            if (c2 == Integer.class) {
                return -1;
            }
            if (c2 == Double.class) {
                return 3;
            }
            if (c2 == Float.class) {
                return 2;
            }
            if (c2 == Byte.class) {
                return -3;
            }
            if (c2 == Short.class) {
                return -2;
            }
            if (c2 == BigDecimal.class) {
                return 4;
            }
            if (c2 == BigInteger.class) {
                return 1;
            }
            return 0;
        }
        if (c1 == Double.class) {
            if (c2 == Integer.class) {
                return -4;
            }
            if (c2 == Long.class) {
                return -3;
            }
            if (c2 == Float.class) {
                return -1;
            }
            if (c2 == Byte.class) {
                return -6;
            }
            if (c2 == Short.class) {
                return -5;
            }
            if (c2 == BigDecimal.class) {
                return 1;
            }
            if (c2 == BigInteger.class) {
                return -2;
            }
            return 0;
        }
        if (c1 == Float.class) {
            if (c2 == Integer.class) {
                return -3;
            }
            if (c2 == Long.class) {
                return -2;
            }
            if (c2 == Double.class) {
                return 1;
            }
            if (c2 == Byte.class) {
                return -5;
            }
            if (c2 == Short.class) {
                return -4;
            }
            if (c2 == BigDecimal.class) {
                return 2;
            }
            if (c2 == BigInteger.class) {
                return -1;
            }
            return 0;
        }
        if (c1 == Byte.class) {
            if (c2 == Integer.class) {
                return 2;
            }
            if (c2 == Long.class) {
                return 3;
            }
            if (c2 == Double.class) {
                return 6;
            }
            if (c2 == Float.class) {
                return 5;
            }
            if (c2 == Short.class) {
                return 1;
            }
            if (c2 == BigDecimal.class) {
                return 7;
            }
            if (c2 == BigInteger.class) {
                return 4;
            }
            return 0;
        }
        if (c1 == Short.class) {
            if (c2 == Integer.class) {
                return 1;
            }
            if (c2 == Long.class) {
                return 2;
            }
            if (c2 == Double.class) {
                return 5;
            }
            if (c2 == Float.class) {
                return 4;
            }
            if (c2 == Byte.class) {
                return -1;
            }
            if (c2 == BigDecimal.class) {
                return 6;
            }
            if (c2 == BigInteger.class) {
                return 3;
            }
            return 0;
        }
        if (c1 == BigDecimal.class) {
            if (c2 == Integer.class) {
                return -5;
            }
            if (c2 == Long.class) {
                return -4;
            }
            if (c2 == Double.class) {
                return -1;
            }
            if (c2 == Float.class) {
                return -2;
            }
            if (c2 == Byte.class) {
                return -7;
            }
            if (c2 == Short.class) {
                return -6;
            }
            if (c2 == BigInteger.class) {
                return -3;
            }
            return 0;
        }
        if (c1 == BigInteger.class) {
            if (c2 == Integer.class) {
                return -2;
            }
            if (c2 == Long.class) {
                return -1;
            }
            if (c2 == Double.class) {
                return 2;
            }
            if (c2 == Float.class) {
                return 1;
            }
            if (c2 == Byte.class) {
                return -4;
            }
            if (c2 == Short.class) {
                return -3;
            }
            if (c2 == BigDecimal.class) {
                return 3;
            }
            return 0;
        }
        return 0;
    }

    static final class BigIntegerOrDouble
    extends BigIntegerOrFPPrimitive {
        BigIntegerOrDouble(BigInteger n) {
            super(n);
        }
    }

    static final class BigIntegerOrFloat
    extends BigIntegerOrFPPrimitive {
        BigIntegerOrFloat(BigInteger n) {
            super(n);
        }
    }

    static abstract class BigIntegerOrFPPrimitive
    extends BigIntegerOrPrimitive {
        BigIntegerOrFPPrimitive(BigInteger n) {
            super(n);
        }

        @Override
        public float floatValue() {
            return this.n.longValue();
        }

        @Override
        public double doubleValue() {
            return this.n.longValue();
        }
    }

    static final class BigIntegerOrLong
    extends BigIntegerOrPrimitive {
        BigIntegerOrLong(BigInteger n) {
            super(n);
        }
    }

    static final class BigIntegerOrInteger
    extends BigIntegerOrPrimitive {
        BigIntegerOrInteger(BigInteger n) {
            super(n);
        }
    }

    static final class BigIntegerOrShort
    extends BigIntegerOrPrimitive {
        BigIntegerOrShort(BigInteger n) {
            super(n);
        }
    }

    static final class BigIntegerOrByte
    extends BigIntegerOrPrimitive {
        BigIntegerOrByte(BigInteger n) {
            super(n);
        }
    }

    static abstract class BigIntegerOrPrimitive
    extends NumberWithFallbackType {
        protected final BigInteger n;

        BigIntegerOrPrimitive(BigInteger n) {
            this.n = n;
        }

        @Override
        protected Number getSourceNumber() {
            return this.n;
        }
    }

    static final class FloatOrInteger
    extends FloatOrWholeNumber {
        private final int w;

        FloatOrInteger(Float n, int w) {
            super(n);
            this.w = w;
        }

        @Override
        public int intValue() {
            return this.w;
        }

        @Override
        public long longValue() {
            return this.w;
        }
    }

    static final class FloatOrShort
    extends FloatOrWholeNumber {
        private final short w;

        FloatOrShort(Float n, short w) {
            super(n);
            this.w = w;
        }

        @Override
        public short shortValue() {
            return this.w;
        }

        @Override
        public int intValue() {
            return this.w;
        }

        @Override
        public long longValue() {
            return this.w;
        }
    }

    static final class FloatOrByte
    extends FloatOrWholeNumber {
        private final byte w;

        FloatOrByte(Float n, byte w) {
            super(n);
            this.w = w;
        }

        @Override
        public byte byteValue() {
            return this.w;
        }

        @Override
        public short shortValue() {
            return this.w;
        }

        @Override
        public int intValue() {
            return this.w;
        }

        @Override
        public long longValue() {
            return this.w;
        }
    }

    static abstract class FloatOrWholeNumber
    extends NumberWithFallbackType {
        private final Float n;

        FloatOrWholeNumber(Float n) {
            this.n = n;
        }

        @Override
        protected Number getSourceNumber() {
            return this.n;
        }

        @Override
        public float floatValue() {
            return this.n.floatValue();
        }
    }

    static final class DoubleOrFloat
    extends NumberWithFallbackType {
        private final Double n;

        DoubleOrFloat(Double n) {
            this.n = n;
        }

        @Override
        public float floatValue() {
            return this.n.floatValue();
        }

        @Override
        public double doubleValue() {
            return this.n;
        }

        @Override
        protected Number getSourceNumber() {
            return this.n;
        }
    }

    static final class DoubleOrLong
    extends DoubleOrWholeNumber {
        private final long w;

        DoubleOrLong(Double n, long w) {
            super(n);
            this.w = w;
        }

        @Override
        public long longValue() {
            return this.w;
        }
    }

    static final class DoubleOrInteger
    extends DoubleOrWholeNumber {
        private final int w;

        DoubleOrInteger(Double n, int w) {
            super(n);
            this.w = w;
        }

        @Override
        public int intValue() {
            return this.w;
        }

        @Override
        public long longValue() {
            return this.w;
        }
    }

    static final class DoubleOrIntegerOrFloat
    extends DoubleOrWholeNumber {
        private final int w;

        DoubleOrIntegerOrFloat(Double n, int w) {
            super(n);
            this.w = w;
        }

        @Override
        public int intValue() {
            return this.w;
        }

        @Override
        public long longValue() {
            return this.w;
        }
    }

    static final class DoubleOrShort
    extends DoubleOrWholeNumber {
        private final short w;

        DoubleOrShort(Double n, short w) {
            super(n);
            this.w = w;
        }

        @Override
        public short shortValue() {
            return this.w;
        }

        @Override
        public int intValue() {
            return this.w;
        }

        @Override
        public long longValue() {
            return this.w;
        }
    }

    static final class DoubleOrByte
    extends DoubleOrWholeNumber {
        private final byte w;

        DoubleOrByte(Double n, byte w) {
            super(n);
            this.w = w;
        }

        @Override
        public byte byteValue() {
            return this.w;
        }

        @Override
        public short shortValue() {
            return this.w;
        }

        @Override
        public int intValue() {
            return this.w;
        }

        @Override
        public long longValue() {
            return this.w;
        }
    }

    static abstract class DoubleOrWholeNumber
    extends NumberWithFallbackType {
        private final Double n;

        protected DoubleOrWholeNumber(Double n) {
            this.n = n;
        }

        @Override
        protected Number getSourceNumber() {
            return this.n;
        }

        @Override
        public double doubleValue() {
            return this.n;
        }
    }

    static class ShortOrByte
    extends NumberWithFallbackType {
        private final Short n;
        private final byte w;

        protected ShortOrByte(Short n, byte w) {
            this.n = n;
            this.w = w;
        }

        @Override
        protected Number getSourceNumber() {
            return this.n;
        }

        @Override
        public short shortValue() {
            return this.n;
        }

        @Override
        public byte byteValue() {
            return this.w;
        }
    }

    static class IntegerOrShort
    extends IntegerOrSmallerInteger {
        private final short w;

        IntegerOrShort(Integer n, short w) {
            super(n);
            this.w = w;
        }

        @Override
        public short shortValue() {
            return this.w;
        }
    }

    static class IntegerOrByte
    extends IntegerOrSmallerInteger {
        private final byte w;

        IntegerOrByte(Integer n, byte w) {
            super(n);
            this.w = w;
        }

        @Override
        public byte byteValue() {
            return this.w;
        }
    }

    static abstract class IntegerOrSmallerInteger
    extends NumberWithFallbackType {
        private final Integer n;

        protected IntegerOrSmallerInteger(Integer n) {
            this.n = n;
        }

        @Override
        protected Number getSourceNumber() {
            return this.n;
        }

        @Override
        public int intValue() {
            return this.n;
        }
    }

    static class LongOrInteger
    extends LongOrSmallerInteger {
        private final int w;

        LongOrInteger(Long n, int w) {
            super(n);
            this.w = w;
        }

        @Override
        public int intValue() {
            return this.w;
        }
    }

    static class LongOrShort
    extends LongOrSmallerInteger {
        private final short w;

        LongOrShort(Long n, short w) {
            super(n);
            this.w = w;
        }

        @Override
        public short shortValue() {
            return this.w;
        }
    }

    static class LongOrByte
    extends LongOrSmallerInteger {
        private final byte w;

        LongOrByte(Long n, byte w) {
            super(n);
            this.w = w;
        }

        @Override
        public byte byteValue() {
            return this.w;
        }
    }

    static abstract class LongOrSmallerInteger
    extends NumberWithFallbackType {
        private final Long n;

        protected LongOrSmallerInteger(Long n) {
            this.n = n;
        }

        @Override
        protected Number getSourceNumber() {
            return this.n;
        }

        @Override
        public long longValue() {
            return this.n;
        }
    }

    static final class IntegerBigDecimal
    extends NumberWithFallbackType {
        private final BigDecimal n;

        IntegerBigDecimal(BigDecimal n) {
            this.n = n;
        }

        @Override
        protected Number getSourceNumber() {
            return this.n;
        }

        public BigInteger bigIntegerValue() {
            return this.n.toBigInteger();
        }
    }

    static abstract class NumberWithFallbackType
    extends Number
    implements Comparable {
        NumberWithFallbackType() {
        }

        protected abstract Number getSourceNumber();

        @Override
        public int intValue() {
            return this.getSourceNumber().intValue();
        }

        @Override
        public long longValue() {
            return this.getSourceNumber().longValue();
        }

        @Override
        public float floatValue() {
            return this.getSourceNumber().floatValue();
        }

        @Override
        public double doubleValue() {
            return this.getSourceNumber().doubleValue();
        }

        @Override
        public byte byteValue() {
            return this.getSourceNumber().byteValue();
        }

        @Override
        public short shortValue() {
            return this.getSourceNumber().shortValue();
        }

        public int hashCode() {
            return this.getSourceNumber().hashCode();
        }

        public boolean equals(Object obj) {
            if (obj != null && this.getClass() == obj.getClass()) {
                return this.getSourceNumber().equals(((NumberWithFallbackType)obj).getSourceNumber());
            }
            return false;
        }

        public String toString() {
            return this.getSourceNumber().toString();
        }

        public int compareTo(Object o) {
            Number n = this.getSourceNumber();
            if (n instanceof Comparable) {
                return ((Comparable)((Object)n)).compareTo(o);
            }
            throw new ClassCastException(n.getClass().getName() + " is not Comparable.");
        }
    }

    static interface BigDecimalSource {
        public BigDecimal bigDecimalValue();
    }

    static interface BigIntegerSource {
        public BigInteger bigIntegerValue();
    }

    static interface DoubleSource {
        public Double doubleValue();
    }

    static interface FloatSource {
        public Float floatValue();
    }

    static interface LongSource {
        public Long longValue();
    }

    static interface IntegerSource {
        public Integer integerValue();
    }

    static interface ShortSource {
        public Short shortValue();
    }

    static interface ByteSource {
        public Byte byteValue();
    }
}


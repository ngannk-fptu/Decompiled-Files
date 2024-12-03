/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlgraphics.util;

public final class DoubleFormatUtil {
    private static final long[] POWERS_OF_TEN_LONG;
    private static final double[] POWERS_OF_TEN_DOUBLE;

    private DoubleFormatUtil() {
    }

    public static void formatDouble(double source, int decimals, int precision, StringBuffer target) {
        int scale;
        int n = scale = Math.abs(source) >= 1.0 ? decimals : precision;
        if (DoubleFormatUtil.tooManyDigitsUsed(source, scale) || DoubleFormatUtil.tooCloseToRound(source, scale)) {
            DoubleFormatUtil.formatDoublePrecise(source, decimals, precision, target);
        } else {
            DoubleFormatUtil.formatDoubleFast(source, decimals, precision, target);
        }
    }

    public static void formatDoublePrecise(double source, int decimals, int precision, StringBuffer target) {
        boolean negative;
        if (DoubleFormatUtil.isRoundedToZero(source, decimals, precision)) {
            target.append('0');
            return;
        }
        if (Double.isNaN(source) || Double.isInfinite(source)) {
            target.append(Double.toString(source));
            return;
        }
        boolean bl = negative = source < 0.0;
        if (negative) {
            source = -source;
            target.append('-');
        }
        int scale = source >= 1.0 ? decimals : precision;
        String s = Double.toString(source);
        if (source >= 0.001 && source < 1.0E7) {
            int dot = s.indexOf(46);
            String decS = s.substring(dot + 1);
            int decLength = decS.length();
            if (scale >= decLength) {
                if ("0".equals(decS)) {
                    target.append(s.substring(0, dot));
                } else {
                    target.append(s);
                    for (int l = target.length() - 1; l >= 0 && target.charAt(l) == '0'; --l) {
                        target.setLength(l);
                    }
                }
                return;
            }
            if (scale + 1 < decLength) {
                decLength = scale + 1;
                decS = decS.substring(0, decLength);
            }
            long intP = Long.parseLong(s.substring(0, dot));
            long decP = Long.parseLong(decS);
            DoubleFormatUtil.format(target, scale, intP, decP);
        } else {
            int dot = s.indexOf(46);
            assert (dot >= 0);
            int exp = s.indexOf(69);
            assert (exp >= 0);
            int exposant = Integer.parseInt(s.substring(exp + 1));
            String intS = s.substring(0, dot);
            String decS = s.substring(dot + 1, exp);
            int decLength = decS.length();
            if (exposant >= 0) {
                int digits = decLength - exposant;
                if (digits <= 0) {
                    target.append(intS);
                    target.append(decS);
                    for (int i = -digits; i > 0; --i) {
                        target.append('0');
                    }
                } else if (digits <= scale) {
                    target.append(intS);
                    target.append(decS.substring(0, exposant));
                    target.append('.');
                    target.append(decS.substring(exposant));
                } else {
                    long intP = Long.parseLong(intS) * DoubleFormatUtil.tenPow(exposant) + Long.parseLong(decS.substring(0, exposant));
                    long decP = Long.parseLong(decS.substring(exposant, exposant + scale + 1));
                    DoubleFormatUtil.format(target, scale, intP, decP);
                }
            } else {
                int digits = scale - (exposant = -exposant) + 1;
                if (digits < 0) {
                    target.append('0');
                } else if (digits == 0) {
                    long decP = Long.parseLong(intS);
                    DoubleFormatUtil.format(target, scale, 0L, decP);
                } else if (decLength < digits) {
                    long decP = Long.parseLong(intS) * DoubleFormatUtil.tenPow(decLength + 1) + Long.parseLong(decS) * 10L;
                    DoubleFormatUtil.format(target, exposant + decLength, 0L, decP);
                } else {
                    long subDecP = Long.parseLong(decS.substring(0, digits));
                    long decP = Long.parseLong(intS) * DoubleFormatUtil.tenPow(digits) + subDecP;
                    DoubleFormatUtil.format(target, scale, 0L, decP);
                }
            }
        }
    }

    private static boolean isRoundedToZero(double source, int decimals, int precision) {
        return source == 0.0 || Math.abs(source) < 4.999999999999999 / DoubleFormatUtil.tenPowDouble(Math.max(decimals, precision) + 1);
    }

    public static long tenPow(int n) {
        assert (n >= 0);
        return n < POWERS_OF_TEN_LONG.length ? POWERS_OF_TEN_LONG[n] : (long)Math.pow(10.0, n);
    }

    private static double tenPowDouble(int n) {
        assert (n >= 0);
        return n < POWERS_OF_TEN_DOUBLE.length ? POWERS_OF_TEN_DOUBLE[n] : Math.pow(10.0, n);
    }

    private static void format(StringBuffer target, int scale, long intP, long decP) {
        if (decP != 0L) {
            decP += 5L;
            if ((double)(decP /= 10L) >= DoubleFormatUtil.tenPowDouble(scale)) {
                ++intP;
                decP -= DoubleFormatUtil.tenPow(scale);
            }
            if (decP != 0L) {
                while (decP % 10L == 0L) {
                    decP /= 10L;
                    --scale;
                }
            }
        }
        target.append(intP);
        if (decP != 0L) {
            target.append('.');
            while (scale > 0 && (scale > 18 ? (double)decP < DoubleFormatUtil.tenPowDouble(--scale) : decP < DoubleFormatUtil.tenPow(--scale))) {
                target.append('0');
            }
            target.append(decP);
        }
    }

    public static void formatDoubleFast(double source, int decimals, int precision, StringBuffer target) {
        double tenScale;
        if (DoubleFormatUtil.isRoundedToZero(source, decimals, precision)) {
            target.append('0');
            return;
        }
        if (Double.isNaN(source) || Double.isInfinite(source)) {
            target.append(Double.toString(source));
            return;
        }
        boolean isPositive = source >= 0.0;
        source = Math.abs(source);
        int scale = source >= 1.0 ? decimals : precision;
        long intPart = (long)Math.floor(source);
        double fracUnroundedPart = (source - (double)intPart) * (tenScale = DoubleFormatUtil.tenPowDouble(scale));
        long fracPart = Math.round(fracUnroundedPart);
        if ((double)fracPart >= tenScale) {
            ++intPart;
            fracPart = Math.round((double)fracPart - tenScale);
        }
        if (fracPart != 0L) {
            while (fracPart % 10L == 0L) {
                fracPart /= 10L;
                --scale;
            }
        }
        if (intPart != 0L || fracPart != 0L) {
            if (!isPositive) {
                target.append('-');
            }
            target.append(intPart);
            if (fracPart != 0L) {
                target.append('.');
                while (scale > 0 && (double)fracPart < DoubleFormatUtil.tenPowDouble(--scale)) {
                    target.append('0');
                }
                target.append(fracPart);
            }
        } else {
            target.append('0');
        }
    }

    public static int getExponant(double value) {
        long exp = Double.doubleToRawLongBits(value) & 0x7FF0000000000000L;
        return (int)((exp >>= 52) - 1023L);
    }

    private static boolean tooManyDigitsUsed(double source, int scale) {
        double decExp = Math.log10(source);
        return scale >= 308 || decExp + (double)scale >= 14.5;
    }

    private static boolean tooCloseToRound(double source, int scale) {
        source = Math.abs(source);
        long intPart = (long)Math.floor(source);
        double fracPart = (source - (double)intPart) * DoubleFormatUtil.tenPowDouble(scale);
        double decExp = Math.log10(source);
        double range = decExp + (double)scale >= 12.0 ? 0.1 : 0.001;
        double distanceToRound1 = Math.abs(fracPart - Math.floor(fracPart));
        double distanceToRound2 = Math.abs(fracPart - Math.floor(fracPart) - 0.5);
        return distanceToRound1 <= range || distanceToRound2 <= range;
    }

    static {
        int i;
        POWERS_OF_TEN_LONG = new long[19];
        POWERS_OF_TEN_DOUBLE = new double[30];
        DoubleFormatUtil.POWERS_OF_TEN_LONG[0] = 1L;
        for (i = 1; i < POWERS_OF_TEN_LONG.length; ++i) {
            DoubleFormatUtil.POWERS_OF_TEN_LONG[i] = POWERS_OF_TEN_LONG[i - 1] * 10L;
        }
        for (i = 0; i < POWERS_OF_TEN_DOUBLE.length; ++i) {
            DoubleFormatUtil.POWERS_OF_TEN_DOUBLE[i] = Double.parseDouble("1e" + i);
        }
    }
}


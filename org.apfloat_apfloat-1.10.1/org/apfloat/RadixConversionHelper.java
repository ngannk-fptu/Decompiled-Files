/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat;

import java.util.ArrayList;
import java.util.List;
import org.apfloat.Apfloat;
import org.apfloat.ApfloatHelper;
import org.apfloat.ApfloatMath;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.Apint;
import org.apfloat.spi.RadixConstants;
import org.apfloat.spi.Util;

class RadixConversionHelper {
    private RadixConversionHelper() {
    }

    public static Apfloat toRadix(Apfloat x, int toRadix) throws ApfloatRuntimeException {
        if (x.radix() == toRadix) {
            return x;
        }
        if (x.signum() == 0) {
            return new Apfloat(0L, (long)toRadix);
        }
        int fromRadix = x.radix();
        long size = x.size();
        long scale = x.scale();
        long precision = RadixConversionHelper.getPrecision(x.precision(), fromRadix, toRadix);
        RadixPowerList radixPowerList = new RadixPowerList(fromRadix, toRadix, precision);
        return RadixConversionHelper.toRadixIntegerPart(x, toRadix, size, scale, radixPowerList).add(RadixConversionHelper.toRadixFractionalPart(x, toRadix, size, scale, radixPowerList)).precision(precision);
    }

    private static Apfloat toRadixIntegerPart(Apfloat x, int toRadix, long size, long scale, RadixPowerList radixPowerList) throws ApfloatRuntimeException {
        if (scale <= 0L) {
            return Apfloat.ZERO;
        }
        if (scale > size) {
            long shift = scale - size;
            x = ApfloatMath.scale(x, -shift);
            x = RadixConversionHelper.toRadixNormalizedPart(x, toRadix, size, radixPowerList);
            return x.multiply(radixPowerList.pow(shift));
        }
        x = x.truncate();
        return RadixConversionHelper.toRadixNormalizedPart(x, toRadix, x.scale(), radixPowerList);
    }

    private static Apfloat toRadixFractionalPart(Apfloat x, int toRadix, long size, long scale, RadixPowerList radixPowerList) throws ApfloatRuntimeException {
        if (size > scale) {
            if (scale > 0L) {
                x = x.frac();
                size -= scale;
                scale = 0L;
            }
            long precision = RadixConversionHelper.getPrecision(x.precision(), x.radix(), toRadix);
            long shift = size - scale;
            x = ApfloatMath.scale(x, shift);
            x = RadixConversionHelper.toRadixNormalizedPart(x, toRadix, size, radixPowerList);
            return x.precision(precision).divide(radixPowerList.pow(shift));
        }
        return Apfloat.ZERO;
    }

    private static Apfloat toRadixNormalizedPart(Apfloat x, int toRadix, long size, RadixPowerList radixPowerList) throws ApfloatRuntimeException {
        long maxPow2 = Util.round2down(size);
        return RadixConversionHelper.split(x, toRadix, size, maxPow2, radixPowerList);
    }

    private static Apfloat split(Apfloat x, int toRadix, long size, long split, RadixPowerList radixPowerList) throws ApfloatRuntimeException {
        if (size <= 0L) {
            return Apfloat.ZERO;
        }
        if (size <= (long)RadixConstants.LONG_DIGITS[x.radix()]) {
            return new Apfloat(x.longValue(), Long.MAX_VALUE, toRadix);
        }
        x = ApfloatMath.scale(x, -split);
        Apint top = x.truncate();
        Apfloat bottom = ApfloatMath.scale(x.frac(), split);
        return RadixConversionHelper.split(top, toRadix, size - split, split >> 1, radixPowerList).multiply(radixPowerList.pow(split)).add(RadixConversionHelper.split(bottom, toRadix, split, split >> 1, radixPowerList));
    }

    private static long getPrecision(long precision, int fromRadix, int toRadix) throws ApfloatRuntimeException {
        long newPrecision = (long)((double)precision * Math.log(fromRadix) / Math.log(toRadix));
        if (fromRadix < toRadix) {
            newPrecision = Math.max(1L, newPrecision);
        }
        return Util.ifFinite(precision, newPrecision);
    }

    private static class RadixPowerList {
        private List<Apfloat> list = new ArrayList<Apfloat>();

        public RadixPowerList(int fromRadix, int toRadix, long precision) throws ApfloatRuntimeException {
            this.list.add(new Apfloat(fromRadix, ApfloatHelper.extendPrecision(precision), toRadix));
        }

        public Apfloat pow(long n) throws ApfloatRuntimeException {
            if (n == 0L) {
                return Apfloat.ONE;
            }
            int p = 0;
            while ((n & 1L) == 0L) {
                ++p;
                n >>>= 1;
            }
            Apfloat r = this.get(p);
            while ((n >>>= 1) > 0L) {
                Apfloat x = this.get(++p);
                if ((n & 1L) == 0L) continue;
                r = r.multiply(x);
            }
            return r;
        }

        private Apfloat get(int index) throws ApfloatRuntimeException {
            Apfloat x;
            if (this.list.size() > index) {
                x = this.list.get(index);
            } else {
                x = this.get(index - 1);
                x = x.multiply(x);
                this.list.add(x);
            }
            return x;
        }
    }
}


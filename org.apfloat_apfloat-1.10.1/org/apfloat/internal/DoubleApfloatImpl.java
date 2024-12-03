/*
 * Decompiled with CFR 0.152.
 */
package org.apfloat.internal;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PushbackReader;
import java.io.StringWriter;
import java.io.Writer;
import org.apfloat.ApfloatContext;
import org.apfloat.ApfloatRuntimeException;
import org.apfloat.InfiniteExpansionException;
import org.apfloat.OverflowException;
import org.apfloat.internal.ApfloatInternalException;
import org.apfloat.internal.DoubleBaseMath;
import org.apfloat.internal.DoubleRadixConstants;
import org.apfloat.internal.ImplementationMismatchException;
import org.apfloat.internal.RadixMismatchException;
import org.apfloat.spi.AdditionBuilder;
import org.apfloat.spi.AdditionStrategy;
import org.apfloat.spi.ApfloatImpl;
import org.apfloat.spi.ArrayAccess;
import org.apfloat.spi.ConvolutionBuilder;
import org.apfloat.spi.ConvolutionStrategy;
import org.apfloat.spi.DataStorage;
import org.apfloat.spi.DataStorageBuilder;
import org.apfloat.spi.RadixConstants;
import org.apfloat.spi.Util;

public class DoubleApfloatImpl
extends DoubleBaseMath
implements ApfloatImpl {
    private static final DataStorage.Iterator ZERO_ITERATOR = new DataStorage.Iterator(){
        private static final long serialVersionUID = 1L;

        @Override
        public double getDouble() {
            return 0.0;
        }

        @Override
        public void next() {
        }
    };
    private static final long serialVersionUID = -4177541592360478544L;
    private static final int UNDEFINED = Integer.MIN_VALUE;
    private static final int MAX_LONG_SIZE = 4;
    private static final int MAX_DOUBLE_SIZE = 4;
    private int sign;
    private long precision;
    private long exponent;
    private DataStorage dataStorage;
    private int radix;
    private int hashCode = 0;
    private int initialDigits = Integer.MIN_VALUE;
    private int isOne = Integer.MIN_VALUE;
    private volatile long leastZeros = Integer.MIN_VALUE;
    private volatile long size = 0L;

    private DoubleApfloatImpl(int sign, long precision, long exponent, DataStorage dataStorage, int radix) {
        super(radix);
        assert (sign == 0 || sign == -1 || sign == 1);
        assert (precision > 0L);
        assert (sign != 0 || precision == Long.MAX_VALUE);
        assert (sign != 0 || exponent == 0L);
        assert (sign != 0 || dataStorage == null);
        assert (sign == 0 || dataStorage != null);
        assert (exponent <= DoubleRadixConstants.MAX_EXPONENT[radix] && exponent >= -DoubleRadixConstants.MAX_EXPONENT[radix]);
        assert (dataStorage == null || dataStorage.isReadOnly());
        this.sign = sign;
        this.precision = precision;
        this.exponent = exponent;
        this.dataStorage = dataStorage;
        this.radix = radix;
    }

    public DoubleApfloatImpl(String value, long precision, int radix, boolean isInteger) throws NumberFormatException, ApfloatRuntimeException {
        super(DoubleApfloatImpl.checkRadix(radix));
        int slack;
        assert (precision == Long.MIN_VALUE || precision > 0L);
        this.radix = radix;
        this.sign = 1;
        int startIndex = -1;
        int pointIndex = -1;
        int expIndex = -1;
        int leadingZeros = 0;
        int trailingZeros = 0;
        int digitSize = 0;
        for (int i = 0; i < value.length(); ++i) {
            char c = value.charAt(i);
            int digit = Character.digit(c, radix);
            if (digit == -1) {
                if (i == 0 && (c == '-' || c == '+')) {
                    this.sign = c == '-' ? -1 : 1;
                    continue;
                }
                if (!isInteger && c == '.' && pointIndex == -1) {
                    pointIndex = digitSize;
                    continue;
                }
                if (!(isInteger || c != 'e' && c != 'E' || expIndex != -1)) {
                    expIndex = i;
                    break;
                }
                throw new NumberFormatException("Invalid character: " + c + " at position " + i);
            }
            if (leadingZeros == digitSize && digit == 0) {
                ++leadingZeros;
            } else if (startIndex == -1) {
                startIndex = i;
            }
            ++digitSize;
            if (digit == 0) {
                ++trailingZeros;
                continue;
            }
            trailingZeros = 0;
        }
        if (digitSize == 0) {
            throw new NumberFormatException("No digits");
        }
        if (startIndex == -1) {
            this.sign = 0;
            this.precision = Long.MAX_VALUE;
            this.exponent = 0L;
            this.dataStorage = null;
            return;
        }
        if (precision == Long.MIN_VALUE) {
            assert (!isInteger);
            precision = digitSize - leadingZeros;
        }
        this.precision = precision;
        int integerSize = (pointIndex >= 0 ? pointIndex : digitSize) - leadingZeros;
        if (expIndex >= 0) {
            String expString = value.substring(expIndex + 1);
            if (expString.startsWith("+")) {
                expString = expString.substring(1);
            }
            try {
                this.exponent = Long.parseLong(expString);
            }
            catch (NumberFormatException nfe) {
                throw new NumberFormatException("Invalid exponent: " + expString);
            }
        } else {
            this.exponent = 0L;
        }
        if (integerSize >= -(slack = DoubleRadixConstants.BASE_DIGITS[radix]) && this.exponent >= Long.MAX_VALUE - (long)integerSize - (long)slack) {
            throw new NumberFormatException("Exponent overflow");
        }
        if (integerSize <= slack && this.exponent <= Long.MIN_VALUE - (long)integerSize + (long)slack) {
            this.sign = 0;
            this.precision = Long.MAX_VALUE;
            this.exponent = 0L;
            this.dataStorage = null;
            return;
        }
        this.exponent += (long)integerSize;
        long baseExp = (this.exponent + (long)(this.exponent > 0L ? DoubleRadixConstants.BASE_DIGITS[radix] - 1 : 0)) / (long)DoubleRadixConstants.BASE_DIGITS[radix];
        if (baseExp > DoubleRadixConstants.MAX_EXPONENT[this.radix]) {
            throw new OverflowException("Overflow");
        }
        if (baseExp < -DoubleRadixConstants.MAX_EXPONENT[this.radix]) {
            this.sign = 0;
            this.precision = Long.MAX_VALUE;
            this.exponent = 0L;
            this.dataStorage = null;
            return;
        }
        int digitsInBase = (int)(baseExp * (long)DoubleRadixConstants.BASE_DIGITS[radix] - this.exponent);
        this.exponent = baseExp;
        digitSize -= leadingZeros + trailingZeros;
        digitSize = (int)Math.min((long)digitSize, precision);
        int size = (int)this.getBasePrecision(digitSize, DoubleRadixConstants.BASE_DIGITS[radix] - digitsInBase);
        this.dataStorage = DoubleApfloatImpl.createDataStorage(size);
        this.dataStorage.setSize(size);
        double word = 0.0;
        DataStorage.Iterator iterator = this.dataStorage.iterator(2, 0L, size);
        int i = startIndex;
        while (digitSize > 0) {
            char c = value.charAt(i);
            if (c != '.') {
                int digit = Character.digit(c, radix);
                word *= (double)radix;
                word += (double)digit;
                if (digitSize == 1) {
                    while (digitsInBase < DoubleRadixConstants.BASE_DIGITS[radix] - 1) {
                        word *= (double)radix;
                        ++digitsInBase;
                    }
                }
                if (++digitsInBase == DoubleRadixConstants.BASE_DIGITS[radix]) {
                    digitsInBase = 0;
                    iterator.setDouble(word);
                    iterator.next();
                    word = 0.0;
                }
                --digitSize;
            }
            ++i;
        }
        assert (!iterator.hasNext());
        this.dataStorage.setReadOnly();
    }

    public DoubleApfloatImpl(long value, long precision, int radix) throws NumberFormatException, ApfloatRuntimeException {
        super(DoubleApfloatImpl.checkRadix(radix));
        int size;
        assert (precision > 0L);
        this.radix = radix;
        int n = this.isOne = value == 1L ? 1 : 0;
        if (value > 0L) {
            this.sign = 1;
            value = -value;
        } else if (value < 0L) {
            this.sign = -1;
        } else {
            this.sign = 0;
            this.precision = Long.MAX_VALUE;
            this.exponent = 0L;
            this.dataStorage = null;
            return;
        }
        this.precision = precision;
        double[] data = new double[4];
        long longBase = (long)DoubleRadixConstants.BASE[radix];
        if (-longBase < value) {
            size = 1;
            data[3] = -value;
        } else {
            size = 0;
            while (value != 0L) {
                long newValue = value / longBase;
                data[3 - size] = newValue * longBase - value;
                value = newValue;
                ++size;
            }
        }
        this.exponent = size;
        this.initialDigits = this.getDigits(data[4 - size]);
        long basePrecision = this.getBasePrecision(precision, this.initialDigits);
        if (basePrecision < (long)size) {
            size = (int)basePrecision;
        }
        while (data[3 - (int)this.exponent + size] == 0.0) {
            --size;
        }
        this.dataStorage = DoubleApfloatImpl.createDataStorage(size);
        this.dataStorage.setSize(size);
        try (ArrayAccess arrayAccess = this.dataStorage.getArray(2, 0L, size);){
            System.arraycopy(data, 4 - (int)this.exponent, arrayAccess.getData(), arrayAccess.getOffset(), size);
        }
        this.dataStorage.setReadOnly();
    }

    public DoubleApfloatImpl(double value, long precision, int radix) throws NumberFormatException, ApfloatRuntimeException {
        super(DoubleApfloatImpl.checkRadix(radix));
        int size;
        if (Double.isInfinite(value) || Double.isNaN(value)) {
            throw new NumberFormatException(value + " is not a valid number");
        }
        this.radix = radix;
        if (value > 0.0) {
            this.sign = 1;
        } else if (value < 0.0) {
            this.sign = -1;
            value = -value;
        } else {
            this.sign = 0;
            this.precision = Long.MAX_VALUE;
            this.exponent = 0L;
            this.dataStorage = null;
            return;
        }
        this.precision = precision;
        double[] data = new double[4];
        double doubleBase = DoubleRadixConstants.BASE[radix];
        this.exponent = (long)Math.floor(Math.log(value) / Math.log(doubleBase));
        if (this.exponent > 0L) {
            value *= Math.pow(doubleBase, -this.exponent);
        } else if (this.exponent < 0L) {
            value *= Math.pow(doubleBase, -this.exponent - 4L);
            value *= Math.pow(doubleBase, 4.0);
        }
        ++this.exponent;
        if (value < 1.0) {
            value = 1.0;
        }
        for (size = 0; size < 4 && value > 0.0; value *= doubleBase, ++size) {
            double tmp = Math.floor(value);
            assert (tmp <= doubleBase);
            if (tmp == doubleBase) {
                tmp -= 1.0;
            }
            data[size] = tmp;
            value -= tmp;
        }
        this.initialDigits = this.getDigits(data[0]);
        long basePrecision = this.getBasePrecision(precision, this.initialDigits);
        if (basePrecision < (long)size) {
            size = (int)basePrecision;
        }
        while (data[size - 1] == 0.0) {
            --size;
        }
        this.dataStorage = DoubleApfloatImpl.createDataStorage(size);
        this.dataStorage.setSize(size);
        try (ArrayAccess arrayAccess = this.dataStorage.getArray(2, 0L, size);){
            System.arraycopy(data, 0, arrayAccess.getData(), arrayAccess.getOffset(), size);
        }
        this.dataStorage.setReadOnly();
    }

    private static long readExponent(PushbackReader in) throws IOException, NumberFormatException {
        int input;
        StringBuilder buffer = new StringBuilder(20);
        long i = 0L;
        while ((input = in.read()) != -1) {
            char c = (char)input;
            int digit = Character.digit(c, 10);
            if ((i != 0L || c != '-') && digit == -1) {
                in.unread(input);
                break;
            }
            buffer.append(c);
            ++i;
        }
        return Long.parseLong(buffer.toString());
    }

    /*
     * Enabled aggressive block sorting
     */
    public DoubleApfloatImpl(PushbackReader in, long precision, int radix, boolean isInteger) throws IOException, NumberFormatException, ApfloatRuntimeException {
        super(DoubleApfloatImpl.checkRadix(radix));
        int slack;
        int input;
        assert (precision == Long.MIN_VALUE || precision > 0L);
        this.radix = radix;
        this.sign = 1;
        long initialSize = DoubleApfloatImpl.getBlockSize();
        long previousAllocatedSize = 0L;
        long allocatedSize = initialSize;
        this.dataStorage = DoubleApfloatImpl.createDataStorage(initialSize);
        this.dataStorage.setSize(initialSize);
        double word = 0.0;
        int digitsInBase = 0;
        DataStorage.Iterator iterator = this.dataStorage.iterator(2, previousAllocatedSize, allocatedSize);
        long actualSize = 0L;
        long startIndex = -1L;
        long pointIndex = -1L;
        long leadingZeros = 0L;
        long trailingZeros = 0L;
        long digitSize = 0L;
        long i = 0L;
        while ((input = in.read()) != -1) {
            block25: {
                int digit;
                block26: {
                    char c = (char)input;
                    digit = Character.digit(c, radix);
                    if (digit != -1) break block26;
                    if (i == 0L && (c == '-' || c == '+')) {
                        this.sign = c == '-' ? -1 : 1;
                        break block25;
                    } else if (!isInteger && c == '.' && pointIndex == -1L) {
                        pointIndex = digitSize;
                        break block25;
                    } else {
                        if (!(isInteger || digitSize <= 0L || c != 'e' && c != 'E')) {
                            this.exponent = DoubleApfloatImpl.readExponent(in);
                            break;
                        }
                        in.unread(input);
                        break;
                    }
                }
                if (leadingZeros == digitSize && digit == 0) {
                    ++leadingZeros;
                } else {
                    if (startIndex == -1L) {
                        startIndex = i;
                    }
                    word *= (double)radix;
                    word += (double)digit;
                    if (actualSize == allocatedSize) {
                        if (actualSize == initialSize) {
                            DataStorage dataStorage = DoubleApfloatImpl.createDataStorage(0xFFFFFFFFFFFFFFFL);
                            dataStorage.copyFrom(this.dataStorage, actualSize);
                            this.dataStorage = dataStorage;
                        }
                        previousAllocatedSize = allocatedSize;
                        this.dataStorage.setSize(allocatedSize += (long)DoubleApfloatImpl.getBlockSize());
                        iterator.close();
                        iterator = this.dataStorage.iterator(2, previousAllocatedSize, allocatedSize);
                    }
                    if (++digitsInBase == DoubleRadixConstants.BASE_DIGITS[radix]) {
                        digitsInBase = 0;
                        iterator.setDouble(word);
                        iterator.next();
                        word = 0.0;
                        ++actualSize;
                    }
                }
                ++digitSize;
                trailingZeros = digit == 0 ? ++trailingZeros : 0L;
            }
            ++i;
        }
        if (digitSize == 0L) {
            throw new NumberFormatException("No digits");
        }
        if (startIndex == -1L) {
            this.sign = 0;
            this.precision = Long.MAX_VALUE;
            this.exponent = 0L;
            this.dataStorage = null;
            return;
        }
        if (digitsInBase > 0 && word != 0.0) {
            while (digitsInBase < DoubleRadixConstants.BASE_DIGITS[radix]) {
                word *= (double)radix;
                ++digitsInBase;
            }
            iterator.setDouble(word);
            ++actualSize;
        }
        iterator.close();
        if (precision == Long.MIN_VALUE) {
            assert (!isInteger);
            precision = digitSize - leadingZeros;
        }
        this.precision = precision;
        long integerSize = (pointIndex >= 0L ? pointIndex : digitSize) - leadingZeros;
        if (integerSize >= (long)(-(slack = DoubleRadixConstants.BASE_DIGITS[radix])) && this.exponent >= Long.MAX_VALUE - integerSize - (long)slack) {
            throw new NumberFormatException("Exponent overflow");
        }
        if (integerSize <= (long)slack && this.exponent <= Long.MIN_VALUE - integerSize + (long)slack) {
            this.sign = 0;
            this.precision = Long.MAX_VALUE;
            this.exponent = 0L;
            this.dataStorage = null;
            return;
        }
        this.exponent += integerSize;
        long baseExp = (this.exponent - (long)(this.exponent < 0L ? DoubleRadixConstants.BASE_DIGITS[radix] - 1 : 0)) / (long)DoubleRadixConstants.BASE_DIGITS[radix];
        if (baseExp > DoubleRadixConstants.MAX_EXPONENT[this.radix]) {
            throw new OverflowException("Overflow");
        }
        if (baseExp < -DoubleRadixConstants.MAX_EXPONENT[this.radix]) {
            this.sign = 0;
            this.precision = Long.MAX_VALUE;
            this.exponent = 0L;
            this.dataStorage = null;
            return;
        }
        int bias = (int)(this.exponent - baseExp * (long)DoubleRadixConstants.BASE_DIGITS[radix]);
        this.exponent = baseExp;
        digitSize -= leadingZeros + trailingZeros;
        digitSize = Math.min(digitSize, precision);
        actualSize = (digitSize + (long)DoubleRadixConstants.BASE_DIGITS[radix] - 1L) / (long)DoubleRadixConstants.BASE_DIGITS[radix];
        this.dataStorage.setSize(actualSize);
        this.dataStorage.setReadOnly();
        if (bias != 0) {
            long factor = 1L;
            for (int i2 = 0; i2 < bias; factor *= (long)radix, ++i2) {
            }
            DoubleApfloatImpl tmp = (DoubleApfloatImpl)this.multiply(new DoubleApfloatImpl(factor, Long.MAX_VALUE, radix));
            this.exponent = tmp.exponent;
            this.dataStorage = tmp.dataStorage;
            this.initialDigits = Integer.MIN_VALUE;
        }
    }

    private static long getTrailingZeros(DataStorage dataStorage, long index) throws ApfloatRuntimeException {
        long count = 0L;
        try (DataStorage.Iterator iterator = dataStorage.iterator(1, index, 0L);){
            while (iterator.hasNext()) {
                if (iterator.getDouble() != 0.0) {
                    break;
                }
                iterator.next();
                ++count;
            }
        }
        return count;
    }

    private static long getLeadingZeros(DataStorage dataStorage, long index) throws ApfloatRuntimeException {
        long count = 0L;
        try (DataStorage.Iterator iterator = dataStorage.iterator(1, index, dataStorage.getSize());){
            while (iterator.hasNext()) {
                if (iterator.getDouble() != 0.0) {
                    break;
                }
                iterator.next();
                ++count;
            }
        }
        return count;
    }

    @Override
    public ApfloatImpl addOrSubtract(ApfloatImpl x, boolean subtract) throws ApfloatRuntimeException {
        DataStorage dataStorage;
        long precision;
        long exponent;
        int sign;
        if (!(x instanceof DoubleApfloatImpl)) {
            throw new ImplementationMismatchException("Wrong operand type: " + x.getClass().getName());
        }
        DoubleApfloatImpl that = (DoubleApfloatImpl)x;
        if (this.radix != that.radix) {
            throw new RadixMismatchException("Cannot use numbers with different radixes: " + this.radix + " and " + that.radix);
        }
        assert (this.sign != 0);
        assert (that.sign != 0);
        int realThatSign = subtract ? -that.sign : that.sign;
        boolean reallySubtract = this.sign != realThatSign;
        ApfloatContext ctx = ApfloatContext.getContext();
        AdditionBuilder<Double> additionBuilder = ctx.getBuilderFactory().getAdditionBuilder(Double.TYPE);
        AdditionStrategy<Double> additionStrategy = additionBuilder.createAddition(this.radix);
        if (this == that) {
            double carry;
            if (reallySubtract) {
                return this.zero();
            }
            sign = this.sign;
            exponent = this.exponent;
            precision = this.precision;
            long size = this.getSize() + 1L;
            dataStorage = DoubleApfloatImpl.createDataStorage(size);
            dataStorage.setSize(size);
            DataStorage.Iterator src1 = this.dataStorage.iterator(1, size - 1L, 0L);
            DataStorage.Iterator src2 = this.dataStorage.iterator(1, size - 1L, 0L);
            try (DataStorage.Iterator dst = dataStorage.iterator(2, size, 0L);){
                carry = additionStrategy.add(src1, src2, 0.0, dst, size - 1L);
                dst.setDouble(carry);
            }
            size -= DoubleApfloatImpl.getTrailingZeros(dataStorage, size);
            int carrySize = (int)carry;
            int leadingZeros = 1 - carrySize;
            dataStorage = dataStorage.subsequence(leadingZeros, size - (long)leadingZeros);
            exponent += (long)carrySize;
            if (this.exponent == DoubleRadixConstants.MAX_EXPONENT[this.radix] && carrySize > 0) {
                throw new OverflowException("Overflow");
            }
            if (precision != Long.MAX_VALUE && (carrySize > 0 || this.getInitialDigits(dataStorage) > this.getInitialDigits())) {
                ++precision;
            }
        } else {
            long leadingZeros;
            long exponentDifference;
            long size;
            long smallSize;
            long bigSize;
            DoubleApfloatImpl small;
            DoubleApfloatImpl big;
            int comparison = this.scale() > that.scale() ? 1 : (this.scale() < that.scale() ? -1 : (reallySubtract ? this.compareMantissaTo(that) : 1));
            if (comparison > 0) {
                big = this;
                small = that;
                sign = this.sign;
            } else if (comparison < 0) {
                big = that;
                small = this;
                sign = realThatSign;
            } else {
                return this.zero();
            }
            long scaleDifference = big.scale() - small.scale();
            if (scaleDifference < 0L) {
                precision = big.precision;
                exponent = big.exponent;
                bigSize = big.getSize();
                smallSize = 0L;
                size = bigSize;
                exponentDifference = bigSize;
            } else {
                precision = Math.min(big.precision, Util.ifFinite(small.precision, scaleDifference + small.precision));
                long basePrecision = Math.min(DoubleRadixConstants.MAX_EXPONENT[this.radix], this.getBasePrecision(precision, big.getInitialDigits()));
                exponent = big.exponent;
                exponentDifference = big.exponent - small.exponent;
                size = Math.min(basePrecision, Math.max(big.getSize(), exponentDifference + small.getSize()));
                bigSize = Math.min(size, big.getSize());
                smallSize = Math.max(0L, Math.min(size - exponentDifference, small.getSize()));
            }
            long dstSize = size + 1L;
            dataStorage = DoubleApfloatImpl.createDataStorage(dstSize);
            dataStorage.setSize(dstSize);
            double carry = 0.0;
            DataStorage.Iterator src1 = big.dataStorage.iterator(1, bigSize, 0L);
            DataStorage.Iterator src2 = small.dataStorage.iterator(1, smallSize, 0L);
            try (DataStorage.Iterator dst = dataStorage.iterator(2, dstSize, 0L);){
                long blockSize;
                if (size > bigSize) {
                    blockSize = Math.min(size - bigSize, smallSize);
                    carry = reallySubtract ? additionStrategy.subtract(null, src2, carry, dst, blockSize).doubleValue() : additionStrategy.add(null, src2, carry, dst, blockSize).doubleValue();
                } else if (size > exponentDifference + smallSize) {
                    blockSize = size - exponentDifference - smallSize;
                    carry = reallySubtract ? additionStrategy.subtract(src1, null, carry, dst, blockSize).doubleValue() : additionStrategy.add(src1, null, carry, dst, blockSize).doubleValue();
                }
                if (exponentDifference > bigSize) {
                    blockSize = exponentDifference - bigSize;
                    carry = reallySubtract ? additionStrategy.subtract(null, null, carry, dst, blockSize).doubleValue() : additionStrategy.add(null, null, carry, dst, blockSize).doubleValue();
                } else if (bigSize > exponentDifference) {
                    blockSize = Math.min(bigSize - exponentDifference, smallSize);
                    carry = reallySubtract ? additionStrategy.subtract(src1, src2, carry, dst, blockSize).doubleValue() : additionStrategy.add(src1, src2, carry, dst, blockSize).doubleValue();
                }
                if (exponentDifference > 0L) {
                    blockSize = Math.min(bigSize, exponentDifference);
                    carry = reallySubtract ? additionStrategy.subtract(src1, null, carry, dst, blockSize).doubleValue() : additionStrategy.add(src1, null, carry, dst, blockSize).doubleValue();
                }
                dst.setDouble(carry);
            }
            if (reallySubtract) {
                leadingZeros = DoubleApfloatImpl.getLeadingZeros(dataStorage, 0L);
                assert (leadingZeros <= size);
            } else {
                leadingZeros = carry == 0.0 ? 1 : 0;
                if (this.exponent == DoubleRadixConstants.MAX_EXPONENT[this.radix] && leadingZeros == 0L) {
                    throw new OverflowException("Overflow");
                }
            }
            dstSize -= DoubleApfloatImpl.getTrailingZeros(dataStorage, dstSize);
            dataStorage = dataStorage.subsequence(leadingZeros, dstSize - leadingZeros);
            if ((exponent += 1L - leadingZeros) < -DoubleRadixConstants.MAX_EXPONENT[this.radix]) {
                return this.zero();
            }
            if (precision != Long.MAX_VALUE) {
                long scaleChange = (1L - leadingZeros) * (long)DoubleRadixConstants.BASE_DIGITS[this.radix] + (long)this.getInitialDigits(dataStorage) - (long)big.getInitialDigits();
                if (-scaleChange >= precision) {
                    return this.zero();
                }
                precision = (precision += scaleChange) <= 0L ? Long.MAX_VALUE : precision;
            }
        }
        dataStorage.setReadOnly();
        return new DoubleApfloatImpl(sign, precision, exponent, dataStorage, this.radix);
    }

    @Override
    public ApfloatImpl multiply(ApfloatImpl x) throws ApfloatRuntimeException {
        int leadingZeros;
        if (!(x instanceof DoubleApfloatImpl)) {
            throw new ImplementationMismatchException("Wrong operand type: " + x.getClass().getName());
        }
        DoubleApfloatImpl that = (DoubleApfloatImpl)x;
        if (this.radix != that.radix) {
            throw new RadixMismatchException("Cannot multiply numbers with different radixes: " + this.radix + " and " + that.radix);
        }
        int sign = this.sign * that.sign;
        if (sign == 0) {
            return this.zero();
        }
        long exponent = this.exponent + that.exponent;
        if (exponent > DoubleRadixConstants.MAX_EXPONENT[this.radix]) {
            throw new OverflowException("Overflow");
        }
        if (exponent < -DoubleRadixConstants.MAX_EXPONENT[this.radix]) {
            return this.zero();
        }
        long precision = Math.min(this.precision, that.precision);
        long basePrecision = this.getBasePrecision(precision, 0);
        long thisSize = this.getSize();
        long thatSize = that.getSize();
        long size = Math.min(Util.ifFinite(basePrecision, basePrecision + 1L), thisSize + thatSize);
        long thisDataSize = Math.min(thisSize, basePrecision);
        long thatDataSize = Math.min(thatSize, basePrecision);
        DataStorage thisDataStorage = this.dataStorage.subsequence(0L, thisDataSize);
        DataStorage thatDataStorage = this.dataStorage == that.dataStorage ? thisDataStorage : that.dataStorage.subsequence(0L, thatDataSize);
        ApfloatContext ctx = ApfloatContext.getContext();
        ConvolutionBuilder convolutionBuilder = ctx.getBuilderFactory().getConvolutionBuilder();
        ConvolutionStrategy convolutionStrategy = convolutionBuilder.createConvolution(this.radix, thisDataSize, thatDataSize, size);
        DataStorage dataStorage = convolutionStrategy.convolute(thisDataStorage, thatDataStorage, size);
        int n = leadingZeros = DoubleApfloatImpl.getMostSignificantWord(dataStorage) == 0.0 ? 1 : 0;
        if ((exponent -= (long)leadingZeros) < -DoubleRadixConstants.MAX_EXPONENT[this.radix]) {
            return this.zero();
        }
        dataStorage = dataStorage.subsequence(leadingZeros, size -= (long)leadingZeros);
        size = Math.min(size, this.getBasePrecision(precision, this.getInitialDigits(dataStorage)));
        size -= DoubleApfloatImpl.getTrailingZeros(dataStorage, size);
        dataStorage = dataStorage.subsequence(0L, size);
        dataStorage.setReadOnly();
        return new DoubleApfloatImpl(sign, precision, exponent, dataStorage, this.radix);
    }

    @Override
    public boolean isShort() throws ApfloatRuntimeException {
        return this.sign == 0 || this.getSize() == 1L;
    }

    @Override
    public ApfloatImpl divideShort(ApfloatImpl x) throws ApfloatRuntimeException {
        DataStorage dataStorage;
        if (!(x instanceof DoubleApfloatImpl)) {
            throw new ImplementationMismatchException("Wrong operand type: " + x.getClass().getName());
        }
        DoubleApfloatImpl that = (DoubleApfloatImpl)x;
        if (this.radix != that.radix) {
            throw new RadixMismatchException("Cannot divide numbers with different radixes: " + this.radix + " and " + that.radix);
        }
        assert (this.sign != 0);
        assert (that.sign != 0);
        int sign = this.sign * that.sign;
        long exponent = this.exponent - that.exponent + 1L;
        if (exponent > DoubleRadixConstants.MAX_EXPONENT[this.radix]) {
            throw new OverflowException("Overflow");
        }
        if (exponent < -DoubleRadixConstants.MAX_EXPONENT[this.radix]) {
            return this.zero();
        }
        long precision = Math.min(this.precision, that.precision);
        long basePrecision = this.getBasePrecision();
        long thisDataSize = Math.min(this.getSize(), basePrecision);
        double divisor = DoubleApfloatImpl.getMostSignificantWord(that.dataStorage);
        if (divisor == 1.0) {
            long size = thisDataSize - DoubleApfloatImpl.getTrailingZeros(this.dataStorage, thisDataSize);
            dataStorage = this.dataStorage.subsequence(0L, size);
        } else {
            double carry;
            long size;
            ApfloatContext ctx = ApfloatContext.getContext();
            AdditionBuilder<Double> additionBuilder = ctx.getBuilderFactory().getAdditionBuilder(Double.TYPE);
            AdditionStrategy<Double> additionStrategy = additionBuilder.createAddition(this.radix);
            double dividend = divisor;
            for (int i = 0; i < RadixConstants.RADIX_FACTORS[this.radix].length; ++i) {
                double quotient;
                double factor = RadixConstants.RADIX_FACTORS[this.radix][i];
                while (dividend - factor * (quotient = (double)((long)(dividend / factor))) == 0.0) {
                    dividend = quotient;
                }
            }
            if (dividend != 1.0) {
                if (basePrecision == Long.MAX_VALUE) {
                    throw new InfiniteExpansionException("Cannot perform inexact division to infinite precision");
                }
                size = basePrecision;
            } else {
                carry = 1.0;
                DataStorage.Iterator dummy = new DataStorage.Iterator(){
                    private static final long serialVersionUID = 1L;

                    @Override
                    public void setDouble(double value) {
                    }

                    @Override
                    public void next() {
                    }
                };
                long sequenceSize = 0L;
                while (carry != 0.0) {
                    carry = additionStrategy.divide(null, divisor, carry, dummy, 1L);
                    ++sequenceSize;
                }
                size = Math.min(basePrecision, thisDataSize + sequenceSize);
            }
            dataStorage = DoubleApfloatImpl.createDataStorage(++size);
            dataStorage.setSize(size);
            DataStorage.Iterator src = this.dataStorage.iterator(1, 0L, thisDataSize);
            DataStorage.Iterator dst = dataStorage.iterator(2, 0L, size);
            carry = additionStrategy.divide(src, divisor, 0.0, dst, thisDataSize);
            carry = additionStrategy.divide(null, divisor, carry, dst, size - thisDataSize);
            size -= DoubleApfloatImpl.getTrailingZeros(dataStorage, size);
            int leadingZeros = this.getMostSignificantWord() < divisor ? 1 : 0;
            dataStorage = dataStorage.subsequence(leadingZeros, size - (long)leadingZeros);
            if ((exponent -= (long)leadingZeros) < -DoubleRadixConstants.MAX_EXPONENT[this.radix]) {
                return this.zero();
            }
            dataStorage.setReadOnly();
        }
        return new DoubleApfloatImpl(sign, precision, exponent, dataStorage, this.radix);
    }

    @Override
    public ApfloatImpl absFloor() throws ApfloatRuntimeException {
        if (this.sign == 0 || this.exponent >= this.dataStorage.getSize()) {
            return this.precision(Long.MAX_VALUE);
        }
        if (this.exponent <= 0L) {
            return this.zero();
        }
        long size = this.exponent;
        size -= DoubleApfloatImpl.getTrailingZeros(this.dataStorage, size);
        DataStorage dataStorage = this.dataStorage.subsequence(0L, size);
        DoubleApfloatImpl apfloatImpl = new DoubleApfloatImpl(this.sign, Long.MAX_VALUE, this.exponent, dataStorage, this.radix);
        return apfloatImpl;
    }

    @Override
    public ApfloatImpl absCeil() throws ApfloatRuntimeException {
        long exponent;
        DataStorage dataStorage;
        if (this.sign == 0) {
            return this;
        }
        if (this.exponent <= 0L) {
            int size = 1;
            dataStorage = DoubleApfloatImpl.createDataStorage(size);
            dataStorage.setSize(size);
            try (ArrayAccess arrayAccess = dataStorage.getArray(2, 0L, size);){
                arrayAccess.getDoubleData()[arrayAccess.getOffset()] = 1.0;
            }
            exponent = 1L;
        } else if (this.getSize() <= this.exponent || this.findMismatch(this.getZeroPaddedIterator(this.exponent, this.getSize()), ZERO_ITERATOR, this.getSize() - this.exponent) < 0L) {
            long size = Math.min(this.dataStorage.getSize(), this.exponent);
            size -= DoubleApfloatImpl.getTrailingZeros(this.dataStorage, size);
            dataStorage = this.dataStorage.subsequence(0L, size);
            exponent = this.exponent;
        } else {
            double carry;
            ApfloatContext ctx = ApfloatContext.getContext();
            AdditionBuilder<Double> additionBuilder = ctx.getBuilderFactory().getAdditionBuilder(Double.TYPE);
            AdditionStrategy<Double> additionStrategy = additionBuilder.createAddition(this.radix);
            long size = this.exponent;
            dataStorage = DoubleApfloatImpl.createDataStorage(size + 1L);
            dataStorage.setSize(size + 1L);
            try (DataStorage.Iterator src = this.dataStorage.iterator(1, size, 0L);
                 DataStorage.Iterator dst = dataStorage.iterator(2, size + 1L, 0L);){
                carry = additionStrategy.add(src, null, 1.0, dst, size);
                dst.setDouble(carry);
            }
            int carrySize = (int)carry;
            size -= DoubleApfloatImpl.getTrailingZeros(dataStorage, size + 1L);
            dataStorage = dataStorage.subsequence(1 - carrySize, size + (long)carrySize);
            exponent = this.exponent + (long)carrySize;
        }
        dataStorage.setReadOnly();
        DoubleApfloatImpl apfloatImpl = new DoubleApfloatImpl(this.sign, Long.MAX_VALUE, exponent, dataStorage, this.radix);
        return apfloatImpl;
    }

    @Override
    public ApfloatImpl frac() throws ApfloatRuntimeException {
        long precision;
        if (this.sign == 0 || this.exponent <= 0L) {
            return this;
        }
        if (this.exponent >= this.getSize()) {
            return this.zero();
        }
        long size = this.dataStorage.getSize() - this.exponent;
        long leadingZeros = DoubleApfloatImpl.getLeadingZeros(this.dataStorage, this.exponent);
        if (this.exponent + leadingZeros >= this.getSize()) {
            return this.zero();
        }
        DataStorage dataStorage = this.dataStorage.subsequence(this.exponent + leadingZeros, size - leadingZeros);
        if (this.precision != Long.MAX_VALUE) {
            precision = this.precision - (long)this.getInitialDigits() - (this.exponent + leadingZeros) * (long)DoubleRadixConstants.BASE_DIGITS[this.radix] + (long)this.getInitialDigits(dataStorage);
            if (precision <= 0L) {
                return this.zero();
            }
        } else {
            precision = Long.MAX_VALUE;
        }
        long exponent = -leadingZeros;
        DoubleApfloatImpl apfloatImpl = new DoubleApfloatImpl(this.sign, precision, exponent, dataStorage, this.radix);
        return apfloatImpl;
    }

    private ApfloatImpl zero() {
        return new DoubleApfloatImpl(0, Long.MAX_VALUE, 0L, null, this.radix);
    }

    @Override
    public int radix() {
        return this.radix;
    }

    @Override
    public long precision() {
        return this.precision;
    }

    @Override
    public long size() throws ApfloatRuntimeException {
        assert (this.dataStorage != null);
        if (this.size == 0L) {
            this.size = (long)this.getInitialDigits() + (this.getSize() - 1L) * (long)DoubleRadixConstants.BASE_DIGITS[this.radix] - this.getLeastZeros();
        }
        return this.size;
    }

    private long getLeastZeros() throws ApfloatRuntimeException {
        if (this.leastZeros == Integer.MIN_VALUE) {
            long index = this.getSize() - 1L;
            double word = this.getWord(index);
            word = this.getLeastSignificantWord(index, word);
            long leastZeros = 0L;
            if (word == 0.0) {
                long trailingZeros = DoubleApfloatImpl.getTrailingZeros(this.dataStorage, index) + 1L;
                word = this.getWord(index -= trailingZeros);
                word = this.getLeastSignificantWord(index, word);
                leastZeros += trailingZeros * (long)DoubleRadixConstants.BASE_DIGITS[this.radix];
            }
            assert (word != 0.0);
            while (word % (double)this.radix == 0.0) {
                ++leastZeros;
                word /= (double)this.radix;
            }
            this.leastZeros = leastZeros;
        }
        return this.leastZeros;
    }

    @Override
    public ApfloatImpl precision(long precision) {
        if (this.sign == 0 || precision == this.precision) {
            return this;
        }
        return new DoubleApfloatImpl(this.sign, precision, this.exponent, this.dataStorage, this.radix);
    }

    @Override
    public long scale() throws ApfloatRuntimeException {
        assert (this.dataStorage != null);
        return (this.exponent - 1L) * (long)DoubleRadixConstants.BASE_DIGITS[this.radix] + (long)this.getInitialDigits();
    }

    @Override
    public int signum() {
        return this.sign;
    }

    @Override
    public ApfloatImpl negate() throws ApfloatRuntimeException {
        return new DoubleApfloatImpl(-this.sign, this.precision, this.exponent, this.dataStorage, this.radix);
    }

    @Override
    public double doubleValue() {
        if (this.sign == 0) {
            return 0.0;
        }
        double value = 0.0;
        double doubleBase = DoubleRadixConstants.BASE[this.radix];
        int size = (int)Math.min(4L, this.getSize());
        DataStorage.Iterator iterator = this.dataStorage.iterator(1, size, 0L);
        while (iterator.hasNext()) {
            value += iterator.getDouble();
            value /= doubleBase;
            iterator.next();
        }
        if (this.exponent > 0L) {
            return (double)this.sign * value * Math.pow(DoubleRadixConstants.BASE[this.radix], this.exponent - 1L) * DoubleRadixConstants.BASE[this.radix];
        }
        return (double)this.sign * value * Math.pow(DoubleRadixConstants.BASE[this.radix], this.exponent);
    }

    @Override
    public long longValue() {
        if (this.sign == 0 || this.exponent <= 0L) {
            return 0L;
        }
        if (this.exponent > 4L) {
            return this.sign > 0 ? Long.MAX_VALUE : Long.MIN_VALUE;
        }
        long value = 0L;
        long longBase = (long)DoubleRadixConstants.BASE[this.radix];
        long maxPrevious = Long.MIN_VALUE / longBase;
        int size = (int)Math.min(this.exponent, this.getSize());
        try (DataStorage.Iterator iterator = this.dataStorage.iterator(1, 0L, size);){
            for (int i = 0; i < (int)this.exponent; ++i) {
                if (value < maxPrevious) {
                    value = 0L;
                    break;
                }
                value *= longBase;
                if (i >= size) continue;
                value -= (long)iterator.getDouble();
                iterator.next();
            }
        }
        if (value == Long.MIN_VALUE || value >= 0L) {
            return this.sign > 0 ? Long.MAX_VALUE : Long.MIN_VALUE;
        }
        return (long)(-this.sign) * value;
    }

    @Override
    public boolean isOne() throws ApfloatRuntimeException {
        if (this.isOne == Integer.MIN_VALUE) {
            this.isOne = this.sign == 1 && this.exponent == 1L && this.getSize() == 1L && this.getMostSignificantWord() == 1.0 ? 1 : 0;
        }
        return this.isOne == 1;
    }

    @Override
    public long equalDigits(ApfloatImpl x) throws ApfloatRuntimeException {
        if (!(x instanceof DoubleApfloatImpl)) {
            throw new ImplementationMismatchException("Wrong operand type: " + x.getClass().getName());
        }
        DoubleApfloatImpl that = (DoubleApfloatImpl)x;
        if (this.sign == 0 && that.sign == 0) {
            return Long.MAX_VALUE;
        }
        if (this.sign != that.sign) {
            return 0L;
        }
        if (this.radix != that.radix) {
            throw new RadixMismatchException("Cannot compare values with different radixes: " + this.radix + " and " + that.radix);
        }
        long thisScale = this.scale();
        long thatScale = that.scale();
        long minScale = Math.min(thisScale, thatScale);
        long maxScale = Math.max(thisScale, thatScale);
        if (maxScale - 1L > minScale) {
            return 0L;
        }
        long thisSize = this.getSize();
        long thatSize = that.getSize();
        long size = Math.max(thisSize, thatSize);
        long result = Math.min(this.precision, that.precision);
        try (DataStorage.Iterator thisIterator = this.getZeroPaddedIterator(0L, thisSize);
             DataStorage.Iterator thatIterator = that.getZeroPaddedIterator(0L, thatSize);){
            long index;
            double carry;
            double value;
            int lastMatchingDigits = -1;
            double base = DoubleRadixConstants.BASE[this.radix];
            if (this.exponent > that.exponent) {
                value = thisIterator.getDouble();
                if (value != 1.0) {
                    long l = 0L;
                    return l;
                }
                carry = base;
                thisIterator.next();
            } else if (this.exponent < that.exponent) {
                value = thatIterator.getDouble();
                if (value != 1.0) {
                    long l = 0L;
                    return l;
                }
                carry = -base;
                thatIterator.next();
            } else {
                carry = 0.0;
            }
            for (index = 0L; index < size; ++index) {
                value = thisIterator.getDouble() - thatIterator.getDouble() + carry;
                if (value == 0.0) {
                    carry = 0.0;
                } else {
                    if (Math.abs(value) > 1.0) {
                        if (Math.abs(value) >= base) {
                            lastMatchingDigits = -1;
                            break;
                        }
                        lastMatchingDigits = DoubleRadixConstants.BASE_DIGITS[this.radix] - this.getDigits(Math.abs(value));
                        break;
                    }
                    if (value == 1.0) {
                        carry = base;
                    } else if (value == -1.0) {
                        carry = -base;
                    }
                }
                thisIterator.next();
                thatIterator.next();
            }
            if (index < size || carry != 0.0) {
                long initialMatchingDigits = this.exponent == that.exponent ? Math.min(this.getInitialDigits(), that.getInitialDigits()) : DoubleRadixConstants.BASE_DIGITS[this.radix];
                long middleMatchingDigits = (index - 1L) * (long)DoubleRadixConstants.BASE_DIGITS[this.radix];
                result = Math.min(result, initialMatchingDigits + middleMatchingDigits + (long)lastMatchingDigits);
                result = Math.max(result, 0L);
            }
        }
        return result;
    }

    @Override
    public int compareTo(ApfloatImpl x) throws ApfloatRuntimeException {
        if (!(x instanceof DoubleApfloatImpl)) {
            throw new ImplementationMismatchException("Wrong operand type: " + x.getClass().getName());
        }
        DoubleApfloatImpl that = (DoubleApfloatImpl)x;
        if (this.sign == 0 && that.sign == 0) {
            return 0;
        }
        if (this.sign < that.sign) {
            return -1;
        }
        if (this.sign > that.sign) {
            return 1;
        }
        if (this.radix != that.radix) {
            throw new RadixMismatchException("Cannot compare values with different radixes: " + this.radix + " and " + that.radix);
        }
        if (this.scale() < that.scale()) {
            return -this.sign;
        }
        if (this.scale() > that.scale()) {
            return this.sign;
        }
        return this.sign * this.compareMantissaTo(that);
    }

    private DataStorage.Iterator getZeroPaddedIterator(final long start, final long end) throws ApfloatRuntimeException {
        final DataStorage.Iterator iterator = this.dataStorage.iterator(1, start, end);
        return new DataStorage.Iterator(){
            private static final long serialVersionUID = 1L;
            private long index;
            {
                this.index = start;
            }

            @Override
            public double getDouble() throws ApfloatRuntimeException {
                double value;
                if (this.index < end) {
                    value = iterator.getDouble();
                    if (this.index == end - 1L) {
                        value = DoubleApfloatImpl.this.getLeastSignificantWord(this.index, value);
                    }
                } else {
                    value = 0.0;
                }
                return value;
            }

            @Override
            public void next() throws ApfloatRuntimeException {
                if (this.index < end) {
                    iterator.next();
                    ++this.index;
                }
            }

            @Override
            public void close() throws ApfloatRuntimeException {
                iterator.close();
            }
        };
    }

    private int compareMantissaTo(DoubleApfloatImpl that) throws ApfloatRuntimeException {
        int result = 0;
        long thisSize = this.getSize();
        long thatSize = that.getSize();
        long size = Math.max(thisSize, thatSize);
        try (DataStorage.Iterator thisIterator = this.getZeroPaddedIterator(0L, thisSize);
             DataStorage.Iterator thatIterator = that.getZeroPaddedIterator(0L, thatSize);){
            long index = this.findMismatch(thisIterator, thatIterator, size);
            if (index >= 0L) {
                double thatValue;
                double thisValue = thisIterator.getDouble();
                if (thisValue < (thatValue = thatIterator.getDouble())) {
                    result = -1;
                } else if (thisValue > thatValue) {
                    result = 1;
                }
            }
        }
        return result;
    }

    private long findMismatch(DataStorage.Iterator thisIterator, DataStorage.Iterator thatIterator, long size) throws ApfloatRuntimeException {
        for (long index = 0L; index < size; ++index) {
            double thatValue;
            double thisValue = thisIterator.getDouble();
            if (thisValue != (thatValue = thatIterator.getDouble())) {
                return index;
            }
            thisIterator.next();
            thatIterator.next();
        }
        return -1L;
    }

    private double getLeastSignificantWord(long index, double word) throws ApfloatRuntimeException {
        if (this.precision == Long.MAX_VALUE) {
            return word;
        }
        long digits = (long)this.getInitialDigits() + index * (long)DoubleRadixConstants.BASE_DIGITS[this.radix];
        if (this.precision >= digits) {
            return word;
        }
        double divisor = DoubleRadixConstants.MINIMUM_FOR_DIGITS[this.radix][(int)(digits - this.precision)];
        return (double)((long)(word / divisor)) * divisor;
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof ApfloatImpl)) {
            return false;
        }
        ApfloatImpl thatImpl = (ApfloatImpl)obj;
        if (this.signum() == 0 && thatImpl.signum() == 0) {
            return true;
        }
        if (this.isOne() && thatImpl.isOne()) {
            return true;
        }
        if (!(obj instanceof DoubleApfloatImpl)) {
            return false;
        }
        DoubleApfloatImpl that = (DoubleApfloatImpl)obj;
        if (this.radix != that.radix) {
            return false;
        }
        if (this.sign != that.sign || this.exponent != that.exponent) {
            return false;
        }
        return this.compareMantissaTo(that) == 0;
    }

    @Override
    public int hashCode() {
        if (this.hashCode == 0) {
            int hashCode = 1 + this.sign + (int)this.exponent + (int)(this.exponent >>> 32);
            if (this.dataStorage != null) {
                long size = this.getSize();
                long i = 0L;
                while (i < size) {
                    double word = this.getWord(i);
                    if (i == size - 1L) {
                        word = this.getLeastSignificantWord(i, word);
                    }
                    long element = (long)word;
                    hashCode += (int)element + (int)(element >>> 32);
                    i = i + i + 1L;
                }
            }
            this.hashCode = hashCode;
        }
        return this.hashCode;
    }

    @Override
    public String toString(boolean pretty) throws ApfloatRuntimeException {
        long length;
        if (this.sign == 0) {
            return "0";
        }
        long size = this.getSize() * (long)DoubleRadixConstants.BASE_DIGITS[this.radix];
        if (pretty) {
            long scale = this.scale();
            length = scale <= 0L ? 2L - scale + size : (size > scale ? 1L + size : scale);
            length += (long)(this.sign < 0 ? 1 : 0);
        } else {
            length = size + 24L;
        }
        if (length > Integer.MAX_VALUE || length < 0L) {
            throw new ApfloatInternalException("Number is too large to fit in a String");
        }
        StringWriter writer = new StringWriter((int)length);
        try {
            this.writeTo(writer, pretty);
        }
        catch (IOException ioe) {
            throw new ApfloatInternalException("Unexpected I/O error writing to StringWriter", ioe);
        }
        String value = writer.toString();
        assert ((long)value.length() <= length);
        return value;
    }

    private static void writeZeros(Writer out, long count) throws IOException {
        for (long i = 0L; i < count; ++i) {
            out.write(48);
        }
    }

    @Override
    public void writeTo(Writer out, boolean pretty) throws IOException, ApfloatRuntimeException {
        long size;
        long exponent;
        long integerDigits;
        if (this.sign == 0) {
            out.write(48);
            return;
        }
        if (this.sign < 0) {
            out.write(45);
        }
        if (pretty) {
            if (this.exponent <= 0L) {
                out.write("0.");
                DoubleApfloatImpl.writeZeros(out, -this.scale());
                integerDigits = -1L;
            } else {
                integerDigits = this.scale();
            }
            exponent = 0L;
        } else {
            integerDigits = 1L;
            exponent = this.scale() - 1L;
        }
        boolean leftPadZeros = false;
        long digitsToWrite = Math.min(this.precision, (long)this.getInitialDigits() + (size - 1L) * (long)DoubleRadixConstants.BASE_DIGITS[this.radix]);
        long digitsWritten = 0L;
        long trailingZeros = 0L;
        DataStorage.Iterator iterator = this.dataStorage.iterator(1, 0L, size);
        char[] buffer = new char[DoubleRadixConstants.BASE_DIGITS[this.radix]];
        for (size = this.getSize(); size > 0L; --size) {
            int start = leftPadZeros ? 0 : DoubleRadixConstants.BASE_DIGITS[this.radix] - this.getInitialDigits();
            int digits = (int)Math.min(digitsToWrite, (long)(DoubleRadixConstants.BASE_DIGITS[this.radix] - start));
            this.formatWord(buffer, iterator.getDouble());
            for (int i = 0; i < digits; ++i) {
                char c = buffer[start + i];
                if (c == '0') {
                    ++trailingZeros;
                    --digitsToWrite;
                    continue;
                }
                while (trailingZeros > 0L) {
                    if (digitsWritten == integerDigits) {
                        out.write(46);
                    }
                    out.write(48);
                    ++digitsWritten;
                    --trailingZeros;
                }
                if (digitsWritten == integerDigits) {
                    out.write(46);
                }
                out.write(c);
                ++digitsWritten;
                --digitsToWrite;
            }
            leftPadZeros = true;
            iterator.next();
        }
        if (!pretty && exponent != 0L) {
            out.write("e" + exponent);
        }
        DoubleApfloatImpl.writeZeros(out, integerDigits - digitsWritten);
    }

    private void formatWord(char[] buffer, double word) {
        int position = DoubleRadixConstants.BASE_DIGITS[this.radix];
        while (position > 0 && word > 0.0) {
            double newWord = (long)(word / (double)this.radix);
            int digit = (int)(word - newWord * (double)this.radix);
            word = newWord;
            buffer[--position] = Character.forDigit(digit, this.radix);
        }
        while (position > 0) {
            buffer[--position] = 48;
        }
    }

    private long getSize() throws ApfloatRuntimeException {
        assert (this.dataStorage != null);
        return Math.min(this.getBasePrecision(), this.dataStorage.getSize());
    }

    private static int checkRadix(int radix) throws NumberFormatException {
        if (radix < 2 || radix > 36) {
            throw new NumberFormatException("Invalid radix " + radix + "; radix must be between " + 2 + " and " + 36);
        }
        return radix;
    }

    private double getMostSignificantWord() throws ApfloatRuntimeException {
        return DoubleApfloatImpl.getMostSignificantWord(this.dataStorage);
    }

    private static double getMostSignificantWord(DataStorage dataStorage) throws ApfloatRuntimeException {
        double msw;
        try (ArrayAccess arrayAccess = dataStorage.getArray(1, 0L, 1);){
            msw = arrayAccess.getDoubleData()[arrayAccess.getOffset()];
        }
        return msw;
    }

    private int getInitialDigits() throws ApfloatRuntimeException {
        if (this.initialDigits == Integer.MIN_VALUE) {
            this.initialDigits = this.getDigits(this.getMostSignificantWord());
        }
        return this.initialDigits;
    }

    private int getInitialDigits(DataStorage dataStorage) throws ApfloatRuntimeException {
        return this.getDigits(DoubleApfloatImpl.getMostSignificantWord(dataStorage));
    }

    private int getDigits(double x) {
        assert (x > 0.0);
        double[] minimums = DoubleRadixConstants.MINIMUM_FOR_DIGITS[this.radix];
        int i = minimums.length;
        while (x < minimums[--i]) {
        }
        return i + 1;
    }

    private long getBasePrecision() throws ApfloatRuntimeException {
        return this.getBasePrecision(this.precision, this.getInitialDigits());
    }

    private long getBasePrecision(long precision, int mswDigits) {
        if (precision == Long.MAX_VALUE) {
            return Long.MAX_VALUE;
        }
        return (precision + (long)DoubleRadixConstants.BASE_DIGITS[this.radix] - (long)mswDigits - 1L) / (long)DoubleRadixConstants.BASE_DIGITS[this.radix] + 1L;
    }

    private double getWord(long index) {
        double word;
        try (ArrayAccess arrayAccess = this.dataStorage.getArray(1, index, 1);){
            word = arrayAccess.getDoubleData()[arrayAccess.getOffset()];
        }
        return word;
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        this.leastZeros = Integer.MIN_VALUE;
        this.isOne = Integer.MIN_VALUE;
        in.defaultReadObject();
    }

    private static DataStorage createDataStorage(long size) throws ApfloatRuntimeException {
        ApfloatContext ctx = ApfloatContext.getContext();
        DataStorageBuilder dataStorageBuilder = ctx.getBuilderFactory().getDataStorageBuilder();
        return dataStorageBuilder.createDataStorage(size * 8L);
    }

    private static int getBlockSize() {
        ApfloatContext ctx = ApfloatContext.getContext();
        return ctx.getBlockSize() / 8;
    }
}


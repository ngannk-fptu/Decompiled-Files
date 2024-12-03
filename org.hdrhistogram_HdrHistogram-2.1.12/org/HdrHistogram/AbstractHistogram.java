/*
 * Decompiled with CFR 0.152.
 */
package org.HdrHistogram;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Iterator;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;
import org.HdrHistogram.AbstractHistogramBase;
import org.HdrHistogram.AllValuesIterator;
import org.HdrHistogram.ConcurrentHistogram;
import org.HdrHistogram.HistogramIterationValue;
import org.HdrHistogram.LinearIterator;
import org.HdrHistogram.LogarithmicIterator;
import org.HdrHistogram.PercentileIterator;
import org.HdrHistogram.RecordedValuesIterator;
import org.HdrHistogram.ValueRecorder;
import org.HdrHistogram.ZigZagEncoding;

public abstract class AbstractHistogram
extends AbstractHistogramBase
implements ValueRecorder,
Serializable {
    int leadingZeroCountBase;
    int subBucketHalfCountMagnitude;
    int unitMagnitude;
    int subBucketHalfCount;
    long subBucketMask;
    long unitMagnitudeMask;
    volatile long maxValue = 0L;
    volatile long minNonZeroValue = Long.MAX_VALUE;
    private static final AtomicLongFieldUpdater<AbstractHistogram> maxValueUpdater = AtomicLongFieldUpdater.newUpdater(AbstractHistogram.class, "maxValue");
    private static final AtomicLongFieldUpdater<AbstractHistogram> minNonZeroValueUpdater = AtomicLongFieldUpdater.newUpdater(AbstractHistogram.class, "minNonZeroValue");
    private static final long serialVersionUID = 478450434L;
    private static final int ENCODING_HEADER_SIZE = 40;
    private static final int V0_ENCODING_HEADER_SIZE = 32;
    private static final int V0EncodingCookieBase = 478450440;
    private static final int V0CompressedEncodingCookieBase = 478450441;
    private static final int V1EncodingCookieBase = 478450433;
    private static final int V1CompressedEncodingCookieBase = 478450434;
    private static final int V2EncodingCookieBase = 478450435;
    private static final int V2CompressedEncodingCookieBase = 478450436;
    private static final int V2maxWordSizeInBytes = 9;
    private static final int encodingCookieBase = 478450435;
    private static final int compressedEncodingCookieBase = 478450436;
    private static final Class[] constructorArgsTypes = new Class[]{Long.TYPE, Long.TYPE, Integer.TYPE};

    abstract long getCountAtIndex(int var1);

    abstract long getCountAtNormalizedIndex(int var1);

    abstract void incrementCountAtIndex(int var1);

    abstract void addToCountAtIndex(int var1, long var2);

    abstract void setCountAtIndex(int var1, long var2);

    abstract void setCountAtNormalizedIndex(int var1, long var2);

    abstract int getNormalizingIndexOffset();

    abstract void setNormalizingIndexOffset(int var1);

    abstract void shiftNormalizingIndexByOffset(int var1, boolean var2, double var3);

    abstract void setTotalCount(long var1);

    abstract void incrementTotalCount();

    abstract void addToTotalCount(long var1);

    abstract void clearCounts();

    abstract int _getEstimatedFootprintInBytes();

    abstract void resize(long var1);

    public abstract long getTotalCount();

    private void updatedMaxValue(long value) {
        long sampledMaxValue;
        long internalValue = value | this.unitMagnitudeMask;
        while (internalValue > (sampledMaxValue = this.maxValue)) {
            maxValueUpdater.compareAndSet(this, sampledMaxValue, internalValue);
        }
    }

    private void resetMaxValue(long maxValue) {
        this.maxValue = maxValue | this.unitMagnitudeMask;
    }

    private void updateMinNonZeroValue(long value) {
        long sampledMinNonZeroValue;
        if (value <= this.unitMagnitudeMask) {
            return;
        }
        long internalValue = value & (this.unitMagnitudeMask ^ 0xFFFFFFFFFFFFFFFFL);
        while (internalValue < (sampledMinNonZeroValue = this.minNonZeroValue)) {
            minNonZeroValueUpdater.compareAndSet(this, sampledMinNonZeroValue, internalValue);
        }
    }

    private void resetMinNonZeroValue(long minNonZeroValue) {
        long internalValue = minNonZeroValue & (this.unitMagnitudeMask ^ 0xFFFFFFFFFFFFFFFFL);
        this.minNonZeroValue = minNonZeroValue == Long.MAX_VALUE ? minNonZeroValue : internalValue;
    }

    protected AbstractHistogram(int numberOfSignificantValueDigits) {
        this(1L, 2L, numberOfSignificantValueDigits);
        this.autoResize = true;
    }

    protected AbstractHistogram(long lowestDiscernibleValue, long highestTrackableValue, int numberOfSignificantValueDigits) {
        if (lowestDiscernibleValue < 1L) {
            throw new IllegalArgumentException("lowestDiscernibleValue must be >= 1");
        }
        if (lowestDiscernibleValue > 0x3FFFFFFFFFFFFFFFL) {
            throw new IllegalArgumentException("lowestDiscernibleValue must be <= Long.MAX_VALUE / 2");
        }
        if (highestTrackableValue < 2L * lowestDiscernibleValue) {
            throw new IllegalArgumentException("highestTrackableValue must be >= 2 * lowestDiscernibleValue");
        }
        if (numberOfSignificantValueDigits < 0 || numberOfSignificantValueDigits > 5) {
            throw new IllegalArgumentException("numberOfSignificantValueDigits must be between 0 and 5");
        }
        this.identity = constructionIdentityCount.getAndIncrement();
        this.init(lowestDiscernibleValue, highestTrackableValue, numberOfSignificantValueDigits, 1.0, 0);
    }

    protected AbstractHistogram(AbstractHistogram source) {
        this(source.getLowestDiscernibleValue(), source.getHighestTrackableValue(), source.getNumberOfSignificantValueDigits());
        this.setStartTimeStamp(source.getStartTimeStamp());
        this.setEndTimeStamp(source.getEndTimeStamp());
        this.autoResize = source.autoResize;
    }

    private void init(long lowestDiscernibleValue, long highestTrackableValue, int numberOfSignificantValueDigits, double integerToDoubleValueConversionRatio, int normalizingIndexOffset) {
        this.lowestDiscernibleValue = lowestDiscernibleValue;
        this.highestTrackableValue = highestTrackableValue;
        this.numberOfSignificantValueDigits = numberOfSignificantValueDigits;
        this.integerToDoubleValueConversionRatio = integerToDoubleValueConversionRatio;
        if (normalizingIndexOffset != 0) {
            this.setNormalizingIndexOffset(normalizingIndexOffset);
        }
        long largestValueWithSingleUnitResolution = 2L * (long)Math.pow(10.0, numberOfSignificantValueDigits);
        this.unitMagnitude = (int)(Math.log(lowestDiscernibleValue) / Math.log(2.0));
        this.unitMagnitudeMask = (1 << this.unitMagnitude) - 1;
        int subBucketCountMagnitude = (int)Math.ceil(Math.log(largestValueWithSingleUnitResolution) / Math.log(2.0));
        this.subBucketHalfCountMagnitude = subBucketCountMagnitude - 1;
        this.subBucketCount = 1 << subBucketCountMagnitude;
        this.subBucketHalfCount = this.subBucketCount / 2;
        this.subBucketMask = (long)this.subBucketCount - 1L << this.unitMagnitude;
        if (subBucketCountMagnitude + this.unitMagnitude > 62) {
            throw new IllegalArgumentException("Cannot represent numberOfSignificantValueDigits worth of values beyond lowestDiscernibleValue");
        }
        this.establishSize(highestTrackableValue);
        this.leadingZeroCountBase = 64 - this.unitMagnitude - subBucketCountMagnitude;
        this.percentileIterator = new PercentileIterator(this, 1);
        this.recordedValuesIterator = new RecordedValuesIterator(this);
    }

    final void establishSize(long newHighestTrackableValue) {
        this.countsArrayLength = this.determineArrayLengthNeeded(newHighestTrackableValue);
        this.bucketCount = this.getBucketsNeededToCoverValue(newHighestTrackableValue);
        this.highestTrackableValue = newHighestTrackableValue;
    }

    final int determineArrayLengthNeeded(long highestTrackableValue) {
        if (highestTrackableValue < 2L * this.lowestDiscernibleValue) {
            throw new IllegalArgumentException("highestTrackableValue (" + highestTrackableValue + ") cannot be < (2 * lowestDiscernibleValue)");
        }
        int countsArrayLength = this.getLengthForNumberOfBuckets(this.getBucketsNeededToCoverValue(highestTrackableValue));
        return countsArrayLength;
    }

    public boolean isAutoResize() {
        return this.autoResize;
    }

    public boolean supportsAutoResize() {
        return true;
    }

    public void setAutoResize(boolean autoResize) {
        this.autoResize = autoResize;
    }

    @Override
    public void recordValue(long value) throws ArrayIndexOutOfBoundsException {
        this.recordSingleValue(value);
    }

    @Override
    public void recordValueWithCount(long value, long count) throws ArrayIndexOutOfBoundsException {
        this.recordCountAtValue(count, value);
    }

    @Override
    public void recordValueWithExpectedInterval(long value, long expectedIntervalBetweenValueSamples) throws ArrayIndexOutOfBoundsException {
        this.recordSingleValueWithExpectedInterval(value, expectedIntervalBetweenValueSamples);
    }

    void recordConvertedDoubleValue(double value) {
        long integerValue = (long)(value * this.doubleToIntegerValueConversionRatio);
        this.recordValue(integerValue);
    }

    public void recordConvertedDoubleValueWithCount(double value, long count) throws ArrayIndexOutOfBoundsException {
        long integerValue = (long)(value * this.doubleToIntegerValueConversionRatio);
        this.recordCountAtValue(count, integerValue);
    }

    public void recordValue(long value, long expectedIntervalBetweenValueSamples) throws ArrayIndexOutOfBoundsException {
        this.recordValueWithExpectedInterval(value, expectedIntervalBetweenValueSamples);
    }

    void updateMinAndMax(long value) {
        if (value > this.maxValue) {
            this.updatedMaxValue(value);
        }
        if (value < this.minNonZeroValue && value != 0L) {
            this.updateMinNonZeroValue(value);
        }
    }

    private void recordCountAtValue(long count, long value) throws ArrayIndexOutOfBoundsException {
        int countsIndex = this.countsArrayIndex(value);
        try {
            this.addToCountAtIndex(countsIndex, count);
        }
        catch (IndexOutOfBoundsException ex) {
            this.handleRecordException(count, value, ex);
        }
        this.updateMinAndMax(value);
        this.addToTotalCount(count);
    }

    private void recordSingleValue(long value) throws ArrayIndexOutOfBoundsException {
        int countsIndex = this.countsArrayIndex(value);
        try {
            this.incrementCountAtIndex(countsIndex);
        }
        catch (IndexOutOfBoundsException ex) {
            this.handleRecordException(1L, value, ex);
        }
        this.updateMinAndMax(value);
        this.incrementTotalCount();
    }

    private void handleRecordException(long count, long value, Exception ex) {
        if (!this.autoResize) {
            throw new ArrayIndexOutOfBoundsException("value " + value + " outside of histogram covered range. Caused by: " + ex);
        }
        this.resize(value);
        int countsIndex = this.countsArrayIndex(value);
        this.addToCountAtIndex(countsIndex, count);
        this.highestTrackableValue = this.highestEquivalentValue(this.valueFromIndex(this.countsArrayLength - 1));
    }

    private void recordValueWithCountAndExpectedInterval(long value, long count, long expectedIntervalBetweenValueSamples) throws ArrayIndexOutOfBoundsException {
        this.recordCountAtValue(count, value);
        if (expectedIntervalBetweenValueSamples <= 0L) {
            return;
        }
        for (long missingValue = value - expectedIntervalBetweenValueSamples; missingValue >= expectedIntervalBetweenValueSamples; missingValue -= expectedIntervalBetweenValueSamples) {
            this.recordCountAtValue(count, missingValue);
        }
    }

    private void recordSingleValueWithExpectedInterval(long value, long expectedIntervalBetweenValueSamples) throws ArrayIndexOutOfBoundsException {
        this.recordSingleValue(value);
        if (expectedIntervalBetweenValueSamples <= 0L) {
            return;
        }
        for (long missingValue = value - expectedIntervalBetweenValueSamples; missingValue >= expectedIntervalBetweenValueSamples; missingValue -= expectedIntervalBetweenValueSamples) {
            this.recordSingleValue(missingValue);
        }
    }

    @Override
    public void reset() {
        this.clearCounts();
        this.resetMaxValue(0L);
        this.resetMinNonZeroValue(Long.MAX_VALUE);
        this.setNormalizingIndexOffset(0);
        this.startTimeStampMsec = Long.MAX_VALUE;
        this.endTimeStampMsec = 0L;
        this.tag = null;
    }

    public abstract AbstractHistogram copy();

    public abstract AbstractHistogram copyCorrectedForCoordinatedOmission(long var1);

    public void copyInto(AbstractHistogram targetHistogram) {
        targetHistogram.reset();
        targetHistogram.add(this);
        targetHistogram.setStartTimeStamp(this.startTimeStampMsec);
        targetHistogram.setEndTimeStamp(this.endTimeStampMsec);
    }

    public void copyIntoCorrectedForCoordinatedOmission(AbstractHistogram targetHistogram, long expectedIntervalBetweenValueSamples) {
        targetHistogram.reset();
        targetHistogram.addWhileCorrectingForCoordinatedOmission(this, expectedIntervalBetweenValueSamples);
        targetHistogram.setStartTimeStamp(this.startTimeStampMsec);
        targetHistogram.setEndTimeStamp(this.endTimeStampMsec);
    }

    public void add(AbstractHistogram otherHistogram) throws ArrayIndexOutOfBoundsException {
        long highestRecordableValue = this.highestEquivalentValue(this.valueFromIndex(this.countsArrayLength - 1));
        if (highestRecordableValue < otherHistogram.getMaxValue()) {
            if (!this.isAutoResize()) {
                throw new ArrayIndexOutOfBoundsException("The other histogram includes values that do not fit in this histogram's range.");
            }
            this.resize(otherHistogram.getMaxValue());
        }
        if (this.bucketCount == otherHistogram.bucketCount && this.subBucketCount == otherHistogram.subBucketCount && this.unitMagnitude == otherHistogram.unitMagnitude && this.getNormalizingIndexOffset() == otherHistogram.getNormalizingIndexOffset() && !(otherHistogram instanceof ConcurrentHistogram)) {
            long observedOtherTotalCount = 0L;
            for (int i = 0; i < otherHistogram.countsArrayLength; ++i) {
                long otherCount = otherHistogram.getCountAtIndex(i);
                if (otherCount <= 0L) continue;
                this.addToCountAtIndex(i, otherCount);
                observedOtherTotalCount += otherCount;
            }
            this.setTotalCount(this.getTotalCount() + observedOtherTotalCount);
            this.updatedMaxValue(Math.max(this.getMaxValue(), otherHistogram.getMaxValue()));
            this.updateMinNonZeroValue(Math.min(this.getMinNonZeroValue(), otherHistogram.getMinNonZeroValue()));
        } else {
            int otherMaxIndex = otherHistogram.countsArrayIndex(otherHistogram.getMaxValue());
            long otherCount = otherHistogram.getCountAtIndex(otherMaxIndex);
            this.recordValueWithCount(otherHistogram.valueFromIndex(otherMaxIndex), otherCount);
            for (int i = 0; i < otherMaxIndex; ++i) {
                otherCount = otherHistogram.getCountAtIndex(i);
                if (otherCount <= 0L) continue;
                this.recordValueWithCount(otherHistogram.valueFromIndex(i), otherCount);
            }
        }
        this.setStartTimeStamp(Math.min(this.startTimeStampMsec, otherHistogram.startTimeStampMsec));
        this.setEndTimeStamp(Math.max(this.endTimeStampMsec, otherHistogram.endTimeStampMsec));
    }

    public void subtract(AbstractHistogram otherHistogram) throws ArrayIndexOutOfBoundsException, IllegalArgumentException {
        if (this.highestEquivalentValue(otherHistogram.getMaxValue()) > this.highestEquivalentValue(this.valueFromIndex(this.countsArrayLength - 1))) {
            throw new IllegalArgumentException("The other histogram includes values that do not fit in this histogram's range.");
        }
        for (int i = 0; i < otherHistogram.countsArrayLength; ++i) {
            long otherCount = otherHistogram.getCountAtIndex(i);
            if (otherCount <= 0L) continue;
            long otherValue = otherHistogram.valueFromIndex(i);
            if (this.getCountAtValue(otherValue) < otherCount) {
                throw new IllegalArgumentException("otherHistogram count (" + otherCount + ") at value " + otherValue + " is larger than this one's (" + this.getCountAtValue(otherValue) + ")");
            }
            this.recordValueWithCount(otherValue, -otherCount);
        }
        if (this.getCountAtValue(this.getMaxValue()) <= 0L || this.getCountAtValue(this.getMinNonZeroValue()) <= 0L) {
            this.establishInternalTackingValues();
        }
    }

    public void addWhileCorrectingForCoordinatedOmission(AbstractHistogram otherHistogram, long expectedIntervalBetweenValueSamples) {
        AbstractHistogram toHistogram = this;
        for (HistogramIterationValue v : otherHistogram.recordedValues()) {
            toHistogram.recordValueWithCountAndExpectedInterval(v.getValueIteratedTo(), v.getCountAtValueIteratedTo(), expectedIntervalBetweenValueSamples);
        }
    }

    public void shiftValuesLeft(int numberOfBinaryOrdersOfMagnitude) {
        this.shiftValuesLeft(numberOfBinaryOrdersOfMagnitude, this.integerToDoubleValueConversionRatio);
    }

    void shiftValuesLeft(int numberOfBinaryOrdersOfMagnitude, double newIntegerToDoubleValueConversionRatio) {
        if (numberOfBinaryOrdersOfMagnitude < 0) {
            throw new IllegalArgumentException("Cannot shift by a negative number of magnitudes");
        }
        if (numberOfBinaryOrdersOfMagnitude == 0) {
            return;
        }
        if (this.getTotalCount() == this.getCountAtIndex(0)) {
            return;
        }
        int shiftAmount = numberOfBinaryOrdersOfMagnitude << this.subBucketHalfCountMagnitude;
        int maxValueIndex = this.countsArrayIndex(this.getMaxValue());
        if (maxValueIndex >= this.countsArrayLength - shiftAmount) {
            throw new ArrayIndexOutOfBoundsException("Operation would overflow, would discard recorded value counts");
        }
        long maxValueBeforeShift = maxValueUpdater.getAndSet(this, 0L);
        long minNonZeroValueBeforeShift = minNonZeroValueUpdater.getAndSet(this, Long.MAX_VALUE);
        boolean lowestHalfBucketPopulated = minNonZeroValueBeforeShift < (long)(this.subBucketHalfCount << this.unitMagnitude);
        this.shiftNormalizingIndexByOffset(shiftAmount, lowestHalfBucketPopulated, newIntegerToDoubleValueConversionRatio);
        this.updateMinAndMax(maxValueBeforeShift << numberOfBinaryOrdersOfMagnitude);
        if (minNonZeroValueBeforeShift < Long.MAX_VALUE) {
            this.updateMinAndMax(minNonZeroValueBeforeShift << numberOfBinaryOrdersOfMagnitude);
        }
    }

    void nonConcurrentNormalizingIndexShift(int shiftAmount, boolean lowestHalfBucketPopulated) {
        long zeroValueCount = this.getCountAtIndex(0);
        this.setCountAtIndex(0, 0L);
        int preShiftZeroIndex = this.normalizeIndex(0, this.getNormalizingIndexOffset(), this.countsArrayLength);
        this.setNormalizingIndexOffset(this.getNormalizingIndexOffset() + shiftAmount);
        if (lowestHalfBucketPopulated) {
            if (shiftAmount <= 0) {
                throw new ArrayIndexOutOfBoundsException("Attempt to right-shift with already-recorded value counts that would underflow and lose precision");
            }
            this.shiftLowestHalfBucketContentsLeft(shiftAmount, preShiftZeroIndex);
        }
        this.setCountAtIndex(0, zeroValueCount);
    }

    private void shiftLowestHalfBucketContentsLeft(int shiftAmount, int preShiftZeroIndex) {
        int numberOfBinaryOrdersOfMagnitude = shiftAmount >> this.subBucketHalfCountMagnitude;
        for (int fromIndex = 1; fromIndex < this.subBucketHalfCount; ++fromIndex) {
            long toValue = this.valueFromIndex(fromIndex) << numberOfBinaryOrdersOfMagnitude;
            int toIndex = this.countsArrayIndex(toValue);
            long countAtFromIndex = this.getCountAtNormalizedIndex(fromIndex + preShiftZeroIndex);
            this.setCountAtIndex(toIndex, countAtFromIndex);
            this.setCountAtNormalizedIndex(fromIndex + preShiftZeroIndex, 0L);
        }
    }

    public void shiftValuesRight(int numberOfBinaryOrdersOfMagnitude) {
        this.shiftValuesRight(numberOfBinaryOrdersOfMagnitude, this.integerToDoubleValueConversionRatio);
    }

    void shiftValuesRight(int numberOfBinaryOrdersOfMagnitude, double newIntegerToDoubleValueConversionRatio) {
        if (numberOfBinaryOrdersOfMagnitude < 0) {
            throw new IllegalArgumentException("Cannot shift by a negative number of magnitudes");
        }
        if (numberOfBinaryOrdersOfMagnitude == 0) {
            return;
        }
        if (this.getTotalCount() == this.getCountAtIndex(0)) {
            return;
        }
        int shiftAmount = this.subBucketHalfCount * numberOfBinaryOrdersOfMagnitude;
        int minNonZeroValueIndex = this.countsArrayIndex(this.getMinNonZeroValue());
        if (minNonZeroValueIndex < shiftAmount + this.subBucketHalfCount) {
            throw new ArrayIndexOutOfBoundsException("Operation would underflow and lose precision of already recorded value counts");
        }
        long maxValueBeforeShift = maxValueUpdater.getAndSet(this, 0L);
        long minNonZeroValueBeforeShift = minNonZeroValueUpdater.getAndSet(this, Long.MAX_VALUE);
        this.shiftNormalizingIndexByOffset(-shiftAmount, false, newIntegerToDoubleValueConversionRatio);
        this.updateMinAndMax(maxValueBeforeShift >> numberOfBinaryOrdersOfMagnitude);
        if (minNonZeroValueBeforeShift < Long.MAX_VALUE) {
            this.updateMinAndMax(minNonZeroValueBeforeShift >> numberOfBinaryOrdersOfMagnitude);
        }
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof AbstractHistogram)) {
            return false;
        }
        AbstractHistogram that = (AbstractHistogram)other;
        if (this.lowestDiscernibleValue != that.lowestDiscernibleValue || this.numberOfSignificantValueDigits != that.numberOfSignificantValueDigits || this.integerToDoubleValueConversionRatio != that.integerToDoubleValueConversionRatio) {
            return false;
        }
        if (this.getTotalCount() != that.getTotalCount()) {
            return false;
        }
        if (this.getMaxValue() != that.getMaxValue()) {
            return false;
        }
        if (this.getMinNonZeroValue() != that.getMinNonZeroValue()) {
            return false;
        }
        if (this.countsArrayLength == that.countsArrayLength) {
            for (int i = 0; i < this.countsArrayLength; ++i) {
                if (this.getCountAtIndex(i) == that.getCountAtIndex(i)) continue;
                return false;
            }
        } else {
            for (HistogramIterationValue value : this.recordedValues()) {
                long countAtValueIteratedTo = value.getCountAtValueIteratedTo();
                long valueIteratedTo = value.getValueIteratedTo();
                if (that.getCountAtValue(valueIteratedTo) == countAtValueIteratedTo) continue;
                return false;
            }
        }
        return true;
    }

    public int hashCode() {
        int h = 0;
        h = this.oneAtATimeHashStep(h, this.unitMagnitude);
        h = this.oneAtATimeHashStep(h, this.numberOfSignificantValueDigits);
        h = this.oneAtATimeHashStep(h, (int)this.getTotalCount());
        h = this.oneAtATimeHashStep(h, (int)this.getMaxValue());
        h = this.oneAtATimeHashStep(h, (int)this.getMinNonZeroValue());
        h += h << 3;
        h ^= h >> 11;
        h += h << 15;
        return h;
    }

    private int oneAtATimeHashStep(int h, int v) {
        h += v;
        h += h << 10;
        h ^= h >> 6;
        return h;
    }

    public long getLowestDiscernibleValue() {
        return this.lowestDiscernibleValue;
    }

    public long getHighestTrackableValue() {
        return this.highestTrackableValue;
    }

    public int getNumberOfSignificantValueDigits() {
        return this.numberOfSignificantValueDigits;
    }

    public long sizeOfEquivalentValueRange(long value) {
        int bucketIndex = this.getBucketIndex(value);
        long distanceToNextValue = 1L << this.unitMagnitude + bucketIndex;
        return distanceToNextValue;
    }

    public long lowestEquivalentValue(long value) {
        int bucketIndex = this.getBucketIndex(value);
        int subBucketIndex = this.getSubBucketIndex(value, bucketIndex);
        long thisValueBaseLevel = this.valueFromIndex(bucketIndex, subBucketIndex);
        return thisValueBaseLevel;
    }

    public long highestEquivalentValue(long value) {
        return this.nextNonEquivalentValue(value) - 1L;
    }

    public long medianEquivalentValue(long value) {
        return this.lowestEquivalentValue(value) + (this.sizeOfEquivalentValueRange(value) >> 1);
    }

    public long nextNonEquivalentValue(long value) {
        return this.lowestEquivalentValue(value) + this.sizeOfEquivalentValueRange(value);
    }

    public boolean valuesAreEquivalent(long value1, long value2) {
        return this.lowestEquivalentValue(value1) == this.lowestEquivalentValue(value2);
    }

    public int getEstimatedFootprintInBytes() {
        return this._getEstimatedFootprintInBytes();
    }

    @Override
    public long getStartTimeStamp() {
        return this.startTimeStampMsec;
    }

    @Override
    public void setStartTimeStamp(long timeStampMsec) {
        this.startTimeStampMsec = timeStampMsec;
    }

    @Override
    public long getEndTimeStamp() {
        return this.endTimeStampMsec;
    }

    @Override
    public void setEndTimeStamp(long timeStampMsec) {
        this.endTimeStampMsec = timeStampMsec;
    }

    @Override
    public String getTag() {
        return this.tag;
    }

    @Override
    public void setTag(String tag) {
        this.tag = tag;
    }

    public long getMinValue() {
        if (this.getCountAtIndex(0) > 0L || this.getTotalCount() == 0L) {
            return 0L;
        }
        return this.getMinNonZeroValue();
    }

    public long getMaxValue() {
        return this.maxValue == 0L ? 0L : this.highestEquivalentValue(this.maxValue);
    }

    public long getMinNonZeroValue() {
        return this.minNonZeroValue == Long.MAX_VALUE ? Long.MAX_VALUE : this.lowestEquivalentValue(this.minNonZeroValue);
    }

    @Override
    public double getMaxValueAsDouble() {
        return this.getMaxValue();
    }

    public double getMean() {
        if (this.getTotalCount() == 0L) {
            return 0.0;
        }
        this.recordedValuesIterator.reset();
        double totalValue = 0.0;
        while (this.recordedValuesIterator.hasNext()) {
            HistogramIterationValue iterationValue = this.recordedValuesIterator.next();
            totalValue += (double)this.medianEquivalentValue(iterationValue.getValueIteratedTo()) * (double)iterationValue.getCountAtValueIteratedTo();
        }
        return totalValue * 1.0 / (double)this.getTotalCount();
    }

    public double getStdDeviation() {
        if (this.getTotalCount() == 0L) {
            return 0.0;
        }
        double mean = this.getMean();
        double geometric_deviation_total = 0.0;
        this.recordedValuesIterator.reset();
        while (this.recordedValuesIterator.hasNext()) {
            HistogramIterationValue iterationValue = this.recordedValuesIterator.next();
            double deviation = (double)this.medianEquivalentValue(iterationValue.getValueIteratedTo()) * 1.0 - mean;
            geometric_deviation_total += deviation * deviation * (double)iterationValue.getCountAddedInThisIterationStep();
        }
        double std_deviation = Math.sqrt(geometric_deviation_total / (double)this.getTotalCount());
        return std_deviation;
    }

    public long getValueAtPercentile(double percentile) {
        double requestedPercentile = Math.min(Math.max(Math.nextAfter(percentile, Double.NEGATIVE_INFINITY), 0.0), 100.0);
        double fpCountAtPercentile = requestedPercentile * (double)this.getTotalCount() / 100.0;
        long countAtPercentile = (long)Math.ceil(fpCountAtPercentile);
        countAtPercentile = Math.max(countAtPercentile, 1L);
        long totalToCurrentIndex = 0L;
        for (int i = 0; i < this.countsArrayLength; ++i) {
            if ((totalToCurrentIndex += this.getCountAtIndex(i)) < countAtPercentile) continue;
            long valueAtIndex = this.valueFromIndex(i);
            return percentile == 0.0 ? this.lowestEquivalentValue(valueAtIndex) : this.highestEquivalentValue(valueAtIndex);
        }
        return 0L;
    }

    public double getPercentileAtOrBelowValue(long value) {
        if (this.getTotalCount() == 0L) {
            return 100.0;
        }
        int targetIndex = Math.min(this.countsArrayIndex(value), this.countsArrayLength - 1);
        long totalToCurrentIndex = 0L;
        for (int i = 0; i <= targetIndex; ++i) {
            totalToCurrentIndex += this.getCountAtIndex(i);
        }
        return 100.0 * (double)totalToCurrentIndex / (double)this.getTotalCount();
    }

    public long getCountBetweenValues(long lowValue, long highValue) throws ArrayIndexOutOfBoundsException {
        int lowIndex = Math.max(0, this.countsArrayIndex(lowValue));
        int highIndex = Math.min(this.countsArrayIndex(highValue), this.countsArrayLength - 1);
        long count = 0L;
        for (int i = lowIndex; i <= highIndex; ++i) {
            count += this.getCountAtIndex(i);
        }
        return count;
    }

    public long getCountAtValue(long value) throws ArrayIndexOutOfBoundsException {
        int index = Math.min(Math.max(0, this.countsArrayIndex(value)), this.countsArrayLength - 1);
        return this.getCountAtIndex(index);
    }

    public Percentiles percentiles(int percentileTicksPerHalfDistance) {
        return new Percentiles(this, percentileTicksPerHalfDistance);
    }

    public LinearBucketValues linearBucketValues(long valueUnitsPerBucket) {
        return new LinearBucketValues(this, valueUnitsPerBucket);
    }

    public LogarithmicBucketValues logarithmicBucketValues(long valueUnitsInFirstBucket, double logBase) {
        return new LogarithmicBucketValues(this, valueUnitsInFirstBucket, logBase);
    }

    public RecordedValues recordedValues() {
        return new RecordedValues(this);
    }

    public AllValues allValues() {
        return new AllValues(this);
    }

    public void outputPercentileDistribution(PrintStream printStream, Double outputValueUnitScalingRatio) {
        this.outputPercentileDistribution(printStream, 5, outputValueUnitScalingRatio);
    }

    public void outputPercentileDistribution(PrintStream printStream, int percentileTicksPerHalfDistance, Double outputValueUnitScalingRatio) {
        this.outputPercentileDistribution(printStream, percentileTicksPerHalfDistance, outputValueUnitScalingRatio, false);
    }

    public void outputPercentileDistribution(PrintStream printStream, int percentileTicksPerHalfDistance, Double outputValueUnitScalingRatio, boolean useCsvFormat) {
        String lastLinePercentileFormatString;
        String percentileFormatString;
        if (useCsvFormat) {
            printStream.format("\"Value\",\"Percentile\",\"TotalCount\",\"1/(1-Percentile)\"\n", new Object[0]);
        } else {
            printStream.format("%12s %14s %10s %14s\n\n", "Value", "Percentile", "TotalCount", "1/(1-Percentile)");
        }
        PercentileIterator iterator = this.percentileIterator;
        iterator.reset(percentileTicksPerHalfDistance);
        if (useCsvFormat) {
            percentileFormatString = "%." + this.numberOfSignificantValueDigits + "f,%.12f,%d,%.2f\n";
            lastLinePercentileFormatString = "%." + this.numberOfSignificantValueDigits + "f,%.12f,%d,Infinity\n";
        } else {
            percentileFormatString = "%12." + this.numberOfSignificantValueDigits + "f %2.12f %10d %14.2f\n";
            lastLinePercentileFormatString = "%12." + this.numberOfSignificantValueDigits + "f %2.12f %10d\n";
        }
        while (iterator.hasNext()) {
            HistogramIterationValue iterationValue = iterator.next();
            if (iterationValue.getPercentileLevelIteratedTo() != 100.0) {
                printStream.format(Locale.US, percentileFormatString, (double)iterationValue.getValueIteratedTo() / outputValueUnitScalingRatio, iterationValue.getPercentileLevelIteratedTo() / 100.0, iterationValue.getTotalCountToThisValue(), 1.0 / (1.0 - iterationValue.getPercentileLevelIteratedTo() / 100.0));
                continue;
            }
            printStream.format(Locale.US, lastLinePercentileFormatString, (double)iterationValue.getValueIteratedTo() / outputValueUnitScalingRatio, iterationValue.getPercentileLevelIteratedTo() / 100.0, iterationValue.getTotalCountToThisValue());
        }
        if (!useCsvFormat) {
            double mean = this.getMean() / outputValueUnitScalingRatio;
            double std_deviation = this.getStdDeviation() / outputValueUnitScalingRatio;
            printStream.format(Locale.US, "#[Mean    = %12." + this.numberOfSignificantValueDigits + "f, StdDeviation   = %12." + this.numberOfSignificantValueDigits + "f]\n", mean, std_deviation);
            printStream.format(Locale.US, "#[Max     = %12." + this.numberOfSignificantValueDigits + "f, Total count    = %12d]\n", (double)this.getMaxValue() / outputValueUnitScalingRatio, this.getTotalCount());
            printStream.format(Locale.US, "#[Buckets = %12d, SubBuckets     = %12d]\n", this.bucketCount, this.subBucketCount);
        }
    }

    private void writeObject(ObjectOutputStream o) throws IOException {
        o.writeLong(this.lowestDiscernibleValue);
        o.writeLong(this.highestTrackableValue);
        o.writeInt(this.numberOfSignificantValueDigits);
        o.writeInt(this.getNormalizingIndexOffset());
        o.writeDouble(this.integerToDoubleValueConversionRatio);
        o.writeLong(this.getTotalCount());
        o.writeLong(this.maxValue);
        o.writeLong(this.minNonZeroValue);
        o.writeLong(this.startTimeStampMsec);
        o.writeLong(this.endTimeStampMsec);
        o.writeBoolean(this.autoResize);
        o.writeInt(this.wordSizeInBytes);
    }

    private void readObject(ObjectInputStream o) throws IOException, ClassNotFoundException {
        long lowestDiscernibleValue = o.readLong();
        long highestTrackableValue = o.readLong();
        int numberOfSignificantValueDigits = o.readInt();
        int normalizingIndexOffset = o.readInt();
        double integerToDoubleValueConversionRatio = o.readDouble();
        long indicatedTotalCount = o.readLong();
        long indicatedMaxValue = o.readLong();
        long indicatedMinNonZeroValue = o.readLong();
        long indicatedStartTimeStampMsec = o.readLong();
        long indicatedEndTimeStampMsec = o.readLong();
        boolean indicatedAutoResize = o.readBoolean();
        int indicatedwordSizeInBytes = o.readInt();
        this.init(lowestDiscernibleValue, highestTrackableValue, numberOfSignificantValueDigits, integerToDoubleValueConversionRatio, normalizingIndexOffset);
        this.setTotalCount(indicatedTotalCount);
        this.maxValue = indicatedMaxValue;
        this.minNonZeroValue = indicatedMinNonZeroValue;
        this.startTimeStampMsec = indicatedStartTimeStampMsec;
        this.endTimeStampMsec = indicatedEndTimeStampMsec;
        this.autoResize = indicatedAutoResize;
        this.wordSizeInBytes = indicatedwordSizeInBytes;
    }

    @Override
    public int getNeededByteBufferCapacity() {
        return this.getNeededByteBufferCapacity(this.countsArrayLength);
    }

    int getNeededByteBufferCapacity(int relevantLength) {
        return this.getNeededPayloadByteBufferCapacity(relevantLength) + 40;
    }

    int getNeededPayloadByteBufferCapacity(int relevantLength) {
        return relevantLength * 9;
    }

    int getNeededV0PayloadByteBufferCapacity(int relevantLength) {
        return relevantLength * this.wordSizeInBytes;
    }

    private int getEncodingCookie() {
        return 478450451;
    }

    private int getCompressedEncodingCookie() {
        return 478450452;
    }

    private static int getCookieBase(int cookie) {
        return cookie & 0xFFFFFF0F;
    }

    private static int getWordSizeInBytesFromCookie(int cookie) {
        if (AbstractHistogram.getCookieBase(cookie) == 478450435 || AbstractHistogram.getCookieBase(cookie) == 478450436) {
            return 9;
        }
        int sizeByte = (cookie & 0xF0) >> 4;
        return sizeByte & 0xE;
    }

    public synchronized int encodeIntoByteBuffer(ByteBuffer buffer) {
        long maxValue = this.getMaxValue();
        int relevantLength = this.countsArrayIndex(maxValue) + 1;
        if (buffer.capacity() < this.getNeededByteBufferCapacity(relevantLength)) {
            throw new ArrayIndexOutOfBoundsException("buffer does not have capacity for " + this.getNeededByteBufferCapacity(relevantLength) + " bytes");
        }
        int initialPosition = buffer.position();
        buffer.putInt(this.getEncodingCookie());
        buffer.putInt(0);
        buffer.putInt(this.getNormalizingIndexOffset());
        buffer.putInt(this.numberOfSignificantValueDigits);
        buffer.putLong(this.lowestDiscernibleValue);
        buffer.putLong(this.highestTrackableValue);
        buffer.putDouble(this.getIntegerToDoubleValueConversionRatio());
        int payloadStartPosition = buffer.position();
        this.fillBufferFromCountsArray(buffer);
        buffer.putInt(initialPosition + 4, buffer.position() - payloadStartPosition);
        return buffer.position() - initialPosition;
    }

    @Override
    public synchronized int encodeIntoCompressedByteBuffer(ByteBuffer targetBuffer, int compressionLevel) {
        byte[] targetArray;
        int neededCapacity = this.getNeededByteBufferCapacity(this.countsArrayLength);
        if (this.intermediateUncompressedByteBuffer == null || this.intermediateUncompressedByteBuffer.capacity() < neededCapacity) {
            this.intermediateUncompressedByteBuffer = ByteBuffer.allocate(neededCapacity).order(ByteOrder.BIG_ENDIAN);
        }
        this.intermediateUncompressedByteBuffer.clear();
        int initialTargetPosition = targetBuffer.position();
        int uncompressedLength = this.encodeIntoByteBuffer(this.intermediateUncompressedByteBuffer);
        targetBuffer.putInt(this.getCompressedEncodingCookie());
        targetBuffer.putInt(0);
        Deflater compressor = new Deflater(compressionLevel);
        compressor.setInput(this.intermediateUncompressedByteBuffer.array(), 0, uncompressedLength);
        compressor.finish();
        if (targetBuffer.hasArray()) {
            targetArray = targetBuffer.array();
        } else {
            if (this.intermediateUncompressedByteArray == null || this.intermediateUncompressedByteArray.length < targetBuffer.capacity()) {
                this.intermediateUncompressedByteArray = new byte[targetBuffer.capacity()];
            }
            targetArray = this.intermediateUncompressedByteArray;
        }
        int compressedTargetOffset = initialTargetPosition + 8;
        int compressedDataLength = compressor.deflate(targetArray, compressedTargetOffset, targetArray.length - compressedTargetOffset);
        compressor.end();
        if (!targetBuffer.hasArray()) {
            targetBuffer.put(targetArray, compressedTargetOffset, compressedDataLength);
        }
        targetBuffer.putInt(initialTargetPosition + 4, compressedDataLength);
        int bytesWritten = compressedDataLength + 8;
        targetBuffer.position(initialTargetPosition + bytesWritten);
        return bytesWritten;
    }

    public int encodeIntoCompressedByteBuffer(ByteBuffer targetBuffer) {
        return this.encodeIntoCompressedByteBuffer(targetBuffer, -1);
    }

    static <T extends AbstractHistogram> T decodeFromByteBuffer(ByteBuffer buffer, Class<T> histogramClass, long minBarForHighestTrackableValue) {
        try {
            return AbstractHistogram.decodeFromByteBuffer(buffer, histogramClass, minBarForHighestTrackableValue, null);
        }
        catch (DataFormatException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static <T extends AbstractHistogram> T decodeFromByteBuffer(ByteBuffer buffer, Class<T> histogramClass, long minBarForHighestTrackableValue, Inflater decompressor) throws DataFormatException {
        ByteBuffer payLoadSourceBuffer;
        AbstractHistogram histogram;
        double integerToDoubleValueConversionRatio;
        long highestTrackableValue;
        long lowestTrackableUnitValue;
        int numberOfSignificantValueDigits;
        int normalizingIndexOffset;
        int payloadLengthInBytes;
        int cookie = buffer.getInt();
        if (AbstractHistogram.getCookieBase(cookie) == 478450435 || AbstractHistogram.getCookieBase(cookie) == 478450433) {
            if (AbstractHistogram.getCookieBase(cookie) == 478450435 && AbstractHistogram.getWordSizeInBytesFromCookie(cookie) != 9) {
                throw new IllegalArgumentException("The buffer does not contain a Histogram (no valid cookie found)");
            }
            payloadLengthInBytes = buffer.getInt();
            normalizingIndexOffset = buffer.getInt();
            numberOfSignificantValueDigits = buffer.getInt();
            lowestTrackableUnitValue = buffer.getLong();
            highestTrackableValue = buffer.getLong();
            integerToDoubleValueConversionRatio = buffer.getDouble();
        } else if (AbstractHistogram.getCookieBase(cookie) == 478450440) {
            numberOfSignificantValueDigits = buffer.getInt();
            lowestTrackableUnitValue = buffer.getLong();
            highestTrackableValue = buffer.getLong();
            buffer.getLong();
            payloadLengthInBytes = Integer.MAX_VALUE;
            integerToDoubleValueConversionRatio = 1.0;
            normalizingIndexOffset = 0;
        } else {
            throw new IllegalArgumentException("The buffer does not contain a Histogram (no valid cookie found)");
        }
        highestTrackableValue = Math.max(highestTrackableValue, minBarForHighestTrackableValue);
        try {
            Constructor<T> constructor = histogramClass.getConstructor(constructorArgsTypes);
            histogram = (AbstractHistogram)constructor.newInstance(lowestTrackableUnitValue, highestTrackableValue, numberOfSignificantValueDigits);
            histogram.setIntegerToDoubleValueConversionRatio(integerToDoubleValueConversionRatio);
            histogram.setNormalizingIndexOffset(normalizingIndexOffset);
            try {
                histogram.setAutoResize(true);
            }
            catch (IllegalStateException illegalStateException) {}
        }
        catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException ex) {
            throw new IllegalArgumentException(ex);
        }
        int expectedCapacity = Math.min(histogram.getNeededV0PayloadByteBufferCapacity(histogram.countsArrayLength), payloadLengthInBytes);
        if (decompressor == null) {
            if (expectedCapacity > buffer.remaining()) {
                throw new IllegalArgumentException("The buffer does not contain the full Histogram payload");
            }
            payLoadSourceBuffer = buffer;
        } else {
            payLoadSourceBuffer = ByteBuffer.allocate(expectedCapacity).order(ByteOrder.BIG_ENDIAN);
            int decompressedByteCount = decompressor.inflate(payLoadSourceBuffer.array());
            if (payloadLengthInBytes != Integer.MAX_VALUE && decompressedByteCount < payloadLengthInBytes) {
                throw new IllegalArgumentException("The buffer does not contain the indicated payload amount");
            }
        }
        int filledLength = histogram.fillCountsArrayFromSourceBuffer(payLoadSourceBuffer, expectedCapacity, AbstractHistogram.getWordSizeInBytesFromCookie(cookie));
        histogram.establishInternalTackingValues(filledLength);
        return (T)histogram;
    }

    private int fillCountsArrayFromSourceBuffer(ByteBuffer sourceBuffer, int lengthInBytes, int wordSizeInBytes) {
        if (wordSizeInBytes != 2 && wordSizeInBytes != 4 && wordSizeInBytes != 8 && wordSizeInBytes != 9) {
            throw new IllegalArgumentException("word size must be 2, 4, 8, or V2maxWordSizeInBytes (9) bytes");
        }
        long maxAllowableCountInHistigram = this.wordSizeInBytes == 2 ? 32767L : (this.wordSizeInBytes == 4 ? Integer.MAX_VALUE : Long.MAX_VALUE);
        int dstIndex = 0;
        int endPosition = sourceBuffer.position() + lengthInBytes;
        while (sourceBuffer.position() < endPosition) {
            long count;
            int zerosCount = 0;
            if (wordSizeInBytes == 9) {
                count = ZigZagEncoding.getLong(sourceBuffer);
                if (count < 0L) {
                    long zc = -count;
                    if (zc > Integer.MAX_VALUE) {
                        throw new IllegalArgumentException("An encoded zero count of > Integer.MAX_VALUE was encountered in the source");
                    }
                    zerosCount = (int)zc;
                }
            } else {
                long l = wordSizeInBytes == 2 ? (long)sourceBuffer.getShort() : (count = wordSizeInBytes == 4 ? (long)sourceBuffer.getInt() : sourceBuffer.getLong());
            }
            if (count > maxAllowableCountInHistigram) {
                throw new IllegalArgumentException("An encoded count (" + count + ") does not fit in the Histogram's (" + this.wordSizeInBytes + " bytes) was encountered in the source");
            }
            if (zerosCount > 0) {
                dstIndex += zerosCount;
                continue;
            }
            this.setCountAtIndex(dstIndex++, count);
        }
        return dstIndex;
    }

    synchronized void fillBufferFromCountsArray(ByteBuffer buffer) {
        int countsLimit = this.countsArrayIndex(this.maxValue) + 1;
        int srcIndex = 0;
        while (srcIndex < countsLimit) {
            long count;
            if ((count = this.getCountAtIndex(srcIndex++)) < 0L) {
                throw new RuntimeException("Cannot encode histogram containing negative counts (" + count + ") at index " + srcIndex + ", corresponding the value range [" + this.lowestEquivalentValue(this.valueFromIndex(srcIndex)) + "," + this.nextNonEquivalentValue(this.valueFromIndex(srcIndex)) + ")");
            }
            long zerosCount = 0L;
            if (count == 0L) {
                zerosCount = 1L;
                while (srcIndex < countsLimit && this.getCountAtIndex(srcIndex) == 0L) {
                    ++zerosCount;
                    ++srcIndex;
                }
            }
            if (zerosCount > 1L) {
                ZigZagEncoding.putLong(buffer, -zerosCount);
                continue;
            }
            ZigZagEncoding.putLong(buffer, count);
        }
    }

    static <T extends AbstractHistogram> T decodeFromCompressedByteBuffer(ByteBuffer buffer, Class<T> histogramClass, long minBarForHighestTrackableValue) throws DataFormatException {
        int headerSize;
        int initialTargetPosition = buffer.position();
        int cookie = buffer.getInt();
        if (AbstractHistogram.getCookieBase(cookie) == 478450436 || AbstractHistogram.getCookieBase(cookie) == 478450434) {
            headerSize = 40;
        } else if (AbstractHistogram.getCookieBase(cookie) == 478450441) {
            headerSize = 32;
        } else {
            throw new IllegalArgumentException("The buffer does not contain a compressed Histogram");
        }
        int lengthOfCompressedContents = buffer.getInt();
        Inflater decompressor = new Inflater();
        if (buffer.hasArray()) {
            decompressor.setInput(buffer.array(), initialTargetPosition + 8, lengthOfCompressedContents);
        } else {
            byte[] compressedContents = new byte[lengthOfCompressedContents];
            buffer.get(compressedContents);
            decompressor.setInput(compressedContents);
        }
        ByteBuffer headerBuffer = ByteBuffer.allocate(headerSize).order(ByteOrder.BIG_ENDIAN);
        decompressor.inflate(headerBuffer.array());
        T histogram = AbstractHistogram.decodeFromByteBuffer(headerBuffer, histogramClass, minBarForHighestTrackableValue, decompressor);
        decompressor.end();
        return histogram;
    }

    private String recordedValuesToString() {
        String output = "";
        try {
            for (int i = 0; i < this.countsArrayLength; ++i) {
                if (this.getCountAtIndex(i) == 0L) continue;
                output = output + String.format("[%d] : %d\n", i, this.getCountAtIndex(i));
            }
            return output;
        }
        catch (Exception ex) {
            output = output + "!!! Exception thown in value iteration...\n";
            return output;
        }
    }

    public String toString() {
        String output = "AbstractHistogram:\n";
        output = output + super.toString();
        output = output + this.recordedValuesToString();
        return output;
    }

    void establishInternalTackingValues() {
        this.establishInternalTackingValues(this.countsArrayLength);
    }

    void establishInternalTackingValues(int lengthToCover) {
        this.resetMaxValue(0L);
        this.resetMinNonZeroValue(Long.MAX_VALUE);
        int maxIndex = -1;
        int minNonZeroIndex = -1;
        long observedTotalCount = 0L;
        for (int index = 0; index < lengthToCover; ++index) {
            long countAtIndex = this.getCountAtIndex(index);
            if (countAtIndex <= 0L) continue;
            observedTotalCount += countAtIndex;
            maxIndex = index;
            if (minNonZeroIndex != -1 || index == 0) continue;
            minNonZeroIndex = index;
        }
        if (maxIndex >= 0) {
            this.updatedMaxValue(this.highestEquivalentValue(this.valueFromIndex(maxIndex)));
        }
        if (minNonZeroIndex >= 0) {
            this.updateMinNonZeroValue(this.valueFromIndex(minNonZeroIndex));
        }
        this.setTotalCount(observedTotalCount);
    }

    int getBucketsNeededToCoverValue(long value) {
        long smallestUntrackableValue = (long)this.subBucketCount << this.unitMagnitude;
        int bucketsNeeded = 1;
        while (smallestUntrackableValue <= value) {
            if (smallestUntrackableValue > 0x3FFFFFFFFFFFFFFFL) {
                return bucketsNeeded + 1;
            }
            smallestUntrackableValue <<= 1;
            ++bucketsNeeded;
        }
        return bucketsNeeded;
    }

    int getLengthForNumberOfBuckets(int numberOfBuckets) {
        int lengthNeeded = (numberOfBuckets + 1) * this.subBucketHalfCount;
        return lengthNeeded;
    }

    int countsArrayIndex(long value) {
        if (value < 0L) {
            throw new ArrayIndexOutOfBoundsException("Histogram recorded value cannot be negative.");
        }
        int bucketIndex = this.getBucketIndex(value);
        int subBucketIndex = this.getSubBucketIndex(value, bucketIndex);
        return this.countsArrayIndex(bucketIndex, subBucketIndex);
    }

    private int countsArrayIndex(int bucketIndex, int subBucketIndex) {
        assert (subBucketIndex < this.subBucketCount);
        assert (bucketIndex == 0 || subBucketIndex >= this.subBucketHalfCount);
        int bucketBaseIndex = bucketIndex + 1 << this.subBucketHalfCountMagnitude;
        int offsetInBucket = subBucketIndex - this.subBucketHalfCount;
        return bucketBaseIndex + offsetInBucket;
    }

    int getBucketIndex(long value) {
        return this.leadingZeroCountBase - Long.numberOfLeadingZeros(value | this.subBucketMask);
    }

    int getSubBucketIndex(long value, int bucketIndex) {
        return (int)(value >>> bucketIndex + this.unitMagnitude);
    }

    int normalizeIndex(int index, int normalizingIndexOffset, int arrayLength) {
        if (normalizingIndexOffset == 0) {
            return index;
        }
        if (index > arrayLength || index < 0) {
            throw new ArrayIndexOutOfBoundsException("index out of covered value range");
        }
        int normalizedIndex = index - normalizingIndexOffset;
        if (normalizedIndex < 0) {
            normalizedIndex += arrayLength;
        } else if (normalizedIndex >= arrayLength) {
            normalizedIndex -= arrayLength;
        }
        return normalizedIndex;
    }

    private long valueFromIndex(int bucketIndex, int subBucketIndex) {
        return (long)subBucketIndex << bucketIndex + this.unitMagnitude;
    }

    final long valueFromIndex(int index) {
        int bucketIndex = (index >> this.subBucketHalfCountMagnitude) - 1;
        int subBucketIndex = (index & this.subBucketHalfCount - 1) + this.subBucketHalfCount;
        if (bucketIndex < 0) {
            subBucketIndex -= this.subBucketHalfCount;
            bucketIndex = 0;
        }
        return this.valueFromIndex(bucketIndex, subBucketIndex);
    }

    static int numberOfSubbuckets(int numberOfSignificantValueDigits) {
        long largestValueWithSingleUnitResolution = 2L * (long)Math.pow(10.0, numberOfSignificantValueDigits);
        int subBucketCountMagnitude = (int)Math.ceil(Math.log(largestValueWithSingleUnitResolution) / Math.log(2.0));
        int subBucketCount = (int)Math.pow(2.0, subBucketCountMagnitude);
        return subBucketCount;
    }

    public class AllValues
    implements Iterable<HistogramIterationValue> {
        final AbstractHistogram histogram;

        private AllValues(AbstractHistogram histogram) {
            this.histogram = histogram;
        }

        @Override
        public Iterator<HistogramIterationValue> iterator() {
            return new AllValuesIterator(this.histogram);
        }
    }

    public class RecordedValues
    implements Iterable<HistogramIterationValue> {
        final AbstractHistogram histogram;

        private RecordedValues(AbstractHistogram histogram) {
            this.histogram = histogram;
        }

        @Override
        public Iterator<HistogramIterationValue> iterator() {
            return new RecordedValuesIterator(this.histogram);
        }
    }

    public class LogarithmicBucketValues
    implements Iterable<HistogramIterationValue> {
        final AbstractHistogram histogram;
        final long valueUnitsInFirstBucket;
        final double logBase;

        private LogarithmicBucketValues(AbstractHistogram histogram, long valueUnitsInFirstBucket, double logBase) {
            this.histogram = histogram;
            this.valueUnitsInFirstBucket = valueUnitsInFirstBucket;
            this.logBase = logBase;
        }

        @Override
        public Iterator<HistogramIterationValue> iterator() {
            return new LogarithmicIterator(this.histogram, this.valueUnitsInFirstBucket, this.logBase);
        }
    }

    public class LinearBucketValues
    implements Iterable<HistogramIterationValue> {
        final AbstractHistogram histogram;
        final long valueUnitsPerBucket;

        private LinearBucketValues(AbstractHistogram histogram, long valueUnitsPerBucket) {
            this.histogram = histogram;
            this.valueUnitsPerBucket = valueUnitsPerBucket;
        }

        @Override
        public Iterator<HistogramIterationValue> iterator() {
            return new LinearIterator(this.histogram, this.valueUnitsPerBucket);
        }
    }

    public class Percentiles
    implements Iterable<HistogramIterationValue> {
        final AbstractHistogram histogram;
        final int percentileTicksPerHalfDistance;

        private Percentiles(AbstractHistogram histogram, int percentileTicksPerHalfDistance) {
            this.histogram = histogram;
            this.percentileTicksPerHalfDistance = percentileTicksPerHalfDistance;
        }

        @Override
        public Iterator<HistogramIterationValue> iterator() {
            return new PercentileIterator(this.histogram, this.percentileTicksPerHalfDistance);
        }
    }
}


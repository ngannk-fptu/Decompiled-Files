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
import java.util.Iterator;
import java.util.zip.DataFormatException;
import org.HdrHistogram.AbstractHistogram;
import org.HdrHistogram.AtomicHistogram;
import org.HdrHistogram.Base64Helper;
import org.HdrHistogram.DoubleAllValuesIterator;
import org.HdrHistogram.DoubleHistogramIterationValue;
import org.HdrHistogram.DoubleLinearIterator;
import org.HdrHistogram.DoubleLogarithmicIterator;
import org.HdrHistogram.DoublePercentileIterator;
import org.HdrHistogram.DoubleRecordedValuesIterator;
import org.HdrHistogram.DoubleValueRecorder;
import org.HdrHistogram.EncodableHistogram;
import org.HdrHistogram.Histogram;
import org.HdrHistogram.HistogramIterationValue;

public class DoubleHistogram
extends EncodableHistogram
implements DoubleValueRecorder,
Serializable {
    private static final double highestAllowedValueEver;
    private long configuredHighestToLowestValueRatio;
    private volatile double currentLowestValueInAutoRange;
    private volatile double currentHighestValueLimitInAutoRange;
    AbstractHistogram integerValuesHistogram;
    private boolean autoResize = false;
    private static final long serialVersionUID = 42L;
    private static final int DHIST_encodingCookie = 208802382;
    private static final int DHIST_compressedEncodingCookie = 208802383;
    private static final Class[] constructorArgTypes;

    public DoubleHistogram(int numberOfSignificantValueDigits) {
        this(2L, numberOfSignificantValueDigits, Histogram.class, null);
        this.setAutoResize(true);
    }

    public DoubleHistogram(int numberOfSignificantValueDigits, Class<? extends AbstractHistogram> internalCountsHistogramClass) {
        this(2L, numberOfSignificantValueDigits, internalCountsHistogramClass, null);
        this.setAutoResize(true);
    }

    public DoubleHistogram(long highestToLowestValueRatio, int numberOfSignificantValueDigits) {
        this(highestToLowestValueRatio, numberOfSignificantValueDigits, Histogram.class);
    }

    protected DoubleHistogram(long highestToLowestValueRatio, int numberOfSignificantValueDigits, Class<? extends AbstractHistogram> internalCountsHistogramClass) {
        this(highestToLowestValueRatio, numberOfSignificantValueDigits, internalCountsHistogramClass, null);
    }

    DoubleHistogram(long highestToLowestValueRatio, int numberOfSignificantValueDigits, Class<? extends AbstractHistogram> internalCountsHistogramClass, AbstractHistogram internalCountsHistogram) {
        this(highestToLowestValueRatio, numberOfSignificantValueDigits, internalCountsHistogramClass, internalCountsHistogram, false);
    }

    private DoubleHistogram(long highestToLowestValueRatio, int numberOfSignificantValueDigits, Class<? extends AbstractHistogram> internalCountsHistogramClass, AbstractHistogram internalCountsHistogram, boolean mimicInternalModel) {
        try {
            double initialLowestValueInAutoRange;
            AbstractHistogram valuesHistogram;
            if (highestToLowestValueRatio < 2L) {
                throw new IllegalArgumentException("highestToLowestValueRatio must be >= 2");
            }
            if ((double)highestToLowestValueRatio * Math.pow(10.0, numberOfSignificantValueDigits) >= 2.305843009213694E18) {
                throw new IllegalArgumentException("highestToLowestValueRatio * (10^numberOfSignificantValueDigits) must be < (1L << 61)");
            }
            if (internalCountsHistogramClass == AtomicHistogram.class) {
                throw new IllegalArgumentException("AtomicHistogram cannot be used as an internal counts histogram (does not support shifting). Use ConcurrentHistogram instead.");
            }
            long integerValueRange = this.deriveIntegerValueRange(highestToLowestValueRatio, numberOfSignificantValueDigits);
            if (internalCountsHistogram == null) {
                Constructor<? extends AbstractHistogram> histogramConstructor = internalCountsHistogramClass.getConstructor(Long.TYPE, Long.TYPE, Integer.TYPE);
                valuesHistogram = histogramConstructor.newInstance(1L, integerValueRange - 1L, numberOfSignificantValueDigits);
                initialLowestValueInAutoRange = Math.pow(2.0, 800.0);
            } else if (mimicInternalModel) {
                Constructor<? extends AbstractHistogram> histogramConstructor = internalCountsHistogramClass.getConstructor(AbstractHistogram.class);
                valuesHistogram = histogramConstructor.newInstance(internalCountsHistogram);
                initialLowestValueInAutoRange = Math.pow(2.0, 800.0);
            } else {
                if (internalCountsHistogram.getLowestDiscernibleValue() != 1L || internalCountsHistogram.getHighestTrackableValue() != integerValueRange - 1L || internalCountsHistogram.getNumberOfSignificantValueDigits() != numberOfSignificantValueDigits) {
                    throw new IllegalStateException("integer values histogram does not match stated parameters.");
                }
                valuesHistogram = internalCountsHistogram;
                initialLowestValueInAutoRange = internalCountsHistogram.getIntegerToDoubleValueConversionRatio() * (double)internalCountsHistogram.subBucketHalfCount;
            }
            this.init(highestToLowestValueRatio, initialLowestValueInAutoRange, valuesHistogram);
        }
        catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException ex) {
            throw new IllegalArgumentException(ex);
        }
    }

    public DoubleHistogram(DoubleHistogram source) {
        this(source.configuredHighestToLowestValueRatio, source.getNumberOfSignificantValueDigits(), source.integerValuesHistogram.getClass(), source.integerValuesHistogram, true);
        this.autoResize = source.autoResize;
        this.setTrackableValueRange(source.currentLowestValueInAutoRange, source.currentHighestValueLimitInAutoRange);
    }

    private void init(long configuredHighestToLowestValueRatio, double lowestTrackableUnitValue, AbstractHistogram integerValuesHistogram) {
        this.configuredHighestToLowestValueRatio = configuredHighestToLowestValueRatio;
        this.integerValuesHistogram = integerValuesHistogram;
        long internalHighestToLowestValueRatio = this.deriveInternalHighestToLowestValueRatio(configuredHighestToLowestValueRatio);
        this.setTrackableValueRange(lowestTrackableUnitValue, lowestTrackableUnitValue * (double)internalHighestToLowestValueRatio);
    }

    private void setTrackableValueRange(double lowestValueInAutoRange, double highestValueInAutoRange) {
        this.currentLowestValueInAutoRange = lowestValueInAutoRange;
        this.currentHighestValueLimitInAutoRange = highestValueInAutoRange;
        double integerToDoubleValueConversionRatio = lowestValueInAutoRange / (double)this.getLowestTrackingIntegerValue();
        this.integerValuesHistogram.setIntegerToDoubleValueConversionRatio(integerToDoubleValueConversionRatio);
    }

    double getDoubleToIntegerValueConversionRatio() {
        return this.integerValuesHistogram.getDoubleToIntegerValueConversionRatio();
    }

    public boolean isAutoResize() {
        return this.autoResize;
    }

    public void setAutoResize(boolean autoResize) {
        this.autoResize = autoResize;
    }

    @Override
    public void recordValue(double value) throws ArrayIndexOutOfBoundsException {
        this.recordSingleValue(value);
    }

    @Override
    public void recordValueWithCount(double value, long count) throws ArrayIndexOutOfBoundsException {
        this.recordCountAtValue(count, value);
    }

    @Override
    public void recordValueWithExpectedInterval(double value, double expectedIntervalBetweenValueSamples) throws ArrayIndexOutOfBoundsException {
        this.recordValueWithCountAndExpectedInterval(value, 1L, expectedIntervalBetweenValueSamples);
    }

    private void recordCountAtValue(long count, double value) throws ArrayIndexOutOfBoundsException {
        int throwCount = 0;
        while (true) {
            if (value < this.currentLowestValueInAutoRange || value >= this.currentHighestValueLimitInAutoRange) {
                this.autoAdjustRangeForValue(value);
            }
            try {
                this.integerValuesHistogram.recordConvertedDoubleValueWithCount(value, count);
                return;
            }
            catch (ArrayIndexOutOfBoundsException ex) {
                if (++throwCount <= 64) continue;
                throw new ArrayIndexOutOfBoundsException("BUG: Unexpected non-transient AIOOB Exception caused by:\n" + ex);
            }
            break;
        }
    }

    private void recordSingleValue(double value) throws ArrayIndexOutOfBoundsException {
        int throwCount = 0;
        while (true) {
            if (value < this.currentLowestValueInAutoRange || value >= this.currentHighestValueLimitInAutoRange) {
                this.autoAdjustRangeForValue(value);
            }
            try {
                this.integerValuesHistogram.recordConvertedDoubleValue(value);
                return;
            }
            catch (ArrayIndexOutOfBoundsException ex) {
                if (++throwCount <= 64) continue;
                throw new ArrayIndexOutOfBoundsException("BUG: Unexpected non-transient AIOOB Exception caused by:\n" + ex);
            }
            break;
        }
    }

    private void recordValueWithCountAndExpectedInterval(double value, long count, double expectedIntervalBetweenValueSamples) throws ArrayIndexOutOfBoundsException {
        this.recordCountAtValue(count, value);
        if (expectedIntervalBetweenValueSamples <= 0.0) {
            return;
        }
        for (double missingValue = value - expectedIntervalBetweenValueSamples; missingValue >= expectedIntervalBetweenValueSamples; missingValue -= expectedIntervalBetweenValueSamples) {
            this.recordCountAtValue(count, missingValue);
        }
    }

    private void autoAdjustRangeForValue(double value) {
        if (value == 0.0) {
            return;
        }
        this.autoAdjustRangeForValueSlowPath(value);
    }

    private synchronized void autoAdjustRangeForValueSlowPath(double value) {
        try {
            if (value < this.currentLowestValueInAutoRange) {
                if (value < 0.0) {
                    throw new ArrayIndexOutOfBoundsException("Negative values cannot be recorded");
                }
                do {
                    int shiftAmount = this.findCappedContainingBinaryOrderOfMagnitude(Math.ceil(this.currentLowestValueInAutoRange / value) - 1.0);
                    this.shiftCoveredRangeToTheRight(shiftAmount);
                } while (value < this.currentLowestValueInAutoRange);
            } else if (value >= this.currentHighestValueLimitInAutoRange) {
                if (value > highestAllowedValueEver) {
                    throw new ArrayIndexOutOfBoundsException("Values above " + highestAllowedValueEver + " cannot be recorded");
                }
                do {
                    int shiftAmount = this.findCappedContainingBinaryOrderOfMagnitude(Math.ceil((value + Math.ulp(value)) / this.currentHighestValueLimitInAutoRange) - 1.0);
                    this.shiftCoveredRangeToTheLeft(shiftAmount);
                } while (value >= this.currentHighestValueLimitInAutoRange);
            }
        }
        catch (ArrayIndexOutOfBoundsException ex) {
            throw new ArrayIndexOutOfBoundsException("The value " + value + " is out of bounds for histogram, current covered range [" + this.currentLowestValueInAutoRange + ", " + this.currentHighestValueLimitInAutoRange + ") cannot be extended any further.\nCaused by: " + ex);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void shiftCoveredRangeToTheRight(int numberOfBinaryOrdersOfMagnitude) {
        double newLowestValueInAutoRange = this.currentLowestValueInAutoRange;
        double newHighestValueLimitInAutoRange = this.currentHighestValueLimitInAutoRange;
        try {
            double shiftMultiplier = 1.0 / (double)(1L << numberOfBinaryOrdersOfMagnitude);
            this.currentHighestValueLimitInAutoRange *= shiftMultiplier;
            double newIntegerToDoubleValueConversionRatio = this.getIntegerToDoubleValueConversionRatio() * shiftMultiplier;
            if (this.getTotalCount() > this.integerValuesHistogram.getCountAtIndex(0)) {
                try {
                    this.integerValuesHistogram.shiftValuesLeft(numberOfBinaryOrdersOfMagnitude, newIntegerToDoubleValueConversionRatio);
                }
                catch (ArrayIndexOutOfBoundsException ex) {
                    this.handleShiftValuesException(numberOfBinaryOrdersOfMagnitude, ex);
                    newHighestValueLimitInAutoRange /= shiftMultiplier;
                    this.integerValuesHistogram.shiftValuesLeft(numberOfBinaryOrdersOfMagnitude, newIntegerToDoubleValueConversionRatio);
                }
            }
            newLowestValueInAutoRange *= shiftMultiplier;
            newHighestValueLimitInAutoRange *= shiftMultiplier;
        }
        finally {
            this.setTrackableValueRange(newLowestValueInAutoRange, newHighestValueLimitInAutoRange);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void shiftCoveredRangeToTheLeft(int numberOfBinaryOrdersOfMagnitude) {
        double newLowestValueInAutoRange = this.currentLowestValueInAutoRange;
        double newHighestValueLimitInAutoRange = this.currentHighestValueLimitInAutoRange;
        try {
            double shiftMultiplier = 1.0 * (double)(1L << numberOfBinaryOrdersOfMagnitude);
            double newIntegerToDoubleValueConversionRatio = this.getIntegerToDoubleValueConversionRatio() * shiftMultiplier;
            this.currentLowestValueInAutoRange *= shiftMultiplier;
            if (this.getTotalCount() > this.integerValuesHistogram.getCountAtIndex(0)) {
                try {
                    this.integerValuesHistogram.shiftValuesRight(numberOfBinaryOrdersOfMagnitude, newIntegerToDoubleValueConversionRatio);
                    newLowestValueInAutoRange *= shiftMultiplier;
                    newHighestValueLimitInAutoRange *= shiftMultiplier;
                }
                catch (ArrayIndexOutOfBoundsException ex) {
                    this.handleShiftValuesException(numberOfBinaryOrdersOfMagnitude, ex);
                    newLowestValueInAutoRange /= shiftMultiplier;
                }
            }
            newLowestValueInAutoRange *= shiftMultiplier;
            newHighestValueLimitInAutoRange *= shiftMultiplier;
        }
        finally {
            this.setTrackableValueRange(newLowestValueInAutoRange, newHighestValueLimitInAutoRange);
        }
    }

    private void handleShiftValuesException(int numberOfBinaryOrdersOfMagnitude, Exception ex) {
        if (!this.autoResize) {
            throw new ArrayIndexOutOfBoundsException("Value outside of histogram covered range.\nCaused by: " + ex);
        }
        long highestTrackableValue = this.integerValuesHistogram.getHighestTrackableValue();
        int currentContainingOrderOfMagnitude = DoubleHistogram.findContainingBinaryOrderOfMagnitude(highestTrackableValue);
        int newContainingOrderOfMagnitude = numberOfBinaryOrdersOfMagnitude + currentContainingOrderOfMagnitude;
        if (newContainingOrderOfMagnitude > 63) {
            throw new ArrayIndexOutOfBoundsException("Cannot resize histogram covered range beyond (1L << 63) / (1L << " + this.integerValuesHistogram.subBucketHalfCountMagnitude + ") - 1.\nCaused by: " + ex);
        }
        long newHighestTrackableValue = (1L << newContainingOrderOfMagnitude) - 1L;
        this.integerValuesHistogram.resize(newHighestTrackableValue);
        this.integerValuesHistogram.highestTrackableValue = newHighestTrackableValue;
        this.configuredHighestToLowestValueRatio <<= numberOfBinaryOrdersOfMagnitude;
    }

    @Override
    public void reset() {
        this.integerValuesHistogram.reset();
        double initialLowestValueInAutoRange = Math.pow(2.0, 800.0);
        this.init(this.configuredHighestToLowestValueRatio, initialLowestValueInAutoRange, this.integerValuesHistogram);
    }

    public DoubleHistogram copy() {
        DoubleHistogram targetHistogram = new DoubleHistogram(this.configuredHighestToLowestValueRatio, this.getNumberOfSignificantValueDigits());
        targetHistogram.setTrackableValueRange(this.currentLowestValueInAutoRange, this.currentHighestValueLimitInAutoRange);
        this.integerValuesHistogram.copyInto(targetHistogram.integerValuesHistogram);
        return targetHistogram;
    }

    public DoubleHistogram copyCorrectedForCoordinatedOmission(double expectedIntervalBetweenValueSamples) {
        DoubleHistogram targetHistogram = new DoubleHistogram(this.configuredHighestToLowestValueRatio, this.getNumberOfSignificantValueDigits());
        targetHistogram.setTrackableValueRange(this.currentLowestValueInAutoRange, this.currentHighestValueLimitInAutoRange);
        targetHistogram.addWhileCorrectingForCoordinatedOmission(this, expectedIntervalBetweenValueSamples);
        return targetHistogram;
    }

    public void copyInto(DoubleHistogram targetHistogram) {
        targetHistogram.reset();
        targetHistogram.add(this);
        targetHistogram.setStartTimeStamp(this.integerValuesHistogram.startTimeStampMsec);
        targetHistogram.setEndTimeStamp(this.integerValuesHistogram.endTimeStampMsec);
    }

    public void copyIntoCorrectedForCoordinatedOmission(DoubleHistogram targetHistogram, double expectedIntervalBetweenValueSamples) {
        targetHistogram.reset();
        targetHistogram.addWhileCorrectingForCoordinatedOmission(this, expectedIntervalBetweenValueSamples);
        targetHistogram.setStartTimeStamp(this.integerValuesHistogram.startTimeStampMsec);
        targetHistogram.setEndTimeStamp(this.integerValuesHistogram.endTimeStampMsec);
    }

    public void add(DoubleHistogram fromHistogram) throws ArrayIndexOutOfBoundsException {
        int arrayLength = fromHistogram.integerValuesHistogram.countsArrayLength;
        AbstractHistogram fromIntegerHistogram = fromHistogram.integerValuesHistogram;
        for (int i = 0; i < arrayLength; ++i) {
            long count = fromIntegerHistogram.getCountAtIndex(i);
            if (count <= 0L) continue;
            this.recordValueWithCount((double)fromIntegerHistogram.valueFromIndex(i) * fromHistogram.getIntegerToDoubleValueConversionRatio(), count);
        }
    }

    public void addWhileCorrectingForCoordinatedOmission(DoubleHistogram fromHistogram, double expectedIntervalBetweenValueSamples) {
        DoubleHistogram toHistogram = this;
        for (HistogramIterationValue v : fromHistogram.integerValuesHistogram.recordedValues()) {
            toHistogram.recordValueWithCountAndExpectedInterval((double)v.getValueIteratedTo() * this.getIntegerToDoubleValueConversionRatio(), v.getCountAtValueIteratedTo(), expectedIntervalBetweenValueSamples);
        }
    }

    public void subtract(DoubleHistogram otherHistogram) {
        int arrayLength = otherHistogram.integerValuesHistogram.countsArrayLength;
        AbstractHistogram otherIntegerHistogram = otherHistogram.integerValuesHistogram;
        for (int i = 0; i < arrayLength; ++i) {
            long otherCount = otherIntegerHistogram.getCountAtIndex(i);
            if (otherCount <= 0L) continue;
            double otherValue = (double)otherIntegerHistogram.valueFromIndex(i) * otherHistogram.getIntegerToDoubleValueConversionRatio();
            if (this.getCountAtValue(otherValue) < otherCount) {
                throw new IllegalArgumentException("otherHistogram count (" + otherCount + ") at value " + otherValue + " is larger than this one's (" + this.getCountAtValue(otherValue) + ")");
            }
            this.recordValueWithCount(otherValue, -otherCount);
        }
    }

    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof DoubleHistogram)) {
            return false;
        }
        DoubleHistogram that = (DoubleHistogram)other;
        return this.integerValuesHistogram.equals(that.integerValuesHistogram);
    }

    public int hashCode() {
        return this.integerValuesHistogram.hashCode();
    }

    public long getTotalCount() {
        return this.integerValuesHistogram.getTotalCount();
    }

    double getCurrentLowestTrackableNonZeroValue() {
        return this.currentLowestValueInAutoRange;
    }

    double getCurrentHighestTrackableValue() {
        return this.currentHighestValueLimitInAutoRange;
    }

    public double getIntegerToDoubleValueConversionRatio() {
        return this.integerValuesHistogram.integerToDoubleValueConversionRatio;
    }

    public int getNumberOfSignificantValueDigits() {
        return this.integerValuesHistogram.numberOfSignificantValueDigits;
    }

    public long getHighestToLowestValueRatio() {
        return this.configuredHighestToLowestValueRatio;
    }

    public double sizeOfEquivalentValueRange(double value) {
        return (double)this.integerValuesHistogram.sizeOfEquivalentValueRange((long)(value * this.getDoubleToIntegerValueConversionRatio())) * this.getIntegerToDoubleValueConversionRatio();
    }

    public double lowestEquivalentValue(double value) {
        return (double)this.integerValuesHistogram.lowestEquivalentValue((long)(value * this.getDoubleToIntegerValueConversionRatio())) * this.getIntegerToDoubleValueConversionRatio();
    }

    public double highestEquivalentValue(double value) {
        double nextNonEquivalentValue = this.nextNonEquivalentValue(value);
        double highestEquivalentValue = nextNonEquivalentValue - 2.0 * Math.ulp(nextNonEquivalentValue);
        while (highestEquivalentValue + Math.ulp(highestEquivalentValue) < nextNonEquivalentValue) {
            highestEquivalentValue += Math.ulp(highestEquivalentValue);
        }
        return highestEquivalentValue;
    }

    public double medianEquivalentValue(double value) {
        return (double)this.integerValuesHistogram.medianEquivalentValue((long)(value * this.getDoubleToIntegerValueConversionRatio())) * this.getIntegerToDoubleValueConversionRatio();
    }

    public double nextNonEquivalentValue(double value) {
        return (double)this.integerValuesHistogram.nextNonEquivalentValue((long)(value * this.getDoubleToIntegerValueConversionRatio())) * this.getIntegerToDoubleValueConversionRatio();
    }

    public boolean valuesAreEquivalent(double value1, double value2) {
        return this.lowestEquivalentValue(value1) == this.lowestEquivalentValue(value2);
    }

    public int getEstimatedFootprintInBytes() {
        return this.integerValuesHistogram._getEstimatedFootprintInBytes();
    }

    @Override
    public long getStartTimeStamp() {
        return this.integerValuesHistogram.getStartTimeStamp();
    }

    @Override
    public void setStartTimeStamp(long timeStampMsec) {
        this.integerValuesHistogram.setStartTimeStamp(timeStampMsec);
    }

    @Override
    public long getEndTimeStamp() {
        return this.integerValuesHistogram.getEndTimeStamp();
    }

    @Override
    public void setEndTimeStamp(long timeStampMsec) {
        this.integerValuesHistogram.setEndTimeStamp(timeStampMsec);
    }

    @Override
    public String getTag() {
        return this.integerValuesHistogram.getTag();
    }

    @Override
    public void setTag(String tag) {
        this.integerValuesHistogram.setTag(tag);
    }

    public double getMinValue() {
        return (double)this.integerValuesHistogram.getMinValue() * this.getIntegerToDoubleValueConversionRatio();
    }

    public double getMaxValue() {
        return (double)this.integerValuesHistogram.getMaxValue() * this.getIntegerToDoubleValueConversionRatio();
    }

    public double getMinNonZeroValue() {
        return (double)this.integerValuesHistogram.getMinNonZeroValue() * this.getIntegerToDoubleValueConversionRatio();
    }

    @Override
    public double getMaxValueAsDouble() {
        return this.getMaxValue();
    }

    public double getMean() {
        return this.integerValuesHistogram.getMean() * this.getIntegerToDoubleValueConversionRatio();
    }

    public double getStdDeviation() {
        return this.integerValuesHistogram.getStdDeviation() * this.getIntegerToDoubleValueConversionRatio();
    }

    public double getValueAtPercentile(double percentile) {
        return (double)this.integerValuesHistogram.getValueAtPercentile(percentile) * this.getIntegerToDoubleValueConversionRatio();
    }

    public double getPercentileAtOrBelowValue(double value) {
        return this.integerValuesHistogram.getPercentileAtOrBelowValue((long)(value * this.getDoubleToIntegerValueConversionRatio()));
    }

    public double getCountBetweenValues(double lowValue, double highValue) throws ArrayIndexOutOfBoundsException {
        return this.integerValuesHistogram.getCountBetweenValues((long)(lowValue * this.getDoubleToIntegerValueConversionRatio()), (long)(highValue * this.getDoubleToIntegerValueConversionRatio()));
    }

    public long getCountAtValue(double value) throws ArrayIndexOutOfBoundsException {
        return this.integerValuesHistogram.getCountAtValue((long)(value * this.getDoubleToIntegerValueConversionRatio()));
    }

    public Percentiles percentiles(int percentileTicksPerHalfDistance) {
        return new Percentiles(this, percentileTicksPerHalfDistance);
    }

    public LinearBucketValues linearBucketValues(double valueUnitsPerBucket) {
        return new LinearBucketValues(this, valueUnitsPerBucket);
    }

    public LogarithmicBucketValues logarithmicBucketValues(double valueUnitsInFirstBucket, double logBase) {
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
        this.integerValuesHistogram.outputPercentileDistribution(printStream, percentileTicksPerHalfDistance, outputValueUnitScalingRatio / this.getIntegerToDoubleValueConversionRatio(), useCsvFormat);
    }

    private void writeObject(ObjectOutputStream o) throws IOException {
        o.writeLong(this.configuredHighestToLowestValueRatio);
        o.writeDouble(this.currentLowestValueInAutoRange);
        o.writeObject(this.integerValuesHistogram);
    }

    private void readObject(ObjectInputStream o) throws IOException, ClassNotFoundException {
        long configuredHighestToLowestValueRatio = o.readLong();
        double lowestValueInAutoRange = o.readDouble();
        AbstractHistogram integerValuesHistogram = (AbstractHistogram)o.readObject();
        this.init(configuredHighestToLowestValueRatio, lowestValueInAutoRange, integerValuesHistogram);
    }

    @Override
    public int getNeededByteBufferCapacity() {
        return this.integerValuesHistogram.getNeededByteBufferCapacity();
    }

    private int getNeededByteBufferCapacity(int relevantLength) {
        return this.integerValuesHistogram.getNeededByteBufferCapacity(relevantLength);
    }

    static boolean isDoubleHistogramCookie(int cookie) {
        return DoubleHistogram.isCompressedDoubleHistogramCookie(cookie) || DoubleHistogram.isNonCompressedDoubleHistogramCookie(cookie);
    }

    static boolean isCompressedDoubleHistogramCookie(int cookie) {
        return cookie == 208802383;
    }

    static boolean isNonCompressedDoubleHistogramCookie(int cookie) {
        return cookie == 208802382;
    }

    public synchronized int encodeIntoByteBuffer(ByteBuffer buffer) {
        long maxValue = this.integerValuesHistogram.getMaxValue();
        int relevantLength = this.integerValuesHistogram.getLengthForNumberOfBuckets(this.integerValuesHistogram.getBucketsNeededToCoverValue(maxValue));
        if (buffer.capacity() < this.getNeededByteBufferCapacity(relevantLength)) {
            throw new ArrayIndexOutOfBoundsException("buffer does not have capacity for " + this.getNeededByteBufferCapacity(relevantLength) + " bytes");
        }
        buffer.putInt(208802382);
        buffer.putInt(this.getNumberOfSignificantValueDigits());
        buffer.putLong(this.configuredHighestToLowestValueRatio);
        return this.integerValuesHistogram.encodeIntoByteBuffer(buffer) + 16;
    }

    @Override
    public synchronized int encodeIntoCompressedByteBuffer(ByteBuffer targetBuffer, int compressionLevel) {
        targetBuffer.putInt(208802383);
        targetBuffer.putInt(this.getNumberOfSignificantValueDigits());
        targetBuffer.putLong(this.configuredHighestToLowestValueRatio);
        return this.integerValuesHistogram.encodeIntoCompressedByteBuffer(targetBuffer, compressionLevel) + 16;
    }

    public int encodeIntoCompressedByteBuffer(ByteBuffer targetBuffer) {
        return this.encodeIntoCompressedByteBuffer(targetBuffer, -1);
    }

    static <T extends DoubleHistogram> T constructHistogramFromBuffer(int cookie, ByteBuffer buffer, Class<T> doubleHistogramClass, Class<? extends AbstractHistogram> histogramClass, long minBarForHighestToLowestValueRatio) throws DataFormatException {
        AbstractHistogram valuesHistogram;
        int numberOfSignificantValueDigits = buffer.getInt();
        long configuredHighestToLowestValueRatio = buffer.getLong();
        if (DoubleHistogram.isNonCompressedDoubleHistogramCookie(cookie)) {
            valuesHistogram = AbstractHistogram.decodeFromByteBuffer(buffer, histogramClass, minBarForHighestToLowestValueRatio);
        } else if (DoubleHistogram.isCompressedDoubleHistogramCookie(cookie)) {
            valuesHistogram = AbstractHistogram.decodeFromCompressedByteBuffer(buffer, histogramClass, minBarForHighestToLowestValueRatio);
        } else {
            throw new IllegalArgumentException("The buffer does not contain a DoubleHistogram");
        }
        try {
            Constructor<T> doubleHistogramConstructor = doubleHistogramClass.getDeclaredConstructor(constructorArgTypes);
            DoubleHistogram histogram = (DoubleHistogram)doubleHistogramConstructor.newInstance(configuredHighestToLowestValueRatio, numberOfSignificantValueDigits, histogramClass, valuesHistogram);
            histogram.setAutoResize(true);
            return (T)histogram;
        }
        catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException ex) {
            throw new IllegalStateException("Unable to construct DoubleHistogram of type " + doubleHistogramClass);
        }
    }

    public static DoubleHistogram decodeFromByteBuffer(ByteBuffer buffer, long minBarForHighestToLowestValueRatio) {
        return DoubleHistogram.decodeFromByteBuffer(buffer, Histogram.class, minBarForHighestToLowestValueRatio);
    }

    public static DoubleHistogram decodeFromByteBuffer(ByteBuffer buffer, Class<? extends AbstractHistogram> internalCountsHistogramClass, long minBarForHighestToLowestValueRatio) {
        try {
            int cookie = buffer.getInt();
            if (!DoubleHistogram.isNonCompressedDoubleHistogramCookie(cookie)) {
                throw new IllegalArgumentException("The buffer does not contain a DoubleHistogram");
            }
            DoubleHistogram histogram = DoubleHistogram.constructHistogramFromBuffer(cookie, buffer, DoubleHistogram.class, internalCountsHistogramClass, minBarForHighestToLowestValueRatio);
            return histogram;
        }
        catch (DataFormatException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static DoubleHistogram decodeFromCompressedByteBuffer(ByteBuffer buffer, long minBarForHighestToLowestValueRatio) throws DataFormatException {
        return DoubleHistogram.decodeFromCompressedByteBuffer(buffer, Histogram.class, minBarForHighestToLowestValueRatio);
    }

    public static DoubleHistogram decodeFromCompressedByteBuffer(ByteBuffer buffer, Class<? extends AbstractHistogram> internalCountsHistogramClass, long minBarForHighestToLowestValueRatio) throws DataFormatException {
        int cookie = buffer.getInt();
        if (!DoubleHistogram.isCompressedDoubleHistogramCookie(cookie)) {
            throw new IllegalArgumentException("The buffer does not contain a compressed DoubleHistogram");
        }
        DoubleHistogram histogram = DoubleHistogram.constructHistogramFromBuffer(cookie, buffer, DoubleHistogram.class, internalCountsHistogramClass, minBarForHighestToLowestValueRatio);
        return histogram;
    }

    public static DoubleHistogram fromString(String base64CompressedHistogramString) throws DataFormatException {
        return DoubleHistogram.decodeFromCompressedByteBuffer(ByteBuffer.wrap(Base64Helper.parseBase64Binary(base64CompressedHistogramString)), 0L);
    }

    private long deriveInternalHighestToLowestValueRatio(long externalHighestToLowestValueRatio) {
        long internalHighestToLowestValueRatio = 1L << DoubleHistogram.findContainingBinaryOrderOfMagnitude(externalHighestToLowestValueRatio) + 1;
        return internalHighestToLowestValueRatio;
    }

    private long deriveIntegerValueRange(long externalHighestToLowestValueRatio, int numberOfSignificantValueDigits) {
        long internalHighestToLowestValueRatio = this.deriveInternalHighestToLowestValueRatio(externalHighestToLowestValueRatio);
        long lowestTackingIntegerValue = AbstractHistogram.numberOfSubbuckets(numberOfSignificantValueDigits) / 2;
        long integerValueRange = lowestTackingIntegerValue * internalHighestToLowestValueRatio;
        return integerValueRange;
    }

    private long getLowestTrackingIntegerValue() {
        return this.integerValuesHistogram.subBucketHalfCount;
    }

    private static int findContainingBinaryOrderOfMagnitude(long longNumber) {
        int pow2ceiling = 64 - Long.numberOfLeadingZeros(longNumber);
        return pow2ceiling;
    }

    private static int findContainingBinaryOrderOfMagnitude(double doubleNumber) {
        long longNumber = (long)Math.ceil(doubleNumber);
        return DoubleHistogram.findContainingBinaryOrderOfMagnitude(longNumber);
    }

    private int findCappedContainingBinaryOrderOfMagnitude(double doubleNumber) {
        if (doubleNumber > (double)this.configuredHighestToLowestValueRatio) {
            return (int)(Math.log(this.configuredHighestToLowestValueRatio) / Math.log(2.0));
        }
        if (doubleNumber > Math.pow(2.0, 50.0)) {
            return 50;
        }
        return DoubleHistogram.findContainingBinaryOrderOfMagnitude(doubleNumber);
    }

    static {
        double value;
        constructorArgTypes = new Class[]{Long.TYPE, Integer.TYPE, Class.class, AbstractHistogram.class};
        for (value = 1.0; value < 4.4942328371557893E307; value *= 2.0) {
        }
        highestAllowedValueEver = value;
    }

    public class AllValues
    implements Iterable<DoubleHistogramIterationValue> {
        final DoubleHistogram histogram;

        private AllValues(DoubleHistogram histogram) {
            this.histogram = histogram;
        }

        @Override
        public Iterator<DoubleHistogramIterationValue> iterator() {
            return new DoubleAllValuesIterator(this.histogram);
        }
    }

    public class RecordedValues
    implements Iterable<DoubleHistogramIterationValue> {
        final DoubleHistogram histogram;

        private RecordedValues(DoubleHistogram histogram) {
            this.histogram = histogram;
        }

        @Override
        public Iterator<DoubleHistogramIterationValue> iterator() {
            return new DoubleRecordedValuesIterator(this.histogram);
        }
    }

    public class LogarithmicBucketValues
    implements Iterable<DoubleHistogramIterationValue> {
        final DoubleHistogram histogram;
        final double valueUnitsInFirstBucket;
        final double logBase;

        private LogarithmicBucketValues(DoubleHistogram histogram, double valueUnitsInFirstBucket, double logBase) {
            this.histogram = histogram;
            this.valueUnitsInFirstBucket = valueUnitsInFirstBucket;
            this.logBase = logBase;
        }

        @Override
        public Iterator<DoubleHistogramIterationValue> iterator() {
            return new DoubleLogarithmicIterator(this.histogram, this.valueUnitsInFirstBucket, this.logBase);
        }
    }

    public class LinearBucketValues
    implements Iterable<DoubleHistogramIterationValue> {
        final DoubleHistogram histogram;
        final double valueUnitsPerBucket;

        private LinearBucketValues(DoubleHistogram histogram, double valueUnitsPerBucket) {
            this.histogram = histogram;
            this.valueUnitsPerBucket = valueUnitsPerBucket;
        }

        @Override
        public Iterator<DoubleHistogramIterationValue> iterator() {
            return new DoubleLinearIterator(this.histogram, this.valueUnitsPerBucket);
        }
    }

    public class Percentiles
    implements Iterable<DoubleHistogramIterationValue> {
        final DoubleHistogram histogram;
        final int percentileTicksPerHalfDistance;

        private Percentiles(DoubleHistogram histogram, int percentileTicksPerHalfDistance) {
            this.histogram = histogram;
            this.percentileTicksPerHalfDistance = percentileTicksPerHalfDistance;
        }

        @Override
        public Iterator<DoubleHistogramIterationValue> iterator() {
            return new DoublePercentileIterator(this.histogram, this.percentileTicksPerHalfDistance);
        }
    }
}


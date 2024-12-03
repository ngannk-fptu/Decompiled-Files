/*
 * Decompiled with CFR 0.152.
 */
package org.HdrHistogram;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.util.zip.DataFormatException;
import org.HdrHistogram.AbstractHistogram;
import org.HdrHistogram.Base64Helper;
import org.HdrHistogram.Histogram;

public class SynchronizedHistogram
extends Histogram {
    public SynchronizedHistogram(int numberOfSignificantValueDigits) {
        this(1L, 2L, numberOfSignificantValueDigits);
        this.setAutoResize(true);
    }

    public SynchronizedHistogram(long highestTrackableValue, int numberOfSignificantValueDigits) {
        this(1L, highestTrackableValue, numberOfSignificantValueDigits);
    }

    public SynchronizedHistogram(long lowestDiscernibleValue, long highestTrackableValue, int numberOfSignificantValueDigits) {
        super(lowestDiscernibleValue, highestTrackableValue, numberOfSignificantValueDigits);
    }

    public SynchronizedHistogram(AbstractHistogram source) {
        super(source);
    }

    public static SynchronizedHistogram decodeFromByteBuffer(ByteBuffer buffer, long minBarForHighestTrackableValue) {
        return SynchronizedHistogram.decodeFromByteBuffer(buffer, SynchronizedHistogram.class, minBarForHighestTrackableValue);
    }

    public static SynchronizedHistogram decodeFromCompressedByteBuffer(ByteBuffer buffer, long minBarForHighestTrackableValue) throws DataFormatException {
        return SynchronizedHistogram.decodeFromCompressedByteBuffer(buffer, SynchronizedHistogram.class, minBarForHighestTrackableValue);
    }

    public static SynchronizedHistogram fromString(String base64CompressedHistogramString) throws DataFormatException {
        return SynchronizedHistogram.decodeFromCompressedByteBuffer(ByteBuffer.wrap(Base64Helper.parseBase64Binary(base64CompressedHistogramString)), 0L);
    }

    @Override
    public synchronized long getTotalCount() {
        return super.getTotalCount();
    }

    @Override
    public synchronized boolean isAutoResize() {
        return super.isAutoResize();
    }

    @Override
    public synchronized void setAutoResize(boolean autoResize) {
        super.setAutoResize(autoResize);
    }

    @Override
    public synchronized void recordValue(long value) throws ArrayIndexOutOfBoundsException {
        super.recordValue(value);
    }

    @Override
    public synchronized void recordValueWithCount(long value, long count) throws ArrayIndexOutOfBoundsException {
        super.recordValueWithCount(value, count);
    }

    @Override
    public synchronized void recordValueWithExpectedInterval(long value, long expectedIntervalBetweenValueSamples) throws ArrayIndexOutOfBoundsException {
        super.recordValueWithExpectedInterval(value, expectedIntervalBetweenValueSamples);
    }

    @Override
    public synchronized void recordValue(long value, long expectedIntervalBetweenValueSamples) throws ArrayIndexOutOfBoundsException {
        super.recordValue(value, expectedIntervalBetweenValueSamples);
    }

    @Override
    public synchronized void reset() {
        super.reset();
    }

    @Override
    public synchronized SynchronizedHistogram copy() {
        SynchronizedHistogram toHistogram = new SynchronizedHistogram(this);
        toHistogram.add(this);
        return toHistogram;
    }

    @Override
    public synchronized SynchronizedHistogram copyCorrectedForCoordinatedOmission(long expectedIntervalBetweenValueSamples) {
        SynchronizedHistogram toHistogram = new SynchronizedHistogram(this);
        toHistogram.addWhileCorrectingForCoordinatedOmission(this, expectedIntervalBetweenValueSamples);
        return toHistogram;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void copyInto(AbstractHistogram targetHistogram) {
        if (this.identity < targetHistogram.identity) {
            SynchronizedHistogram synchronizedHistogram = this;
            synchronized (synchronizedHistogram) {
                AbstractHistogram abstractHistogram = targetHistogram;
                synchronized (abstractHistogram) {
                    super.copyInto(targetHistogram);
                }
            }
        }
        AbstractHistogram abstractHistogram = targetHistogram;
        synchronized (abstractHistogram) {
            SynchronizedHistogram synchronizedHistogram = this;
            synchronized (synchronizedHistogram) {
                super.copyInto(targetHistogram);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void copyIntoCorrectedForCoordinatedOmission(AbstractHistogram targetHistogram, long expectedIntervalBetweenValueSamples) {
        if (this.identity < targetHistogram.identity) {
            SynchronizedHistogram synchronizedHistogram = this;
            synchronized (synchronizedHistogram) {
                AbstractHistogram abstractHistogram = targetHistogram;
                synchronized (abstractHistogram) {
                    super.copyIntoCorrectedForCoordinatedOmission(targetHistogram, expectedIntervalBetweenValueSamples);
                }
            }
        }
        AbstractHistogram abstractHistogram = targetHistogram;
        synchronized (abstractHistogram) {
            SynchronizedHistogram synchronizedHistogram = this;
            synchronized (synchronizedHistogram) {
                super.copyIntoCorrectedForCoordinatedOmission(targetHistogram, expectedIntervalBetweenValueSamples);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void add(AbstractHistogram otherHistogram) {
        if (this.identity < otherHistogram.identity) {
            SynchronizedHistogram synchronizedHistogram = this;
            synchronized (synchronizedHistogram) {
                AbstractHistogram abstractHistogram = otherHistogram;
                synchronized (abstractHistogram) {
                    super.add(otherHistogram);
                }
            }
        }
        AbstractHistogram abstractHistogram = otherHistogram;
        synchronized (abstractHistogram) {
            SynchronizedHistogram synchronizedHistogram = this;
            synchronized (synchronizedHistogram) {
                super.add(otherHistogram);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void subtract(AbstractHistogram otherHistogram) throws ArrayIndexOutOfBoundsException, IllegalArgumentException {
        if (this.identity < otherHistogram.identity) {
            SynchronizedHistogram synchronizedHistogram = this;
            synchronized (synchronizedHistogram) {
                AbstractHistogram abstractHistogram = otherHistogram;
                synchronized (abstractHistogram) {
                    super.subtract(otherHistogram);
                }
            }
        }
        AbstractHistogram abstractHistogram = otherHistogram;
        synchronized (abstractHistogram) {
            SynchronizedHistogram synchronizedHistogram = this;
            synchronized (synchronizedHistogram) {
                super.subtract(otherHistogram);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void addWhileCorrectingForCoordinatedOmission(AbstractHistogram fromHistogram, long expectedIntervalBetweenValueSamples) {
        if (this.identity < fromHistogram.identity) {
            SynchronizedHistogram synchronizedHistogram = this;
            synchronized (synchronizedHistogram) {
                AbstractHistogram abstractHistogram = fromHistogram;
                synchronized (abstractHistogram) {
                    super.addWhileCorrectingForCoordinatedOmission(fromHistogram, expectedIntervalBetweenValueSamples);
                }
            }
        }
        AbstractHistogram abstractHistogram = fromHistogram;
        synchronized (abstractHistogram) {
            SynchronizedHistogram synchronizedHistogram = this;
            synchronized (synchronizedHistogram) {
                super.addWhileCorrectingForCoordinatedOmission(fromHistogram, expectedIntervalBetweenValueSamples);
            }
        }
    }

    @Override
    public synchronized void shiftValuesLeft(int numberOfBinaryOrdersOfMagnitude) {
        super.shiftValuesLeft(numberOfBinaryOrdersOfMagnitude);
    }

    @Override
    public synchronized void shiftValuesRight(int numberOfBinaryOrdersOfMagnitude) {
        super.shiftValuesRight(numberOfBinaryOrdersOfMagnitude);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof AbstractHistogram) {
            AbstractHistogram otherHistogram = (AbstractHistogram)other;
            if (this.identity < otherHistogram.identity) {
                SynchronizedHistogram synchronizedHistogram = this;
                synchronized (synchronizedHistogram) {
                    AbstractHistogram abstractHistogram = otherHistogram;
                    synchronized (abstractHistogram) {
                        return super.equals(otherHistogram);
                    }
                }
            }
            AbstractHistogram abstractHistogram = otherHistogram;
            synchronized (abstractHistogram) {
                SynchronizedHistogram synchronizedHistogram = this;
                synchronized (synchronizedHistogram) {
                    return super.equals(otherHistogram);
                }
            }
        }
        SynchronizedHistogram synchronizedHistogram = this;
        synchronized (synchronizedHistogram) {
            return super.equals(other);
        }
    }

    @Override
    public synchronized int hashCode() {
        return super.hashCode();
    }

    @Override
    public synchronized long getLowestDiscernibleValue() {
        return super.getLowestDiscernibleValue();
    }

    @Override
    public synchronized long getHighestTrackableValue() {
        return super.getHighestTrackableValue();
    }

    @Override
    public synchronized int getNumberOfSignificantValueDigits() {
        return super.getNumberOfSignificantValueDigits();
    }

    @Override
    public synchronized long sizeOfEquivalentValueRange(long value) {
        return super.sizeOfEquivalentValueRange(value);
    }

    @Override
    public synchronized long lowestEquivalentValue(long value) {
        return super.lowestEquivalentValue(value);
    }

    @Override
    public synchronized long highestEquivalentValue(long value) {
        return super.highestEquivalentValue(value);
    }

    @Override
    public synchronized long medianEquivalentValue(long value) {
        return super.medianEquivalentValue(value);
    }

    @Override
    public synchronized long nextNonEquivalentValue(long value) {
        return super.nextNonEquivalentValue(value);
    }

    @Override
    public synchronized boolean valuesAreEquivalent(long value1, long value2) {
        return super.valuesAreEquivalent(value1, value2);
    }

    @Override
    public synchronized int getEstimatedFootprintInBytes() {
        return super.getEstimatedFootprintInBytes();
    }

    @Override
    public synchronized long getStartTimeStamp() {
        return super.getStartTimeStamp();
    }

    @Override
    public synchronized void setStartTimeStamp(long timeStampMsec) {
        super.setStartTimeStamp(timeStampMsec);
    }

    @Override
    public synchronized long getEndTimeStamp() {
        return super.getEndTimeStamp();
    }

    @Override
    public synchronized void setEndTimeStamp(long timeStampMsec) {
        super.setEndTimeStamp(timeStampMsec);
    }

    @Override
    public synchronized long getMinValue() {
        return super.getMinValue();
    }

    @Override
    public synchronized long getMaxValue() {
        return super.getMaxValue();
    }

    @Override
    public synchronized long getMinNonZeroValue() {
        return super.getMinNonZeroValue();
    }

    @Override
    public synchronized double getMaxValueAsDouble() {
        return super.getMaxValueAsDouble();
    }

    @Override
    public synchronized double getMean() {
        return super.getMean();
    }

    @Override
    public synchronized double getStdDeviation() {
        return super.getStdDeviation();
    }

    @Override
    public synchronized long getValueAtPercentile(double percentile) {
        return super.getValueAtPercentile(percentile);
    }

    @Override
    public synchronized double getPercentileAtOrBelowValue(long value) {
        return super.getPercentileAtOrBelowValue(value);
    }

    @Override
    public synchronized long getCountBetweenValues(long lowValue, long highValue) throws ArrayIndexOutOfBoundsException {
        return super.getCountBetweenValues(lowValue, highValue);
    }

    @Override
    public synchronized long getCountAtValue(long value) throws ArrayIndexOutOfBoundsException {
        return super.getCountAtValue(value);
    }

    @Override
    public synchronized AbstractHistogram.Percentiles percentiles(int percentileTicksPerHalfDistance) {
        return super.percentiles(percentileTicksPerHalfDistance);
    }

    @Override
    public synchronized AbstractHistogram.LinearBucketValues linearBucketValues(long valueUnitsPerBucket) {
        return super.linearBucketValues(valueUnitsPerBucket);
    }

    @Override
    public synchronized AbstractHistogram.LogarithmicBucketValues logarithmicBucketValues(long valueUnitsInFirstBucket, double logBase) {
        return super.logarithmicBucketValues(valueUnitsInFirstBucket, logBase);
    }

    @Override
    public synchronized AbstractHistogram.RecordedValues recordedValues() {
        return super.recordedValues();
    }

    @Override
    public synchronized AbstractHistogram.AllValues allValues() {
        return super.allValues();
    }

    @Override
    public synchronized void outputPercentileDistribution(PrintStream printStream, Double outputValueUnitScalingRatio) {
        super.outputPercentileDistribution(printStream, outputValueUnitScalingRatio);
    }

    @Override
    public synchronized void outputPercentileDistribution(PrintStream printStream, int percentileTicksPerHalfDistance, Double outputValueUnitScalingRatio) {
        super.outputPercentileDistribution(printStream, percentileTicksPerHalfDistance, outputValueUnitScalingRatio);
    }

    @Override
    public synchronized void outputPercentileDistribution(PrintStream printStream, int percentileTicksPerHalfDistance, Double outputValueUnitScalingRatio, boolean useCsvFormat) {
        super.outputPercentileDistribution(printStream, percentileTicksPerHalfDistance, outputValueUnitScalingRatio, useCsvFormat);
    }

    @Override
    public synchronized int getNeededByteBufferCapacity() {
        return super.getNeededByteBufferCapacity();
    }

    @Override
    public synchronized int encodeIntoByteBuffer(ByteBuffer buffer) {
        return super.encodeIntoByteBuffer(buffer);
    }

    @Override
    public synchronized int encodeIntoCompressedByteBuffer(ByteBuffer targetBuffer, int compressionLevel) {
        return super.encodeIntoCompressedByteBuffer(targetBuffer, compressionLevel);
    }

    @Override
    public synchronized int encodeIntoCompressedByteBuffer(ByteBuffer targetBuffer) {
        return super.encodeIntoCompressedByteBuffer(targetBuffer);
    }

    private void readObject(ObjectInputStream o) throws IOException, ClassNotFoundException {
        o.defaultReadObject();
    }
}


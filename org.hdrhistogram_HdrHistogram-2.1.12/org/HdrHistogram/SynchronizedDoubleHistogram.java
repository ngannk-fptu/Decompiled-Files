/*
 * Decompiled with CFR 0.152.
 */
package org.HdrHistogram;

import java.io.PrintStream;
import java.nio.ByteBuffer;
import org.HdrHistogram.ConcurrentDoubleHistogram;
import org.HdrHistogram.DoubleHistogram;
import org.HdrHistogram.SynchronizedHistogram;

public class SynchronizedDoubleHistogram
extends DoubleHistogram {
    public SynchronizedDoubleHistogram(int numberOfSignificantValueDigits) {
        this(2L, numberOfSignificantValueDigits);
        this.setAutoResize(true);
    }

    public SynchronizedDoubleHistogram(long highestToLowestValueRatio, int numberOfSignificantValueDigits) {
        super(highestToLowestValueRatio, numberOfSignificantValueDigits, SynchronizedHistogram.class);
    }

    public SynchronizedDoubleHistogram(ConcurrentDoubleHistogram source) {
        super(source);
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
    public synchronized void recordValue(double value) throws ArrayIndexOutOfBoundsException {
        super.recordValue(value);
    }

    @Override
    public synchronized void recordValueWithCount(double value, long count) throws ArrayIndexOutOfBoundsException {
        super.recordValueWithCount(value, count);
    }

    @Override
    public synchronized void recordValueWithExpectedInterval(double value, double expectedIntervalBetweenValueSamples) throws ArrayIndexOutOfBoundsException {
        super.recordValueWithExpectedInterval(value, expectedIntervalBetweenValueSamples);
    }

    @Override
    public synchronized void reset() {
        super.reset();
    }

    @Override
    public synchronized DoubleHistogram copy() {
        DoubleHistogram targetHistogram = new DoubleHistogram(this);
        this.integerValuesHistogram.copyInto(targetHistogram.integerValuesHistogram);
        return targetHistogram;
    }

    @Override
    public synchronized DoubleHistogram copyCorrectedForCoordinatedOmission(double expectedIntervalBetweenValueSamples) {
        DoubleHistogram targetHistogram = new DoubleHistogram(this);
        targetHistogram.addWhileCorrectingForCoordinatedOmission(this, expectedIntervalBetweenValueSamples);
        return targetHistogram;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public synchronized void copyInto(DoubleHistogram targetHistogram) {
        if (this.integerValuesHistogram.identity < targetHistogram.integerValuesHistogram.identity) {
            SynchronizedDoubleHistogram synchronizedDoubleHistogram = this;
            synchronized (synchronizedDoubleHistogram) {
                DoubleHistogram doubleHistogram = targetHistogram;
                synchronized (doubleHistogram) {
                    super.copyInto(targetHistogram);
                }
            }
        }
        DoubleHistogram doubleHistogram = targetHistogram;
        synchronized (doubleHistogram) {
            SynchronizedDoubleHistogram synchronizedDoubleHistogram = this;
            synchronized (synchronizedDoubleHistogram) {
                super.copyInto(targetHistogram);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public synchronized void copyIntoCorrectedForCoordinatedOmission(DoubleHistogram targetHistogram, double expectedIntervalBetweenValueSamples) {
        if (this.integerValuesHistogram.identity < targetHistogram.integerValuesHistogram.identity) {
            SynchronizedDoubleHistogram synchronizedDoubleHistogram = this;
            synchronized (synchronizedDoubleHistogram) {
                DoubleHistogram doubleHistogram = targetHistogram;
                synchronized (doubleHistogram) {
                    super.copyIntoCorrectedForCoordinatedOmission(targetHistogram, expectedIntervalBetweenValueSamples);
                }
            }
        }
        DoubleHistogram doubleHistogram = targetHistogram;
        synchronized (doubleHistogram) {
            SynchronizedDoubleHistogram synchronizedDoubleHistogram = this;
            synchronized (synchronizedDoubleHistogram) {
                super.copyIntoCorrectedForCoordinatedOmission(targetHistogram, expectedIntervalBetweenValueSamples);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public synchronized void add(DoubleHistogram fromHistogram) throws ArrayIndexOutOfBoundsException {
        if (this.integerValuesHistogram.identity < fromHistogram.integerValuesHistogram.identity) {
            SynchronizedDoubleHistogram synchronizedDoubleHistogram = this;
            synchronized (synchronizedDoubleHistogram) {
                DoubleHistogram doubleHistogram = fromHistogram;
                synchronized (doubleHistogram) {
                    super.add(fromHistogram);
                }
            }
        }
        DoubleHistogram doubleHistogram = fromHistogram;
        synchronized (doubleHistogram) {
            SynchronizedDoubleHistogram synchronizedDoubleHistogram = this;
            synchronized (synchronizedDoubleHistogram) {
                super.add(fromHistogram);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public synchronized void subtract(DoubleHistogram fromHistogram) {
        if (this.integerValuesHistogram.identity < fromHistogram.integerValuesHistogram.identity) {
            SynchronizedDoubleHistogram synchronizedDoubleHistogram = this;
            synchronized (synchronizedDoubleHistogram) {
                DoubleHistogram doubleHistogram = fromHistogram;
                synchronized (doubleHistogram) {
                    super.subtract(fromHistogram);
                }
            }
        }
        DoubleHistogram doubleHistogram = fromHistogram;
        synchronized (doubleHistogram) {
            SynchronizedDoubleHistogram synchronizedDoubleHistogram = this;
            synchronized (synchronizedDoubleHistogram) {
                super.subtract(fromHistogram);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public synchronized void addWhileCorrectingForCoordinatedOmission(DoubleHistogram fromHistogram, double expectedIntervalBetweenValueSamples) {
        if (this.integerValuesHistogram.identity < fromHistogram.integerValuesHistogram.identity) {
            SynchronizedDoubleHistogram synchronizedDoubleHistogram = this;
            synchronized (synchronizedDoubleHistogram) {
                DoubleHistogram doubleHistogram = fromHistogram;
                synchronized (doubleHistogram) {
                    super.addWhileCorrectingForCoordinatedOmission(fromHistogram, expectedIntervalBetweenValueSamples);
                }
            }
        }
        DoubleHistogram doubleHistogram = fromHistogram;
        synchronized (doubleHistogram) {
            SynchronizedDoubleHistogram synchronizedDoubleHistogram = this;
            synchronized (synchronizedDoubleHistogram) {
                super.addWhileCorrectingForCoordinatedOmission(fromHistogram, expectedIntervalBetweenValueSamples);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public synchronized boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other instanceof DoubleHistogram) {
            DoubleHistogram otherHistogram = (DoubleHistogram)other;
            if (this.integerValuesHistogram.identity < otherHistogram.integerValuesHistogram.identity) {
                SynchronizedDoubleHistogram synchronizedDoubleHistogram = this;
                synchronized (synchronizedDoubleHistogram) {
                    DoubleHistogram doubleHistogram = otherHistogram;
                    synchronized (doubleHistogram) {
                        return super.equals(otherHistogram);
                    }
                }
            }
            DoubleHistogram doubleHistogram = otherHistogram;
            synchronized (doubleHistogram) {
                SynchronizedDoubleHistogram synchronizedDoubleHistogram = this;
                synchronized (synchronizedDoubleHistogram) {
                    return super.equals(otherHistogram);
                }
            }
        }
        SynchronizedDoubleHistogram synchronizedDoubleHistogram = this;
        synchronized (synchronizedDoubleHistogram) {
            return super.equals(other);
        }
    }

    @Override
    public synchronized int hashCode() {
        return super.hashCode();
    }

    @Override
    public synchronized long getTotalCount() {
        return super.getTotalCount();
    }

    @Override
    public synchronized double getIntegerToDoubleValueConversionRatio() {
        return super.getIntegerToDoubleValueConversionRatio();
    }

    @Override
    public synchronized int getNumberOfSignificantValueDigits() {
        return super.getNumberOfSignificantValueDigits();
    }

    @Override
    public synchronized long getHighestToLowestValueRatio() {
        return super.getHighestToLowestValueRatio();
    }

    @Override
    public synchronized double sizeOfEquivalentValueRange(double value) {
        return super.sizeOfEquivalentValueRange(value);
    }

    @Override
    public synchronized double lowestEquivalentValue(double value) {
        return super.lowestEquivalentValue(value);
    }

    @Override
    public synchronized double highestEquivalentValue(double value) {
        return super.highestEquivalentValue(value);
    }

    @Override
    public synchronized double medianEquivalentValue(double value) {
        return super.medianEquivalentValue(value);
    }

    @Override
    public synchronized double nextNonEquivalentValue(double value) {
        return super.nextNonEquivalentValue(value);
    }

    @Override
    public synchronized boolean valuesAreEquivalent(double value1, double value2) {
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
    public synchronized double getMinValue() {
        return super.getMinValue();
    }

    @Override
    public synchronized double getMaxValue() {
        return super.getMaxValue();
    }

    @Override
    public synchronized double getMinNonZeroValue() {
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
    public synchronized double getValueAtPercentile(double percentile) {
        return super.getValueAtPercentile(percentile);
    }

    @Override
    public synchronized double getPercentileAtOrBelowValue(double value) {
        return super.getPercentileAtOrBelowValue(value);
    }

    @Override
    public synchronized double getCountBetweenValues(double lowValue, double highValue) throws ArrayIndexOutOfBoundsException {
        return super.getCountBetweenValues(lowValue, highValue);
    }

    @Override
    public synchronized long getCountAtValue(double value) throws ArrayIndexOutOfBoundsException {
        return super.getCountAtValue(value);
    }

    @Override
    public synchronized DoubleHistogram.Percentiles percentiles(int percentileTicksPerHalfDistance) {
        return super.percentiles(percentileTicksPerHalfDistance);
    }

    @Override
    public synchronized DoubleHistogram.LinearBucketValues linearBucketValues(double valueUnitsPerBucket) {
        return super.linearBucketValues(valueUnitsPerBucket);
    }

    @Override
    public synchronized DoubleHistogram.LogarithmicBucketValues logarithmicBucketValues(double valueUnitsInFirstBucket, double logBase) {
        return super.logarithmicBucketValues(valueUnitsInFirstBucket, logBase);
    }

    @Override
    public synchronized DoubleHistogram.RecordedValues recordedValues() {
        return super.recordedValues();
    }

    @Override
    public synchronized DoubleHistogram.AllValues allValues() {
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
}


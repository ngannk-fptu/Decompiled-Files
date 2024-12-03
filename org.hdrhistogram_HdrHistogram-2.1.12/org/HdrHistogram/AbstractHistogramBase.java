/*
 * Decompiled with CFR 0.152.
 */
package org.HdrHistogram;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicLong;
import org.HdrHistogram.EncodableHistogram;
import org.HdrHistogram.PercentileIterator;
import org.HdrHistogram.RecordedValuesIterator;

abstract class AbstractHistogramBase
extends EncodableHistogram {
    static AtomicLong constructionIdentityCount = new AtomicLong(0L);
    long identity;
    volatile boolean autoResize = false;
    long highestTrackableValue;
    long lowestDiscernibleValue;
    int numberOfSignificantValueDigits;
    int bucketCount;
    int subBucketCount;
    int countsArrayLength;
    int wordSizeInBytes;
    long startTimeStampMsec = Long.MAX_VALUE;
    long endTimeStampMsec = 0L;
    String tag = null;
    double integerToDoubleValueConversionRatio = 1.0;
    double doubleToIntegerValueConversionRatio = 1.0;
    PercentileIterator percentileIterator;
    RecordedValuesIterator recordedValuesIterator;
    ByteBuffer intermediateUncompressedByteBuffer = null;
    byte[] intermediateUncompressedByteArray = null;

    AbstractHistogramBase() {
    }

    double getIntegerToDoubleValueConversionRatio() {
        return this.integerToDoubleValueConversionRatio;
    }

    double getDoubleToIntegerValueConversionRatio() {
        return this.doubleToIntegerValueConversionRatio;
    }

    void nonConcurrentSetIntegerToDoubleValueConversionRatio(double integerToDoubleValueConversionRatio) {
        this.integerToDoubleValueConversionRatio = integerToDoubleValueConversionRatio;
        this.doubleToIntegerValueConversionRatio = 1.0 / integerToDoubleValueConversionRatio;
    }

    abstract void setIntegerToDoubleValueConversionRatio(double var1);
}


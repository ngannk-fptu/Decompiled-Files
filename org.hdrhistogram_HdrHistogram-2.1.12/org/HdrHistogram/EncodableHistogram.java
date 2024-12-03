/*
 * Decompiled with CFR 0.152.
 */
package org.HdrHistogram;

import java.nio.ByteBuffer;
import java.util.zip.DataFormatException;
import org.HdrHistogram.DoubleHistogram;
import org.HdrHistogram.Histogram;

public abstract class EncodableHistogram {
    public abstract int getNeededByteBufferCapacity();

    public abstract int encodeIntoCompressedByteBuffer(ByteBuffer var1, int var2);

    public abstract long getStartTimeStamp();

    public abstract void setStartTimeStamp(long var1);

    public abstract long getEndTimeStamp();

    public abstract void setEndTimeStamp(long var1);

    public abstract String getTag();

    public abstract void setTag(String var1);

    public abstract double getMaxValueAsDouble();

    static EncodableHistogram decodeFromCompressedByteBuffer(ByteBuffer buffer, long minBarForHighestTrackableValue) throws DataFormatException {
        int cookie = buffer.getInt(buffer.position());
        if (DoubleHistogram.isDoubleHistogramCookie(cookie)) {
            return DoubleHistogram.decodeFromCompressedByteBuffer(buffer, minBarForHighestTrackableValue);
        }
        return Histogram.decodeFromCompressedByteBuffer(buffer, minBarForHighestTrackableValue);
    }
}


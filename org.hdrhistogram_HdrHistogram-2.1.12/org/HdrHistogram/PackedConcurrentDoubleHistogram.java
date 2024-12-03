/*
 * Decompiled with CFR 0.152.
 */
package org.HdrHistogram;

import java.nio.ByteBuffer;
import java.util.zip.DataFormatException;
import org.HdrHistogram.AbstractHistogram;
import org.HdrHistogram.ConcurrentDoubleHistogram;
import org.HdrHistogram.DoubleHistogram;
import org.HdrHistogram.PackedConcurrentHistogram;

public class PackedConcurrentDoubleHistogram
extends ConcurrentDoubleHistogram {
    public PackedConcurrentDoubleHistogram(int numberOfSignificantValueDigits) {
        this(2L, numberOfSignificantValueDigits);
        this.setAutoResize(true);
    }

    public PackedConcurrentDoubleHistogram(long highestToLowestValueRatio, int numberOfSignificantValueDigits) {
        this(highestToLowestValueRatio, numberOfSignificantValueDigits, PackedConcurrentHistogram.class);
    }

    public PackedConcurrentDoubleHistogram(DoubleHistogram source) {
        super(source);
    }

    PackedConcurrentDoubleHistogram(long highestToLowestValueRatio, int numberOfSignificantValueDigits, Class<? extends AbstractHistogram> internalCountsHistogramClass) {
        super(highestToLowestValueRatio, numberOfSignificantValueDigits, internalCountsHistogramClass);
    }

    PackedConcurrentDoubleHistogram(long highestToLowestValueRatio, int numberOfSignificantValueDigits, Class<? extends AbstractHistogram> internalCountsHistogramClass, AbstractHistogram internalCountsHistogram) {
        super(highestToLowestValueRatio, numberOfSignificantValueDigits, internalCountsHistogramClass, internalCountsHistogram);
    }

    public static PackedConcurrentDoubleHistogram decodeFromByteBuffer(ByteBuffer buffer, long minBarForHighestToLowestValueRatio) {
        try {
            int cookie = buffer.getInt();
            if (!PackedConcurrentDoubleHistogram.isNonCompressedDoubleHistogramCookie(cookie)) {
                throw new IllegalArgumentException("The buffer does not contain a DoubleHistogram");
            }
            PackedConcurrentDoubleHistogram histogram = PackedConcurrentDoubleHistogram.constructHistogramFromBuffer(cookie, buffer, PackedConcurrentDoubleHistogram.class, PackedConcurrentHistogram.class, minBarForHighestToLowestValueRatio);
            return histogram;
        }
        catch (DataFormatException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static PackedConcurrentDoubleHistogram decodeFromCompressedByteBuffer(ByteBuffer buffer, long minBarForHighestToLowestValueRatio) throws DataFormatException {
        int cookie = buffer.getInt();
        if (!PackedConcurrentDoubleHistogram.isCompressedDoubleHistogramCookie(cookie)) {
            throw new IllegalArgumentException("The buffer does not contain a compressed DoubleHistogram");
        }
        PackedConcurrentDoubleHistogram histogram = PackedConcurrentDoubleHistogram.constructHistogramFromBuffer(cookie, buffer, PackedConcurrentDoubleHistogram.class, PackedConcurrentHistogram.class, minBarForHighestToLowestValueRatio);
        return histogram;
    }
}


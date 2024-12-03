/*
 * Decompiled with CFR 0.152.
 */
package org.HdrHistogram;

import java.nio.ByteBuffer;
import java.util.zip.DataFormatException;
import org.HdrHistogram.AbstractHistogram;
import org.HdrHistogram.ConcurrentHistogram;
import org.HdrHistogram.DoubleHistogram;

public class ConcurrentDoubleHistogram
extends DoubleHistogram {
    public ConcurrentDoubleHistogram(int numberOfSignificantValueDigits) {
        this(2L, numberOfSignificantValueDigits);
        this.setAutoResize(true);
    }

    public ConcurrentDoubleHistogram(long highestToLowestValueRatio, int numberOfSignificantValueDigits) {
        this(highestToLowestValueRatio, numberOfSignificantValueDigits, ConcurrentHistogram.class);
    }

    public ConcurrentDoubleHistogram(DoubleHistogram source) {
        super(source);
    }

    ConcurrentDoubleHistogram(long highestToLowestValueRatio, int numberOfSignificantValueDigits, Class<? extends AbstractHistogram> internalCountsHistogramClass) {
        super(highestToLowestValueRatio, numberOfSignificantValueDigits, internalCountsHistogramClass);
    }

    ConcurrentDoubleHistogram(long highestToLowestValueRatio, int numberOfSignificantValueDigits, Class<? extends AbstractHistogram> internalCountsHistogramClass, AbstractHistogram internalCountsHistogram) {
        super(highestToLowestValueRatio, numberOfSignificantValueDigits, internalCountsHistogramClass, internalCountsHistogram);
    }

    public static ConcurrentDoubleHistogram decodeFromByteBuffer(ByteBuffer buffer, long minBarForHighestToLowestValueRatio) {
        try {
            int cookie = buffer.getInt();
            if (!ConcurrentDoubleHistogram.isNonCompressedDoubleHistogramCookie(cookie)) {
                throw new IllegalArgumentException("The buffer does not contain a DoubleHistogram");
            }
            ConcurrentDoubleHistogram histogram = ConcurrentDoubleHistogram.constructHistogramFromBuffer(cookie, buffer, ConcurrentDoubleHistogram.class, ConcurrentHistogram.class, minBarForHighestToLowestValueRatio);
            return histogram;
        }
        catch (DataFormatException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static ConcurrentDoubleHistogram decodeFromCompressedByteBuffer(ByteBuffer buffer, long minBarForHighestToLowestValueRatio) throws DataFormatException {
        int cookie = buffer.getInt();
        if (!ConcurrentDoubleHistogram.isCompressedDoubleHistogramCookie(cookie)) {
            throw new IllegalArgumentException("The buffer does not contain a compressed DoubleHistogram");
        }
        ConcurrentDoubleHistogram histogram = ConcurrentDoubleHistogram.constructHistogramFromBuffer(cookie, buffer, ConcurrentDoubleHistogram.class, ConcurrentHistogram.class, minBarForHighestToLowestValueRatio);
        return histogram;
    }
}


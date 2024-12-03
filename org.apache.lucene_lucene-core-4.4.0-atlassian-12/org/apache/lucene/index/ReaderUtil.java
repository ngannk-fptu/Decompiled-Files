/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.index;

import java.util.List;
import org.apache.lucene.index.AtomicReaderContext;
import org.apache.lucene.index.IndexReaderContext;

public final class ReaderUtil {
    private ReaderUtil() {
    }

    public static IndexReaderContext getTopLevelContext(IndexReaderContext context) {
        while (context.parent != null) {
            context = context.parent;
        }
        return context;
    }

    public static int subIndex(int n, int[] docStarts) {
        int size = docStarts.length;
        int lo = 0;
        int hi = size - 1;
        while (hi >= lo) {
            int mid = lo + hi >>> 1;
            int midValue = docStarts[mid];
            if (n < midValue) {
                hi = mid - 1;
                continue;
            }
            if (n > midValue) {
                lo = mid + 1;
                continue;
            }
            while (mid + 1 < size && docStarts[mid + 1] == midValue) {
                ++mid;
            }
            return mid;
        }
        return hi;
    }

    public static int subIndex(int n, List<AtomicReaderContext> leaves) {
        int size = leaves.size();
        int lo = 0;
        int hi = size - 1;
        while (hi >= lo) {
            int mid = lo + hi >>> 1;
            int midValue = leaves.get((int)mid).docBase;
            if (n < midValue) {
                hi = mid - 1;
                continue;
            }
            if (n > midValue) {
                lo = mid + 1;
                continue;
            }
            while (mid + 1 < size && leaves.get((int)(mid + 1)).docBase == midValue) {
                ++mid;
            }
            return mid;
        }
        return hi;
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xssf.util;

import java.util.Comparator;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCol;

public class CTColComparator {
    public static final Comparator<CTCol> BY_MAX = (col1, col2) -> {
        long col1max = col1.getMax();
        long col2max = col2.getMax();
        return Long.compare(col1max, col2max);
    };
    public static final Comparator<CTCol> BY_MIN_MAX = (col1, col2) -> {
        long col2min;
        long col11min = col1.getMin();
        return col11min < (col2min = col2.getMin()) ? -1 : (col11min > col2min ? 1 : BY_MAX.compare((CTCol)col1, (CTCol)col2));
    };

    private CTColComparator() {
    }
}


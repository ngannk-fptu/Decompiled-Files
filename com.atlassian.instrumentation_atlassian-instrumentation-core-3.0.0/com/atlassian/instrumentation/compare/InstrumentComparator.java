/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.instrumentation.compare;

import com.atlassian.instrumentation.Instrument;
import java.util.Comparator;

public class InstrumentComparator
implements Comparator<Instrument> {
    @Override
    public int compare(Instrument o1, Instrument o2) {
        if (o1 == o2) {
            return 0;
        }
        if (o1 == null && o2 != null) {
            return -1;
        }
        if (o1 != null && o2 == null) {
            return 1;
        }
        long rc = o2.getValue() - o1.getValue();
        if (rc == 0L) {
            rc = o1.getName().compareTo(o2.getName());
        }
        return rc > 0L ? 1 : (rc < 0L ? -1 : 0);
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.stats;

import java.util.Comparator;
import org.apache.jackrabbit.stats.QueryStatDtoImpl;

public class QueryStatDtoOccurrenceComparator
implements Comparator<QueryStatDtoImpl> {
    @Override
    public int compare(QueryStatDtoImpl o1, QueryStatDtoImpl o2) {
        return new Integer(o1.getOccurrenceCount()).compareTo(o2.getOccurrenceCount());
    }
}


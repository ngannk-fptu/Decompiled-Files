/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.stats;

import java.util.Comparator;
import org.apache.jackrabbit.api.stats.QueryStatDto;

public class QueryStatDtoComparator
implements Comparator<QueryStatDto> {
    @Override
    public int compare(QueryStatDto o1, QueryStatDto o2) {
        return new Long(o1.getDuration()).compareTo(o2.getDuration());
    }
}


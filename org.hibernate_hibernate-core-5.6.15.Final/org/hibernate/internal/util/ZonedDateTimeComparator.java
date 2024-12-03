/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.internal.util;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Comparator;

public class ZonedDateTimeComparator
implements Comparator<ZonedDateTime>,
Serializable {
    public static final Comparator INSTANCE = new ZonedDateTimeComparator();

    @Override
    public int compare(ZonedDateTime one, ZonedDateTime another) {
        return one.toInstant().compareTo(another.toInstant());
    }
}


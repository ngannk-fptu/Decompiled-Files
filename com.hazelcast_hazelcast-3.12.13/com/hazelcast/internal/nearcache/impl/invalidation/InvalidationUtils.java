/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.internal.nearcache.impl.invalidation;

import com.hazelcast.core.IFunction;
import com.hazelcast.nio.serialization.SerializableByConvention;
import com.hazelcast.spi.EventRegistration;

public final class InvalidationUtils {
    public static final long NO_SEQUENCE = -1L;
    public static final IFunction<EventRegistration, Boolean> TRUE_FILTER = new TrueFilter();

    private InvalidationUtils() {
    }

    @SerializableByConvention
    private static class TrueFilter
    implements IFunction<EventRegistration, Boolean> {
        private TrueFilter() {
        }

        @Override
        public Boolean apply(EventRegistration eventRegistration) {
            return Boolean.TRUE;
        }
    }
}


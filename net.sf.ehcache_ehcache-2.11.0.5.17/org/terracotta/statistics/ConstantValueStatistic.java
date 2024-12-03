/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.statistics;

import java.util.HashMap;
import java.util.Map;
import org.terracotta.statistics.ValueStatistic;

public class ConstantValueStatistic<T extends Number>
implements ValueStatistic<T> {
    private static final Map<Object, ValueStatistic<?>> common = new HashMap();
    private final T value;

    public static <T extends Number> ValueStatistic<T> instance(T value) {
        ValueStatistic<?> interned = common.get(value);
        if (interned == null) {
            return new ConstantValueStatistic<T>(value);
        }
        return interned;
    }

    private ConstantValueStatistic(T value) {
        this.value = value;
    }

    @Override
    public T value() {
        return this.value;
    }

    static {
        common.put(0, new ConstantValueStatistic<Integer>(0));
        common.put(0L, new ConstantValueStatistic<Long>(0L));
        common.put(null, new ConstantValueStatistic<Object>(null));
    }
}


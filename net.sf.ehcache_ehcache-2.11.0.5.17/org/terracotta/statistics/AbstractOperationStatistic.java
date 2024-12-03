/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.statistics;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.terracotta.context.annotations.ContextAttribute;
import org.terracotta.statistics.AbstractSourceStatistic;
import org.terracotta.statistics.OperationStatistic;
import org.terracotta.statistics.Time;
import org.terracotta.statistics.ValueStatistic;
import org.terracotta.statistics.observer.ChainedOperationObserver;

@ContextAttribute(value="this")
public abstract class AbstractOperationStatistic<T extends Enum<T>>
extends AbstractSourceStatistic<ChainedOperationObserver<? super T>>
implements OperationStatistic<T> {
    @ContextAttribute(value="name")
    public final String name;
    @ContextAttribute(value="tags")
    public final Set<String> tags;
    @ContextAttribute(value="properties")
    public final Map<String, Object> properties;
    @ContextAttribute(value="type")
    public final Class<T> type;

    AbstractOperationStatistic(String name, Set<String> tags, Map<String, ? extends Object> properties, Class<T> type) {
        this.name = name;
        this.tags = Collections.unmodifiableSet(new HashSet<String>(tags));
        this.properties = Collections.unmodifiableMap(new HashMap<String, Object>(properties));
        this.type = type;
    }

    @Override
    public Class<T> type() {
        return this.type;
    }

    @Override
    public ValueStatistic<Long> statistic(final T result) {
        return new ValueStatistic<Long>(){

            @Override
            public Long value() {
                return AbstractOperationStatistic.this.count(result);
            }
        };
    }

    @Override
    public ValueStatistic<Long> statistic(final Set<T> results) {
        return new ValueStatistic<Long>(){

            @Override
            public Long value() {
                return AbstractOperationStatistic.this.sum(results);
            }
        };
    }

    @Override
    public long sum() {
        return this.sum(EnumSet.allOf(this.type));
    }

    @Override
    public void begin() {
        if (!this.derivedStatistics.isEmpty()) {
            long time = Time.time();
            for (ChainedOperationObserver observer : this.derivedStatistics) {
                observer.begin(time);
            }
        }
    }
}


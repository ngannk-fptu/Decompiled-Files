/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.statistics;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.terracotta.statistics.StatisticsManager;
import org.terracotta.statistics.observer.OperationObserver;

public final class StatisticsBuilder {
    private StatisticsBuilder() {
    }

    public static <T extends Enum<T>> OperationStatisticBuilder<T> operation(Class<T> type) {
        return new OperationStatisticBuilder<T>(type);
    }

    static class AbstractStatisticBuilder<T extends AbstractStatisticBuilder> {
        protected final Set<String> tags = new HashSet<String>();
        protected Object context;
        protected String name;

        AbstractStatisticBuilder() {
        }

        public T of(Object of) {
            if (this.context == null) {
                this.context = of;
                return (T)this;
            }
            throw new IllegalStateException("Context already defined");
        }

        public T named(String name) {
            if (this.name == null) {
                this.name = name;
                return (T)this;
            }
            throw new IllegalStateException("Name already defined");
        }

        public T tag(String ... tags) {
            Collections.addAll(this.tags, tags);
            return (T)this;
        }
    }

    public static class OperationStatisticBuilder<T extends Enum<T>>
    extends AbstractStatisticBuilder<OperationStatisticBuilder<T>> {
        private final Class<T> type;

        public OperationStatisticBuilder(Class<T> type) {
            this.type = type;
        }

        public OperationObserver<T> build() {
            if (this.context == null || this.name == null) {
                throw new IllegalStateException();
            }
            return StatisticsManager.createOperationStatistic(this.context, this.name, this.tags, this.type);
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.terracotta.statistics;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArrayList;
import org.terracotta.context.WeakIdentityHashMap;
import org.terracotta.context.annotations.ContextAttribute;
import org.terracotta.statistics.ValueStatistic;

@ContextAttribute(value="this")
class PassThroughStatistic<T extends Number>
implements ValueStatistic<T> {
    private static final WeakIdentityHashMap<Object, Collection<PassThroughStatistic<?>>> BINDING = new WeakIdentityHashMap();
    @ContextAttribute(value="name")
    public final String name;
    @ContextAttribute(value="tags")
    public final Set<String> tags;
    @ContextAttribute(value="properties")
    public final Map<String, Object> properties;
    private final Callable<T> source;

    private static void bindStatistic(PassThroughStatistic<?> stat, Object to) {
        Collection<PassThroughStatistic<?>> racer;
        Collection<PassThroughStatistic<?>> collection = BINDING.get(to);
        if (collection == null && (racer = BINDING.putIfAbsent(to, collection = new CopyOnWriteArrayList())) != null) {
            collection = racer;
        }
        collection.add(stat);
    }

    public PassThroughStatistic(Object context, String name, Set<String> tags, Map<String, ? extends Object> properties, Callable<T> source) {
        this.name = name;
        this.tags = Collections.unmodifiableSet(new HashSet<String>(tags));
        this.properties = Collections.unmodifiableMap(new HashMap<String, Object>(properties));
        this.source = source;
        PassThroughStatistic.bindStatistic(this, context);
    }

    @Override
    public T value() {
        try {
            return (T)((Number)this.source.call());
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}


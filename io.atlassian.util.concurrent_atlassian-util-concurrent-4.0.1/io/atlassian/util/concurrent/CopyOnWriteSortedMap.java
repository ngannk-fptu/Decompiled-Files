/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.jcip.annotations.GuardedBy
 *  net.jcip.annotations.ThreadSafe
 */
package io.atlassian.util.concurrent;

import io.atlassian.util.concurrent.AbstractCopyOnWriteMap;
import io.atlassian.util.concurrent.NotNull;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;
import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

@ThreadSafe
public abstract class CopyOnWriteSortedMap<K, V>
extends AbstractCopyOnWriteMap<K, V, SortedMap<K, V>>
implements SortedMap<K, V> {
    private static final long serialVersionUID = 7375772978175545647L;

    public static <K, V> Builder<K, V> builder() {
        return new Builder();
    }

    public static <K, V> CopyOnWriteSortedMap<K, V> newTreeMap() {
        Builder<K, V> builder = CopyOnWriteSortedMap.builder();
        return builder.newTreeMap();
    }

    public static <K, V> CopyOnWriteSortedMap<K, V> newTreeMap(@NotNull Map<? extends K, ? extends V> map) {
        Builder<? extends K, ? extends V> builder = CopyOnWriteSortedMap.builder();
        Objects.requireNonNull(map, "map");
        return builder.addAll(map).newTreeMap();
    }

    public static <K, V> CopyOnWriteSortedMap<K, V> newTreeMap(@NotNull Comparator<? super K> comparator) {
        Builder<? super K, V> builder = CopyOnWriteSortedMap.builder();
        Objects.requireNonNull(comparator, "comparator");
        return builder.ordering(comparator).newTreeMap();
    }

    public static <K, V> CopyOnWriteSortedMap<K, V> newTreeMap(@NotNull Map<? extends K, ? extends V> map, @NotNull Comparator<? super K> comparator) {
        Builder<? super K, V> builder = CopyOnWriteSortedMap.builder();
        Objects.requireNonNull(comparator, "comparator");
        Objects.requireNonNull(map, "map");
        return builder.ordering(comparator).addAll(map).newTreeMap();
    }

    protected CopyOnWriteSortedMap(AbstractCopyOnWriteMap.View.Type viewType) {
        super(Collections.emptyMap(), viewType);
    }

    protected CopyOnWriteSortedMap(Map<? extends K, ? extends V> map, AbstractCopyOnWriteMap.View.Type viewType) {
        super(map, viewType);
    }

    @Override
    @GuardedBy(value="internal-lock")
    protected abstract <N extends Map<? extends K, ? extends V>> SortedMap<K, V> copy(N var1);

    @Override
    public Comparator<? super K> comparator() {
        return ((SortedMap)this.getDelegate()).comparator();
    }

    @Override
    public K firstKey() {
        return ((SortedMap)this.getDelegate()).firstKey();
    }

    @Override
    public K lastKey() {
        return ((SortedMap)this.getDelegate()).lastKey();
    }

    @Override
    public SortedMap<K, V> headMap(K toKey) {
        return Collections.unmodifiableSortedMap(((SortedMap)this.getDelegate()).headMap(toKey));
    }

    @Override
    public SortedMap<K, V> tailMap(K fromKey) {
        return Collections.unmodifiableSortedMap(((SortedMap)this.getDelegate()).tailMap(fromKey));
    }

    @Override
    public SortedMap<K, V> subMap(K fromKey, K toKey) {
        return Collections.unmodifiableSortedMap(((SortedMap)this.getDelegate()).subMap(fromKey, toKey));
    }

    private static <K, V> CopyOnWriteSortedMap<K, V> comparedTreeMap(Map<? extends K, ? extends V> map, AbstractCopyOnWriteMap.View.Type viewType, final Comparator<? super K> comparator) {
        Objects.requireNonNull(comparator, "comparator");
        return new CopyOnWriteSortedMap<K, V>(map, viewType){
            private static final long serialVersionUID = -7243810284130497340L;

            @Override
            public <N extends Map<? extends K, ? extends V>> SortedMap<K, V> copy(N map) {
                TreeMap treeMap = new TreeMap(comparator);
                treeMap.putAll(map);
                return treeMap;
            }
        };
    }

    private static final class Tree<K, V>
    extends CopyOnWriteSortedMap<K, V> {
        private static final long serialVersionUID = 8015823768891873357L;

        Tree(Map<? extends K, ? extends V> map, AbstractCopyOnWriteMap.View.Type viewType) {
            super(map, viewType);
        }

        @Override
        public final <N extends Map<? extends K, ? extends V>> SortedMap<K, V> copy(N map) {
            return new TreeMap(map);
        }
    }

    public static class Builder<K, V> {
        private AbstractCopyOnWriteMap.View.Type viewType = AbstractCopyOnWriteMap.View.Type.STABLE;
        private Comparator<? super K> comparator;
        private final Map<K, V> initialValues = new HashMap();

        Builder() {
        }

        public Builder<K, V> stableViews() {
            this.viewType = AbstractCopyOnWriteMap.View.Type.STABLE;
            return this;
        }

        public Builder<K, V> liveViews() {
            this.viewType = AbstractCopyOnWriteMap.View.Type.STABLE;
            return this;
        }

        public Builder<K, V> addAll(Map<? extends K, ? extends V> values) {
            this.initialValues.putAll(values);
            return this;
        }

        public Builder<K, V> ordering(Comparator<? super K> comparator) {
            this.comparator = comparator;
            return this;
        }

        public Builder<K, V> orderingNatural() {
            this.comparator = null;
            return this;
        }

        public CopyOnWriteSortedMap<K, V> newTreeMap() {
            return this.comparator == null ? new Tree<K, V>(this.initialValues, this.viewType) : CopyOnWriteSortedMap.comparedTreeMap(this.initialValues, this.viewType, this.comparator);
        }
    }
}


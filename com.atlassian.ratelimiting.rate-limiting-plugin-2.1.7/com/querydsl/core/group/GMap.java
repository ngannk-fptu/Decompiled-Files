/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.core.group;

import com.mysema.commons.lang.Pair;
import com.querydsl.core.group.AbstractGroupExpression;
import com.querydsl.core.group.GroupCollector;
import com.querydsl.core.group.GroupExpression;
import com.querydsl.core.group.QPair;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

abstract class GMap<K, V, M extends Map<K, V>>
extends AbstractGroupExpression<Pair<K, V>, M> {
    private static final long serialVersionUID = 7106389414200843920L;

    public GMap(QPair<K, V> qpair) {
        super(Map.class, qpair);
    }

    protected abstract M createMap();

    public static <T, U> GMap<T, U, Map<T, U>> createLinked(QPair<T, U> expr) {
        return new GMap<T, U, Map<T, U>>((QPair)expr){

            @Override
            protected Map<T, U> createMap() {
                return new LinkedHashMap();
            }
        };
    }

    public static <T extends Comparable<? super T>, U> GMap<T, U, SortedMap<T, U>> createSorted(QPair<T, U> expr) {
        return new GMap<T, U, SortedMap<T, U>>((QPair)expr){

            @Override
            protected SortedMap<T, U> createMap() {
                return new TreeMap();
            }
        };
    }

    public static <T, U> GMap<T, U, SortedMap<T, U>> createSorted(QPair<T, U> expr, final Comparator<? super T> comparator) {
        return new GMap<T, U, SortedMap<T, U>>(expr){

            @Override
            protected SortedMap<T, U> createMap() {
                return new TreeMap(comparator);
            }
        };
    }

    @Override
    public GroupCollector<Pair<K, V>, M> createGroupCollector() {
        return new GroupCollector<Pair<K, V>, M>(){
            private final M map;
            {
                this.map = GMap.this.createMap();
            }

            @Override
            public void add(Pair<K, V> pair) {
                this.map.put(pair.getFirst(), pair.getSecond());
            }

            @Override
            public M get() {
                return this.map;
            }
        };
    }

    static class Mixin<K, V, T, U, R extends Map<? super T, ? super U>>
    extends AbstractGroupExpression<Pair<K, V>, R> {
        private static final long serialVersionUID = 1939989270493531116L;
        private final GroupExpression<Pair<T, U>, R> mixin;
        private final GroupExpression<K, T> keyExpression;
        private final GroupExpression<V, U> valueExpression;

        public Mixin(GroupExpression<K, T> keyExpression, GroupExpression<V, U> valueExpression, AbstractGroupExpression<Pair<T, U>, R> mixin) {
            super(mixin.getType(), QPair.create(keyExpression.getExpression(), valueExpression.getExpression()));
            this.keyExpression = keyExpression;
            this.valueExpression = valueExpression;
            this.mixin = mixin;
        }

        @Override
        public GroupCollector<Pair<K, V>, R> createGroupCollector() {
            return new GroupCollectorImpl();
        }

        private class GroupCollectorImpl
        implements GroupCollector<Pair<K, V>, R> {
            private final GroupCollector<Pair<T, U>, R> groupCollector;
            private final Map<K, GroupCollector<K, T>> keyCollectors = new LinkedHashMap();
            private final Map<GroupCollector<K, T>, GroupCollector<V, U>> valueCollectors = new HashMap();

            public GroupCollectorImpl() {
                this.groupCollector = Mixin.this.mixin.createGroupCollector();
            }

            @Override
            public void add(Pair<K, V> pair) {
                Object first = pair.getFirst();
                GroupCollector<Object, Object> keyCollector = this.keyCollectors.get(first);
                if (keyCollector == null) {
                    keyCollector = Mixin.this.keyExpression.createGroupCollector();
                    this.keyCollectors.put(first, keyCollector);
                }
                keyCollector.add(first);
                GroupCollector<Object, Object> valueCollector = this.valueCollectors.get(keyCollector);
                if (valueCollector == null) {
                    valueCollector = Mixin.this.valueExpression.createGroupCollector();
                    this.valueCollectors.put(keyCollector, valueCollector);
                }
                Object second = pair.getSecond();
                valueCollector.add(second);
            }

            @Override
            public R get() {
                for (GroupCollector keyCollector : this.keyCollectors.values()) {
                    Object key = keyCollector.get();
                    GroupCollector valueCollector = this.valueCollectors.remove(keyCollector);
                    Object value = valueCollector.get();
                    this.groupCollector.add(Pair.of(key, value));
                }
                this.keyCollectors.clear();
                return (Map)this.groupCollector.get();
            }
        }
    }
}


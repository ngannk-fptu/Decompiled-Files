/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl;

import com.hazelcast.core.TypeConverter;
import com.hazelcast.query.impl.CompositeConverter;
import com.hazelcast.query.impl.Indexes;
import com.hazelcast.query.impl.InternalIndex;
import com.hazelcast.query.impl.QueryContext;
import com.hazelcast.query.impl.TypeConverters;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class ConverterCache {
    private static final int FULLY_UNRESOLVED = -1;
    private final Indexes indexes;
    private final Map<String, TypeConverter> cache = new ConcurrentHashMap<String, TypeConverter>();

    public ConverterCache(Indexes indexes) {
        this.indexes = indexes;
    }

    public TypeConverter get(String attribute) {
        TypeConverter cached = this.cache.get(attribute);
        if (cached == null || cached instanceof UnresolvedConverter) {
            cached = this.tryResolve(attribute, (UnresolvedConverter)cached);
        }
        return cached;
    }

    public void invalidate(InternalIndex index) {
        String[] components = index.getComponents();
        if (components.length == 1) {
            this.cache.remove(components[0]);
            return;
        }
        for (String component : components) {
            TypeConverter converter = this.cache.get(component);
            if (!(converter instanceof UnresolvedConverter)) continue;
            this.cache.remove(component);
        }
    }

    public void clear() {
        this.cache.clear();
    }

    private TypeConverter tryResolve(String attribute, UnresolvedConverter unresolved) {
        InternalIndex[] indexesSnapshot = this.indexes.getIndexes();
        if (indexesSnapshot.length == 0) {
            return null;
        }
        if (unresolved != null) {
            TypeConverter converter = unresolved.tryResolve();
            if (converter == null) {
                return null;
            }
            this.cache.put(attribute, converter);
            return converter;
        }
        InternalIndex nonCompositeIndex = this.indexes.matchIndex(attribute, QueryContext.IndexMatchHint.NONE, -1);
        if (nonCompositeIndex != null) {
            TypeConverter converter = nonCompositeIndex.getConverter();
            if (ConverterCache.isNull(converter)) {
                this.cache.put(attribute, new UnresolvedConverter(nonCompositeIndex, -1));
                return null;
            }
            this.cache.put(attribute, converter);
            return converter;
        }
        for (InternalIndex index : indexesSnapshot) {
            String[] components = index.getComponents();
            if (components.length == 1) continue;
            for (int i = 0; i < components.length; ++i) {
                String component = components[i];
                if (!component.equals(attribute)) continue;
                CompositeConverter compositeConverter = (CompositeConverter)index.getConverter();
                if (compositeConverter == null) {
                    this.cache.put(attribute, new UnresolvedConverter(index, i));
                    return null;
                }
                TypeConverter converter = compositeConverter.getComponentConverter(i);
                if (converter == TypeConverters.NULL_CONVERTER) {
                    this.cache.put(attribute, new UnresolvedConverter(index, i));
                    return null;
                }
                this.cache.put(attribute, converter);
                return converter;
            }
        }
        this.cache.put(attribute, new UnresolvedConverter(null, -1));
        return null;
    }

    private static boolean isNull(TypeConverter converter) {
        return converter == null || converter == TypeConverters.NULL_CONVERTER;
    }

    private static final class UnresolvedConverter
    implements TypeConverter {
        final InternalIndex index;
        final int component;

        public UnresolvedConverter(InternalIndex index, int component) {
            this.index = index;
            this.component = component;
        }

        public TypeConverter tryResolve() {
            if (this.index == null) {
                assert (this.component == -1);
                return null;
            }
            if (this.component == -1) {
                assert (this.index.getComponents().length == 1);
                TypeConverter converter = this.index.getConverter();
                return ConverterCache.isNull(converter) ? null : converter;
            }
            CompositeConverter compositeConverter = (CompositeConverter)this.index.getConverter();
            if (compositeConverter == null) {
                return null;
            }
            TypeConverter converter = compositeConverter.getComponentConverter(this.component);
            return converter == TypeConverters.NULL_CONVERTER ? null : converter;
        }

        @Override
        public Comparable convert(Comparable value) {
            throw new UnsupportedOperationException("should never be called");
        }
    }
}


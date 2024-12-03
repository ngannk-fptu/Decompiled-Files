/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.query.impl;

import com.hazelcast.core.TypeConverter;
import com.hazelcast.query.impl.AbstractIndex;
import com.hazelcast.query.impl.CompositeValue;
import com.hazelcast.query.impl.TypeConverters;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

public class CompositeConverter
implements TypeConverter {
    private final TypeConverter[] converters;
    private final boolean isTransient;

    @SuppressFBWarnings(value={"EI_EXPOSE_REP2"})
    public CompositeConverter(TypeConverter[] converters) {
        this.converters = converters;
        boolean isTransient = false;
        for (TypeConverter converter : converters) {
            assert (converter != null);
            if (converter != TypeConverters.NULL_CONVERTER) continue;
            isTransient = true;
            break;
        }
        this.isTransient = isTransient;
    }

    public boolean isTransient() {
        return this.isTransient;
    }

    public TypeConverter getComponentConverter(int component) {
        return this.converters[component];
    }

    @Override
    public Comparable convert(Comparable value) {
        if (!(value instanceof CompositeValue)) {
            throw new IllegalArgumentException("Cannot convert [" + value + "] to composite");
        }
        CompositeValue compositeValue = (CompositeValue)value;
        Comparable[] components = compositeValue.getComponents();
        Comparable[] converted = new Comparable[components.length];
        for (int i = 0; i < components.length; ++i) {
            Comparable component = components[i];
            converted[i] = component == AbstractIndex.NULL || component == CompositeValue.NEGATIVE_INFINITY || component == CompositeValue.POSITIVE_INFINITY ? component : this.converters[i].convert(component);
        }
        return new CompositeValue(converted);
    }
}


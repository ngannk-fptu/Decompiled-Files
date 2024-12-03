/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.beans;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.springframework.beans.PropertyValue;
import org.springframework.lang.Nullable;

public interface PropertyValues
extends Iterable<PropertyValue> {
    @Override
    default public Iterator<PropertyValue> iterator() {
        return Arrays.asList(this.getPropertyValues()).iterator();
    }

    @Override
    default public Spliterator<PropertyValue> spliterator() {
        return Spliterators.spliterator(this.getPropertyValues(), 0);
    }

    default public Stream<PropertyValue> stream() {
        return StreamSupport.stream(this.spliterator(), false);
    }

    public PropertyValue[] getPropertyValues();

    @Nullable
    public PropertyValue getPropertyValue(String var1);

    public PropertyValues changesSince(PropertyValues var1);

    public boolean contains(String var1);

    public boolean isEmpty();
}


/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.commons.iterator;

import java.util.Iterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;

@Deprecated
public class PropertyIterable
implements Iterable<Property> {
    private final PropertyIterator iterator;

    public PropertyIterable(PropertyIterator iterator) {
        this.iterator = iterator;
    }

    @Override
    public Iterator<Property> iterator() {
        return this.iterator;
    }
}


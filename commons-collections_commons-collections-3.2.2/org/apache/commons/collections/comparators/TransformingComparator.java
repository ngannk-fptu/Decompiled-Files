/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.collections.comparators;

import java.util.Comparator;
import org.apache.commons.collections.Transformer;
import org.apache.commons.collections.comparators.ComparableComparator;

public class TransformingComparator
implements Comparator {
    protected Comparator decorated;
    protected Transformer transformer;

    public TransformingComparator(Transformer transformer) {
        this(transformer, new ComparableComparator());
    }

    public TransformingComparator(Transformer transformer, Comparator decorated) {
        this.decorated = decorated;
        this.transformer = transformer;
    }

    public int compare(Object obj1, Object obj2) {
        Object value1 = this.transformer.transform(obj1);
        Object value2 = this.transformer.transform(obj2);
        return this.decorated.compare(value1, value2);
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package javax.jcr;

import javax.jcr.Property;
import javax.jcr.RangeIterator;

public interface PropertyIterator
extends RangeIterator {
    public Property nextProperty();
}


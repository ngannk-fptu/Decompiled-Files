/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.util;

import com.atlassian.user.util.Assert;
import java.util.Enumeration;
import java.util.Iterator;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class EnumerationAdaptor<T>
implements Iterator<T> {
    private final Enumeration<T> enumeration;

    public EnumerationAdaptor(Enumeration<T> enumeration) {
        Assert.notNull(enumeration, "enumeration");
        this.enumeration = enumeration;
    }

    @Override
    public void remove() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasNext() {
        return this.enumeration.hasMoreElements();
    }

    @Override
    public T next() {
        return this.enumeration.nextElement();
    }
}


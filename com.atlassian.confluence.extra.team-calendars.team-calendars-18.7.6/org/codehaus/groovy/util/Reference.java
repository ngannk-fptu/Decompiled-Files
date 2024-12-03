/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.util;

import org.codehaus.groovy.util.Finalizable;

public interface Reference<T, V extends Finalizable> {
    public T get();

    public void clear();

    public V getHandler();
}


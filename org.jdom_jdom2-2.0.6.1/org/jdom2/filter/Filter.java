/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.filter;

import java.io.Serializable;
import java.util.List;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface Filter<T>
extends Serializable {
    public List<T> filter(List<?> var1);

    public T filter(Object var1);

    public boolean matches(Object var1);

    public Filter<? extends Object> negate();

    public Filter<? extends Object> or(Filter<?> var1);

    public Filter<T> and(Filter<?> var1);

    public <R> Filter<R> refine(Filter<R> var1);
}


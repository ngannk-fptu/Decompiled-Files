/*
 * Decompiled with CFR 0.152.
 */
package groovy.lang;

import groovy.lang.Closure;
import java.util.List;

public interface Range<T extends Comparable>
extends List<T> {
    public T getFrom();

    public T getTo();

    public boolean isReverse();

    public boolean containsWithinBounds(Object var1);

    public void step(int var1, Closure var2);

    public List<T> step(int var1);

    public String inspect();
}


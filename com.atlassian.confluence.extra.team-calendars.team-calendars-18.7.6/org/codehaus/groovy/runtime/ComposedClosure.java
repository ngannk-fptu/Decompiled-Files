/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import groovy.lang.Closure;
import java.util.List;

public final class ComposedClosure<V>
extends Closure<V> {
    private Closure first = (Closure)this.getOwner();
    private Closure<V> second;

    public ComposedClosure(Closure first, Closure<V> second) {
        super(first.clone());
        this.second = (Closure)second.clone();
        this.maximumNumberOfParameters = first.getMaximumNumberOfParameters();
    }

    @Override
    public void setDelegate(Object delegate) {
        ((Closure)this.getOwner()).setDelegate(delegate);
        this.second.setDelegate(delegate);
    }

    @Override
    public Object getDelegate() {
        return ((Closure)this.getOwner()).getDelegate();
    }

    @Override
    public void setResolveStrategy(int resolveStrategy) {
        ((Closure)this.getOwner()).setResolveStrategy(resolveStrategy);
        this.second.setResolveStrategy(resolveStrategy);
    }

    @Override
    public int getResolveStrategy() {
        return ((Closure)this.getOwner()).getResolveStrategy();
    }

    @Override
    public Object clone() {
        return new ComposedClosure<V>(this.first, this.second);
    }

    @Override
    public Class[] getParameterTypes() {
        return this.first.getParameterTypes();
    }

    public Object doCall(Object ... args) {
        return this.call(args);
    }

    @Override
    public V call(Object ... args) {
        Object temp = this.first.call(args);
        if (temp instanceof List && this.second.getParameterTypes().length > 1) {
            temp = ((List)temp).toArray();
        }
        return temp instanceof Object[] ? this.second.call((Object[])temp) : this.second.call(temp);
    }
}


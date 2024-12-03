/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.runtime;

import groovy.lang.Closure;
import groovy.lang.MetaClass;
import java.util.ArrayList;
import java.util.List;
import org.codehaus.groovy.runtime.InvokerHelper;

public class IteratorClosureAdapter<T>
extends Closure {
    private final List<T> list = new ArrayList<T>();
    private MetaClass metaClass = InvokerHelper.getMetaClass(this.getClass());

    public IteratorClosureAdapter(Object delegate) {
        super(delegate);
    }

    @Override
    public MetaClass getMetaClass() {
        return this.metaClass;
    }

    @Override
    public void setMetaClass(MetaClass metaClass) {
        this.metaClass = metaClass;
    }

    public List<T> asList() {
        return this.list;
    }

    protected Object doCall(T argument) {
        this.list.add(argument);
        return null;
    }
}


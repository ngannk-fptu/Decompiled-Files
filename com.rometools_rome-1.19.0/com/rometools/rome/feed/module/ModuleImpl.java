/*
 * Decompiled with CFR 0.152.
 */
package com.rometools.rome.feed.module;

import com.rometools.rome.feed.impl.CloneableBean;
import com.rometools.rome.feed.impl.EqualsBean;
import com.rometools.rome.feed.impl.ToStringBean;
import com.rometools.rome.feed.module.Module;
import java.io.Serializable;
import java.util.Collections;

public abstract class ModuleImpl
implements Cloneable,
Serializable,
Module {
    private static final long serialVersionUID = 1L;
    private final Class<?> beanClass;
    private final String uri;

    protected ModuleImpl(Class<?> beanClass, String uri) {
        this.beanClass = beanClass;
        this.uri = uri;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return CloneableBean.beanClone(this, Collections.<String>emptySet());
    }

    public boolean equals(Object other) {
        if (!(other instanceof ModuleImpl)) {
            return false;
        }
        return EqualsBean.beanEquals(this.beanClass, this, other);
    }

    public int hashCode() {
        return EqualsBean.beanHashCode(this);
    }

    public String toString() {
        return ToStringBean.toString(this.beanClass, this);
    }

    @Override
    public String getUri() {
        return this.uri;
    }
}


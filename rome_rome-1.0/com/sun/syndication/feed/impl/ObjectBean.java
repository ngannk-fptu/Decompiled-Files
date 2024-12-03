/*
 * Decompiled with CFR 0.152.
 */
package com.sun.syndication.feed.impl;

import com.sun.syndication.feed.impl.CloneableBean;
import com.sun.syndication.feed.impl.EqualsBean;
import com.sun.syndication.feed.impl.ToStringBean;
import java.io.Serializable;
import java.util.Set;

public class ObjectBean
implements Serializable,
Cloneable {
    private EqualsBean _equalsBean;
    private ToStringBean _toStringBean;
    private CloneableBean _cloneableBean;

    public ObjectBean(Class beanClass, Object obj) {
        this(beanClass, obj, null);
    }

    public ObjectBean(Class beanClass, Object obj, Set ignoreProperties) {
        this._equalsBean = new EqualsBean(beanClass, obj);
        this._toStringBean = new ToStringBean(beanClass, obj);
        this._cloneableBean = new CloneableBean(obj, ignoreProperties);
    }

    public Object clone() throws CloneNotSupportedException {
        return this._cloneableBean.beanClone();
    }

    public boolean equals(Object other) {
        return this._equalsBean.beanEquals(other);
    }

    public int hashCode() {
        return this._equalsBean.beanHashCode();
    }

    public String toString() {
        return this._toStringBean.toString();
    }
}


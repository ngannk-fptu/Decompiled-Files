/*
 * Decompiled with CFR 0.152.
 */
package com.rometools.rome.feed.rss;

import com.rometools.rome.feed.impl.CloneableBean;
import com.rometools.rome.feed.impl.EqualsBean;
import com.rometools.rome.feed.impl.ToStringBean;
import java.io.Serializable;
import java.util.Collections;

public class Source
implements Cloneable,
Serializable {
    private static final long serialVersionUID = 1L;
    private String url;
    private String value;

    public Object clone() throws CloneNotSupportedException {
        return CloneableBean.beanClone(this, Collections.<String>emptySet());
    }

    public boolean equals(Object other) {
        if (!(other instanceof Source)) {
            return false;
        }
        return EqualsBean.beanEquals(this.getClass(), this, other);
    }

    public int hashCode() {
        return EqualsBean.beanHashCode(this);
    }

    public String toString() {
        return ToStringBean.toString(this.getClass(), this);
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}


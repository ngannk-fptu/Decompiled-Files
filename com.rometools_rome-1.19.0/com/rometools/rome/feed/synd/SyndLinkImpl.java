/*
 * Decompiled with CFR 0.152.
 */
package com.rometools.rome.feed.synd;

import com.rometools.rome.feed.impl.CloneableBean;
import com.rometools.rome.feed.impl.EqualsBean;
import com.rometools.rome.feed.impl.ToStringBean;
import com.rometools.rome.feed.synd.SyndLink;
import java.io.Serializable;
import java.util.Collections;

public class SyndLinkImpl
implements Cloneable,
Serializable,
SyndLink {
    private static final long serialVersionUID = 1L;
    private String href;
    private String rel;
    private String type;
    private String hreflang;
    private String title;
    private long length;

    @Override
    public Object clone() throws CloneNotSupportedException {
        return CloneableBean.beanClone(this, Collections.<String>emptySet());
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof SyndLinkImpl)) {
            return false;
        }
        return EqualsBean.beanEquals(this.getClass(), this, other);
    }

    @Override
    public int hashCode() {
        return EqualsBean.beanHashCode(this);
    }

    @Override
    public String toString() {
        return ToStringBean.toString(this.getClass(), this);
    }

    @Override
    public String getRel() {
        return this.rel;
    }

    @Override
    public void setRel(String rel) {
        this.rel = rel;
    }

    @Override
    public String getType() {
        return this.type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getHref() {
        return this.href;
    }

    @Override
    public void setHref(String href) {
        this.href = href;
    }

    @Override
    public String getTitle() {
        return this.title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String getHreflang() {
        return this.hreflang;
    }

    @Override
    public void setHreflang(String hreflang) {
        this.hreflang = hreflang;
    }

    @Override
    public long getLength() {
        return this.length;
    }

    @Override
    public void setLength(long length) {
        this.length = length;
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.rometools.utils.Alternatives
 */
package com.rometools.rome.feed.atom;

import com.rometools.rome.feed.impl.CloneableBean;
import com.rometools.rome.feed.impl.EqualsBean;
import com.rometools.rome.feed.impl.ToStringBean;
import com.rometools.utils.Alternatives;
import java.io.Serializable;
import java.util.Collections;

public class Link
implements Cloneable,
Serializable {
    private static final long serialVersionUID = 1L;
    private String href;
    private String hrefResolved;
    private String rel = "alternate";
    private String type;
    private String hreflang;
    private String title;
    private long length;

    public Object clone() throws CloneNotSupportedException {
        return CloneableBean.beanClone(this, Collections.<String>emptySet());
    }

    public boolean equals(Object other) {
        return EqualsBean.beanEquals(this.getClass(), this, other);
    }

    public int hashCode() {
        return EqualsBean.beanHashCode(this);
    }

    public String toString() {
        return ToStringBean.toString(this.getClass(), this);
    }

    public String getRel() {
        return this.rel;
    }

    public void setRel(String rel) {
        this.rel = rel;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getHref() {
        return this.href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public void setHrefResolved(String hrefResolved) {
        this.hrefResolved = hrefResolved;
    }

    public String getHrefResolved() {
        return (String)Alternatives.firstNotNull((Object[])new String[]{this.hrefResolved, this.href});
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHreflang() {
        return this.hreflang;
    }

    public void setHreflang(String hreflang) {
        this.hreflang = hreflang;
    }

    public long getLength() {
        return this.length;
    }

    public void setLength(long length) {
        this.length = length;
    }
}


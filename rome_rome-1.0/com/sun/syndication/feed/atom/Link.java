/*
 * Decompiled with CFR 0.152.
 */
package com.sun.syndication.feed.atom;

import com.sun.syndication.feed.impl.ObjectBean;
import java.io.Serializable;

public class Link
implements Cloneable,
Serializable {
    private ObjectBean _objBean = new ObjectBean(this.getClass(), this);
    private String _href;
    private String _hrefResolved;
    private String _rel = "alternate";
    private String _type;
    private String _hreflang;
    private String _title;
    private long _length;

    public Object clone() throws CloneNotSupportedException {
        return this._objBean.clone();
    }

    public boolean equals(Object other) {
        return this._objBean.equals(other);
    }

    public int hashCode() {
        return this._objBean.hashCode();
    }

    public String toString() {
        return this._objBean.toString();
    }

    public String getRel() {
        return this._rel;
    }

    public void setRel(String rel) {
        this._rel = rel;
    }

    public String getType() {
        return this._type;
    }

    public void setType(String type) {
        this._type = type;
    }

    public String getHref() {
        return this._href;
    }

    public void setHref(String href) {
        this._href = href;
    }

    public void setHrefResolved(String hrefResolved) {
        this._hrefResolved = hrefResolved;
    }

    public String getHrefResolved() {
        return this._hrefResolved != null ? this._hrefResolved : this._href;
    }

    public String getTitle() {
        return this._title;
    }

    public void setTitle(String title) {
        this._title = title;
    }

    public String getHreflang() {
        return this._hreflang;
    }

    public void setHreflang(String hreflang) {
        this._hreflang = hreflang;
    }

    public long getLength() {
        return this._length;
    }

    public void setLength(long length) {
        this._length = length;
    }
}


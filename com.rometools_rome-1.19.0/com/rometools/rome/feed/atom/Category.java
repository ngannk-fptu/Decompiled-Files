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

public class Category
implements Cloneable,
Serializable {
    private static final long serialVersionUID = 1L;
    private String term;
    private String scheme;
    private String schemeResolved;
    private String label;

    public Object clone() throws CloneNotSupportedException {
        return CloneableBean.beanClone(this, Collections.<String>emptySet());
    }

    public boolean equals(Object other) {
        if (!(other instanceof Category)) {
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

    public String getLabel() {
        return this.label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getScheme() {
        return this.scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public void setSchemeResolved(String schemeResolved) {
        this.schemeResolved = schemeResolved;
    }

    public String getSchemeResolved() {
        return (String)Alternatives.firstNotNull((Object[])new String[]{this.schemeResolved, this.scheme});
    }

    public String getTerm() {
        return this.term;
    }

    public void setTerm(String term) {
        this.term = term;
    }
}


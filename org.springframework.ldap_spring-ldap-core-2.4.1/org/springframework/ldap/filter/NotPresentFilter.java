/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.filter;

import org.springframework.ldap.filter.AbstractFilter;

public class NotPresentFilter
extends AbstractFilter {
    private String attribute;

    public NotPresentFilter(String attribute) {
        this.attribute = attribute;
    }

    @Override
    public StringBuffer encode(StringBuffer buff) {
        buff.append("(!(");
        buff.append(this.attribute);
        buff.append("=*))");
        return buff;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        NotPresentFilter that = (NotPresentFilter)o;
        return !(this.attribute != null ? !this.attribute.equals(that.attribute) : that.attribute != null);
    }

    @Override
    public int hashCode() {
        return this.attribute != null ? this.attribute.hashCode() : 0;
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.filter;

import org.springframework.ldap.filter.AbstractFilter;
import org.springframework.ldap.support.LdapEncoder;

public abstract class CompareFilter
extends AbstractFilter {
    private final String attribute;
    private final String value;
    private final String encodedValue;

    public CompareFilter(String attribute, String value) {
        this.attribute = attribute;
        this.value = value;
        this.encodedValue = this.encodeValue(value);
    }

    String getEncodedValue() {
        return this.encodedValue;
    }

    protected String encodeValue(String value) {
        return LdapEncoder.filterEncode(value);
    }

    public CompareFilter(String attribute, int value) {
        this.attribute = attribute;
        this.value = String.valueOf(value);
        this.encodedValue = LdapEncoder.filterEncode(this.value);
    }

    @Override
    public StringBuffer encode(StringBuffer buff) {
        buff.append('(');
        buff.append(this.attribute).append(this.getCompareString()).append(this.encodedValue);
        buff.append(')');
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
        CompareFilter that = (CompareFilter)o;
        if (this.attribute != null ? !this.attribute.equals(that.attribute) : that.attribute != null) {
            return false;
        }
        return !(this.value != null ? !this.value.equals(that.value) : that.value != null);
    }

    @Override
    public int hashCode() {
        int result = this.attribute != null ? this.attribute.hashCode() : 0;
        result = 31 * result + (this.value != null ? this.value.hashCode() : 0);
        return result;
    }

    protected abstract String getCompareString();
}


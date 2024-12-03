/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.springframework.ldap.filter.AbstractFilter
 */
package com.atlassian.crowd.search.ldap;

import com.google.common.base.Preconditions;
import org.springframework.ldap.filter.AbstractFilter;

public class BitwiseFilter
extends AbstractFilter {
    private static final String LDAP_MATCHING_RULE_BIT_AND = "1.2.840.113556.1.4.803";
    private static final String LDAP_MATCHING_RULE_BIT_OR = "1.2.840.113556.1.4.804";
    private final String attribute;
    private final String ruleOID;
    private final int mask;

    private BitwiseFilter(String attribute, String ruleOID, int mask) {
        this.attribute = (String)Preconditions.checkNotNull((Object)attribute);
        this.ruleOID = ruleOID;
        this.mask = mask;
    }

    public StringBuffer encode(StringBuffer buff) {
        buff.append("(");
        buff.append(this.attribute).append(":").append(this.ruleOID).append(":=").append(this.mask);
        buff.append(")");
        return buff;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || ((Object)((Object)this)).getClass() != o.getClass()) {
            return false;
        }
        BitwiseFilter that = (BitwiseFilter)((Object)o);
        if (this.mask != that.mask) {
            return false;
        }
        if (!this.attribute.equals(that.attribute)) {
            return false;
        }
        return this.ruleOID.equals(that.ruleOID);
    }

    public int hashCode() {
        int result = this.attribute.hashCode();
        result = 31 * result + this.ruleOID.hashCode();
        result = 31 * result + this.mask;
        return result;
    }

    public static BitwiseFilter and(String attribute, int mask) {
        return new BitwiseFilter(attribute, LDAP_MATCHING_RULE_BIT_AND, mask);
    }

    public static BitwiseFilter or(String attribute, int mask) {
        return new BitwiseFilter(attribute, LDAP_MATCHING_RULE_BIT_OR, mask);
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.filter;

import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.support.LdapEncoder;

public class LikeFilter
extends EqualsFilter {
    public LikeFilter(String attribute, String value) {
        super(attribute, value);
    }

    @Override
    protected String encodeValue(String value) {
        if (value == null) {
            return "";
        }
        String[] substrings = value.split("\\*", -2);
        if (substrings.length == 1) {
            return LdapEncoder.filterEncode(substrings[0]);
        }
        StringBuilder buff = new StringBuilder();
        for (int i = 0; i < substrings.length; ++i) {
            buff.append(LdapEncoder.filterEncode(substrings[i]));
            if (i >= substrings.length - 1) continue;
            buff.append("*");
        }
        return buff.toString();
    }
}


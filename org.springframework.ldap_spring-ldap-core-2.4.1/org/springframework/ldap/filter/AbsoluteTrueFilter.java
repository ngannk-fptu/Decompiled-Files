/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.filter;

import org.springframework.ldap.filter.AbstractFilter;

public class AbsoluteTrueFilter
extends AbstractFilter {
    @Override
    public StringBuffer encode(StringBuffer buff) {
        buff.append("(&)");
        return buff;
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.sf.ldaptemplate.support.filter.Filter
 */
package com.atlassian.user.impl.ldap;

import com.atlassian.user.util.LDAPUtils;
import net.sf.ldaptemplate.support.filter.Filter;

public class LiteralFilter
implements Filter {
    private String filter;

    public LiteralFilter(String filter) {
        if (!LDAPUtils.isValidFilter(filter)) {
            throw new IllegalArgumentException("Invalid filter:" + filter);
        }
        this.filter = filter;
    }

    public String encode() {
        return this.filter;
    }

    public StringBuffer encode(StringBuffer stringBuffer) {
        return stringBuffer.append(this.filter);
    }
}


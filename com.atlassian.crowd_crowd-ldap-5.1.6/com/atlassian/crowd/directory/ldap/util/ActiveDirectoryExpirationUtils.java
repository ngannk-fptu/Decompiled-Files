/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.ldap.filter.EqualsFilter
 *  org.springframework.ldap.filter.Filter
 *  org.springframework.ldap.filter.GreaterThanOrEqualsFilter
 *  org.springframework.ldap.filter.NotPresentFilter
 *  org.springframework.ldap.filter.OrFilter
 */
package com.atlassian.crowd.directory.ldap.util;

import java.util.Date;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.Filter;
import org.springframework.ldap.filter.GreaterThanOrEqualsFilter;
import org.springframework.ldap.filter.NotPresentFilter;
import org.springframework.ldap.filter.OrFilter;

public class ActiveDirectoryExpirationUtils {
    public static final String ACCOUNT_EXPIRES_ATTRIBUTE = "accountExpires";
    private static final long DATE_OFFSET = 116444736000000000L;

    public static Filter notExpiredAt(Date date) {
        OrFilter expirationFilter = new OrFilter();
        expirationFilter.or((Filter)new EqualsFilter(ACCOUNT_EXPIRES_ATTRIBUTE, "0"));
        expirationFilter.or((Filter)new NotPresentFilter(ACCOUNT_EXPIRES_ATTRIBUTE));
        expirationFilter.or((Filter)new GreaterThanOrEqualsFilter(ACCOUNT_EXPIRES_ATTRIBUTE, Long.toString(ActiveDirectoryExpirationUtils.encodeDate(date))));
        return expirationFilter;
    }

    public static long encodeDate(Date date) {
        long intervalsSinceJanuaryFirst1970 = date.getTime() * 10000L;
        long intervalsSinceJanuaryFirst1601 = intervalsSinceJanuaryFirst1970 + 116444736000000000L;
        if (intervalsSinceJanuaryFirst1601 < 0L) {
            throw new IllegalArgumentException("The date " + date + " could not be encoded");
        }
        return intervalsSinceJanuaryFirst1601;
    }
}


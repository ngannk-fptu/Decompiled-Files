/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.util.TextUtils
 *  net.sf.ldaptemplate.support.filter.AndFilter
 *  net.sf.ldaptemplate.support.filter.Filter
 *  org.apache.log4j.Category
 */
package com.atlassian.user.util;

import com.opensymphony.util.TextUtils;
import javax.naming.directory.SearchControls;
import net.sf.ldaptemplate.support.filter.AndFilter;
import net.sf.ldaptemplate.support.filter.Filter;
import org.apache.log4j.Category;

public class LDAPUtils {
    public static final Category log = Category.getInstance(LDAPUtils.class);

    public static Filter makeAndFilter(Filter filter1, Filter filter2) {
        if (filter1 == null) {
            return filter2;
        }
        if (filter2 == null) {
            return filter1;
        }
        return new AndFilter().and(filter1).and(filter2);
    }

    public static boolean isValidFilter(String filter) {
        return TextUtils.stringSet((String)filter) && filter.startsWith("(") && filter.endsWith(")") && filter.indexOf("=") != -1;
    }

    public static SearchControls createSearchControls(String[] attributesToReturn, boolean searchAllDepths, int timeLimitMillis) {
        SearchControls ctls = new SearchControls();
        int searchScope = searchAllDepths ? 2 : 1;
        ctls.setSearchScope(searchScope);
        if (attributesToReturn != null) {
            ctls.setReturningAttributes(attributesToReturn);
        }
        ctls.setTimeLimit(timeLimitMillis);
        return ctls;
    }
}


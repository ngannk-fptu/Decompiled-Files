/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.StringUtils
 */
package com.atlassian.user.search.query.match;

import com.atlassian.user.search.query.match.Matcher;
import org.apache.commons.lang.StringUtils;

public class ContainsIgnoreCaseMatcher
implements Matcher {
    public boolean matches(String content, String searchTerm) {
        if (content == null || searchTerm == null) {
            return false;
        }
        return StringUtils.contains((String)content.toLowerCase(), (String)searchTerm.toLowerCase());
    }
}


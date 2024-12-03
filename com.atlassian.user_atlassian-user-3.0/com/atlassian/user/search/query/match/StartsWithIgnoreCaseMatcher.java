/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang.StringUtils
 */
package com.atlassian.user.search.query.match;

import com.atlassian.user.search.query.match.Matcher;
import org.apache.commons.lang.StringUtils;

public class StartsWithIgnoreCaseMatcher
implements Matcher {
    public boolean matches(String content, String searchTerm) {
        if (StringUtils.isBlank((String)content) || searchTerm == null) {
            return false;
        }
        return content.toLowerCase().startsWith(searchTerm.toLowerCase());
    }
}


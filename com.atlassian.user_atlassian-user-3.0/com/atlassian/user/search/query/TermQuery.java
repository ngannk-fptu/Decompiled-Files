/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.search.query;

import com.atlassian.user.Entity;
import com.atlassian.user.search.query.Query;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public interface TermQuery<T extends Entity>
extends Query<T> {
    public static final String SUBSTRING_STARTS_WITH = "starts_with";
    public static final String SUBSTRING_ENDS_WITH = "ends_with";
    public static final String SUBSTRING_CONTAINS = "contains";
    public static final String WILDCARD = "*";

    public String getTerm();

    public String getMatchingRule();

    public boolean isMatchingSubstring();
}


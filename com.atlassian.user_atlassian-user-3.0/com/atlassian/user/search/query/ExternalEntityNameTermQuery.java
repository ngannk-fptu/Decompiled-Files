/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.search.query;

import com.atlassian.user.User;
import com.atlassian.user.search.query.AbstractSingleTermQuery;
import com.atlassian.user.search.query.UserQuery;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ExternalEntityNameTermQuery
extends AbstractSingleTermQuery<User>
implements UserQuery {
    public ExternalEntityNameTermQuery(String term) {
        super(term);
    }

    public ExternalEntityNameTermQuery(String term, String matchingRule) {
        super(term, matchingRule);
    }
}


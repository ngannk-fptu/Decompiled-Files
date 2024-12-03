/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.search.query;

import com.atlassian.user.Group;
import com.atlassian.user.search.query.AbstractSingleTermQuery;
import com.atlassian.user.search.query.GroupQuery;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class GroupNameTermQuery
extends AbstractSingleTermQuery<Group>
implements GroupQuery {
    public GroupNameTermQuery(String term) {
        super(term);
    }

    public GroupNameTermQuery(String term, String matchingRule) {
        super(term, matchingRule);
    }
}


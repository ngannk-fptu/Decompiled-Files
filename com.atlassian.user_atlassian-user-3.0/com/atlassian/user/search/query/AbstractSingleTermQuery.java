/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.user.search.query;

import com.atlassian.user.Entity;
import com.atlassian.user.search.query.Query;
import com.atlassian.user.search.query.TermQuery;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class AbstractSingleTermQuery<T extends Entity>
implements TermQuery<T> {
    protected String matchingRule;
    protected boolean matchingSubstring;
    protected String term;

    public AbstractSingleTermQuery(String term) {
        this.term = term;
    }

    public AbstractSingleTermQuery(String term, String matchingRule) {
        this.term = term;
        if (matchingRule != "contains" && matchingRule != "ends_with" && matchingRule != "starts_with") {
            throw new IllegalArgumentException("Invalid substring matching rule - please use " + Query.class.getName() + "SUBSTRING_CONTAINS, SUBSTRING_ENDS_WITH, or " + "SUBSTRING_STARTS_WITH");
        }
        this.matchingRule = matchingRule;
        this.matchingSubstring = true;
    }

    @Override
    public String getTerm() {
        return this.term;
    }

    @Override
    public String getMatchingRule() {
        return this.matchingRule;
    }

    @Override
    public boolean isMatchingSubstring() {
        return this.matchingSubstring;
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.search.v2.query;

import com.atlassian.confluence.labels.Label;
import com.atlassian.confluence.search.v2.SearchFieldNames;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.query.ConstantScoreQuery;
import com.atlassian.confluence.search.v2.query.LabelQuery;
import com.atlassian.confluence.search.v2.query.TermQuery;
import java.util.List;

public class InheritedLabelQuery
implements SearchQuery {
    private static final String KEY = "inheritedLabel";
    private final LabelQuery labelQuery;

    public InheritedLabelQuery(String label) {
        this(new LabelQuery(label));
    }

    public InheritedLabelQuery(Label label) {
        this(new LabelQuery(label));
    }

    private InheritedLabelQuery(LabelQuery query) {
        this.labelQuery = query;
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public List getParameters() {
        return this.labelQuery.getParameters();
    }

    public String getLabelAsString() {
        return this.labelQuery.getLabelAsString();
    }

    @Override
    public SearchQuery expand() {
        return new ConstantScoreQuery(new TermQuery(SearchFieldNames.INHERITED_LABEL, this.labelQuery.getLabelAsString()));
    }

    public boolean equals(Object obj) {
        return obj instanceof InheritedLabelQuery && this.labelQuery.equals(((InheritedLabelQuery)obj).labelQuery);
    }

    public int hashCode() {
        return this.labelQuery.hashCode();
    }
}


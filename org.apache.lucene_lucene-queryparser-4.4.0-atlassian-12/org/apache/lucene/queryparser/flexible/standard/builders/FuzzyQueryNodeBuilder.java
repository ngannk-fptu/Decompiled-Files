/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.index.Term
 *  org.apache.lucene.search.FuzzyQuery
 */
package org.apache.lucene.queryparser.flexible.standard.builders;

import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.core.nodes.FuzzyQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.apache.lucene.queryparser.flexible.standard.builders.StandardQueryBuilder;
import org.apache.lucene.search.FuzzyQuery;

public class FuzzyQueryNodeBuilder
implements StandardQueryBuilder {
    public FuzzyQuery build(QueryNode queryNode) throws QueryNodeException {
        FuzzyQueryNode fuzzyNode = (FuzzyQueryNode)queryNode;
        String text = fuzzyNode.getTextAsString();
        int numEdits = FuzzyQuery.floatToEdits((float)fuzzyNode.getSimilarity(), (int)text.codePointCount(0, text.length()));
        return new FuzzyQuery(new Term(fuzzyNode.getFieldAsString(), fuzzyNode.getTextAsString()), numEdits, fuzzyNode.getPrefixLength());
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.search.MultiTermQuery$RewriteMethod
 *  org.apache.lucene.search.TermRangeQuery
 */
package org.apache.lucene.queryparser.flexible.standard.builders;

import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.core.nodes.FieldQueryNode;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.apache.lucene.queryparser.flexible.core.util.StringUtils;
import org.apache.lucene.queryparser.flexible.standard.builders.StandardQueryBuilder;
import org.apache.lucene.queryparser.flexible.standard.nodes.TermRangeQueryNode;
import org.apache.lucene.search.MultiTermQuery;
import org.apache.lucene.search.TermRangeQuery;

public class TermRangeQueryNodeBuilder
implements StandardQueryBuilder {
    public TermRangeQuery build(QueryNode queryNode) throws QueryNodeException {
        TermRangeQueryNode rangeNode = (TermRangeQueryNode)queryNode;
        FieldQueryNode upper = (FieldQueryNode)rangeNode.getUpperBound();
        FieldQueryNode lower = (FieldQueryNode)rangeNode.getLowerBound();
        String field = StringUtils.toString(rangeNode.getField());
        String lowerText = lower.getTextAsString();
        String upperText = upper.getTextAsString();
        if (lowerText.length() == 0) {
            lowerText = null;
        }
        if (upperText.length() == 0) {
            upperText = null;
        }
        TermRangeQuery rangeQuery = TermRangeQuery.newStringRange((String)field, (String)lowerText, (String)upperText, (boolean)rangeNode.isLowerInclusive(), (boolean)rangeNode.isUpperInclusive());
        MultiTermQuery.RewriteMethod method = (MultiTermQuery.RewriteMethod)queryNode.getTag("MultiTermRewriteMethodConfiguration");
        if (method != null) {
            rangeQuery.setRewriteMethod(method);
        }
        return rangeQuery;
    }
}


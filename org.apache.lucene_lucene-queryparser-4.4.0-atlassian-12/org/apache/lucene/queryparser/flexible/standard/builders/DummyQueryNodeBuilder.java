/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.search.TermQuery
 */
package org.apache.lucene.queryparser.flexible.standard.builders;

import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;
import org.apache.lucene.queryparser.flexible.standard.builders.StandardQueryBuilder;
import org.apache.lucene.search.TermQuery;

public class DummyQueryNodeBuilder
implements StandardQueryBuilder {
    public TermQuery build(QueryNode queryNode) throws QueryNodeException {
        return null;
    }
}


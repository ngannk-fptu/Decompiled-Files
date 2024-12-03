/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queryparser.flexible.core.processors;

import org.apache.lucene.queryparser.flexible.core.QueryNodeException;
import org.apache.lucene.queryparser.flexible.core.config.QueryConfigHandler;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;

public interface QueryNodeProcessor {
    public QueryNode process(QueryNode var1) throws QueryNodeException;

    public void setQueryConfigHandler(QueryConfigHandler var1);

    public QueryConfigHandler getQueryConfigHandler();
}


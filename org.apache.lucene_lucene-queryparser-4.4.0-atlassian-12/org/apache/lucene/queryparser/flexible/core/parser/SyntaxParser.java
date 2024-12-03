/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.queryparser.flexible.core.parser;

import org.apache.lucene.queryparser.flexible.core.QueryNodeParseException;
import org.apache.lucene.queryparser.flexible.core.nodes.QueryNode;

public interface SyntaxParser {
    public QueryNode parse(CharSequence var1, CharSequence var2) throws QueryNodeParseException;
}


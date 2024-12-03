/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.search.Query
 */
package org.apache.lucene.queryparser.ext;

import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.ext.ExtensionQuery;
import org.apache.lucene.search.Query;

public abstract class ParserExtension {
    public abstract Query parse(ExtensionQuery var1) throws ParseException;
}


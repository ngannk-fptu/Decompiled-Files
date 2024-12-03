/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.querylang.antlrgen.AqlParser$MapExprValueContext
 */
package com.atlassian.confluence.plugins.cql.fields.dynamic;

import com.atlassian.querylang.antlrgen.AqlParser;

public interface ValueExpressionEvaluator {
    public Object evaluate(AqlParser.MapExprValueContext var1);
}


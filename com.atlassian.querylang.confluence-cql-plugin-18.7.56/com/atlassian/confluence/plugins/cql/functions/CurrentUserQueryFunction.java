/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.cql.spi.functions.CQLEvaluationContext
 *  com.atlassian.confluence.plugins.cql.spi.functions.CQLSingleValueQueryFunction
 */
package com.atlassian.confluence.plugins.cql.functions;

import com.atlassian.confluence.plugins.cql.spi.functions.CQLEvaluationContext;
import com.atlassian.confluence.plugins.cql.spi.functions.CQLSingleValueQueryFunction;
import java.util.List;

public class CurrentUserQueryFunction
extends CQLSingleValueQueryFunction {
    public CurrentUserQueryFunction() {
        super("currentUser");
    }

    public int paramCount() {
        return 0;
    }

    public String invoke(List<String> params, CQLEvaluationContext context) {
        return (String)context.getCurrentUser().getOrNull();
    }
}


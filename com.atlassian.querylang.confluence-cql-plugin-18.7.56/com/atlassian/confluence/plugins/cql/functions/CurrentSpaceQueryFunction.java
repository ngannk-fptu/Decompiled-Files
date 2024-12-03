/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.cql.spi.functions.CQLEvaluationContext
 *  com.atlassian.confluence.plugins.cql.spi.functions.CQLSingleValueQueryFunction
 *  com.atlassian.querylang.exceptions.InvalidFunctionQueryException
 */
package com.atlassian.confluence.plugins.cql.functions;

import com.atlassian.confluence.plugins.cql.spi.functions.CQLEvaluationContext;
import com.atlassian.confluence.plugins.cql.spi.functions.CQLSingleValueQueryFunction;
import com.atlassian.querylang.exceptions.InvalidFunctionQueryException;
import java.util.List;

public class CurrentSpaceQueryFunction
extends CQLSingleValueQueryFunction {
    public CurrentSpaceQueryFunction() {
        super("currentSpace");
    }

    public int paramCount() {
        return 0;
    }

    public String invoke(List<String> params, CQLEvaluationContext context) {
        return (String)context.currentSpaceKey().orElseThrow(() -> InvalidFunctionQueryException.invalidFunctionContext((String)this.name(), (String[])new String[]{"space"}));
    }
}


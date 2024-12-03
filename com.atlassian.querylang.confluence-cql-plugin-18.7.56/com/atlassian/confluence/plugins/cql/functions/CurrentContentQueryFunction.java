/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.id.ContentId
 *  com.atlassian.confluence.plugins.cql.spi.functions.CQLEvaluationContext
 *  com.atlassian.confluence.plugins.cql.spi.functions.CQLSingleValueQueryFunction
 */
package com.atlassian.confluence.plugins.cql.functions;

import com.atlassian.confluence.api.model.content.id.ContentId;
import com.atlassian.confluence.plugins.cql.spi.functions.CQLEvaluationContext;
import com.atlassian.confluence.plugins.cql.spi.functions.CQLSingleValueQueryFunction;
import java.util.List;

public class CurrentContentQueryFunction
extends CQLSingleValueQueryFunction {
    public CurrentContentQueryFunction() {
        super("currentContent");
    }

    public int paramCount() {
        return 0;
    }

    public String invoke(List<String> params, CQLEvaluationContext context) {
        return context.currentContent().orElse(ContentId.UNSET).serialise();
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.cql.spi.functions.CQLEvaluationContext
 */
package com.atlassian.confluence.plugins.cql.rest;

import com.atlassian.confluence.plugins.cql.rest.model.QueryExpression;
import com.atlassian.confluence.plugins.cql.rest.model.QueryField;
import com.atlassian.confluence.plugins.cql.spi.functions.CQLEvaluationContext;
import java.util.Map;

public interface CQLMetaDataService {
    public Iterable<QueryExpression> parseExpressions(String var1, CQLEvaluationContext var2);

    public Iterable<String> parseTextExpressions(String var1, CQLEvaluationContext var2);

    public Map<QueryField.FieldType, Iterable<QueryField>> getFields(GetFieldsFilter var1);

    public static enum GetFieldsFilter {
        WITH_UI_SUPPORT("withUiSupport"),
        ALL("all");

        private final String value;

        private GetFieldsFilter(String value) {
            this.value = value;
        }

        public static GetFieldsFilter fromString(String str) {
            for (GetFieldsFilter filter : GetFieldsFilter.values()) {
                if (!filter.value.equals(str)) continue;
                return filter;
            }
            return ALL;
        }

        public String value() {
            return this.value;
        }
    }
}


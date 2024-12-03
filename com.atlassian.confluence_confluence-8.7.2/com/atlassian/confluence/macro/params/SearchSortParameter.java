/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.macro.params;

import com.atlassian.confluence.macro.MacroExecutionContext;
import com.atlassian.confluence.macro.params.BaseParameter;
import com.atlassian.confluence.macro.params.ParameterException;
import com.atlassian.confluence.macro.params.SortType;
import com.atlassian.confluence.search.v2.SearchSort;
import com.atlassian.confluence.search.v2.sort.CreatedSort;
import com.atlassian.confluence.search.v2.sort.ModifiedSort;
import com.atlassian.confluence.search.v2.sort.TitleSort;
import java.util.List;

public class SearchSortParameter
extends BaseParameter<SearchSort> {
    private static final String[] DEFAULT_PARAM_NAMES = new String[]{"sort"};

    public SearchSortParameter() {
        this(null);
    }

    public SearchSortParameter(String defaultValue) {
        super(DEFAULT_PARAM_NAMES, defaultValue);
    }

    public SearchSortParameter(List<String> names, String defaultValue) {
        super(names, defaultValue);
    }

    @Override
    protected SearchSort findObject(String paramValue, MacroExecutionContext ctx) throws ParameterException {
        if (paramValue != null) {
            SortType sort = SortType.get(paramValue);
            Boolean reverse = (Boolean)new ReverseParameter().findValue(ctx);
            if (sort == null) {
                throw new ParameterException("sort parameter value cannot be null");
            }
            switch (sort) {
                case TITLE: {
                    return reverse != false ? TitleSort.DESCENDING : TitleSort.ASCENDING;
                }
                case CREATION: {
                    return reverse != false ? CreatedSort.DESCENDING : CreatedSort.ASCENDING;
                }
                case MODIFIED: {
                    return reverse != false ? ModifiedSort.DESCENDING : ModifiedSort.ASCENDING;
                }
            }
            throw new ParameterException("'" + paramValue + "' is not a recogized sort type");
        }
        return null;
    }

    private static class ReverseParameter
    extends BaseParameter<Boolean> {
        public ReverseParameter() {
            this(new String[]{"reverse"}, Boolean.FALSE);
        }

        public ReverseParameter(String[] names, Boolean defaultValue) {
            super(names, defaultValue.toString());
        }

        @Override
        protected Boolean findObject(String paramValue, MacroExecutionContext ctx) throws ParameterException {
            if (paramValue != null) {
                return Boolean.valueOf(paramValue);
            }
            return false;
        }
    }
}


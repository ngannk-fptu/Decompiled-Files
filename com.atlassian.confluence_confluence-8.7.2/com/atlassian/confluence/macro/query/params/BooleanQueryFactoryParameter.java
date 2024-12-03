/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.macro.query.params;

import com.atlassian.confluence.macro.MacroExecutionContext;
import com.atlassian.confluence.macro.params.BaseParameter;
import com.atlassian.confluence.macro.params.ParameterException;
import com.atlassian.confluence.macro.query.BooleanQueryFactory;
import com.atlassian.confluence.macro.query.InclusionCriteria;
import com.atlassian.confluence.macro.query.SearchQueryInterpreter;
import com.atlassian.confluence.macro.query.SearchQueryParserException;
import com.atlassian.confluence.macro.query.SimpleSearchQueryParser;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public abstract class BooleanQueryFactoryParameter
extends BaseParameter<BooleanQueryFactory> {
    private InclusionCriteria defaultInclusionCriteria = InclusionCriteria.ANY;

    protected BooleanQueryFactoryParameter(String name, String defaultValue) {
        super(name, defaultValue);
    }

    protected BooleanQueryFactoryParameter(String[] names, String defaultValue) {
        super(names, defaultValue);
    }

    protected BooleanQueryFactoryParameter(List<String> names, String defaultValue) {
        super(names, defaultValue);
    }

    public final void setDefaultInclusionCriteria(InclusionCriteria criteria) {
        this.defaultInclusionCriteria = criteria;
    }

    @Override
    protected final BooleanQueryFactory findObject(String paramValue, MacroExecutionContext ctx) throws ParameterException {
        return this.createBooleanQueryFactory(paramValue, ctx);
    }

    protected abstract SearchQueryInterpreter createSearchQueryInterpreter(MacroExecutionContext var1);

    protected BooleanQueryFactory createBooleanQueryFactory(String paramValue, MacroExecutionContext ctx) throws ParameterException {
        if (StringUtils.isNotEmpty((CharSequence)paramValue)) {
            try {
                SimpleSearchQueryParser parser = new SimpleSearchQueryParser(this.createSearchQueryInterpreter(ctx));
                parser.setDefaultInclusionCriteria(this.defaultInclusionCriteria);
                return parser.parse(paramValue);
            }
            catch (SearchQueryParserException sqpe) {
                throw new ParameterException(sqpe.getMessage());
            }
        }
        return null;
    }
}


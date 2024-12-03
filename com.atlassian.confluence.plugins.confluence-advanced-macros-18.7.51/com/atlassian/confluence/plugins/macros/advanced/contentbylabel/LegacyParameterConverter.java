/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.macro.MacroExecutionContext
 *  com.atlassian.confluence.macro.params.ParameterException
 *  com.atlassian.confluence.macro.query.BooleanQueryFactory
 *  com.atlassian.confluence.macro.query.InclusionCriteria
 *  com.atlassian.confluence.macro.query.params.AuthorParameter
 *  com.atlassian.confluence.macro.query.params.ContentTypeParameter
 *  com.atlassian.confluence.macro.query.params.LabelParameter
 *  com.atlassian.confluence.macro.query.params.SpaceKeyParameter
 *  com.atlassian.confluence.renderer.PageContext
 *  com.atlassian.confluence.search.v2.query.BooleanQuery
 *  com.atlassian.confluence.util.i18n.I18NBean
 *  com.atlassian.renderer.v2.macro.MacroException
 *  com.google.common.collect.Lists
 *  org.apache.commons.lang3.StringEscapeUtils
 */
package com.atlassian.confluence.plugins.macros.advanced.contentbylabel;

import com.atlassian.confluence.macro.MacroExecutionContext;
import com.atlassian.confluence.macro.params.ParameterException;
import com.atlassian.confluence.macro.query.BooleanQueryFactory;
import com.atlassian.confluence.macro.query.InclusionCriteria;
import com.atlassian.confluence.macro.query.params.AuthorParameter;
import com.atlassian.confluence.macro.query.params.ContentTypeParameter;
import com.atlassian.confluence.macro.query.params.LabelParameter;
import com.atlassian.confluence.macro.query.params.SpaceKeyParameter;
import com.atlassian.confluence.plugins.macros.advanced.contentbylabel.BooleanQueryConverter;
import com.atlassian.confluence.plugins.macros.advanced.contentbylabel.CompositeQueryExpression;
import com.atlassian.confluence.plugins.macros.advanced.contentbylabel.QueryExpression;
import com.atlassian.confluence.plugins.macros.advanced.contentbylabel.SimpleQueryExpression;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.renderer.v2.macro.MacroException;
import com.google.common.collect.Lists;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.StringEscapeUtils;

class LegacyParameterConverter {
    private static final String OPERATOR = "operator";
    private static final String OPERATOR_AND = "AND";
    private final I18NBean i18n;
    private final SpaceKeyParameter spaceKeyParam;
    private final LabelParameter labelParam;
    private final AuthorParameter authorParam;
    private final ContentTypeParameter contentTypeParam;

    LegacyParameterConverter(I18NBean i18n, SpaceKeyParameter spaceKeyParam, LabelParameter labelParam, AuthorParameter authorParam, ContentTypeParameter contentTypeParam) {
        this.i18n = i18n;
        this.spaceKeyParam = spaceKeyParam;
        this.labelParam = labelParam;
        this.authorParam = authorParam;
        this.contentTypeParam = contentTypeParam;
    }

    public String buildQueryStringFromLegacyParameters(Map<String, String> parameters, MacroExecutionContext ctx) throws MacroException {
        CompositeQueryExpression.Builder builder = CompositeQueryExpression.builder(CompositeQueryExpression.BooleanOperator.AND);
        builder.add(this.getLabelExpression(parameters, ctx));
        builder.add(this.getCreatorExpression(ctx));
        builder.add(this.getTypeExpression(ctx));
        builder.add(this.getSpaceExpression(ctx));
        return builder.build().toQueryString();
    }

    QueryExpression getSpaceExpression(MacroExecutionContext ctx) throws ParameterException {
        PageContext pageContext = new PageContext("currentSpace()");
        MacroExecutionContext macroExecutionContext = new MacroExecutionContext(ctx.getParams(), null, pageContext);
        BooleanQueryFactory spaceKeyQuery = (BooleanQueryFactory)this.spaceKeyParam.findValue(macroExecutionContext);
        if (spaceKeyQuery == null) {
            return null;
        }
        BooleanQuery booleanQuery = spaceKeyQuery.toBooleanQuery();
        return new BooleanQueryConverter(this.i18n).convertToExpression(booleanQuery);
    }

    private QueryExpression getTypeExpression(MacroExecutionContext ctx) throws MacroException {
        try {
            BooleanQueryFactory contentTypeQuery = (BooleanQueryFactory)this.contentTypeParam.findValue(ctx);
            if (contentTypeQuery != null) {
                return new BooleanQueryConverter(this.i18n).convertToExpression(contentTypeQuery.toBooleanQuery());
            }
        }
        catch (ParameterException pe) {
            throw new MacroException(this.i18n.getText("contentbylabel.error.parse-types-param", (Object[])new String[]{StringEscapeUtils.escapeHtml4((String)pe.getMessage())}), (Throwable)pe);
        }
        return null;
    }

    QueryExpression getLabelExpression(Map<String, String> parameters, MacroExecutionContext ctx) throws MacroException {
        BooleanQueryFactory queryFactory;
        if (OPERATOR_AND.equalsIgnoreCase(parameters.get(OPERATOR))) {
            this.labelParam.setDefaultInclusionCriteria(InclusionCriteria.ALL);
        }
        if ((queryFactory = (BooleanQueryFactory)this.labelParam.findValue(ctx)) == null) {
            throw new MacroException(this.i18n.getText("contentbylabel.error.label-parameter-required"));
        }
        BooleanQuery query = queryFactory.toBooleanQuery();
        return new BooleanQueryConverter(this.i18n).convertToExpression(query);
    }

    QueryExpression getCreatorExpression(MacroExecutionContext ctx) throws ParameterException {
        Set authors = (Set)this.authorParam.findValue(ctx);
        if (authors.isEmpty()) {
            return null;
        }
        return SimpleQueryExpression.of("creator", Lists.newArrayList((Iterable)authors));
    }
}


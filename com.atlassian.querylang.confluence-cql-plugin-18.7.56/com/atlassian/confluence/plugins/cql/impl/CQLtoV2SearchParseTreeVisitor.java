/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.cql.spi.v2search.query.NotQuery
 *  com.atlassian.confluence.plugins.cql.spi.v2searchhelpers.V2SearchQueryWrapper
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.query.BooleanQuery
 *  com.atlassian.confluence.search.v2.query.MatchNoDocsQuery
 *  com.atlassian.querylang.antlrgen.AqlBaseVisitor
 *  com.atlassian.querylang.antlrgen.AqlParser$AndClauseContext
 *  com.atlassian.querylang.antlrgen.AqlParser$DateTimeExprContext
 *  com.atlassian.querylang.antlrgen.AqlParser$EntityExprContext
 *  com.atlassian.querylang.antlrgen.AqlParser$MapExprContext
 *  com.atlassian.querylang.antlrgen.AqlParser$MapExprValueContext
 *  com.atlassian.querylang.antlrgen.AqlParser$MapKeyContext
 *  com.atlassian.querylang.antlrgen.AqlParser$MapPathContext
 *  com.atlassian.querylang.antlrgen.AqlParser$NotClauseContext
 *  com.atlassian.querylang.antlrgen.AqlParser$NumericExprContext
 *  com.atlassian.querylang.antlrgen.AqlParser$OrClauseContext
 *  com.atlassian.querylang.antlrgen.AqlParser$SubClauseContext
 *  com.atlassian.querylang.antlrgen.AqlParser$TextExprContext
 *  com.atlassian.querylang.exceptions.InvalidDynamicFieldQueryException
 *  com.atlassian.querylang.fields.DateTimeFieldHandler
 *  com.atlassian.querylang.fields.DateTimePrecision
 *  com.atlassian.querylang.fields.EqualityFieldHandler
 *  com.atlassian.querylang.fields.NumericFieldHandler
 *  com.atlassian.querylang.fields.TextFieldHandler
 *  com.atlassian.querylang.fields.expressiondata.RangeExpressionData
 *  com.atlassian.querylang.lib.fields.FieldRegistry
 *  com.atlassian.querylang.lib.fields.MapFieldHandler
 *  com.atlassian.querylang.lib.fields.MapFieldHandler$ValidationResult
 *  com.atlassian.querylang.lib.fields.MapFieldHandler$ValueType
 *  com.atlassian.querylang.lib.fields.expressiondata.ExpressionDataFactory
 *  com.atlassian.querylang.literals.DateLiteralHelper
 *  com.atlassian.querylang.query.SearchQuery
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Lists
 *  org.antlr.v4.runtime.misc.NotNull
 */
package com.atlassian.confluence.plugins.cql.impl;

import com.atlassian.confluence.plugins.cql.fields.dynamic.MapFieldValueEvaluatorRegistry;
import com.atlassian.confluence.plugins.cql.impl.CQLIterableStringValueParseTreeVisitor;
import com.atlassian.confluence.plugins.cql.impl.CQLStringValueParseTreeVisitor;
import com.atlassian.confluence.plugins.cql.spi.v2search.query.NotQuery;
import com.atlassian.confluence.plugins.cql.spi.v2searchhelpers.V2SearchQueryWrapper;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.MatchNoDocsQuery;
import com.atlassian.querylang.antlrgen.AqlBaseVisitor;
import com.atlassian.querylang.antlrgen.AqlParser;
import com.atlassian.querylang.exceptions.InvalidDynamicFieldQueryException;
import com.atlassian.querylang.fields.DateTimeFieldHandler;
import com.atlassian.querylang.fields.DateTimePrecision;
import com.atlassian.querylang.fields.EqualityFieldHandler;
import com.atlassian.querylang.fields.NumericFieldHandler;
import com.atlassian.querylang.fields.TextFieldHandler;
import com.atlassian.querylang.fields.expressiondata.RangeExpressionData;
import com.atlassian.querylang.lib.fields.FieldRegistry;
import com.atlassian.querylang.lib.fields.MapFieldHandler;
import com.atlassian.querylang.lib.fields.expressiondata.ExpressionDataFactory;
import com.atlassian.querylang.literals.DateLiteralHelper;
import com.atlassian.querylang.query.SearchQuery;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.Iterator;
import org.antlr.v4.runtime.misc.NotNull;

public class CQLtoV2SearchParseTreeVisitor
extends AqlBaseVisitor<com.atlassian.confluence.search.v2.SearchQuery> {
    private final FieldRegistry registry;
    private final CQLStringValueParseTreeVisitor cqlStringValueParseTreeVisitor;
    private final CQLIterableStringValueParseTreeVisitor cqlIterableStringValueParseTreeVisitor;
    private final MapFieldValueEvaluatorRegistry mapFieldValueEvaluatorRegistry;
    private final ExpressionDataFactory expressionDataFactory;

    public CQLtoV2SearchParseTreeVisitor(FieldRegistry fieldRegistry, CQLStringValueParseTreeVisitor cqlStringValueParseTreeVisitor, CQLIterableStringValueParseTreeVisitor cqlIterableStringValueParseTreeVisitor, ExpressionDataFactory expressionDataFactory) {
        this.registry = fieldRegistry;
        this.cqlStringValueParseTreeVisitor = cqlStringValueParseTreeVisitor;
        this.expressionDataFactory = expressionDataFactory;
        this.cqlIterableStringValueParseTreeVisitor = cqlIterableStringValueParseTreeVisitor;
        this.mapFieldValueEvaluatorRegistry = new MapFieldValueEvaluatorRegistry(cqlStringValueParseTreeVisitor, cqlIterableStringValueParseTreeVisitor);
    }

    public com.atlassian.confluence.search.v2.SearchQuery visitOrClause(@NotNull AqlParser.OrClauseContext ctx) {
        Iterator it = ctx.andClause().iterator();
        com.atlassian.confluence.search.v2.SearchQuery query = this.visitAndClause((AqlParser.AndClauseContext)it.next());
        while (it.hasNext()) {
            query = BooleanQuery.orQuery((com.atlassian.confluence.search.v2.SearchQuery[])new com.atlassian.confluence.search.v2.SearchQuery[]{query, this.visitAndClause((AqlParser.AndClauseContext)it.next())});
        }
        return query;
    }

    public com.atlassian.confluence.search.v2.SearchQuery visitAndClause(@NotNull AqlParser.AndClauseContext ctx) {
        com.atlassian.confluence.search.v2.SearchQuery query = this.visitSubClause(ctx.subClause());
        for (AqlParser.NotClauseContext notClause : ctx.notClause()) {
            com.atlassian.confluence.search.v2.SearchQuery subQuery = this.visitNotClause(notClause);
            if (subQuery instanceof NotQuery) {
                NotQuery notQuery = (NotQuery)subQuery;
                query = new BooleanQuery((Collection)ImmutableList.of((Object)query), null, (Collection)ImmutableList.of((Object)notQuery.getSubQuery()));
                continue;
            }
            if (query instanceof NotQuery) {
                query = new BooleanQuery((Collection)ImmutableList.of((Object)subQuery), null, (Collection)ImmutableList.of((Object)((NotQuery)query).getSubQuery()));
                continue;
            }
            query = BooleanQuery.andQuery((com.atlassian.confluence.search.v2.SearchQuery[])new com.atlassian.confluence.search.v2.SearchQuery[]{query, subQuery});
        }
        return query;
    }

    public com.atlassian.confluence.search.v2.SearchQuery visitNotClause(@NotNull AqlParser.NotClauseContext ctx) {
        com.atlassian.confluence.search.v2.SearchQuery query = this.visitSubClause(ctx.subClause());
        if (ctx.not() != null) {
            return this.negate(query);
        }
        return query;
    }

    public com.atlassian.confluence.search.v2.SearchQuery visitSubClause(@NotNull AqlParser.SubClauseContext ctx) {
        if (ctx.clause() != null) {
            return (com.atlassian.confluence.search.v2.SearchQuery)this.visitClause(ctx.clause());
        }
        return (com.atlassian.confluence.search.v2.SearchQuery)this.visitExpr(ctx.expr());
    }

    private com.atlassian.confluence.search.v2.SearchQuery negate(com.atlassian.confluence.search.v2.SearchQuery query) {
        if (query instanceof NotQuery) {
            return ((NotQuery)query).getSubQuery();
        }
        return new NotQuery(query);
    }

    protected com.atlassian.confluence.search.v2.SearchQuery aggregateResult(com.atlassian.confluence.search.v2.SearchQuery aggregate, com.atlassian.confluence.search.v2.SearchQuery nextResult) {
        if (aggregate == null) {
            return nextResult;
        }
        if (nextResult == null) {
            return aggregate;
        }
        return BooleanQuery.andQuery((com.atlassian.confluence.search.v2.SearchQuery[])new com.atlassian.confluence.search.v2.SearchQuery[]{aggregate, nextResult});
    }

    public com.atlassian.confluence.search.v2.SearchQuery visitTextExpr(@NotNull AqlParser.TextExprContext ctx) {
        String fieldName = ctx.textField().getText();
        TextFieldHandler fieldHandler = this.registry.getTextFieldHandler(fieldName);
        return this.convertToV2SearchQuery(fieldHandler.build(this.expressionDataFactory.create(fieldName, ctx.textOp()), (String)this.cqlStringValueParseTreeVisitor.visitTextExpr(ctx)));
    }

    public com.atlassian.confluence.search.v2.SearchQuery visitMapExpr(@NotNull AqlParser.MapExprContext ctx) {
        AqlParser.MapExprValueContext mapExprValueContext;
        AqlParser.MapPathContext mapPathContext;
        AqlParser.MapKeyContext mapKeyContext;
        String fieldName = ctx.mapField().getText();
        MapFieldHandler mapFieldHandler = this.registry.getMapFieldHandler(fieldName);
        MapFieldHandler.ValidationResult validationResult = mapFieldHandler.validate(mapKeyContext = ctx.mapKey(), mapPathContext = ctx.mapPath(), mapExprValueContext = ctx.mapExprValue());
        if (validationResult.isValid()) {
            MapFieldHandler.ValueType type = mapFieldHandler.getValueType(mapKeyContext, mapPathContext);
            return this.convertToV2SearchQuery(mapFieldHandler.build(mapKeyContext, mapPathContext, mapExprValueContext, this.mapFieldValueEvaluatorRegistry.getEvaluator(type).evaluate(mapExprValueContext)));
        }
        throw new InvalidDynamicFieldQueryException(validationResult.getMessage());
    }

    public com.atlassian.confluence.search.v2.SearchQuery visitEntityExpr(@NotNull AqlParser.EntityExprContext ctx) {
        String fieldName = ctx.entityField().getText();
        EqualityFieldHandler fieldHandler = this.registry.getEqualityFieldHandler(fieldName);
        if (ctx.setOp() != null) {
            Iterable setOperandValues = (Iterable)this.cqlIterableStringValueParseTreeVisitor.visitSetOperand(ctx.setOperand());
            if (!setOperandValues.iterator().hasNext()) {
                return MatchNoDocsQuery.getInstance();
            }
            return this.convertToV2SearchQuery(fieldHandler.build(this.expressionDataFactory.create(fieldName, ctx.setOp()), (Iterable)Lists.newArrayList((Iterable)setOperandValues)));
        }
        if (ctx.eqOp() != null) {
            return this.convertToV2SearchQuery(fieldHandler.build(this.expressionDataFactory.create(fieldName, ctx.eqOp()), (Object)((String)this.cqlStringValueParseTreeVisitor.visitValue(ctx.value()))));
        }
        throw new IllegalStateException("Unrecognized operator" + ctx);
    }

    public com.atlassian.confluence.search.v2.SearchQuery visitDateTimeExpr(@NotNull AqlParser.DateTimeExprContext ctx) {
        String fieldName = ctx.dateTimeField().getText();
        DateTimeFieldHandler fieldHandler = this.registry.getDateTimeFieldHandler(fieldName);
        RangeExpressionData rangeExpressionData = this.expressionDataFactory.create(fieldName, ctx.rangeOp());
        String value = (String)this.cqlStringValueParseTreeVisitor.visitDateTimeValue(ctx.dateTimeValue());
        DateTimePrecision dateValue = DateLiteralHelper.create((String)value);
        return this.convertToV2SearchQuery(fieldHandler.build(rangeExpressionData, dateValue));
    }

    public com.atlassian.confluence.search.v2.SearchQuery visitNumericExpr(@NotNull AqlParser.NumericExprContext ctx) {
        String fieldName = ctx.numericField().getText();
        NumericFieldHandler fieldHandler = this.registry.getNumberFieldHandler(fieldName);
        RangeExpressionData rangeExpressionData = this.expressionDataFactory.create(fieldName, ctx.rangeOp());
        String value = (String)this.cqlStringValueParseTreeVisitor.visitNumericValue(ctx.numericValue());
        return this.convertToV2SearchQuery(fieldHandler.build(rangeExpressionData, value));
    }

    private com.atlassian.confluence.search.v2.SearchQuery convertToV2SearchQuery(SearchQuery aqlQuery) {
        if (aqlQuery instanceof com.atlassian.confluence.search.v2.SearchQuery) {
            return (com.atlassian.confluence.search.v2.SearchQuery)aqlQuery;
        }
        if (aqlQuery instanceof V2SearchQueryWrapper) {
            return ((V2SearchQueryWrapper)aqlQuery).toV2SearchQuery();
        }
        throw new IllegalArgumentException("Cannot convert to v2 Search Query");
    }
}


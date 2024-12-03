/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.exceptions.unchecked.NotImplementedServiceException
 *  com.atlassian.querylang.antlrgen.AqlParser$AndClauseContext
 *  com.atlassian.querylang.antlrgen.AqlParser$DateTimeExprContext
 *  com.atlassian.querylang.antlrgen.AqlParser$EntityExprContext
 *  com.atlassian.querylang.antlrgen.AqlParser$NotClauseContext
 *  com.atlassian.querylang.antlrgen.AqlParser$OrClauseContext
 *  com.atlassian.querylang.antlrgen.AqlParser$SetOpContext
 *  com.atlassian.querylang.antlrgen.AqlParser$SetValueContext
 *  com.atlassian.querylang.antlrgen.AqlParser$SubClauseContext
 *  com.atlassian.querylang.antlrgen.AqlParser$TextExprContext
 *  com.atlassian.querylang.antlrgen.AqlParser$TextOpContext
 *  com.atlassian.querylang.antlrgen.AqlParser$ValueContext
 *  com.atlassian.querylang.fields.expressiondata.EqualityExpressionData$Operator
 *  com.atlassian.querylang.fields.expressiondata.RangeExpressionData$Operator
 *  com.atlassian.querylang.fields.expressiondata.SetExpressionData$Operator
 *  com.atlassian.querylang.fields.expressiondata.TextExpressionData$Operator
 *  com.atlassian.querylang.lib.fields.expressiondata.ExpressionDataFactory
 *  com.atlassian.querylang.lib.visitor.AqlBaseVisitorToIterable
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  org.antlr.v4.runtime.ParserRuleContext
 *  org.antlr.v4.runtime.misc.NotNull
 *  org.antlr.v4.runtime.tree.ParseTree
 */
package com.atlassian.confluence.plugins.cql.impl;

import com.atlassian.confluence.api.service.exceptions.unchecked.NotImplementedServiceException;
import com.atlassian.confluence.plugins.cql.impl.CQLQueryFunctionValueParseTreeVisitor;
import com.atlassian.confluence.plugins.cql.impl.CQLStringValueParseTreeVisitor;
import com.atlassian.confluence.plugins.cql.rest.RestUiSupportFactory;
import com.atlassian.confluence.plugins.cql.rest.model.QueryExpression;
import com.atlassian.confluence.plugins.cql.rest.model.QueryField;
import com.atlassian.confluence.plugins.cql.rest.model.QueryOperator;
import com.atlassian.querylang.antlrgen.AqlParser;
import com.atlassian.querylang.fields.expressiondata.EqualityExpressionData;
import com.atlassian.querylang.fields.expressiondata.RangeExpressionData;
import com.atlassian.querylang.fields.expressiondata.SetExpressionData;
import com.atlassian.querylang.fields.expressiondata.TextExpressionData;
import com.atlassian.querylang.lib.fields.expressiondata.ExpressionDataFactory;
import com.atlassian.querylang.lib.visitor.AqlBaseVisitorToIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.misc.NotNull;
import org.antlr.v4.runtime.tree.ParseTree;

public class CQLtoRESTParseTreeVisitor
extends AqlBaseVisitorToIterable<QueryExpression> {
    private final CQLStringValueParseTreeVisitor cqlStringValueParseTreeVisitor;
    private final CQLQueryFunctionValueParseTreeVisitor functionVisitor;
    private final RestUiSupportFactory restUiSupportFactory;
    private final ExpressionDataFactory expressionDataFactory;

    public CQLtoRESTParseTreeVisitor(CQLStringValueParseTreeVisitor stringValueParseTreeVisitor, CQLQueryFunctionValueParseTreeVisitor functionVisitor, RestUiSupportFactory restUiSupportFactory, ExpressionDataFactory expressionDataFactory) {
        this.cqlStringValueParseTreeVisitor = stringValueParseTreeVisitor;
        this.functionVisitor = functionVisitor;
        this.restUiSupportFactory = restUiSupportFactory;
        this.expressionDataFactory = expressionDataFactory;
    }

    public Iterable<QueryExpression> visit(@NotNull ParseTree tree) {
        return ImmutableList.builder().addAll((Iterable)super.visit(tree)).build();
    }

    public Iterable<QueryExpression> visitOrClause(@NotNull AqlParser.OrClauseContext ctx) {
        if (!ctx.K_OR().isEmpty()) {
            throw new NotImplementedServiceException("OR clauses are unsupported in parsing to REST");
        }
        return this.visitAndClause(ctx.andClause(0));
    }

    public Iterable<QueryExpression> visitAndClause(@NotNull AqlParser.AndClauseContext ctx) {
        ArrayList clauses = Lists.newArrayList((Iterable)this.visitSubClause(ctx.subClause()));
        for (AqlParser.NotClauseContext notClause : ctx.notClause()) {
            Iterables.addAll((Collection)clauses, (Iterable)this.visitNotClause(notClause));
        }
        return clauses;
    }

    public Iterable<QueryExpression> visitNotClause(@NotNull AqlParser.NotClauseContext ctx) {
        Object query = this.visitSubClause(ctx.subClause());
        if (Iterables.size((Iterable)query) > 1) {
            throw new NotImplementedServiceException("Negating multiple parenthesized expressions is not supported");
        }
        if (ctx.not() != null) {
            return this.negate((Iterable<QueryExpression>)query);
        }
        return query;
    }

    public Iterable<QueryExpression> visitSubClause(@NotNull AqlParser.SubClauseContext ctx) {
        if (ctx.clause() != null) {
            return (Iterable)this.visitClause(ctx.clause());
        }
        return (Iterable)this.visitExpr(ctx.expr());
    }

    private Iterable<QueryExpression> negate(Iterable<QueryExpression> query) {
        return StreamSupport.stream(query.spliterator(), false).map(QueryExpression::negate).collect(Collectors.toList());
    }

    private QueryOperator convertToOperator(ParserRuleContext ctx) {
        return new QueryOperator(ctx.getText());
    }

    public Iterable<QueryExpression> visitTextExpr(@NotNull AqlParser.TextExprContext ctx) {
        String fieldName = ctx.textField().getText();
        String value = (String)this.cqlStringValueParseTreeVisitor.visitTextExpr(ctx);
        Iterable functionValues = (Iterable)this.functionVisitor.visitTextExpr(ctx);
        AqlParser.TextOpContext opContext = ctx.textOp();
        QueryField queryField = QueryField.builder().name(fieldName).type(QueryField.FieldType.TEXT).uiSupport(this.restUiSupportFactory.makeUiSupport(fieldName, QueryField.FieldType.TEXT)).build();
        QueryExpression expression = new QueryExpression(queryField, this.convertToOperator((ParserRuleContext)opContext), Lists.newArrayList((Object[])new String[]{value}), functionValues);
        if (((TextExpressionData.Operator)this.expressionDataFactory.create(fieldName, opContext).getOperator()).negate()) {
            expression = expression.negate();
        }
        return Collections.singletonList(expression);
    }

    public Iterable<QueryExpression> visitEntityExpr(@NotNull AqlParser.EntityExprContext ctx) {
        boolean negate;
        AqlParser.SetOpContext opCtx;
        Iterable stringValues;
        String fieldName = ctx.entityField().getText();
        if (ctx.setOp() != null) {
            stringValues = ctx.setOperand().setValue().stream().map(this::setValueCtxToString).collect(Collectors.toList());
            opCtx = ctx.setOp();
            negate = ((SetExpressionData.Operator)this.expressionDataFactory.create(fieldName, ctx.setOp()).getOperator()).negate();
        } else if (ctx.eqOp() != null) {
            stringValues = Lists.newArrayList((Object[])new String[]{this.valueCtxToString(ctx.value())});
            opCtx = ctx.eqOp();
            negate = ((EqualityExpressionData.Operator)this.expressionDataFactory.create(fieldName, ctx.eqOp()).getOperator()).negate();
        } else {
            throw new IllegalStateException("Unrecognized operator" + ctx);
        }
        Iterable functionValues = (Iterable)this.functionVisitor.visitEntityExpr(ctx);
        QueryField queryField = QueryField.builder().name(fieldName).type(QueryField.FieldType.EQUALITY).uiSupport(this.restUiSupportFactory.makeUiSupport(fieldName, QueryField.FieldType.EQUALITY)).build();
        QueryExpression expression = new QueryExpression(queryField, this.convertToOperator((ParserRuleContext)opCtx), stringValues, functionValues);
        if (negate) {
            expression = expression.negate();
        }
        return Collections.singletonList(expression);
    }

    public Iterable<QueryExpression> visitDateTimeExpr(@NotNull AqlParser.DateTimeExprContext ctx) {
        String fieldName = ctx.dateTimeField().getText();
        String value = (String)this.cqlStringValueParseTreeVisitor.visitDateTimeValue(ctx.dateTimeValue());
        Iterable functionValues = (Iterable)this.functionVisitor.visitDateTimeExpr(ctx);
        QueryField queryField = QueryField.builder().name(fieldName).type(QueryField.FieldType.DATE).uiSupport(this.restUiSupportFactory.makeUiSupport(fieldName, QueryField.FieldType.DATE)).build();
        QueryExpression expression = new QueryExpression(queryField, this.convertToOperator((ParserRuleContext)ctx.rangeOp()), Lists.newArrayList((Object[])new String[]{value}), functionValues);
        if (((RangeExpressionData.Operator)this.expressionDataFactory.create(fieldName, ctx.rangeOp()).getOperator()).negate()) {
            expression = expression.negate();
        }
        return Collections.singletonList(expression);
    }

    private String valueCtxToString(AqlParser.ValueContext value) {
        return (String)this.cqlStringValueParseTreeVisitor.visitValue(value);
    }

    private String setValueCtxToString(AqlParser.SetValueContext value) {
        return (String)this.cqlStringValueParseTreeVisitor.visitSetValue(value);
    }
}


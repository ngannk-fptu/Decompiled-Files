/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.confluence.api.service.exceptions.ServiceException
 *  com.atlassian.confluence.plugins.cql.spi.functions.CQLEvaluationContext
 *  com.atlassian.querylang.antlrgen.AqlParser
 *  com.atlassian.querylang.antlrgen.AqlParser$AqlStatementContext
 *  com.atlassian.querylang.exceptions.QueryException
 *  com.atlassian.querylang.fields.FieldHandler
 *  com.atlassian.querylang.lib.fields.FieldRegistry
 *  com.atlassian.querylang.lib.fields.FieldRegistryProvider
 *  com.atlassian.querylang.lib.fields.expressiondata.ExpressionDataFactory
 *  com.atlassian.querylang.lib.functions.FunctionRegistry
 *  com.atlassian.querylang.lib.functions.FunctionRegistryProvider
 *  com.atlassian.querylang.lib.parserfactory.AqlParserFactory
 *  com.atlassian.querylang.lib.parserfactory.BaseParserConfig
 *  com.atlassian.querylang.lib.parserfactory.DefaultParserFactory
 *  com.atlassian.querylang.lib.parserfactory.ParserConfig
 *  org.antlr.v4.runtime.misc.ParseCancellationException
 *  org.antlr.v4.runtime.tree.ParseTree
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.cql.rest;

import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.plugins.cql.impl.CQLQueryFunctionValueParseTreeVisitor;
import com.atlassian.confluence.plugins.cql.impl.CQLStringValueParseTreeVisitor;
import com.atlassian.confluence.plugins.cql.impl.CQLTextExprParseTreeVisitor;
import com.atlassian.confluence.plugins.cql.impl.CQLtoRESTParseTreeVisitor;
import com.atlassian.confluence.plugins.cql.impl.QueryExceptionMapper;
import com.atlassian.confluence.plugins.cql.rest.CQLMetaDataService;
import com.atlassian.confluence.plugins.cql.rest.RestUiSupportFactory;
import com.atlassian.confluence.plugins.cql.rest.model.QueryExpression;
import com.atlassian.confluence.plugins.cql.rest.model.QueryField;
import com.atlassian.confluence.plugins.cql.rest.model.QueryOperator;
import com.atlassian.confluence.plugins.cql.rest.model.RestUiSupport;
import com.atlassian.confluence.plugins.cql.spi.functions.CQLEvaluationContext;
import com.atlassian.querylang.antlrgen.AqlParser;
import com.atlassian.querylang.exceptions.QueryException;
import com.atlassian.querylang.fields.FieldHandler;
import com.atlassian.querylang.lib.fields.FieldRegistry;
import com.atlassian.querylang.lib.fields.FieldRegistryProvider;
import com.atlassian.querylang.lib.fields.expressiondata.ExpressionDataFactory;
import com.atlassian.querylang.lib.functions.FunctionRegistry;
import com.atlassian.querylang.lib.functions.FunctionRegistryProvider;
import com.atlassian.querylang.lib.parserfactory.AqlParserFactory;
import com.atlassian.querylang.lib.parserfactory.BaseParserConfig;
import com.atlassian.querylang.lib.parserfactory.DefaultParserFactory;
import com.atlassian.querylang.lib.parserfactory.ParserConfig;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import org.antlr.v4.runtime.misc.ParseCancellationException;
import org.antlr.v4.runtime.tree.ParseTree;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DefaultCQLMetaDataService
implements CQLMetaDataService {
    private final FieldRegistryProvider cqlFieldRegistryProvider;
    private final FunctionRegistryProvider cqlFunctionRegistryProvider;
    private final RestUiSupportFactory restUiSupportFactory;
    private final ParserConfig config = BaseParserConfig.DEFAULT_CONFIG;
    private final AqlParserFactory parserFactory = new DefaultParserFactory();
    private final ExpressionDataFactory expressionDataFactory;

    @Autowired
    public DefaultCQLMetaDataService(FieldRegistryProvider cqlFieldRegistryProvider, FunctionRegistryProvider cqlFunctionRegistryProvider, RestUiSupportFactory restUiSupportFactory, ExpressionDataFactory expressionDataFactory) {
        this.cqlFieldRegistryProvider = cqlFieldRegistryProvider;
        this.cqlFunctionRegistryProvider = cqlFunctionRegistryProvider;
        this.restUiSupportFactory = restUiSupportFactory;
        this.expressionDataFactory = expressionDataFactory;
    }

    @Override
    public Iterable<QueryExpression> parseExpressions(String cqlInput, CQLEvaluationContext evaluationContext) {
        try {
            FieldRegistry fieldRegistry = this.cqlFieldRegistryProvider.getFieldRegistry();
            FunctionRegistry functionRegistry = this.cqlFunctionRegistryProvider.getFunctionRegistry();
            AqlParser parser = this.parserFactory.createParser(cqlInput, fieldRegistry, functionRegistry, this.config);
            return this.visitParseTree(parser.aqlStatement(), functionRegistry, evaluationContext);
        }
        catch (IOException e) {
            throw new ServiceException("IOException executing cql : " + cqlInput, (Throwable)e);
        }
        catch (ParseCancellationException e) {
            throw new BadRequestException("Could not parse cql : " + cqlInput, (Throwable)e);
        }
        catch (QueryException e) {
            throw QueryExceptionMapper.mapToServiceException(e);
        }
    }

    @Override
    public Iterable<String> parseTextExpressions(String cqlInput, CQLEvaluationContext evaluationContext) {
        try {
            FieldRegistry fieldRegistry = this.cqlFieldRegistryProvider.getFieldRegistry();
            FunctionRegistry functionRegistry = this.cqlFunctionRegistryProvider.getFunctionRegistry();
            AqlParser parser = this.parserFactory.createParser(cqlInput, fieldRegistry, functionRegistry, this.config);
            return this.visitTextExprParseTree(parser.aqlStatement(), functionRegistry, evaluationContext);
        }
        catch (IOException e) {
            throw new ServiceException("IOException executing cql : " + cqlInput, (Throwable)e);
        }
        catch (ParseCancellationException e) {
            throw new BadRequestException("Could not parse cql : " + cqlInput, (Throwable)e);
        }
        catch (QueryException e) {
            throw QueryExceptionMapper.mapToServiceException(e);
        }
    }

    private Iterable<QueryExpression> visitParseTree(AqlParser.AqlStatementContext aqlStatement, FunctionRegistry functionRegistry, CQLEvaluationContext evalContext) {
        CQLStringValueParseTreeVisitor cqlStringValueParseTreeVisitor = new CQLStringValueParseTreeVisitor(functionRegistry, evalContext);
        CQLQueryFunctionValueParseTreeVisitor functionVisitor = new CQLQueryFunctionValueParseTreeVisitor();
        CQLtoRESTParseTreeVisitor visitor = new CQLtoRESTParseTreeVisitor(cqlStringValueParseTreeVisitor, functionVisitor, this.restUiSupportFactory, this.expressionDataFactory);
        return visitor.visit((ParseTree)aqlStatement);
    }

    private Iterable<String> visitTextExprParseTree(AqlParser.AqlStatementContext aqlStatement, FunctionRegistry functionRegistry, CQLEvaluationContext evalContext) {
        CQLStringValueParseTreeVisitor cqlStringValueParseTreeVisitor = new CQLStringValueParseTreeVisitor(functionRegistry, evalContext);
        CQLTextExprParseTreeVisitor visitor = new CQLTextExprParseTreeVisitor(cqlStringValueParseTreeVisitor);
        return visitor.visit((ParseTree)aqlStatement);
    }

    @Override
    public Map<QueryField.FieldType, Iterable<QueryField>> getFields(CQLMetaDataService.GetFieldsFilter filter) {
        HashMap<QueryField.FieldType, Iterable<QueryField>> results = new HashMap<QueryField.FieldType, Iterable<QueryField>>();
        FieldRegistry registry = this.cqlFieldRegistryProvider.getFieldRegistry();
        results.put(QueryField.FieldType.TEXT, this.toFieldDTO(registry.getTextFieldHandlers(), QueryField.FieldType.TEXT, this.toOperatorDTOs(FieldRegistry.getTextOperators()), filter));
        results.put(QueryField.FieldType.NUMBER, this.toFieldDTO(registry.getNumericFieldHandlers(), QueryField.FieldType.NUMBER, this.toOperatorDTOs(FieldRegistry.getRangeOperators()), filter));
        results.put(QueryField.FieldType.EQUALITY, this.toFieldDTO(registry.getEqualityFieldHandlers(), QueryField.FieldType.EQUALITY, this.toOperatorDTOs(FieldRegistry.getEqualityOperators()), filter));
        results.put(QueryField.FieldType.DATE, this.toFieldDTO(registry.getDateFieldHandlers(), QueryField.FieldType.DATE, this.toOperatorDTOs(FieldRegistry.getRangeOperators()), filter));
        return results;
    }

    private Iterable<QueryOperator> toOperatorDTOs(List<String> textOperators) {
        return textOperators.stream().map(QueryOperator::new).collect(Collectors.toList());
    }

    private Iterable<QueryField> toFieldDTO(Iterable<? extends FieldHandler> handlers, QueryField.FieldType type, Iterable<QueryOperator> operators, CQLMetaDataService.GetFieldsFilter filter) {
        Stream<QueryField> queryFields = StreamSupport.stream(handlers.spliterator(), false).map(input -> {
            RestUiSupport uiSupport = this.restUiSupportFactory.makeUiSupport(input.getFieldMetaData().uiSupport(), type);
            if (filter == CQLMetaDataService.GetFieldsFilter.WITH_UI_SUPPORT && uiSupport == null) {
                return null;
            }
            return QueryField.builder().name(input.fieldName()).type(type).supportedOps(operators).uiSupport(uiSupport).build();
        });
        if (filter == CQLMetaDataService.GetFieldsFilter.WITH_UI_SUPPORT) {
            queryFields = queryFields.filter(Objects::nonNull);
        }
        return queryFields.collect(Collectors.toList());
    }
}


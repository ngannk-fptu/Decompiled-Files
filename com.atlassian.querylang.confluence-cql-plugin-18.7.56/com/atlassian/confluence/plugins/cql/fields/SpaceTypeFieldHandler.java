/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.content.SpaceType
 *  com.atlassian.confluence.api.service.exceptions.BadRequestException
 *  com.atlassian.confluence.labels.LabelManager
 *  com.atlassian.confluence.plugins.cql.spi.v2searchhelpers.V2FieldHandlerHelper
 *  com.atlassian.confluence.plugins.cql.spi.v2searchhelpers.V2SearchQueryWrapper
 *  com.atlassian.confluence.search.service.SpaceCategoryEnum
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.query.SpaceCategoryQuery
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.querylang.fields.BaseFieldHandler
 *  com.atlassian.querylang.fields.EqualityFieldHandler
 *  com.atlassian.querylang.fields.expressiondata.EqualityExpressionData
 *  com.atlassian.querylang.fields.expressiondata.EqualityExpressionData$Operator
 *  com.atlassian.querylang.fields.expressiondata.ExpressionData
 *  com.atlassian.querylang.fields.expressiondata.SetExpressionData
 *  com.atlassian.querylang.fields.expressiondata.SetExpressionData$Operator
 *  com.google.common.collect.Sets
 */
package com.atlassian.confluence.plugins.cql.fields;

import com.atlassian.confluence.api.model.content.SpaceType;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.plugins.cql.spi.v2searchhelpers.V2FieldHandlerHelper;
import com.atlassian.confluence.plugins.cql.spi.v2searchhelpers.V2SearchQueryWrapper;
import com.atlassian.confluence.search.service.SpaceCategoryEnum;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.query.SpaceCategoryQuery;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.querylang.fields.BaseFieldHandler;
import com.atlassian.querylang.fields.EqualityFieldHandler;
import com.atlassian.querylang.fields.expressiondata.EqualityExpressionData;
import com.atlassian.querylang.fields.expressiondata.ExpressionData;
import com.atlassian.querylang.fields.expressiondata.SetExpressionData;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class SpaceTypeFieldHandler
extends BaseFieldHandler
implements EqualityFieldHandler<String, V2SearchQueryWrapper> {
    private static final String FIELD_NAME = "space.type";
    private final LabelManager labelManager;

    public SpaceTypeFieldHandler(@ComponentImport LabelManager labelManager) {
        super(FIELD_NAME);
        this.labelManager = labelManager;
    }

    public V2SearchQueryWrapper build(SetExpressionData expressionData, Iterable<String> values) {
        this.validateSupportedOp((Enum)((SetExpressionData.Operator)expressionData.getOperator()), Collections.unmodifiableSet(Stream.of(SetExpressionData.Operator.IN, SetExpressionData.Operator.NOT_IN).collect(Collectors.toSet())));
        SpaceCategoryQuery query = new SpaceCategoryQuery(Collections.unmodifiableSet(StreamSupport.stream(values.spliterator(), false).map(SpaceTypeFieldHandler::categoryFromValue).collect(Collectors.toSet())), this.labelManager);
        return V2FieldHandlerHelper.wrapV2Search((SearchQuery)query, (ExpressionData)expressionData);
    }

    public V2SearchQueryWrapper build(EqualityExpressionData expressionData, String value) {
        this.validateSupportedOp((Enum)((EqualityExpressionData.Operator)expressionData.getOperator()), Sets.newHashSet((Object[])new EqualityExpressionData.Operator[]{EqualityExpressionData.Operator.EQUALS, EqualityExpressionData.Operator.NOT_EQUALS}));
        SpaceCategoryEnum category = SpaceTypeFieldHandler.categoryFromValue(value);
        return V2FieldHandlerHelper.wrapV2Search((SearchQuery)new SpaceCategoryQuery(category, this.labelManager), (ExpressionData)expressionData);
    }

    private static SpaceCategoryEnum categoryFromValue(String value) {
        SpaceType type = SpaceType.forName((String)value);
        if (type.equals((Object)SpaceType.GLOBAL)) {
            return SpaceCategoryEnum.GLOBAL;
        }
        if (type.equals((Object)SpaceType.PERSONAL)) {
            return SpaceCategoryEnum.PERSONAL;
        }
        String lowerCase = value.toLowerCase();
        if (lowerCase.equals("favourite") || lowerCase.equals("favorite")) {
            return SpaceCategoryEnum.FAVOURITES;
        }
        throw new BadRequestException("Unsupported space type : " + value);
    }
}


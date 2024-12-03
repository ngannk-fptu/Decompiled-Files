/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.cql.spi.v2searchhelpers.V2FieldHandlerHelper
 *  com.atlassian.confluence.plugins.cql.spi.v2searchhelpers.V2SearchQueryWrapper
 *  com.atlassian.confluence.search.v2.SearchQuery
 *  com.atlassian.confluence.search.v2.query.InSpaceQuery
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.spaces.SpaceManager
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

import com.atlassian.confluence.plugins.cql.spi.v2searchhelpers.V2FieldHandlerHelper;
import com.atlassian.confluence.plugins.cql.spi.v2searchhelpers.V2SearchQueryWrapper;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.query.InSpaceQuery;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.querylang.fields.BaseFieldHandler;
import com.atlassian.querylang.fields.EqualityFieldHandler;
import com.atlassian.querylang.fields.expressiondata.EqualityExpressionData;
import com.atlassian.querylang.fields.expressiondata.ExpressionData;
import com.atlassian.querylang.fields.expressiondata.SetExpressionData;
import com.google.common.collect.Sets;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class SpaceKeyFieldHandler
extends BaseFieldHandler
implements EqualityFieldHandler<String, V2SearchQueryWrapper> {
    private static final String FIELD_NAME = "space";
    private final SpaceManager spaceManager;

    public SpaceKeyFieldHandler(@ComponentImport SpaceManager spaceManager) {
        super(FIELD_NAME);
        this.spaceManager = spaceManager;
    }

    public V2SearchQueryWrapper build(SetExpressionData expressionData, Iterable<String> values) {
        this.validateSupportedOp((Enum)((SetExpressionData.Operator)expressionData.getOperator()), Sets.newHashSet((Object[])new SetExpressionData.Operator[]{SetExpressionData.Operator.IN, SetExpressionData.Operator.NOT_IN}));
        InSpaceQuery query = new InSpaceQuery(this.resolveKeys(values));
        return V2FieldHandlerHelper.wrapV2Search((SearchQuery)query, (ExpressionData)expressionData);
    }

    public V2SearchQueryWrapper build(EqualityExpressionData expressionData, String value) {
        this.validateSupportedOp((Enum)((EqualityExpressionData.Operator)expressionData.getOperator()), Sets.newHashSet((Object[])new EqualityExpressionData.Operator[]{EqualityExpressionData.Operator.EQUALS, EqualityExpressionData.Operator.NOT_EQUALS}));
        InSpaceQuery query = new InSpaceQuery(this.resolveKeys(Collections.singleton(value)));
        return V2FieldHandlerHelper.wrapV2Search((SearchQuery)query, (ExpressionData)expressionData);
    }

    private Set<String> resolveKeys(Iterable<String> values) {
        return Collections.unmodifiableSet(StreamSupport.stream(values.spliterator(), false).map(incomingSpaceKey -> {
            Space space = this.spaceManager.getSpace(incomingSpaceKey);
            return space != null ? space.getKey() : null;
        }).filter(Objects::nonNull).collect(Collectors.toSet()));
    }
}


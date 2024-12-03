/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.plugins.cql.spi.v2searchhelpers.V2SearchQueryWrapper
 *  com.atlassian.confluence.search.service.PredefinedSearchBuilder
 *  com.atlassian.confluence.search.v2.SearchManager
 *  com.atlassian.confluence.spaces.SpaceManager
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.querylang.fields.BaseFieldHandler
 *  com.atlassian.querylang.fields.EqualityFieldHandler
 *  com.atlassian.querylang.fields.TextFieldHandler
 *  com.atlassian.querylang.fields.expressiondata.EqualityExpressionData
 *  com.atlassian.querylang.fields.expressiondata.SetExpressionData
 *  com.atlassian.querylang.fields.expressiondata.TextExpressionData
 *  com.atlassian.querylang.query.FieldOrder
 *  com.atlassian.querylang.query.OrderDirection
 */
package com.atlassian.confluence.plugins.cql.fields;

import com.atlassian.confluence.plugins.cql.fields.SpaceKeyFieldHandler;
import com.atlassian.confluence.plugins.cql.fields.SpaceTitleFieldHandler;
import com.atlassian.confluence.plugins.cql.spi.v2searchhelpers.V2SearchQueryWrapper;
import com.atlassian.confluence.search.service.PredefinedSearchBuilder;
import com.atlassian.confluence.search.v2.SearchManager;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.querylang.fields.BaseFieldHandler;
import com.atlassian.querylang.fields.EqualityFieldHandler;
import com.atlassian.querylang.fields.TextFieldHandler;
import com.atlassian.querylang.fields.expressiondata.EqualityExpressionData;
import com.atlassian.querylang.fields.expressiondata.SetExpressionData;
import com.atlassian.querylang.fields.expressiondata.TextExpressionData;
import com.atlassian.querylang.query.FieldOrder;
import com.atlassian.querylang.query.OrderDirection;

public class SpaceFieldHandler
extends BaseFieldHandler
implements TextFieldHandler<V2SearchQueryWrapper>,
EqualityFieldHandler<String, V2SearchQueryWrapper> {
    private final SpaceKeyFieldHandler spaceKeyFieldHandler;
    private final SpaceTitleFieldHandler spaceNameFieldHandler;

    protected SpaceFieldHandler(@ComponentImport PredefinedSearchBuilder searchBuilder, @ComponentImport SearchManager searchManager, @ComponentImport SpaceManager spaceManager) {
        super("space", true);
        this.spaceKeyFieldHandler = new SpaceKeyFieldHandler(spaceManager);
        this.spaceNameFieldHandler = new SpaceTitleFieldHandler(searchBuilder, searchManager);
    }

    public FieldOrder buildOrder(OrderDirection direction) {
        return this.spaceKeyFieldHandler.buildOrder(direction);
    }

    public V2SearchQueryWrapper build(SetExpressionData expressionData, Iterable<String> values) {
        return this.spaceKeyFieldHandler.build(expressionData, values);
    }

    public V2SearchQueryWrapper build(EqualityExpressionData expressionData, String value) {
        return this.spaceKeyFieldHandler.build(expressionData, value);
    }

    public V2SearchQueryWrapper build(TextExpressionData expressionData, String value) {
        return this.spaceNameFieldHandler.build(expressionData, value);
    }
}


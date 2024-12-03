/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.querylang.lib.fields.MapFieldHandler$ValueType
 *  com.google.common.collect.ImmutableMap
 */
package com.atlassian.confluence.plugins.cql.fields.dynamic;

import com.atlassian.confluence.plugins.cql.fields.dynamic.DateValueEvaluator;
import com.atlassian.confluence.plugins.cql.fields.dynamic.NumberValueEvaluator;
import com.atlassian.confluence.plugins.cql.fields.dynamic.StringValueEvaluator;
import com.atlassian.confluence.plugins.cql.fields.dynamic.TextValueEvaluator;
import com.atlassian.confluence.plugins.cql.fields.dynamic.ValueExpressionEvaluator;
import com.atlassian.confluence.plugins.cql.impl.CQLIterableStringValueParseTreeVisitor;
import com.atlassian.confluence.plugins.cql.impl.CQLStringValueParseTreeVisitor;
import com.atlassian.querylang.lib.fields.MapFieldHandler;
import com.google.common.collect.ImmutableMap;
import java.util.Map;

public class MapFieldValueEvaluatorRegistry {
    private Map<MapFieldHandler.ValueType, ValueExpressionEvaluator> registry;

    public MapFieldValueEvaluatorRegistry(CQLStringValueParseTreeVisitor visitor, CQLIterableStringValueParseTreeVisitor iterableVisitor) {
        this.registry = ImmutableMap.of((Object)MapFieldHandler.ValueType.DATE, (Object)new DateValueEvaluator(visitor), (Object)MapFieldHandler.ValueType.STRING, (Object)new StringValueEvaluator(iterableVisitor), (Object)MapFieldHandler.ValueType.NUMBER, (Object)new NumberValueEvaluator(visitor), (Object)MapFieldHandler.ValueType.TEXT, (Object)new TextValueEvaluator(visitor));
    }

    public ValueExpressionEvaluator getEvaluator(MapFieldHandler.ValueType valueType) {
        return this.registry.get(valueType);
    }
}


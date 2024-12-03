/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.querylang.lib.fields.MapFieldHandler$ValueType
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.contentproperty.search.fields;

import com.atlassian.confluence.plugins.contentproperty.index.schema.SchemaFieldType;
import com.atlassian.querylang.lib.fields.MapFieldHandler;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SchemaFieldToContentPropertyValueMapping {
    private static final Logger log = LoggerFactory.getLogger(SchemaFieldToContentPropertyValueMapping.class);
    private final Map<SchemaFieldType, MapFieldHandler.ValueType> mapping = Maps.newEnumMap((Map)ImmutableMap.of((Object)((Object)SchemaFieldType.DATE), (Object)MapFieldHandler.ValueType.DATE, (Object)((Object)SchemaFieldType.STRING), (Object)MapFieldHandler.ValueType.STRING, (Object)((Object)SchemaFieldType.TEXT), (Object)MapFieldHandler.ValueType.TEXT, (Object)((Object)SchemaFieldType.NUMBER), (Object)MapFieldHandler.ValueType.NUMBER));

    public MapFieldHandler.ValueType getValueTypeFor(SchemaFieldType fieldType) {
        if (this.mapping.containsKey((Object)fieldType)) {
            return this.mapping.get((Object)fieldType);
        }
        log.warn("Could not find map field value type corresponding to given schema field type '{}'", (Object)fieldType);
        throw new IllegalStateException("Missing schema field type to content property type mapping.");
    }
}


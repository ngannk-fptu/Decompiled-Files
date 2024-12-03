/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.query.impl;

import com.hazelcast.core.HazelcastJsonValue;
import com.hazelcast.core.TypeConverter;
import com.hazelcast.internal.json.Json;
import com.hazelcast.internal.serialization.InternalSerializationService;
import com.hazelcast.nio.serialization.Data;
import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.query.Metadata;
import com.hazelcast.query.QueryConstants;
import com.hazelcast.query.QueryException;
import com.hazelcast.query.impl.AttributeType;
import com.hazelcast.query.impl.Extractable;
import com.hazelcast.query.impl.TypeConverters;
import com.hazelcast.query.impl.getters.Extractors;
import com.hazelcast.query.impl.getters.MultiResult;
import com.hazelcast.query.impl.getters.ReflectionHelper;
import java.util.Map;

public abstract class QueryableEntry<K, V>
implements Extractable,
Map.Entry<K, V> {
    protected InternalSerializationService serializationService;
    protected Extractors extractors;
    private Metadata metadata;

    public Metadata getMetadata() {
        return this.metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    @Override
    public Object getAttributeValue(String attributeName) throws QueryException {
        return this.extractAttributeValue(attributeName);
    }

    @Override
    public abstract V getValue();

    @Override
    public abstract K getKey();

    public abstract Data getKeyData();

    public abstract Data getValueData();

    protected abstract Object getTargetObject(boolean var1);

    TypeConverter getConverter(String attributeName) {
        Object attributeValue = this.getAttributeValue(attributeName);
        if (attributeValue == null) {
            return TypeConverters.NULL_CONVERTER;
        }
        if (attributeValue instanceof MultiResult) {
            MultiResult multiResult = (MultiResult)attributeValue;
            for (Object result : multiResult.getResults()) {
                if (result == null) continue;
                AttributeType attributeType = QueryableEntry.extractAttributeType(result);
                return attributeType == null ? TypeConverters.IDENTITY_CONVERTER : attributeType.getConverter();
            }
            return TypeConverters.NULL_CONVERTER;
        }
        AttributeType attributeType = QueryableEntry.extractAttributeType(attributeValue);
        return attributeType == null ? TypeConverters.IDENTITY_CONVERTER : attributeType.getConverter();
    }

    private Object extractAttributeValue(String attributeName) throws QueryException {
        Object result = this.extractAttributeValueIfAttributeQueryConstant(attributeName);
        if (result == null) {
            boolean isKey = QueryableEntry.startsWithKeyConstant(attributeName);
            attributeName = QueryableEntry.getAttributeName(isKey, attributeName);
            Object target = this.getTargetObject(isKey);
            Object metadata = QueryableEntry.getMetadataOrNull(this.metadata, isKey);
            result = QueryableEntry.extractAttributeValueFromTargetObject(this.extractors, attributeName, target, metadata);
        }
        if (result instanceof HazelcastJsonValue) {
            return Json.parse(result.toString());
        }
        return result;
    }

    private Object extractAttributeValueIfAttributeQueryConstant(String attributeName) {
        if (QueryConstants.KEY_ATTRIBUTE_NAME.value().equals(attributeName)) {
            return this.getKey();
        }
        if (QueryConstants.THIS_ATTRIBUTE_NAME.value().equals(attributeName)) {
            return this.getValue();
        }
        return null;
    }

    static Object extractAttributeValue(Extractors extractors, InternalSerializationService serializationService, String attributeName, Data key, Object value, Object metadata) throws QueryException {
        Object result = QueryableEntry.extractAttributeValueIfAttributeQueryConstant(serializationService, attributeName, key, value);
        if (result == null) {
            boolean isKey = QueryableEntry.startsWithKeyConstant(attributeName);
            attributeName = QueryableEntry.getAttributeName(isKey, attributeName);
            Object target = isKey ? key : value;
            result = QueryableEntry.extractAttributeValueFromTargetObject(extractors, attributeName, target, metadata);
        }
        return result;
    }

    private static Object extractAttributeValueIfAttributeQueryConstant(InternalSerializationService serializationService, String attributeName, Data key, Object value) {
        if (QueryConstants.KEY_ATTRIBUTE_NAME.value().equals(attributeName)) {
            return serializationService.toObject(key);
        }
        if (QueryConstants.THIS_ATTRIBUTE_NAME.value().equals(attributeName)) {
            return value instanceof Data ? serializationService.toObject(value) : value;
        }
        return null;
    }

    private static boolean startsWithKeyConstant(String attributeName) {
        return attributeName.startsWith(QueryConstants.KEY_ATTRIBUTE_NAME.value());
    }

    private static String getAttributeName(boolean isKey, String attributeName) {
        if (isKey) {
            return attributeName.substring(QueryConstants.KEY_ATTRIBUTE_NAME.value().length() + 1);
        }
        return attributeName;
    }

    private static Object extractAttributeValueFromTargetObject(Extractors extractors, String attributeName, Object target, Object metadata) {
        return extractors.extract(target, attributeName, metadata);
    }

    public static AttributeType extractAttributeType(Object attributeValue) {
        if (attributeValue instanceof Portable) {
            return AttributeType.PORTABLE;
        }
        return ReflectionHelper.getAttributeType(attributeValue.getClass());
    }

    private static Object getMetadataOrNull(Metadata metadata, boolean isKey) {
        if (metadata == null) {
            return null;
        }
        return isKey ? metadata.getKeyMetadata() : metadata.getValueMetadata();
    }
}


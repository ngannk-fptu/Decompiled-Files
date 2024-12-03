/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Function
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  org.codehaus.jackson.JsonNode
 *  org.codehaus.jackson.node.ArrayNode
 *  org.codehaus.jackson.node.ObjectNode
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.mywork.host.soy;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.template.soy.data.SanitizedContent;
import com.google.template.soy.data.SoyData;
import com.google.template.soy.data.SoyListData;
import com.google.template.soy.data.SoyMapData;
import com.google.template.soy.data.restricted.BooleanData;
import com.google.template.soy.data.restricted.FloatData;
import com.google.template.soy.data.restricted.IntegerData;
import com.google.template.soy.data.restricted.NullData;
import com.google.template.soy.data.restricted.StringData;
import java.io.Serializable;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SoyUtils {
    private static final Logger log = LoggerFactory.getLogger(SoyUtils.class);
    private static final Function<SoyData, Serializable> SOY_DATA_TO_SERIALIZABLE_FUNCTION = new Function<SoyData, Serializable>(){

        public Serializable apply(SoyData fromSoyData) {
            Object convertedSoyData = SoyUtils.convertFromSoyData(fromSoyData);
            if (convertedSoyData instanceof Serializable) {
                return (Serializable)convertedSoyData;
            }
            if (convertedSoyData == null) {
                return NullData.INSTANCE.toString();
            }
            if (log.isWarnEnabled()) {
                log.warn("Conversion of {} from {} is not a Serializable, defaulting to toString() invocation.", (Object)convertedSoyData.getClass().getName(), (Object)fromSoyData.getClass().getName());
            }
            return convertedSoyData.toString();
        }
    };

    private SoyUtils() {
    }

    public static SoyData toSoyData(JsonNode value) {
        if (value.isObject()) {
            return SoyUtils.toSoyData((ObjectNode)value);
        }
        if (value.isArray()) {
            return SoyUtils.toSoyData((ArrayNode)value);
        }
        if (value.isNull()) {
            return null;
        }
        if (value.isBoolean()) {
            return BooleanData.forValue(value.getBooleanValue());
        }
        if (value.isFloatingPointNumber()) {
            return FloatData.forValue(value.getDoubleValue());
        }
        if (value.isIntegralNumber()) {
            BigInteger bigIntegerValue = value.getBigIntegerValue();
            if (bigIntegerValue.compareTo(BigInteger.valueOf(Integer.MAX_VALUE)) <= 0 && bigIntegerValue.compareTo(BigInteger.valueOf(Integer.MIN_VALUE)) >= 0) {
                return IntegerData.forValue(value.getIntValue());
            }
            double doubleValue = value.getDoubleValue();
            if (doubleValue < Double.POSITIVE_INFINITY && doubleValue > Double.NEGATIVE_INFINITY) {
                return FloatData.forValue(doubleValue);
            }
            return StringData.forValue(value.getValueAsText());
        }
        return StringData.forValue(value.getValueAsText());
    }

    private static SoyMapData toSoyData(ObjectNode value) {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        Iterator fields = value.getFields();
        while (fields.hasNext()) {
            Map.Entry field = (Map.Entry)fields.next();
            builder.put((Object)((String)field.getKey()), (Object)SoyUtils.toSoyData((JsonNode)field.getValue()));
        }
        return new SoyMapData((Map<String, ?>)builder.build());
    }

    private static SoyListData toSoyData(ArrayNode value) {
        return new SoyListData(Iterables.transform((Iterable)value, (Function)new Function<JsonNode, SoyData>(){

            public SoyData apply(JsonNode from) {
                return SoyUtils.toSoyData(from);
            }
        }));
    }

    public static Serializable[] toSerializableArray(List<SoyData> params) {
        return (Serializable[])Iterables.toArray((Iterable)Iterables.transform(params, SOY_DATA_TO_SERIALIZABLE_FUNCTION), Serializable.class);
    }

    private static Object convertFromSoyData(SoyData data) {
        if (data instanceof SoyMapData) {
            return Maps.transformValues(((SoyMapData)data).asMap(), (Function)new Function<SoyData, Object>(){

                public Object apply(SoyData from) {
                    return SoyUtils.convertFromSoyData(from);
                }
            });
        }
        if (data instanceof SoyListData) {
            return Lists.transform(((SoyListData)data).asList(), (Function)new Function<SoyData, Object>(){

                public Object apply(SoyData from) {
                    return SoyUtils.convertFromSoyData(from);
                }
            });
        }
        if (data instanceof StringData) {
            return data.stringValue();
        }
        if (data instanceof IntegerData) {
            return ((IntegerData)data).getValue();
        }
        if (data instanceof BooleanData) {
            return ((BooleanData)data).getValue();
        }
        if (data instanceof FloatData) {
            return ((FloatData)data).getValue();
        }
        if (data instanceof SanitizedContent) {
            return ((SanitizedContent)data).getContent();
        }
        return data == NullData.INSTANCE ? null : data.stringValue();
    }
}


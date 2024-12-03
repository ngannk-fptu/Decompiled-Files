/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonFormat$Shape
 *  com.fasterxml.jackson.annotation.JsonFormat$Value
 *  com.fasterxml.jackson.core.JsonParser$NumberType
 *  com.fasterxml.jackson.databind.BeanProperty
 *  com.fasterxml.jackson.databind.JavaType
 *  com.fasterxml.jackson.databind.JsonMappingException
 *  com.fasterxml.jackson.databind.JsonNode
 *  com.fasterxml.jackson.databind.JsonSerializer
 *  com.fasterxml.jackson.databind.SerializationFeature
 *  com.fasterxml.jackson.databind.SerializerProvider
 *  com.fasterxml.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor
 *  com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes
 *  com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper
 *  com.fasterxml.jackson.databind.jsonFormatVisitors.JsonIntegerFormatVisitor
 *  com.fasterxml.jackson.databind.jsonFormatVisitors.JsonStringFormatVisitor
 *  com.fasterxml.jackson.databind.jsonFormatVisitors.JsonValueFormat
 *  com.fasterxml.jackson.databind.ser.ContextualSerializer
 */
package com.fasterxml.jackson.datatype.joda.ser;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonArrayFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatTypes;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonIntegerFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonStringFormatVisitor;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonValueFormat;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.datatype.joda.cfg.JacksonJodaDateFormat;
import com.fasterxml.jackson.datatype.joda.ser.JodaSerializerBase;
import java.lang.reflect.Type;

public abstract class JodaDateSerializerBase<T>
extends JodaSerializerBase<T>
implements ContextualSerializer {
    private static final long serialVersionUID = 1L;
    protected static final int FORMAT_STRING = 1;
    protected static final int FORMAT_TIMESTAMP = 2;
    protected static final int FORMAT_ARRAY = 3;
    protected final JacksonJodaDateFormat _format;
    protected final SerializationFeature _featureForNumeric;
    protected final int _defaultNumericShape;
    protected final int _shapeOverride;

    protected JodaDateSerializerBase(Class<T> type, JacksonJodaDateFormat format, SerializationFeature numericFeature, int defaultNumericShape, int shapeOverride) {
        super(type);
        this._format = format;
        this._featureForNumeric = numericFeature;
        this._defaultNumericShape = defaultNumericShape;
        this._shapeOverride = shapeOverride;
    }

    public abstract JodaDateSerializerBase<T> withFormat(JacksonJodaDateFormat var1, int var2);

    public boolean isEmpty(SerializerProvider prov, T value) {
        return value == null;
    }

    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        JsonFormat.Value ann = this.findFormatOverrides(prov, property, this.handledType());
        if (ann != null) {
            int shapeOverride;
            Boolean useTimestamp;
            JsonFormat.Shape shape = ann.getShape();
            if (shape.isNumeric()) {
                useTimestamp = Boolean.TRUE;
                shapeOverride = 2;
            } else if (shape == JsonFormat.Shape.STRING) {
                useTimestamp = Boolean.FALSE;
                shapeOverride = 1;
            } else if (shape == JsonFormat.Shape.ARRAY) {
                useTimestamp = Boolean.TRUE;
                shapeOverride = 3;
            } else {
                useTimestamp = null;
                shapeOverride = this._shapeOverride;
            }
            JacksonJodaDateFormat format = this._format;
            if (useTimestamp != null) {
                format = format.withUseTimestamp(useTimestamp);
            }
            if ((format = format.with(ann)) != this._format || shapeOverride != this._shapeOverride) {
                return this.withFormat(format, shapeOverride);
            }
        }
        return this;
    }

    @Deprecated
    public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
        switch (this._serializationShape(provider)) {
            case 2: {
                return this.createSchemaNode("array", true);
            }
            case 3: {
                return this.createSchemaNode("number", true);
            }
        }
        return this.createSchemaNode("string", true);
    }

    public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
        switch (this._serializationShape(visitor.getProvider())) {
            case 2: {
                JsonIntegerFormatVisitor v2 = visitor.expectIntegerFormat(typeHint);
                if (v2 == null) break;
                v2.numberType(JsonParser.NumberType.LONG);
                v2.format(JsonValueFormat.UTC_MILLISEC);
                break;
            }
            case 3: {
                JsonArrayFormatVisitor v2 = visitor.expectArrayFormat(typeHint);
                if (v2 == null) break;
                v2.itemsFormat(JsonFormatTypes.INTEGER);
                break;
            }
            default: {
                JsonStringFormatVisitor v2 = visitor.expectStringFormat(typeHint);
                if (v2 == null) break;
                v2.format(JsonValueFormat.DATE_TIME);
            }
        }
    }

    protected boolean writeWithZoneId(SerializerProvider provider) {
        return this._format.shouldWriteWithZoneId(provider);
    }

    protected int _serializationShape(SerializerProvider provider) {
        int shape = this._shapeOverride;
        if (shape == 0) {
            shape = this._format.useTimestamp(provider, this._featureForNumeric) ? this._defaultNumericShape : 1;
        }
        return shape;
    }
}


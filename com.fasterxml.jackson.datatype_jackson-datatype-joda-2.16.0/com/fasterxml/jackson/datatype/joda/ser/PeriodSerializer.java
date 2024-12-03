/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonFormat$Shape
 *  com.fasterxml.jackson.annotation.JsonFormat$Value
 *  com.fasterxml.jackson.core.JsonGenerator
 *  com.fasterxml.jackson.databind.BeanProperty
 *  com.fasterxml.jackson.databind.JavaType
 *  com.fasterxml.jackson.databind.JsonMappingException
 *  com.fasterxml.jackson.databind.JsonNode
 *  com.fasterxml.jackson.databind.JsonSerializer
 *  com.fasterxml.jackson.databind.SerializerProvider
 *  com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper
 *  com.fasterxml.jackson.databind.jsonFormatVisitors.JsonStringFormatVisitor
 *  com.fasterxml.jackson.databind.ser.ContextualSerializer
 *  org.joda.time.ReadablePeriod
 */
package com.fasterxml.jackson.datatype.joda.ser;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonFormatVisitorWrapper;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonStringFormatVisitor;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.fasterxml.jackson.datatype.joda.cfg.FormatConfig;
import com.fasterxml.jackson.datatype.joda.cfg.JacksonJodaPeriodFormat;
import com.fasterxml.jackson.datatype.joda.ser.JodaSerializerBase;
import java.io.IOException;
import java.lang.reflect.Type;
import org.joda.time.ReadablePeriod;

public class PeriodSerializer
extends JodaSerializerBase<ReadablePeriod>
implements ContextualSerializer {
    private static final long serialVersionUID = 1L;
    protected final JacksonJodaPeriodFormat _format;

    public PeriodSerializer() {
        this(FormatConfig.DEFAULT_PERIOD_FORMAT);
    }

    protected PeriodSerializer(JacksonJodaPeriodFormat format) {
        super(ReadablePeriod.class);
        this._format = format;
    }

    public JsonSerializer<?> createContextual(SerializerProvider prov, BeanProperty property) throws JsonMappingException {
        JsonFormat.Value ann;
        if (property != null && (ann = this.findFormatOverrides(prov, property, this.handledType())) != null) {
            JacksonJodaPeriodFormat format = this._format;
            Boolean useTimestamp = ann.getShape().isNumeric() ? Boolean.TRUE : (ann.getShape() == JsonFormat.Shape.STRING ? Boolean.FALSE : (ann.getShape() == JsonFormat.Shape.ARRAY ? Boolean.TRUE : null));
            if (useTimestamp != null) {
                format = format.withUseTimestamp(useTimestamp);
            }
            format = format.withFormat(ann.getPattern());
            if ((format = format.withLocale(ann.getLocale())) != this._format) {
                return new PeriodSerializer(format);
            }
        }
        return this;
    }

    public void serialize(ReadablePeriod value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(this._format.createFormatter(provider).print(value));
    }

    @Deprecated
    public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
        return this.createSchemaNode("string", true);
    }

    public void acceptJsonFormatVisitor(JsonFormatVisitorWrapper visitor, JavaType typeHint) throws JsonMappingException {
        JsonStringFormatVisitor v2 = visitor.expectStringFormat(typeHint);
        if (v2 != null) {
            // empty if block
        }
    }
}


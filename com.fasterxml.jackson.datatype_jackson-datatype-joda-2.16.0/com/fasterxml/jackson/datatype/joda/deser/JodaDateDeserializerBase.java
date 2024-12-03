/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonFormat$Shape
 *  com.fasterxml.jackson.annotation.JsonFormat$Value
 *  com.fasterxml.jackson.databind.BeanProperty
 *  com.fasterxml.jackson.databind.DeserializationContext
 *  com.fasterxml.jackson.databind.JsonDeserializer
 *  com.fasterxml.jackson.databind.JsonMappingException
 *  com.fasterxml.jackson.databind.deser.ContextualDeserializer
 */
package com.fasterxml.jackson.datatype.joda.deser;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.deser.ContextualDeserializer;
import com.fasterxml.jackson.datatype.joda.cfg.JacksonJodaDateFormat;
import com.fasterxml.jackson.datatype.joda.deser.JodaDeserializerBase;

public abstract class JodaDateDeserializerBase<T>
extends JodaDeserializerBase<T>
implements ContextualDeserializer {
    private static final long serialVersionUID = 1L;
    protected final JacksonJodaDateFormat _format;

    protected JodaDateDeserializerBase(Class<?> type, JacksonJodaDateFormat format) {
        super(type);
        this._format = format;
    }

    public abstract JodaDateDeserializerBase<?> withFormat(JacksonJodaDateFormat var1);

    public JsonDeserializer<?> createContextual(DeserializationContext ctxt, BeanProperty prop) throws JsonMappingException {
        JsonFormat.Value ann = this.findFormatOverrides(ctxt, prop, this.handledType());
        if (ann != null) {
            JacksonJodaDateFormat format = this._format;
            Boolean useTimestamp = ann.getShape().isNumeric() ? Boolean.TRUE : (ann.getShape() == JsonFormat.Shape.STRING ? Boolean.FALSE : (ann.getShape() == JsonFormat.Shape.ARRAY ? Boolean.TRUE : null));
            if (useTimestamp != null) {
                format = format.withUseTimestamp(useTimestamp);
            }
            if ((format = format.with(ann)) != this._format) {
                return this.withFormat(format);
            }
        }
        return this;
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package com.fasterxml.jackson.databind.ser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.jsonFormatVisitors.JsonObjectFormatVisitor;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.ser.PropertyWriter;

public interface PropertyFilter {
    public void serializeAsField(Object var1, JsonGenerator var2, SerializerProvider var3, PropertyWriter var4) throws Exception;

    public void serializeAsElement(Object var1, JsonGenerator var2, SerializerProvider var3, PropertyWriter var4) throws Exception;

    @Deprecated
    public void depositSchemaProperty(PropertyWriter var1, ObjectNode var2, SerializerProvider var3) throws JsonMappingException;

    public void depositSchemaProperty(PropertyWriter var1, JsonObjectFormatVisitor var2, SerializerProvider var3) throws JsonMappingException;
}


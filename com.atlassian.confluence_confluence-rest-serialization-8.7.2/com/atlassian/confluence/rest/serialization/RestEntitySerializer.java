/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.reference.Reference
 *  com.atlassian.confluence.rest.api.model.RestEntity
 *  com.google.common.base.Supplier
 *  com.google.common.base.Suppliers
 *  org.codehaus.jackson.JsonGenerator
 *  org.codehaus.jackson.map.BeanDescription
 *  org.codehaus.jackson.map.BeanProperty
 *  org.codehaus.jackson.map.BeanProperty$Std
 *  org.codehaus.jackson.map.BeanPropertyDefinition
 *  org.codehaus.jackson.map.JsonSerializer
 *  org.codehaus.jackson.map.SerializerProvider
 *  org.codehaus.jackson.map.introspect.AnnotatedField
 *  org.codehaus.jackson.map.introspect.AnnotatedMember
 *  org.codehaus.jackson.map.introspect.AnnotationMap
 *  org.codehaus.jackson.map.ser.std.SerializerBase
 *  org.codehaus.jackson.map.util.Annotations
 *  org.codehaus.jackson.type.JavaType
 */
package com.atlassian.confluence.rest.serialization;

import com.atlassian.confluence.api.model.reference.Reference;
import com.atlassian.confluence.rest.api.model.RestEntity;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.BeanDescription;
import org.codehaus.jackson.map.BeanProperty;
import org.codehaus.jackson.map.BeanPropertyDefinition;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.introspect.AnnotatedField;
import org.codehaus.jackson.map.introspect.AnnotatedMember;
import org.codehaus.jackson.map.introspect.AnnotationMap;
import org.codehaus.jackson.map.ser.std.SerializerBase;
import org.codehaus.jackson.map.util.Annotations;
import org.codehaus.jackson.type.JavaType;

public class RestEntitySerializer
extends SerializerBase<RestEntity> {
    protected RestEntitySerializer() {
        super(RestEntity.class, false);
    }

    public void serialize(RestEntity entity, JsonGenerator jsonGen, SerializerProvider serializerProvider) throws IOException {
        Supplier properties = Suppliers.memoize(() -> this.getDelegateBeanPropertyDefinitions(entity, serializerProvider));
        jsonGen.writeStartObject();
        for (Map.Entry prop : entity.properties().entrySet()) {
            Object value = prop.getValue();
            if (value instanceof Reference && !((Reference)value).isExpanded()) continue;
            if (value instanceof Collection) {
                if (this.hasDelegateBeanProperty((String)prop.getKey(), (List)properties.get())) {
                    this.writeDelegateField(jsonGen, serializerProvider, (String)prop.getKey(), value, (List)properties.get());
                    continue;
                }
                jsonGen.writeObjectField((String)prop.getKey(), value);
                continue;
            }
            jsonGen.writeObjectField((String)prop.getKey(), value);
        }
        jsonGen.writeEndObject();
        jsonGen.flush();
    }

    private void writeDelegateField(JsonGenerator jsonGen, SerializerProvider serializerProvider, String fieldName, Object value, List<BeanPropertyDefinition> properties) throws IOException {
        BeanProperty property = this.getDelegateBeanProperty(serializerProvider, fieldName, properties);
        jsonGen.writeFieldName(fieldName);
        JsonSerializer serializer = serializerProvider.findTypedValueSerializer(property.getType(), true, property);
        serializer.serialize(value, jsonGen, serializerProvider);
    }

    private List<BeanPropertyDefinition> getDelegateBeanPropertyDefinitions(RestEntity entity, SerializerProvider serializerProvider) {
        JavaType javaType = serializerProvider.constructType(entity.getDelegate().getClass());
        BeanDescription beanDescription = serializerProvider.getConfig().introspect(javaType);
        return beanDescription.findProperties();
    }

    private boolean hasDelegateBeanProperty(String fieldName, List<BeanPropertyDefinition> properties) {
        for (BeanPropertyDefinition property : properties) {
            AnnotatedField field = property.getField();
            if (!property.hasField() || !fieldName.equals(field.getName())) continue;
            return true;
        }
        return false;
    }

    private BeanProperty getDelegateBeanProperty(SerializerProvider serializerProvider, String fieldName, List<BeanPropertyDefinition> properties) {
        for (BeanPropertyDefinition property : properties) {
            AnnotatedField field = property.getField();
            if (!property.hasField() || !fieldName.equals(field.getName())) continue;
            JavaType fieldJavaType = serializerProvider.constructType(field.getGenericType());
            AnnotationMap contextAnnotations = new AnnotationMap();
            return new BeanProperty.Std(fieldName, fieldJavaType, (Annotations)contextAnnotations, (AnnotatedMember)field);
        }
        return null;
    }
}


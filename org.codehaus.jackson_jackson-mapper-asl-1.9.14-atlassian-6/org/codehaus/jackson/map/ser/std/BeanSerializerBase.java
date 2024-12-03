/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.JsonGenerationException
 *  org.codehaus.jackson.JsonGenerator
 *  org.codehaus.jackson.JsonNode
 *  org.codehaus.jackson.type.JavaType
 */
package org.codehaus.jackson.map.ser.std;

import java.io.IOException;
import java.lang.reflect.Type;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.BeanProperty;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.ResolvableSerializer;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.TypeSerializer;
import org.codehaus.jackson.map.ser.AnyGetterWriter;
import org.codehaus.jackson.map.ser.BeanPropertyFilter;
import org.codehaus.jackson.map.ser.BeanPropertyWriter;
import org.codehaus.jackson.map.ser.FilterProvider;
import org.codehaus.jackson.map.ser.std.ContainerSerializerBase;
import org.codehaus.jackson.map.ser.std.SerializerBase;
import org.codehaus.jackson.node.ObjectNode;
import org.codehaus.jackson.schema.JsonSchema;
import org.codehaus.jackson.schema.SchemaAware;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class BeanSerializerBase
extends SerializerBase<Object>
implements ResolvableSerializer,
SchemaAware {
    protected static final BeanPropertyWriter[] NO_PROPS = new BeanPropertyWriter[0];
    protected final BeanPropertyWriter[] _props;
    protected final BeanPropertyWriter[] _filteredProps;
    protected final AnyGetterWriter _anyGetterWriter;
    protected final Object _propertyFilterId;

    protected BeanSerializerBase(JavaType type, BeanPropertyWriter[] properties, BeanPropertyWriter[] filteredProperties, AnyGetterWriter anyGetterWriter, Object filterId) {
        super(type);
        this._props = properties;
        this._filteredProps = filteredProperties;
        this._anyGetterWriter = anyGetterWriter;
        this._propertyFilterId = filterId;
    }

    public BeanSerializerBase(Class<?> rawType, BeanPropertyWriter[] properties, BeanPropertyWriter[] filteredProperties, AnyGetterWriter anyGetterWriter, Object filterId) {
        super(rawType);
        this._props = properties;
        this._filteredProps = filteredProperties;
        this._anyGetterWriter = anyGetterWriter;
        this._propertyFilterId = filterId;
    }

    protected BeanSerializerBase(BeanSerializerBase src) {
        this(src._handledType, src._props, src._filteredProps, src._anyGetterWriter, src._propertyFilterId);
    }

    @Override
    public abstract void serialize(Object var1, JsonGenerator var2, SerializerProvider var3) throws IOException, JsonGenerationException;

    @Override
    public void serializeWithType(Object bean, JsonGenerator jgen, SerializerProvider provider, TypeSerializer typeSer) throws IOException, JsonGenerationException {
        typeSer.writeTypePrefixForObject(bean, jgen);
        if (this._propertyFilterId != null) {
            this.serializeFieldsFiltered(bean, jgen, provider);
        } else {
            this.serializeFields(bean, jgen, provider);
        }
        typeSer.writeTypeSuffixForObject(bean, jgen);
    }

    protected void serializeFields(Object bean, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
        int i;
        BeanPropertyWriter[] props = this._filteredProps != null && provider.getSerializationView() != null ? this._filteredProps : this._props;
        try {
            for (BeanPropertyWriter prop : props) {
                if (prop == null) continue;
                prop.serializeAsField(bean, jgen, provider);
            }
            if (this._anyGetterWriter != null) {
                this._anyGetterWriter.getAndSerialize(bean, jgen, provider);
            }
        }
        catch (Exception e) {
            String name = i == props.length ? "[anySetter]" : props[i].getName();
            this.wrapAndThrow(provider, (Throwable)e, bean, name);
        }
        catch (StackOverflowError e) {
            JsonMappingException mapE = new JsonMappingException("Infinite recursion (StackOverflowError)", e);
            String name = i == props.length ? "[anySetter]" : props[i].getName();
            mapE.prependPath(new JsonMappingException.Reference(bean, name));
            throw mapE;
        }
    }

    protected void serializeFieldsFiltered(Object bean, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonGenerationException {
        int i;
        BeanPropertyWriter[] props = this._filteredProps != null && provider.getSerializationView() != null ? this._filteredProps : this._props;
        BeanPropertyFilter filter = this.findFilter(provider);
        if (filter == null) {
            this.serializeFields(bean, jgen, provider);
            return;
        }
        try {
            for (BeanPropertyWriter prop : props) {
                if (prop == null) continue;
                filter.serializeAsField(bean, jgen, provider, prop);
            }
            if (this._anyGetterWriter != null) {
                this._anyGetterWriter.getAndSerialize(bean, jgen, provider);
            }
        }
        catch (Exception e) {
            String name = i == props.length ? "[anySetter]" : props[i].getName();
            this.wrapAndThrow(provider, (Throwable)e, bean, name);
        }
        catch (StackOverflowError e) {
            JsonMappingException mapE = new JsonMappingException("Infinite recursion (StackOverflowError)", e);
            String name = i == props.length ? "[anySetter]" : props[i].getName();
            mapE.prependPath(new JsonMappingException.Reference(bean, name));
            throw mapE;
        }
    }

    protected BeanPropertyFilter findFilter(SerializerProvider provider) throws JsonMappingException {
        Object filterId = this._propertyFilterId;
        FilterProvider filters = provider.getFilterProvider();
        if (filters == null) {
            throw new JsonMappingException("Can not resolve BeanPropertyFilter with id '" + filterId + "'; no FilterProvider configured");
        }
        BeanPropertyFilter filter = filters.findFilter(filterId);
        return filter;
    }

    @Override
    public JsonNode getSchema(SerializerProvider provider, Type typeHint) throws JsonMappingException {
        ObjectNode o = this.createSchemaNode("object", true);
        ObjectNode propertiesNode = o.objectNode();
        for (int i = 0; i < this._props.length; ++i) {
            BeanPropertyWriter prop = this._props[i];
            JavaType propType = prop.getSerializationType();
            Type hint = propType == null ? prop.getGenericPropertyType() : propType.getRawClass();
            JsonSerializer<Object> ser = prop.getSerializer();
            if (ser == null) {
                Class<?> serType = prop.getRawSerializationType();
                if (serType == null) {
                    serType = prop.getPropertyType();
                }
                ser = provider.findValueSerializer(serType, (BeanProperty)prop);
            }
            JsonNode schemaNode = ser instanceof SchemaAware ? ((SchemaAware)((Object)ser)).getSchema(provider, hint) : JsonSchema.getDefaultSchemaNode();
            propertiesNode.put(prop.getName(), schemaNode);
        }
        o.put("properties", propertiesNode);
        return o;
    }

    @Override
    public void resolve(SerializerProvider provider) throws JsonMappingException {
        int filteredCount = this._filteredProps == null ? 0 : this._filteredProps.length;
        int len = this._props.length;
        for (int i = 0; i < len; ++i) {
            BeanPropertyWriter w2;
            TypeSerializer typeSer;
            BeanPropertyWriter prop = this._props[i];
            if (prop.hasSerializer()) continue;
            JavaType type = prop.getSerializationType();
            if (type == null && !(type = provider.constructType(prop.getGenericPropertyType())).isFinal()) {
                if (!type.isContainerType() && type.containedTypeCount() <= 0) continue;
                prop.setNonTrivialBaseType(type);
                continue;
            }
            JsonSerializer<Object> ser = provider.findValueSerializer(type, (BeanProperty)prop);
            if (type.isContainerType() && (typeSer = (TypeSerializer)type.getContentType().getTypeHandler()) != null && ser instanceof ContainerSerializerBase) {
                ContainerSerializerBase<?> ser2 = ((ContainerSerializerBase)ser).withValueTypeSerializer(typeSer);
                ser = ser2;
            }
            this._props[i] = prop = prop.withSerializer(ser);
            if (i >= filteredCount || (w2 = this._filteredProps[i]) == null) continue;
            this._filteredProps[i] = w2.withSerializer(ser);
        }
        if (this._anyGetterWriter != null) {
            this._anyGetterWriter.resolve(provider);
        }
    }
}


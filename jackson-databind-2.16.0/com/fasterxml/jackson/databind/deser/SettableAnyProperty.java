/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.core.JsonParser
 *  com.fasterxml.jackson.core.JsonToken
 */
package com.fasterxml.jackson.databind.deser;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.deser.UnresolvedForwardReference;
import com.fasterxml.jackson.databind.deser.ValueInstantiator;
import com.fasterxml.jackson.databind.deser.impl.JDKValueInstantiators;
import com.fasterxml.jackson.databind.deser.impl.ReadableObjectId;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.jsontype.TypeDeserializer;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.util.ClassUtil;
import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

public abstract class SettableAnyProperty
implements Serializable {
    private static final long serialVersionUID = 1L;
    protected final BeanProperty _property;
    protected final AnnotatedMember _setter;
    protected final boolean _setterIsField;
    protected final JavaType _type;
    protected JsonDeserializer<Object> _valueDeserializer;
    protected final TypeDeserializer _valueTypeDeserializer;
    protected final KeyDeserializer _keyDeserializer;

    public SettableAnyProperty(BeanProperty property, AnnotatedMember setter, JavaType type, KeyDeserializer keyDeser, JsonDeserializer<Object> valueDeser, TypeDeserializer typeDeser) {
        this._property = property;
        this._setter = setter;
        this._type = type;
        this._valueDeserializer = valueDeser;
        this._valueTypeDeserializer = typeDeser;
        this._keyDeserializer = keyDeser;
        this._setterIsField = setter instanceof AnnotatedField;
    }

    public static SettableAnyProperty constructForMethod(DeserializationContext ctxt, BeanProperty property, AnnotatedMember field, JavaType valueType, KeyDeserializer keyDeser, JsonDeserializer<Object> valueDeser, TypeDeserializer typeDeser) {
        return new MethodAnyProperty(property, field, valueType, keyDeser, valueDeser, typeDeser);
    }

    public static SettableAnyProperty constructForMapField(DeserializationContext ctxt, BeanProperty property, AnnotatedMember field, JavaType valueType, KeyDeserializer keyDeser, JsonDeserializer<Object> valueDeser, TypeDeserializer typeDeser) {
        Class<Object> mapType = field.getRawType();
        if (mapType == Map.class) {
            mapType = LinkedHashMap.class;
        }
        ValueInstantiator vi = JDKValueInstantiators.findStdValueInstantiator(ctxt.getConfig(), mapType);
        return new MapFieldAnyProperty(property, field, valueType, keyDeser, valueDeser, typeDeser, vi);
    }

    public static SettableAnyProperty constructForJsonNodeField(DeserializationContext ctxt, BeanProperty property, AnnotatedMember field, JavaType valueType, JsonDeserializer<Object> valueDeser) {
        return new JsonNodeFieldAnyProperty(property, field, valueType, valueDeser, ctxt.getNodeFactory());
    }

    public abstract SettableAnyProperty withValueDeserializer(JsonDeserializer<Object> var1);

    public void fixAccess(DeserializationConfig config) {
        this._setter.fixAccess(config.isEnabled(MapperFeature.OVERRIDE_PUBLIC_ACCESS_MODIFIERS));
    }

    Object readResolve() {
        if (this._setter == null || this._setter.getAnnotated() == null) {
            throw new IllegalArgumentException("Missing method/field (broken JDK (de)serialization?)");
        }
        return this;
    }

    public BeanProperty getProperty() {
        return this._property;
    }

    public boolean hasValueDeserializer() {
        return this._valueDeserializer != null;
    }

    public JavaType getType() {
        return this._type;
    }

    public String getPropertyName() {
        return this._property.getName();
    }

    public void deserializeAndSet(JsonParser p, DeserializationContext ctxt, Object instance, String propName) throws IOException {
        try {
            String key = this._keyDeserializer == null ? propName : this._keyDeserializer.deserializeKey(propName, ctxt);
            this.set(instance, key, this.deserialize(p, ctxt));
        }
        catch (UnresolvedForwardReference reference) {
            if (this._valueDeserializer.getObjectIdReader() == null) {
                throw JsonMappingException.from(p, "Unresolved forward reference but no identity info.", (Throwable)((Object)reference));
            }
            AnySetterReferring referring = new AnySetterReferring(this, reference, this._type.getRawClass(), instance, propName);
            reference.getRoid().appendReferring(referring);
        }
    }

    public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.hasToken(JsonToken.VALUE_NULL)) {
            return this._valueDeserializer.getNullValue(ctxt);
        }
        if (this._valueTypeDeserializer != null) {
            return this._valueDeserializer.deserializeWithType(p, ctxt, this._valueTypeDeserializer);
        }
        return this._valueDeserializer.deserialize(p, ctxt);
    }

    public void set(Object instance, Object propName, Object value) throws IOException {
        try {
            this._set(instance, propName, value);
        }
        catch (IOException e) {
            throw e;
        }
        catch (Exception e) {
            this._throwAsIOE(e, propName, value);
        }
    }

    protected abstract void _set(Object var1, Object var2, Object var3) throws Exception;

    protected void _throwAsIOE(Exception e, Object propName, Object value) throws IOException {
        if (e instanceof IllegalArgumentException) {
            String actType = ClassUtil.classNameOf(value);
            StringBuilder msg = new StringBuilder("Problem deserializing \"any-property\" '").append(propName);
            msg.append("' of class " + this.getClassName() + " (expected type: ").append(this._type);
            msg.append("; actual type: ").append(actType).append(")");
            String origMsg = ClassUtil.exceptionMessage(e);
            if (origMsg != null) {
                msg.append(", problem: ").append(origMsg);
            } else {
                msg.append(" (no error message provided)");
            }
            throw new JsonMappingException(null, msg.toString(), (Throwable)e);
        }
        ClassUtil.throwIfIOE(e);
        ClassUtil.throwIfRTE(e);
        Throwable t = ClassUtil.getRootCause(e);
        throw new JsonMappingException(null, ClassUtil.exceptionMessage(t), t);
    }

    private String getClassName() {
        return ClassUtil.nameOf(this._setter.getDeclaringClass());
    }

    public String toString() {
        return "[any property on class " + this.getClassName() + "]";
    }

    protected static class JsonNodeFieldAnyProperty
    extends SettableAnyProperty
    implements Serializable {
        private static final long serialVersionUID = 1L;
        protected final JsonNodeFactory _nodeFactory;

        public JsonNodeFieldAnyProperty(BeanProperty property, AnnotatedMember field, JavaType valueType, JsonDeserializer<Object> valueDeser, JsonNodeFactory nodeFactory) {
            super(property, field, valueType, null, valueDeser, null);
            this._nodeFactory = nodeFactory;
        }

        @Override
        public void deserializeAndSet(JsonParser p, DeserializationContext ctxt, Object instance, String propName) throws IOException {
            this.setProperty(instance, propName, (JsonNode)this.deserialize(p, ctxt));
        }

        @Override
        public Object deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            return this._valueDeserializer.deserialize(p, ctxt);
        }

        @Override
        protected void _set(Object instance, Object propName, Object value) throws Exception {
            this.setProperty(instance, (String)propName, (JsonNode)value);
        }

        protected void setProperty(Object instance, String propName, JsonNode value) throws IOException {
            ObjectNode objectNode;
            AnnotatedField field = (AnnotatedField)this._setter;
            Object val0 = field.getValue(instance);
            if (val0 == null) {
                objectNode = this._nodeFactory.objectNode();
                field.setValue(instance, objectNode);
            } else {
                if (!(val0 instanceof ObjectNode)) {
                    throw JsonMappingException.from((DeserializationContext)null, String.format("Value \"any-setter\" '%s' not `ObjectNode` but %s", this.getPropertyName(), ClassUtil.nameOf(val0.getClass())));
                }
                objectNode = (ObjectNode)val0;
            }
            objectNode.set(propName, value);
        }

        @Override
        public SettableAnyProperty withValueDeserializer(JsonDeserializer<Object> deser) {
            return this;
        }
    }

    protected static class MapFieldAnyProperty
    extends SettableAnyProperty
    implements Serializable {
        private static final long serialVersionUID = 1L;
        protected final ValueInstantiator _valueInstantiator;

        public MapFieldAnyProperty(BeanProperty property, AnnotatedMember field, JavaType valueType, KeyDeserializer keyDeser, JsonDeserializer<Object> valueDeser, TypeDeserializer typeDeser, ValueInstantiator inst) {
            super(property, field, valueType, keyDeser, valueDeser, typeDeser);
            this._valueInstantiator = inst;
        }

        @Override
        public SettableAnyProperty withValueDeserializer(JsonDeserializer<Object> deser) {
            return new MapFieldAnyProperty(this._property, this._setter, this._type, this._keyDeserializer, deser, this._valueTypeDeserializer, this._valueInstantiator);
        }

        @Override
        protected void _set(Object instance, Object propName, Object value) throws Exception {
            AnnotatedField field = (AnnotatedField)this._setter;
            Map<Object, Object> val = (Map<Object, Object>)field.getValue(instance);
            if (val == null) {
                val = this._createAndSetMap(null, field, instance, propName);
            }
            val.put(propName, value);
        }

        protected Map<Object, Object> _createAndSetMap(DeserializationContext ctxt, AnnotatedField field, Object instance, Object propName) throws IOException {
            if (this._valueInstantiator == null) {
                throw JsonMappingException.from(ctxt, String.format("Cannot create an instance of %s for use as \"any-setter\" '%s'", ClassUtil.nameOf(this._type.getRawClass()), this._property.getName()));
            }
            Map map = (Map)this._valueInstantiator.createUsingDefault(ctxt);
            field.setValue(instance, map);
            return map;
        }
    }

    protected static class MethodAnyProperty
    extends SettableAnyProperty
    implements Serializable {
        private static final long serialVersionUID = 1L;

        public MethodAnyProperty(BeanProperty property, AnnotatedMember field, JavaType valueType, KeyDeserializer keyDeser, JsonDeserializer<Object> valueDeser, TypeDeserializer typeDeser) {
            super(property, field, valueType, keyDeser, valueDeser, typeDeser);
        }

        @Override
        protected void _set(Object instance, Object propName, Object value) throws Exception {
            ((AnnotatedMethod)this._setter).callOnWith(instance, propName, value);
        }

        @Override
        public SettableAnyProperty withValueDeserializer(JsonDeserializer<Object> deser) {
            return new MethodAnyProperty(this._property, this._setter, this._type, this._keyDeserializer, deser, this._valueTypeDeserializer);
        }
    }

    private static class AnySetterReferring
    extends ReadableObjectId.Referring {
        private final SettableAnyProperty _parent;
        private final Object _pojo;
        private final String _propName;

        public AnySetterReferring(SettableAnyProperty parent, UnresolvedForwardReference reference, Class<?> type, Object instance, String propName) {
            super(reference, type);
            this._parent = parent;
            this._pojo = instance;
            this._propName = propName;
        }

        @Override
        public void handleResolvedForwardReference(Object id, Object value) throws IOException {
            if (!this.hasId(id)) {
                throw new IllegalArgumentException("Trying to resolve a forward reference with id [" + id.toString() + "] that wasn't previously registered.");
            }
            this._parent.set(this._pojo, this._propName, value);
        }
    }
}


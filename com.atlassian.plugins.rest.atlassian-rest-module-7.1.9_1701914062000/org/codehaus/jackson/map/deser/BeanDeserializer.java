/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.deser;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.JsonToken;
import org.codehaus.jackson.annotate.JsonTypeInfo;
import org.codehaus.jackson.map.BeanDescription;
import org.codehaus.jackson.map.BeanProperty;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.DeserializerProvider;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ResolvableDeserializer;
import org.codehaus.jackson.map.TypeDeserializer;
import org.codehaus.jackson.map.annotate.JsonCachable;
import org.codehaus.jackson.map.deser.AbstractDeserializer;
import org.codehaus.jackson.map.deser.SettableAnyProperty;
import org.codehaus.jackson.map.deser.SettableBeanProperty;
import org.codehaus.jackson.map.deser.ValueInstantiator;
import org.codehaus.jackson.map.deser.impl.BeanPropertyMap;
import org.codehaus.jackson.map.deser.impl.CreatorCollector;
import org.codehaus.jackson.map.deser.impl.ExternalTypeHandler;
import org.codehaus.jackson.map.deser.impl.PropertyBasedCreator;
import org.codehaus.jackson.map.deser.impl.PropertyValueBuffer;
import org.codehaus.jackson.map.deser.impl.UnwrappedPropertyHandler;
import org.codehaus.jackson.map.deser.impl.ValueInjector;
import org.codehaus.jackson.map.deser.std.ContainerDeserializerBase;
import org.codehaus.jackson.map.deser.std.StdDeserializer;
import org.codehaus.jackson.map.introspect.AnnotatedClass;
import org.codehaus.jackson.map.introspect.AnnotatedMember;
import org.codehaus.jackson.map.introspect.AnnotatedWithParams;
import org.codehaus.jackson.map.type.ClassKey;
import org.codehaus.jackson.map.util.ClassUtil;
import org.codehaus.jackson.type.JavaType;
import org.codehaus.jackson.util.TokenBuffer;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@JsonCachable
public class BeanDeserializer
extends StdDeserializer<Object>
implements ResolvableDeserializer {
    protected final AnnotatedClass _forClass;
    protected final JavaType _beanType;
    protected final BeanProperty _property;
    protected final ValueInstantiator _valueInstantiator;
    protected JsonDeserializer<Object> _delegateDeserializer;
    protected final PropertyBasedCreator _propertyBasedCreator;
    protected boolean _nonStandardCreation;
    protected final BeanPropertyMap _beanProperties;
    protected final ValueInjector[] _injectables;
    protected SettableAnyProperty _anySetter;
    protected final HashSet<String> _ignorableProps;
    protected final boolean _ignoreAllUnknown;
    protected final Map<String, SettableBeanProperty> _backRefs;
    protected HashMap<ClassKey, JsonDeserializer<Object>> _subDeserializers;
    protected UnwrappedPropertyHandler _unwrappedPropertyHandler;
    protected ExternalTypeHandler _externalTypeIdHandler;

    @Deprecated
    public BeanDeserializer(AnnotatedClass forClass, JavaType type, BeanProperty property, CreatorCollector creators, BeanPropertyMap properties, Map<String, SettableBeanProperty> backRefs, HashSet<String> ignorableProps, boolean ignoreAllUnknown, SettableAnyProperty anySetter) {
        this(forClass, type, property, creators.constructValueInstantiator(null), properties, backRefs, ignorableProps, ignoreAllUnknown, anySetter, null);
    }

    public BeanDeserializer(BeanDescription beanDesc, BeanProperty property, ValueInstantiator valueInstantiator, BeanPropertyMap properties, Map<String, SettableBeanProperty> backRefs, HashSet<String> ignorableProps, boolean ignoreAllUnknown, SettableAnyProperty anySetter, List<ValueInjector> injectables) {
        this(beanDesc.getClassInfo(), beanDesc.getType(), property, valueInstantiator, properties, backRefs, ignorableProps, ignoreAllUnknown, anySetter, injectables);
    }

    protected BeanDeserializer(AnnotatedClass forClass, JavaType type, BeanProperty property, ValueInstantiator valueInstantiator, BeanPropertyMap properties, Map<String, SettableBeanProperty> backRefs, HashSet<String> ignorableProps, boolean ignoreAllUnknown, SettableAnyProperty anySetter, List<ValueInjector> injectables) {
        super(type);
        this._forClass = forClass;
        this._beanType = type;
        this._property = property;
        this._valueInstantiator = valueInstantiator;
        this._propertyBasedCreator = valueInstantiator.canCreateFromObjectWith() ? new PropertyBasedCreator(valueInstantiator) : null;
        this._beanProperties = properties;
        this._backRefs = backRefs;
        this._ignorableProps = ignorableProps;
        this._ignoreAllUnknown = ignoreAllUnknown;
        this._anySetter = anySetter;
        this._injectables = injectables == null || injectables.isEmpty() ? null : injectables.toArray(new ValueInjector[injectables.size()]);
        this._nonStandardCreation = valueInstantiator.canCreateUsingDelegate() || this._propertyBasedCreator != null || !valueInstantiator.canCreateUsingDefault() || this._unwrappedPropertyHandler != null;
    }

    protected BeanDeserializer(BeanDeserializer src) {
        this(src, src._ignoreAllUnknown);
    }

    protected BeanDeserializer(BeanDeserializer src, boolean ignoreAllUnknown) {
        super(src._beanType);
        this._forClass = src._forClass;
        this._beanType = src._beanType;
        this._property = src._property;
        this._valueInstantiator = src._valueInstantiator;
        this._delegateDeserializer = src._delegateDeserializer;
        this._propertyBasedCreator = src._propertyBasedCreator;
        this._beanProperties = src._beanProperties;
        this._backRefs = src._backRefs;
        this._ignorableProps = src._ignorableProps;
        this._ignoreAllUnknown = ignoreAllUnknown;
        this._anySetter = src._anySetter;
        this._injectables = src._injectables;
        this._nonStandardCreation = src._nonStandardCreation;
        this._unwrappedPropertyHandler = src._unwrappedPropertyHandler;
    }

    @Override
    public JsonDeserializer<Object> unwrappingDeserializer() {
        if (this.getClass() != BeanDeserializer.class) {
            return this;
        }
        return new BeanDeserializer(this, true);
    }

    public boolean hasProperty(String propertyName) {
        return this._beanProperties.find(propertyName) != null;
    }

    public int getPropertyCount() {
        return this._beanProperties.size();
    }

    public final Class<?> getBeanClass() {
        return this._beanType.getRawClass();
    }

    @Override
    public JavaType getValueType() {
        return this._beanType;
    }

    public Iterator<SettableBeanProperty> properties() {
        if (this._beanProperties == null) {
            throw new IllegalStateException("Can only call before BeanDeserializer has been resolved");
        }
        return this._beanProperties.allProperties();
    }

    public SettableBeanProperty findBackReference(String logicalName) {
        if (this._backRefs == null) {
            return null;
        }
        return this._backRefs.get(logicalName);
    }

    public ValueInstantiator getValueInstantiator() {
        return this._valueInstantiator;
    }

    @Override
    public void resolve(DeserializationConfig config, DeserializerProvider provider) throws JsonMappingException {
        Iterator<SettableBeanProperty> it = this._beanProperties.allProperties();
        UnwrappedPropertyHandler unwrapped = null;
        ExternalTypeHandler.Builder extTypes = null;
        while (it.hasNext()) {
            TypeDeserializer typeDeser;
            SettableBeanProperty u;
            SettableBeanProperty origProp = it.next();
            SettableBeanProperty prop = origProp;
            if (!prop.hasValueDeserializer()) {
                prop = prop.withValueDeserializer(this.findDeserializer(config, provider, prop.getType(), prop));
            }
            if ((u = this._resolveUnwrappedProperty(config, prop = this._resolveManagedReferenceProperty(config, prop))) != null) {
                prop = u;
                if (unwrapped == null) {
                    unwrapped = new UnwrappedPropertyHandler();
                }
                unwrapped.addProperty(prop);
            }
            if ((prop = this._resolveInnerClassValuedProperty(config, prop)) != origProp) {
                this._beanProperties.replace(prop);
            }
            if (!prop.hasValueTypeDeserializer() || (typeDeser = prop.getValueTypeDeserializer()).getTypeInclusion() != JsonTypeInfo.As.EXTERNAL_PROPERTY) continue;
            if (extTypes == null) {
                extTypes = new ExternalTypeHandler.Builder();
            }
            extTypes.addExternal(prop, typeDeser.getPropertyName());
            this._beanProperties.remove(prop);
        }
        if (this._anySetter != null && !this._anySetter.hasValueDeserializer()) {
            this._anySetter = this._anySetter.withValueDeserializer(this.findDeserializer(config, provider, this._anySetter.getType(), this._anySetter.getProperty()));
        }
        if (this._valueInstantiator.canCreateUsingDelegate()) {
            JavaType delegateType = this._valueInstantiator.getDelegateType();
            if (delegateType == null) {
                throw new IllegalArgumentException("Invalid delegate-creator definition for " + this._beanType + ": value instantiator (" + this._valueInstantiator.getClass().getName() + ") returned true for 'canCreateUsingDelegate()', but null for 'getDelegateType()'");
            }
            AnnotatedWithParams delegateCreator = this._valueInstantiator.getDelegateCreator();
            BeanProperty.Std property = new BeanProperty.Std(null, delegateType, this._forClass.getAnnotations(), delegateCreator);
            this._delegateDeserializer = this.findDeserializer(config, provider, delegateType, property);
        }
        if (this._propertyBasedCreator != null) {
            for (SettableBeanProperty prop : this._propertyBasedCreator.getCreatorProperties()) {
                if (prop.hasValueDeserializer()) continue;
                this._propertyBasedCreator.assignDeserializer(prop, this.findDeserializer(config, provider, prop.getType(), prop));
            }
        }
        if (extTypes != null) {
            this._externalTypeIdHandler = extTypes.build();
            this._nonStandardCreation = true;
        }
        this._unwrappedPropertyHandler = unwrapped;
        if (unwrapped != null) {
            this._nonStandardCreation = true;
        }
    }

    protected SettableBeanProperty _resolveManagedReferenceProperty(DeserializationConfig config, SettableBeanProperty prop) {
        String refName = prop.getManagedReferenceName();
        if (refName == null) {
            return prop;
        }
        JsonDeserializer<Object> valueDeser = prop.getValueDeserializer();
        SettableBeanProperty backProp = null;
        boolean isContainer = false;
        if (valueDeser instanceof BeanDeserializer) {
            backProp = ((BeanDeserializer)valueDeser).findBackReference(refName);
        } else if (valueDeser instanceof ContainerDeserializerBase) {
            JsonDeserializer<Object> contentDeser = ((ContainerDeserializerBase)valueDeser).getContentDeserializer();
            if (!(contentDeser instanceof BeanDeserializer)) {
                throw new IllegalArgumentException("Can not handle managed/back reference '" + refName + "': value deserializer is of type ContainerDeserializerBase, but content type is not handled by a BeanDeserializer  (instead it's of type " + contentDeser.getClass().getName() + ")");
            }
            backProp = ((BeanDeserializer)contentDeser).findBackReference(refName);
            isContainer = true;
        } else {
            if (valueDeser instanceof AbstractDeserializer) {
                throw new IllegalArgumentException("Can not handle managed/back reference for abstract types (property " + this._beanType.getRawClass().getName() + "." + prop.getName() + ")");
            }
            throw new IllegalArgumentException("Can not handle managed/back reference '" + refName + "': type for value deserializer is not BeanDeserializer or ContainerDeserializerBase, but " + valueDeser.getClass().getName());
        }
        if (backProp == null) {
            throw new IllegalArgumentException("Can not handle managed/back reference '" + refName + "': no back reference property found from type " + prop.getType());
        }
        JavaType referredType = this._beanType;
        JavaType backRefType = backProp.getType();
        if (!backRefType.getRawClass().isAssignableFrom(referredType.getRawClass())) {
            throw new IllegalArgumentException("Can not handle managed/back reference '" + refName + "': back reference type (" + backRefType.getRawClass().getName() + ") not compatible with managed type (" + referredType.getRawClass().getName() + ")");
        }
        return new SettableBeanProperty.ManagedReferenceProperty(refName, prop, backProp, this._forClass.getAnnotations(), isContainer);
    }

    protected SettableBeanProperty _resolveUnwrappedProperty(DeserializationConfig config, SettableBeanProperty prop) {
        JsonDeserializer<Object> orig;
        JsonDeserializer<Object> unwrapping;
        AnnotatedMember am = prop.getMember();
        if (am != null && config.getAnnotationIntrospector().shouldUnwrapProperty(am) == Boolean.TRUE && (unwrapping = (orig = prop.getValueDeserializer()).unwrappingDeserializer()) != orig && unwrapping != null) {
            return prop.withValueDeserializer(unwrapping);
        }
        return null;
    }

    protected SettableBeanProperty _resolveInnerClassValuedProperty(DeserializationConfig config, SettableBeanProperty prop) {
        Class<?> valueClass;
        Class<?> enclosing;
        BeanDeserializer bd;
        ValueInstantiator vi;
        JsonDeserializer<Object> deser = prop.getValueDeserializer();
        if (deser instanceof BeanDeserializer && !(vi = (bd = (BeanDeserializer)deser).getValueInstantiator()).canCreateUsingDefault() && (enclosing = ClassUtil.getOuterClass(valueClass = prop.getType().getRawClass())) != null && enclosing == this._beanType.getRawClass()) {
            for (Constructor<?> ctor : valueClass.getConstructors()) {
                Class<?>[] paramTypes = ctor.getParameterTypes();
                if (paramTypes.length != 1 || paramTypes[0] != enclosing) continue;
                if (config.isEnabled(DeserializationConfig.Feature.CAN_OVERRIDE_ACCESS_MODIFIERS)) {
                    ClassUtil.checkAndFixAccess(ctor);
                }
                return new SettableBeanProperty.InnerClassProperty(prop, ctor);
            }
        }
        return prop;
    }

    @Override
    public final Object deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.START_OBJECT) {
            jp.nextToken();
            return this.deserializeFromObject(jp, ctxt);
        }
        switch (t) {
            case VALUE_STRING: {
                return this.deserializeFromString(jp, ctxt);
            }
            case VALUE_NUMBER_INT: {
                return this.deserializeFromNumber(jp, ctxt);
            }
            case VALUE_NUMBER_FLOAT: {
                return this.deserializeFromDouble(jp, ctxt);
            }
            case VALUE_EMBEDDED_OBJECT: {
                return jp.getEmbeddedObject();
            }
            case VALUE_TRUE: 
            case VALUE_FALSE: {
                return this.deserializeFromBoolean(jp, ctxt);
            }
            case START_ARRAY: {
                return this.deserializeFromArray(jp, ctxt);
            }
            case FIELD_NAME: 
            case END_OBJECT: {
                return this.deserializeFromObject(jp, ctxt);
            }
        }
        throw ctxt.mappingException(this.getBeanClass());
    }

    @Override
    public Object deserialize(JsonParser jp, DeserializationContext ctxt, Object bean) throws IOException, JsonProcessingException {
        if (this._injectables != null) {
            this.injectValues(ctxt, bean);
        }
        if (this._unwrappedPropertyHandler != null) {
            return this.deserializeWithUnwrapped(jp, ctxt, bean);
        }
        if (this._externalTypeIdHandler != null) {
            return this.deserializeWithExternalTypeId(jp, ctxt, bean);
        }
        JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.START_OBJECT) {
            t = jp.nextToken();
        }
        while (t == JsonToken.FIELD_NAME) {
            String propName = jp.getCurrentName();
            jp.nextToken();
            SettableBeanProperty prop = this._beanProperties.find(propName);
            if (prop != null) {
                try {
                    prop.deserializeAndSet(jp, ctxt, bean);
                }
                catch (Exception e) {
                    this.wrapAndThrow((Throwable)e, bean, propName, ctxt);
                }
            } else if (this._ignorableProps != null && this._ignorableProps.contains(propName)) {
                jp.skipChildren();
            } else if (this._anySetter != null) {
                this._anySetter.deserializeAndSet(jp, ctxt, bean, propName);
            } else {
                this.handleUnknownProperty(jp, ctxt, bean, propName);
            }
            t = jp.nextToken();
        }
        return bean;
    }

    @Override
    public Object deserializeWithType(JsonParser jp, DeserializationContext ctxt, TypeDeserializer typeDeserializer) throws IOException, JsonProcessingException {
        return typeDeserializer.deserializeTypedFromObject(jp, ctxt);
    }

    public Object deserializeFromObject(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        if (this._nonStandardCreation) {
            if (this._unwrappedPropertyHandler != null) {
                return this.deserializeWithUnwrapped(jp, ctxt);
            }
            if (this._externalTypeIdHandler != null) {
                return this.deserializeWithExternalTypeId(jp, ctxt);
            }
            return this.deserializeFromObjectUsingNonDefault(jp, ctxt);
        }
        Object bean = this._valueInstantiator.createUsingDefault();
        if (this._injectables != null) {
            this.injectValues(ctxt, bean);
        }
        while (jp.getCurrentToken() != JsonToken.END_OBJECT) {
            String propName = jp.getCurrentName();
            jp.nextToken();
            SettableBeanProperty prop = this._beanProperties.find(propName);
            if (prop != null) {
                try {
                    prop.deserializeAndSet(jp, ctxt, bean);
                }
                catch (Exception e) {
                    this.wrapAndThrow((Throwable)e, bean, propName, ctxt);
                }
            } else {
                this._handleUnknown(jp, ctxt, bean, propName);
            }
            jp.nextToken();
        }
        return bean;
    }

    private final void _handleUnknown(JsonParser jp, DeserializationContext ctxt, Object bean, String propName) throws IOException, JsonProcessingException {
        if (this._ignorableProps != null && this._ignorableProps.contains(propName)) {
            jp.skipChildren();
        } else if (this._anySetter != null) {
            try {
                this._anySetter.deserializeAndSet(jp, ctxt, bean, propName);
            }
            catch (Exception e) {
                this.wrapAndThrow((Throwable)e, bean, propName, ctxt);
            }
        } else {
            this.handleUnknownProperty(jp, ctxt, bean, propName);
        }
    }

    protected Object deserializeFromObjectUsingNonDefault(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        if (this._delegateDeserializer != null) {
            return this._valueInstantiator.createUsingDelegate(this._delegateDeserializer.deserialize(jp, ctxt));
        }
        if (this._propertyBasedCreator != null) {
            return this._deserializeUsingPropertyBased(jp, ctxt);
        }
        if (this._beanType.isAbstract()) {
            throw JsonMappingException.from(jp, "Can not instantiate abstract type " + this._beanType + " (need to add/enable type information?)");
        }
        throw JsonMappingException.from(jp, "No suitable constructor found for type " + this._beanType + ": can not instantiate from JSON object (need to add/enable type information?)");
    }

    public Object deserializeFromString(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        if (this._delegateDeserializer != null && !this._valueInstantiator.canCreateFromString()) {
            Object bean = this._valueInstantiator.createUsingDelegate(this._delegateDeserializer.deserialize(jp, ctxt));
            if (this._injectables != null) {
                this.injectValues(ctxt, bean);
            }
            return bean;
        }
        return this._valueInstantiator.createFromString(jp.getText());
    }

    public Object deserializeFromNumber(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        switch (jp.getNumberType()) {
            case INT: {
                if (this._delegateDeserializer != null && !this._valueInstantiator.canCreateFromInt()) {
                    Object bean = this._valueInstantiator.createUsingDelegate(this._delegateDeserializer.deserialize(jp, ctxt));
                    if (this._injectables != null) {
                        this.injectValues(ctxt, bean);
                    }
                    return bean;
                }
                return this._valueInstantiator.createFromInt(jp.getIntValue());
            }
            case LONG: {
                if (this._delegateDeserializer != null && !this._valueInstantiator.canCreateFromInt()) {
                    Object bean = this._valueInstantiator.createUsingDelegate(this._delegateDeserializer.deserialize(jp, ctxt));
                    if (this._injectables != null) {
                        this.injectValues(ctxt, bean);
                    }
                    return bean;
                }
                return this._valueInstantiator.createFromLong(jp.getLongValue());
            }
        }
        if (this._delegateDeserializer != null) {
            Object bean = this._valueInstantiator.createUsingDelegate(this._delegateDeserializer.deserialize(jp, ctxt));
            if (this._injectables != null) {
                this.injectValues(ctxt, bean);
            }
            return bean;
        }
        throw ctxt.instantiationException(this.getBeanClass(), "no suitable creator method found to deserialize from JSON integer number");
    }

    public Object deserializeFromDouble(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        switch (jp.getNumberType()) {
            case FLOAT: 
            case DOUBLE: {
                if (this._delegateDeserializer != null && !this._valueInstantiator.canCreateFromDouble()) {
                    Object bean = this._valueInstantiator.createUsingDelegate(this._delegateDeserializer.deserialize(jp, ctxt));
                    if (this._injectables != null) {
                        this.injectValues(ctxt, bean);
                    }
                    return bean;
                }
                return this._valueInstantiator.createFromDouble(jp.getDoubleValue());
            }
        }
        if (this._delegateDeserializer != null) {
            return this._valueInstantiator.createUsingDelegate(this._delegateDeserializer.deserialize(jp, ctxt));
        }
        throw ctxt.instantiationException(this.getBeanClass(), "no suitable creator method found to deserialize from JSON floating-point number");
    }

    public Object deserializeFromBoolean(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        if (this._delegateDeserializer != null && !this._valueInstantiator.canCreateFromBoolean()) {
            Object bean = this._valueInstantiator.createUsingDelegate(this._delegateDeserializer.deserialize(jp, ctxt));
            if (this._injectables != null) {
                this.injectValues(ctxt, bean);
            }
            return bean;
        }
        boolean value = jp.getCurrentToken() == JsonToken.VALUE_TRUE;
        return this._valueInstantiator.createFromBoolean(value);
    }

    public Object deserializeFromArray(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        if (this._delegateDeserializer != null) {
            try {
                Object bean = this._valueInstantiator.createUsingDelegate(this._delegateDeserializer.deserialize(jp, ctxt));
                if (this._injectables != null) {
                    this.injectValues(ctxt, bean);
                }
                return bean;
            }
            catch (Exception e) {
                this.wrapInstantiationProblem(e, ctxt);
            }
        }
        throw ctxt.mappingException(this.getBeanClass());
    }

    protected final Object _deserializeUsingPropertyBased(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        Object bean;
        PropertyBasedCreator creator = this._propertyBasedCreator;
        PropertyValueBuffer buffer = creator.startBuilding(jp, ctxt);
        TokenBuffer unknown = null;
        JsonToken t = jp.getCurrentToken();
        while (t == JsonToken.FIELD_NAME) {
            block19: {
                String propName = jp.getCurrentName();
                jp.nextToken();
                SettableBeanProperty creatorProp = creator.findCreatorProperty(propName);
                if (creatorProp != null) {
                    Object value = creatorProp.deserialize(jp, ctxt);
                    if (buffer.assignParameter(creatorProp.getPropertyIndex(), value)) {
                        Object bean2;
                        jp.nextToken();
                        try {
                            bean2 = creator.build(buffer);
                        }
                        catch (Exception e) {
                            this.wrapAndThrow((Throwable)e, this._beanType.getRawClass(), propName, ctxt);
                            break block19;
                        }
                        if (bean2.getClass() != this._beanType.getRawClass()) {
                            return this.handlePolymorphic(jp, ctxt, bean2, unknown);
                        }
                        if (unknown != null) {
                            bean2 = this.handleUnknownProperties(ctxt, bean2, unknown);
                        }
                        return this.deserialize(jp, ctxt, bean2);
                    }
                } else {
                    SettableBeanProperty prop = this._beanProperties.find(propName);
                    if (prop != null) {
                        buffer.bufferProperty(prop, prop.deserialize(jp, ctxt));
                    } else if (this._ignorableProps != null && this._ignorableProps.contains(propName)) {
                        jp.skipChildren();
                    } else if (this._anySetter != null) {
                        buffer.bufferAnyProperty(this._anySetter, propName, this._anySetter.deserialize(jp, ctxt));
                    } else {
                        if (unknown == null) {
                            unknown = new TokenBuffer(jp.getCodec());
                        }
                        unknown.writeFieldName(propName);
                        unknown.copyCurrentStructure(jp);
                    }
                }
            }
            t = jp.nextToken();
        }
        try {
            bean = creator.build(buffer);
        }
        catch (Exception e) {
            this.wrapInstantiationProblem(e, ctxt);
            return null;
        }
        if (unknown != null) {
            if (bean.getClass() != this._beanType.getRawClass()) {
                return this.handlePolymorphic(null, ctxt, bean, unknown);
            }
            return this.handleUnknownProperties(ctxt, bean, unknown);
        }
        return bean;
    }

    protected Object handlePolymorphic(JsonParser jp, DeserializationContext ctxt, Object bean, TokenBuffer unknownTokens) throws IOException, JsonProcessingException {
        JsonDeserializer<Object> subDeser = this._findSubclassDeserializer(ctxt, bean, unknownTokens);
        if (subDeser != null) {
            if (unknownTokens != null) {
                unknownTokens.writeEndObject();
                JsonParser p2 = unknownTokens.asParser();
                p2.nextToken();
                bean = subDeser.deserialize(p2, ctxt, bean);
            }
            if (jp != null) {
                bean = subDeser.deserialize(jp, ctxt, bean);
            }
            return bean;
        }
        if (unknownTokens != null) {
            bean = this.handleUnknownProperties(ctxt, bean, unknownTokens);
        }
        if (jp != null) {
            bean = this.deserialize(jp, ctxt, bean);
        }
        return bean;
    }

    protected Object deserializeWithUnwrapped(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        if (this._delegateDeserializer != null) {
            return this._valueInstantiator.createUsingDelegate(this._delegateDeserializer.deserialize(jp, ctxt));
        }
        if (this._propertyBasedCreator != null) {
            return this.deserializeUsingPropertyBasedWithUnwrapped(jp, ctxt);
        }
        TokenBuffer tokens = new TokenBuffer(jp.getCodec());
        tokens.writeStartObject();
        Object bean = this._valueInstantiator.createUsingDefault();
        if (this._injectables != null) {
            this.injectValues(ctxt, bean);
        }
        while (jp.getCurrentToken() != JsonToken.END_OBJECT) {
            String propName = jp.getCurrentName();
            jp.nextToken();
            SettableBeanProperty prop = this._beanProperties.find(propName);
            if (prop != null) {
                try {
                    prop.deserializeAndSet(jp, ctxt, bean);
                }
                catch (Exception e) {
                    this.wrapAndThrow((Throwable)e, bean, propName, ctxt);
                }
            } else if (this._ignorableProps != null && this._ignorableProps.contains(propName)) {
                jp.skipChildren();
            } else {
                tokens.writeFieldName(propName);
                tokens.copyCurrentStructure(jp);
                if (this._anySetter != null) {
                    try {
                        this._anySetter.deserializeAndSet(jp, ctxt, bean, propName);
                    }
                    catch (Exception e) {
                        this.wrapAndThrow((Throwable)e, bean, propName, ctxt);
                    }
                }
            }
            jp.nextToken();
        }
        tokens.writeEndObject();
        this._unwrappedPropertyHandler.processUnwrapped(jp, ctxt, bean, tokens);
        return bean;
    }

    protected Object deserializeWithUnwrapped(JsonParser jp, DeserializationContext ctxt, Object bean) throws IOException, JsonProcessingException {
        JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.START_OBJECT) {
            t = jp.nextToken();
        }
        TokenBuffer tokens = new TokenBuffer(jp.getCodec());
        tokens.writeStartObject();
        while (t == JsonToken.FIELD_NAME) {
            String propName = jp.getCurrentName();
            SettableBeanProperty prop = this._beanProperties.find(propName);
            jp.nextToken();
            if (prop != null) {
                try {
                    prop.deserializeAndSet(jp, ctxt, bean);
                }
                catch (Exception e) {
                    this.wrapAndThrow((Throwable)e, bean, propName, ctxt);
                }
            } else if (this._ignorableProps != null && this._ignorableProps.contains(propName)) {
                jp.skipChildren();
            } else {
                tokens.writeFieldName(propName);
                tokens.copyCurrentStructure(jp);
                if (this._anySetter != null) {
                    this._anySetter.deserializeAndSet(jp, ctxt, bean, propName);
                }
            }
            t = jp.nextToken();
        }
        tokens.writeEndObject();
        this._unwrappedPropertyHandler.processUnwrapped(jp, ctxt, bean, tokens);
        return bean;
    }

    protected Object deserializeUsingPropertyBasedWithUnwrapped(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        Object bean;
        PropertyBasedCreator creator = this._propertyBasedCreator;
        PropertyValueBuffer buffer = creator.startBuilding(jp, ctxt);
        TokenBuffer tokens = new TokenBuffer(jp.getCodec());
        tokens.writeStartObject();
        JsonToken t = jp.getCurrentToken();
        while (t == JsonToken.FIELD_NAME) {
            block15: {
                String propName = jp.getCurrentName();
                jp.nextToken();
                SettableBeanProperty creatorProp = creator.findCreatorProperty(propName);
                if (creatorProp != null) {
                    Object value = creatorProp.deserialize(jp, ctxt);
                    if (buffer.assignParameter(creatorProp.getPropertyIndex(), value)) {
                        Object bean2;
                        t = jp.nextToken();
                        try {
                            bean2 = creator.build(buffer);
                        }
                        catch (Exception e) {
                            this.wrapAndThrow((Throwable)e, this._beanType.getRawClass(), propName, ctxt);
                            break block15;
                        }
                        while (t == JsonToken.FIELD_NAME) {
                            jp.nextToken();
                            tokens.copyCurrentStructure(jp);
                            t = jp.nextToken();
                        }
                        tokens.writeEndObject();
                        if (bean2.getClass() != this._beanType.getRawClass()) {
                            throw ctxt.mappingException("Can not create polymorphic instances with unwrapped values");
                        }
                        return this._unwrappedPropertyHandler.processUnwrapped(jp, ctxt, bean2, tokens);
                    }
                } else {
                    SettableBeanProperty prop = this._beanProperties.find(propName);
                    if (prop != null) {
                        buffer.bufferProperty(prop, prop.deserialize(jp, ctxt));
                    } else if (this._ignorableProps != null && this._ignorableProps.contains(propName)) {
                        jp.skipChildren();
                    } else {
                        tokens.writeFieldName(propName);
                        tokens.copyCurrentStructure(jp);
                        if (this._anySetter != null) {
                            buffer.bufferAnyProperty(this._anySetter, propName, this._anySetter.deserialize(jp, ctxt));
                        }
                    }
                }
            }
            t = jp.nextToken();
        }
        try {
            bean = creator.build(buffer);
        }
        catch (Exception e) {
            this.wrapInstantiationProblem(e, ctxt);
            return null;
        }
        return this._unwrappedPropertyHandler.processUnwrapped(jp, ctxt, bean, tokens);
    }

    protected Object deserializeWithExternalTypeId(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        if (this._propertyBasedCreator != null) {
            return this.deserializeUsingPropertyBasedWithExternalTypeId(jp, ctxt);
        }
        return this.deserializeWithExternalTypeId(jp, ctxt, this._valueInstantiator.createUsingDefault());
    }

    protected Object deserializeWithExternalTypeId(JsonParser jp, DeserializationContext ctxt, Object bean) throws IOException, JsonProcessingException {
        ExternalTypeHandler ext = this._externalTypeIdHandler.start();
        while (jp.getCurrentToken() != JsonToken.END_OBJECT) {
            String propName = jp.getCurrentName();
            jp.nextToken();
            SettableBeanProperty prop = this._beanProperties.find(propName);
            if (prop != null) {
                if (jp.getCurrentToken().isScalarValue()) {
                    ext.handleTypePropertyValue(jp, ctxt, propName, bean);
                }
                try {
                    prop.deserializeAndSet(jp, ctxt, bean);
                }
                catch (Exception e) {
                    this.wrapAndThrow((Throwable)e, bean, propName, ctxt);
                }
            } else if (this._ignorableProps != null && this._ignorableProps.contains(propName)) {
                jp.skipChildren();
            } else if (!ext.handleToken(jp, ctxt, propName, bean)) {
                if (this._anySetter != null) {
                    try {
                        this._anySetter.deserializeAndSet(jp, ctxt, bean, propName);
                    }
                    catch (Exception e) {
                        this.wrapAndThrow((Throwable)e, bean, propName, ctxt);
                    }
                } else {
                    this.handleUnknownProperty(jp, ctxt, bean, propName);
                }
            }
            jp.nextToken();
        }
        return ext.complete(jp, ctxt, bean);
    }

    protected Object deserializeUsingPropertyBasedWithExternalTypeId(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        Object bean;
        ExternalTypeHandler ext = this._externalTypeIdHandler.start();
        PropertyBasedCreator creator = this._propertyBasedCreator;
        PropertyValueBuffer buffer = creator.startBuilding(jp, ctxt);
        TokenBuffer tokens = new TokenBuffer(jp.getCodec());
        tokens.writeStartObject();
        JsonToken t = jp.getCurrentToken();
        while (t == JsonToken.FIELD_NAME) {
            block16: {
                String propName = jp.getCurrentName();
                jp.nextToken();
                SettableBeanProperty creatorProp = creator.findCreatorProperty(propName);
                if (creatorProp != null) {
                    Object value = creatorProp.deserialize(jp, ctxt);
                    if (buffer.assignParameter(creatorProp.getPropertyIndex(), value)) {
                        Object bean2;
                        t = jp.nextToken();
                        try {
                            bean2 = creator.build(buffer);
                        }
                        catch (Exception e) {
                            this.wrapAndThrow((Throwable)e, this._beanType.getRawClass(), propName, ctxt);
                            break block16;
                        }
                        while (t == JsonToken.FIELD_NAME) {
                            jp.nextToken();
                            tokens.copyCurrentStructure(jp);
                            t = jp.nextToken();
                        }
                        if (bean2.getClass() != this._beanType.getRawClass()) {
                            throw ctxt.mappingException("Can not create polymorphic instances with unwrapped values");
                        }
                        return ext.complete(jp, ctxt, bean2);
                    }
                } else {
                    SettableBeanProperty prop = this._beanProperties.find(propName);
                    if (prop != null) {
                        buffer.bufferProperty(prop, prop.deserialize(jp, ctxt));
                    } else if (!ext.handleToken(jp, ctxt, propName, null)) {
                        if (this._ignorableProps != null && this._ignorableProps.contains(propName)) {
                            jp.skipChildren();
                        } else if (this._anySetter != null) {
                            buffer.bufferAnyProperty(this._anySetter, propName, this._anySetter.deserialize(jp, ctxt));
                        }
                    }
                }
            }
            t = jp.nextToken();
        }
        try {
            bean = creator.build(buffer);
        }
        catch (Exception e) {
            this.wrapInstantiationProblem(e, ctxt);
            return null;
        }
        return ext.complete(jp, ctxt, bean);
    }

    protected void injectValues(DeserializationContext ctxt, Object bean) throws IOException, JsonProcessingException {
        for (ValueInjector injector : this._injectables) {
            injector.inject(ctxt, bean);
        }
    }

    @Override
    protected void handleUnknownProperty(JsonParser jp, DeserializationContext ctxt, Object beanOrClass, String propName) throws IOException, JsonProcessingException {
        if (this._ignoreAllUnknown || this._ignorableProps != null && this._ignorableProps.contains(propName)) {
            jp.skipChildren();
            return;
        }
        super.handleUnknownProperty(jp, ctxt, beanOrClass, propName);
    }

    protected Object handleUnknownProperties(DeserializationContext ctxt, Object bean, TokenBuffer unknownTokens) throws IOException, JsonProcessingException {
        unknownTokens.writeEndObject();
        JsonParser bufferParser = unknownTokens.asParser();
        while (bufferParser.nextToken() != JsonToken.END_OBJECT) {
            String propName = bufferParser.getCurrentName();
            bufferParser.nextToken();
            this.handleUnknownProperty(bufferParser, ctxt, bean, propName);
        }
        return bean;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected JsonDeserializer<Object> _findSubclassDeserializer(DeserializationContext ctxt, Object bean, TokenBuffer unknownTokens) throws IOException, JsonProcessingException {
        JsonDeserializer<Object> subDeser;
        BeanDeserializer beanDeserializer = this;
        synchronized (beanDeserializer) {
            subDeser = this._subDeserializers == null ? null : this._subDeserializers.get(new ClassKey(bean.getClass()));
        }
        if (subDeser != null) {
            return subDeser;
        }
        DeserializerProvider deserProv = ctxt.getDeserializerProvider();
        if (deserProv != null) {
            JavaType type = ctxt.constructType(bean.getClass());
            subDeser = deserProv.findValueDeserializer(ctxt.getConfig(), type, this._property);
            if (subDeser != null) {
                BeanDeserializer beanDeserializer2 = this;
                synchronized (beanDeserializer2) {
                    if (this._subDeserializers == null) {
                        this._subDeserializers = new HashMap();
                    }
                    this._subDeserializers.put(new ClassKey(bean.getClass()), subDeser);
                }
            }
        }
        return subDeser;
    }

    public void wrapAndThrow(Throwable t, Object bean, String fieldName, DeserializationContext ctxt) throws IOException {
        boolean wrap;
        while (t instanceof InvocationTargetException && t.getCause() != null) {
            t = t.getCause();
        }
        if (t instanceof Error) {
            throw (Error)t;
        }
        boolean bl = wrap = ctxt == null || ctxt.isEnabled(DeserializationConfig.Feature.WRAP_EXCEPTIONS);
        if (t instanceof IOException) {
            if (!wrap || !(t instanceof JsonMappingException)) {
                throw (IOException)t;
            }
        } else if (!wrap && t instanceof RuntimeException) {
            throw (RuntimeException)t;
        }
        throw JsonMappingException.wrapWithPath(t, bean, fieldName);
    }

    public void wrapAndThrow(Throwable t, Object bean, int index, DeserializationContext ctxt) throws IOException {
        boolean wrap;
        while (t instanceof InvocationTargetException && t.getCause() != null) {
            t = t.getCause();
        }
        if (t instanceof Error) {
            throw (Error)t;
        }
        boolean bl = wrap = ctxt == null || ctxt.isEnabled(DeserializationConfig.Feature.WRAP_EXCEPTIONS);
        if (t instanceof IOException) {
            if (!wrap || !(t instanceof JsonMappingException)) {
                throw (IOException)t;
            }
        } else if (!wrap && t instanceof RuntimeException) {
            throw (RuntimeException)t;
        }
        throw JsonMappingException.wrapWithPath(t, bean, index);
    }

    protected void wrapInstantiationProblem(Throwable t, DeserializationContext ctxt) throws IOException {
        boolean wrap;
        while (t instanceof InvocationTargetException && t.getCause() != null) {
            t = t.getCause();
        }
        if (t instanceof Error) {
            throw (Error)t;
        }
        boolean bl = wrap = ctxt == null || ctxt.isEnabled(DeserializationConfig.Feature.WRAP_EXCEPTIONS);
        if (t instanceof IOException) {
            throw (IOException)t;
        }
        if (!wrap && t instanceof RuntimeException) {
            throw (RuntimeException)t;
        }
        throw ctxt.instantiationException(this._beanType.getRawClass(), t);
    }

    @Deprecated
    public void wrapAndThrow(Throwable t, Object bean, String fieldName) throws IOException {
        this.wrapAndThrow(t, bean, fieldName, null);
    }

    @Deprecated
    public void wrapAndThrow(Throwable t, Object bean, int index) throws IOException {
        this.wrapAndThrow(t, bean, index, null);
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.fasterxml.jackson.annotation.JsonIgnoreProperties$Value
 *  com.fasterxml.jackson.annotation.JsonIncludeProperties$Value
 *  com.fasterxml.jackson.annotation.JsonTypeInfo$As
 *  com.fasterxml.jackson.annotation.ObjectIdGenerator
 *  com.fasterxml.jackson.annotation.ObjectIdGenerators$PropertyGenerator
 *  com.fasterxml.jackson.core.JsonGenerator
 *  com.fasterxml.jackson.core.JsonParser
 *  com.fasterxml.jackson.core.TokenStreamFactory
 */
package com.fasterxml.jackson.databind.ser;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIncludeProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.TokenStreamFactory;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.PropertyMetadata;
import com.fasterxml.jackson.databind.PropertyName;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.cfg.SerializerFactoryConfig;
import com.fasterxml.jackson.databind.introspect.AnnotatedClass;
import com.fasterxml.jackson.databind.introspect.AnnotatedField;
import com.fasterxml.jackson.databind.introspect.AnnotatedMember;
import com.fasterxml.jackson.databind.introspect.AnnotatedMethod;
import com.fasterxml.jackson.databind.introspect.BeanPropertyDefinition;
import com.fasterxml.jackson.databind.introspect.ObjectIdInfo;
import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.jsontype.TypeResolverBuilder;
import com.fasterxml.jackson.databind.jsontype.TypeSerializer;
import com.fasterxml.jackson.databind.ser.AnyGetterWriter;
import com.fasterxml.jackson.databind.ser.BasicSerializerFactory;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerBuilder;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.fasterxml.jackson.databind.ser.PropertyBuilder;
import com.fasterxml.jackson.databind.ser.ResolvableSerializer;
import com.fasterxml.jackson.databind.ser.SerializerFactory;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.fasterxml.jackson.databind.ser.impl.FilteredBeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.impl.ObjectIdWriter;
import com.fasterxml.jackson.databind.ser.impl.PropertyBasedObjectIdGenerator;
import com.fasterxml.jackson.databind.ser.impl.UnsupportedTypeSerializer;
import com.fasterxml.jackson.databind.ser.std.MapSerializer;
import com.fasterxml.jackson.databind.ser.std.StdDelegatingSerializer;
import com.fasterxml.jackson.databind.ser.std.ToEmptyObjectSerializer;
import com.fasterxml.jackson.databind.type.ReferenceType;
import com.fasterxml.jackson.databind.util.BeanUtil;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.util.Converter;
import com.fasterxml.jackson.databind.util.IgnorePropertiesUtil;
import com.fasterxml.jackson.databind.util.NativeImageUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class BeanSerializerFactory
extends BasicSerializerFactory
implements Serializable {
    private static final long serialVersionUID = 1L;
    public static final BeanSerializerFactory instance = new BeanSerializerFactory(null);

    protected BeanSerializerFactory(SerializerFactoryConfig config) {
        super(config);
    }

    @Override
    public SerializerFactory withConfig(SerializerFactoryConfig config) {
        if (this._factoryConfig == config) {
            return this;
        }
        if (this.getClass() != BeanSerializerFactory.class) {
            throw new IllegalStateException("Subtype of BeanSerializerFactory (" + this.getClass().getName() + ") has not properly overridden method 'withAdditionalSerializers': cannot instantiate subtype with additional serializer definitions");
        }
        return new BeanSerializerFactory(config);
    }

    @Override
    protected Iterable<Serializers> customSerializers() {
        return this._factoryConfig.serializers();
    }

    @Override
    public JsonSerializer<Object> createSerializer(SerializerProvider prov, JavaType origType) throws JsonMappingException {
        boolean staticTyping;
        JavaType type;
        SerializationConfig config = prov.getConfig();
        BeanDescription beanDesc = config.introspect(origType);
        JsonSerializer<Object> ser = this.findSerializerFromAnnotation(prov, beanDesc.getClassInfo());
        if (ser != null) {
            return ser;
        }
        AnnotationIntrospector intr = config.getAnnotationIntrospector();
        if (intr == null) {
            type = origType;
        } else {
            try {
                type = intr.refineSerializationType(config, beanDesc.getClassInfo(), origType);
            }
            catch (JsonMappingException e) {
                return (JsonSerializer)prov.reportBadTypeDefinition(beanDesc, e.getMessage(), new Object[0]);
            }
        }
        if (type == origType) {
            staticTyping = false;
        } else {
            staticTyping = true;
            if (!type.hasRawClass(origType.getRawClass())) {
                beanDesc = config.introspect(type);
            }
        }
        Converter<Object, Object> conv = beanDesc.findSerializationConverter();
        if (conv == null) {
            return this._createSerializer2(prov, type, beanDesc, staticTyping);
        }
        JavaType delegateType = conv.getOutputType(prov.getTypeFactory());
        if (!delegateType.hasRawClass(type.getRawClass())) {
            beanDesc = config.introspect(delegateType);
            ser = this.findSerializerFromAnnotation(prov, beanDesc.getClassInfo());
        }
        if (ser == null && !delegateType.isJavaLangObject()) {
            ser = this._createSerializer2(prov, delegateType, beanDesc, true);
        }
        return new StdDelegatingSerializer(conv, delegateType, ser);
    }

    protected JsonSerializer<?> _createSerializer2(SerializerProvider prov, JavaType type, BeanDescription beanDesc, boolean staticTyping) throws JsonMappingException {
        JsonSerializer<Object> ser = null;
        SerializationConfig config = prov.getConfig();
        if (type.isContainerType()) {
            if (!staticTyping) {
                staticTyping = this.usesStaticTyping(config, beanDesc);
            }
            if ((ser = this.buildContainerSerializer(prov, type, beanDesc, staticTyping)) != null) {
                return ser;
            }
        } else {
            if (type.isReferenceType()) {
                ser = this.findReferenceSerializer(prov, (ReferenceType)type, beanDesc, staticTyping);
            } else {
                Serializers serializers;
                Iterator<Object> iterator = this.customSerializers().iterator();
                while (iterator.hasNext() && (ser = (serializers = iterator.next()).findSerializer(config, type, beanDesc)) == null) {
                }
            }
            if (ser == null) {
                ser = this.findSerializerByAnnotations(prov, type, beanDesc);
            }
        }
        if (ser == null && (ser = this.findSerializerByLookup(type, config, beanDesc, staticTyping)) == null && (ser = this.findSerializerByPrimaryType(prov, type, beanDesc, staticTyping)) == null && (ser = this.findBeanOrAddOnSerializer(prov, type, beanDesc, staticTyping)) == null) {
            ser = prov.getUnknownTypeSerializer(beanDesc.getBeanClass());
        }
        if (ser != null && this._factoryConfig.hasSerializerModifiers()) {
            for (BeanSerializerModifier mod : this._factoryConfig.serializerModifiers()) {
                ser = mod.modifySerializer(config, beanDesc, ser);
            }
        }
        return ser;
    }

    @Deprecated
    public JsonSerializer<Object> findBeanSerializer(SerializerProvider prov, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
        return this.findBeanOrAddOnSerializer(prov, type, beanDesc, prov.isEnabled(MapperFeature.USE_STATIC_TYPING));
    }

    public JsonSerializer<Object> findBeanOrAddOnSerializer(SerializerProvider prov, JavaType type, BeanDescription beanDesc, boolean staticTyping) throws JsonMappingException {
        if (!this.isPotentialBeanType(type.getRawClass()) && !ClassUtil.isEnumType(type.getRawClass())) {
            return null;
        }
        return this.constructBeanOrAddOnSerializer(prov, type, beanDesc, staticTyping);
    }

    public TypeSerializer findPropertyTypeSerializer(JavaType baseType, SerializationConfig config, AnnotatedMember accessor) throws JsonMappingException {
        TypeSerializer typeSer;
        AnnotationIntrospector ai = config.getAnnotationIntrospector();
        TypeResolverBuilder<?> b = ai.findPropertyTypeResolver(config, accessor, baseType);
        if (b == null) {
            typeSer = this.createTypeSerializer(config, baseType);
        } else {
            Collection<NamedType> subtypes = config.getSubtypeResolver().collectAndResolveSubtypesByClass(config, accessor, baseType);
            typeSer = b.buildTypeSerializer(config, baseType, subtypes);
        }
        return typeSer;
    }

    public TypeSerializer findPropertyContentTypeSerializer(JavaType containerType, SerializationConfig config, AnnotatedMember accessor) throws JsonMappingException {
        TypeSerializer typeSer;
        JavaType contentType = containerType.getContentType();
        AnnotationIntrospector ai = config.getAnnotationIntrospector();
        TypeResolverBuilder<?> b = ai.findPropertyContentTypeResolver(config, accessor, containerType);
        if (b == null) {
            typeSer = this.createTypeSerializer(config, contentType);
        } else {
            Collection<NamedType> subtypes = config.getSubtypeResolver().collectAndResolveSubtypesByClass(config, accessor, contentType);
            typeSer = b.buildTypeSerializer(config, contentType, subtypes);
        }
        return typeSer;
    }

    @Deprecated
    protected JsonSerializer<Object> constructBeanSerializer(SerializerProvider prov, BeanDescription beanDesc) throws JsonMappingException {
        return this.constructBeanOrAddOnSerializer(prov, beanDesc.getType(), beanDesc, prov.isEnabled(MapperFeature.USE_STATIC_TYPING));
    }

    protected JsonSerializer<Object> constructBeanOrAddOnSerializer(SerializerProvider prov, JavaType type, BeanDescription beanDesc, boolean staticTyping) throws JsonMappingException {
        if (beanDesc.getBeanClass() == Object.class) {
            return prov.getUnknownTypeSerializer(Object.class);
        }
        JsonSerializer<Object> ser = this._findUnsupportedTypeSerializer(prov, type, beanDesc);
        if (ser != null) {
            return ser;
        }
        if (this._isUnserializableJacksonType(prov, type)) {
            return new ToEmptyObjectSerializer(type);
        }
        SerializationConfig config = prov.getConfig();
        BeanSerializerBuilder builder = this.constructBeanSerializerBuilder(beanDesc);
        builder.setConfig(config);
        List<BeanPropertyWriter> props = this.findBeanProperties(prov, beanDesc, builder);
        props = props == null ? new ArrayList<BeanPropertyWriter>() : this.removeOverlappingTypeIds(prov, beanDesc, builder, props);
        prov.getAnnotationIntrospector().findAndAddVirtualProperties(config, beanDesc.getClassInfo(), props);
        if (this._factoryConfig.hasSerializerModifiers()) {
            for (BeanSerializerModifier mod : this._factoryConfig.serializerModifiers()) {
                props = mod.changeProperties(config, beanDesc, props);
            }
        }
        props = this.filterUnwantedJDKProperties(config, beanDesc, props);
        props = this.filterBeanProperties(config, beanDesc, props);
        if (this._factoryConfig.hasSerializerModifiers()) {
            for (BeanSerializerModifier mod : this._factoryConfig.serializerModifiers()) {
                props = mod.orderProperties(config, beanDesc, props);
            }
        }
        builder.setObjectIdWriter(this.constructObjectIdHandler(prov, beanDesc, props));
        builder.setProperties(props);
        builder.setFilterId(this.findFilterId(config, beanDesc));
        AnnotatedMember anyGetter = beanDesc.findAnyGetter();
        if (anyGetter != null) {
            JavaType anyType = anyGetter.getType();
            JavaType valueType = anyType.getContentType();
            TypeSerializer typeSer = this.createTypeSerializer(config, valueType);
            MapSerializer anySer = this.findSerializerFromAnnotation(prov, anyGetter);
            if (anySer == null) {
                anySer = MapSerializer.construct((Set<String>)null, anyType, config.isEnabled(MapperFeature.USE_STATIC_TYPING), typeSer, null, null, null);
            }
            PropertyName name = PropertyName.construct(anyGetter.getName());
            BeanProperty.Std anyProp = new BeanProperty.Std(name, valueType, null, anyGetter, PropertyMetadata.STD_OPTIONAL);
            builder.setAnyGetter(new AnyGetterWriter(anyProp, anyGetter, anySer));
        }
        this.processViews(config, builder);
        if (this._factoryConfig.hasSerializerModifiers()) {
            for (BeanSerializerModifier mod : this._factoryConfig.serializerModifiers()) {
                builder = mod.updateBuilder(config, beanDesc, builder);
            }
        }
        try {
            ser = builder.build();
        }
        catch (RuntimeException e) {
            return (JsonSerializer)prov.reportBadTypeDefinition(beanDesc, "Failed to construct BeanSerializer for %s: (%s) %s", beanDesc.getType(), e.getClass().getName(), e.getMessage());
        }
        if (ser == null) {
            if (type.isRecordType() && !NativeImageUtil.needsReflectionConfiguration(type.getRawClass())) {
                return builder.createDummy();
            }
            ser = this.findSerializerByAddonType(config, type, beanDesc, staticTyping);
            if (ser == null && beanDesc.hasKnownClassAnnotations()) {
                return builder.createDummy();
            }
        }
        return ser;
    }

    protected ObjectIdWriter constructObjectIdHandler(SerializerProvider prov, BeanDescription beanDesc, List<BeanPropertyWriter> props) throws JsonMappingException {
        ObjectIdInfo objectIdInfo = beanDesc.getObjectIdInfo();
        if (objectIdInfo == null) {
            return null;
        }
        Class<? extends ObjectIdGenerator<?>> implClass = objectIdInfo.getGeneratorType();
        if (implClass == ObjectIdGenerators.PropertyGenerator.class) {
            String propName = objectIdInfo.getPropertyName().getSimpleName();
            BeanPropertyWriter idProp = null;
            int i = 0;
            int len = props.size();
            while (true) {
                if (i == len) {
                    throw new IllegalArgumentException(String.format("Invalid Object Id definition for %s: cannot find property with name %s", ClassUtil.getTypeDescription(beanDesc.getType()), ClassUtil.name(propName)));
                }
                BeanPropertyWriter prop = props.get(i);
                if (propName.equals(prop.getName())) {
                    idProp = prop;
                    if (i <= 0) break;
                    props.remove(i);
                    props.add(0, idProp);
                    break;
                }
                ++i;
            }
            JavaType idType = idProp.getType();
            PropertyBasedObjectIdGenerator gen = new PropertyBasedObjectIdGenerator(objectIdInfo, idProp);
            return ObjectIdWriter.construct(idType, null, gen, objectIdInfo.getAlwaysAsId());
        }
        JavaType type = prov.constructType(implClass);
        JavaType idType = prov.getTypeFactory().findTypeParameters(type, ObjectIdGenerator.class)[0];
        ObjectIdGenerator<?> gen = prov.objectIdGeneratorInstance(beanDesc.getClassInfo(), objectIdInfo);
        return ObjectIdWriter.construct(idType, objectIdInfo.getPropertyName(), gen, objectIdInfo.getAlwaysAsId());
    }

    protected BeanPropertyWriter constructFilteredBeanWriter(BeanPropertyWriter writer, Class<?>[] inViews) {
        return FilteredBeanPropertyWriter.constructViewBased(writer, inViews);
    }

    protected PropertyBuilder constructPropertyBuilder(SerializationConfig config, BeanDescription beanDesc) {
        return new PropertyBuilder(config, beanDesc);
    }

    protected BeanSerializerBuilder constructBeanSerializerBuilder(BeanDescription beanDesc) {
        return new BeanSerializerBuilder(beanDesc);
    }

    protected boolean isPotentialBeanType(Class<?> type) {
        return ClassUtil.canBeABeanType(type) == null && !ClassUtil.isProxyType(type);
    }

    protected List<BeanPropertyWriter> findBeanProperties(SerializerProvider prov, BeanDescription beanDesc, BeanSerializerBuilder builder) throws JsonMappingException {
        List<BeanPropertyDefinition> properties = beanDesc.findProperties();
        SerializationConfig config = prov.getConfig();
        this.removeIgnorableTypes(config, beanDesc, properties);
        if (config.isEnabled(MapperFeature.REQUIRE_SETTERS_FOR_GETTERS)) {
            this.removeSetterlessGetters(config, beanDesc, properties);
        }
        if (properties.isEmpty()) {
            return null;
        }
        boolean staticTyping = this.usesStaticTyping(config, beanDesc);
        PropertyBuilder pb = this.constructPropertyBuilder(config, beanDesc);
        ArrayList<BeanPropertyWriter> result = new ArrayList<BeanPropertyWriter>(properties.size());
        for (BeanPropertyDefinition property : properties) {
            AnnotatedMember accessor = property.getAccessor();
            if (property.isTypeId()) {
                if (accessor == null) continue;
                builder.setTypeId(accessor);
                continue;
            }
            AnnotationIntrospector.ReferenceProperty refType = property.findReferenceType();
            if (refType != null && refType.isBackReference()) continue;
            if (accessor instanceof AnnotatedMethod) {
                result.add(this._constructWriter(prov, property, pb, staticTyping, (AnnotatedMethod)accessor));
                continue;
            }
            result.add(this._constructWriter(prov, property, pb, staticTyping, (AnnotatedField)accessor));
        }
        return result;
    }

    protected List<BeanPropertyWriter> filterBeanProperties(SerializationConfig config, BeanDescription beanDesc, List<BeanPropertyWriter> props) {
        JsonIgnoreProperties.Value ignorals = config.getDefaultPropertyIgnorals(beanDesc.getBeanClass(), beanDesc.getClassInfo());
        Set ignored = null;
        if (ignorals != null) {
            ignored = ignorals.findIgnoredForSerialization();
        }
        JsonIncludeProperties.Value inclusions = config.getDefaultPropertyInclusions(beanDesc.getBeanClass(), beanDesc.getClassInfo());
        Set included = null;
        if (inclusions != null) {
            included = inclusions.getIncluded();
        }
        if (included != null || ignored != null && !ignored.isEmpty()) {
            Iterator<BeanPropertyWriter> it = props.iterator();
            while (it.hasNext()) {
                if (!IgnorePropertiesUtil.shouldIgnore(it.next().getName(), ignored, included)) continue;
                it.remove();
            }
        }
        return props;
    }

    protected List<BeanPropertyWriter> filterUnwantedJDKProperties(SerializationConfig config, BeanDescription beanDesc, List<BeanPropertyWriter> props) {
        BeanPropertyWriter prop;
        AnnotatedMember m;
        if (beanDesc.getType().isTypeOrSubTypeOf(CharSequence.class) && props.size() == 1 && (m = (prop = props.get(0)).getMember()) instanceof AnnotatedMethod && "isEmpty".equals(m.getName()) && m.getDeclaringClass() == CharSequence.class) {
            props.remove(0);
        }
        return props;
    }

    protected void processViews(SerializationConfig config, BeanSerializerBuilder builder) {
        List<BeanPropertyWriter> props = builder.getProperties();
        boolean includeByDefault = config.isEnabled(MapperFeature.DEFAULT_VIEW_INCLUSION);
        int propCount = props.size();
        int viewsFound = 0;
        BeanPropertyWriter[] filtered = new BeanPropertyWriter[propCount];
        for (int i = 0; i < propCount; ++i) {
            BeanPropertyWriter bpw = props.get(i);
            Class<?>[] views = bpw.getViews();
            if (views == null || views.length == 0) {
                if (!includeByDefault) continue;
                filtered[i] = bpw;
                continue;
            }
            ++viewsFound;
            filtered[i] = this.constructFilteredBeanWriter(bpw, views);
        }
        if (includeByDefault && viewsFound == 0) {
            return;
        }
        builder.setFilteredProperties(filtered);
    }

    protected void removeIgnorableTypes(SerializationConfig config, BeanDescription beanDesc, List<BeanPropertyDefinition> properties) {
        AnnotationIntrospector intr = config.getAnnotationIntrospector();
        HashMap ignores = new HashMap();
        Iterator<BeanPropertyDefinition> it = properties.iterator();
        while (it.hasNext()) {
            BeanPropertyDefinition property = it.next();
            AnnotatedMember accessor = property.getAccessor();
            if (accessor == null) {
                it.remove();
                continue;
            }
            Class<?> type = property.getRawPrimaryType();
            Boolean result = (Boolean)ignores.get(type);
            if (result == null) {
                BeanDescription desc;
                AnnotatedClass ac;
                result = config.getConfigOverride(type).getIsIgnoredType();
                if (result == null && (result = intr.isIgnorableType(ac = (desc = config.introspectClassAnnotations(type)).getClassInfo())) == null) {
                    result = Boolean.FALSE;
                }
                ignores.put(type, result);
            }
            if (!result.booleanValue()) continue;
            it.remove();
        }
    }

    protected void removeSetterlessGetters(SerializationConfig config, BeanDescription beanDesc, List<BeanPropertyDefinition> properties) {
        properties.removeIf(property -> !property.couldDeserialize() && !property.isExplicitlyIncluded());
    }

    protected List<BeanPropertyWriter> removeOverlappingTypeIds(SerializerProvider prov, BeanDescription beanDesc, BeanSerializerBuilder builder, List<BeanPropertyWriter> props) {
        int end = props.size();
        block0: for (int i = 0; i < end; ++i) {
            BeanPropertyWriter bpw = props.get(i);
            TypeSerializer td = bpw.getTypeSerializer();
            if (td == null || td.getTypeInclusion() != JsonTypeInfo.As.EXTERNAL_PROPERTY) continue;
            String n = td.getPropertyName();
            PropertyName typePropName = PropertyName.construct(n);
            for (BeanPropertyWriter w2 : props) {
                if (w2 == bpw || !w2.wouldConflictWithName(typePropName)) continue;
                bpw.assignTypeSerializer(null);
                continue block0;
            }
        }
        return props;
    }

    protected BeanPropertyWriter _constructWriter(SerializerProvider prov, BeanPropertyDefinition propDef, PropertyBuilder pb, boolean staticTyping, AnnotatedMember accessor) throws JsonMappingException {
        PropertyName name = propDef.getFullName();
        JavaType type = accessor.getType();
        BeanProperty.Std property = new BeanProperty.Std(name, type, propDef.getWrapperName(), accessor, propDef.getMetadata());
        JsonSerializer<Object> annotatedSerializer = this.findSerializerFromAnnotation(prov, accessor);
        if (annotatedSerializer instanceof ResolvableSerializer) {
            ((ResolvableSerializer)((Object)annotatedSerializer)).resolve(prov);
        }
        annotatedSerializer = prov.handlePrimaryContextualization(annotatedSerializer, property);
        TypeSerializer contentTypeSer = null;
        if (type.isContainerType() || type.isReferenceType()) {
            contentTypeSer = this.findPropertyContentTypeSerializer(type, prov.getConfig(), accessor);
        }
        TypeSerializer typeSer = this.findPropertyTypeSerializer(type, prov.getConfig(), accessor);
        return pb.buildWriter(prov, propDef, type, annotatedSerializer, typeSer, contentTypeSer, accessor, staticTyping);
    }

    protected JsonSerializer<?> _findUnsupportedTypeSerializer(SerializerProvider ctxt, JavaType type, BeanDescription beanDesc) throws JsonMappingException {
        String errorMsg = BeanUtil.checkUnsupportedType(type);
        if (errorMsg != null && ctxt.getConfig().findMixInClassFor(type.getRawClass()) == null) {
            return new UnsupportedTypeSerializer(type, errorMsg);
        }
        return null;
    }

    protected boolean _isUnserializableJacksonType(SerializerProvider ctxt, JavaType type) {
        Class<?> raw = type.getRawClass();
        return ObjectMapper.class.isAssignableFrom(raw) || ObjectReader.class.isAssignableFrom(raw) || ObjectWriter.class.isAssignableFrom(raw) || DatabindContext.class.isAssignableFrom(raw) || TokenStreamFactory.class.isAssignableFrom(raw) || JsonParser.class.isAssignableFrom(raw) || JsonGenerator.class.isAssignableFrom(raw);
    }
}


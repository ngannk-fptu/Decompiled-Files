/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.ser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.BeanProperty;
import org.codehaus.jackson.map.BeanPropertyDefinition;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.SerializerFactory;
import org.codehaus.jackson.map.Serializers;
import org.codehaus.jackson.map.TypeSerializer;
import org.codehaus.jackson.map.introspect.AnnotatedClass;
import org.codehaus.jackson.map.introspect.AnnotatedField;
import org.codehaus.jackson.map.introspect.AnnotatedMember;
import org.codehaus.jackson.map.introspect.AnnotatedMethod;
import org.codehaus.jackson.map.introspect.BasicBeanDescription;
import org.codehaus.jackson.map.jsontype.NamedType;
import org.codehaus.jackson.map.jsontype.TypeResolverBuilder;
import org.codehaus.jackson.map.ser.AnyGetterWriter;
import org.codehaus.jackson.map.ser.BasicSerializerFactory;
import org.codehaus.jackson.map.ser.BeanPropertyWriter;
import org.codehaus.jackson.map.ser.BeanSerializerBuilder;
import org.codehaus.jackson.map.ser.BeanSerializerModifier;
import org.codehaus.jackson.map.ser.FilteredBeanPropertyWriter;
import org.codehaus.jackson.map.ser.PropertyBuilder;
import org.codehaus.jackson.map.ser.std.MapSerializer;
import org.codehaus.jackson.map.type.TypeBindings;
import org.codehaus.jackson.map.util.ArrayBuilders;
import org.codehaus.jackson.map.util.ClassUtil;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class BeanSerializerFactory
extends BasicSerializerFactory {
    public static final BeanSerializerFactory instance = new BeanSerializerFactory(null);
    protected final SerializerFactory.Config _factoryConfig;

    protected BeanSerializerFactory(SerializerFactory.Config config) {
        if (config == null) {
            config = new ConfigImpl();
        }
        this._factoryConfig = config;
    }

    @Override
    public SerializerFactory.Config getConfig() {
        return this._factoryConfig;
    }

    @Override
    public SerializerFactory withConfig(SerializerFactory.Config config) {
        if (this._factoryConfig == config) {
            return this;
        }
        if (this.getClass() != BeanSerializerFactory.class) {
            throw new IllegalStateException("Subtype of BeanSerializerFactory (" + this.getClass().getName() + ") has not properly overridden method 'withAdditionalSerializers': can not instantiate subtype with additional serializer definitions");
        }
        return new BeanSerializerFactory(config);
    }

    @Override
    protected Iterable<Serializers> customSerializers() {
        return this._factoryConfig.serializers();
    }

    @Override
    public JsonSerializer<Object> createSerializer(SerializationConfig config, JavaType origType, BeanProperty property) throws JsonMappingException {
        boolean staticTyping;
        BasicBeanDescription beanDesc = (BasicBeanDescription)config.introspect(origType);
        JsonSerializer<Object> ser = this.findSerializerFromAnnotation(config, beanDesc.getClassInfo(), property);
        if (ser != null) {
            return ser;
        }
        JavaType type = this.modifyTypeByAnnotation(config, beanDesc.getClassInfo(), origType);
        boolean bl = staticTyping = type != origType;
        if (type != origType && type.getRawClass() != origType.getRawClass()) {
            beanDesc = (BasicBeanDescription)config.introspect(type);
        }
        if (origType.isContainerType()) {
            return this.buildContainerSerializer(config, type, beanDesc, property, staticTyping);
        }
        for (Serializers serializers : this._factoryConfig.serializers()) {
            ser = serializers.findSerializer(config, type, beanDesc, property);
            if (ser == null) continue;
            return ser;
        }
        ser = this.findSerializerByLookup(type, config, beanDesc, property, staticTyping);
        if (ser == null && (ser = this.findSerializerByPrimaryType(type, config, beanDesc, property, staticTyping)) == null && (ser = this.findBeanSerializer(config, type, beanDesc, property)) == null) {
            ser = this.findSerializerByAddonType(config, type, beanDesc, property, staticTyping);
        }
        return ser;
    }

    @Override
    public JsonSerializer<Object> createKeySerializer(SerializationConfig config, JavaType type, BeanProperty property) {
        Serializers serializers;
        if (!this._factoryConfig.hasKeySerializers()) {
            return null;
        }
        BasicBeanDescription beanDesc = (BasicBeanDescription)config.introspectClassAnnotations(type.getRawClass());
        JsonSerializer<?> ser = null;
        Iterator<Serializers> iterator = this._factoryConfig.keySerializers().iterator();
        while (iterator.hasNext() && (ser = (serializers = iterator.next()).findSerializer(config, type, beanDesc, property)) == null) {
        }
        return ser;
    }

    public JsonSerializer<Object> findBeanSerializer(SerializationConfig config, JavaType type, BasicBeanDescription beanDesc, BeanProperty property) throws JsonMappingException {
        if (!this.isPotentialBeanType(type.getRawClass())) {
            return null;
        }
        JsonSerializer<Object> serializer = this.constructBeanSerializer(config, beanDesc, property);
        if (this._factoryConfig.hasSerializerModifiers()) {
            for (BeanSerializerModifier mod : this._factoryConfig.serializerModifiers()) {
                serializer = mod.modifySerializer(config, beanDesc, serializer);
            }
        }
        return serializer;
    }

    public TypeSerializer findPropertyTypeSerializer(JavaType baseType, SerializationConfig config, AnnotatedMember accessor, BeanProperty property) throws JsonMappingException {
        AnnotationIntrospector ai = config.getAnnotationIntrospector();
        TypeResolverBuilder<?> b = ai.findPropertyTypeResolver(config, accessor, baseType);
        if (b == null) {
            return this.createTypeSerializer(config, baseType, property);
        }
        Collection<NamedType> subtypes = config.getSubtypeResolver().collectAndResolveSubtypes(accessor, config, ai);
        return b.buildTypeSerializer(config, baseType, subtypes, property);
    }

    public TypeSerializer findPropertyContentTypeSerializer(JavaType containerType, SerializationConfig config, AnnotatedMember accessor, BeanProperty property) throws JsonMappingException {
        JavaType contentType = containerType.getContentType();
        AnnotationIntrospector ai = config.getAnnotationIntrospector();
        TypeResolverBuilder<?> b = ai.findPropertyContentTypeResolver(config, accessor, containerType);
        if (b == null) {
            return this.createTypeSerializer(config, contentType, property);
        }
        Collection<NamedType> subtypes = config.getSubtypeResolver().collectAndResolveSubtypes(accessor, config, ai);
        return b.buildTypeSerializer(config, contentType, subtypes, property);
    }

    protected JsonSerializer<Object> constructBeanSerializer(SerializationConfig config, BasicBeanDescription beanDesc, BeanProperty property) throws JsonMappingException {
        JsonSerializer<Object> ser;
        if (beanDesc.getBeanClass() == Object.class) {
            throw new IllegalArgumentException("Can not create bean serializer for Object.class");
        }
        BeanSerializerBuilder builder = this.constructBeanSerializerBuilder(beanDesc);
        List<BeanPropertyWriter> props = this.findBeanProperties(config, beanDesc);
        if (props == null) {
            props = new ArrayList<BeanPropertyWriter>();
        }
        if (this._factoryConfig.hasSerializerModifiers()) {
            for (BeanSerializerModifier mod : this._factoryConfig.serializerModifiers()) {
                props = mod.changeProperties(config, beanDesc, props);
            }
        }
        props = this.filterBeanProperties(config, beanDesc, props);
        props = this.sortBeanProperties(config, beanDesc, props);
        if (this._factoryConfig.hasSerializerModifiers()) {
            for (BeanSerializerModifier mod : this._factoryConfig.serializerModifiers()) {
                props = mod.orderProperties(config, beanDesc, props);
            }
        }
        builder.setProperties(props);
        builder.setFilterId(this.findFilterId(config, beanDesc));
        AnnotatedMethod anyGetter = beanDesc.findAnyGetter();
        if (anyGetter != null) {
            if (config.isEnabled(SerializationConfig.Feature.CAN_OVERRIDE_ACCESS_MODIFIERS)) {
                anyGetter.fixAccess();
            }
            JavaType type = anyGetter.getType(beanDesc.bindingsForBeanType());
            boolean staticTyping = config.isEnabled(SerializationConfig.Feature.USE_STATIC_TYPING);
            JavaType valueType = type.getContentType();
            TypeSerializer typeSer = this.createTypeSerializer(config, valueType, property);
            MapSerializer mapSer = MapSerializer.construct(null, type, staticTyping, typeSer, property, null, null);
            builder.setAnyGetter(new AnyGetterWriter(anyGetter, mapSer));
        }
        this.processViews(config, builder);
        if (this._factoryConfig.hasSerializerModifiers()) {
            for (BeanSerializerModifier mod : this._factoryConfig.serializerModifiers()) {
                builder = mod.updateBuilder(config, beanDesc, builder);
            }
        }
        if ((ser = builder.build()) == null && beanDesc.hasKnownClassAnnotations()) {
            return builder.createDummy();
        }
        return ser;
    }

    protected BeanPropertyWriter constructFilteredBeanWriter(BeanPropertyWriter writer, Class<?>[] inViews) {
        return FilteredBeanPropertyWriter.constructViewBased(writer, inViews);
    }

    protected PropertyBuilder constructPropertyBuilder(SerializationConfig config, BasicBeanDescription beanDesc) {
        return new PropertyBuilder(config, beanDesc);
    }

    protected BeanSerializerBuilder constructBeanSerializerBuilder(BasicBeanDescription beanDesc) {
        return new BeanSerializerBuilder(beanDesc);
    }

    protected Object findFilterId(SerializationConfig config, BasicBeanDescription beanDesc) {
        return config.getAnnotationIntrospector().findFilterId(beanDesc.getClassInfo());
    }

    protected boolean isPotentialBeanType(Class<?> type) {
        return ClassUtil.canBeABeanType(type) == null && !ClassUtil.isProxyType(type);
    }

    protected List<BeanPropertyWriter> findBeanProperties(SerializationConfig config, BasicBeanDescription beanDesc) throws JsonMappingException {
        List<BeanPropertyDefinition> properties = beanDesc.findProperties();
        AnnotationIntrospector intr = config.getAnnotationIntrospector();
        this.removeIgnorableTypes(config, beanDesc, properties);
        if (config.isEnabled(SerializationConfig.Feature.REQUIRE_SETTERS_FOR_GETTERS)) {
            this.removeSetterlessGetters(config, beanDesc, properties);
        }
        if (properties.isEmpty()) {
            return null;
        }
        boolean staticTyping = this.usesStaticTyping(config, beanDesc, null, null);
        PropertyBuilder pb = this.constructPropertyBuilder(config, beanDesc);
        ArrayList<BeanPropertyWriter> result = new ArrayList<BeanPropertyWriter>(properties.size());
        TypeBindings typeBind = beanDesc.bindingsForBeanType();
        for (BeanPropertyDefinition property : properties) {
            AnnotatedMember accessor = property.getAccessor();
            AnnotationIntrospector.ReferenceProperty prop = intr.findReferenceType(accessor);
            if (prop != null && prop.isBackReference()) continue;
            String name = property.getName();
            if (accessor instanceof AnnotatedMethod) {
                result.add(this._constructWriter(config, typeBind, pb, staticTyping, name, (AnnotatedMethod)accessor));
                continue;
            }
            result.add(this._constructWriter(config, typeBind, pb, staticTyping, name, (AnnotatedField)accessor));
        }
        return result;
    }

    protected List<BeanPropertyWriter> filterBeanProperties(SerializationConfig config, BasicBeanDescription beanDesc, List<BeanPropertyWriter> props) {
        AnnotatedClass ac;
        AnnotationIntrospector intr = config.getAnnotationIntrospector();
        String[] ignored = intr.findPropertiesToIgnore(ac = beanDesc.getClassInfo());
        if (ignored != null && ignored.length > 0) {
            HashSet<String> ignoredSet = ArrayBuilders.arrayToSet(ignored);
            Iterator<BeanPropertyWriter> it = props.iterator();
            while (it.hasNext()) {
                if (!ignoredSet.contains(it.next().getName())) continue;
                it.remove();
            }
        }
        return props;
    }

    @Deprecated
    protected List<BeanPropertyWriter> sortBeanProperties(SerializationConfig config, BasicBeanDescription beanDesc, List<BeanPropertyWriter> props) {
        return props;
    }

    protected void processViews(SerializationConfig config, BeanSerializerBuilder builder) {
        List<BeanPropertyWriter> props = builder.getProperties();
        boolean includeByDefault = config.isEnabled(SerializationConfig.Feature.DEFAULT_VIEW_INCLUSION);
        int propCount = props.size();
        int viewsFound = 0;
        BeanPropertyWriter[] filtered = new BeanPropertyWriter[propCount];
        for (int i = 0; i < propCount; ++i) {
            BeanPropertyWriter bpw = props.get(i);
            Class<?>[] views = bpw.getViews();
            if (views == null) {
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

    protected void removeIgnorableTypes(SerializationConfig config, BasicBeanDescription beanDesc, List<BeanPropertyDefinition> properties) {
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
            Class<?> type = accessor.getRawType();
            Boolean result = (Boolean)ignores.get(type);
            if (result == null) {
                BasicBeanDescription desc = (BasicBeanDescription)config.introspectClassAnnotations(type);
                AnnotatedClass ac = desc.getClassInfo();
                result = intr.isIgnorableType(ac);
                if (result == null) {
                    result = Boolean.FALSE;
                }
                ignores.put(type, result);
            }
            if (!result.booleanValue()) continue;
            it.remove();
        }
    }

    protected void removeSetterlessGetters(SerializationConfig config, BasicBeanDescription beanDesc, List<BeanPropertyDefinition> properties) {
        Iterator<BeanPropertyDefinition> it = properties.iterator();
        while (it.hasNext()) {
            BeanPropertyDefinition property = it.next();
            if (property.couldDeserialize() || property.isExplicitlyIncluded()) continue;
            it.remove();
        }
    }

    protected BeanPropertyWriter _constructWriter(SerializationConfig config, TypeBindings typeContext, PropertyBuilder pb, boolean staticTyping, String name, AnnotatedMember accessor) throws JsonMappingException {
        if (config.isEnabled(SerializationConfig.Feature.CAN_OVERRIDE_ACCESS_MODIFIERS)) {
            accessor.fixAccess();
        }
        JavaType type = accessor.getType(typeContext);
        BeanProperty.Std property = new BeanProperty.Std(name, type, pb.getClassAnnotations(), accessor);
        JsonSerializer<Object> annotatedSerializer = this.findSerializerFromAnnotation(config, accessor, property);
        TypeSerializer contentTypeSer = null;
        if (ClassUtil.isCollectionMapOrArray(type.getRawClass())) {
            contentTypeSer = this.findPropertyContentTypeSerializer(type, config, accessor, property);
        }
        TypeSerializer typeSer = this.findPropertyTypeSerializer(type, config, accessor, property);
        BeanPropertyWriter pbw = pb.buildWriter(name, type, annotatedSerializer, typeSer, contentTypeSer, accessor, staticTyping);
        AnnotationIntrospector intr = config.getAnnotationIntrospector();
        pbw.setViews(intr.findSerializationViews(accessor));
        return pbw;
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class ConfigImpl
    extends SerializerFactory.Config {
        protected static final Serializers[] NO_SERIALIZERS = new Serializers[0];
        protected static final BeanSerializerModifier[] NO_MODIFIERS = new BeanSerializerModifier[0];
        protected final Serializers[] _additionalSerializers;
        protected final Serializers[] _additionalKeySerializers;
        protected final BeanSerializerModifier[] _modifiers;

        public ConfigImpl() {
            this(null, null, null);
        }

        protected ConfigImpl(Serializers[] allAdditionalSerializers, Serializers[] allAdditionalKeySerializers, BeanSerializerModifier[] modifiers) {
            this._additionalSerializers = allAdditionalSerializers == null ? NO_SERIALIZERS : allAdditionalSerializers;
            this._additionalKeySerializers = allAdditionalKeySerializers == null ? NO_SERIALIZERS : allAdditionalKeySerializers;
            this._modifiers = modifiers == null ? NO_MODIFIERS : modifiers;
        }

        @Override
        public SerializerFactory.Config withAdditionalSerializers(Serializers additional) {
            if (additional == null) {
                throw new IllegalArgumentException("Can not pass null Serializers");
            }
            Serializers[] all = ArrayBuilders.insertInListNoDup(this._additionalSerializers, additional);
            return new ConfigImpl(all, this._additionalKeySerializers, this._modifiers);
        }

        @Override
        public SerializerFactory.Config withAdditionalKeySerializers(Serializers additional) {
            if (additional == null) {
                throw new IllegalArgumentException("Can not pass null Serializers");
            }
            Serializers[] all = ArrayBuilders.insertInListNoDup(this._additionalKeySerializers, additional);
            return new ConfigImpl(this._additionalSerializers, all, this._modifiers);
        }

        @Override
        public SerializerFactory.Config withSerializerModifier(BeanSerializerModifier modifier) {
            if (modifier == null) {
                throw new IllegalArgumentException("Can not pass null modifier");
            }
            BeanSerializerModifier[] modifiers = ArrayBuilders.insertInListNoDup(this._modifiers, modifier);
            return new ConfigImpl(this._additionalSerializers, this._additionalKeySerializers, modifiers);
        }

        @Override
        public boolean hasSerializers() {
            return this._additionalSerializers.length > 0;
        }

        @Override
        public boolean hasKeySerializers() {
            return this._additionalKeySerializers.length > 0;
        }

        @Override
        public boolean hasSerializerModifiers() {
            return this._modifiers.length > 0;
        }

        @Override
        public Iterable<Serializers> serializers() {
            return ArrayBuilders.arrayAsIterable(this._additionalSerializers);
        }

        @Override
        public Iterable<Serializers> keySerializers() {
            return ArrayBuilders.arrayAsIterable(this._additionalKeySerializers);
        }

        @Override
        public Iterable<BeanSerializerModifier> serializerModifiers() {
            return ArrayBuilders.arrayAsIterable(this._modifiers);
        }
    }
}


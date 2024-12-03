/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.deser;

import java.lang.reflect.Member;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.AbstractTypeResolver;
import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.BeanProperty;
import org.codehaus.jackson.map.BeanPropertyDefinition;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.DeserializerFactory;
import org.codehaus.jackson.map.DeserializerProvider;
import org.codehaus.jackson.map.Deserializers;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.KeyDeserializer;
import org.codehaus.jackson.map.KeyDeserializers;
import org.codehaus.jackson.map.TypeDeserializer;
import org.codehaus.jackson.map.deser.AbstractDeserializer;
import org.codehaus.jackson.map.deser.BasicDeserializerFactory;
import org.codehaus.jackson.map.deser.BeanDeserializer;
import org.codehaus.jackson.map.deser.BeanDeserializerBuilder;
import org.codehaus.jackson.map.deser.BeanDeserializerModifier;
import org.codehaus.jackson.map.deser.SettableAnyProperty;
import org.codehaus.jackson.map.deser.SettableBeanProperty;
import org.codehaus.jackson.map.deser.ValueInstantiator;
import org.codehaus.jackson.map.deser.ValueInstantiators;
import org.codehaus.jackson.map.deser.impl.CreatorCollector;
import org.codehaus.jackson.map.deser.impl.CreatorProperty;
import org.codehaus.jackson.map.deser.std.StdKeyDeserializers;
import org.codehaus.jackson.map.deser.std.ThrowableDeserializer;
import org.codehaus.jackson.map.introspect.AnnotatedClass;
import org.codehaus.jackson.map.introspect.AnnotatedConstructor;
import org.codehaus.jackson.map.introspect.AnnotatedField;
import org.codehaus.jackson.map.introspect.AnnotatedMember;
import org.codehaus.jackson.map.introspect.AnnotatedMethod;
import org.codehaus.jackson.map.introspect.AnnotatedParameter;
import org.codehaus.jackson.map.introspect.BasicBeanDescription;
import org.codehaus.jackson.map.introspect.VisibilityChecker;
import org.codehaus.jackson.map.jsontype.impl.SubTypeValidator;
import org.codehaus.jackson.map.type.ArrayType;
import org.codehaus.jackson.map.type.CollectionLikeType;
import org.codehaus.jackson.map.type.CollectionType;
import org.codehaus.jackson.map.type.MapLikeType;
import org.codehaus.jackson.map.type.MapType;
import org.codehaus.jackson.map.util.ArrayBuilders;
import org.codehaus.jackson.map.util.ClassUtil;
import org.codehaus.jackson.map.util.EnumResolver;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class BeanDeserializerFactory
extends BasicDeserializerFactory {
    private static final Class<?>[] INIT_CAUSE_PARAMS = new Class[]{Throwable.class};
    protected static final Set<String> DEFAULT_NO_DESER_CLASS_NAMES;
    protected Set<String> _cfgIllegalClassNames = DEFAULT_NO_DESER_CLASS_NAMES;
    public static final BeanDeserializerFactory instance;
    protected final DeserializerFactory.Config _factoryConfig;
    protected SubTypeValidator _subtypeValidator = SubTypeValidator.instance();

    @Deprecated
    public BeanDeserializerFactory() {
        this(null);
    }

    public BeanDeserializerFactory(DeserializerFactory.Config config) {
        if (config == null) {
            config = new ConfigImpl();
        }
        this._factoryConfig = config;
    }

    @Override
    public final DeserializerFactory.Config getConfig() {
        return this._factoryConfig;
    }

    @Override
    public DeserializerFactory withConfig(DeserializerFactory.Config config) {
        if (this._factoryConfig == config) {
            return this;
        }
        if (this.getClass() != BeanDeserializerFactory.class) {
            throw new IllegalStateException("Subtype of BeanDeserializerFactory (" + this.getClass().getName() + ") has not properly overridden method 'withAdditionalDeserializers': can not instantiate subtype with additional deserializer definitions");
        }
        return new BeanDeserializerFactory(config);
    }

    @Override
    public KeyDeserializer createKeyDeserializer(DeserializationConfig config, JavaType type, BeanProperty property) throws JsonMappingException {
        Class<?> raw;
        if (this._factoryConfig.hasKeyDeserializers()) {
            BasicBeanDescription beanDesc = (BasicBeanDescription)config.introspectClassAnnotations(type.getRawClass());
            for (KeyDeserializers d : this._factoryConfig.keyDeserializers()) {
                KeyDeserializer deser = d.findKeyDeserializer(type, config, beanDesc, property);
                if (deser == null) continue;
                return deser;
            }
        }
        if ((raw = type.getRawClass()) == String.class || raw == Object.class) {
            return StdKeyDeserializers.constructStringKeyDeserializer(config, type);
        }
        KeyDeserializer kdes = (KeyDeserializer)_keyDeserializers.get(type);
        if (kdes != null) {
            return kdes;
        }
        if (type.isEnumType()) {
            return this._createEnumKeyDeserializer(config, type, property);
        }
        kdes = StdKeyDeserializers.findStringBasedKeyDeserializer(config, type);
        return kdes;
    }

    private KeyDeserializer _createEnumKeyDeserializer(DeserializationConfig config, JavaType type, BeanProperty property) throws JsonMappingException {
        BasicBeanDescription beanDesc = (BasicBeanDescription)config.introspect(type);
        Class<?> enumClass = type.getRawClass();
        EnumResolver<?> enumRes = this.constructEnumResolver(enumClass, config);
        for (AnnotatedMethod factory : beanDesc.getFactoryMethods()) {
            Class<?> returnType;
            if (!config.getAnnotationIntrospector().hasCreatorAnnotation(factory)) continue;
            int argCount = factory.getParameterCount();
            if (argCount == 1 && (returnType = factory.getRawType()).isAssignableFrom(enumClass)) {
                if (factory.getParameterType(0) != String.class) {
                    throw new IllegalArgumentException("Parameter #0 type for factory method (" + factory + ") not suitable, must be java.lang.String");
                }
                if (config.canOverrideAccessModifiers()) {
                    ClassUtil.checkAndFixAccess(factory.getMember());
                }
                return StdKeyDeserializers.constructEnumKeyDeserializer(enumRes, factory);
            }
            throw new IllegalArgumentException("Unsuitable method (" + factory + ") decorated with @JsonCreator (for Enum type " + enumClass.getName() + ")");
        }
        return StdKeyDeserializers.constructEnumKeyDeserializer(enumRes);
    }

    @Override
    protected JsonDeserializer<?> _findCustomArrayDeserializer(ArrayType type, DeserializationConfig config, DeserializerProvider provider, BeanProperty property, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
        for (Deserializers d : this._factoryConfig.deserializers()) {
            JsonDeserializer<?> deser = d.findArrayDeserializer(type, config, provider, property, elementTypeDeserializer, elementDeserializer);
            if (deser == null) continue;
            return deser;
        }
        return null;
    }

    @Override
    protected JsonDeserializer<?> _findCustomCollectionDeserializer(CollectionType type, DeserializationConfig config, DeserializerProvider provider, BasicBeanDescription beanDesc, BeanProperty property, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
        for (Deserializers d : this._factoryConfig.deserializers()) {
            JsonDeserializer<?> deser = d.findCollectionDeserializer(type, config, provider, beanDesc, property, elementTypeDeserializer, elementDeserializer);
            if (deser == null) continue;
            return deser;
        }
        return null;
    }

    @Override
    protected JsonDeserializer<?> _findCustomCollectionLikeDeserializer(CollectionLikeType type, DeserializationConfig config, DeserializerProvider provider, BasicBeanDescription beanDesc, BeanProperty property, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
        for (Deserializers d : this._factoryConfig.deserializers()) {
            JsonDeserializer<?> deser = d.findCollectionLikeDeserializer(type, config, provider, beanDesc, property, elementTypeDeserializer, elementDeserializer);
            if (deser == null) continue;
            return deser;
        }
        return null;
    }

    @Override
    protected JsonDeserializer<?> _findCustomEnumDeserializer(Class<?> type, DeserializationConfig config, BasicBeanDescription beanDesc, BeanProperty property) throws JsonMappingException {
        for (Deserializers d : this._factoryConfig.deserializers()) {
            JsonDeserializer<?> deser = d.findEnumDeserializer(type, config, beanDesc, property);
            if (deser == null) continue;
            return deser;
        }
        return null;
    }

    @Override
    protected JsonDeserializer<?> _findCustomMapDeserializer(MapType type, DeserializationConfig config, DeserializerProvider provider, BasicBeanDescription beanDesc, BeanProperty property, KeyDeserializer keyDeserializer, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
        for (Deserializers d : this._factoryConfig.deserializers()) {
            JsonDeserializer<?> deser = d.findMapDeserializer(type, config, provider, beanDesc, property, keyDeserializer, elementTypeDeserializer, elementDeserializer);
            if (deser == null) continue;
            return deser;
        }
        return null;
    }

    @Override
    protected JsonDeserializer<?> _findCustomMapLikeDeserializer(MapLikeType type, DeserializationConfig config, DeserializerProvider provider, BasicBeanDescription beanDesc, BeanProperty property, KeyDeserializer keyDeserializer, TypeDeserializer elementTypeDeserializer, JsonDeserializer<?> elementDeserializer) throws JsonMappingException {
        for (Deserializers d : this._factoryConfig.deserializers()) {
            JsonDeserializer<?> deser = d.findMapLikeDeserializer(type, config, provider, beanDesc, property, keyDeserializer, elementTypeDeserializer, elementDeserializer);
            if (deser == null) continue;
            return deser;
        }
        return null;
    }

    @Override
    protected JsonDeserializer<?> _findCustomTreeNodeDeserializer(Class<? extends JsonNode> type, DeserializationConfig config, BeanProperty property) throws JsonMappingException {
        for (Deserializers d : this._factoryConfig.deserializers()) {
            JsonDeserializer<?> deser = d.findTreeNodeDeserializer(type, config, property);
            if (deser == null) continue;
            return deser;
        }
        return null;
    }

    protected JsonDeserializer<Object> _findCustomBeanDeserializer(JavaType type, DeserializationConfig config, DeserializerProvider provider, BasicBeanDescription beanDesc, BeanProperty property) throws JsonMappingException {
        for (Deserializers d : this._factoryConfig.deserializers()) {
            JsonDeserializer<Object> deser = d.findBeanDeserializer(type, config, provider, beanDesc, property);
            if (deser == null) continue;
            return deser;
        }
        return null;
    }

    @Override
    public JavaType mapAbstractType(DeserializationConfig config, JavaType type) throws JsonMappingException {
        JavaType next;
        while ((next = this._mapAbstractType2(config, type)) != null) {
            Class<?> nextCls;
            Class<?> prevCls = type.getRawClass();
            if (prevCls == (nextCls = next.getRawClass()) || !prevCls.isAssignableFrom(nextCls)) {
                throw new IllegalArgumentException("Invalid abstract type resolution from " + type + " to " + next + ": latter is not a subtype of former");
            }
            type = next;
        }
        return type;
    }

    @Override
    public ValueInstantiator findValueInstantiator(DeserializationConfig config, BasicBeanDescription beanDesc) throws JsonMappingException {
        ValueInstantiator instantiator;
        AnnotatedClass ac = beanDesc.getClassInfo();
        Object instDef = config.getAnnotationIntrospector().findValueInstantiator(ac);
        if (instDef != null) {
            if (instDef instanceof ValueInstantiator) {
                instantiator = (ValueInstantiator)instDef;
            } else {
                if (!(instDef instanceof Class)) {
                    throw new IllegalStateException("Invalid value instantiator returned for type " + beanDesc + ": neither a Class nor ValueInstantiator");
                }
                Class cls = (Class)instDef;
                if (!ValueInstantiator.class.isAssignableFrom(cls)) {
                    throw new IllegalStateException("Invalid instantiator Class<?> returned for type " + beanDesc + ": " + cls.getName() + " not a ValueInstantiator");
                }
                Class instClass = cls;
                instantiator = config.valueInstantiatorInstance(ac, instClass);
            }
        } else {
            instantiator = this.constructDefaultValueInstantiator(config, beanDesc);
        }
        if (this._factoryConfig.hasValueInstantiators()) {
            for (ValueInstantiators insts : this._factoryConfig.valueInstantiators()) {
                instantiator = insts.findValueInstantiator(config, beanDesc, instantiator);
                if (instantiator != null) continue;
                throw new JsonMappingException("Broken registered ValueInstantiators (of type " + insts.getClass().getName() + "): returned null ValueInstantiator");
            }
        }
        return instantiator;
    }

    @Override
    public JsonDeserializer<Object> createBeanDeserializer(DeserializationConfig config, DeserializerProvider p, JavaType type, BeanProperty property) throws JsonMappingException {
        JavaType concreteType;
        JsonDeserializer<Object> custom;
        BasicBeanDescription beanDesc;
        JsonDeserializer<Object> ad;
        if (type.isAbstract()) {
            type = this.mapAbstractType(config, type);
        }
        if ((ad = this.findDeserializerFromAnnotation(config, (beanDesc = (BasicBeanDescription)config.introspect(type)).getClassInfo(), property)) != null) {
            return ad;
        }
        JavaType newType = this.modifyTypeByAnnotation(config, beanDesc.getClassInfo(), type, null);
        if (newType.getRawClass() != type.getRawClass()) {
            type = newType;
            beanDesc = (BasicBeanDescription)config.introspect(type);
        }
        if ((custom = this._findCustomBeanDeserializer(type, config, p, beanDesc, property)) != null) {
            return custom;
        }
        if (type.isThrowable()) {
            return this.buildThrowableDeserializer(config, type, beanDesc, property);
        }
        if (type.isAbstract() && (concreteType = this.materializeAbstractType(config, beanDesc)) != null) {
            beanDesc = (BasicBeanDescription)config.introspect(concreteType);
            return this.buildBeanDeserializer(config, concreteType, beanDesc, property);
        }
        JsonDeserializer<Object> deser = this.findStdBeanDeserializer(config, p, type, property);
        if (deser != null) {
            return deser;
        }
        if (!this.isPotentialBeanType(type.getRawClass())) {
            return null;
        }
        this.checkIllegalTypes(type);
        return this.buildBeanDeserializer(config, type, beanDesc, property);
    }

    protected JavaType _mapAbstractType2(DeserializationConfig config, JavaType type) throws JsonMappingException {
        Class<?> currClass = type.getRawClass();
        if (this._factoryConfig.hasAbstractTypeResolvers()) {
            for (AbstractTypeResolver resolver : this._factoryConfig.abstractTypeResolvers()) {
                JavaType concrete = resolver.findTypeMapping(config, type);
                if (concrete == null || concrete.getRawClass() == currClass) continue;
                return concrete;
            }
        }
        return null;
    }

    protected JavaType materializeAbstractType(DeserializationConfig config, BasicBeanDescription beanDesc) throws JsonMappingException {
        JavaType abstractType = beanDesc.getType();
        for (AbstractTypeResolver r : this._factoryConfig.abstractTypeResolvers()) {
            JavaType concrete = r.resolveAbstractType(config, abstractType);
            if (concrete == null) continue;
            return concrete;
        }
        return null;
    }

    public JsonDeserializer<Object> buildBeanDeserializer(DeserializationConfig config, JavaType type, BasicBeanDescription beanDesc, BeanProperty property) throws JsonMappingException {
        ValueInstantiator valueInstantiator = this.findValueInstantiator(config, beanDesc);
        if (type.isAbstract() && !valueInstantiator.canInstantiate()) {
            return new AbstractDeserializer(type);
        }
        BeanDeserializerBuilder builder = this.constructBeanDeserializerBuilder(beanDesc);
        builder.setValueInstantiator(valueInstantiator);
        this.addBeanProps(config, beanDesc, builder);
        this.addReferenceProperties(config, beanDesc, builder);
        this.addInjectables(config, beanDesc, builder);
        if (this._factoryConfig.hasDeserializerModifiers()) {
            for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
                builder = mod.updateBuilder(config, beanDesc, builder);
            }
        }
        JsonDeserializer<Object> deserializer = builder.build(property);
        if (this._factoryConfig.hasDeserializerModifiers()) {
            for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
                deserializer = mod.modifyDeserializer(config, beanDesc, deserializer);
            }
        }
        return deserializer;
    }

    public JsonDeserializer<Object> buildThrowableDeserializer(DeserializationConfig config, JavaType type, BasicBeanDescription beanDesc, BeanProperty property) throws JsonMappingException {
        JsonDeserializer deserializer;
        Object prop;
        BeanDeserializerBuilder builder = this.constructBeanDeserializerBuilder(beanDesc);
        builder.setValueInstantiator(this.findValueInstantiator(config, beanDesc));
        this.addBeanProps(config, beanDesc, builder);
        AnnotatedMethod am = beanDesc.findMethod("initCause", INIT_CAUSE_PARAMS);
        if (am != null && (prop = this.constructSettableProperty(config, beanDesc, "cause", am)) != null) {
            builder.addOrReplaceProperty((SettableBeanProperty)prop, true);
        }
        builder.addIgnorable("localizedMessage");
        builder.addIgnorable("message");
        builder.addIgnorable("suppressed");
        if (this._factoryConfig.hasDeserializerModifiers()) {
            for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
                builder = mod.updateBuilder(config, beanDesc, builder);
            }
        }
        if ((deserializer = builder.build(property)) instanceof BeanDeserializer) {
            deserializer = new ThrowableDeserializer((BeanDeserializer)deserializer);
        }
        if (this._factoryConfig.hasDeserializerModifiers()) {
            for (BeanDeserializerModifier mod : this._factoryConfig.deserializerModifiers()) {
                deserializer = mod.modifyDeserializer(config, beanDesc, deserializer);
            }
        }
        return deserializer;
    }

    protected BeanDeserializerBuilder constructBeanDeserializerBuilder(BasicBeanDescription beanDesc) {
        return new BeanDeserializerBuilder(beanDesc);
    }

    protected ValueInstantiator constructDefaultValueInstantiator(DeserializationConfig config, BasicBeanDescription beanDesc) throws JsonMappingException {
        AnnotatedConstructor defaultCtor;
        boolean fixAccess = config.isEnabled(DeserializationConfig.Feature.CAN_OVERRIDE_ACCESS_MODIFIERS);
        CreatorCollector creators = new CreatorCollector(beanDesc, fixAccess);
        AnnotationIntrospector intr = config.getAnnotationIntrospector();
        if (beanDesc.getType().isConcrete() && (defaultCtor = beanDesc.findDefaultConstructor()) != null) {
            if (fixAccess) {
                ClassUtil.checkAndFixAccess((Member)((Object)defaultCtor.getAnnotated()));
            }
            creators.setDefaultConstructor(defaultCtor);
        }
        VisibilityChecker<?> vchecker = config.getDefaultVisibilityChecker();
        vchecker = config.getAnnotationIntrospector().findAutoDetectVisibility(beanDesc.getClassInfo(), vchecker);
        this._addDeserializerFactoryMethods(config, beanDesc, vchecker, intr, creators);
        this._addDeserializerConstructors(config, beanDesc, vchecker, intr, creators);
        return creators.constructValueInstantiator(config);
    }

    protected void _addDeserializerConstructors(DeserializationConfig config, BasicBeanDescription beanDesc, VisibilityChecker<?> vchecker, AnnotationIntrospector intr, CreatorCollector creators) throws JsonMappingException {
        for (AnnotatedConstructor ctor : beanDesc.getConstructors()) {
            int argCount = ctor.getParameterCount();
            if (argCount < 1) continue;
            boolean isCreator = intr.hasCreatorAnnotation(ctor);
            boolean isVisible = vchecker.isCreatorVisible(ctor);
            if (argCount == 1) {
                this._handleSingleArgumentConstructor(config, beanDesc, vchecker, intr, creators, ctor, isCreator, isVisible);
                continue;
            }
            if (!isCreator && !isVisible) continue;
            boolean annotationFound = false;
            AnnotatedParameter nonAnnotatedParam = null;
            int namedCount = 0;
            int injectCount = 0;
            CreatorProperty[] properties = new CreatorProperty[argCount];
            for (int i = 0; i < argCount; ++i) {
                AnnotatedParameter param = ctor.getParameter(i);
                String name = param == null ? null : intr.findPropertyNameForParam(param);
                Object injectId = intr.findInjectableValueId(param);
                if (name != null && name.length() > 0) {
                    ++namedCount;
                    properties[i] = this.constructCreatorProperty(config, beanDesc, name, i, param, injectId);
                    continue;
                }
                if (injectId != null) {
                    ++injectCount;
                    properties[i] = this.constructCreatorProperty(config, beanDesc, name, i, param, injectId);
                    continue;
                }
                if (nonAnnotatedParam != null) continue;
                nonAnnotatedParam = param;
            }
            if (isCreator || namedCount > 0 || injectCount > 0) {
                if (namedCount + injectCount == argCount) {
                    creators.addPropertyCreator(ctor, properties);
                } else {
                    if (namedCount == 0 && injectCount + 1 == argCount) {
                        throw new IllegalArgumentException("Delegated constructor with Injectables not yet supported (see [JACKSON-712]) for " + ctor);
                    }
                    throw new IllegalArgumentException("Argument #" + nonAnnotatedParam.getIndex() + " of constructor " + ctor + " has no property name annotation; must have name when multiple-paramater constructor annotated as Creator");
                }
            }
            if (!annotationFound) continue;
            creators.addPropertyCreator(ctor, properties);
        }
    }

    protected boolean _handleSingleArgumentConstructor(DeserializationConfig config, BasicBeanDescription beanDesc, VisibilityChecker<?> vchecker, AnnotationIntrospector intr, CreatorCollector creators, AnnotatedConstructor ctor, boolean isCreator, boolean isVisible) throws JsonMappingException {
        AnnotatedParameter param = ctor.getParameter(0);
        String name = intr.findPropertyNameForParam(param);
        Object injectId = intr.findInjectableValueId(param);
        if (injectId != null || name != null && name.length() > 0) {
            CreatorProperty[] properties = new CreatorProperty[]{this.constructCreatorProperty(config, beanDesc, name, 0, param, injectId)};
            creators.addPropertyCreator(ctor, properties);
            return true;
        }
        Class<?> type = ctor.getParameterClass(0);
        if (type == String.class) {
            if (isCreator || isVisible) {
                creators.addStringCreator(ctor);
            }
            return true;
        }
        if (type == Integer.TYPE || type == Integer.class) {
            if (isCreator || isVisible) {
                creators.addIntCreator(ctor);
            }
            return true;
        }
        if (type == Long.TYPE || type == Long.class) {
            if (isCreator || isVisible) {
                creators.addLongCreator(ctor);
            }
            return true;
        }
        if (type == Double.TYPE || type == Double.class) {
            if (isCreator || isVisible) {
                creators.addDoubleCreator(ctor);
            }
            return true;
        }
        if (isCreator) {
            creators.addDelegatingCreator(ctor);
            return true;
        }
        return false;
    }

    protected void _addDeserializerFactoryMethods(DeserializationConfig config, BasicBeanDescription beanDesc, VisibilityChecker<?> vchecker, AnnotationIntrospector intr, CreatorCollector creators) throws JsonMappingException {
        for (AnnotatedMethod factory : beanDesc.getFactoryMethods()) {
            int argCount = factory.getParameterCount();
            if (argCount < 1) continue;
            boolean isCreator = intr.hasCreatorAnnotation(factory);
            if (argCount == 1) {
                AnnotatedParameter param = factory.getParameter(0);
                String name = intr.findPropertyNameForParam(param);
                Object injectId = intr.findInjectableValueId(param);
                if (injectId == null && (name == null || name.length() == 0)) {
                    this._handleSingleArgumentFactory(config, beanDesc, vchecker, intr, creators, factory, isCreator);
                    continue;
                }
            } else if (!intr.hasCreatorAnnotation(factory)) continue;
            CreatorProperty[] properties = new CreatorProperty[argCount];
            for (int i = 0; i < argCount; ++i) {
                AnnotatedParameter param = factory.getParameter(i);
                String name = intr.findPropertyNameForParam(param);
                Object injectableId = intr.findInjectableValueId(param);
                if ((name == null || name.length() == 0) && injectableId == null) {
                    throw new IllegalArgumentException("Argument #" + i + " of factory method " + factory + " has no property name annotation; must have when multiple-paramater static method annotated as Creator");
                }
                properties[i] = this.constructCreatorProperty(config, beanDesc, name, i, param, injectableId);
            }
            creators.addPropertyCreator(factory, properties);
        }
    }

    protected boolean _handleSingleArgumentFactory(DeserializationConfig config, BasicBeanDescription beanDesc, VisibilityChecker<?> vchecker, AnnotationIntrospector intr, CreatorCollector creators, AnnotatedMethod factory, boolean isCreator) throws JsonMappingException {
        Class<?> type = factory.getParameterClass(0);
        if (type == String.class) {
            if (isCreator || vchecker.isCreatorVisible(factory)) {
                creators.addStringCreator(factory);
            }
            return true;
        }
        if (type == Integer.TYPE || type == Integer.class) {
            if (isCreator || vchecker.isCreatorVisible(factory)) {
                creators.addIntCreator(factory);
            }
            return true;
        }
        if (type == Long.TYPE || type == Long.class) {
            if (isCreator || vchecker.isCreatorVisible(factory)) {
                creators.addLongCreator(factory);
            }
            return true;
        }
        if (type == Double.TYPE || type == Double.class) {
            if (isCreator || vchecker.isCreatorVisible(factory)) {
                creators.addDoubleCreator(factory);
            }
            return true;
        }
        if (type == Boolean.TYPE || type == Boolean.class) {
            if (isCreator || vchecker.isCreatorVisible(factory)) {
                creators.addBooleanCreator(factory);
            }
            return true;
        }
        if (intr.hasCreatorAnnotation(factory)) {
            creators.addDelegatingCreator(factory);
            return true;
        }
        return false;
    }

    protected CreatorProperty constructCreatorProperty(DeserializationConfig config, BasicBeanDescription beanDesc, String name, int index, AnnotatedParameter param, Object injectableValueId) throws JsonMappingException {
        BeanProperty.Std property;
        JavaType t0 = config.getTypeFactory().constructType(param.getParameterType(), beanDesc.bindingsForBeanType());
        JavaType type = this.resolveType(config, beanDesc, t0, param, property = new BeanProperty.Std(name, t0, beanDesc.getClassAnnotations(), param));
        if (type != t0) {
            property = property.withType(type);
        }
        JsonDeserializer<Object> deser = this.findDeserializerFromAnnotation(config, param, property);
        TypeDeserializer typeDeser = (TypeDeserializer)(type = this.modifyTypeByAnnotation(config, param, type, name)).getTypeHandler();
        if (typeDeser == null) {
            typeDeser = this.findTypeDeserializer(config, type, property);
        }
        SettableBeanProperty prop = new CreatorProperty(name, type, typeDeser, beanDesc.getClassAnnotations(), param, index, injectableValueId);
        if (deser != null) {
            prop = prop.withValueDeserializer((JsonDeserializer)deser);
        }
        return prop;
    }

    protected void addBeanProps(DeserializationConfig config, BasicBeanDescription beanDesc, BeanDeserializerBuilder builder) throws JsonMappingException {
        String name;
        Set<String> ignored2;
        List<BeanPropertyDefinition> props = beanDesc.findProperties();
        AnnotationIntrospector intr = config.getAnnotationIntrospector();
        boolean ignoreAny = false;
        Boolean B = intr.findIgnoreUnknownProperties(beanDesc.getClassInfo());
        if (B != null) {
            ignoreAny = B;
            builder.setIgnoreUnknownProperties(ignoreAny);
        }
        HashSet<String> ignored = ArrayBuilders.arrayToSet(intr.findPropertiesToIgnore(beanDesc.getClassInfo()));
        for (String propName : ignored) {
            builder.addIgnorable(propName);
        }
        AnnotatedMethod anySetter = beanDesc.findAnySetter();
        Set<String> set = ignored2 = anySetter == null ? beanDesc.getIgnoredPropertyNames() : beanDesc.getIgnoredPropertyNamesForDeser();
        if (ignored2 != null) {
            for (String propName : ignored2) {
                builder.addIgnorable(propName);
            }
        }
        HashMap ignoredTypes = new HashMap();
        for (BeanPropertyDefinition property : props) {
            SettableBeanProperty prop;
            Class<?> type;
            name = property.getName();
            if (ignored.contains(name)) continue;
            if (property.hasConstructorParameter()) {
                builder.addCreatorProperty(property);
                continue;
            }
            if (property.hasSetter()) {
                AnnotatedMethod setter = property.getSetter();
                type = setter.getParameterClass(0);
                if (this.isIgnorableType(config, beanDesc, type, ignoredTypes)) {
                    builder.addIgnorable(name);
                    continue;
                }
                prop = this.constructSettableProperty(config, beanDesc, name, setter);
                if (prop == null) continue;
                builder.addProperty(prop);
                continue;
            }
            if (!property.hasField()) continue;
            AnnotatedField field = property.getField();
            type = field.getRawType();
            if (this.isIgnorableType(config, beanDesc, type, ignoredTypes)) {
                builder.addIgnorable(name);
                continue;
            }
            prop = this.constructSettableProperty(config, beanDesc, name, field);
            if (prop == null) continue;
            builder.addProperty(prop);
        }
        if (anySetter != null) {
            builder.setAnySetter(this.constructAnySetter(config, beanDesc, anySetter));
        }
        if (config.isEnabled(DeserializationConfig.Feature.USE_GETTERS_AS_SETTERS)) {
            for (BeanPropertyDefinition property : props) {
                AnnotatedMethod getter;
                Class<?> rt;
                if (!property.hasGetter() || builder.hasProperty(name = property.getName()) || ignored.contains(name) || !Collection.class.isAssignableFrom(rt = (getter = property.getGetter()).getRawType()) && !Map.class.isAssignableFrom(rt) || ignored.contains(name) || builder.hasProperty(name)) continue;
                builder.addProperty(this.constructSetterlessProperty(config, beanDesc, name, getter));
            }
        }
    }

    protected void addReferenceProperties(DeserializationConfig config, BasicBeanDescription beanDesc, BeanDeserializerBuilder builder) throws JsonMappingException {
        Map<String, AnnotatedMember> refs = beanDesc.findBackReferenceProperties();
        if (refs != null) {
            for (Map.Entry<String, AnnotatedMember> en : refs.entrySet()) {
                String name = en.getKey();
                AnnotatedMember m = en.getValue();
                if (m instanceof AnnotatedMethod) {
                    builder.addBackReferenceProperty(name, this.constructSettableProperty(config, beanDesc, m.getName(), (AnnotatedMethod)m));
                    continue;
                }
                builder.addBackReferenceProperty(name, this.constructSettableProperty(config, beanDesc, m.getName(), (AnnotatedField)m));
            }
        }
    }

    protected void addInjectables(DeserializationConfig config, BasicBeanDescription beanDesc, BeanDeserializerBuilder builder) throws JsonMappingException {
        Map<Object, AnnotatedMember> raw = beanDesc.findInjectables();
        if (raw != null) {
            boolean fixAccess = config.isEnabled(DeserializationConfig.Feature.CAN_OVERRIDE_ACCESS_MODIFIERS);
            for (Map.Entry<Object, AnnotatedMember> entry : raw.entrySet()) {
                AnnotatedMember m = entry.getValue();
                if (fixAccess) {
                    m.fixAccess();
                }
                builder.addInjectable(m.getName(), beanDesc.resolveType(m.getGenericType()), beanDesc.getClassAnnotations(), m, entry.getKey());
            }
        }
    }

    protected SettableAnyProperty constructAnySetter(DeserializationConfig config, BasicBeanDescription beanDesc, AnnotatedMethod setter) throws JsonMappingException {
        if (config.isEnabled(DeserializationConfig.Feature.CAN_OVERRIDE_ACCESS_MODIFIERS)) {
            setter.fixAccess();
        }
        JavaType type = beanDesc.bindingsForBeanType().resolveType(setter.getParameterType(1));
        BeanProperty.Std property = new BeanProperty.Std(setter.getName(), type, beanDesc.getClassAnnotations(), setter);
        type = this.resolveType(config, beanDesc, type, setter, property);
        JsonDeserializer<Object> deser = this.findDeserializerFromAnnotation(config, setter, property);
        if (deser != null) {
            return new SettableAnyProperty((BeanProperty)property, setter, type, deser);
        }
        type = this.modifyTypeByAnnotation(config, setter, type, property.getName());
        return new SettableAnyProperty((BeanProperty)property, setter, type, null);
    }

    protected SettableBeanProperty constructSettableProperty(DeserializationConfig config, BasicBeanDescription beanDesc, String name, AnnotatedMethod setter) throws JsonMappingException {
        AnnotationIntrospector.ReferenceProperty ref;
        BeanProperty.Std property;
        JavaType t0;
        JavaType type;
        if (config.isEnabled(DeserializationConfig.Feature.CAN_OVERRIDE_ACCESS_MODIFIERS)) {
            setter.fixAccess();
        }
        if ((type = this.resolveType(config, beanDesc, t0 = beanDesc.bindingsForBeanType().resolveType(setter.getParameterType(0)), setter, property = new BeanProperty.Std(name, t0, beanDesc.getClassAnnotations(), setter))) != t0) {
            property = property.withType(type);
        }
        JsonDeserializer<Object> propDeser = this.findDeserializerFromAnnotation(config, setter, property);
        type = this.modifyTypeByAnnotation(config, setter, type, name);
        TypeDeserializer typeDeser = (TypeDeserializer)type.getTypeHandler();
        SettableBeanProperty prop = new SettableBeanProperty.MethodProperty(name, type, typeDeser, beanDesc.getClassAnnotations(), setter);
        if (propDeser != null) {
            prop = prop.withValueDeserializer(propDeser);
        }
        if ((ref = config.getAnnotationIntrospector().findReferenceType(setter)) != null && ref.isManagedReference()) {
            prop.setManagedReferenceName(ref.getName());
        }
        return prop;
    }

    protected SettableBeanProperty constructSettableProperty(DeserializationConfig config, BasicBeanDescription beanDesc, String name, AnnotatedField field) throws JsonMappingException {
        AnnotationIntrospector.ReferenceProperty ref;
        BeanProperty.Std property;
        JavaType t0;
        JavaType type;
        if (config.isEnabled(DeserializationConfig.Feature.CAN_OVERRIDE_ACCESS_MODIFIERS)) {
            field.fixAccess();
        }
        if ((type = this.resolveType(config, beanDesc, t0 = beanDesc.bindingsForBeanType().resolveType(field.getGenericType()), field, property = new BeanProperty.Std(name, t0, beanDesc.getClassAnnotations(), field))) != t0) {
            property = property.withType(type);
        }
        JsonDeserializer<Object> propDeser = this.findDeserializerFromAnnotation(config, field, property);
        type = this.modifyTypeByAnnotation(config, field, type, name);
        TypeDeserializer typeDeser = (TypeDeserializer)type.getTypeHandler();
        SettableBeanProperty prop = new SettableBeanProperty.FieldProperty(name, type, typeDeser, beanDesc.getClassAnnotations(), field);
        if (propDeser != null) {
            prop = prop.withValueDeserializer(propDeser);
        }
        if ((ref = config.getAnnotationIntrospector().findReferenceType(field)) != null && ref.isManagedReference()) {
            prop.setManagedReferenceName(ref.getName());
        }
        return prop;
    }

    protected SettableBeanProperty constructSetterlessProperty(DeserializationConfig config, BasicBeanDescription beanDesc, String name, AnnotatedMethod getter) throws JsonMappingException {
        if (config.isEnabled(DeserializationConfig.Feature.CAN_OVERRIDE_ACCESS_MODIFIERS)) {
            getter.fixAccess();
        }
        JavaType type = getter.getType(beanDesc.bindingsForBeanType());
        BeanProperty.Std property = new BeanProperty.Std(name, type, beanDesc.getClassAnnotations(), getter);
        JsonDeserializer<Object> propDeser = this.findDeserializerFromAnnotation(config, getter, property);
        type = this.modifyTypeByAnnotation(config, getter, type, name);
        TypeDeserializer typeDeser = (TypeDeserializer)type.getTypeHandler();
        SettableBeanProperty prop = new SettableBeanProperty.SetterlessProperty(name, type, typeDeser, beanDesc.getClassAnnotations(), getter);
        if (propDeser != null) {
            prop = ((SettableBeanProperty)prop).withValueDeserializer(propDeser);
        }
        return prop;
    }

    protected boolean isPotentialBeanType(Class<?> type) {
        String typeStr = ClassUtil.canBeABeanType(type);
        if (typeStr != null) {
            throw new IllegalArgumentException("Can not deserialize Class " + type.getName() + " (of type " + typeStr + ") as a Bean");
        }
        if (ClassUtil.isProxyType(type)) {
            throw new IllegalArgumentException("Can not deserialize Proxy class " + type.getName() + " as a Bean");
        }
        typeStr = ClassUtil.isLocalType(type, true);
        if (typeStr != null) {
            throw new IllegalArgumentException("Can not deserialize Class " + type.getName() + " (of type " + typeStr + ") as a Bean");
        }
        return true;
    }

    protected boolean isIgnorableType(DeserializationConfig config, BasicBeanDescription beanDesc, Class<?> type, Map<Class<?>, Boolean> ignoredTypes) {
        Boolean status = ignoredTypes.get(type);
        if (status == null) {
            BasicBeanDescription desc = (BasicBeanDescription)config.introspectClassAnnotations(type);
            status = config.getAnnotationIntrospector().isIgnorableType(desc.getClassInfo());
            if (status == null) {
                status = Boolean.FALSE;
            }
        }
        return status;
    }

    protected void checkIllegalTypes(JavaType type) throws JsonMappingException {
        this._subtypeValidator.validateSubType(type);
    }

    static {
        HashSet<String> s = new HashSet<String>();
        s.add("org.apache.commons.collections.functors.InvokerTransformer");
        s.add("org.apache.commons.collections.functors.InstantiateTransformer");
        s.add("org.apache.commons.collections4.functors.InvokerTransformer");
        s.add("org.apache.commons.collections4.functors.InstantiateTransformer");
        s.add("org.codehaus.groovy.runtime.ConvertedClosure");
        s.add("org.codehaus.groovy.runtime.MethodClosure");
        s.add("org.springframework.beans.factory.ObjectFactory");
        s.add("com.sun.org.apache.xalan.internal.xsltc.trax.TemplatesImpl");
        s.add("org.apache.xalan.xsltc.trax.TemplatesImpl");
        DEFAULT_NO_DESER_CLASS_NAMES = Collections.unmodifiableSet(s);
        instance = new BeanDeserializerFactory(null);
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class ConfigImpl
    extends DeserializerFactory.Config {
        protected static final KeyDeserializers[] NO_KEY_DESERIALIZERS = new KeyDeserializers[0];
        protected static final BeanDeserializerModifier[] NO_MODIFIERS = new BeanDeserializerModifier[0];
        protected static final AbstractTypeResolver[] NO_ABSTRACT_TYPE_RESOLVERS = new AbstractTypeResolver[0];
        protected static final ValueInstantiators[] NO_VALUE_INSTANTIATORS = new ValueInstantiators[0];
        protected final Deserializers[] _additionalDeserializers;
        protected final KeyDeserializers[] _additionalKeyDeserializers;
        protected final BeanDeserializerModifier[] _modifiers;
        protected final AbstractTypeResolver[] _abstractTypeResolvers;
        protected final ValueInstantiators[] _valueInstantiators;

        public ConfigImpl() {
            this(null, null, null, null, null);
        }

        protected ConfigImpl(Deserializers[] allAdditionalDeserializers, KeyDeserializers[] allAdditionalKeyDeserializers, BeanDeserializerModifier[] modifiers, AbstractTypeResolver[] atr, ValueInstantiators[] vi) {
            this._additionalDeserializers = allAdditionalDeserializers == null ? NO_DESERIALIZERS : allAdditionalDeserializers;
            this._additionalKeyDeserializers = allAdditionalKeyDeserializers == null ? NO_KEY_DESERIALIZERS : allAdditionalKeyDeserializers;
            this._modifiers = modifiers == null ? NO_MODIFIERS : modifiers;
            this._abstractTypeResolvers = atr == null ? NO_ABSTRACT_TYPE_RESOLVERS : atr;
            this._valueInstantiators = vi == null ? NO_VALUE_INSTANTIATORS : vi;
        }

        @Override
        public DeserializerFactory.Config withAdditionalDeserializers(Deserializers additional) {
            if (additional == null) {
                throw new IllegalArgumentException("Can not pass null Deserializers");
            }
            Deserializers[] all = ArrayBuilders.insertInListNoDup(this._additionalDeserializers, additional);
            return new ConfigImpl(all, this._additionalKeyDeserializers, this._modifiers, this._abstractTypeResolvers, this._valueInstantiators);
        }

        @Override
        public DeserializerFactory.Config withAdditionalKeyDeserializers(KeyDeserializers additional) {
            if (additional == null) {
                throw new IllegalArgumentException("Can not pass null KeyDeserializers");
            }
            KeyDeserializers[] all = ArrayBuilders.insertInListNoDup(this._additionalKeyDeserializers, additional);
            return new ConfigImpl(this._additionalDeserializers, all, this._modifiers, this._abstractTypeResolvers, this._valueInstantiators);
        }

        @Override
        public DeserializerFactory.Config withDeserializerModifier(BeanDeserializerModifier modifier) {
            if (modifier == null) {
                throw new IllegalArgumentException("Can not pass null modifier");
            }
            BeanDeserializerModifier[] all = ArrayBuilders.insertInListNoDup(this._modifiers, modifier);
            return new ConfigImpl(this._additionalDeserializers, this._additionalKeyDeserializers, all, this._abstractTypeResolvers, this._valueInstantiators);
        }

        @Override
        public DeserializerFactory.Config withAbstractTypeResolver(AbstractTypeResolver resolver) {
            if (resolver == null) {
                throw new IllegalArgumentException("Can not pass null resolver");
            }
            AbstractTypeResolver[] all = ArrayBuilders.insertInListNoDup(this._abstractTypeResolvers, resolver);
            return new ConfigImpl(this._additionalDeserializers, this._additionalKeyDeserializers, this._modifiers, all, this._valueInstantiators);
        }

        @Override
        public DeserializerFactory.Config withValueInstantiators(ValueInstantiators instantiators) {
            if (instantiators == null) {
                throw new IllegalArgumentException("Can not pass null resolver");
            }
            ValueInstantiators[] all = ArrayBuilders.insertInListNoDup(this._valueInstantiators, instantiators);
            return new ConfigImpl(this._additionalDeserializers, this._additionalKeyDeserializers, this._modifiers, this._abstractTypeResolvers, all);
        }

        @Override
        public boolean hasDeserializers() {
            return this._additionalDeserializers.length > 0;
        }

        @Override
        public boolean hasKeyDeserializers() {
            return this._additionalKeyDeserializers.length > 0;
        }

        @Override
        public boolean hasDeserializerModifiers() {
            return this._modifiers.length > 0;
        }

        @Override
        public boolean hasAbstractTypeResolvers() {
            return this._abstractTypeResolvers.length > 0;
        }

        @Override
        public boolean hasValueInstantiators() {
            return this._valueInstantiators.length > 0;
        }

        @Override
        public Iterable<Deserializers> deserializers() {
            return ArrayBuilders.arrayAsIterable(this._additionalDeserializers);
        }

        @Override
        public Iterable<KeyDeserializers> keyDeserializers() {
            return ArrayBuilders.arrayAsIterable(this._additionalKeyDeserializers);
        }

        @Override
        public Iterable<BeanDeserializerModifier> deserializerModifiers() {
            return ArrayBuilders.arrayAsIterable(this._modifiers);
        }

        @Override
        public Iterable<AbstractTypeResolver> abstractTypeResolvers() {
            return ArrayBuilders.arrayAsIterable(this._abstractTypeResolvers);
        }

        @Override
        public Iterable<ValueInstantiators> valueInstantiators() {
            return ArrayBuilders.arrayAsIterable(this._valueInstantiators);
        }
    }
}


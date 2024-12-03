/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.Base64Variant
 *  org.codehaus.jackson.Base64Variants
 *  org.codehaus.jackson.annotate.JsonAutoDetect$Visibility
 *  org.codehaus.jackson.annotate.JsonMethod
 *  org.codehaus.jackson.type.JavaType
 */
package org.codehaus.jackson.map;

import java.text.DateFormat;
import java.util.HashMap;
import org.codehaus.jackson.Base64Variant;
import org.codehaus.jackson.Base64Variants;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.BeanDescription;
import org.codehaus.jackson.map.ClassIntrospector;
import org.codehaus.jackson.map.DeserializationProblemHandler;
import org.codehaus.jackson.map.HandlerInstantiator;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.KeyDeserializer;
import org.codehaus.jackson.map.MapperConfig;
import org.codehaus.jackson.map.PropertyNamingStrategy;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.deser.ValueInstantiator;
import org.codehaus.jackson.map.introspect.Annotated;
import org.codehaus.jackson.map.introspect.AnnotatedClass;
import org.codehaus.jackson.map.introspect.NopAnnotationIntrospector;
import org.codehaus.jackson.map.introspect.VisibilityChecker;
import org.codehaus.jackson.map.jsontype.SubtypeResolver;
import org.codehaus.jackson.map.jsontype.TypeResolverBuilder;
import org.codehaus.jackson.map.type.ClassKey;
import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.map.util.ClassUtil;
import org.codehaus.jackson.map.util.LinkedNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class DeserializationConfig
extends MapperConfig.Impl<Feature, DeserializationConfig> {
    protected LinkedNode<DeserializationProblemHandler> _problemHandlers;
    protected final JsonNodeFactory _nodeFactory;
    protected boolean _sortPropertiesAlphabetically;

    public DeserializationConfig(ClassIntrospector<? extends BeanDescription> intr, AnnotationIntrospector annIntr, VisibilityChecker<?> vc, SubtypeResolver subtypeResolver, PropertyNamingStrategy propertyNamingStrategy, TypeFactory typeFactory, HandlerInstantiator handlerInstantiator) {
        super(intr, annIntr, vc, subtypeResolver, propertyNamingStrategy, typeFactory, handlerInstantiator, DeserializationConfig.collectFeatureDefaults(Feature.class));
        this._nodeFactory = JsonNodeFactory.instance;
    }

    protected DeserializationConfig(DeserializationConfig src) {
        this(src, src._base);
    }

    private DeserializationConfig(DeserializationConfig src, HashMap<ClassKey, Class<?>> mixins, SubtypeResolver str) {
        this(src, src._base);
        this._mixInAnnotations = mixins;
        this._subtypeResolver = str;
    }

    protected DeserializationConfig(DeserializationConfig src, MapperConfig.Base base) {
        super(src, base, src._subtypeResolver);
        this._problemHandlers = src._problemHandlers;
        this._nodeFactory = src._nodeFactory;
        this._sortPropertiesAlphabetically = src._sortPropertiesAlphabetically;
    }

    protected DeserializationConfig(DeserializationConfig src, JsonNodeFactory f) {
        super(src);
        this._problemHandlers = src._problemHandlers;
        this._nodeFactory = f;
        this._sortPropertiesAlphabetically = src._sortPropertiesAlphabetically;
    }

    protected DeserializationConfig(DeserializationConfig src, int featureFlags) {
        super(src, featureFlags);
        this._problemHandlers = src._problemHandlers;
        this._nodeFactory = src._nodeFactory;
        this._sortPropertiesAlphabetically = src._sortPropertiesAlphabetically;
    }

    protected DeserializationConfig passSerializationFeatures(int serializationFeatureFlags) {
        this._sortPropertiesAlphabetically = (serializationFeatureFlags & SerializationConfig.Feature.SORT_PROPERTIES_ALPHABETICALLY.getMask()) != 0;
        return this;
    }

    @Override
    public DeserializationConfig withClassIntrospector(ClassIntrospector<? extends BeanDescription> ci) {
        return new DeserializationConfig(this, this._base.withClassIntrospector(ci));
    }

    @Override
    public DeserializationConfig withAnnotationIntrospector(AnnotationIntrospector ai) {
        return new DeserializationConfig(this, this._base.withAnnotationIntrospector(ai));
    }

    @Override
    public DeserializationConfig withVisibilityChecker(VisibilityChecker<?> vc) {
        return new DeserializationConfig(this, this._base.withVisibilityChecker(vc));
    }

    @Override
    public DeserializationConfig withVisibility(JsonMethod forMethod, JsonAutoDetect.Visibility visibility) {
        return new DeserializationConfig(this, this._base.withVisibility(forMethod, visibility));
    }

    @Override
    public DeserializationConfig withTypeResolverBuilder(TypeResolverBuilder<?> trb) {
        return new DeserializationConfig(this, this._base.withTypeResolverBuilder(trb));
    }

    @Override
    public DeserializationConfig withSubtypeResolver(SubtypeResolver str) {
        DeserializationConfig cfg = new DeserializationConfig(this);
        cfg._subtypeResolver = str;
        return cfg;
    }

    @Override
    public DeserializationConfig withPropertyNamingStrategy(PropertyNamingStrategy pns) {
        return new DeserializationConfig(this, this._base.withPropertyNamingStrategy(pns));
    }

    @Override
    public DeserializationConfig withTypeFactory(TypeFactory tf) {
        return tf == this._base.getTypeFactory() ? this : new DeserializationConfig(this, this._base.withTypeFactory(tf));
    }

    @Override
    public DeserializationConfig withDateFormat(DateFormat df) {
        return df == this._base.getDateFormat() ? this : new DeserializationConfig(this, this._base.withDateFormat(df));
    }

    @Override
    public DeserializationConfig withHandlerInstantiator(HandlerInstantiator hi) {
        return hi == this._base.getHandlerInstantiator() ? this : new DeserializationConfig(this, this._base.withHandlerInstantiator(hi));
    }

    @Override
    public DeserializationConfig withInsertedAnnotationIntrospector(AnnotationIntrospector ai) {
        return new DeserializationConfig(this, this._base.withInsertedAnnotationIntrospector(ai));
    }

    @Override
    public DeserializationConfig withAppendedAnnotationIntrospector(AnnotationIntrospector ai) {
        return new DeserializationConfig(this, this._base.withAppendedAnnotationIntrospector(ai));
    }

    public DeserializationConfig withNodeFactory(JsonNodeFactory f) {
        return new DeserializationConfig(this, f);
    }

    public DeserializationConfig with(Feature ... features) {
        int flags = this._featureFlags;
        for (Feature f : features) {
            flags |= f.getMask();
        }
        return new DeserializationConfig(this, flags);
    }

    public DeserializationConfig without(Feature ... features) {
        int flags = this._featureFlags;
        for (Feature f : features) {
            flags &= ~f.getMask();
        }
        return new DeserializationConfig(this, flags);
    }

    @Override
    @Deprecated
    public void fromAnnotations(Class<?> cls) {
        AnnotationIntrospector ai = this.getAnnotationIntrospector();
        AnnotatedClass ac = AnnotatedClass.construct(cls, ai, null);
        VisibilityChecker<?> prevVc = this.getDefaultVisibilityChecker();
        this._base = this._base.withVisibilityChecker(ai.findAutoDetectVisibility(ac, prevVc));
    }

    @Override
    public DeserializationConfig createUnshared(SubtypeResolver subtypeResolver) {
        HashMap mixins = this._mixInAnnotations;
        this._mixInAnnotationsShared = true;
        return new DeserializationConfig(this, mixins, subtypeResolver);
    }

    @Override
    public AnnotationIntrospector getAnnotationIntrospector() {
        if (this.isEnabled(Feature.USE_ANNOTATIONS)) {
            return super.getAnnotationIntrospector();
        }
        return NopAnnotationIntrospector.instance;
    }

    @Override
    public <T extends BeanDescription> T introspectClassAnnotations(JavaType type) {
        return (T)this.getClassIntrospector().forClassAnnotations(this, type, (ClassIntrospector.MixInResolver)this);
    }

    @Override
    public <T extends BeanDescription> T introspectDirectClassAnnotations(JavaType type) {
        return (T)this.getClassIntrospector().forDirectClassAnnotations(this, type, (ClassIntrospector.MixInResolver)this);
    }

    @Override
    public boolean isAnnotationProcessingEnabled() {
        return this.isEnabled(Feature.USE_ANNOTATIONS);
    }

    @Override
    public boolean canOverrideAccessModifiers() {
        return this.isEnabled(Feature.CAN_OVERRIDE_ACCESS_MODIFIERS);
    }

    @Override
    public boolean shouldSortPropertiesAlphabetically() {
        return this._sortPropertiesAlphabetically;
    }

    @Override
    public VisibilityChecker<?> getDefaultVisibilityChecker() {
        VisibilityChecker<?> vchecker = super.getDefaultVisibilityChecker();
        if (!this.isEnabled(Feature.AUTO_DETECT_SETTERS)) {
            vchecker = vchecker.withSetterVisibility(JsonAutoDetect.Visibility.NONE);
        }
        if (!this.isEnabled(Feature.AUTO_DETECT_CREATORS)) {
            vchecker = vchecker.withCreatorVisibility(JsonAutoDetect.Visibility.NONE);
        }
        if (!this.isEnabled(Feature.AUTO_DETECT_FIELDS)) {
            vchecker = vchecker.withFieldVisibility(JsonAutoDetect.Visibility.NONE);
        }
        return vchecker;
    }

    public boolean isEnabled(Feature f) {
        return (this._featureFlags & f.getMask()) != 0;
    }

    @Override
    @Deprecated
    public void enable(Feature f) {
        super.enable(f);
    }

    @Override
    @Deprecated
    public void disable(Feature f) {
        super.disable(f);
    }

    @Override
    @Deprecated
    public void set(Feature f, boolean state) {
        super.set(f, state);
    }

    public LinkedNode<DeserializationProblemHandler> getProblemHandlers() {
        return this._problemHandlers;
    }

    public void addHandler(DeserializationProblemHandler h) {
        if (!LinkedNode.contains(this._problemHandlers, h)) {
            this._problemHandlers = new LinkedNode<DeserializationProblemHandler>(h, this._problemHandlers);
        }
    }

    public void clearHandlers() {
        this._problemHandlers = null;
    }

    public Base64Variant getBase64Variant() {
        return Base64Variants.getDefaultVariant();
    }

    public final JsonNodeFactory getNodeFactory() {
        return this._nodeFactory;
    }

    public <T extends BeanDescription> T introspect(JavaType type) {
        return (T)this.getClassIntrospector().forDeserialization(this, type, this);
    }

    public <T extends BeanDescription> T introspectForCreation(JavaType type) {
        return (T)this.getClassIntrospector().forCreation(this, type, this);
    }

    public JsonDeserializer<Object> deserializerInstance(Annotated annotated, Class<? extends JsonDeserializer<?>> deserClass) {
        JsonDeserializer<Object> deser;
        HandlerInstantiator hi = this.getHandlerInstantiator();
        if (hi != null && (deser = hi.deserializerInstance(this, annotated, deserClass)) != null) {
            return deser;
        }
        return ClassUtil.createInstance(deserClass, this.canOverrideAccessModifiers());
    }

    public KeyDeserializer keyDeserializerInstance(Annotated annotated, Class<? extends KeyDeserializer> keyDeserClass) {
        KeyDeserializer keyDeser;
        HandlerInstantiator hi = this.getHandlerInstantiator();
        if (hi != null && (keyDeser = hi.keyDeserializerInstance(this, annotated, keyDeserClass)) != null) {
            return keyDeser;
        }
        return ClassUtil.createInstance(keyDeserClass, this.canOverrideAccessModifiers());
    }

    public ValueInstantiator valueInstantiatorInstance(Annotated annotated, Class<? extends ValueInstantiator> instClass) {
        ValueInstantiator inst;
        HandlerInstantiator hi = this.getHandlerInstantiator();
        if (hi != null && (inst = hi.valueInstantiatorInstance(this, annotated, instClass)) != null) {
            return inst;
        }
        return ClassUtil.createInstance(instClass, this.canOverrideAccessModifiers());
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Feature implements MapperConfig.ConfigFeature
    {
        USE_ANNOTATIONS(true),
        AUTO_DETECT_SETTERS(true),
        AUTO_DETECT_CREATORS(true),
        AUTO_DETECT_FIELDS(true),
        USE_GETTERS_AS_SETTERS(true),
        CAN_OVERRIDE_ACCESS_MODIFIERS(true),
        USE_BIG_DECIMAL_FOR_FLOATS(false),
        USE_BIG_INTEGER_FOR_INTS(false),
        USE_JAVA_ARRAY_FOR_JSON_ARRAY(false),
        READ_ENUMS_USING_TO_STRING(false),
        FAIL_ON_UNKNOWN_PROPERTIES(true),
        FAIL_ON_NULL_FOR_PRIMITIVES(false),
        FAIL_ON_NUMBERS_FOR_ENUMS(false),
        WRAP_EXCEPTIONS(true),
        ACCEPT_SINGLE_VALUE_AS_ARRAY(false),
        UNWRAP_ROOT_VALUE(false),
        ACCEPT_EMPTY_STRING_AS_NULL_OBJECT(false);

        final boolean _defaultState;

        private Feature(boolean defaultState) {
            this._defaultState = defaultState;
        }

        @Override
        public boolean enabledByDefault() {
            return this._defaultState;
        }

        @Override
        public int getMask() {
            return 1 << this.ordinal();
        }
    }
}


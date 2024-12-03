/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.annotate.JsonAutoDetect$Visibility
 *  org.codehaus.jackson.annotate.JsonMethod
 *  org.codehaus.jackson.type.JavaType
 */
package org.codehaus.jackson.map;

import java.text.DateFormat;
import java.util.HashMap;
import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.BeanDescription;
import org.codehaus.jackson.map.ClassIntrospector;
import org.codehaus.jackson.map.HandlerInstantiator;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.MapperConfig;
import org.codehaus.jackson.map.PropertyNamingStrategy;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.introspect.Annotated;
import org.codehaus.jackson.map.introspect.AnnotatedClass;
import org.codehaus.jackson.map.introspect.VisibilityChecker;
import org.codehaus.jackson.map.jsontype.SubtypeResolver;
import org.codehaus.jackson.map.jsontype.TypeResolverBuilder;
import org.codehaus.jackson.map.ser.FilterProvider;
import org.codehaus.jackson.map.type.ClassKey;
import org.codehaus.jackson.map.type.TypeFactory;
import org.codehaus.jackson.map.util.ClassUtil;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class SerializationConfig
extends MapperConfig.Impl<Feature, SerializationConfig> {
    protected JsonSerialize.Inclusion _serializationInclusion = null;
    protected Class<?> _serializationView;
    protected FilterProvider _filterProvider;

    public SerializationConfig(ClassIntrospector<? extends BeanDescription> intr, AnnotationIntrospector annIntr, VisibilityChecker<?> vc, SubtypeResolver subtypeResolver, PropertyNamingStrategy propertyNamingStrategy, TypeFactory typeFactory, HandlerInstantiator handlerInstantiator) {
        super(intr, annIntr, vc, subtypeResolver, propertyNamingStrategy, typeFactory, handlerInstantiator, SerializationConfig.collectFeatureDefaults(Feature.class));
        this._filterProvider = null;
    }

    protected SerializationConfig(SerializationConfig src) {
        this(src, src._base);
    }

    protected SerializationConfig(SerializationConfig src, HashMap<ClassKey, Class<?>> mixins, SubtypeResolver str) {
        this(src, src._base);
        this._mixInAnnotations = mixins;
        this._subtypeResolver = str;
    }

    protected SerializationConfig(SerializationConfig src, MapperConfig.Base base) {
        super(src, base, src._subtypeResolver);
        this._serializationInclusion = src._serializationInclusion;
        this._serializationView = src._serializationView;
        this._filterProvider = src._filterProvider;
    }

    protected SerializationConfig(SerializationConfig src, FilterProvider filters) {
        super(src);
        this._serializationInclusion = src._serializationInclusion;
        this._serializationView = src._serializationView;
        this._filterProvider = filters;
    }

    protected SerializationConfig(SerializationConfig src, Class<?> view) {
        super(src);
        this._serializationInclusion = src._serializationInclusion;
        this._serializationView = view;
        this._filterProvider = src._filterProvider;
    }

    protected SerializationConfig(SerializationConfig src, JsonSerialize.Inclusion incl) {
        super(src);
        this._serializationInclusion = incl;
        this._featureFlags = incl == JsonSerialize.Inclusion.NON_NULL ? (this._featureFlags &= ~Feature.WRITE_NULL_PROPERTIES.getMask()) : (this._featureFlags |= Feature.WRITE_NULL_PROPERTIES.getMask());
        this._serializationView = src._serializationView;
        this._filterProvider = src._filterProvider;
    }

    protected SerializationConfig(SerializationConfig src, int features) {
        super(src, features);
        this._serializationInclusion = src._serializationInclusion;
        this._serializationView = src._serializationView;
        this._filterProvider = src._filterProvider;
    }

    @Override
    public SerializationConfig withClassIntrospector(ClassIntrospector<? extends BeanDescription> ci) {
        return new SerializationConfig(this, this._base.withClassIntrospector(ci));
    }

    @Override
    public SerializationConfig withAnnotationIntrospector(AnnotationIntrospector ai) {
        return new SerializationConfig(this, this._base.withAnnotationIntrospector(ai));
    }

    @Override
    public SerializationConfig withInsertedAnnotationIntrospector(AnnotationIntrospector ai) {
        return new SerializationConfig(this, this._base.withInsertedAnnotationIntrospector(ai));
    }

    @Override
    public SerializationConfig withAppendedAnnotationIntrospector(AnnotationIntrospector ai) {
        return new SerializationConfig(this, this._base.withAppendedAnnotationIntrospector(ai));
    }

    @Override
    public SerializationConfig withVisibilityChecker(VisibilityChecker<?> vc) {
        return new SerializationConfig(this, this._base.withVisibilityChecker(vc));
    }

    @Override
    public SerializationConfig withVisibility(JsonMethod forMethod, JsonAutoDetect.Visibility visibility) {
        return new SerializationConfig(this, this._base.withVisibility(forMethod, visibility));
    }

    @Override
    public SerializationConfig withTypeResolverBuilder(TypeResolverBuilder<?> trb) {
        return new SerializationConfig(this, this._base.withTypeResolverBuilder(trb));
    }

    @Override
    public SerializationConfig withSubtypeResolver(SubtypeResolver str) {
        SerializationConfig cfg = new SerializationConfig(this);
        cfg._subtypeResolver = str;
        return cfg;
    }

    @Override
    public SerializationConfig withPropertyNamingStrategy(PropertyNamingStrategy pns) {
        return new SerializationConfig(this, this._base.withPropertyNamingStrategy(pns));
    }

    @Override
    public SerializationConfig withTypeFactory(TypeFactory tf) {
        return new SerializationConfig(this, this._base.withTypeFactory(tf));
    }

    @Override
    public SerializationConfig withDateFormat(DateFormat df) {
        SerializationConfig cfg = new SerializationConfig(this, this._base.withDateFormat(df));
        cfg = df == null ? cfg.with(Feature.WRITE_DATES_AS_TIMESTAMPS) : cfg.without(Feature.WRITE_DATES_AS_TIMESTAMPS);
        return cfg;
    }

    @Override
    public SerializationConfig withHandlerInstantiator(HandlerInstantiator hi) {
        return new SerializationConfig(this, this._base.withHandlerInstantiator(hi));
    }

    public SerializationConfig withFilters(FilterProvider filterProvider) {
        return new SerializationConfig(this, filterProvider);
    }

    public SerializationConfig withView(Class<?> view) {
        return new SerializationConfig(this, view);
    }

    public SerializationConfig withSerializationInclusion(JsonSerialize.Inclusion incl) {
        return new SerializationConfig(this, incl);
    }

    public SerializationConfig with(Feature ... features) {
        int flags = this._featureFlags;
        for (Feature f : features) {
            flags |= f.getMask();
        }
        return new SerializationConfig(this, flags);
    }

    public SerializationConfig without(Feature ... features) {
        int flags = this._featureFlags;
        for (Feature f : features) {
            flags &= ~f.getMask();
        }
        return new SerializationConfig(this, flags);
    }

    @Override
    @Deprecated
    public void fromAnnotations(Class<?> cls) {
        JsonSerialize.Typing typing;
        AnnotationIntrospector ai = this.getAnnotationIntrospector();
        AnnotatedClass ac = AnnotatedClass.construct(cls, ai, null);
        this._base = this._base.withVisibilityChecker(ai.findAutoDetectVisibility(ac, this.getDefaultVisibilityChecker()));
        JsonSerialize.Inclusion incl = ai.findSerializationInclusion(ac, null);
        if (incl != this._serializationInclusion) {
            this.setSerializationInclusion(incl);
        }
        if ((typing = ai.findSerializationTyping(ac)) != null) {
            this.set(Feature.USE_STATIC_TYPING, typing == JsonSerialize.Typing.STATIC);
        }
    }

    @Override
    public SerializationConfig createUnshared(SubtypeResolver subtypeResolver) {
        HashMap mixins = this._mixInAnnotations;
        this._mixInAnnotationsShared = true;
        return new SerializationConfig(this, mixins, subtypeResolver);
    }

    @Override
    public AnnotationIntrospector getAnnotationIntrospector() {
        if (this.isEnabled(Feature.USE_ANNOTATIONS)) {
            return super.getAnnotationIntrospector();
        }
        return AnnotationIntrospector.nopInstance();
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
        return this.isEnabled(Feature.SORT_PROPERTIES_ALPHABETICALLY);
    }

    @Override
    public VisibilityChecker<?> getDefaultVisibilityChecker() {
        VisibilityChecker<?> vchecker = super.getDefaultVisibilityChecker();
        if (!this.isEnabled(Feature.AUTO_DETECT_GETTERS)) {
            vchecker = vchecker.withGetterVisibility(JsonAutoDetect.Visibility.NONE);
        }
        if (!this.isEnabled(Feature.AUTO_DETECT_IS_GETTERS)) {
            vchecker = vchecker.withIsGetterVisibility(JsonAutoDetect.Visibility.NONE);
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

    public Class<?> getSerializationView() {
        return this._serializationView;
    }

    public JsonSerialize.Inclusion getSerializationInclusion() {
        if (this._serializationInclusion != null) {
            return this._serializationInclusion;
        }
        return this.isEnabled(Feature.WRITE_NULL_PROPERTIES) ? JsonSerialize.Inclusion.ALWAYS : JsonSerialize.Inclusion.NON_NULL;
    }

    @Deprecated
    public void setSerializationInclusion(JsonSerialize.Inclusion props) {
        this._serializationInclusion = props;
        if (props == JsonSerialize.Inclusion.NON_NULL) {
            this.disable(Feature.WRITE_NULL_PROPERTIES);
        } else {
            this.enable(Feature.WRITE_NULL_PROPERTIES);
        }
    }

    public FilterProvider getFilterProvider() {
        return this._filterProvider;
    }

    public <T extends BeanDescription> T introspect(JavaType type) {
        return (T)this.getClassIntrospector().forSerialization(this, type, this);
    }

    public JsonSerializer<Object> serializerInstance(Annotated annotated, Class<? extends JsonSerializer<?>> serClass) {
        JsonSerializer<Object> ser;
        HandlerInstantiator hi = this.getHandlerInstantiator();
        if (hi != null && (ser = hi.serializerInstance(this, annotated, serClass)) != null) {
            return ser;
        }
        return ClassUtil.createInstance(serClass, this.canOverrideAccessModifiers());
    }

    @Override
    @Deprecated
    public final void setDateFormat(DateFormat df) {
        super.setDateFormat(df);
        this.set(Feature.WRITE_DATES_AS_TIMESTAMPS, df == null);
    }

    @Deprecated
    public void setSerializationView(Class<?> view) {
        this._serializationView = view;
    }

    public String toString() {
        return "[SerializationConfig: flags=0x" + Integer.toHexString(this._featureFlags) + "]";
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Feature implements MapperConfig.ConfigFeature
    {
        USE_ANNOTATIONS(true),
        AUTO_DETECT_GETTERS(true),
        AUTO_DETECT_IS_GETTERS(true),
        AUTO_DETECT_FIELDS(true),
        CAN_OVERRIDE_ACCESS_MODIFIERS(true),
        REQUIRE_SETTERS_FOR_GETTERS(false),
        WRITE_NULL_PROPERTIES(true),
        USE_STATIC_TYPING(false),
        DEFAULT_VIEW_INCLUSION(true),
        WRAP_ROOT_VALUE(false),
        INDENT_OUTPUT(false),
        SORT_PROPERTIES_ALPHABETICALLY(false),
        FAIL_ON_EMPTY_BEANS(true),
        WRAP_EXCEPTIONS(true),
        CLOSE_CLOSEABLE(false),
        FLUSH_AFTER_WRITE_VALUE(true),
        WRITE_DATES_AS_TIMESTAMPS(true),
        WRITE_DATE_KEYS_AS_TIMESTAMPS(false),
        WRITE_CHAR_ARRAYS_AS_JSON_ARRAYS(false),
        WRITE_ENUMS_USING_TO_STRING(false),
        WRITE_ENUMS_USING_INDEX(false),
        WRITE_NULL_MAP_VALUES(true),
        WRITE_EMPTY_JSON_ARRAYS(true);

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


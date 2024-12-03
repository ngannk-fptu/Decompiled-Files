/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.introspect;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Map;
import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.ClassIntrospector;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.MapperConfig;
import org.codehaus.jackson.map.SerializationConfig;
import org.codehaus.jackson.map.introspect.AnnotatedClass;
import org.codehaus.jackson.map.introspect.BasicBeanDescription;
import org.codehaus.jackson.map.introspect.MethodFilter;
import org.codehaus.jackson.map.introspect.POJOPropertiesCollector;
import org.codehaus.jackson.map.type.SimpleType;
import org.codehaus.jackson.map.util.ClassUtil;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class BasicClassIntrospector
extends ClassIntrospector<BasicBeanDescription> {
    protected static final BasicBeanDescription STRING_DESC;
    protected static final BasicBeanDescription BOOLEAN_DESC;
    protected static final BasicBeanDescription INT_DESC;
    protected static final BasicBeanDescription LONG_DESC;
    @Deprecated
    public static final GetterMethodFilter DEFAULT_GETTER_FILTER;
    @Deprecated
    public static final SetterMethodFilter DEFAULT_SETTER_FILTER;
    @Deprecated
    public static final SetterAndGetterMethodFilter DEFAULT_SETTER_AND_GETTER_FILTER;
    protected static final MethodFilter MINIMAL_FILTER;
    public static final BasicClassIntrospector instance;

    @Override
    public BasicBeanDescription forSerialization(SerializationConfig cfg, JavaType type, ClassIntrospector.MixInResolver r) {
        BasicBeanDescription desc = this._findCachedDesc(type);
        if (desc == null) {
            desc = BasicBeanDescription.forSerialization(this.collectProperties(cfg, type, r, true));
        }
        return desc;
    }

    @Override
    public BasicBeanDescription forDeserialization(DeserializationConfig cfg, JavaType type, ClassIntrospector.MixInResolver r) {
        BasicBeanDescription desc = this._findCachedDesc(type);
        if (desc == null) {
            desc = BasicBeanDescription.forDeserialization(this.collectProperties(cfg, type, r, false));
        }
        return desc;
    }

    @Override
    public BasicBeanDescription forCreation(DeserializationConfig cfg, JavaType type, ClassIntrospector.MixInResolver r) {
        BasicBeanDescription desc = this._findCachedDesc(type);
        if (desc == null) {
            desc = BasicBeanDescription.forDeserialization(this.collectProperties(cfg, type, r, false));
        }
        return desc;
    }

    @Override
    public BasicBeanDescription forClassAnnotations(MapperConfig<?> cfg, JavaType type, ClassIntrospector.MixInResolver r) {
        boolean useAnnotations = cfg.isAnnotationProcessingEnabled();
        AnnotationIntrospector ai = cfg.getAnnotationIntrospector();
        AnnotatedClass ac = AnnotatedClass.construct(type.getRawClass(), useAnnotations ? ai : null, r);
        return BasicBeanDescription.forOtherUse(cfg, type, ac);
    }

    @Override
    public BasicBeanDescription forDirectClassAnnotations(MapperConfig<?> cfg, JavaType type, ClassIntrospector.MixInResolver r) {
        boolean useAnnotations = cfg.isAnnotationProcessingEnabled();
        AnnotationIntrospector ai = cfg.getAnnotationIntrospector();
        AnnotatedClass ac = AnnotatedClass.constructWithoutSuperTypes(type.getRawClass(), useAnnotations ? ai : null, r);
        return BasicBeanDescription.forOtherUse(cfg, type, ac);
    }

    public POJOPropertiesCollector collectProperties(MapperConfig<?> config, JavaType type, ClassIntrospector.MixInResolver r, boolean forSerialization) {
        AnnotatedClass ac = this.classWithCreators(config, type, r);
        ac.resolveMemberMethods(MINIMAL_FILTER);
        ac.resolveFields();
        return this.constructPropertyCollector(config, ac, type, forSerialization).collect();
    }

    protected POJOPropertiesCollector constructPropertyCollector(MapperConfig<?> config, AnnotatedClass ac, JavaType type, boolean forSerialization) {
        return new POJOPropertiesCollector(config, forSerialization, type, ac);
    }

    public AnnotatedClass classWithCreators(MapperConfig<?> config, JavaType type, ClassIntrospector.MixInResolver r) {
        boolean useAnnotations = config.isAnnotationProcessingEnabled();
        AnnotationIntrospector ai = config.getAnnotationIntrospector();
        AnnotatedClass ac = AnnotatedClass.construct(type.getRawClass(), useAnnotations ? ai : null, r);
        ac.resolveMemberMethods(MINIMAL_FILTER);
        ac.resolveCreators(true);
        return ac;
    }

    protected BasicBeanDescription _findCachedDesc(JavaType type) {
        Class<?> cls = type.getRawClass();
        if (cls == String.class) {
            return STRING_DESC;
        }
        if (cls == Boolean.TYPE) {
            return BOOLEAN_DESC;
        }
        if (cls == Integer.TYPE) {
            return INT_DESC;
        }
        if (cls == Long.TYPE) {
            return LONG_DESC;
        }
        return null;
    }

    @Deprecated
    protected MethodFilter getSerializationMethodFilter(SerializationConfig cfg) {
        return DEFAULT_GETTER_FILTER;
    }

    @Deprecated
    protected MethodFilter getDeserializationMethodFilter(DeserializationConfig cfg) {
        if (cfg.isEnabled(DeserializationConfig.Feature.USE_GETTERS_AS_SETTERS)) {
            return DEFAULT_SETTER_AND_GETTER_FILTER;
        }
        return DEFAULT_SETTER_FILTER;
    }

    static {
        AnnotatedClass ac = AnnotatedClass.constructWithoutSuperTypes(String.class, null, null);
        STRING_DESC = BasicBeanDescription.forOtherUse(null, SimpleType.constructUnsafe(String.class), ac);
        ac = AnnotatedClass.constructWithoutSuperTypes(Boolean.TYPE, null, null);
        BOOLEAN_DESC = BasicBeanDescription.forOtherUse(null, SimpleType.constructUnsafe(Boolean.TYPE), ac);
        ac = AnnotatedClass.constructWithoutSuperTypes(Integer.TYPE, null, null);
        INT_DESC = BasicBeanDescription.forOtherUse(null, SimpleType.constructUnsafe(Integer.TYPE), ac);
        ac = AnnotatedClass.constructWithoutSuperTypes(Long.TYPE, null, null);
        LONG_DESC = BasicBeanDescription.forOtherUse(null, SimpleType.constructUnsafe(Long.TYPE), ac);
        DEFAULT_GETTER_FILTER = new GetterMethodFilter();
        DEFAULT_SETTER_FILTER = new SetterMethodFilter();
        DEFAULT_SETTER_AND_GETTER_FILTER = new SetterAndGetterMethodFilter();
        MINIMAL_FILTER = new MinimalMethodFilter();
        instance = new BasicClassIntrospector();
    }

    @Deprecated
    public static final class SetterAndGetterMethodFilter
    extends SetterMethodFilter {
        public boolean includeMethod(Method m) {
            if (super.includeMethod(m)) {
                return true;
            }
            if (!ClassUtil.hasGetterSignature(m)) {
                return false;
            }
            Class<?> rt = m.getReturnType();
            return Collection.class.isAssignableFrom(rt) || Map.class.isAssignableFrom(rt);
        }
    }

    @Deprecated
    public static class SetterMethodFilter
    implements MethodFilter {
        public boolean includeMethod(Method m) {
            if (Modifier.isStatic(m.getModifiers())) {
                return false;
            }
            int pcount = m.getParameterTypes().length;
            switch (pcount) {
                case 1: {
                    return true;
                }
                case 2: {
                    return true;
                }
            }
            return false;
        }
    }

    @Deprecated
    public static class GetterMethodFilter
    implements MethodFilter {
        private GetterMethodFilter() {
        }

        public boolean includeMethod(Method m) {
            return ClassUtil.hasGetterSignature(m);
        }
    }

    private static class MinimalMethodFilter
    implements MethodFilter {
        private MinimalMethodFilter() {
        }

        public boolean includeMethod(Method m) {
            if (Modifier.isStatic(m.getModifiers())) {
                return false;
            }
            int pcount = m.getParameterTypes().length;
            return pcount <= 2;
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.build;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.build.Plugin;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.ToStringMethod;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@HashCodeAndEqualsPlugin.Enhance
public class ToStringPlugin
implements Plugin,
Plugin.Factory {
    private static final MethodDescription.InDefinedShape ENHANCE_PREFIX;
    private static final MethodDescription.InDefinedShape ENHANCE_INCLUDE_SYNTHETIC_FIELDS;

    @Override
    public Plugin make() {
        return this;
    }

    @Override
    public boolean matches(@MaybeNull TypeDescription target) {
        return target != null && target.getDeclaredAnnotations().isAnnotationPresent(Enhance.class);
    }

    @Override
    public DynamicType.Builder<?> apply(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassFileLocator classFileLocator) {
        AnnotationDescription.Loadable<Enhance> enhance = typeDescription.getDeclaredAnnotations().ofType(Enhance.class);
        if (((MethodList)typeDescription.getDeclaredMethods().filter(ElementMatchers.isToString())).isEmpty()) {
            builder = builder.method(ElementMatchers.isToString()).intercept(ToStringMethod.prefixedBy(enhance.getValue(ENHANCE_PREFIX).load(Enhance.class.getClassLoader()).resolve(Enhance.Prefix.class).getPrefixResolver()).withIgnoredFields(enhance.getValue(ENHANCE_INCLUDE_SYNTHETIC_FIELDS).resolve(Boolean.class) != false ? ElementMatchers.none() : ElementMatchers.isSynthetic()).withIgnoredFields(ElementMatchers.isAnnotatedWith(Exclude.class)));
        }
        return builder;
    }

    @Override
    public void close() {
    }

    static {
        MethodList<MethodDescription.InDefinedShape> enhanceMethods = TypeDescription.ForLoadedType.of(Enhance.class).getDeclaredMethods();
        ENHANCE_PREFIX = (MethodDescription.InDefinedShape)((MethodList)enhanceMethods.filter(ElementMatchers.named("prefix"))).getOnly();
        ENHANCE_INCLUDE_SYNTHETIC_FIELDS = (MethodDescription.InDefinedShape)((MethodList)enhanceMethods.filter(ElementMatchers.named("includeSyntheticFields"))).getOnly();
    }

    public boolean equals(@MaybeNull Object object) {
        if (this == object) {
            return true;
        }
        if (object == null) {
            return false;
        }
        return this.getClass() == object.getClass();
    }

    public int hashCode() {
        return this.getClass().hashCode();
    }

    @Documented
    @Target(value={ElementType.FIELD})
    @Retention(value=RetentionPolicy.RUNTIME)
    public static @interface Exclude {
    }

    @Documented
    @Target(value={ElementType.TYPE})
    @Retention(value=RetentionPolicy.RUNTIME)
    public static @interface Enhance {
        public Prefix prefix() default Prefix.SIMPLE;

        public boolean includeSyntheticFields() default false;

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum Prefix {
            FULLY_QUALIFIED(ToStringMethod.PrefixResolver.Default.FULLY_QUALIFIED_CLASS_NAME),
            CANONICAL(ToStringMethod.PrefixResolver.Default.CANONICAL_CLASS_NAME),
            SIMPLE(ToStringMethod.PrefixResolver.Default.SIMPLE_CLASS_NAME);

            private final ToStringMethod.PrefixResolver.Default prefixResolver;

            private Prefix(ToStringMethod.PrefixResolver.Default prefixResolver) {
                this.prefixResolver = prefixResolver;
            }

            protected ToStringMethod.PrefixResolver.Default getPrefixResolver() {
                return this.prefixResolver;
            }
        }
    }
}


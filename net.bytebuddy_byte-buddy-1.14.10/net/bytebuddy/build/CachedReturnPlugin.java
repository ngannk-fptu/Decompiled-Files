/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package net.bytebuddy.build;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.Map;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.build.Plugin;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.field.FieldList;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.modifier.FieldPersistence;
import net.bytebuddy.description.modifier.Ownership;
import net.bytebuddy.description.modifier.SyntheticState;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.ClassFileLocator;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.pool.TypePool;
import net.bytebuddy.utility.RandomString;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@HashCodeAndEqualsPlugin.Enhance
public class CachedReturnPlugin
extends Plugin.ForElementMatcher
implements Plugin.Factory {
    private static final String NAME_INFIX = "_";
    private static final String ADVICE_INFIX = "$Advice$";
    private static final MethodDescription.InDefinedShape ENHANCE_VALUE = (MethodDescription.InDefinedShape)((MethodList)TypeDescription.ForLoadedType.of(Enhance.class).getDeclaredMethods().filter(ElementMatchers.named("value"))).getOnly();
    private final boolean ignoreExistingFields;
    @HashCodeAndEqualsPlugin.ValueHandling(value=HashCodeAndEqualsPlugin.ValueHandling.Sort.IGNORE)
    private final RandomString randomString;
    private final ClassFileLocator classFileLocator;
    @HashCodeAndEqualsPlugin.ValueHandling(value=HashCodeAndEqualsPlugin.ValueHandling.Sort.IGNORE)
    private final Map<TypeDescription, TypeDescription> adviceByType;

    public CachedReturnPlugin() {
        this(false);
    }

    public CachedReturnPlugin(boolean ignoreExistingFields) {
        super(ElementMatchers.declaresMethod(ElementMatchers.isAnnotatedWith(Enhance.class)));
        this.ignoreExistingFields = ignoreExistingFields;
        this.randomString = new RandomString();
        this.classFileLocator = ClassFileLocator.ForClassLoader.of(CachedReturnPlugin.class.getClassLoader());
        TypePool typePool = TypePool.Default.of(this.classFileLocator);
        this.adviceByType = new HashMap<TypeDescription, TypeDescription>();
        for (Class type : new Class[]{Boolean.TYPE, Byte.TYPE, Short.TYPE, Character.TYPE, Integer.TYPE, Long.TYPE, Float.TYPE, Double.TYPE, Object.class}) {
            this.adviceByType.put(TypeDescription.ForLoadedType.of(type), typePool.describe(CachedReturnPlugin.class.getName() + ADVICE_INFIX + type.getSimpleName()).resolve());
        }
    }

    @Override
    public Plugin make() {
        return this;
    }

    @Override
    @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Annotation presence is required by matcher.")
    public DynamicType.Builder<?> apply(DynamicType.Builder<?> builder, TypeDescription typeDescription, ClassFileLocator classFileLocator) {
        for (MethodDescription.InDefinedShape methodDescription : (MethodList)typeDescription.getDeclaredMethods().filter(ElementMatchers.not(ElementMatchers.isBridge()).and(ElementMatchers.isAnnotatedWith(Enhance.class)))) {
            if (methodDescription.isAbstract()) {
                throw new IllegalStateException("Cannot cache the value of an abstract method: " + methodDescription);
            }
            if (!methodDescription.getParameters().isEmpty()) {
                throw new IllegalStateException("Cannot cache the value of a method with parameters: " + methodDescription);
            }
            if (methodDescription.getReturnType().represents(Void.TYPE)) {
                throw new IllegalStateException("Cannot cache void result for " + methodDescription);
            }
            String name = methodDescription.getDeclaredAnnotations().ofType(Enhance.class).getValue(ENHANCE_VALUE).resolve(String.class);
            if (name.length() == 0) {
                name = methodDescription.getName() + NAME_INFIX + this.randomString.nextString();
            } else if (this.ignoreExistingFields && !((FieldList)typeDescription.getDeclaredFields().filter(ElementMatchers.named(name))).isEmpty()) {
                return builder;
            }
            builder = builder.defineField(name, (TypeDefinition)methodDescription.getReturnType().asErasure(), methodDescription.isStatic() ? Ownership.STATIC : Ownership.MEMBER, methodDescription.isStatic() ? FieldPersistence.PLAIN : FieldPersistence.TRANSIENT, Visibility.PRIVATE, SyntheticState.SYNTHETIC).visit(Advice.withCustomMapping().bind(CacheField.class, new CacheFieldOffsetMapping(name)).to(this.adviceByType.get(methodDescription.getReturnType().isPrimitive() ? methodDescription.getReturnType().asErasure() : TypeDescription.ForLoadedType.of(Object.class)), this.classFileLocator).on(ElementMatchers.is(methodDescription)));
        }
        return builder;
    }

    @Override
    public void close() {
    }

    @Override
    public boolean equals(@MaybeNull Object object) {
        if (!super.equals(object)) {
            return false;
        }
        if (this == object) {
            return true;
        }
        if (object == null) {
            return false;
        }
        if (this.getClass() != object.getClass()) {
            return false;
        }
        if (this.ignoreExistingFields != ((CachedReturnPlugin)object).ignoreExistingFields) {
            return false;
        }
        return this.classFileLocator.equals(((CachedReturnPlugin)object).classFileLocator);
    }

    @Override
    public int hashCode() {
        return (super.hashCode() * 31 + this.ignoreExistingFields) * 31 + this.classFileLocator.hashCode();
    }

    @HashCodeAndEqualsPlugin.Enhance
    protected static class CacheFieldOffsetMapping
    implements Advice.OffsetMapping {
        private final String name;

        protected CacheFieldOffsetMapping(String name) {
            this.name = name;
        }

        public Advice.OffsetMapping.Target resolve(TypeDescription instrumentedType, MethodDescription instrumentedMethod, Assigner assigner, Advice.ArgumentHandler argumentHandler, Advice.OffsetMapping.Sort sort) {
            return new Advice.OffsetMapping.Target.ForField.ReadWrite((FieldDescription)((FieldList)instrumentedType.getDeclaredFields().filter(ElementMatchers.named(this.name))).getOnly());
        }

        public boolean equals(@MaybeNull Object object) {
            if (this == object) {
                return true;
            }
            if (object == null) {
                return false;
            }
            if (this.getClass() != object.getClass()) {
                return false;
            }
            return this.name.equals(((CacheFieldOffsetMapping)object).name);
        }

        public int hashCode() {
            return this.getClass().hashCode() * 31 + this.name.hashCode();
        }
    }

    @Target(value={ElementType.PARAMETER})
    @Retention(value=RetentionPolicy.RUNTIME)
    protected static @interface CacheField {
    }

    @Documented
    @Target(value={ElementType.METHOD})
    @Retention(value=RetentionPolicy.RUNTIME)
    public static @interface Enhance {
        public String value() default "";
    }
}


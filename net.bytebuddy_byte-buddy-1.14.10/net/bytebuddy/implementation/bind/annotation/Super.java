/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.implementation.bind.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Type;
import java.util.Arrays;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.enumeration.EnumerationDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.TargetType;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.auxiliary.TypeProxy;
import net.bytebuddy.implementation.bind.MethodDelegationBinder;
import net.bytebuddy.implementation.bind.annotation.TargetMethodAnnotationDrivenBinder;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Documented
@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.PARAMETER})
public @interface Super {
    public Instantiation strategy() default Instantiation.CONSTRUCTOR;

    public boolean ignoreFinalizer() default true;

    public boolean serializableProxy() default false;

    public Class<?>[] constructorParameters() default {};

    public Class<?> proxyType() default void.class;

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Binder implements TargetMethodAnnotationDrivenBinder.ParameterBinder<Super>
    {
        INSTANCE;

        private static final MethodDescription.InDefinedShape STRATEGY;
        private static final MethodDescription.InDefinedShape PROXY_TYPE;

        @Override
        public Class<Super> getHandledType() {
            return Super.class;
        }

        @Override
        public MethodDelegationBinder.ParameterBinding<?> bind(AnnotationDescription.Loadable<Super> annotation, MethodDescription source, ParameterDescription target, Implementation.Target implementationTarget, Assigner assigner, Assigner.Typing typing) {
            if (target.getType().isPrimitive() || target.getType().isArray()) {
                throw new IllegalStateException(target + " uses the @Super annotation on an invalid type");
            }
            TypeDescription proxyType = TypeLocator.ForType.of(annotation.getValue(PROXY_TYPE).resolve(TypeDescription.class)).resolve(implementationTarget.getInstrumentedType(), target.getType());
            if (proxyType.isFinal()) {
                throw new IllegalStateException("Cannot extend final type as @Super proxy: " + proxyType);
            }
            if (source.isStatic() || !implementationTarget.getInstrumentedType().isAssignableTo(proxyType)) {
                return MethodDelegationBinder.ParameterBinding.Illegal.INSTANCE;
            }
            return new MethodDelegationBinder.ParameterBinding.Anonymous(annotation.getValue(STRATEGY).resolve(EnumerationDescription.class).load(Instantiation.class).proxyFor(proxyType, implementationTarget, annotation));
        }

        static {
            MethodList<MethodDescription.InDefinedShape> annotationProperties = TypeDescription.ForLoadedType.of(Super.class).getDeclaredMethods();
            STRATEGY = (MethodDescription.InDefinedShape)((MethodList)annotationProperties.filter(ElementMatchers.named("strategy"))).getOnly();
            PROXY_TYPE = (MethodDescription.InDefinedShape)((MethodList)annotationProperties.filter(ElementMatchers.named("proxyType"))).getOnly();
        }

        protected static interface TypeLocator {
            public TypeDescription resolve(TypeDescription var1, TypeDescription.Generic var2);

            @HashCodeAndEqualsPlugin.Enhance
            public static class ForType
            implements TypeLocator {
                private final TypeDescription typeDescription;

                protected ForType(TypeDescription typeDescription) {
                    this.typeDescription = typeDescription;
                }

                protected static TypeLocator of(TypeDescription typeDescription) {
                    if (typeDescription.represents(Void.TYPE)) {
                        return ForParameterType.INSTANCE;
                    }
                    if (typeDescription.represents((Type)((Object)TargetType.class))) {
                        return ForInstrumentedType.INSTANCE;
                    }
                    if (typeDescription.isPrimitive() || typeDescription.isArray()) {
                        throw new IllegalStateException("Cannot assign proxy to " + typeDescription);
                    }
                    return new ForType(typeDescription);
                }

                public TypeDescription resolve(TypeDescription instrumentedType, TypeDescription.Generic parameterType) {
                    if (!this.typeDescription.isAssignableTo(parameterType.asErasure())) {
                        throw new IllegalStateException("Impossible to assign " + this.typeDescription + " to parameter of type " + parameterType);
                    }
                    return this.typeDescription;
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
                    return this.typeDescription.equals(((ForType)object).typeDescription);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.typeDescription.hashCode();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static enum ForParameterType implements TypeLocator
            {
                INSTANCE;


                @Override
                public TypeDescription resolve(TypeDescription instrumentedType, TypeDescription.Generic parameterType) {
                    TypeDescription erasure = parameterType.asErasure();
                    return erasure.equals(instrumentedType) ? instrumentedType : erasure;
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static enum ForInstrumentedType implements TypeLocator
            {
                INSTANCE;


                @Override
                public TypeDescription resolve(TypeDescription instrumentedType, TypeDescription.Generic parameterType) {
                    return instrumentedType;
                }
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Instantiation {
        CONSTRUCTOR{

            @Override
            protected StackManipulation proxyFor(TypeDescription parameterType, Implementation.Target implementationTarget, AnnotationDescription.Loadable<Super> annotation) {
                return new TypeProxy.ForSuperMethodByConstructor(parameterType, implementationTarget, Arrays.asList((Object[])annotation.getValue(CONSTRUCTOR_PARAMETERS).resolve(TypeDescription[].class)), annotation.getValue(IGNORE_FINALIZER).resolve(Boolean.class), annotation.getValue(SERIALIZABLE_PROXY).resolve(Boolean.class));
            }
        }
        ,
        UNSAFE{

            @Override
            protected StackManipulation proxyFor(TypeDescription parameterType, Implementation.Target implementationTarget, AnnotationDescription.Loadable<Super> annotation) {
                return new TypeProxy.ForSuperMethodByReflectionFactory(parameterType, implementationTarget, annotation.getValue(IGNORE_FINALIZER).resolve(Boolean.class), annotation.getValue(SERIALIZABLE_PROXY).resolve(Boolean.class));
            }
        };

        private static final MethodDescription.InDefinedShape IGNORE_FINALIZER;
        private static final MethodDescription.InDefinedShape SERIALIZABLE_PROXY;
        private static final MethodDescription.InDefinedShape CONSTRUCTOR_PARAMETERS;

        protected abstract StackManipulation proxyFor(TypeDescription var1, Implementation.Target var2, AnnotationDescription.Loadable<Super> var3);

        static {
            MethodList<MethodDescription.InDefinedShape> annotationProperties = TypeDescription.ForLoadedType.of(Super.class).getDeclaredMethods();
            IGNORE_FINALIZER = (MethodDescription.InDefinedShape)((MethodList)annotationProperties.filter(ElementMatchers.named("ignoreFinalizer"))).getOnly();
            SERIALIZABLE_PROXY = (MethodDescription.InDefinedShape)((MethodList)annotationProperties.filter(ElementMatchers.named("serializableProxy"))).getOnly();
            CONSTRUCTOR_PARAMETERS = (MethodDescription.InDefinedShape)((MethodList)annotationProperties.filter(ElementMatchers.named("constructorParameters"))).getOnly();
        }
    }
}


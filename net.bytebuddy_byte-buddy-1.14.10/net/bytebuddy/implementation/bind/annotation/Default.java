/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.implementation.bind.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.auxiliary.TypeProxy;
import net.bytebuddy.implementation.bind.MethodDelegationBinder;
import net.bytebuddy.implementation.bind.annotation.TargetMethodAnnotationDrivenBinder;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Documented
@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.PARAMETER})
public @interface Default {
    public boolean serializableProxy() default false;

    public Class<?> proxyType() default void.class;

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Binder implements TargetMethodAnnotationDrivenBinder.ParameterBinder<Default>
    {
        INSTANCE;

        private static final MethodDescription.InDefinedShape SERIALIZABLE_PROXY;
        private static final MethodDescription.InDefinedShape PROXY_TYPE;

        @Override
        public Class<Default> getHandledType() {
            return Default.class;
        }

        @Override
        public MethodDelegationBinder.ParameterBinding<?> bind(AnnotationDescription.Loadable<Default> annotation, MethodDescription source, ParameterDescription target, Implementation.Target implementationTarget, Assigner assigner, Assigner.Typing typing) {
            TypeDescription proxyType = TypeLocator.ForType.of(annotation.getValue(PROXY_TYPE).resolve(TypeDescription.class)).resolve(target.getType());
            if (!proxyType.isInterface()) {
                throw new IllegalStateException(target + " uses the @Default annotation on an invalid type");
            }
            if (source.isStatic() || !implementationTarget.getInstrumentedType().getInterfaces().asErasures().contains(proxyType)) {
                return MethodDelegationBinder.ParameterBinding.Illegal.INSTANCE;
            }
            return new MethodDelegationBinder.ParameterBinding.Anonymous(new TypeProxy.ForDefaultMethod(proxyType, implementationTarget, annotation.getValue(SERIALIZABLE_PROXY).resolve(Boolean.class)));
        }

        static {
            MethodList<MethodDescription.InDefinedShape> annotationProperties = TypeDescription.ForLoadedType.of(Default.class).getDeclaredMethods();
            SERIALIZABLE_PROXY = (MethodDescription.InDefinedShape)((MethodList)annotationProperties.filter(ElementMatchers.named("serializableProxy"))).getOnly();
            PROXY_TYPE = (MethodDescription.InDefinedShape)((MethodList)annotationProperties.filter(ElementMatchers.named("proxyType"))).getOnly();
        }

        protected static interface TypeLocator {
            public TypeDescription resolve(TypeDescription.Generic var1);

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
                    if (!typeDescription.isInterface()) {
                        throw new IllegalStateException("Cannot assign proxy to " + typeDescription);
                    }
                    return new ForType(typeDescription);
                }

                public TypeDescription resolve(TypeDescription.Generic parameterType) {
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
                public TypeDescription resolve(TypeDescription.Generic parameterType) {
                    return parameterType.asErasure();
                }
            }
        }
    }
}


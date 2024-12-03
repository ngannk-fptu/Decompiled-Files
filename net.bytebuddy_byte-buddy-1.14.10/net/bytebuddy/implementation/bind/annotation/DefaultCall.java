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
import java.util.concurrent.Callable;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.auxiliary.MethodCallProxy;
import net.bytebuddy.implementation.bind.MethodDelegationBinder;
import net.bytebuddy.implementation.bind.annotation.TargetMethodAnnotationDrivenBinder;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.implementation.bytecode.constant.NullConstant;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Documented
@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.PARAMETER})
public @interface DefaultCall {
    public Class<?> targetType() default void.class;

    public boolean serializableProxy() default false;

    public boolean nullIfImpossible() default false;

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Binder implements TargetMethodAnnotationDrivenBinder.ParameterBinder<DefaultCall>
    {
        INSTANCE;

        private static final MethodDescription.InDefinedShape TARGET_TYPE;
        private static final MethodDescription.InDefinedShape SERIALIZABLE_PROXY;
        private static final MethodDescription.InDefinedShape NULL_IF_IMPOSSIBLE;

        @Override
        public Class<DefaultCall> getHandledType() {
            return DefaultCall.class;
        }

        @Override
        public MethodDelegationBinder.ParameterBinding<?> bind(AnnotationDescription.Loadable<DefaultCall> annotation, MethodDescription source, ParameterDescription target, Implementation.Target implementationTarget, Assigner assigner, Assigner.Typing typing) {
            StackManipulation stackManipulation;
            TypeDescription targetType = target.getType().asErasure();
            if (!(targetType.represents((Type)((Object)Runnable.class)) || targetType.represents((Type)((Object)Callable.class)) || targetType.represents((Type)((Object)Object.class)))) {
                throw new IllegalStateException("A default method call proxy can only be assigned to Runnable or Callable types: " + target);
            }
            if (source.isConstructor()) {
                return annotation.getValue(NULL_IF_IMPOSSIBLE).resolve(Boolean.class) != false ? new MethodDelegationBinder.ParameterBinding.Anonymous(NullConstant.INSTANCE) : MethodDelegationBinder.ParameterBinding.Illegal.INSTANCE;
            }
            TypeDescription typeDescription = annotation.getValue(TARGET_TYPE).resolve(TypeDescription.class);
            Implementation.SpecialMethodInvocation specialMethodInvocation = (typeDescription.represents(Void.TYPE) ? DefaultMethodLocator.Implicit.INSTANCE : new DefaultMethodLocator.Explicit(typeDescription)).resolve(implementationTarget, source).withCheckedCompatibilityTo(source.asTypeToken());
            if (specialMethodInvocation.isValid()) {
                stackManipulation = new MethodCallProxy.AssignableSignatureCall(specialMethodInvocation, annotation.getValue(SERIALIZABLE_PROXY).resolve(Boolean.class));
            } else if (annotation.getValue(NULL_IF_IMPOSSIBLE).resolve(Boolean.class).booleanValue()) {
                stackManipulation = NullConstant.INSTANCE;
            } else {
                return MethodDelegationBinder.ParameterBinding.Illegal.INSTANCE;
            }
            return new MethodDelegationBinder.ParameterBinding.Anonymous(stackManipulation);
        }

        static {
            MethodList<MethodDescription.InDefinedShape> annotationProperties = TypeDescription.ForLoadedType.of(DefaultCall.class).getDeclaredMethods();
            TARGET_TYPE = (MethodDescription.InDefinedShape)((MethodList)annotationProperties.filter(ElementMatchers.named("targetType"))).getOnly();
            SERIALIZABLE_PROXY = (MethodDescription.InDefinedShape)((MethodList)annotationProperties.filter(ElementMatchers.named("serializableProxy"))).getOnly();
            NULL_IF_IMPOSSIBLE = (MethodDescription.InDefinedShape)((MethodList)annotationProperties.filter(ElementMatchers.named("nullIfImpossible"))).getOnly();
        }

        protected static interface DefaultMethodLocator {
            public Implementation.SpecialMethodInvocation resolve(Implementation.Target var1, MethodDescription var2);

            @HashCodeAndEqualsPlugin.Enhance
            public static class Explicit
            implements DefaultMethodLocator {
                private final TypeDescription typeDescription;

                public Explicit(TypeDescription typeDescription) {
                    this.typeDescription = typeDescription;
                }

                public Implementation.SpecialMethodInvocation resolve(Implementation.Target implementationTarget, MethodDescription source) {
                    if (!this.typeDescription.isInterface()) {
                        throw new IllegalStateException(source + " method carries default method call parameter on non-interface type");
                    }
                    return implementationTarget.invokeDefault(source.asSignatureToken(), this.typeDescription);
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
                    return this.typeDescription.equals(((Explicit)object).typeDescription);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.typeDescription.hashCode();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static enum Implicit implements DefaultMethodLocator
            {
                INSTANCE;


                @Override
                public Implementation.SpecialMethodInvocation resolve(Implementation.Target implementationTarget, MethodDescription source) {
                    return implementationTarget.invokeDefault(source.asSignatureToken());
                }
            }
        }
    }
}


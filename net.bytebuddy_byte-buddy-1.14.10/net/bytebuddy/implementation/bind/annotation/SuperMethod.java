/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.implementation.bind.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.MethodAccessorFactory;
import net.bytebuddy.implementation.bind.MethodDelegationBinder;
import net.bytebuddy.implementation.bind.annotation.TargetMethodAnnotationDrivenBinder;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.implementation.bytecode.constant.MethodConstant;
import net.bytebuddy.implementation.bytecode.constant.NullConstant;
import net.bytebuddy.implementation.bytecode.member.FieldAccess;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.nullability.MaybeNull;

@Documented
@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.PARAMETER})
public @interface SuperMethod {
    public boolean cached() default true;

    public boolean privileged() default false;

    public boolean fallbackToDefault() default true;

    public boolean nullIfImpossible() default false;

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Binder implements TargetMethodAnnotationDrivenBinder.ParameterBinder<SuperMethod>
    {
        INSTANCE;

        private static final MethodDescription.InDefinedShape CACHED;
        private static final MethodDescription.InDefinedShape PRIVILEGED;
        private static final MethodDescription.InDefinedShape FALLBACK_TO_DEFAULT;
        private static final MethodDescription.InDefinedShape NULL_IF_IMPOSSIBLE;

        @Override
        public Class<SuperMethod> getHandledType() {
            return SuperMethod.class;
        }

        @Override
        public MethodDelegationBinder.ParameterBinding<?> bind(AnnotationDescription.Loadable<SuperMethod> annotation, MethodDescription source, ParameterDescription target, Implementation.Target implementationTarget, Assigner assigner, Assigner.Typing typing) {
            if (!target.getType().asErasure().isAssignableFrom(Method.class)) {
                throw new IllegalStateException("Cannot assign Method type to " + target);
            }
            if (source.isMethod()) {
                Implementation.SpecialMethodInvocation specialMethodInvocation = (annotation.getValue(FALLBACK_TO_DEFAULT).resolve(Boolean.class) != false ? implementationTarget.invokeDominant(source.asSignatureToken()) : implementationTarget.invokeSuper(source.asSignatureToken())).withCheckedCompatibilityTo(source.asTypeToken());
                if (specialMethodInvocation.isValid()) {
                    return new MethodDelegationBinder.ParameterBinding.Anonymous(new DelegationMethod(specialMethodInvocation, annotation.getValue(CACHED).resolve(Boolean.class), annotation.getValue(PRIVILEGED).resolve(Boolean.class)));
                }
                if (annotation.getValue(NULL_IF_IMPOSSIBLE).resolve(Boolean.class).booleanValue()) {
                    return new MethodDelegationBinder.ParameterBinding.Anonymous(NullConstant.INSTANCE);
                }
                return MethodDelegationBinder.ParameterBinding.Illegal.INSTANCE;
            }
            if (annotation.getValue(NULL_IF_IMPOSSIBLE).resolve(Boolean.class).booleanValue()) {
                return new MethodDelegationBinder.ParameterBinding.Anonymous(NullConstant.INSTANCE);
            }
            return MethodDelegationBinder.ParameterBinding.Illegal.INSTANCE;
        }

        static {
            MethodList<MethodDescription.InDefinedShape> methods = TypeDescription.ForLoadedType.of(SuperMethod.class).getDeclaredMethods();
            CACHED = (MethodDescription.InDefinedShape)((MethodList)methods.filter(ElementMatchers.named("cached"))).getOnly();
            PRIVILEGED = (MethodDescription.InDefinedShape)((MethodList)methods.filter(ElementMatchers.named("privileged"))).getOnly();
            FALLBACK_TO_DEFAULT = (MethodDescription.InDefinedShape)((MethodList)methods.filter(ElementMatchers.named("fallbackToDefault"))).getOnly();
            NULL_IF_IMPOSSIBLE = (MethodDescription.InDefinedShape)((MethodList)methods.filter(ElementMatchers.named("nullIfImpossible"))).getOnly();
        }

        @HashCodeAndEqualsPlugin.Enhance
        protected static class DelegationMethod
        implements StackManipulation {
            private final Implementation.SpecialMethodInvocation specialMethodInvocation;
            private final boolean cached;
            private final boolean privileged;

            protected DelegationMethod(Implementation.SpecialMethodInvocation specialMethodInvocation, boolean cached, boolean privileged) {
                this.specialMethodInvocation = specialMethodInvocation;
                this.cached = cached;
                this.privileged = privileged;
            }

            public boolean isValid() {
                return this.specialMethodInvocation.isValid();
            }

            public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
                MethodConstant.CanCache methodConstant = this.privileged ? MethodConstant.ofPrivileged(implementationContext.registerAccessorFor(this.specialMethodInvocation, MethodAccessorFactory.AccessType.PUBLIC)) : MethodConstant.of(implementationContext.registerAccessorFor(this.specialMethodInvocation, MethodAccessorFactory.AccessType.PUBLIC));
                return (this.cached ? FieldAccess.forField(implementationContext.cache(methodConstant, TypeDescription.ForLoadedType.of(Method.class))).read() : methodConstant).apply(methodVisitor, implementationContext);
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
                if (this.cached != ((DelegationMethod)object).cached) {
                    return false;
                }
                if (this.privileged != ((DelegationMethod)object).privileged) {
                    return false;
                }
                return this.specialMethodInvocation.equals(((DelegationMethod)object).specialMethodInvocation);
            }

            public int hashCode() {
                return ((this.getClass().hashCode() * 31 + this.specialMethodInvocation.hashCode()) * 31 + this.cached) * 31 + this.privileged;
            }
        }
    }
}


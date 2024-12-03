/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.implementation.bind.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bind.MethodDelegationBinder;
import net.bytebuddy.implementation.bind.annotation.TargetMethodAnnotationDrivenBinder;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.implementation.bytecode.constant.NullConstant;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaType;

@Documented
@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.PARAMETER})
public @interface SuperMethodHandle {
    public boolean fallbackToDefault() default true;

    public boolean nullIfImpossible() default false;

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Binder implements TargetMethodAnnotationDrivenBinder.ParameterBinder<SuperMethodHandle>
    {
        INSTANCE;

        private static final MethodDescription.InDefinedShape FALLBACK_TO_DEFAULT;
        private static final MethodDescription.InDefinedShape NULL_IF_IMPOSSIBLE;

        @Override
        public Class<SuperMethodHandle> getHandledType() {
            return SuperMethodHandle.class;
        }

        @Override
        public MethodDelegationBinder.ParameterBinding<?> bind(AnnotationDescription.Loadable<SuperMethodHandle> annotation, MethodDescription source, ParameterDescription target, Implementation.Target implementationTarget, Assigner assigner, Assigner.Typing typing) {
            if (!target.getType().asErasure().isAssignableFrom(JavaType.METHOD_HANDLE.getTypeStub())) {
                throw new IllegalStateException("Cannot assign MethodHandle type to " + target);
            }
            if (source.isMethod()) {
                Implementation.SpecialMethodInvocation specialMethodInvocation = (annotation.getValue(FALLBACK_TO_DEFAULT).resolve(Boolean.class) != false ? implementationTarget.invokeDominant(source.asSignatureToken()) : implementationTarget.invokeSuper(source.asSignatureToken())).withCheckedCompatibilityTo(source.asTypeToken());
                if (specialMethodInvocation.isValid()) {
                    return new MethodDelegationBinder.ParameterBinding.Anonymous(specialMethodInvocation.toMethodHandle().toStackManipulation());
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
            MethodList<MethodDescription.InDefinedShape> methods = TypeDescription.ForLoadedType.of(SuperMethodHandle.class).getDeclaredMethods();
            FALLBACK_TO_DEFAULT = (MethodDescription.InDefinedShape)((MethodList)methods.filter(ElementMatchers.named("fallbackToDefault"))).getOnly();
            NULL_IF_IMPOSSIBLE = (MethodDescription.InDefinedShape)((MethodList)methods.filter(ElementMatchers.named("nullIfImpossible"))).getOnly();
        }
    }
}


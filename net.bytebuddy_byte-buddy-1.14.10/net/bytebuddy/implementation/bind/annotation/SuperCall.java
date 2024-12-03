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

@Documented
@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.PARAMETER})
public @interface SuperCall {
    public boolean serializableProxy() default false;

    public boolean fallbackToDefault() default true;

    public boolean nullIfImpossible() default false;

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Binder implements TargetMethodAnnotationDrivenBinder.ParameterBinder<SuperCall>
    {
        INSTANCE;

        private static final MethodDescription.InDefinedShape SERIALIZABLE_PROXY;
        private static final MethodDescription.InDefinedShape FALLBACK_TO_DEFAULT;
        private static final MethodDescription.InDefinedShape NULL_IF_IMPOSSIBLE;

        @Override
        public Class<SuperCall> getHandledType() {
            return SuperCall.class;
        }

        @Override
        public MethodDelegationBinder.ParameterBinding<?> bind(AnnotationDescription.Loadable<SuperCall> annotation, MethodDescription source, ParameterDescription target, Implementation.Target implementationTarget, Assigner assigner, Assigner.Typing typing) {
            StackManipulation stackManipulation;
            TypeDescription targetType = target.getType().asErasure();
            if (!(targetType.represents((Type)((Object)Runnable.class)) || targetType.represents((Type)((Object)Callable.class)) || targetType.represents((Type)((Object)Object.class)))) {
                throw new IllegalStateException("A super method call proxy can only be assigned to Runnable or Callable types: " + target);
            }
            if (source.isConstructor()) {
                return annotation.getValue(NULL_IF_IMPOSSIBLE).resolve(Boolean.class) != false ? new MethodDelegationBinder.ParameterBinding.Anonymous(NullConstant.INSTANCE) : MethodDelegationBinder.ParameterBinding.Illegal.INSTANCE;
            }
            Implementation.SpecialMethodInvocation specialMethodInvocation = (annotation.getValue(FALLBACK_TO_DEFAULT).resolve(Boolean.class) != false ? implementationTarget.invokeDominant(source.asSignatureToken()) : implementationTarget.invokeSuper(source.asSignatureToken())).withCheckedCompatibilityTo(source.asTypeToken());
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
            MethodList<MethodDescription.InDefinedShape> methods = TypeDescription.ForLoadedType.of(SuperCall.class).getDeclaredMethods();
            SERIALIZABLE_PROXY = (MethodDescription.InDefinedShape)((MethodList)methods.filter(ElementMatchers.named("serializableProxy"))).getOnly();
            FALLBACK_TO_DEFAULT = (MethodDescription.InDefinedShape)((MethodList)methods.filter(ElementMatchers.named("fallbackToDefault"))).getOnly();
            NULL_IF_IMPOSSIBLE = (MethodDescription.InDefinedShape)((MethodList)methods.filter(ElementMatchers.named("nullIfImpossible"))).getOnly();
        }
    }
}


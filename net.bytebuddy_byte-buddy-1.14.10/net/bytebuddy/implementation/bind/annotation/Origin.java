/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.implementation.bind.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bind.MethodDelegationBinder;
import net.bytebuddy.implementation.bind.annotation.TargetMethodAnnotationDrivenBinder;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.implementation.bytecode.constant.ClassConstant;
import net.bytebuddy.implementation.bytecode.constant.IntegerConstant;
import net.bytebuddy.implementation.bytecode.constant.MethodConstant;
import net.bytebuddy.implementation.bytecode.constant.TextConstant;
import net.bytebuddy.implementation.bytecode.member.MethodInvocation;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaConstant;
import net.bytebuddy.utility.JavaType;

@Documented
@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.PARAMETER})
public @interface Origin {
    public boolean cache() default true;

    public boolean privileged() default false;

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Binder implements TargetMethodAnnotationDrivenBinder.ParameterBinder<Origin>
    {
        INSTANCE;

        private static final MethodDescription.InDefinedShape CACHE;
        private static final MethodDescription.InDefinedShape PRIVILEGED;

        private static StackManipulation methodConstant(AnnotationDescription.Loadable<Origin> annotation, MethodDescription.InDefinedShape methodDescription) {
            MethodConstant.CanCache methodConstant = annotation.getValue(PRIVILEGED).resolve(Boolean.class) != false ? MethodConstant.ofPrivileged(methodDescription) : MethodConstant.of(methodDescription);
            return annotation.getValue(CACHE).resolve(Boolean.class) != false ? methodConstant.cached() : methodConstant;
        }

        @Override
        public Class<Origin> getHandledType() {
            return Origin.class;
        }

        @Override
        public MethodDelegationBinder.ParameterBinding<?> bind(AnnotationDescription.Loadable<Origin> annotation, MethodDescription source, ParameterDescription target, Implementation.Target implementationTarget, Assigner assigner, Assigner.Typing typing) {
            TypeDescription parameterType = target.getType().asErasure();
            if (parameterType.represents((Type)((Object)Class.class))) {
                return new MethodDelegationBinder.ParameterBinding.Anonymous(ClassConstant.of(implementationTarget.getOriginType().asErasure()));
            }
            if (parameterType.represents((Type)((Object)Method.class))) {
                return source.isMethod() ? new MethodDelegationBinder.ParameterBinding.Anonymous(Binder.methodConstant(annotation, (MethodDescription.InDefinedShape)source.asDefined())) : MethodDelegationBinder.ParameterBinding.Illegal.INSTANCE;
            }
            if (parameterType.represents((Type)((Object)Constructor.class))) {
                return source.isConstructor() ? new MethodDelegationBinder.ParameterBinding.Anonymous(Binder.methodConstant(annotation, (MethodDescription.InDefinedShape)source.asDefined())) : MethodDelegationBinder.ParameterBinding.Illegal.INSTANCE;
            }
            if (JavaType.EXECUTABLE.getTypeStub().equals(parameterType)) {
                return new MethodDelegationBinder.ParameterBinding.Anonymous(Binder.methodConstant(annotation, (MethodDescription.InDefinedShape)source.asDefined()));
            }
            if (parameterType.represents((Type)((Object)String.class))) {
                return new MethodDelegationBinder.ParameterBinding.Anonymous(new TextConstant(source.toString()));
            }
            if (parameterType.represents(Integer.TYPE)) {
                return new MethodDelegationBinder.ParameterBinding.Anonymous(IntegerConstant.forValue(source.getModifiers()));
            }
            if (parameterType.equals(JavaType.METHOD_HANDLE.getTypeStub())) {
                return new MethodDelegationBinder.ParameterBinding.Anonymous(JavaConstant.MethodHandle.of((MethodDescription.InDefinedShape)source.asDefined()).toStackManipulation());
            }
            if (parameterType.equals(JavaType.METHOD_TYPE.getTypeStub())) {
                return new MethodDelegationBinder.ParameterBinding.Anonymous(JavaConstant.MethodType.of((MethodDescription)source.asDefined()).toStackManipulation());
            }
            if (parameterType.equals(JavaType.METHOD_HANDLES_LOOKUP.getTypeStub())) {
                return new MethodDelegationBinder.ParameterBinding.Anonymous(MethodInvocation.lookup());
            }
            throw new IllegalStateException("The " + target + " method's " + target.getIndex() + " parameter is annotated with a Origin annotation with an argument not representing a Class, Method, Constructor, String, int, MethodType or MethodHandle type");
        }

        static {
            MethodList<MethodDescription.InDefinedShape> methods = TypeDescription.ForLoadedType.of(Origin.class).getDeclaredMethods();
            CACHE = (MethodDescription.InDefinedShape)((MethodList)methods.filter(ElementMatchers.named("cache"))).getOnly();
            PRIVILEGED = (MethodDescription.InDefinedShape)((MethodList)methods.filter(ElementMatchers.named("privileged"))).getOnly();
        }
    }
}


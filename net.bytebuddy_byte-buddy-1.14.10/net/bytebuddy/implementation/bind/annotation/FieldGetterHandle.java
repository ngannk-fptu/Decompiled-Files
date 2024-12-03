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
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeList;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bind.MethodDelegationBinder;
import net.bytebuddy.implementation.bind.annotation.TargetMethodAnnotationDrivenBinder;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.implementation.bytecode.member.MethodInvocation;
import net.bytebuddy.implementation.bytecode.member.MethodVariableAccess;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaConstant;
import net.bytebuddy.utility.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@Documented
@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.PARAMETER})
public @interface FieldGetterHandle {
    public String value() default "";

    public Class<?> declaringType() default void.class;

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Binder implements TargetMethodAnnotationDrivenBinder.ParameterBinder<FieldGetterHandle>
    {
        INSTANCE(new Delegate());

        private static final MethodDescription.InDefinedShape DECLARING_TYPE;
        private static final MethodDescription.InDefinedShape FIELD_NAME;
        private final TargetMethodAnnotationDrivenBinder.ParameterBinder<FieldGetterHandle> delegate;

        private Binder(TargetMethodAnnotationDrivenBinder.ParameterBinder<FieldGetterHandle> delegate) {
            this.delegate = delegate;
        }

        @Override
        public Class<FieldGetterHandle> getHandledType() {
            return this.delegate.getHandledType();
        }

        @Override
        public MethodDelegationBinder.ParameterBinding<?> bind(AnnotationDescription.Loadable<FieldGetterHandle> annotation, MethodDescription source, ParameterDescription target, Implementation.Target implementationTarget, Assigner assigner, Assigner.Typing typing) {
            return this.delegate.bind(annotation, source, target, implementationTarget, assigner, typing);
        }

        static {
            MethodList<MethodDescription.InDefinedShape> methodList = TypeDescription.ForLoadedType.of(FieldGetterHandle.class).getDeclaredMethods();
            DECLARING_TYPE = (MethodDescription.InDefinedShape)((MethodList)methodList.filter(ElementMatchers.named("declaringType"))).getOnly();
            FIELD_NAME = (MethodDescription.InDefinedShape)((MethodList)methodList.filter(ElementMatchers.named("value"))).getOnly();
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        protected static class Delegate
        extends TargetMethodAnnotationDrivenBinder.ParameterBinder.ForFieldBinding<FieldGetterHandle> {
            protected Delegate() {
            }

            @Override
            public Class<FieldGetterHandle> getHandledType() {
                return FieldGetterHandle.class;
            }

            @Override
            protected String fieldName(AnnotationDescription.Loadable<FieldGetterHandle> annotation) {
                return annotation.getValue(FIELD_NAME).resolve(String.class);
            }

            @Override
            protected TypeDescription declaringType(AnnotationDescription.Loadable<FieldGetterHandle> annotation) {
                return annotation.getValue(DECLARING_TYPE).resolve(TypeDescription.class);
            }

            @Override
            protected MethodDelegationBinder.ParameterBinding<?> bind(FieldDescription fieldDescription, AnnotationDescription.Loadable<FieldGetterHandle> annotation, MethodDescription source, ParameterDescription target, Implementation.Target implementationTarget, Assigner assigner) {
                if (!target.getType().asErasure().isAssignableFrom(JavaType.METHOD_HANDLE.getTypeStub())) {
                    throw new IllegalStateException("Cannot assign method handle to " + target);
                }
                if (fieldDescription.isStatic()) {
                    return new MethodDelegationBinder.ParameterBinding.Anonymous(JavaConstant.MethodHandle.ofGetter((FieldDescription.InDefinedShape)fieldDescription.asDefined()).toStackManipulation());
                }
                return new MethodDelegationBinder.ParameterBinding.Anonymous(new StackManipulation.Compound(JavaConstant.MethodHandle.ofGetter((FieldDescription.InDefinedShape)fieldDescription.asDefined()).toStackManipulation(), MethodVariableAccess.loadThis(), MethodInvocation.invoke(new MethodDescription.Latent(JavaType.METHOD_HANDLE.getTypeStub(), new MethodDescription.Token("bindTo", 1, JavaType.METHOD_HANDLE.getTypeStub().asGenericType(), new TypeList.Generic.Explicit(TypeDefinition.Sort.describe(Object.class)))))));
            }
        }
    }
}


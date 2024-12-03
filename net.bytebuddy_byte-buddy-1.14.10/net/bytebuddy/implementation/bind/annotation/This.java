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
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.implementation.bytecode.constant.NullConstant;
import net.bytebuddy.implementation.bytecode.member.MethodVariableAccess;
import net.bytebuddy.matcher.ElementMatchers;

@Documented
@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.PARAMETER})
public @interface This {
    public boolean optional() default false;

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Binder implements TargetMethodAnnotationDrivenBinder.ParameterBinder<This>
    {
        INSTANCE;

        private static final MethodDescription.InDefinedShape OPTIONAL;

        @Override
        public Class<This> getHandledType() {
            return This.class;
        }

        @Override
        public MethodDelegationBinder.ParameterBinding<?> bind(AnnotationDescription.Loadable<This> annotation, MethodDescription source, ParameterDescription target, Implementation.Target implementationTarget, Assigner assigner, Assigner.Typing typing) {
            if (target.getType().isPrimitive()) {
                throw new IllegalStateException(target + " uses a primitive type with a @This annotation");
            }
            if (target.getType().isArray()) {
                throw new IllegalStateException(target + " uses an array type with a @This annotation");
            }
            if (source.isStatic() && !annotation.getValue(OPTIONAL).resolve(Boolean.class).booleanValue()) {
                return MethodDelegationBinder.ParameterBinding.Illegal.INSTANCE;
            }
            return new MethodDelegationBinder.ParameterBinding.Anonymous(source.isStatic() ? NullConstant.INSTANCE : new StackManipulation.Compound(MethodVariableAccess.loadThis(), assigner.assign(implementationTarget.getInstrumentedType().asGenericType(), target.getType(), typing)));
        }

        static {
            OPTIONAL = (MethodDescription.InDefinedShape)((MethodList)TypeDescription.ForLoadedType.of(This.class).getDeclaredMethods().filter(ElementMatchers.named("optional"))).getOnly();
        }
    }
}


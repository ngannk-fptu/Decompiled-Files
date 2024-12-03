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
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bind.MethodDelegationBinder;
import net.bytebuddy.implementation.bind.annotation.TargetMethodAnnotationDrivenBinder;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.implementation.bytecode.constant.DefaultValue;
import net.bytebuddy.implementation.bytecode.constant.NullConstant;

@Documented
@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.PARAMETER})
public @interface StubValue {

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Binder implements TargetMethodAnnotationDrivenBinder.ParameterBinder<StubValue>
    {
        INSTANCE;


        @Override
        public Class<StubValue> getHandledType() {
            return StubValue.class;
        }

        @Override
        public MethodDelegationBinder.ParameterBinding<?> bind(AnnotationDescription.Loadable<StubValue> annotation, MethodDescription source, ParameterDescription target, Implementation.Target implementationTarget, Assigner assigner, Assigner.Typing typing) {
            if (!target.getType().represents((Type)((Object)Object.class))) {
                throw new IllegalStateException(target + " uses StubValue annotation on non-Object type");
            }
            return new MethodDelegationBinder.ParameterBinding.Anonymous(source.getReturnType().represents(Void.TYPE) ? NullConstant.INSTANCE : new StackManipulation.Compound(DefaultValue.of(source.getReturnType().asErasure()), assigner.assign(source.getReturnType(), TypeDescription.Generic.OfNonGenericType.ForLoadedType.of(Object.class), typing)));
        }
    }
}


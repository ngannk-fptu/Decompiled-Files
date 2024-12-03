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
import net.bytebuddy.implementation.bind.ArgumentTypeResolver;
import net.bytebuddy.implementation.bind.MethodDelegationBinder;
import net.bytebuddy.implementation.bind.annotation.TargetMethodAnnotationDrivenBinder;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.implementation.bytecode.member.MethodVariableAccess;
import net.bytebuddy.matcher.ElementMatchers;

@Documented
@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.PARAMETER})
public @interface Argument {
    public int value();

    public BindingMechanic bindingMechanic() default BindingMechanic.UNIQUE;

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Binder implements TargetMethodAnnotationDrivenBinder.ParameterBinder<Argument>
    {
        INSTANCE;

        private static final MethodDescription.InDefinedShape VALUE;
        private static final MethodDescription.InDefinedShape BINDING_MECHANIC;

        @Override
        public Class<Argument> getHandledType() {
            return Argument.class;
        }

        @Override
        public MethodDelegationBinder.ParameterBinding<?> bind(AnnotationDescription.Loadable<Argument> annotation, MethodDescription source, ParameterDescription target, Implementation.Target implementationTarget, Assigner assigner, Assigner.Typing typing) {
            if (annotation.getValue(VALUE).resolve(Integer.class) < 0) {
                throw new IllegalArgumentException("@Argument annotation on " + target + " specifies negative index");
            }
            if (source.getParameters().size() <= annotation.getValue(VALUE).resolve(Integer.class)) {
                return MethodDelegationBinder.ParameterBinding.Illegal.INSTANCE;
            }
            return annotation.getValue(BINDING_MECHANIC).load(Argument.class.getClassLoader()).resolve(BindingMechanic.class).makeBinding(((ParameterDescription)source.getParameters().get(annotation.getValue(VALUE).resolve(Integer.class))).getType(), target.getType(), annotation.getValue(VALUE).resolve(Integer.class), assigner, typing, ((ParameterDescription)source.getParameters().get(annotation.getValue(VALUE).resolve(Integer.class))).getOffset());
        }

        static {
            MethodList<MethodDescription.InDefinedShape> methods = TypeDescription.ForLoadedType.of(Argument.class).getDeclaredMethods();
            VALUE = (MethodDescription.InDefinedShape)((MethodList)methods.filter(ElementMatchers.named("value"))).getOnly();
            BINDING_MECHANIC = (MethodDescription.InDefinedShape)((MethodList)methods.filter(ElementMatchers.named("bindingMechanic"))).getOnly();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum BindingMechanic {
        UNIQUE{

            @Override
            protected MethodDelegationBinder.ParameterBinding<?> makeBinding(TypeDescription.Generic source, TypeDescription.Generic target, int sourceParameterIndex, Assigner assigner, Assigner.Typing typing, int parameterOffset) {
                return MethodDelegationBinder.ParameterBinding.Unique.of(new StackManipulation.Compound(MethodVariableAccess.of(source).loadFrom(parameterOffset), assigner.assign(source, target, typing)), new ArgumentTypeResolver.ParameterIndexToken(sourceParameterIndex));
            }
        }
        ,
        ANONYMOUS{

            @Override
            protected MethodDelegationBinder.ParameterBinding<?> makeBinding(TypeDescription.Generic source, TypeDescription.Generic target, int sourceParameterIndex, Assigner assigner, Assigner.Typing typing, int parameterOffset) {
                return new MethodDelegationBinder.ParameterBinding.Anonymous(new StackManipulation.Compound(MethodVariableAccess.of(source).loadFrom(parameterOffset), assigner.assign(source, target, typing)));
            }
        };


        protected abstract MethodDelegationBinder.ParameterBinding<?> makeBinding(TypeDescription.Generic var1, TypeDescription.Generic var2, int var3, Assigner var4, Assigner.Typing var5, int var6);
    }
}


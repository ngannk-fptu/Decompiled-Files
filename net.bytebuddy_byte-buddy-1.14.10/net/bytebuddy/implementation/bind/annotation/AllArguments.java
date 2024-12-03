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
import java.util.ArrayList;
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
import net.bytebuddy.implementation.bytecode.collection.ArrayFactory;
import net.bytebuddy.implementation.bytecode.constant.NullConstant;
import net.bytebuddy.implementation.bytecode.member.MethodVariableAccess;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.CompoundList;

@Documented
@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.PARAMETER})
public @interface AllArguments {
    public Assignment value() default Assignment.STRICT;

    public boolean includeSelf() default false;

    public boolean nullIfEmpty() default false;

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Binder implements TargetMethodAnnotationDrivenBinder.ParameterBinder<AllArguments>
    {
        INSTANCE;

        private static final MethodDescription.InDefinedShape VALUE;
        private static final MethodDescription.InDefinedShape INCLUDE_SELF;
        private static final MethodDescription.InDefinedShape NULL_IF_EMPTY;

        @Override
        public Class<AllArguments> getHandledType() {
            return AllArguments.class;
        }

        @Override
        public MethodDelegationBinder.ParameterBinding<?> bind(AnnotationDescription.Loadable<AllArguments> annotation, MethodDescription source, ParameterDescription target, Implementation.Target implementationTarget, Assigner assigner, Assigner.Typing typing) {
            boolean includeThis;
            TypeDescription.Generic componentType;
            if (target.getType().represents((Type)((Object)Object.class))) {
                componentType = TypeDescription.Generic.OfNonGenericType.ForLoadedType.of(Object.class);
            } else if (target.getType().isArray()) {
                componentType = target.getType().getComponentType();
            } else {
                throw new IllegalStateException("Expected an array type for all argument annotation on " + source);
            }
            boolean bl = includeThis = !source.isStatic() && annotation.getValue(INCLUDE_SELF).resolve(Boolean.class) != false;
            if (!includeThis && source.getParameters().isEmpty() && annotation.getValue(NULL_IF_EMPTY).resolve(Boolean.class).booleanValue()) {
                return new MethodDelegationBinder.ParameterBinding.Anonymous(NullConstant.INSTANCE);
            }
            ArrayList<StackManipulation.Compound> stackManipulations = new ArrayList<StackManipulation.Compound>(source.getParameters().size() + (includeThis ? 1 : 0));
            int offset = source.isStatic() || includeThis ? 0 : 1;
            for (TypeDescription.Generic sourceParameter : includeThis ? CompoundList.of(implementationTarget.getInstrumentedType().asGenericType(), source.getParameters().asTypeList()) : source.getParameters().asTypeList()) {
                StackManipulation.Compound stackManipulation = new StackManipulation.Compound(MethodVariableAccess.of(sourceParameter).loadFrom(offset), assigner.assign(sourceParameter, componentType, typing));
                if (stackManipulation.isValid()) {
                    stackManipulations.add(stackManipulation);
                } else if (annotation.getValue(VALUE).load(AllArguments.class.getClassLoader()).resolve(Assignment.class).isStrict()) {
                    return MethodDelegationBinder.ParameterBinding.Illegal.INSTANCE;
                }
                offset += sourceParameter.getStackSize().getSize();
            }
            return new MethodDelegationBinder.ParameterBinding.Anonymous(ArrayFactory.forType(componentType).withValues(stackManipulations));
        }

        static {
            MethodList<MethodDescription.InDefinedShape> methods = TypeDescription.ForLoadedType.of(AllArguments.class).getDeclaredMethods();
            VALUE = (MethodDescription.InDefinedShape)((MethodList)methods.filter(ElementMatchers.named("value"))).getOnly();
            INCLUDE_SELF = (MethodDescription.InDefinedShape)((MethodList)methods.filter(ElementMatchers.named("includeSelf"))).getOnly();
            NULL_IF_EMPTY = (MethodDescription.InDefinedShape)((MethodList)methods.filter(ElementMatchers.named("nullIfEmpty"))).getOnly();
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Assignment {
        STRICT(true),
        SLACK(false);

        private final boolean strict;

        private Assignment(boolean strict) {
            this.strict = strict;
        }

        protected boolean isStrict() {
            return this.strict;
        }
    }
}


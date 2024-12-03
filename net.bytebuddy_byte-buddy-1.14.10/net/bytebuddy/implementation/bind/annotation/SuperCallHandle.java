/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.implementation.bind.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import net.bytebuddy.description.annotation.AnnotationDescription;
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
import net.bytebuddy.implementation.bytecode.collection.ArrayFactory;
import net.bytebuddy.implementation.bytecode.constant.IntegerConstant;
import net.bytebuddy.implementation.bytecode.constant.NullConstant;
import net.bytebuddy.implementation.bytecode.member.MethodInvocation;
import net.bytebuddy.implementation.bytecode.member.MethodVariableAccess;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.JavaType;

@Documented
@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.PARAMETER})
public @interface SuperCallHandle {
    public boolean fallbackToDefault() default true;

    public boolean nullIfImpossible() default false;

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static enum Binder implements TargetMethodAnnotationDrivenBinder.ParameterBinder<SuperCallHandle>
    {
        INSTANCE;

        private static final MethodDescription.InDefinedShape FALLBACK_TO_DEFAULT;
        private static final MethodDescription.InDefinedShape NULL_IF_IMPOSSIBLE;

        @Override
        public Class<SuperCallHandle> getHandledType() {
            return SuperCallHandle.class;
        }

        @Override
        public MethodDelegationBinder.ParameterBinding<?> bind(AnnotationDescription.Loadable<SuperCallHandle> annotation, MethodDescription source, ParameterDescription target, Implementation.Target implementationTarget, Assigner assigner, Assigner.Typing typing) {
            StackManipulation stackManipulation;
            if (!target.getType().asErasure().isAssignableFrom(JavaType.METHOD_HANDLE.getTypeStub())) {
                throw new IllegalStateException("A method handle for a super method invocation cannot be assigned to " + target);
            }
            if (source.isConstructor()) {
                return annotation.getValue(NULL_IF_IMPOSSIBLE).resolve(Boolean.class) != false ? new MethodDelegationBinder.ParameterBinding.Anonymous(NullConstant.INSTANCE) : MethodDelegationBinder.ParameterBinding.Illegal.INSTANCE;
            }
            Implementation.SpecialMethodInvocation specialMethodInvocation = (annotation.getValue(FALLBACK_TO_DEFAULT).resolve(Boolean.class) != false ? implementationTarget.invokeDominant(source.asSignatureToken()) : implementationTarget.invokeSuper(source.asSignatureToken())).withCheckedCompatibilityTo(source.asTypeToken());
            if (specialMethodInvocation.isValid()) {
                ArrayList<StackManipulation> stackManipulations = new ArrayList<StackManipulation>(1 + (source.isStatic() ? 0 : 2) + source.getParameters().size() * 3);
                stackManipulations.add(specialMethodInvocation.toMethodHandle().toStackManipulation());
                if (!source.isStatic()) {
                    stackManipulations.add(MethodVariableAccess.loadThis());
                    stackManipulations.add(MethodInvocation.invoke(new MethodDescription.Latent(JavaType.METHOD_HANDLE.getTypeStub(), new MethodDescription.Token("bindTo", 1, JavaType.METHOD_HANDLE.getTypeStub().asGenericType(), new TypeList.Generic.Explicit(TypeDefinition.Sort.describe(Object.class))))));
                }
                if (!source.getParameters().isEmpty()) {
                    ArrayList<StackManipulation.Compound> values = new ArrayList<StackManipulation.Compound>(source.getParameters().size());
                    for (ParameterDescription parameterDescription : source.getParameters()) {
                        values.add((StackManipulation.Compound)(parameterDescription.getType().isPrimitive() ? new StackManipulation.Compound(MethodVariableAccess.load(parameterDescription), assigner.assign(parameterDescription.getType(), parameterDescription.getType().asErasure().asBoxed().asGenericType(), typing)) : MethodVariableAccess.load(parameterDescription)));
                    }
                    stackManipulations.add(IntegerConstant.forValue(0));
                    stackManipulations.add(ArrayFactory.forType(TypeDescription.ForLoadedType.of(Object.class).asGenericType()).withValues(values));
                    stackManipulations.add(MethodInvocation.invoke(new MethodDescription.Latent(JavaType.METHOD_HANDLES.getTypeStub(), new MethodDescription.Token("insertArguments", 9, JavaType.METHOD_HANDLE.getTypeStub().asGenericType(), new TypeList.Generic.Explicit(JavaType.METHOD_HANDLE.getTypeStub(), TypeDefinition.Sort.describe(Integer.TYPE), TypeDefinition.Sort.describe(Object[].class))))));
                }
                stackManipulation = new StackManipulation.Compound(stackManipulations);
            } else if (annotation.getValue(NULL_IF_IMPOSSIBLE).resolve(Boolean.class).booleanValue()) {
                stackManipulation = NullConstant.INSTANCE;
            } else {
                return MethodDelegationBinder.ParameterBinding.Illegal.INSTANCE;
            }
            return new MethodDelegationBinder.ParameterBinding.Anonymous(stackManipulation);
        }

        static {
            MethodList<MethodDescription.InDefinedShape> methods = TypeDescription.ForLoadedType.of(SuperCallHandle.class).getDeclaredMethods();
            FALLBACK_TO_DEFAULT = (MethodDescription.InDefinedShape)((MethodList)methods.filter(ElementMatchers.named("fallbackToDefault"))).getOnly();
            NULL_IF_IMPOSSIBLE = (MethodDescription.InDefinedShape)((MethodList)methods.filter(ElementMatchers.named("nullIfImpossible"))).getOnly();
        }
    }
}


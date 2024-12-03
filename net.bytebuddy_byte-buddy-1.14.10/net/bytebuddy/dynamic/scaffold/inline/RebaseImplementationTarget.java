/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.dynamic.scaffold.inline;

import java.util.ArrayList;
import java.util.Map;
import net.bytebuddy.ClassFileVersion;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeList;
import net.bytebuddy.dynamic.scaffold.MethodGraph;
import net.bytebuddy.dynamic.scaffold.inline.MethodRebaseResolver;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.constant.DefaultValue;
import net.bytebuddy.implementation.bytecode.member.MethodInvocation;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.utility.CompoundList;
import net.bytebuddy.utility.JavaConstant;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@HashCodeAndEqualsPlugin.Enhance
public class RebaseImplementationTarget
extends Implementation.Target.AbstractBase {
    private final Map<MethodDescription.SignatureToken, MethodRebaseResolver.Resolution> rebaseableMethods;

    protected RebaseImplementationTarget(TypeDescription instrumentedType, MethodGraph.Linked methodGraph, Implementation.Target.AbstractBase.DefaultMethodInvocation defaultMethodInvocation, Map<MethodDescription.SignatureToken, MethodRebaseResolver.Resolution> rebaseableMethods) {
        super(instrumentedType, methodGraph, defaultMethodInvocation);
        this.rebaseableMethods = rebaseableMethods;
    }

    protected static Implementation.Target of(TypeDescription instrumentedType, MethodGraph.Linked methodGraph, ClassFileVersion classFileVersion, MethodRebaseResolver methodRebaseResolver) {
        return new RebaseImplementationTarget(instrumentedType, methodGraph, Implementation.Target.AbstractBase.DefaultMethodInvocation.of(classFileVersion), methodRebaseResolver.asTokenMap());
    }

    @Override
    public Implementation.SpecialMethodInvocation invokeSuper(MethodDescription.SignatureToken token) {
        MethodRebaseResolver.Resolution resolution = this.rebaseableMethods.get(token);
        return resolution == null ? this.invokeSuper(this.methodGraph.getSuperClassGraph().locate(token)) : this.invokeSuper(resolution);
    }

    private Implementation.SpecialMethodInvocation invokeSuper(MethodGraph.Node node) {
        TypeDescription.Generic superClass = this.instrumentedType.getSuperClass();
        return node.getSort().isResolved() && superClass != null ? Implementation.SpecialMethodInvocation.Simple.of(node.getRepresentative(), superClass.asErasure()) : Implementation.SpecialMethodInvocation.Illegal.INSTANCE;
    }

    private Implementation.SpecialMethodInvocation invokeSuper(MethodRebaseResolver.Resolution resolution) {
        return resolution.isRebased() ? RebasedMethodInvocation.of(resolution.getResolvedMethod(), this.instrumentedType, resolution.getAppendedParameters()) : Implementation.SpecialMethodInvocation.Simple.of(resolution.getResolvedMethod(), this.instrumentedType);
    }

    @Override
    public TypeDescription getOriginType() {
        return this.instrumentedType;
    }

    @Override
    public boolean equals(@MaybeNull Object object) {
        if (!super.equals(object)) {
            return false;
        }
        if (this == object) {
            return true;
        }
        if (object == null) {
            return false;
        }
        if (this.getClass() != object.getClass()) {
            return false;
        }
        return ((Object)this.rebaseableMethods).equals(((RebaseImplementationTarget)object).rebaseableMethods);
    }

    @Override
    public int hashCode() {
        return super.hashCode() * 31 + ((Object)this.rebaseableMethods).hashCode();
    }

    @HashCodeAndEqualsPlugin.Enhance
    public static class Factory
    implements Implementation.Target.Factory {
        private final MethodRebaseResolver methodRebaseResolver;

        public Factory(MethodRebaseResolver methodRebaseResolver) {
            this.methodRebaseResolver = methodRebaseResolver;
        }

        public Implementation.Target make(TypeDescription instrumentedType, MethodGraph.Linked methodGraph, ClassFileVersion classFileVersion) {
            return RebaseImplementationTarget.of(instrumentedType, methodGraph, classFileVersion, this.methodRebaseResolver);
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
            return this.methodRebaseResolver.equals(((Factory)object).methodRebaseResolver);
        }

        public int hashCode() {
            return this.getClass().hashCode() * 31 + this.methodRebaseResolver.hashCode();
        }
    }

    protected static class RebasedMethodInvocation
    extends Implementation.SpecialMethodInvocation.AbstractBase {
        private final MethodDescription.InDefinedShape methodDescription;
        private final TypeDescription instrumentedType;
        private final StackManipulation stackManipulation;
        private final TypeList prependedParameters;

        protected RebasedMethodInvocation(MethodDescription.InDefinedShape methodDescription, TypeDescription instrumentedType, StackManipulation stackManipulation, TypeList prependedParameters) {
            this.methodDescription = methodDescription;
            this.instrumentedType = instrumentedType;
            this.stackManipulation = stackManipulation;
            this.prependedParameters = prependedParameters;
        }

        protected static Implementation.SpecialMethodInvocation of(MethodDescription.InDefinedShape resolvedMethod, TypeDescription instrumentedType, TypeList prependedParameters) {
            MethodInvocation.WithImplicitInvocationTargetType stackManipulation;
            StackManipulation stackManipulation2 = stackManipulation = resolvedMethod.isStatic() ? MethodInvocation.invoke(resolvedMethod) : MethodInvocation.invoke(resolvedMethod).special(instrumentedType);
            if (stackManipulation.isValid()) {
                ArrayList<StackManipulation> stackManipulations = new ArrayList<StackManipulation>(prependedParameters.size() + 1);
                for (TypeDescription prependedParameter : prependedParameters) {
                    stackManipulations.add(DefaultValue.of(prependedParameter));
                }
                stackManipulations.add(stackManipulation);
                return new RebasedMethodInvocation(resolvedMethod, instrumentedType, new StackManipulation.Compound(stackManipulations), prependedParameters);
            }
            return Implementation.SpecialMethodInvocation.Illegal.INSTANCE;
        }

        public MethodDescription getMethodDescription() {
            return this.methodDescription;
        }

        public TypeDescription getTypeDescription() {
            return this.instrumentedType;
        }

        public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
            return this.stackManipulation.apply(methodVisitor, implementationContext);
        }

        public Implementation.SpecialMethodInvocation withCheckedCompatibilityTo(MethodDescription.TypeToken token) {
            if (this.methodDescription.asTypeToken().equals(new MethodDescription.TypeToken(token.getReturnType(), CompoundList.of(token.getParameterTypes(), this.prependedParameters)))) {
                return this;
            }
            return Implementation.SpecialMethodInvocation.Illegal.INSTANCE;
        }

        public JavaConstant.MethodHandle toMethodHandle() {
            return this.methodDescription.isStatic() ? JavaConstant.MethodHandle.of(this.methodDescription) : JavaConstant.MethodHandle.ofSpecial(this.methodDescription, this.instrumentedType);
        }
    }
}


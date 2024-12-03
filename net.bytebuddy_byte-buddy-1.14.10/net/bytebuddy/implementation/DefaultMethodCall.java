/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.implementation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.description.type.TypeList;
import net.bytebuddy.dynamic.scaffold.InstrumentedType;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.member.MethodReturn;
import net.bytebuddy.implementation.bytecode.member.MethodVariableAccess;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@HashCodeAndEqualsPlugin.Enhance
public class DefaultMethodCall
implements Implementation {
    private final List<TypeDescription> prioritizedInterfaces;

    protected DefaultMethodCall(List<TypeDescription> prioritizedInterfaces) {
        this.prioritizedInterfaces = prioritizedInterfaces;
    }

    public static Implementation prioritize(Class<?> ... prioritizedInterface) {
        return DefaultMethodCall.prioritize(new TypeList.ForLoadedTypes(prioritizedInterface));
    }

    public static Implementation prioritize(Iterable<? extends Class<?>> prioritizedInterfaces) {
        ArrayList list = new ArrayList();
        for (Class<?> prioritizedInterface : prioritizedInterfaces) {
            list.add(prioritizedInterface);
        }
        return DefaultMethodCall.prioritize(new TypeList.ForLoadedTypes(list));
    }

    public static Implementation prioritize(TypeDescription ... prioritizedInterface) {
        return DefaultMethodCall.prioritize(Arrays.asList(prioritizedInterface));
    }

    public static Implementation prioritize(Collection<? extends TypeDescription> prioritizedInterfaces) {
        return new DefaultMethodCall(new ArrayList<TypeDescription>(prioritizedInterfaces));
    }

    public static Implementation unambiguousOnly() {
        return new DefaultMethodCall(Collections.<TypeDescription>emptyList());
    }

    @Override
    public InstrumentedType prepare(InstrumentedType instrumentedType) {
        return instrumentedType;
    }

    @Override
    public ByteCodeAppender appender(Implementation.Target implementationTarget) {
        return new Appender(implementationTarget, this.filterRelevant(implementationTarget.getInstrumentedType()));
    }

    private List<TypeDescription> filterRelevant(TypeDescription typeDescription) {
        ArrayList<TypeDescription> filtered = new ArrayList<TypeDescription>(this.prioritizedInterfaces.size());
        HashSet<TypeDescription> relevant = new HashSet<TypeDescription>(typeDescription.getInterfaces().asErasures());
        for (TypeDescription prioritizedInterface : this.prioritizedInterfaces) {
            if (!relevant.remove(prioritizedInterface)) continue;
            filtered.add(prioritizedInterface);
        }
        return filtered;
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
        return ((Object)this.prioritizedInterfaces).equals(((DefaultMethodCall)object).prioritizedInterfaces);
    }

    public int hashCode() {
        return this.getClass().hashCode() * 31 + ((Object)this.prioritizedInterfaces).hashCode();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    protected static class Appender
    implements ByteCodeAppender {
        private final Implementation.Target implementationTarget;
        private final List<TypeDescription> prioritizedInterfaces;
        @HashCodeAndEqualsPlugin.ValueHandling(value=HashCodeAndEqualsPlugin.ValueHandling.Sort.IGNORE)
        private final Set<TypeDescription> nonPrioritizedInterfaces;

        protected Appender(Implementation.Target implementationTarget, List<TypeDescription> prioritizedInterfaces) {
            this.implementationTarget = implementationTarget;
            this.prioritizedInterfaces = prioritizedInterfaces;
            this.nonPrioritizedInterfaces = new HashSet<TypeDescription>(implementationTarget.getInstrumentedType().getInterfaces().asErasures());
            this.nonPrioritizedInterfaces.removeAll(prioritizedInterfaces);
        }

        @Override
        public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
            StackManipulation defaultMethodInvocation = this.locateDefault(instrumentedMethod);
            if (!defaultMethodInvocation.isValid()) {
                throw new IllegalStateException("Cannot invoke default method on " + instrumentedMethod);
            }
            StackManipulation.Size stackSize = new StackManipulation.Compound(MethodVariableAccess.allArgumentsOf(instrumentedMethod).prependThisReference(), defaultMethodInvocation, MethodReturn.of(instrumentedMethod.getReturnType())).apply(methodVisitor, implementationContext);
            return new ByteCodeAppender.Size(stackSize.getMaximalSize(), instrumentedMethod.getStackSize());
        }

        private StackManipulation locateDefault(MethodDescription methodDescription) {
            MethodDescription.SignatureToken methodToken = methodDescription.asSignatureToken();
            Implementation.SpecialMethodInvocation specialMethodInvocation = Implementation.SpecialMethodInvocation.Illegal.INSTANCE;
            for (TypeDescription typeDescription : this.prioritizedInterfaces) {
                specialMethodInvocation = this.implementationTarget.invokeDefault(methodToken, typeDescription).withCheckedCompatibilityTo(methodDescription.asTypeToken());
                if (!specialMethodInvocation.isValid()) continue;
                return specialMethodInvocation;
            }
            for (TypeDescription typeDescription : this.nonPrioritizedInterfaces) {
                Implementation.SpecialMethodInvocation other = this.implementationTarget.invokeDefault(methodToken, typeDescription).withCheckedCompatibilityTo(methodDescription.asTypeToken());
                if (specialMethodInvocation.isValid() && other.isValid()) {
                    throw new IllegalStateException(methodDescription + " has an ambiguous default method with " + other.getMethodDescription() + " and " + specialMethodInvocation.getMethodDescription());
                }
                specialMethodInvocation = other;
            }
            return specialMethodInvocation;
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
            if (!this.implementationTarget.equals(((Appender)object).implementationTarget)) {
                return false;
            }
            return ((Object)this.prioritizedInterfaces).equals(((Appender)object).prioritizedInterfaces);
        }

        public int hashCode() {
            return (this.getClass().hashCode() * 31 + this.implementationTarget.hashCode()) * 31 + ((Object)this.prioritizedInterfaces).hashCode();
        }
    }
}


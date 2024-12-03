/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package net.bytebuddy.implementation.bind;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bind.ArgumentTypeResolver;
import net.bytebuddy.implementation.bind.DeclaringTypeResolver;
import net.bytebuddy.implementation.bind.MethodNameEqualityResolver;
import net.bytebuddy.implementation.bind.ParameterLengthResolver;
import net.bytebuddy.implementation.bind.annotation.BindingPriority;
import net.bytebuddy.implementation.bytecode.Removal;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.implementation.bytecode.member.MethodInvocation;
import net.bytebuddy.implementation.bytecode.member.MethodReturn;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.utility.CompoundList;
import net.bytebuddy.utility.nullability.MaybeNull;

public interface MethodDelegationBinder {
    public Record compile(MethodDescription var1);

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    public static class Processor
    implements Record {
        private final List<? extends Record> records;
        private final AmbiguityResolver ambiguityResolver;
        private final BindingResolver bindingResolver;

        public Processor(List<? extends Record> records, AmbiguityResolver ambiguityResolver, BindingResolver bindingResolver) {
            this.records = records;
            this.ambiguityResolver = ambiguityResolver;
            this.bindingResolver = bindingResolver;
        }

        @Override
        public MethodBinding bind(Implementation.Target implementationTarget, MethodDescription source, TerminationHandler terminationHandler, MethodInvoker methodInvoker, Assigner assigner) {
            ArrayList<MethodBinding> targets = new ArrayList<MethodBinding>();
            for (Record record : this.records) {
                MethodBinding methodBinding = record.bind(implementationTarget, source, terminationHandler, methodInvoker, assigner);
                if (!methodBinding.isValid()) continue;
                targets.add(methodBinding);
            }
            if (targets.isEmpty()) {
                throw new IllegalArgumentException("None of " + this.records + " allows for delegation from " + source);
            }
            return this.bindingResolver.resolve(this.ambiguityResolver, source, targets);
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
            if (!((Object)this.records).equals(((Processor)object).records)) {
                return false;
            }
            if (!this.ambiguityResolver.equals(((Processor)object).ambiguityResolver)) {
                return false;
            }
            return this.bindingResolver.equals(((Processor)object).bindingResolver);
        }

        public int hashCode() {
            return ((this.getClass().hashCode() * 31 + ((Object)this.records).hashCode()) * 31 + this.ambiguityResolver.hashCode()) * 31 + this.bindingResolver.hashCode();
        }
    }

    public static interface TerminationHandler {
        public StackManipulation resolve(Assigner var1, Assigner.Typing var2, MethodDescription var3, MethodDescription var4);

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum Default implements TerminationHandler
        {
            RETURNING{

                public StackManipulation resolve(Assigner assigner, Assigner.Typing typing, MethodDescription source, MethodDescription target) {
                    return new StackManipulation.Compound(assigner.assign(target.isConstructor() ? target.getDeclaringType().asGenericType() : target.getReturnType(), source.getReturnType(), typing), MethodReturn.of(source.getReturnType()));
                }
            }
            ,
            DROPPING{

                public StackManipulation resolve(Assigner assigner, Assigner.Typing typing, MethodDescription source, MethodDescription target) {
                    return Removal.of(target.isConstructor() ? target.getDeclaringType() : target.getReturnType());
                }
            };

        }
    }

    @SuppressFBWarnings(value={"IC_SUPERCLASS_USES_SUBCLASS_DURING_INITIALIZATION"}, justification="Safe initialization is implied.")
    public static interface AmbiguityResolver {
        public static final AmbiguityResolver DEFAULT = new Compound(BindingPriority.Resolver.INSTANCE, DeclaringTypeResolver.INSTANCE, ArgumentTypeResolver.INSTANCE, MethodNameEqualityResolver.INSTANCE, ParameterLengthResolver.INSTANCE);

        public Resolution resolve(MethodDescription var1, MethodBinding var2, MethodBinding var3);

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        public static class Compound
        implements AmbiguityResolver {
            private final List<AmbiguityResolver> ambiguityResolvers = new ArrayList<AmbiguityResolver>();

            public Compound(AmbiguityResolver ... ambiguityResolver) {
                this(Arrays.asList(ambiguityResolver));
            }

            public Compound(List<? extends AmbiguityResolver> ambiguityResolvers) {
                for (AmbiguityResolver ambiguityResolver : ambiguityResolvers) {
                    if (ambiguityResolver instanceof Compound) {
                        this.ambiguityResolvers.addAll(((Compound)ambiguityResolver).ambiguityResolvers);
                        continue;
                    }
                    if (ambiguityResolver instanceof NoOp) continue;
                    this.ambiguityResolvers.add(ambiguityResolver);
                }
            }

            @Override
            public Resolution resolve(MethodDescription source, MethodBinding left, MethodBinding right) {
                Resolution resolution = Resolution.UNKNOWN;
                Iterator<AmbiguityResolver> iterator = this.ambiguityResolvers.iterator();
                while (resolution.isUnresolved() && iterator.hasNext()) {
                    resolution = iterator.next().resolve(source, left, right);
                }
                return resolution;
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
                return ((Object)this.ambiguityResolvers).equals(((Compound)object).ambiguityResolvers);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + ((Object)this.ambiguityResolvers).hashCode();
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum Directional implements AmbiguityResolver
        {
            LEFT(true),
            RIGHT(false);

            private final boolean left;

            private Directional(boolean left) {
                this.left = left;
            }

            @Override
            public Resolution resolve(MethodDescription source, MethodBinding left, MethodBinding right) {
                return this.left ? Resolution.LEFT : Resolution.RIGHT;
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum NoOp implements AmbiguityResolver
        {
            INSTANCE;


            @Override
            public Resolution resolve(MethodDescription source, MethodBinding left, MethodBinding right) {
                return Resolution.UNKNOWN;
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum Resolution {
            UNKNOWN(true),
            LEFT(false),
            RIGHT(false),
            AMBIGUOUS(true);

            private final boolean unresolved;

            private Resolution(boolean unresolved) {
                this.unresolved = unresolved;
            }

            public boolean isUnresolved() {
                return this.unresolved;
            }

            public Resolution merge(Resolution other) {
                switch (this) {
                    case UNKNOWN: {
                        return other;
                    }
                    case AMBIGUOUS: {
                        return AMBIGUOUS;
                    }
                    case LEFT: 
                    case RIGHT: {
                        return other == UNKNOWN || other == this ? this : AMBIGUOUS;
                    }
                }
                throw new AssertionError();
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static interface BindingResolver {
        public MethodBinding resolve(AmbiguityResolver var1, MethodDescription var2, List<MethodBinding> var3);

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        public static class StreamWriting
        implements BindingResolver {
            private final BindingResolver delegate;
            private final PrintStream printStream;

            public StreamWriting(BindingResolver delegate, PrintStream printStream) {
                this.delegate = delegate;
                this.printStream = printStream;
            }

            public static BindingResolver toSystemOut() {
                return StreamWriting.toSystemOut(Default.INSTANCE);
            }

            public static BindingResolver toSystemOut(BindingResolver bindingResolver) {
                return new StreamWriting(bindingResolver, System.out);
            }

            public static BindingResolver toSystemError() {
                return StreamWriting.toSystemError(Default.INSTANCE);
            }

            public static BindingResolver toSystemError(BindingResolver bindingResolver) {
                return new StreamWriting(bindingResolver, System.err);
            }

            @Override
            public MethodBinding resolve(AmbiguityResolver ambiguityResolver, MethodDescription source, List<MethodBinding> targets) {
                MethodBinding methodBinding = this.delegate.resolve(ambiguityResolver, source, targets);
                this.printStream.println("Binding " + source + " as delegation to " + methodBinding.getTarget());
                return methodBinding;
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
                if (!this.delegate.equals(((StreamWriting)object).delegate)) {
                    return false;
                }
                return this.printStream.equals(((StreamWriting)object).printStream);
            }

            public int hashCode() {
                return (this.getClass().hashCode() * 31 + this.delegate.hashCode()) * 31 + this.printStream.hashCode();
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum Unique implements BindingResolver
        {
            INSTANCE;

            private static final int ONLY = 0;

            @Override
            public MethodBinding resolve(AmbiguityResolver ambiguityResolver, MethodDescription source, List<MethodBinding> targets) {
                if (targets.size() == 1) {
                    return targets.get(0);
                }
                throw new IllegalStateException(source + " allowed for more than one binding: " + targets);
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum Default implements BindingResolver
        {
            INSTANCE;

            private static final int ONLY = 0;
            private static final int LEFT = 0;
            private static final int RIGHT = 1;

            @Override
            public MethodBinding resolve(AmbiguityResolver ambiguityResolver, MethodDescription source, List<MethodBinding> targets) {
                return this.doResolve(ambiguityResolver, source, new ArrayList<MethodBinding>(targets));
            }

            private MethodBinding doResolve(AmbiguityResolver ambiguityResolver, MethodDescription source, List<MethodBinding> targets) {
                switch (targets.size()) {
                    case 1: {
                        return targets.get(0);
                    }
                    case 2: {
                        MethodBinding left = targets.get(0);
                        MethodBinding right = targets.get(1);
                        switch (ambiguityResolver.resolve(source, left, right)) {
                            case LEFT: {
                                return left;
                            }
                            case RIGHT: {
                                return right;
                            }
                            case AMBIGUOUS: 
                            case UNKNOWN: {
                                throw new IllegalArgumentException("Cannot resolve ambiguous delegation of " + source + " to " + left.getTarget() + " or " + right.getTarget());
                            }
                        }
                        throw new AssertionError();
                    }
                }
                MethodBinding left = targets.get(0);
                MethodBinding right = targets.get(1);
                switch (ambiguityResolver.resolve(source, left, right)) {
                    case LEFT: {
                        targets.remove(1);
                        return this.doResolve(ambiguityResolver, source, targets);
                    }
                    case RIGHT: {
                        targets.remove(0);
                        return this.doResolve(ambiguityResolver, source, targets);
                    }
                    case AMBIGUOUS: 
                    case UNKNOWN: {
                        targets.remove(1);
                        targets.remove(0);
                        MethodBinding subResult = this.doResolve(ambiguityResolver, source, targets);
                        switch (ambiguityResolver.resolve(source, left, subResult).merge(ambiguityResolver.resolve(source, right, subResult))) {
                            case RIGHT: {
                                return subResult;
                            }
                            case LEFT: 
                            case AMBIGUOUS: 
                            case UNKNOWN: {
                                throw new IllegalArgumentException("Cannot resolve ambiguous delegation of " + source + " to " + left.getTarget() + " or " + right.getTarget());
                            }
                        }
                        throw new AssertionError();
                    }
                }
                throw new IllegalStateException("Unexpected amount of targets: " + targets.size());
            }
        }
    }

    public static interface MethodBinding
    extends StackManipulation {
        @MaybeNull
        public Integer getTargetParameterIndex(Object var1);

        public MethodDescription getTarget();

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static class Builder {
            private final MethodInvoker methodInvoker;
            private final MethodDescription candidate;
            private final List<StackManipulation> parameterStackManipulations;
            private final LinkedHashMap<Object, Integer> registeredTargetIndices;
            private int nextParameterIndex;

            public Builder(MethodInvoker methodInvoker, MethodDescription candidate) {
                this.methodInvoker = methodInvoker;
                this.candidate = candidate;
                this.parameterStackManipulations = new ArrayList<StackManipulation>(candidate.getParameters().size());
                this.registeredTargetIndices = new LinkedHashMap();
                this.nextParameterIndex = 0;
            }

            public boolean append(ParameterBinding<?> parameterBinding) {
                this.parameterStackManipulations.add(parameterBinding);
                return this.registeredTargetIndices.put(parameterBinding.getIdentificationToken(), this.nextParameterIndex++) == null;
            }

            public MethodBinding build(StackManipulation terminatingManipulation) {
                if (this.candidate.getParameters().size() != this.nextParameterIndex) {
                    throw new IllegalStateException("The number of parameters bound does not equal the target's number of parameters");
                }
                return new Build(this.candidate, this.registeredTargetIndices, this.methodInvoker.invoke(this.candidate), this.parameterStackManipulations, terminatingManipulation);
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            protected static class Build
            implements MethodBinding {
                private final MethodDescription target;
                private final Map<?, Integer> registeredTargetIndices;
                private final StackManipulation methodInvocation;
                private final List<StackManipulation> parameterStackManipulations;
                private final StackManipulation terminatingStackManipulation;

                protected Build(MethodDescription target, Map<?, Integer> registeredTargetIndices, StackManipulation methodInvocation, List<StackManipulation> parameterStackManipulations, StackManipulation terminatingStackManipulation) {
                    this.target = target;
                    this.registeredTargetIndices = new HashMap(registeredTargetIndices);
                    this.methodInvocation = methodInvocation;
                    this.parameterStackManipulations = new ArrayList<StackManipulation>(parameterStackManipulations);
                    this.terminatingStackManipulation = terminatingStackManipulation;
                }

                @Override
                public boolean isValid() {
                    boolean result = this.methodInvocation.isValid() && this.terminatingStackManipulation.isValid();
                    Iterator<StackManipulation> assignment = this.parameterStackManipulations.iterator();
                    while (result && assignment.hasNext()) {
                        result = assignment.next().isValid();
                    }
                    return result;
                }

                @Override
                @MaybeNull
                public Integer getTargetParameterIndex(Object parameterBindingToken) {
                    return this.registeredTargetIndices.get(parameterBindingToken);
                }

                @Override
                public MethodDescription getTarget() {
                    return this.target;
                }

                @Override
                public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
                    return new StackManipulation.Compound(CompoundList.of(this.parameterStackManipulations, Arrays.asList(this.methodInvocation, this.terminatingStackManipulation))).apply(methodVisitor, implementationContext);
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
                    if (!this.target.equals(((Build)object).target)) {
                        return false;
                    }
                    if (!((Object)this.registeredTargetIndices).equals(((Build)object).registeredTargetIndices)) {
                        return false;
                    }
                    if (!this.methodInvocation.equals(((Build)object).methodInvocation)) {
                        return false;
                    }
                    if (!((Object)this.parameterStackManipulations).equals(((Build)object).parameterStackManipulations)) {
                        return false;
                    }
                    return this.terminatingStackManipulation.equals(((Build)object).terminatingStackManipulation);
                }

                public int hashCode() {
                    return ((((this.getClass().hashCode() * 31 + this.target.hashCode()) * 31 + ((Object)this.registeredTargetIndices).hashCode()) * 31 + this.methodInvocation.hashCode()) * 31 + ((Object)this.parameterStackManipulations).hashCode()) * 31 + this.terminatingStackManipulation.hashCode();
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum Illegal implements MethodBinding
        {
            INSTANCE;


            @Override
            public Integer getTargetParameterIndex(Object parameterBindingToken) {
                throw new IllegalStateException("Method is not bound");
            }

            @Override
            public MethodDescription getTarget() {
                throw new IllegalStateException("Method is not bound");
            }

            @Override
            public boolean isValid() {
                return false;
            }

            @Override
            public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
                throw new IllegalStateException("Cannot delegate to an unbound method");
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static interface ParameterBinding<T>
    extends StackManipulation {
        public T getIdentificationToken();

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        public static class Unique<T>
        implements ParameterBinding<T> {
            private final T identificationToken;
            private final StackManipulation delegate;

            public Unique(StackManipulation delegate, T identificationToken) {
                this.delegate = delegate;
                this.identificationToken = identificationToken;
            }

            public static <S> Unique<S> of(StackManipulation delegate, S identificationToken) {
                return new Unique<S>(delegate, identificationToken);
            }

            @Override
            public T getIdentificationToken() {
                return this.identificationToken;
            }

            @Override
            public boolean isValid() {
                return this.delegate.isValid();
            }

            @Override
            public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
                return this.delegate.apply(methodVisitor, implementationContext);
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
                if (!this.identificationToken.equals(((Unique)object).identificationToken)) {
                    return false;
                }
                return this.delegate.equals(((Unique)object).delegate);
            }

            public int hashCode() {
                return (this.getClass().hashCode() * 31 + this.identificationToken.hashCode()) * 31 + this.delegate.hashCode();
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        public static class Anonymous
        implements ParameterBinding<Object> {
            @HashCodeAndEqualsPlugin.ValueHandling(value=HashCodeAndEqualsPlugin.ValueHandling.Sort.IGNORE)
            private final Object anonymousToken;
            private final StackManipulation delegate;

            public Anonymous(StackManipulation delegate) {
                this.delegate = delegate;
                this.anonymousToken = new Object();
            }

            @Override
            public Object getIdentificationToken() {
                return this.anonymousToken;
            }

            @Override
            public boolean isValid() {
                return this.delegate.isValid();
            }

            @Override
            public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
                return this.delegate.apply(methodVisitor, implementationContext);
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
                return this.delegate.equals(((Anonymous)object).delegate);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + this.delegate.hashCode();
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum Illegal implements ParameterBinding<Void>
        {
            INSTANCE;


            @Override
            public Void getIdentificationToken() {
                throw new IllegalStateException("An illegal binding does not define an identification token");
            }

            @Override
            public boolean isValid() {
                return false;
            }

            @Override
            public StackManipulation.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext) {
                throw new IllegalStateException("An illegal parameter binding must not be applied");
            }
        }
    }

    public static interface MethodInvoker {
        public StackManipulation invoke(MethodDescription var1);

        @HashCodeAndEqualsPlugin.Enhance
        public static class Virtual
        implements MethodInvoker {
            private final TypeDescription typeDescription;

            public Virtual(TypeDescription typeDescription) {
                this.typeDescription = typeDescription;
            }

            public StackManipulation invoke(MethodDescription methodDescription) {
                return MethodInvocation.invoke(methodDescription).virtual(this.typeDescription);
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
                return this.typeDescription.equals(((Virtual)object).typeDescription);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + this.typeDescription.hashCode();
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum Simple implements MethodInvoker
        {
            INSTANCE;


            @Override
            public StackManipulation invoke(MethodDescription methodDescription) {
                return MethodInvocation.invoke(methodDescription);
            }
        }
    }

    public static interface Record {
        public MethodBinding bind(Implementation.Target var1, MethodDescription var2, TerminationHandler var3, MethodInvoker var4, Assigner var5);

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum Illegal implements Record
        {
            INSTANCE;


            @Override
            public MethodBinding bind(Implementation.Target implementationTarget, MethodDescription source, TerminationHandler terminationHandler, MethodInvoker methodInvoker, Assigner assigner) {
                return MethodBinding.Illegal.INSTANCE;
            }
        }
    }
}


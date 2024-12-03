/*
 * Decompiled with CFR 0.152.
 */
package net.bytebuddy.implementation;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.field.FieldList;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.MethodList;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.scaffold.FieldLocator;
import net.bytebuddy.dynamic.scaffold.InstrumentedType;
import net.bytebuddy.dynamic.scaffold.MethodGraph;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bind.MethodDelegationBinder;
import net.bytebuddy.implementation.bind.annotation.TargetMethodAnnotationDrivenBinder;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.implementation.bytecode.Duplication;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.TypeCreation;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.implementation.bytecode.member.FieldAccess;
import net.bytebuddy.implementation.bytecode.member.MethodInvocation;
import net.bytebuddy.implementation.bytecode.member.MethodVariableAccess;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.CompoundList;
import net.bytebuddy.utility.RandomString;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@HashCodeAndEqualsPlugin.Enhance
public class MethodDelegation
implements Implementation.Composable {
    private final ImplementationDelegate implementationDelegate;
    private final List<TargetMethodAnnotationDrivenBinder.ParameterBinder<?>> parameterBinders;
    private final MethodDelegationBinder.AmbiguityResolver ambiguityResolver;
    private final MethodDelegationBinder.TerminationHandler terminationHandler;
    private final MethodDelegationBinder.BindingResolver bindingResolver;
    private final Assigner assigner;

    protected MethodDelegation(ImplementationDelegate implementationDelegate, List<TargetMethodAnnotationDrivenBinder.ParameterBinder<?>> parameterBinders, MethodDelegationBinder.AmbiguityResolver ambiguityResolver, MethodDelegationBinder.BindingResolver bindingResolver) {
        this(implementationDelegate, parameterBinders, ambiguityResolver, MethodDelegationBinder.TerminationHandler.Default.RETURNING, bindingResolver, Assigner.DEFAULT);
    }

    private MethodDelegation(ImplementationDelegate implementationDelegate, List<TargetMethodAnnotationDrivenBinder.ParameterBinder<?>> parameterBinders, MethodDelegationBinder.AmbiguityResolver ambiguityResolver, MethodDelegationBinder.TerminationHandler terminationHandler, MethodDelegationBinder.BindingResolver bindingResolver, Assigner assigner) {
        this.implementationDelegate = implementationDelegate;
        this.parameterBinders = parameterBinders;
        this.terminationHandler = terminationHandler;
        this.ambiguityResolver = ambiguityResolver;
        this.bindingResolver = bindingResolver;
        this.assigner = assigner;
    }

    public static MethodDelegation to(Class<?> type) {
        return MethodDelegation.withDefaultConfiguration().to(type);
    }

    public static MethodDelegation to(TypeDescription typeDescription) {
        return MethodDelegation.withDefaultConfiguration().to(typeDescription);
    }

    public static MethodDelegation to(Object target) {
        return MethodDelegation.withDefaultConfiguration().to(target);
    }

    public static MethodDelegation to(Object target, MethodGraph.Compiler methodGraphCompiler) {
        return MethodDelegation.withDefaultConfiguration().to(target, methodGraphCompiler);
    }

    public static MethodDelegation to(Object target, String fieldName) {
        return MethodDelegation.withDefaultConfiguration().to(target, fieldName);
    }

    public static MethodDelegation to(Object target, String fieldName, MethodGraph.Compiler methodGraphCompiler) {
        return MethodDelegation.withDefaultConfiguration().to(target, fieldName, methodGraphCompiler);
    }

    public static MethodDelegation to(Object target, Type type) {
        return MethodDelegation.withDefaultConfiguration().to(target, type);
    }

    public static MethodDelegation to(Object target, Type type, MethodGraph.Compiler methodGraphCompiler) {
        return MethodDelegation.withDefaultConfiguration().to(target, type, methodGraphCompiler);
    }

    public static MethodDelegation to(Object target, Type type, String fieldName) {
        return MethodDelegation.withDefaultConfiguration().to(target, type, fieldName);
    }

    public static MethodDelegation to(Object target, Type type, String fieldName, MethodGraph.Compiler methodGraphCompiler) {
        return MethodDelegation.withDefaultConfiguration().to(target, type, fieldName, methodGraphCompiler);
    }

    public static MethodDelegation to(Object target, TypeDefinition typeDefinition) {
        return MethodDelegation.withDefaultConfiguration().to(target, typeDefinition);
    }

    public static MethodDelegation to(Object target, TypeDefinition typeDefinition, MethodGraph.Compiler methodGraphCompiler) {
        return MethodDelegation.withDefaultConfiguration().to(target, typeDefinition, methodGraphCompiler);
    }

    public static MethodDelegation to(Object target, TypeDefinition typeDefinition, String fieldName) {
        return MethodDelegation.withDefaultConfiguration().to(target, typeDefinition, fieldName);
    }

    public static MethodDelegation to(Object target, TypeDefinition typeDefinition, String fieldName, MethodGraph.Compiler methodGraphCompiler) {
        return MethodDelegation.withDefaultConfiguration().to(target, typeDefinition, fieldName, methodGraphCompiler);
    }

    public static MethodDelegation toConstructor(Class<?> type) {
        return MethodDelegation.withDefaultConfiguration().toConstructor(type);
    }

    public static MethodDelegation toConstructor(TypeDescription typeDescription) {
        return MethodDelegation.withDefaultConfiguration().toConstructor(typeDescription);
    }

    public static MethodDelegation toField(String name) {
        return MethodDelegation.withDefaultConfiguration().toField(name);
    }

    public static MethodDelegation toField(String name, FieldLocator.Factory fieldLocatorFactory) {
        return MethodDelegation.withDefaultConfiguration().toField(name, fieldLocatorFactory);
    }

    public static MethodDelegation toField(String name, MethodGraph.Compiler methodGraphCompiler) {
        return MethodDelegation.withDefaultConfiguration().toField(name, methodGraphCompiler);
    }

    public static MethodDelegation toField(String name, FieldLocator.Factory fieldLocatorFactory, MethodGraph.Compiler methodGraphCompiler) {
        return MethodDelegation.withDefaultConfiguration().toField(name, fieldLocatorFactory, methodGraphCompiler);
    }

    public static MethodDelegation toMethodReturnOf(String name) {
        return MethodDelegation.withDefaultConfiguration().toMethodReturnOf(name);
    }

    public static MethodDelegation toMethodReturnOf(String name, MethodGraph.Compiler methodGraphCompiler) {
        return MethodDelegation.withDefaultConfiguration().toMethodReturnOf(name, methodGraphCompiler);
    }

    public static WithCustomProperties withDefaultConfiguration() {
        return new WithCustomProperties(MethodDelegationBinder.AmbiguityResolver.DEFAULT, TargetMethodAnnotationDrivenBinder.ParameterBinder.DEFAULTS);
    }

    public static WithCustomProperties withEmptyConfiguration() {
        return new WithCustomProperties(MethodDelegationBinder.AmbiguityResolver.NoOp.INSTANCE, Collections.<TargetMethodAnnotationDrivenBinder.ParameterBinder<?>>emptyList());
    }

    public Implementation.Composable withAssigner(Assigner assigner) {
        return new MethodDelegation(this.implementationDelegate, this.parameterBinders, this.ambiguityResolver, this.terminationHandler, this.bindingResolver, assigner);
    }

    @Override
    public Implementation andThen(Implementation implementation) {
        return new Implementation.Compound(new MethodDelegation(this.implementationDelegate, this.parameterBinders, this.ambiguityResolver, MethodDelegationBinder.TerminationHandler.Default.DROPPING, this.bindingResolver, this.assigner), implementation);
    }

    @Override
    public Implementation.Composable andThen(Implementation.Composable implementation) {
        return new Implementation.Compound.Composable(new MethodDelegation(this.implementationDelegate, this.parameterBinders, this.ambiguityResolver, MethodDelegationBinder.TerminationHandler.Default.DROPPING, this.bindingResolver, this.assigner), implementation);
    }

    @Override
    public InstrumentedType prepare(InstrumentedType instrumentedType) {
        return this.implementationDelegate.prepare(instrumentedType);
    }

    @Override
    public ByteCodeAppender appender(Implementation.Target implementationTarget) {
        ImplementationDelegate.Compiled compiled = this.implementationDelegate.compile(implementationTarget.getInstrumentedType());
        return new Appender(implementationTarget, new MethodDelegationBinder.Processor(compiled.getRecords(), this.ambiguityResolver, this.bindingResolver), this.terminationHandler, this.assigner, compiled);
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
        if (!this.implementationDelegate.equals(((MethodDelegation)object).implementationDelegate)) {
            return false;
        }
        if (!((Object)this.parameterBinders).equals(((MethodDelegation)object).parameterBinders)) {
            return false;
        }
        if (!this.ambiguityResolver.equals(((MethodDelegation)object).ambiguityResolver)) {
            return false;
        }
        if (!this.terminationHandler.equals(((MethodDelegation)object).terminationHandler)) {
            return false;
        }
        if (!this.bindingResolver.equals(((MethodDelegation)object).bindingResolver)) {
            return false;
        }
        return this.assigner.equals(((MethodDelegation)object).assigner);
    }

    public int hashCode() {
        return (((((this.getClass().hashCode() * 31 + this.implementationDelegate.hashCode()) * 31 + ((Object)this.parameterBinders).hashCode()) * 31 + this.ambiguityResolver.hashCode()) * 31 + this.terminationHandler.hashCode()) * 31 + this.bindingResolver.hashCode()) * 31 + this.assigner.hashCode();
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    @HashCodeAndEqualsPlugin.Enhance
    public static class WithCustomProperties {
        private final MethodDelegationBinder.AmbiguityResolver ambiguityResolver;
        private final List<TargetMethodAnnotationDrivenBinder.ParameterBinder<?>> parameterBinders;
        private final MethodDelegationBinder.BindingResolver bindingResolver;
        private final ElementMatcher<? super MethodDescription> matcher;

        protected WithCustomProperties(MethodDelegationBinder.AmbiguityResolver ambiguityResolver, List<TargetMethodAnnotationDrivenBinder.ParameterBinder<?>> parameterBinders) {
            this(ambiguityResolver, parameterBinders, MethodDelegationBinder.BindingResolver.Default.INSTANCE, ElementMatchers.any());
        }

        private WithCustomProperties(MethodDelegationBinder.AmbiguityResolver ambiguityResolver, List<TargetMethodAnnotationDrivenBinder.ParameterBinder<?>> parameterBinders, MethodDelegationBinder.BindingResolver bindingResolver, ElementMatcher<? super MethodDescription> matcher) {
            this.ambiguityResolver = ambiguityResolver;
            this.parameterBinders = parameterBinders;
            this.bindingResolver = bindingResolver;
            this.matcher = matcher;
        }

        public WithCustomProperties withResolvers(MethodDelegationBinder.AmbiguityResolver ... ambiguityResolver) {
            return this.withResolvers(Arrays.asList(ambiguityResolver));
        }

        public WithCustomProperties withResolvers(List<? extends MethodDelegationBinder.AmbiguityResolver> ambiguityResolvers) {
            return new WithCustomProperties(new MethodDelegationBinder.AmbiguityResolver.Compound(CompoundList.of(this.ambiguityResolver, ambiguityResolvers)), this.parameterBinders, this.bindingResolver, this.matcher);
        }

        public WithCustomProperties withBinders(TargetMethodAnnotationDrivenBinder.ParameterBinder<?> ... parameterBinder) {
            return this.withBinders(Arrays.asList(parameterBinder));
        }

        public WithCustomProperties withBinders(List<? extends TargetMethodAnnotationDrivenBinder.ParameterBinder<?>> parameterBinders) {
            return new WithCustomProperties(this.ambiguityResolver, CompoundList.of(this.parameterBinders, parameterBinders), this.bindingResolver, this.matcher);
        }

        public WithCustomProperties withBindingResolver(MethodDelegationBinder.BindingResolver bindingResolver) {
            return new WithCustomProperties(this.ambiguityResolver, this.parameterBinders, bindingResolver, this.matcher);
        }

        public WithCustomProperties filter(ElementMatcher<? super MethodDescription> matcher) {
            return new WithCustomProperties(this.ambiguityResolver, this.parameterBinders, this.bindingResolver, new ElementMatcher.Junction.Conjunction(this.matcher, matcher));
        }

        public MethodDelegation to(Class<?> type) {
            return this.to(TypeDescription.ForLoadedType.of(type));
        }

        public MethodDelegation to(TypeDescription typeDescription) {
            if (typeDescription.isArray()) {
                throw new IllegalArgumentException("Cannot delegate to array " + typeDescription);
            }
            if (typeDescription.isPrimitive()) {
                throw new IllegalArgumentException("Cannot delegate to primitive " + typeDescription);
            }
            return new MethodDelegation(ImplementationDelegate.ForStaticMethod.of((MethodList)typeDescription.getDeclaredMethods().filter(ElementMatchers.isStatic().and(this.matcher)), TargetMethodAnnotationDrivenBinder.of(this.parameterBinders)), this.parameterBinders, this.ambiguityResolver, this.bindingResolver);
        }

        public MethodDelegation to(Object target) {
            return this.to(target, MethodGraph.Compiler.DEFAULT);
        }

        public MethodDelegation to(Object target, MethodGraph.Compiler methodGraphCompiler) {
            return this.to(target, target.getClass(), methodGraphCompiler);
        }

        public MethodDelegation to(Object target, String fieldName) {
            return this.to(target, fieldName, MethodGraph.Compiler.DEFAULT);
        }

        public MethodDelegation to(Object target, String fieldName, MethodGraph.Compiler methodGraphCompiler) {
            return this.to(target, target.getClass(), fieldName, methodGraphCompiler);
        }

        public MethodDelegation to(Object target, Type type) {
            return this.to(target, type, MethodGraph.Compiler.DEFAULT);
        }

        public MethodDelegation to(Object target, Type type, MethodGraph.Compiler methodGraphCompiler) {
            return this.to(target, type, "delegate$" + RandomString.hashOf(target), methodGraphCompiler);
        }

        public MethodDelegation to(Object target, Type type, String fieldName) {
            return this.to(target, type, fieldName, MethodGraph.Compiler.DEFAULT);
        }

        public MethodDelegation to(Object target, Type type, String fieldName, MethodGraph.Compiler methodGraphCompiler) {
            return this.to(target, TypeDefinition.Sort.describe(type), fieldName, methodGraphCompiler);
        }

        public MethodDelegation to(Object target, TypeDefinition typeDefinition) {
            return this.to(target, typeDefinition, MethodGraph.Compiler.DEFAULT);
        }

        public MethodDelegation to(Object target, TypeDefinition typeDefinition, MethodGraph.Compiler methodGraphCompiler) {
            return this.to(target, typeDefinition, "delegate$" + RandomString.hashOf(target), methodGraphCompiler);
        }

        public MethodDelegation to(Object target, TypeDefinition typeDefinition, String fieldName) {
            return this.to(target, typeDefinition, fieldName, MethodGraph.Compiler.DEFAULT);
        }

        public MethodDelegation to(Object target, TypeDefinition typeDefinition, String fieldName, MethodGraph.Compiler methodGraphCompiler) {
            if (!typeDefinition.asErasure().isInstance(target)) {
                throw new IllegalArgumentException(target + " is not an instance of " + typeDefinition);
            }
            return new MethodDelegation(new ImplementationDelegate.ForField.WithInstance(fieldName, methodGraphCompiler, this.parameterBinders, this.matcher, target, typeDefinition.asGenericType()), this.parameterBinders, this.ambiguityResolver, this.bindingResolver);
        }

        public MethodDelegation toConstructor(Class<?> type) {
            return this.toConstructor(TypeDescription.ForLoadedType.of(type));
        }

        public MethodDelegation toConstructor(TypeDescription typeDescription) {
            return new MethodDelegation(ImplementationDelegate.ForConstruction.of(typeDescription, (MethodList)typeDescription.getDeclaredMethods().filter(ElementMatchers.isConstructor().and(this.matcher)), TargetMethodAnnotationDrivenBinder.of(this.parameterBinders)), this.parameterBinders, this.ambiguityResolver, this.bindingResolver);
        }

        public MethodDelegation toField(String name) {
            return this.toField(name, FieldLocator.ForClassHierarchy.Factory.INSTANCE);
        }

        public MethodDelegation toField(String name, FieldLocator.Factory fieldLocatorFactory) {
            return this.toField(name, fieldLocatorFactory, MethodGraph.Compiler.DEFAULT);
        }

        public MethodDelegation toField(String name, MethodGraph.Compiler methodGraphCompiler) {
            return this.toField(name, FieldLocator.ForClassHierarchy.Factory.INSTANCE, methodGraphCompiler);
        }

        public MethodDelegation toField(String name, FieldLocator.Factory fieldLocatorFactory, MethodGraph.Compiler methodGraphCompiler) {
            return new MethodDelegation(new ImplementationDelegate.ForField.WithLookup(name, methodGraphCompiler, this.parameterBinders, this.matcher, fieldLocatorFactory), this.parameterBinders, this.ambiguityResolver, this.bindingResolver);
        }

        public MethodDelegation toMethodReturnOf(String name) {
            return this.toMethodReturnOf(name, MethodGraph.Compiler.DEFAULT);
        }

        public MethodDelegation toMethodReturnOf(String name, MethodGraph.Compiler methodGraphCompiler) {
            return new MethodDelegation(new ImplementationDelegate.ForMethodReturn(name, methodGraphCompiler, this.parameterBinders, this.matcher), this.parameterBinders, this.ambiguityResolver, this.bindingResolver);
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
            if (!this.ambiguityResolver.equals(((WithCustomProperties)object).ambiguityResolver)) {
                return false;
            }
            if (!((Object)this.parameterBinders).equals(((WithCustomProperties)object).parameterBinders)) {
                return false;
            }
            if (!this.bindingResolver.equals(((WithCustomProperties)object).bindingResolver)) {
                return false;
            }
            return this.matcher.equals(((WithCustomProperties)object).matcher);
        }

        public int hashCode() {
            return (((this.getClass().hashCode() * 31 + this.ambiguityResolver.hashCode()) * 31 + ((Object)this.parameterBinders).hashCode()) * 31 + this.bindingResolver.hashCode()) * 31 + this.matcher.hashCode();
        }
    }

    @HashCodeAndEqualsPlugin.Enhance
    protected static class Appender
    implements ByteCodeAppender {
        private final Implementation.Target implementationTarget;
        private final MethodDelegationBinder.Record processor;
        private final MethodDelegationBinder.TerminationHandler terminationHandler;
        private final Assigner assigner;
        private final ImplementationDelegate.Compiled compiled;

        protected Appender(Implementation.Target implementationTarget, MethodDelegationBinder.Record processor, MethodDelegationBinder.TerminationHandler terminationHandler, Assigner assigner, ImplementationDelegate.Compiled compiled) {
            this.implementationTarget = implementationTarget;
            this.processor = processor;
            this.terminationHandler = terminationHandler;
            this.assigner = assigner;
            this.compiled = compiled;
        }

        public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
            StackManipulation.Size stackSize = new StackManipulation.Compound(this.compiled.prepare(instrumentedMethod), this.processor.bind(this.implementationTarget, instrumentedMethod, this.terminationHandler, this.compiled.invoke(), this.assigner)).apply(methodVisitor, implementationContext);
            return new ByteCodeAppender.Size(stackSize.getMaximalSize(), instrumentedMethod.getStackSize());
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
            if (!this.processor.equals(((Appender)object).processor)) {
                return false;
            }
            if (!this.terminationHandler.equals(((Appender)object).terminationHandler)) {
                return false;
            }
            if (!this.assigner.equals(((Appender)object).assigner)) {
                return false;
            }
            return this.compiled.equals(((Appender)object).compiled);
        }

        public int hashCode() {
            return ((((this.getClass().hashCode() * 31 + this.implementationTarget.hashCode()) * 31 + this.processor.hashCode()) * 31 + this.terminationHandler.hashCode()) * 31 + this.assigner.hashCode()) * 31 + this.compiled.hashCode();
        }
    }

    protected static interface ImplementationDelegate
    extends InstrumentedType.Prepareable {
        public static final String FIELD_NAME_PREFIX = "delegate";

        public Compiled compile(TypeDescription var1);

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        public static class ForConstruction
        implements ImplementationDelegate {
            private final TypeDescription typeDescription;
            private final List<MethodDelegationBinder.Record> records;

            protected ForConstruction(TypeDescription typeDescription, List<MethodDelegationBinder.Record> records) {
                this.typeDescription = typeDescription;
                this.records = records;
            }

            protected static ImplementationDelegate of(TypeDescription typeDescription, MethodList<?> methods, MethodDelegationBinder methodDelegationBinder) {
                ArrayList<MethodDelegationBinder.Record> records = new ArrayList<MethodDelegationBinder.Record>(methods.size());
                for (MethodDescription methodDescription : methods) {
                    records.add(methodDelegationBinder.compile(methodDescription));
                }
                return new ForConstruction(typeDescription, records);
            }

            @Override
            public InstrumentedType prepare(InstrumentedType instrumentedType) {
                return instrumentedType;
            }

            @Override
            public Compiled compile(TypeDescription instrumentedType) {
                return new Compiled.ForConstruction(this.typeDescription, this.records);
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
                if (!this.typeDescription.equals(((ForConstruction)object).typeDescription)) {
                    return false;
                }
                return ((Object)this.records).equals(((ForConstruction)object).records);
            }

            public int hashCode() {
                return (this.getClass().hashCode() * 31 + this.typeDescription.hashCode()) * 31 + ((Object)this.records).hashCode();
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        public static class ForMethodReturn
        implements ImplementationDelegate {
            private final String name;
            private final MethodGraph.Compiler methodGraphCompiler;
            private final List<? extends TargetMethodAnnotationDrivenBinder.ParameterBinder<?>> parameterBinders;
            private final ElementMatcher<? super MethodDescription> matcher;

            protected ForMethodReturn(String name, MethodGraph.Compiler methodGraphCompiler, List<? extends TargetMethodAnnotationDrivenBinder.ParameterBinder<?>> parameterBinders, ElementMatcher<? super MethodDescription> matcher) {
                this.name = name;
                this.methodGraphCompiler = methodGraphCompiler;
                this.parameterBinders = parameterBinders;
                this.matcher = matcher;
            }

            @Override
            public Compiled compile(TypeDescription instrumentedType) {
                MethodList targets = (MethodList)new MethodList.Explicit(CompoundList.of(instrumentedType.getDeclaredMethods().filter(ElementMatchers.isStatic().or(ElementMatchers.isPrivate())), this.methodGraphCompiler.compile((TypeDefinition)instrumentedType).listNodes().asMethodList())).filter(ElementMatchers.named(this.name).and(ElementMatchers.takesArguments(0)).and(ElementMatchers.not(ElementMatchers.returns(ElementMatchers.isPrimitive().or(ElementMatchers.isArray())))));
                if (targets.size() != 1) {
                    throw new IllegalStateException(instrumentedType + " does not define method without arguments with name " + this.name + ": " + targets);
                }
                if (!((MethodDescription)targets.getOnly()).getReturnType().asErasure().isVisibleTo(instrumentedType)) {
                    throw new IllegalStateException(targets.getOnly() + " is not visible to " + instrumentedType);
                }
                MethodList candidates = (MethodList)this.methodGraphCompiler.compile(((MethodDescription)targets.getOnly()).getReturnType(), instrumentedType).listNodes().asMethodList().filter(this.matcher);
                ArrayList<MethodDelegationBinder.Record> records = new ArrayList<MethodDelegationBinder.Record>(candidates.size());
                MethodDelegationBinder methodDelegationBinder = TargetMethodAnnotationDrivenBinder.of(this.parameterBinders);
                for (MethodDescription candidate : candidates) {
                    records.add(methodDelegationBinder.compile(candidate));
                }
                return new Compiled.ForMethodReturn((MethodDescription)targets.get(0), records);
            }

            @Override
            public InstrumentedType prepare(InstrumentedType instrumentedType) {
                return instrumentedType;
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
                if (!this.name.equals(((ForMethodReturn)object).name)) {
                    return false;
                }
                if (!this.methodGraphCompiler.equals(((ForMethodReturn)object).methodGraphCompiler)) {
                    return false;
                }
                if (!((Object)this.parameterBinders).equals(((ForMethodReturn)object).parameterBinders)) {
                    return false;
                }
                return this.matcher.equals(((ForMethodReturn)object).matcher);
            }

            public int hashCode() {
                return (((this.getClass().hashCode() * 31 + this.name.hashCode()) * 31 + this.methodGraphCompiler.hashCode()) * 31 + ((Object)this.parameterBinders).hashCode()) * 31 + this.matcher.hashCode();
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        public static abstract class ForField
        implements ImplementationDelegate {
            protected final String fieldName;
            protected final MethodGraph.Compiler methodGraphCompiler;
            protected final List<? extends TargetMethodAnnotationDrivenBinder.ParameterBinder<?>> parameterBinders;
            protected final ElementMatcher<? super MethodDescription> matcher;

            protected ForField(String fieldName, MethodGraph.Compiler methodGraphCompiler, List<? extends TargetMethodAnnotationDrivenBinder.ParameterBinder<?>> parameterBinders, ElementMatcher<? super MethodDescription> matcher) {
                this.fieldName = fieldName;
                this.methodGraphCompiler = methodGraphCompiler;
                this.parameterBinders = parameterBinders;
                this.matcher = matcher;
            }

            @Override
            public Compiled compile(TypeDescription instrumentedType) {
                FieldDescription fieldDescription = this.resolve(instrumentedType);
                if (!fieldDescription.getType().asErasure().isVisibleTo(instrumentedType)) {
                    throw new IllegalStateException(fieldDescription + " is not visible to " + instrumentedType);
                }
                MethodList candidates = (MethodList)this.methodGraphCompiler.compile(fieldDescription.getType(), instrumentedType).listNodes().asMethodList().filter(this.matcher);
                ArrayList<MethodDelegationBinder.Record> records = new ArrayList<MethodDelegationBinder.Record>(candidates.size());
                MethodDelegationBinder methodDelegationBinder = TargetMethodAnnotationDrivenBinder.of(this.parameterBinders);
                for (MethodDescription candidate : candidates) {
                    records.add(methodDelegationBinder.compile(candidate));
                }
                return new Compiled.ForField(fieldDescription, records);
            }

            protected abstract FieldDescription resolve(TypeDescription var1);

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
                if (!this.fieldName.equals(((ForField)object).fieldName)) {
                    return false;
                }
                if (!this.methodGraphCompiler.equals(((ForField)object).methodGraphCompiler)) {
                    return false;
                }
                if (!((Object)this.parameterBinders).equals(((ForField)object).parameterBinders)) {
                    return false;
                }
                return this.matcher.equals(((ForField)object).matcher);
            }

            public int hashCode() {
                return (((this.getClass().hashCode() * 31 + this.fieldName.hashCode()) * 31 + this.methodGraphCompiler.hashCode()) * 31 + ((Object)this.parameterBinders).hashCode()) * 31 + this.matcher.hashCode();
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            protected static class WithLookup
            extends ForField {
                private final FieldLocator.Factory fieldLocatorFactory;

                protected WithLookup(String fieldName, MethodGraph.Compiler methodGraphCompiler, List<? extends TargetMethodAnnotationDrivenBinder.ParameterBinder<?>> parameterBinders, ElementMatcher<? super MethodDescription> matcher, FieldLocator.Factory fieldLocatorFactory) {
                    super(fieldName, methodGraphCompiler, parameterBinders, matcher);
                    this.fieldLocatorFactory = fieldLocatorFactory;
                }

                @Override
                public InstrumentedType prepare(InstrumentedType instrumentedType) {
                    return instrumentedType;
                }

                @Override
                protected FieldDescription resolve(TypeDescription instrumentedType) {
                    FieldLocator.Resolution resolution = this.fieldLocatorFactory.make(instrumentedType).locate(this.fieldName);
                    if (!resolution.isResolved()) {
                        throw new IllegalStateException("Could not locate " + this.fieldName + " on " + instrumentedType);
                    }
                    return resolution.getField();
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
                    return this.fieldLocatorFactory.equals(((WithLookup)object).fieldLocatorFactory);
                }

                @Override
                public int hashCode() {
                    return super.hashCode() * 31 + this.fieldLocatorFactory.hashCode();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            protected static class WithInstance
            extends ForField {
                private final Object target;
                private final TypeDescription.Generic fieldType;

                protected WithInstance(String fieldName, MethodGraph.Compiler methodGraphCompiler, List<? extends TargetMethodAnnotationDrivenBinder.ParameterBinder<?>> parameterBinders, ElementMatcher<? super MethodDescription> matcher, Object target, TypeDescription.Generic fieldType) {
                    super(fieldName, methodGraphCompiler, parameterBinders, matcher);
                    this.target = target;
                    this.fieldType = fieldType;
                }

                @Override
                public InstrumentedType prepare(InstrumentedType instrumentedType) {
                    return instrumentedType.withAuxiliaryField(new FieldDescription.Token(this.fieldName, 4169, this.fieldType), this.target);
                }

                @Override
                protected FieldDescription resolve(TypeDescription instrumentedType) {
                    if (!this.fieldType.asErasure().isVisibleTo(instrumentedType)) {
                        throw new IllegalStateException(this.fieldType + " is not visible to " + instrumentedType);
                    }
                    return (FieldDescription)((FieldList)instrumentedType.getDeclaredFields().filter(ElementMatchers.named(this.fieldName).and(ElementMatchers.fieldType(this.fieldType.asErasure())))).getOnly();
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
                    if (!this.target.equals(((WithInstance)object).target)) {
                        return false;
                    }
                    return this.fieldType.equals(((WithInstance)object).fieldType);
                }

                @Override
                public int hashCode() {
                    return (super.hashCode() * 31 + this.target.hashCode()) * 31 + this.fieldType.hashCode();
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        public static class ForStaticMethod
        implements ImplementationDelegate {
            private final List<MethodDelegationBinder.Record> records;

            protected ForStaticMethod(List<MethodDelegationBinder.Record> records) {
                this.records = records;
            }

            protected static ImplementationDelegate of(MethodList<?> methods, MethodDelegationBinder methodDelegationBinder) {
                ArrayList<MethodDelegationBinder.Record> records = new ArrayList<MethodDelegationBinder.Record>(methods.size());
                for (MethodDescription methodDescription : methods) {
                    records.add(methodDelegationBinder.compile(methodDescription));
                }
                return new ForStaticMethod(records);
            }

            @Override
            public InstrumentedType prepare(InstrumentedType instrumentedType) {
                return instrumentedType;
            }

            @Override
            public Compiled compile(TypeDescription instrumentedType) {
                return new Compiled.ForStaticCall(this.records);
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
                return ((Object)this.records).equals(((ForStaticMethod)object).records);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + ((Object)this.records).hashCode();
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static interface Compiled {
            public StackManipulation prepare(MethodDescription var1);

            public MethodDelegationBinder.MethodInvoker invoke();

            public List<MethodDelegationBinder.Record> getRecords();

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            public static class ForConstruction
            implements Compiled {
                private final TypeDescription typeDescription;
                private final List<MethodDelegationBinder.Record> records;

                protected ForConstruction(TypeDescription typeDescription, List<MethodDelegationBinder.Record> records) {
                    this.typeDescription = typeDescription;
                    this.records = records;
                }

                @Override
                public StackManipulation prepare(MethodDescription instrumentedMethod) {
                    return new StackManipulation.Compound(TypeCreation.of(this.typeDescription), Duplication.SINGLE);
                }

                @Override
                public MethodDelegationBinder.MethodInvoker invoke() {
                    return MethodDelegationBinder.MethodInvoker.Simple.INSTANCE;
                }

                @Override
                public List<MethodDelegationBinder.Record> getRecords() {
                    return this.records;
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
                    if (!this.typeDescription.equals(((ForConstruction)object).typeDescription)) {
                        return false;
                    }
                    return ((Object)this.records).equals(((ForConstruction)object).records);
                }

                public int hashCode() {
                    return (this.getClass().hashCode() * 31 + this.typeDescription.hashCode()) * 31 + ((Object)this.records).hashCode();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            public static class ForMethodReturn
            implements Compiled {
                private final MethodDescription methodDescription;
                private final List<MethodDelegationBinder.Record> records;

                protected ForMethodReturn(MethodDescription methodDescription, List<MethodDelegationBinder.Record> records) {
                    this.methodDescription = methodDescription;
                    this.records = records;
                }

                @Override
                public StackManipulation prepare(MethodDescription instrumentedMethod) {
                    if (instrumentedMethod.isStatic() && !this.methodDescription.isStatic()) {
                        throw new IllegalStateException("Cannot invoke " + this.methodDescription + " from " + instrumentedMethod);
                    }
                    return new StackManipulation.Compound(this.methodDescription.isStatic() ? StackManipulation.Trivial.INSTANCE : MethodVariableAccess.loadThis(), MethodInvocation.invoke(this.methodDescription));
                }

                @Override
                public MethodDelegationBinder.MethodInvoker invoke() {
                    return new MethodDelegationBinder.MethodInvoker.Virtual(this.methodDescription.getReturnType().asErasure());
                }

                @Override
                public List<MethodDelegationBinder.Record> getRecords() {
                    return this.records;
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
                    if (!this.methodDescription.equals(((ForMethodReturn)object).methodDescription)) {
                        return false;
                    }
                    return ((Object)this.records).equals(((ForMethodReturn)object).records);
                }

                public int hashCode() {
                    return (this.getClass().hashCode() * 31 + this.methodDescription.hashCode()) * 31 + ((Object)this.records).hashCode();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            public static class ForField
            implements Compiled {
                private final FieldDescription fieldDescription;
                private final List<MethodDelegationBinder.Record> records;

                protected ForField(FieldDescription fieldDescription, List<MethodDelegationBinder.Record> records) {
                    this.fieldDescription = fieldDescription;
                    this.records = records;
                }

                @Override
                public StackManipulation prepare(MethodDescription instrumentedMethod) {
                    if (instrumentedMethod.isStatic() && !this.fieldDescription.isStatic()) {
                        throw new IllegalStateException("Cannot read " + this.fieldDescription + " from " + instrumentedMethod);
                    }
                    return new StackManipulation.Compound(this.fieldDescription.isStatic() ? StackManipulation.Trivial.INSTANCE : MethodVariableAccess.loadThis(), FieldAccess.forField(this.fieldDescription).read());
                }

                @Override
                public MethodDelegationBinder.MethodInvoker invoke() {
                    return new MethodDelegationBinder.MethodInvoker.Virtual(this.fieldDescription.getType().asErasure());
                }

                @Override
                public List<MethodDelegationBinder.Record> getRecords() {
                    return this.records;
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
                    if (!this.fieldDescription.equals(((ForField)object).fieldDescription)) {
                        return false;
                    }
                    return ((Object)this.records).equals(((ForField)object).records);
                }

                public int hashCode() {
                    return (this.getClass().hashCode() * 31 + this.fieldDescription.hashCode()) * 31 + ((Object)this.records).hashCode();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            public static class ForStaticCall
            implements Compiled {
                private final List<MethodDelegationBinder.Record> records;

                protected ForStaticCall(List<MethodDelegationBinder.Record> records) {
                    this.records = records;
                }

                @Override
                public StackManipulation prepare(MethodDescription instrumentedMethod) {
                    return StackManipulation.Trivial.INSTANCE;
                }

                @Override
                public MethodDelegationBinder.MethodInvoker invoke() {
                    return MethodDelegationBinder.MethodInvoker.Simple.INSTANCE;
                }

                @Override
                public List<MethodDelegationBinder.Record> getRecords() {
                    return this.records;
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
                    return ((Object)this.records).equals(((ForStaticCall)object).records);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + ((Object)this.records).hashCode();
                }
            }
        }
    }
}


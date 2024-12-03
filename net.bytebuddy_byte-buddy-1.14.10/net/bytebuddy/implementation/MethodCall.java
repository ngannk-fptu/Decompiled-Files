/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package net.bytebuddy.implementation;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import net.bytebuddy.build.HashCodeAndEqualsPlugin;
import net.bytebuddy.description.enumeration.EnumerationDescription;
import net.bytebuddy.description.field.FieldDescription;
import net.bytebuddy.description.field.FieldList;
import net.bytebuddy.description.method.MethodDescription;
import net.bytebuddy.description.method.ParameterDescription;
import net.bytebuddy.description.method.ParameterList;
import net.bytebuddy.description.type.TypeDefinition;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.scaffold.FieldLocator;
import net.bytebuddy.dynamic.scaffold.InstrumentedType;
import net.bytebuddy.dynamic.scaffold.MethodGraph;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.implementation.bytecode.Duplication;
import net.bytebuddy.implementation.bytecode.Removal;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.TypeCreation;
import net.bytebuddy.implementation.bytecode.assign.Assigner;
import net.bytebuddy.implementation.bytecode.collection.ArrayAccess;
import net.bytebuddy.implementation.bytecode.collection.ArrayFactory;
import net.bytebuddy.implementation.bytecode.constant.ClassConstant;
import net.bytebuddy.implementation.bytecode.constant.IntegerConstant;
import net.bytebuddy.implementation.bytecode.constant.NullConstant;
import net.bytebuddy.implementation.bytecode.member.FieldAccess;
import net.bytebuddy.implementation.bytecode.member.MethodInvocation;
import net.bytebuddy.implementation.bytecode.member.MethodReturn;
import net.bytebuddy.implementation.bytecode.member.MethodVariableAccess;
import net.bytebuddy.jar.asm.MethodVisitor;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import net.bytebuddy.utility.CompoundList;
import net.bytebuddy.utility.ConstantValue;
import net.bytebuddy.utility.JavaConstant;
import net.bytebuddy.utility.RandomString;
import net.bytebuddy.utility.nullability.MaybeNull;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
@HashCodeAndEqualsPlugin.Enhance
public class MethodCall
implements Implementation.Composable {
    protected final MethodLocator.Factory methodLocator;
    protected final TargetHandler.Factory targetHandler;
    protected final List<ArgumentLoader.Factory> argumentLoaders;
    protected final MethodInvoker.Factory methodInvoker;
    protected final TerminationHandler.Factory terminationHandler;
    protected final Assigner assigner;
    protected final Assigner.Typing typing;

    protected MethodCall(MethodLocator.Factory methodLocator, TargetHandler.Factory targetHandler, List<ArgumentLoader.Factory> argumentLoaders, MethodInvoker.Factory methodInvoker, TerminationHandler.Factory terminationHandler, Assigner assigner, Assigner.Typing typing) {
        this.methodLocator = methodLocator;
        this.targetHandler = targetHandler;
        this.argumentLoaders = argumentLoaders;
        this.methodInvoker = methodInvoker;
        this.terminationHandler = terminationHandler;
        this.assigner = assigner;
        this.typing = typing;
    }

    public static WithoutSpecifiedTarget invoke(Method method) {
        return MethodCall.invoke(new MethodDescription.ForLoadedMethod(method));
    }

    public static WithoutSpecifiedTarget invoke(Constructor<?> constructor) {
        return MethodCall.invoke(new MethodDescription.ForLoadedConstructor(constructor));
    }

    public static WithoutSpecifiedTarget invoke(MethodDescription methodDescription) {
        return MethodCall.invoke(new MethodLocator.ForExplicitMethod(methodDescription));
    }

    public static WithoutSpecifiedTarget invoke(ElementMatcher<? super MethodDescription> matcher) {
        return MethodCall.invoke(matcher, MethodGraph.Compiler.DEFAULT);
    }

    public static WithoutSpecifiedTarget invoke(ElementMatcher<? super MethodDescription> matcher, MethodGraph.Compiler methodGraphCompiler) {
        return MethodCall.invoke(new MethodLocator.ForElementMatcher.Factory(matcher, methodGraphCompiler));
    }

    public static WithoutSpecifiedTarget invoke(MethodLocator.Factory methodLocator) {
        return new WithoutSpecifiedTarget(methodLocator);
    }

    public static WithoutSpecifiedTarget invokeSelf() {
        return new WithoutSpecifiedTarget(MethodLocator.ForInstrumentedMethod.INSTANCE);
    }

    public static MethodCall invokeSuper() {
        return MethodCall.invokeSelf().onSuper();
    }

    public static Implementation.Composable call(Callable<?> callable) {
        try {
            return MethodCall.invoke(Callable.class.getMethod("call", new Class[0])).on(callable, Callable.class).withAssigner(Assigner.DEFAULT, Assigner.Typing.DYNAMIC);
        }
        catch (NoSuchMethodException exception) {
            throw new IllegalStateException("Could not locate Callable::call method", exception);
        }
    }

    public static Implementation.Composable run(Runnable runnable) {
        try {
            return MethodCall.invoke(Runnable.class.getMethod("run", new Class[0])).on(runnable, Runnable.class).withAssigner(Assigner.DEFAULT, Assigner.Typing.DYNAMIC);
        }
        catch (NoSuchMethodException exception) {
            throw new IllegalStateException("Could not locate Runnable::run method", exception);
        }
    }

    public static MethodCall construct(Constructor<?> constructor) {
        return MethodCall.construct(new MethodDescription.ForLoadedConstructor(constructor));
    }

    public static MethodCall construct(MethodDescription methodDescription) {
        if (!methodDescription.isConstructor()) {
            throw new IllegalArgumentException("Not a constructor: " + methodDescription);
        }
        return new MethodCall(new MethodLocator.ForExplicitMethod(methodDescription), TargetHandler.ForConstructingInvocation.Factory.INSTANCE, Collections.<ArgumentLoader.Factory>emptyList(), MethodInvoker.ForContextualInvocation.Factory.INSTANCE, TerminationHandler.Simple.RETURNING, Assigner.DEFAULT, Assigner.Typing.STATIC);
    }

    public MethodCall with(Object ... argument) {
        ArrayList<ArgumentLoader.Factory> argumentLoaders = new ArrayList<ArgumentLoader.Factory>(argument.length);
        for (Object anArgument : argument) {
            argumentLoaders.add(ArgumentLoader.ForStackManipulation.of(anArgument));
        }
        return this.with(argumentLoaders);
    }

    public MethodCall with(TypeDescription ... typeDescription) {
        ArrayList<ArgumentLoader.ForStackManipulation> argumentLoaders = new ArrayList<ArgumentLoader.ForStackManipulation>(typeDescription.length);
        for (TypeDescription aTypeDescription : typeDescription) {
            argumentLoaders.add(new ArgumentLoader.ForStackManipulation(ClassConstant.of(aTypeDescription), (Type)((Object)Class.class)));
        }
        return this.with(argumentLoaders);
    }

    public MethodCall with(EnumerationDescription ... enumerationDescription) {
        ArrayList<ArgumentLoader.ForStackManipulation> argumentLoaders = new ArrayList<ArgumentLoader.ForStackManipulation>(enumerationDescription.length);
        for (EnumerationDescription anEnumerationDescription : enumerationDescription) {
            argumentLoaders.add(new ArgumentLoader.ForStackManipulation(FieldAccess.forEnumeration(anEnumerationDescription), anEnumerationDescription.getEnumerationType()));
        }
        return this.with(argumentLoaders);
    }

    public MethodCall with(ConstantValue ... constant) {
        ArrayList<ArgumentLoader.ForStackManipulation> argumentLoaders = new ArrayList<ArgumentLoader.ForStackManipulation>(constant.length);
        for (ConstantValue aConstant : constant) {
            argumentLoaders.add(new ArgumentLoader.ForStackManipulation(aConstant.toStackManipulation(), aConstant.getTypeDescription()));
        }
        return this.with(argumentLoaders);
    }

    public MethodCall with(JavaConstant ... constant) {
        return this.with((ConstantValue[])constant);
    }

    public MethodCall withReference(Object ... argument) {
        ArrayList<ArgumentLoader.ForNullConstant> argumentLoaders = new ArrayList<ArgumentLoader.ForNullConstant>(argument.length);
        for (Object anArgument : argument) {
            argumentLoaders.add((ArgumentLoader.ForNullConstant)(anArgument == null ? ArgumentLoader.ForNullConstant.INSTANCE : new ArgumentLoader.ForInstance.Factory(anArgument)));
        }
        return this.with(argumentLoaders);
    }

    public MethodCall withArgument(int ... index) {
        ArrayList<ArgumentLoader.ForMethodParameter.Factory> argumentLoaders = new ArrayList<ArgumentLoader.ForMethodParameter.Factory>(index.length);
        for (int anIndex : index) {
            if (anIndex < 0) {
                throw new IllegalArgumentException("Negative index: " + anIndex);
            }
            argumentLoaders.add(new ArgumentLoader.ForMethodParameter.Factory(anIndex));
        }
        return this.with(argumentLoaders);
    }

    public MethodCall withAllArguments() {
        return this.with(ArgumentLoader.ForMethodParameter.OfInstrumentedMethod.INSTANCE);
    }

    public MethodCall withArgumentArray() {
        return this.with(ArgumentLoader.ForMethodParameterArray.ForInstrumentedMethod.INSTANCE);
    }

    public MethodCall withArgumentArrayElements(int index) {
        if (index < 0) {
            throw new IllegalArgumentException("A parameter index cannot be negative: " + index);
        }
        return this.with(new ArgumentLoader.ForMethodParameterArrayElement.OfInvokedMethod(index));
    }

    public MethodCall withArgumentArrayElements(int index, int size) {
        return this.withArgumentArrayElements(index, 0, size);
    }

    public MethodCall withArgumentArrayElements(int index, int start, int size) {
        if (index < 0) {
            throw new IllegalArgumentException("A parameter index cannot be negative: " + index);
        }
        if (start < 0) {
            throw new IllegalArgumentException("An array index cannot be negative: " + start);
        }
        if (size == 0) {
            return this;
        }
        if (size < 0) {
            throw new IllegalArgumentException("Size cannot be negative: " + size);
        }
        ArrayList<ArgumentLoader.ForMethodParameterArrayElement.OfParameter> argumentLoaders = new ArrayList<ArgumentLoader.ForMethodParameterArrayElement.OfParameter>(size);
        for (int position = 0; position < size; ++position) {
            argumentLoaders.add(new ArgumentLoader.ForMethodParameterArrayElement.OfParameter(index, start + position));
        }
        return this.with(argumentLoaders);
    }

    public MethodCall withThis() {
        return this.with(ArgumentLoader.ForThisReference.Factory.INSTANCE);
    }

    public MethodCall withOwnType() {
        return this.with(ArgumentLoader.ForInstrumentedType.Factory.INSTANCE);
    }

    public MethodCall withField(String ... name) {
        return this.withField(FieldLocator.ForClassHierarchy.Factory.INSTANCE, name);
    }

    public MethodCall withField(FieldLocator.Factory fieldLocatorFactory, String ... name) {
        ArrayList<ArgumentLoader.ForField.Factory> argumentLoaders = new ArrayList<ArgumentLoader.ForField.Factory>(name.length);
        for (String aName : name) {
            argumentLoaders.add(new ArgumentLoader.ForField.Factory(aName, fieldLocatorFactory));
        }
        return this.with(argumentLoaders);
    }

    public MethodCall withMethodCall(MethodCall methodCall) {
        return this.with(new ArgumentLoader.ForMethodCall.Factory(methodCall));
    }

    public MethodCall with(StackManipulation stackManipulation, Type type) {
        return this.with(stackManipulation, TypeDefinition.Sort.describe(type));
    }

    public MethodCall with(StackManipulation stackManipulation, TypeDefinition typeDefinition) {
        return this.with(new ArgumentLoader.ForStackManipulation(stackManipulation, typeDefinition));
    }

    public MethodCall with(ArgumentLoader.Factory ... argumentLoader) {
        return this.with(Arrays.asList(argumentLoader));
    }

    public MethodCall with(List<? extends ArgumentLoader.Factory> argumentLoaders) {
        return new MethodCall(this.methodLocator, this.targetHandler, CompoundList.of(this.argumentLoaders, argumentLoaders), this.methodInvoker, this.terminationHandler, this.assigner, this.typing);
    }

    public FieldSetting setsField(Field field) {
        return this.setsField(new FieldDescription.ForLoadedField(field));
    }

    public FieldSetting setsField(FieldDescription fieldDescription) {
        return new FieldSetting(new MethodCall(this.methodLocator, this.targetHandler, this.argumentLoaders, this.methodInvoker, new TerminationHandler.FieldSetting.Explicit(fieldDescription), this.assigner, this.typing));
    }

    public FieldSetting setsField(ElementMatcher<? super FieldDescription> matcher) {
        return new FieldSetting(new MethodCall(this.methodLocator, this.targetHandler, this.argumentLoaders, this.methodInvoker, new TerminationHandler.FieldSetting.Implicit(matcher), this.assigner, this.typing));
    }

    public Implementation.Composable withAssigner(Assigner assigner, Assigner.Typing typing) {
        return new MethodCall(this.methodLocator, this.targetHandler, this.argumentLoaders, this.methodInvoker, this.terminationHandler, assigner, typing);
    }

    @Override
    public Implementation andThen(Implementation implementation) {
        return new Implementation.Compound(new MethodCall(this.methodLocator, this.targetHandler, this.argumentLoaders, this.methodInvoker, TerminationHandler.Simple.DROPPING, this.assigner, this.typing), implementation);
    }

    @Override
    public Implementation.Composable andThen(Implementation.Composable implementation) {
        return new Implementation.Compound.Composable(new MethodCall(this.methodLocator, this.targetHandler, this.argumentLoaders, this.methodInvoker, TerminationHandler.Simple.DROPPING, this.assigner, this.typing), implementation);
    }

    @Override
    public InstrumentedType prepare(InstrumentedType instrumentedType) {
        for (InstrumentedType.Prepareable prepareable : this.argumentLoaders) {
            instrumentedType = prepareable.prepare(instrumentedType);
        }
        return this.targetHandler.prepare(instrumentedType);
    }

    @Override
    public ByteCodeAppender appender(Implementation.Target implementationTarget) {
        return new Appender(implementationTarget, this.terminationHandler.make(implementationTarget.getInstrumentedType()));
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
        if (!this.typing.equals((Object)((MethodCall)object).typing)) {
            return false;
        }
        if (!this.methodLocator.equals(((MethodCall)object).methodLocator)) {
            return false;
        }
        if (!this.targetHandler.equals(((MethodCall)object).targetHandler)) {
            return false;
        }
        if (!((Object)this.argumentLoaders).equals(((MethodCall)object).argumentLoaders)) {
            return false;
        }
        if (!this.methodInvoker.equals(((MethodCall)object).methodInvoker)) {
            return false;
        }
        if (!this.terminationHandler.equals(((MethodCall)object).terminationHandler)) {
            return false;
        }
        return this.assigner.equals(((MethodCall)object).assigner);
    }

    public int hashCode() {
        return ((((((this.getClass().hashCode() * 31 + this.methodLocator.hashCode()) * 31 + this.targetHandler.hashCode()) * 31 + ((Object)this.argumentLoaders).hashCode()) * 31 + this.methodInvoker.hashCode()) * 31 + this.terminationHandler.hashCode()) * 31 + this.assigner.hashCode()) * 31 + this.typing.hashCode();
    }

    @HashCodeAndEqualsPlugin.Enhance(includeSyntheticFields=true)
    protected class Appender
    implements ByteCodeAppender {
        private final Implementation.Target implementationTarget;
        private final MethodLocator methodLocator;
        private final List<ArgumentLoader.ArgumentProvider> argumentProviders;
        private final MethodInvoker methodInvoker;
        private final TargetHandler targetHandler;
        private final TerminationHandler terminationHandler;

        protected Appender(Implementation.Target implementationTarget, TerminationHandler terminationHandler) {
            this.implementationTarget = implementationTarget;
            this.methodLocator = MethodCall.this.methodLocator.make(implementationTarget.getInstrumentedType());
            this.argumentProviders = new ArrayList<ArgumentLoader.ArgumentProvider>(MethodCall.this.argumentLoaders.size());
            for (ArgumentLoader.Factory factory : MethodCall.this.argumentLoaders) {
                this.argumentProviders.add(factory.make(implementationTarget));
            }
            this.methodInvoker = MethodCall.this.methodInvoker.make(implementationTarget.getInstrumentedType());
            this.targetHandler = MethodCall.this.targetHandler.make(implementationTarget);
            this.terminationHandler = terminationHandler;
        }

        public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
            TargetHandler.Resolved targetHandler = this.targetHandler.resolve(instrumentedMethod);
            return new ByteCodeAppender.Size(new StackManipulation.Compound(this.terminationHandler.prepare(), this.toStackManipulation(instrumentedMethod, this.toInvokedMethod(instrumentedMethod, targetHandler), targetHandler)).apply(methodVisitor, implementationContext).getMaximalSize(), instrumentedMethod.getStackSize());
        }

        protected MethodDescription toInvokedMethod(MethodDescription instrumentedMethod, TargetHandler.Resolved targetHandler) {
            return this.methodLocator.resolve(targetHandler.getTypeDescription(), instrumentedMethod);
        }

        protected StackManipulation toStackManipulation(MethodDescription instrumentedMethod, MethodDescription invokedMethod, TargetHandler.Resolved targetHandler) {
            ArrayList<ArgumentLoader> argumentLoaders = new ArrayList<ArgumentLoader>();
            for (ArgumentLoader.ArgumentProvider argumentProvider : this.argumentProviders) {
                argumentLoaders.addAll(argumentProvider.resolve(instrumentedMethod, invokedMethod));
            }
            ParameterList<?> parameters = invokedMethod.getParameters();
            if (parameters.size() != argumentLoaders.size()) {
                throw new IllegalStateException(invokedMethod + " does not accept " + argumentLoaders.size() + " arguments");
            }
            Iterator parameterIterator = parameters.iterator();
            ArrayList<StackManipulation> argumentInstructions = new ArrayList<StackManipulation>(argumentLoaders.size());
            for (ArgumentLoader argumentLoader : argumentLoaders) {
                argumentInstructions.add(argumentLoader.toStackManipulation((ParameterDescription)parameterIterator.next(), MethodCall.this.assigner, MethodCall.this.typing));
            }
            return new StackManipulation.Compound(targetHandler.toStackManipulation(invokedMethod, MethodCall.this.assigner, MethodCall.this.typing), new StackManipulation.Compound(argumentInstructions), this.methodInvoker.toStackManipulation(invokedMethod, this.implementationTarget), this.terminationHandler.toStackManipulation(invokedMethod, instrumentedMethod, MethodCall.this.assigner, MethodCall.this.typing));
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
            if (!this.methodLocator.equals(((Appender)object).methodLocator)) {
                return false;
            }
            if (!((Object)this.argumentProviders).equals(((Appender)object).argumentProviders)) {
                return false;
            }
            if (!this.methodInvoker.equals(((Appender)object).methodInvoker)) {
                return false;
            }
            if (!this.targetHandler.equals(((Appender)object).targetHandler)) {
                return false;
            }
            if (!this.terminationHandler.equals(((Appender)object).terminationHandler)) {
                return false;
            }
            return MethodCall.this.equals(((Appender)object).MethodCall.this);
        }

        public int hashCode() {
            return ((((((this.getClass().hashCode() * 31 + this.implementationTarget.hashCode()) * 31 + this.methodLocator.hashCode()) * 31 + ((Object)this.argumentProviders).hashCode()) * 31 + this.methodInvoker.hashCode()) * 31 + this.targetHandler.hashCode()) * 31 + this.terminationHandler.hashCode()) * 31 + MethodCall.this.hashCode();
        }
    }

    @HashCodeAndEqualsPlugin.Enhance
    public static class FieldSetting
    implements Implementation.Composable {
        private final MethodCall methodCall;

        protected FieldSetting(MethodCall methodCall) {
            this.methodCall = methodCall;
        }

        public Implementation.Composable withAssigner(Assigner assigner, Assigner.Typing typing) {
            return new FieldSetting((MethodCall)this.methodCall.withAssigner(assigner, typing));
        }

        public InstrumentedType prepare(InstrumentedType instrumentedType) {
            return this.methodCall.prepare(instrumentedType);
        }

        public ByteCodeAppender appender(Implementation.Target implementationTarget) {
            return new ByteCodeAppender.Compound(this.methodCall.appender(implementationTarget), Appender.INSTANCE);
        }

        public Implementation andThen(Implementation implementation) {
            return new Implementation.Compound(this.methodCall, implementation);
        }

        public Implementation.Composable andThen(Implementation.Composable implementation) {
            return new Implementation.Compound.Composable(this.methodCall, implementation);
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
            return this.methodCall.equals(((FieldSetting)object).methodCall);
        }

        public int hashCode() {
            return this.getClass().hashCode() * 31 + this.methodCall.hashCode();
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        protected static enum Appender implements ByteCodeAppender
        {
            INSTANCE;


            @Override
            public ByteCodeAppender.Size apply(MethodVisitor methodVisitor, Implementation.Context implementationContext, MethodDescription instrumentedMethod) {
                if (!instrumentedMethod.getReturnType().represents(Void.TYPE)) {
                    throw new IllegalStateException("Instrumented method " + instrumentedMethod + " does not return void for field setting method call");
                }
                return new ByteCodeAppender.Size(MethodReturn.VOID.apply(methodVisitor, implementationContext).getMaximalSize(), instrumentedMethod.getStackSize());
            }
        }
    }

    /*
     * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
     */
    public static class WithoutSpecifiedTarget
    extends MethodCall {
        protected WithoutSpecifiedTarget(MethodLocator.Factory methodLocator) {
            super(methodLocator, TargetHandler.ForSelfOrStaticInvocation.Factory.INSTANCE, Collections.<ArgumentLoader.Factory>emptyList(), MethodInvoker.ForContextualInvocation.Factory.INSTANCE, TerminationHandler.Simple.RETURNING, Assigner.DEFAULT, Assigner.Typing.STATIC);
        }

        public MethodCall on(Object target) {
            return this.on(target, target.getClass());
        }

        public <T> MethodCall on(T target, Class<? super T> type) {
            return new MethodCall(this.methodLocator, new TargetHandler.ForValue.Factory(target, TypeDescription.Generic.OfNonGenericType.ForLoadedType.of(type)), this.argumentLoaders, new MethodInvoker.ForVirtualInvocation.Factory(TypeDescription.ForLoadedType.of(type)), this.terminationHandler, this.assigner, this.typing);
        }

        public MethodCall on(StackManipulation stackManipulation, Class<?> type) {
            return this.on(stackManipulation, TypeDescription.ForLoadedType.of(type));
        }

        public MethodCall on(StackManipulation stackManipulation, TypeDescription typeDescription) {
            return new MethodCall(this.methodLocator, new TargetHandler.Simple(typeDescription, stackManipulation), this.argumentLoaders, new MethodInvoker.ForVirtualInvocation.Factory(typeDescription), this.terminationHandler, this.assigner, this.typing);
        }

        public MethodCall onArgument(int index) {
            if (index < 0) {
                throw new IllegalArgumentException("An argument index cannot be negative: " + index);
            }
            return new MethodCall(this.methodLocator, new TargetHandler.ForMethodParameter(index), this.argumentLoaders, MethodInvoker.ForVirtualInvocation.WithImplicitType.INSTANCE, this.terminationHandler, this.assigner, this.typing);
        }

        public MethodCall onField(String name) {
            return this.onField(name, FieldLocator.ForClassHierarchy.Factory.INSTANCE);
        }

        public MethodCall onField(String name, FieldLocator.Factory fieldLocatorFactory) {
            return new MethodCall(this.methodLocator, new TargetHandler.ForField.Factory(new TargetHandler.ForField.Location.ForImplicitField(name, fieldLocatorFactory)), this.argumentLoaders, MethodInvoker.ForVirtualInvocation.WithImplicitType.INSTANCE, this.terminationHandler, this.assigner, this.typing);
        }

        public MethodCall onField(Field field) {
            return this.onField(new FieldDescription.ForLoadedField(field));
        }

        public MethodCall onField(FieldDescription fieldDescription) {
            return new MethodCall(this.methodLocator, new TargetHandler.ForField.Factory(new TargetHandler.ForField.Location.ForExplicitField(fieldDescription)), this.argumentLoaders, MethodInvoker.ForVirtualInvocation.WithImplicitType.INSTANCE, this.terminationHandler, this.assigner, this.typing);
        }

        public MethodCall onMethodCall(MethodCall methodCall) {
            return new MethodCall(this.methodLocator, new TargetHandler.ForMethodCall.Factory(methodCall), this.argumentLoaders, MethodInvoker.ForVirtualInvocation.WithImplicitType.INSTANCE, this.terminationHandler, this.assigner, this.typing);
        }

        public MethodCall onSuper() {
            return new MethodCall(this.methodLocator, TargetHandler.ForSelfOrStaticInvocation.Factory.INSTANCE, this.argumentLoaders, MethodInvoker.ForSuperMethodInvocation.Factory.INSTANCE, this.terminationHandler, this.assigner, this.typing);
        }

        public MethodCall onDefault() {
            return new MethodCall(this.methodLocator, TargetHandler.ForSelfOrStaticInvocation.Factory.INSTANCE, this.argumentLoaders, MethodInvoker.ForDefaultMethodInvocation.Factory.INSTANCE, this.terminationHandler, this.assigner, this.typing);
        }
    }

    protected static interface TerminationHandler {
        public StackManipulation prepare();

        public StackManipulation toStackManipulation(MethodDescription var1, MethodDescription var2, Assigner var3, Assigner.Typing var4);

        @HashCodeAndEqualsPlugin.Enhance
        public static class FieldSetting
        implements TerminationHandler {
            private final FieldDescription fieldDescription;

            protected FieldSetting(FieldDescription fieldDescription) {
                this.fieldDescription = fieldDescription;
            }

            public StackManipulation prepare() {
                return this.fieldDescription.isStatic() ? StackManipulation.Trivial.INSTANCE : MethodVariableAccess.loadThis();
            }

            public StackManipulation toStackManipulation(MethodDescription invokedMethod, MethodDescription instrumentedMethod, Assigner assigner, Assigner.Typing typing) {
                StackManipulation stackManipulation = assigner.assign(invokedMethod.isConstructor() ? invokedMethod.getDeclaringType().asGenericType() : invokedMethod.getReturnType(), this.fieldDescription.getType(), typing);
                if (!stackManipulation.isValid()) {
                    throw new IllegalStateException("Cannot assign result of " + invokedMethod + " to " + this.fieldDescription);
                }
                return new StackManipulation.Compound(stackManipulation, FieldAccess.forField(this.fieldDescription).write());
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
                return this.fieldDescription.equals(((FieldSetting)object).fieldDescription);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + this.fieldDescription.hashCode();
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            protected static class Implicit
            implements Factory {
                private final ElementMatcher<? super FieldDescription> matcher;

                protected Implicit(ElementMatcher<? super FieldDescription> matcher) {
                    this.matcher = matcher;
                }

                @Override
                public TerminationHandler make(TypeDescription instrumentedType) {
                    TypeDefinition current = instrumentedType;
                    do {
                        FieldList candidates;
                        if ((candidates = (FieldList)current.getDeclaredFields().filter(ElementMatchers.isVisibleTo(instrumentedType).and(this.matcher))).size() == 1) {
                            return new FieldSetting((FieldDescription)candidates.getOnly());
                        }
                        if (candidates.size() != 2) continue;
                        throw new IllegalStateException(this.matcher + " is ambiguous and resolved: " + candidates);
                    } while ((current = current.getSuperClass()) != null);
                    throw new IllegalStateException(this.matcher + " does not locate any accessible fields for " + instrumentedType);
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
                    return this.matcher.equals(((Implicit)object).matcher);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.matcher.hashCode();
                }
            }

            @HashCodeAndEqualsPlugin.Enhance
            protected static class Explicit
            implements Factory {
                private final FieldDescription fieldDescription;

                protected Explicit(FieldDescription fieldDescription) {
                    this.fieldDescription = fieldDescription;
                }

                @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Assuming declaring type for type member.")
                public TerminationHandler make(TypeDescription instrumentedType) {
                    if (!this.fieldDescription.isStatic() && !instrumentedType.isAssignableTo(this.fieldDescription.getDeclaringType().asErasure())) {
                        throw new IllegalStateException("Cannot set " + this.fieldDescription + " from " + instrumentedType);
                    }
                    if (!this.fieldDescription.isVisibleTo(instrumentedType)) {
                        throw new IllegalStateException("Cannot access " + this.fieldDescription + " from " + instrumentedType);
                    }
                    return new FieldSetting(this.fieldDescription);
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
                    return this.fieldDescription.equals(((Explicit)object).fieldDescription);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.fieldDescription.hashCode();
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum Simple implements TerminationHandler,
        Factory
        {
            RETURNING{

                public StackManipulation toStackManipulation(MethodDescription invokedMethod, MethodDescription instrumentedMethod, Assigner assigner, Assigner.Typing typing) {
                    StackManipulation stackManipulation = assigner.assign(invokedMethod.isConstructor() ? invokedMethod.getDeclaringType().asGenericType() : invokedMethod.getReturnType(), instrumentedMethod.getReturnType(), typing);
                    if (!stackManipulation.isValid()) {
                        throw new IllegalStateException("Cannot return " + invokedMethod.getReturnType() + " from " + instrumentedMethod);
                    }
                    return new StackManipulation.Compound(stackManipulation, MethodReturn.of(instrumentedMethod.getReturnType()));
                }
            }
            ,
            DROPPING{

                public StackManipulation toStackManipulation(MethodDescription invokedMethod, MethodDescription instrumentedMethod, Assigner assigner, Assigner.Typing typing) {
                    return Removal.of(invokedMethod.isConstructor() ? invokedMethod.getDeclaringType() : invokedMethod.getReturnType());
                }
            }
            ,
            IGNORING{

                public StackManipulation toStackManipulation(MethodDescription invokedMethod, MethodDescription instrumentedMethod, Assigner assigner, Assigner.Typing typing) {
                    return StackManipulation.Trivial.INSTANCE;
                }
            };


            @Override
            public TerminationHandler make(TypeDescription instrumentedType) {
                return this;
            }

            @Override
            public StackManipulation prepare() {
                return StackManipulation.Trivial.INSTANCE;
            }
        }

        public static interface Factory {
            public TerminationHandler make(TypeDescription var1);
        }
    }

    protected static interface MethodInvoker {
        public StackManipulation toStackManipulation(MethodDescription var1, Implementation.Target var2);

        @HashCodeAndEqualsPlugin.Enhance
        public static class ForDefaultMethodInvocation
        implements MethodInvoker {
            private final TypeDescription instrumentedType;

            protected ForDefaultMethodInvocation(TypeDescription instrumentedType) {
                this.instrumentedType = instrumentedType;
            }

            public StackManipulation toStackManipulation(MethodDescription invokedMethod, Implementation.Target implementationTarget) {
                if (!invokedMethod.isInvokableOn(this.instrumentedType)) {
                    throw new IllegalStateException("Cannot invoke " + invokedMethod + " as default method of " + this.instrumentedType);
                }
                Implementation.SpecialMethodInvocation stackManipulation = implementationTarget.invokeDefault(invokedMethod.asSignatureToken(), invokedMethod.getDeclaringType().asErasure()).withCheckedCompatibilityTo(invokedMethod.asTypeToken());
                if (!stackManipulation.isValid()) {
                    throw new IllegalStateException("Cannot invoke " + invokedMethod + " on " + this.instrumentedType);
                }
                return stackManipulation;
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
                return this.instrumentedType.equals(((ForDefaultMethodInvocation)object).instrumentedType);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + this.instrumentedType.hashCode();
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            static enum Factory implements net.bytebuddy.implementation.MethodCall$MethodInvoker$Factory
            {
                INSTANCE;


                @Override
                public MethodInvoker make(TypeDescription instrumentedType) {
                    return new ForDefaultMethodInvocation(instrumentedType);
                }
            }
        }

        @HashCodeAndEqualsPlugin.Enhance
        public static class ForSuperMethodInvocation
        implements MethodInvoker {
            private final TypeDescription instrumentedType;

            protected ForSuperMethodInvocation(TypeDescription instrumentedType) {
                this.instrumentedType = instrumentedType;
            }

            public StackManipulation toStackManipulation(MethodDescription invokedMethod, Implementation.Target implementationTarget) {
                if (!invokedMethod.isInvokableOn(implementationTarget.getOriginType().asErasure())) {
                    throw new IllegalStateException("Cannot invoke " + invokedMethod + " as super method of " + this.instrumentedType);
                }
                Implementation.SpecialMethodInvocation stackManipulation = implementationTarget.invokeDominant(invokedMethod.asSignatureToken()).withCheckedCompatibilityTo(invokedMethod.asTypeToken());
                if (!stackManipulation.isValid()) {
                    throw new IllegalStateException("Cannot invoke " + invokedMethod + " as a super method");
                }
                return stackManipulation;
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
                return this.instrumentedType.equals(((ForSuperMethodInvocation)object).instrumentedType);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + this.instrumentedType.hashCode();
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            static enum Factory implements net.bytebuddy.implementation.MethodCall$MethodInvoker$Factory
            {
                INSTANCE;


                @Override
                public MethodInvoker make(TypeDescription instrumentedType) {
                    if (instrumentedType.getSuperClass() == null) {
                        throw new IllegalStateException("Cannot invoke super method for " + instrumentedType);
                    }
                    return new ForSuperMethodInvocation(instrumentedType);
                }
            }
        }

        @HashCodeAndEqualsPlugin.Enhance
        public static class ForVirtualInvocation
        implements MethodInvoker {
            private final TypeDescription typeDescription;

            protected ForVirtualInvocation(TypeDescription typeDescription) {
                this.typeDescription = typeDescription;
            }

            public StackManipulation toStackManipulation(MethodDescription invokedMethod, Implementation.Target implementationTarget) {
                if (!invokedMethod.isInvokableOn(this.typeDescription)) {
                    throw new IllegalStateException("Cannot invoke " + invokedMethod + " on " + this.typeDescription);
                }
                return MethodInvocation.invoke(invokedMethod).virtual(this.typeDescription);
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
                return this.typeDescription.equals(((ForVirtualInvocation)object).typeDescription);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + this.typeDescription.hashCode();
            }

            @HashCodeAndEqualsPlugin.Enhance
            protected static class Factory
            implements net.bytebuddy.implementation.MethodCall$MethodInvoker$Factory {
                private final TypeDescription typeDescription;

                protected Factory(TypeDescription typeDescription) {
                    this.typeDescription = typeDescription;
                }

                public MethodInvoker make(TypeDescription instrumentedType) {
                    if (!this.typeDescription.asErasure().isAccessibleTo(instrumentedType)) {
                        throw new IllegalStateException(this.typeDescription + " is not accessible to " + instrumentedType);
                    }
                    return new ForVirtualInvocation(this.typeDescription);
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
                    return this.typeDescription.equals(((Factory)object).typeDescription);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.typeDescription.hashCode();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            protected static enum WithImplicitType implements MethodInvoker,
            net.bytebuddy.implementation.MethodCall$MethodInvoker$Factory
            {
                INSTANCE;


                @Override
                public MethodInvoker make(TypeDescription instrumentedType) {
                    return this;
                }

                @Override
                public StackManipulation toStackManipulation(MethodDescription invokedMethod, Implementation.Target implementationTarget) {
                    if (!invokedMethod.isAccessibleTo(implementationTarget.getInstrumentedType()) || !invokedMethod.isVirtual()) {
                        throw new IllegalStateException("Cannot invoke " + invokedMethod + " virtually");
                    }
                    return MethodInvocation.invoke(invokedMethod);
                }
            }
        }

        @HashCodeAndEqualsPlugin.Enhance
        public static class ForContextualInvocation
        implements MethodInvoker {
            private final TypeDescription instrumentedType;

            protected ForContextualInvocation(TypeDescription instrumentedType) {
                this.instrumentedType = instrumentedType;
            }

            public StackManipulation toStackManipulation(MethodDescription invokedMethod, Implementation.Target implementationTarget) {
                if (invokedMethod.isVirtual() && !invokedMethod.isInvokableOn(this.instrumentedType)) {
                    throw new IllegalStateException("Cannot invoke " + invokedMethod + " on " + this.instrumentedType);
                }
                return invokedMethod.isVirtual() ? MethodInvocation.invoke(invokedMethod).virtual(this.instrumentedType) : MethodInvocation.invoke(invokedMethod);
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
                return this.instrumentedType.equals(((ForContextualInvocation)object).instrumentedType);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + this.instrumentedType.hashCode();
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            static enum Factory implements net.bytebuddy.implementation.MethodCall$MethodInvoker$Factory
            {
                INSTANCE;


                @Override
                public MethodInvoker make(TypeDescription instrumentedType) {
                    return new ForContextualInvocation(instrumentedType);
                }
            }
        }

        public static interface Factory {
            public MethodInvoker make(TypeDescription var1);
        }
    }

    protected static interface TargetHandler {
        public Resolved resolve(MethodDescription var1);

        @HashCodeAndEqualsPlugin.Enhance
        public static class ForMethodCall
        implements TargetHandler {
            private final Appender appender;

            protected ForMethodCall(Appender appender) {
                this.appender = appender;
            }

            public net.bytebuddy.implementation.MethodCall$TargetHandler$Resolved resolve(MethodDescription instrumentedMethod) {
                net.bytebuddy.implementation.MethodCall$TargetHandler$Resolved targetHandler = this.appender.targetHandler.resolve(instrumentedMethod);
                return new Resolved(this.appender, this.appender.toInvokedMethod(instrumentedMethod, targetHandler), instrumentedMethod, targetHandler);
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
                return this.appender.equals(((ForMethodCall)object).appender);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + this.appender.hashCode();
            }

            @HashCodeAndEqualsPlugin.Enhance
            protected static class Factory
            implements net.bytebuddy.implementation.MethodCall$TargetHandler$Factory {
                private final MethodCall methodCall;

                public Factory(MethodCall methodCall) {
                    this.methodCall = methodCall;
                }

                public InstrumentedType prepare(InstrumentedType instrumentedType) {
                    return this.methodCall.prepare(instrumentedType);
                }

                public TargetHandler make(Implementation.Target implementationTarget) {
                    MethodCall methodCall = this.methodCall;
                    methodCall.getClass();
                    return new ForMethodCall(methodCall.new Appender(implementationTarget, TerminationHandler.Simple.IGNORING));
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
                    return this.methodCall.equals(((Factory)object).methodCall);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.methodCall.hashCode();
                }
            }

            @HashCodeAndEqualsPlugin.Enhance
            protected static class Resolved
            implements net.bytebuddy.implementation.MethodCall$TargetHandler$Resolved {
                private final Appender appender;
                private final MethodDescription methodDescription;
                private final MethodDescription instrumentedMethod;
                private final net.bytebuddy.implementation.MethodCall$TargetHandler$Resolved targetHandler;

                protected Resolved(Appender appender, MethodDescription methodDescription, MethodDescription instrumentedMethod, net.bytebuddy.implementation.MethodCall$TargetHandler$Resolved targetHandler) {
                    this.appender = appender;
                    this.methodDescription = methodDescription;
                    this.instrumentedMethod = instrumentedMethod;
                    this.targetHandler = targetHandler;
                }

                public TypeDescription getTypeDescription() {
                    return this.methodDescription.isConstructor() ? this.methodDescription.getDeclaringType().asErasure() : this.methodDescription.getReturnType().asErasure();
                }

                public StackManipulation toStackManipulation(MethodDescription invokedMethod, Assigner assigner, Assigner.Typing typing) {
                    StackManipulation stackManipulation = assigner.assign(this.methodDescription.isConstructor() ? this.methodDescription.getDeclaringType().asGenericType() : this.methodDescription.getReturnType(), invokedMethod.getDeclaringType().asGenericType(), typing);
                    if (!stackManipulation.isValid()) {
                        throw new IllegalStateException("Cannot invoke " + invokedMethod + " on " + (this.methodDescription.isConstructor() ? this.methodDescription.getDeclaringType() : this.methodDescription.getReturnType()));
                    }
                    return new StackManipulation.Compound(this.appender.toStackManipulation(this.instrumentedMethod, this.methodDescription, this.targetHandler), stackManipulation);
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
                    if (!this.appender.equals(((Resolved)object).appender)) {
                        return false;
                    }
                    if (!this.methodDescription.equals(((Resolved)object).methodDescription)) {
                        return false;
                    }
                    if (!this.instrumentedMethod.equals(((Resolved)object).instrumentedMethod)) {
                        return false;
                    }
                    return this.targetHandler.equals(((Resolved)object).targetHandler);
                }

                public int hashCode() {
                    return (((this.getClass().hashCode() * 31 + this.appender.hashCode()) * 31 + this.methodDescription.hashCode()) * 31 + this.instrumentedMethod.hashCode()) * 31 + this.targetHandler.hashCode();
                }
            }
        }

        @HashCodeAndEqualsPlugin.Enhance
        public static class ForMethodParameter
        implements TargetHandler,
        Factory {
            private final int index;

            protected ForMethodParameter(int index) {
                this.index = index;
            }

            public InstrumentedType prepare(InstrumentedType instrumentedType) {
                return instrumentedType;
            }

            public TargetHandler make(Implementation.Target implementationTarget) {
                return this;
            }

            public net.bytebuddy.implementation.MethodCall$TargetHandler$Resolved resolve(MethodDescription instrumentedMethod) {
                if (this.index >= instrumentedMethod.getParameters().size()) {
                    throw new IllegalArgumentException(instrumentedMethod + " does not have a parameter with index " + this.index);
                }
                return new Resolved((ParameterDescription)instrumentedMethod.getParameters().get(this.index));
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
                return this.index == ((ForMethodParameter)object).index;
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + this.index;
            }

            @HashCodeAndEqualsPlugin.Enhance
            protected static class Resolved
            implements net.bytebuddy.implementation.MethodCall$TargetHandler$Resolved {
                private final ParameterDescription parameterDescription;

                protected Resolved(ParameterDescription parameterDescription) {
                    this.parameterDescription = parameterDescription;
                }

                public TypeDescription getTypeDescription() {
                    return this.parameterDescription.getType().asErasure();
                }

                public StackManipulation toStackManipulation(MethodDescription invokedMethod, Assigner assigner, Assigner.Typing typing) {
                    StackManipulation stackManipulation = assigner.assign(this.parameterDescription.getType(), invokedMethod.getDeclaringType().asGenericType(), typing);
                    if (!stackManipulation.isValid()) {
                        throw new IllegalStateException("Cannot invoke " + invokedMethod + " on " + this.parameterDescription.getType());
                    }
                    return new StackManipulation.Compound(MethodVariableAccess.load(this.parameterDescription), stackManipulation);
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
                    return this.parameterDescription.equals(((Resolved)object).parameterDescription);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.parameterDescription.hashCode();
                }
            }
        }

        @HashCodeAndEqualsPlugin.Enhance
        public static class ForField
        implements TargetHandler,
        Resolved {
            private final FieldDescription fieldDescription;

            protected ForField(FieldDescription fieldDescription) {
                this.fieldDescription = fieldDescription;
            }

            public Resolved resolve(MethodDescription instrumentedMethod) {
                return this;
            }

            public TypeDescription getTypeDescription() {
                return this.fieldDescription.getType().asErasure();
            }

            public StackManipulation toStackManipulation(MethodDescription invokedMethod, Assigner assigner, Assigner.Typing typing) {
                if (!(invokedMethod.isMethod() && invokedMethod.isVirtual() && invokedMethod.isVisibleTo(this.fieldDescription.getType().asErasure()))) {
                    throw new IllegalStateException("Cannot invoke " + invokedMethod + " on " + this.fieldDescription);
                }
                StackManipulation stackManipulation = assigner.assign(this.fieldDescription.getType(), invokedMethod.getDeclaringType().asGenericType(), typing);
                if (!stackManipulation.isValid()) {
                    throw new IllegalStateException("Cannot invoke " + invokedMethod + " on " + this.fieldDescription);
                }
                return new StackManipulation.Compound(invokedMethod.isStatic() || this.fieldDescription.isStatic() ? StackManipulation.Trivial.INSTANCE : MethodVariableAccess.loadThis(), FieldAccess.forField(this.fieldDescription).read(), stackManipulation);
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
                return this.fieldDescription.equals(((ForField)object).fieldDescription);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + this.fieldDescription.hashCode();
            }

            @HashCodeAndEqualsPlugin.Enhance
            protected static class Factory
            implements net.bytebuddy.implementation.MethodCall$TargetHandler$Factory {
                private final Location location;

                protected Factory(Location location) {
                    this.location = location;
                }

                public InstrumentedType prepare(InstrumentedType instrumentedType) {
                    return instrumentedType;
                }

                @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Assuming declaring type for type member.")
                public TargetHandler make(Implementation.Target implementationTarget) {
                    FieldDescription fieldDescription = this.location.resolve(implementationTarget.getInstrumentedType());
                    if (!fieldDescription.isStatic() && !implementationTarget.getInstrumentedType().isAssignableTo(fieldDescription.getDeclaringType().asErasure())) {
                        throw new IllegalStateException("Cannot access " + fieldDescription + " from " + implementationTarget.getInstrumentedType());
                    }
                    return new ForField(fieldDescription);
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
                    return this.location.equals(((Factory)object).location);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.location.hashCode();
                }
            }

            protected static interface Location {
                public FieldDescription resolve(TypeDescription var1);

                @HashCodeAndEqualsPlugin.Enhance
                public static class ForExplicitField
                implements Location {
                    private final FieldDescription fieldDescription;

                    protected ForExplicitField(FieldDescription fieldDescription) {
                        this.fieldDescription = fieldDescription;
                    }

                    public FieldDescription resolve(TypeDescription instrumentedType) {
                        return this.fieldDescription;
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
                        return this.fieldDescription.equals(((ForExplicitField)object).fieldDescription);
                    }

                    public int hashCode() {
                        return this.getClass().hashCode() * 31 + this.fieldDescription.hashCode();
                    }
                }

                @HashCodeAndEqualsPlugin.Enhance
                public static class ForImplicitField
                implements Location {
                    private final String name;
                    private final FieldLocator.Factory fieldLocatorFactory;

                    protected ForImplicitField(String name, FieldLocator.Factory fieldLocatorFactory) {
                        this.name = name;
                        this.fieldLocatorFactory = fieldLocatorFactory;
                    }

                    public FieldDescription resolve(TypeDescription instrumentedType) {
                        FieldLocator.Resolution resolution = this.fieldLocatorFactory.make(instrumentedType).locate(this.name);
                        if (!resolution.isResolved()) {
                            throw new IllegalStateException("Could not locate field name " + this.name + " on " + instrumentedType);
                        }
                        return resolution.getField();
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
                        if (!this.name.equals(((ForImplicitField)object).name)) {
                            return false;
                        }
                        return this.fieldLocatorFactory.equals(((ForImplicitField)object).fieldLocatorFactory);
                    }

                    public int hashCode() {
                        return (this.getClass().hashCode() * 31 + this.name.hashCode()) * 31 + this.fieldLocatorFactory.hashCode();
                    }
                }
            }
        }

        @HashCodeAndEqualsPlugin.Enhance
        public static class ForValue
        implements TargetHandler,
        Resolved {
            private final FieldDescription.InDefinedShape fieldDescription;

            protected ForValue(FieldDescription.InDefinedShape fieldDescription) {
                this.fieldDescription = fieldDescription;
            }

            public Resolved resolve(MethodDescription instrumentedMethod) {
                return this;
            }

            public TypeDescription getTypeDescription() {
                return this.fieldDescription.getType().asErasure();
            }

            public StackManipulation toStackManipulation(MethodDescription invokedMethod, Assigner assigner, Assigner.Typing typing) {
                StackManipulation stackManipulation = assigner.assign(this.fieldDescription.getType(), invokedMethod.getDeclaringType().asGenericType(), typing);
                if (!stackManipulation.isValid()) {
                    throw new IllegalStateException("Cannot invoke " + invokedMethod + " on " + this.fieldDescription);
                }
                return new StackManipulation.Compound(FieldAccess.forField(this.fieldDescription).read(), stackManipulation);
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
                return this.fieldDescription.equals(((ForValue)object).fieldDescription);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + this.fieldDescription.hashCode();
            }

            @HashCodeAndEqualsPlugin.Enhance
            protected static class Factory
            implements net.bytebuddy.implementation.MethodCall$TargetHandler$Factory {
                private static final String FIELD_PREFIX = "invocationTarget";
                private final Object target;
                private final TypeDescription.Generic fieldType;
                @HashCodeAndEqualsPlugin.ValueHandling(value=HashCodeAndEqualsPlugin.ValueHandling.Sort.IGNORE)
                private final String name;

                protected Factory(Object target, TypeDescription.Generic fieldType) {
                    this.target = target;
                    this.fieldType = fieldType;
                    this.name = "invocationTarget$" + RandomString.hashOf(target);
                }

                public InstrumentedType prepare(InstrumentedType instrumentedType) {
                    return instrumentedType.withAuxiliaryField(new FieldDescription.Token(this.name, 4169, this.fieldType), this.target);
                }

                public TargetHandler make(Implementation.Target implementationTarget) {
                    return new ForValue((FieldDescription.InDefinedShape)((FieldList)implementationTarget.getInstrumentedType().getDeclaredFields().filter(ElementMatchers.named(this.name))).getOnly());
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
                    if (!this.target.equals(((Factory)object).target)) {
                        return false;
                    }
                    return this.fieldType.equals(((Factory)object).fieldType);
                }

                public int hashCode() {
                    return (this.getClass().hashCode() * 31 + this.target.hashCode()) * 31 + this.fieldType.hashCode();
                }
            }
        }

        @HashCodeAndEqualsPlugin.Enhance
        public static class ForConstructingInvocation
        implements TargetHandler,
        Resolved {
            private final TypeDescription instrumentedType;

            protected ForConstructingInvocation(TypeDescription instrumentedType) {
                this.instrumentedType = instrumentedType;
            }

            public Resolved resolve(MethodDescription instrumentedMethod) {
                return this;
            }

            public TypeDescription getTypeDescription() {
                return this.instrumentedType;
            }

            public StackManipulation toStackManipulation(MethodDescription invokedMethod, Assigner assigner, Assigner.Typing typing) {
                return new StackManipulation.Compound(TypeCreation.of(invokedMethod.getDeclaringType().asErasure()), Duplication.SINGLE);
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
                return this.instrumentedType.equals(((ForConstructingInvocation)object).instrumentedType);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + this.instrumentedType.hashCode();
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            static enum Factory implements net.bytebuddy.implementation.MethodCall$TargetHandler$Factory
            {
                INSTANCE;


                @Override
                public InstrumentedType prepare(InstrumentedType instrumentedType) {
                    return instrumentedType;
                }

                @Override
                public TargetHandler make(Implementation.Target implementationTarget) {
                    return new ForConstructingInvocation(implementationTarget.getInstrumentedType());
                }
            }
        }

        @HashCodeAndEqualsPlugin.Enhance
        public static class ForSelfOrStaticInvocation
        implements TargetHandler {
            private final TypeDescription instrumentedType;

            protected ForSelfOrStaticInvocation(TypeDescription instrumentedType) {
                this.instrumentedType = instrumentedType;
            }

            public net.bytebuddy.implementation.MethodCall$TargetHandler$Resolved resolve(MethodDescription instrumentedMethod) {
                return new Resolved(this.instrumentedType, instrumentedMethod);
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
                return this.instrumentedType.equals(((ForSelfOrStaticInvocation)object).instrumentedType);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + this.instrumentedType.hashCode();
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            protected static enum Factory implements net.bytebuddy.implementation.MethodCall$TargetHandler$Factory
            {
                INSTANCE;


                @Override
                public InstrumentedType prepare(InstrumentedType instrumentedType) {
                    return instrumentedType;
                }

                @Override
                public TargetHandler make(Implementation.Target implementationTarget) {
                    return new ForSelfOrStaticInvocation(implementationTarget.getInstrumentedType());
                }
            }

            @HashCodeAndEqualsPlugin.Enhance
            protected static class Resolved
            implements net.bytebuddy.implementation.MethodCall$TargetHandler$Resolved {
                private final TypeDescription instrumentedType;
                private final MethodDescription instrumentedMethod;

                protected Resolved(TypeDescription instrumentedType, MethodDescription instrumentedMethod) {
                    this.instrumentedType = instrumentedType;
                    this.instrumentedMethod = instrumentedMethod;
                }

                public TypeDescription getTypeDescription() {
                    return this.instrumentedType;
                }

                @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Assuming declaring type for type member.")
                public StackManipulation toStackManipulation(MethodDescription invokedMethod, Assigner assigner, Assigner.Typing typing) {
                    if (this.instrumentedMethod.isStatic() && !invokedMethod.isStatic() && !invokedMethod.isConstructor()) {
                        throw new IllegalStateException("Cannot invoke " + invokedMethod + " from " + this.instrumentedMethod);
                    }
                    if (!(!invokedMethod.isConstructor() || this.instrumentedMethod.isConstructor() && (this.instrumentedType.equals(invokedMethod.getDeclaringType().asErasure()) || this.instrumentedType.getSuperClass() != null && this.instrumentedType.getSuperClass().asErasure().equals(invokedMethod.getDeclaringType().asErasure())))) {
                        throw new IllegalStateException("Cannot invoke " + invokedMethod + " from " + this.instrumentedMethod + " in " + this.instrumentedType);
                    }
                    return new StackManipulation.Compound(invokedMethod.isStatic() ? StackManipulation.Trivial.INSTANCE : MethodVariableAccess.loadThis(), (StackManipulation)((Object)(invokedMethod.isConstructor() ? Duplication.SINGLE : StackManipulation.Trivial.INSTANCE)));
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
                    if (!this.instrumentedType.equals(((Resolved)object).instrumentedType)) {
                        return false;
                    }
                    return this.instrumentedMethod.equals(((Resolved)object).instrumentedMethod);
                }

                public int hashCode() {
                    return (this.getClass().hashCode() * 31 + this.instrumentedType.hashCode()) * 31 + this.instrumentedMethod.hashCode();
                }
            }
        }

        @HashCodeAndEqualsPlugin.Enhance
        public static class Simple
        implements TargetHandler,
        Factory,
        Resolved {
            private final TypeDescription typeDescription;
            private final StackManipulation stackManipulation;

            protected Simple(TypeDescription typeDescription, StackManipulation stackManipulation) {
                this.typeDescription = typeDescription;
                this.stackManipulation = stackManipulation;
            }

            public TargetHandler make(Implementation.Target implementationTarget) {
                return this;
            }

            public InstrumentedType prepare(InstrumentedType instrumentedType) {
                return instrumentedType;
            }

            public Resolved resolve(MethodDescription instrumentedMethod) {
                return this;
            }

            public TypeDescription getTypeDescription() {
                return this.typeDescription;
            }

            public StackManipulation toStackManipulation(MethodDescription invokedMethod, Assigner assigner, Assigner.Typing typing) {
                return this.stackManipulation;
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
                if (!this.typeDescription.equals(((Simple)object).typeDescription)) {
                    return false;
                }
                return this.stackManipulation.equals(((Simple)object).stackManipulation);
            }

            public int hashCode() {
                return (this.getClass().hashCode() * 31 + this.typeDescription.hashCode()) * 31 + this.stackManipulation.hashCode();
            }
        }

        public static interface Factory
        extends InstrumentedType.Prepareable {
            public TargetHandler make(Implementation.Target var1);
        }

        public static interface Resolved {
            public TypeDescription getTypeDescription();

            public StackManipulation toStackManipulation(MethodDescription var1, Assigner var2, Assigner.Typing var3);
        }
    }

    public static interface ArgumentLoader {
        public StackManipulation toStackManipulation(ParameterDescription var1, Assigner var2, Assigner.Typing var3);

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        public static class ForStackManipulation
        implements ArgumentLoader,
        ArgumentProvider,
        Factory {
            private final StackManipulation stackManipulation;
            private final TypeDefinition typeDefinition;

            public ForStackManipulation(StackManipulation stackManipulation, Type type) {
                this(stackManipulation, TypeDefinition.Sort.describe(type));
            }

            public ForStackManipulation(StackManipulation stackManipulation, TypeDefinition typeDefinition) {
                this.stackManipulation = stackManipulation;
                this.typeDefinition = typeDefinition;
            }

            public static Factory of(@MaybeNull Object value) {
                if (value == null) {
                    return ForNullConstant.INSTANCE;
                }
                ConstantValue constant = ConstantValue.Simple.wrapOrNull(value);
                return constant == null ? new ForInstance.Factory(value) : new ForStackManipulation(constant.toStackManipulation(), constant.getTypeDescription());
            }

            @Override
            public InstrumentedType prepare(InstrumentedType instrumentedType) {
                return instrumentedType;
            }

            @Override
            public ArgumentProvider make(Implementation.Target implementationTarget) {
                return this;
            }

            @Override
            public List<ArgumentLoader> resolve(MethodDescription instrumentedMethod, MethodDescription invokedMethod) {
                return Collections.singletonList(this);
            }

            @Override
            public StackManipulation toStackManipulation(ParameterDescription target, Assigner assigner, Assigner.Typing typing) {
                StackManipulation assignment = assigner.assign(this.typeDefinition.asGenericType(), target.getType(), typing);
                if (!assignment.isValid()) {
                    throw new IllegalStateException("Cannot assign " + target + " to " + this.typeDefinition);
                }
                return new StackManipulation.Compound(this.stackManipulation, assignment);
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
                if (!this.stackManipulation.equals(((ForStackManipulation)object).stackManipulation)) {
                    return false;
                }
                return this.typeDefinition.equals(((ForStackManipulation)object).typeDefinition);
            }

            public int hashCode() {
                return (this.getClass().hashCode() * 31 + this.stackManipulation.hashCode()) * 31 + this.typeDefinition.hashCode();
            }
        }

        @HashCodeAndEqualsPlugin.Enhance
        public static class ForMethodCall
        implements ArgumentLoader {
            private final Appender appender;
            private final MethodDescription methodDescription;
            private final MethodDescription instrumentedMethod;
            private final TargetHandler.Resolved targetHandler;

            public ForMethodCall(Appender appender, MethodDescription methodDescription, MethodDescription instrumentedMethod, TargetHandler.Resolved targetHandler) {
                this.appender = appender;
                this.methodDescription = methodDescription;
                this.instrumentedMethod = instrumentedMethod;
                this.targetHandler = targetHandler;
            }

            public StackManipulation toStackManipulation(ParameterDescription target, Assigner assigner, Assigner.Typing typing) {
                StackManipulation.Compound stackManipulation = new StackManipulation.Compound(this.appender.toStackManipulation(this.instrumentedMethod, this.methodDescription, this.targetHandler), assigner.assign(this.methodDescription.isConstructor() ? this.methodDescription.getDeclaringType().asGenericType() : this.methodDescription.getReturnType(), target.getType(), typing));
                if (!stackManipulation.isValid()) {
                    throw new IllegalStateException("Cannot assign return type of " + this.methodDescription + " to " + target);
                }
                return stackManipulation;
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
                if (!this.appender.equals(((ForMethodCall)object).appender)) {
                    return false;
                }
                if (!this.methodDescription.equals(((ForMethodCall)object).methodDescription)) {
                    return false;
                }
                if (!this.instrumentedMethod.equals(((ForMethodCall)object).instrumentedMethod)) {
                    return false;
                }
                return this.targetHandler.equals(((ForMethodCall)object).targetHandler);
            }

            public int hashCode() {
                return (((this.getClass().hashCode() * 31 + this.appender.hashCode()) * 31 + this.methodDescription.hashCode()) * 31 + this.instrumentedMethod.hashCode()) * 31 + this.targetHandler.hashCode();
            }

            @HashCodeAndEqualsPlugin.Enhance
            protected static class Factory
            implements net.bytebuddy.implementation.MethodCall$ArgumentLoader$Factory {
                private final MethodCall methodCall;

                public Factory(MethodCall methodCall) {
                    this.methodCall = methodCall;
                }

                public InstrumentedType prepare(InstrumentedType instrumentedType) {
                    return this.methodCall.prepare(instrumentedType);
                }

                public net.bytebuddy.implementation.MethodCall$ArgumentLoader$ArgumentProvider make(Implementation.Target implementationTarget) {
                    MethodCall methodCall = this.methodCall;
                    methodCall.getClass();
                    return new ArgumentProvider(methodCall.new Appender(implementationTarget, TerminationHandler.Simple.IGNORING));
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
                    return this.methodCall.equals(((Factory)object).methodCall);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.methodCall.hashCode();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            protected static class ArgumentProvider
            implements net.bytebuddy.implementation.MethodCall$ArgumentLoader$ArgumentProvider {
                private final Appender appender;

                protected ArgumentProvider(Appender appender) {
                    this.appender = appender;
                }

                @Override
                public List<ArgumentLoader> resolve(MethodDescription instrumentedMethod, MethodDescription invokedMethod) {
                    TargetHandler.Resolved targetHandler = this.appender.targetHandler.resolve(instrumentedMethod);
                    return Collections.singletonList(new ForMethodCall(this.appender, this.appender.toInvokedMethod(instrumentedMethod, targetHandler), instrumentedMethod, targetHandler));
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
                    return this.appender.equals(((ArgumentProvider)object).appender);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.appender.hashCode();
                }
            }
        }

        @HashCodeAndEqualsPlugin.Enhance
        public static class ForField
        implements ArgumentLoader {
            private final FieldDescription fieldDescription;
            private final MethodDescription instrumentedMethod;

            public ForField(FieldDescription fieldDescription, MethodDescription instrumentedMethod) {
                this.fieldDescription = fieldDescription;
                this.instrumentedMethod = instrumentedMethod;
            }

            public StackManipulation toStackManipulation(ParameterDescription target, Assigner assigner, Assigner.Typing typing) {
                if (!this.fieldDescription.isStatic() && this.instrumentedMethod.isStatic()) {
                    throw new IllegalStateException("Cannot access non-static " + this.fieldDescription + " from " + this.instrumentedMethod);
                }
                StackManipulation.Compound stackManipulation = new StackManipulation.Compound(this.fieldDescription.isStatic() ? StackManipulation.Trivial.INSTANCE : MethodVariableAccess.loadThis(), FieldAccess.forField(this.fieldDescription).read(), assigner.assign(this.fieldDescription.getType(), target.getType(), typing));
                if (!stackManipulation.isValid()) {
                    throw new IllegalStateException("Cannot assign " + this.fieldDescription + " to " + target);
                }
                return stackManipulation;
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
                return this.instrumentedMethod.equals(((ForField)object).instrumentedMethod);
            }

            public int hashCode() {
                return (this.getClass().hashCode() * 31 + this.fieldDescription.hashCode()) * 31 + this.instrumentedMethod.hashCode();
            }

            @HashCodeAndEqualsPlugin.Enhance
            protected static class Factory
            implements net.bytebuddy.implementation.MethodCall$ArgumentLoader$Factory {
                private final String name;
                private final FieldLocator.Factory fieldLocatorFactory;

                public Factory(String name, FieldLocator.Factory fieldLocatorFactory) {
                    this.name = name;
                    this.fieldLocatorFactory = fieldLocatorFactory;
                }

                public InstrumentedType prepare(InstrumentedType instrumentedType) {
                    return instrumentedType;
                }

                public net.bytebuddy.implementation.MethodCall$ArgumentLoader$ArgumentProvider make(Implementation.Target implementationTarget) {
                    FieldLocator.Resolution resolution = this.fieldLocatorFactory.make(implementationTarget.getInstrumentedType()).locate(this.name);
                    if (!resolution.isResolved()) {
                        throw new IllegalStateException("Could not locate field '" + this.name + "' on " + implementationTarget.getInstrumentedType());
                    }
                    return new ArgumentProvider(resolution.getField());
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
                    if (!this.name.equals(((Factory)object).name)) {
                        return false;
                    }
                    return this.fieldLocatorFactory.equals(((Factory)object).fieldLocatorFactory);
                }

                public int hashCode() {
                    return (this.getClass().hashCode() * 31 + this.name.hashCode()) * 31 + this.fieldLocatorFactory.hashCode();
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            protected static class ArgumentProvider
            implements net.bytebuddy.implementation.MethodCall$ArgumentLoader$ArgumentProvider {
                private final FieldDescription fieldDescription;

                protected ArgumentProvider(FieldDescription fieldDescription) {
                    this.fieldDescription = fieldDescription;
                }

                @Override
                public List<ArgumentLoader> resolve(MethodDescription instrumentedMethod, MethodDescription invokedMethod) {
                    return Collections.singletonList(new ForField(this.fieldDescription, instrumentedMethod));
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
                    return this.fieldDescription.equals(((ArgumentProvider)object).fieldDescription);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.fieldDescription.hashCode();
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        public static class ForInstance
        implements ArgumentLoader,
        ArgumentProvider {
            private final FieldDescription fieldDescription;

            public ForInstance(FieldDescription fieldDescription) {
                this.fieldDescription = fieldDescription;
            }

            @Override
            public List<ArgumentLoader> resolve(MethodDescription instrumentedMethod, MethodDescription invokedMethod) {
                return Collections.singletonList(this);
            }

            @Override
            public StackManipulation toStackManipulation(ParameterDescription target, Assigner assigner, Assigner.Typing typing) {
                StackManipulation.Compound stackManipulation = new StackManipulation.Compound(FieldAccess.forField(this.fieldDescription).read(), assigner.assign(this.fieldDescription.getType(), target.getType(), typing));
                if (!stackManipulation.isValid()) {
                    throw new IllegalStateException("Cannot assign " + this.fieldDescription.getType() + " to " + target);
                }
                return stackManipulation;
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
                return this.fieldDescription.equals(((ForInstance)object).fieldDescription);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + this.fieldDescription.hashCode();
            }

            @HashCodeAndEqualsPlugin.Enhance
            protected static class Factory
            implements net.bytebuddy.implementation.MethodCall$ArgumentLoader$Factory {
                private static final String FIELD_PREFIX = "methodCall";
                private final Object value;
                @HashCodeAndEqualsPlugin.ValueHandling(value=HashCodeAndEqualsPlugin.ValueHandling.Sort.IGNORE)
                private final String name;

                public Factory(Object value) {
                    this.value = value;
                    this.name = "methodCall$" + RandomString.hashOf(value);
                }

                public InstrumentedType prepare(InstrumentedType instrumentedType) {
                    return instrumentedType.withAuxiliaryField(new FieldDescription.Token(this.name, 4105, TypeDescription.Generic.OfNonGenericType.ForLoadedType.of(this.value.getClass())), this.value);
                }

                public ArgumentProvider make(Implementation.Target implementationTarget) {
                    return new ForInstance((FieldDescription)((FieldList)implementationTarget.getInstrumentedType().getDeclaredFields().filter(ElementMatchers.named(this.name))).getOnly());
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
                    return this.value.equals(((Factory)object).value);
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.value.hashCode();
                }
            }
        }

        @HashCodeAndEqualsPlugin.Enhance
        public static class ForMethodParameterArrayElement
        implements ArgumentLoader {
            private final ParameterDescription parameterDescription;
            private final int index;

            public ForMethodParameterArrayElement(ParameterDescription parameterDescription, int index) {
                this.parameterDescription = parameterDescription;
                this.index = index;
            }

            @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Assuming component type for array type.")
            public StackManipulation toStackManipulation(ParameterDescription target, Assigner assigner, Assigner.Typing typing) {
                StackManipulation.Compound stackManipulation = new StackManipulation.Compound(MethodVariableAccess.load(this.parameterDescription), IntegerConstant.forValue(this.index), ArrayAccess.of(this.parameterDescription.getType().getComponentType()).load(), assigner.assign(this.parameterDescription.getType().getComponentType(), target.getType(), typing));
                if (!stackManipulation.isValid()) {
                    throw new IllegalStateException("Cannot assign " + this.parameterDescription.getType().getComponentType() + " to " + target);
                }
                return stackManipulation;
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
                if (this.index != ((ForMethodParameterArrayElement)object).index) {
                    return false;
                }
                return this.parameterDescription.equals(((ForMethodParameterArrayElement)object).parameterDescription);
            }

            public int hashCode() {
                return (this.getClass().hashCode() * 31 + this.parameterDescription.hashCode()) * 31 + this.index;
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            public static class OfInvokedMethod
            implements Factory,
            ArgumentProvider {
                private final int index;

                public OfInvokedMethod(int index) {
                    this.index = index;
                }

                @Override
                public InstrumentedType prepare(InstrumentedType instrumentedType) {
                    return instrumentedType;
                }

                @Override
                public ArgumentProvider make(Implementation.Target implementationTarget) {
                    return this;
                }

                @Override
                public List<ArgumentLoader> resolve(MethodDescription instrumentedMethod, MethodDescription invokedMethod) {
                    if (instrumentedMethod.getParameters().size() <= this.index) {
                        throw new IllegalStateException(instrumentedMethod + " does not declare a parameter with index " + this.index + ", " + instrumentedMethod.getParameters().size() + " defined");
                    }
                    if (!((ParameterDescription)instrumentedMethod.getParameters().get(this.index)).getType().isArray()) {
                        throw new IllegalStateException("Cannot access an item from non-array parameter " + instrumentedMethod.getParameters().get(this.index) + " at index " + this.index);
                    }
                    ArrayList<ArgumentLoader> argumentLoaders = new ArrayList<ArgumentLoader>(invokedMethod.getParameters().size());
                    for (int index = 0; index < invokedMethod.getParameters().size(); ++index) {
                        argumentLoaders.add(new ForMethodParameterArrayElement((ParameterDescription)instrumentedMethod.getParameters().get(this.index), index));
                    }
                    return argumentLoaders;
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
                    return this.index == ((OfInvokedMethod)object).index;
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.index;
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            public static class OfParameter
            implements Factory,
            ArgumentProvider {
                private final int index;
                private final int arrayIndex;

                public OfParameter(int index, int arrayIndex) {
                    this.index = index;
                    this.arrayIndex = arrayIndex;
                }

                @Override
                public InstrumentedType prepare(InstrumentedType instrumentedType) {
                    return instrumentedType;
                }

                @Override
                public ArgumentProvider make(Implementation.Target implementationTarget) {
                    return this;
                }

                @Override
                public List<ArgumentLoader> resolve(MethodDescription instrumentedMethod, MethodDescription invokedMethod) {
                    if (instrumentedMethod.getParameters().size() <= this.index) {
                        throw new IllegalStateException(instrumentedMethod + " does not declare a parameter with index " + this.index + ", " + instrumentedMethod.getParameters().size() + " defined");
                    }
                    if (!((ParameterDescription)instrumentedMethod.getParameters().get(this.index)).getType().isArray()) {
                        throw new IllegalStateException("Cannot access an item from non-array parameter " + instrumentedMethod.getParameters().get(this.index) + " at index " + this.index);
                    }
                    return Collections.singletonList(new ForMethodParameterArrayElement((ParameterDescription)instrumentedMethod.getParameters().get(this.index), this.arrayIndex));
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
                    if (this.index != ((OfParameter)object).index) {
                        return false;
                    }
                    return this.arrayIndex == ((OfParameter)object).arrayIndex;
                }

                public int hashCode() {
                    return (this.getClass().hashCode() * 31 + this.index) * 31 + this.arrayIndex;
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        public static class ForMethodParameterArray
        implements ArgumentLoader {
            private final ParameterList<?> parameters;

            public ForMethodParameterArray(ParameterList<?> parameters) {
                this.parameters = parameters;
            }

            @Override
            @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"}, justification="Assuming component type for array type.")
            public StackManipulation toStackManipulation(ParameterDescription target, Assigner assigner, Assigner.Typing typing) {
                TypeDescription.Generic componentType;
                if (target.getType().represents((Type)((Object)Object.class))) {
                    componentType = TypeDescription.Generic.OfNonGenericType.ForLoadedType.of(Object.class);
                } else if (target.getType().isArray()) {
                    componentType = target.getType().getComponentType();
                } else {
                    throw new IllegalStateException("Cannot set method parameter array for non-array type: " + target);
                }
                ArrayList<StackManipulation.Compound> stackManipulations = new ArrayList<StackManipulation.Compound>(this.parameters.size());
                for (ParameterDescription parameter : this.parameters) {
                    StackManipulation.Compound stackManipulation = new StackManipulation.Compound(MethodVariableAccess.load(parameter), assigner.assign(parameter.getType(), componentType, typing));
                    if (stackManipulation.isValid()) {
                        stackManipulations.add(stackManipulation);
                        continue;
                    }
                    throw new IllegalStateException("Cannot assign " + parameter + " to " + componentType);
                }
                return new StackManipulation.Compound(ArrayFactory.forType(componentType).withValues(stackManipulations));
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
                return this.parameters.equals(((ForMethodParameterArray)object).parameters);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + this.parameters.hashCode();
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static enum ForInstrumentedMethod implements Factory,
            ArgumentProvider
            {
                INSTANCE;


                @Override
                public InstrumentedType prepare(InstrumentedType instrumentedType) {
                    return instrumentedType;
                }

                @Override
                public ArgumentProvider make(Implementation.Target implementationTarget) {
                    return this;
                }

                @Override
                public List<ArgumentLoader> resolve(MethodDescription instrumentedMethod, MethodDescription invokedMethod) {
                    return Collections.singletonList(new ForMethodParameterArray(instrumentedMethod.getParameters()));
                }
            }
        }

        @HashCodeAndEqualsPlugin.Enhance
        public static class ForMethodParameter
        implements ArgumentLoader {
            private final int index;
            private final MethodDescription instrumentedMethod;

            public ForMethodParameter(int index, MethodDescription instrumentedMethod) {
                this.index = index;
                this.instrumentedMethod = instrumentedMethod;
            }

            public StackManipulation toStackManipulation(ParameterDescription target, Assigner assigner, Assigner.Typing typing) {
                ParameterDescription parameterDescription = (ParameterDescription)this.instrumentedMethod.getParameters().get(this.index);
                StackManipulation.Compound stackManipulation = new StackManipulation.Compound(MethodVariableAccess.load(parameterDescription), assigner.assign(parameterDescription.getType(), target.getType(), typing));
                if (!stackManipulation.isValid()) {
                    throw new IllegalStateException("Cannot assign " + parameterDescription + " to " + target + " for " + this.instrumentedMethod);
                }
                return stackManipulation;
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
                if (this.index != ((ForMethodParameter)object).index) {
                    return false;
                }
                return this.instrumentedMethod.equals(((ForMethodParameter)object).instrumentedMethod);
            }

            public int hashCode() {
                return (this.getClass().hashCode() * 31 + this.index) * 31 + this.instrumentedMethod.hashCode();
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            public static class Factory
            implements net.bytebuddy.implementation.MethodCall$ArgumentLoader$Factory,
            ArgumentProvider {
                private final int index;

                public Factory(int index) {
                    this.index = index;
                }

                @Override
                public InstrumentedType prepare(InstrumentedType instrumentedType) {
                    return instrumentedType;
                }

                @Override
                public ArgumentProvider make(Implementation.Target implementationTarget) {
                    return this;
                }

                @Override
                public List<ArgumentLoader> resolve(MethodDescription instrumentedMethod, MethodDescription invokedMethod) {
                    if (this.index >= instrumentedMethod.getParameters().size()) {
                        throw new IllegalStateException(instrumentedMethod + " does not have a parameter with index " + this.index + ", " + instrumentedMethod.getParameters().size() + " defined");
                    }
                    return Collections.singletonList(new ForMethodParameter(this.index, instrumentedMethod));
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
                    return this.index == ((Factory)object).index;
                }

                public int hashCode() {
                    return this.getClass().hashCode() * 31 + this.index;
                }
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            protected static enum OfInstrumentedMethod implements net.bytebuddy.implementation.MethodCall$ArgumentLoader$Factory,
            ArgumentProvider
            {
                INSTANCE;


                @Override
                public InstrumentedType prepare(InstrumentedType instrumentedType) {
                    return instrumentedType;
                }

                @Override
                public ArgumentProvider make(Implementation.Target implementationTarget) {
                    return this;
                }

                @Override
                public List<ArgumentLoader> resolve(MethodDescription instrumentedMethod, MethodDescription invokedMethod) {
                    ArrayList<ArgumentLoader> argumentLoaders = new ArrayList<ArgumentLoader>(instrumentedMethod.getParameters().size());
                    for (ParameterDescription parameterDescription : instrumentedMethod.getParameters()) {
                        argumentLoaders.add(new ForMethodParameter(parameterDescription.getIndex(), instrumentedMethod));
                    }
                    return argumentLoaders;
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        public static class ForInstrumentedType
        implements ArgumentLoader,
        ArgumentProvider {
            private final TypeDescription instrumentedType;

            public ForInstrumentedType(TypeDescription instrumentedType) {
                this.instrumentedType = instrumentedType;
            }

            @Override
            public List<ArgumentLoader> resolve(MethodDescription instrumentedMethod, MethodDescription invokedMethod) {
                return Collections.singletonList(this);
            }

            @Override
            public StackManipulation toStackManipulation(ParameterDescription target, Assigner assigner, Assigner.Typing typing) {
                StackManipulation.Compound stackManipulation = new StackManipulation.Compound(ClassConstant.of(this.instrumentedType), assigner.assign(TypeDescription.Generic.OfNonGenericType.ForLoadedType.of(Class.class), target.getType(), typing));
                if (!stackManipulation.isValid()) {
                    throw new IllegalStateException("Cannot assign Class value to " + target);
                }
                return stackManipulation;
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
                return this.instrumentedType.equals(((ForInstrumentedType)object).instrumentedType);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + this.instrumentedType.hashCode();
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static enum Factory implements net.bytebuddy.implementation.MethodCall$ArgumentLoader$Factory
            {
                INSTANCE;


                @Override
                public InstrumentedType prepare(InstrumentedType instrumentedType) {
                    return instrumentedType;
                }

                @Override
                public ArgumentProvider make(Implementation.Target implementationTarget) {
                    return new ForInstrumentedType(implementationTarget.getInstrumentedType());
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        public static class ForThisReference
        implements ArgumentLoader,
        ArgumentProvider {
            private final TypeDescription instrumentedType;

            public ForThisReference(TypeDescription instrumentedType) {
                this.instrumentedType = instrumentedType;
            }

            @Override
            public List<ArgumentLoader> resolve(MethodDescription instrumentedMethod, MethodDescription invokedMethod) {
                if (instrumentedMethod.isStatic()) {
                    throw new IllegalStateException(instrumentedMethod + " is static and cannot supply an invoker instance");
                }
                return Collections.singletonList(this);
            }

            @Override
            public StackManipulation toStackManipulation(ParameterDescription target, Assigner assigner, Assigner.Typing typing) {
                StackManipulation.Compound stackManipulation = new StackManipulation.Compound(MethodVariableAccess.loadThis(), assigner.assign(this.instrumentedType.asGenericType(), target.getType(), typing));
                if (!stackManipulation.isValid()) {
                    throw new IllegalStateException("Cannot assign " + this.instrumentedType + " to " + target);
                }
                return stackManipulation;
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
                return this.instrumentedType.equals(((ForThisReference)object).instrumentedType);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + this.instrumentedType.hashCode();
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            public static enum Factory implements net.bytebuddy.implementation.MethodCall$ArgumentLoader$Factory
            {
                INSTANCE;


                @Override
                public InstrumentedType prepare(InstrumentedType instrumentedType) {
                    return instrumentedType;
                }

                @Override
                public ArgumentProvider make(Implementation.Target implementationTarget) {
                    return new ForThisReference(implementationTarget.getInstrumentedType());
                }
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum ForNullConstant implements ArgumentLoader,
        ArgumentProvider,
        Factory
        {
            INSTANCE;


            @Override
            public InstrumentedType prepare(InstrumentedType instrumentedType) {
                return instrumentedType;
            }

            @Override
            public ArgumentProvider make(Implementation.Target implementationTarget) {
                return this;
            }

            @Override
            public List<ArgumentLoader> resolve(MethodDescription instrumentedMethod, MethodDescription invokedMethod) {
                return Collections.singletonList(this);
            }

            @Override
            public StackManipulation toStackManipulation(ParameterDescription target, Assigner assigner, Assigner.Typing typing) {
                if (target.getType().isPrimitive()) {
                    throw new IllegalStateException("Cannot assign null to " + target);
                }
                return NullConstant.INSTANCE;
            }
        }

        public static interface Factory
        extends InstrumentedType.Prepareable {
            public ArgumentProvider make(Implementation.Target var1);
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static interface ArgumentProvider {
            public List<ArgumentLoader> resolve(MethodDescription var1, MethodDescription var2);
        }
    }

    public static interface MethodLocator {
        public MethodDescription resolve(TypeDescription var1, MethodDescription var2);

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        @HashCodeAndEqualsPlugin.Enhance
        public static class ForElementMatcher
        implements MethodLocator {
            private final TypeDescription instrumentedType;
            private final ElementMatcher<? super MethodDescription> matcher;
            private final MethodGraph.Compiler methodGraphCompiler;

            protected ForElementMatcher(TypeDescription instrumentedType, ElementMatcher<? super MethodDescription> matcher, MethodGraph.Compiler methodGraphCompiler) {
                this.instrumentedType = instrumentedType;
                this.matcher = matcher;
                this.methodGraphCompiler = methodGraphCompiler;
            }

            @Override
            public MethodDescription resolve(TypeDescription targetType, MethodDescription instrumentedMethod) {
                TypeDescription.Generic superClass = this.instrumentedType.getSuperClass();
                List candidates = CompoundList.of(superClass == null ? Collections.emptyList() : superClass.getDeclaredMethods().filter(ElementMatchers.isConstructor().and(this.matcher)), this.instrumentedType.getDeclaredMethods().filter(ElementMatchers.not(ElementMatchers.isVirtual()).and(this.matcher)), this.methodGraphCompiler.compile((TypeDefinition)targetType, this.instrumentedType).listNodes().asMethodList().filter(this.matcher));
                if (candidates.size() == 1) {
                    return (MethodDescription)candidates.get(0);
                }
                throw new IllegalStateException(this.instrumentedType + " does not define exactly one virtual method or constructor for " + this.matcher + " but contained " + candidates.size() + " candidates: " + candidates);
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
                if (!this.instrumentedType.equals(((ForElementMatcher)object).instrumentedType)) {
                    return false;
                }
                if (!this.matcher.equals(((ForElementMatcher)object).matcher)) {
                    return false;
                }
                return this.methodGraphCompiler.equals(((ForElementMatcher)object).methodGraphCompiler);
            }

            public int hashCode() {
                return ((this.getClass().hashCode() * 31 + this.instrumentedType.hashCode()) * 31 + this.matcher.hashCode()) * 31 + this.methodGraphCompiler.hashCode();
            }

            /*
             * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
             */
            @HashCodeAndEqualsPlugin.Enhance
            public static class Factory
            implements net.bytebuddy.implementation.MethodCall$MethodLocator$Factory {
                private final ElementMatcher<? super MethodDescription> matcher;
                private final MethodGraph.Compiler methodGraphCompiler;

                public Factory(ElementMatcher<? super MethodDescription> matcher, MethodGraph.Compiler methodGraphCompiler) {
                    this.matcher = matcher;
                    this.methodGraphCompiler = methodGraphCompiler;
                }

                @Override
                public MethodLocator make(TypeDescription instrumentedType) {
                    return new ForElementMatcher(instrumentedType, this.matcher, this.methodGraphCompiler);
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
                    if (!this.matcher.equals(((Factory)object).matcher)) {
                        return false;
                    }
                    return this.methodGraphCompiler.equals(((Factory)object).methodGraphCompiler);
                }

                public int hashCode() {
                    return (this.getClass().hashCode() * 31 + this.matcher.hashCode()) * 31 + this.methodGraphCompiler.hashCode();
                }
            }
        }

        @HashCodeAndEqualsPlugin.Enhance
        public static class ForExplicitMethod
        implements MethodLocator,
        Factory {
            private final MethodDescription methodDescription;

            protected ForExplicitMethod(MethodDescription methodDescription) {
                this.methodDescription = methodDescription;
            }

            public MethodLocator make(TypeDescription instrumentedType) {
                return this;
            }

            public MethodDescription resolve(TypeDescription targetType, MethodDescription instrumentedMethod) {
                return this.methodDescription;
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
                return this.methodDescription.equals(((ForExplicitMethod)object).methodDescription);
            }

            public int hashCode() {
                return this.getClass().hashCode() * 31 + this.methodDescription.hashCode();
            }
        }

        /*
         * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
         */
        public static enum ForInstrumentedMethod implements MethodLocator,
        Factory
        {
            INSTANCE;


            @Override
            public MethodLocator make(TypeDescription instrumentedType) {
                return this;
            }

            @Override
            public MethodDescription resolve(TypeDescription targetType, MethodDescription instrumentedMethod) {
                return instrumentedMethod;
            }
        }

        public static interface Factory {
            public MethodLocator make(TypeDescription var1);
        }
    }
}

